package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.registry.MythosWorldVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShaderPacket {
    public final String shaderLocation;
    public final float r, g, b;

    public ShaderPacket(String shaderLocation, float r, float g, float b) {
        this.shaderLocation = shaderLocation;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ShaderPacket(FriendlyByteBuf buf) {
        this.shaderLocation = buf.readUtf();
        this.r = buf.readFloat();
        this.g = buf.readFloat();
        this.b = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.shaderLocation);
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
    }

    public static ShaderPacket decode(FriendlyByteBuf buf) {
        return new ShaderPacket(buf);
    }

    public static void handle(ShaderPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (msg.shaderLocation.equalsIgnoreCase("none")) {
                    mc.gameRenderer.shutdownEffect();
                    MythosWorldVisuals.alpha = 0; // Turn off global state
                    return;
                }

                mc.gameRenderer.loadEffect(new ResourceLocation(msg.shaderLocation));

                // Save to global state so the Tick Event can keep it alive
                MythosWorldVisuals.r = msg.r;
                MythosWorldVisuals.g = msg.g;
                MythosWorldVisuals.b = msg.b;
                MythosWorldVisuals.alpha = 1.0f;

                applyColorsToShader(msg.r, msg.g, msg.b);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            // If our custom sky color is active but no shader is loaded, force it
            if (mc.level != null && MythosWorldVisuals.alpha > 0) {
                if (mc.gameRenderer.currentEffect() == null) {
                    mc.gameRenderer.loadEffect(new ResourceLocation("trmythos", "shaders/post/master_sky.json"));
                    // Re-apply the current colors after forcing the load
                    applyColorsToShader(MythosWorldVisuals.r, MythosWorldVisuals.g, MythosWorldVisuals.b);
                }
            }
        }
    }

    public static void applyColorsToShader(float r, float g, float b) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameRenderer.currentEffect() != null) {
            var effect = mc.gameRenderer.currentEffect();

            // 1.19.2 Srg names for 'passes'
            String[] fieldNames = {"passes", "f_110011_", "field_147714_b"};
            java.lang.reflect.Field passesField = null;

            for (String name : fieldNames) {
                try {
                    passesField = net.minecraft.client.renderer.PostChain.class.getDeclaredField(name);
                    break;
                } catch (NoSuchFieldException ignored) {}
            }

            if (passesField != null) {
                try {
                    passesField.setAccessible(true);
                    java.util.List<net.minecraft.client.renderer.PostPass> passes =
                            (java.util.List<net.minecraft.client.renderer.PostPass>) passesField.get(effect);

                    for (net.minecraft.client.renderer.PostPass pass : passes) {
                        var uniform = pass.getEffect().getUniform("TintRGB");
                        if (uniform != null) {
                            uniform.set(r, g, b);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }
}