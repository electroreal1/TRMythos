package com.github.mythos.mythos.networking.play2server;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShaderPacket {
    private final String shaderLocation;
    private final float r, g, b;

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

                if (msg.shaderLocation.equals("none")) {
                    mc.gameRenderer.shutdownEffect();
                } else {
                    mc.gameRenderer.loadEffect(new ResourceLocation(msg.shaderLocation));

                    net.minecraft.client.renderer.PostChain effect = mc.gameRenderer.currentEffect();
                    if (effect != null) {
                        try {
                            java.lang.reflect.Field passesField;
                            try {
                                passesField = net.minecraft.client.renderer.PostChain.class.getDeclaredField("passes");
                            } catch (NoSuchFieldException e) {
                                passesField = net.minecraft.client.renderer.PostChain.class.getDeclaredField("f_110011_");
                            }

                            passesField.setAccessible(true);
                            java.util.List<net.minecraft.client.renderer.PostPass> passes =
                                    (java.util.List<net.minecraft.client.renderer.PostPass>) passesField.get(effect);

                            for (net.minecraft.client.renderer.PostPass pass : passes) {
                                // getEffect() is public and provides the uniform interface
                                var uniform = pass.getEffect().getUniform("TintRGB");
                                if (uniform != null) {
                                    uniform.set(msg.r, msg.g, msg.b);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}