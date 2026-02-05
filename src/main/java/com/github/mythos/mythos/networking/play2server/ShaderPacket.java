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

                    if (mc.gameRenderer.currentEffect() != null) {
                        try {
                            java.lang.reflect.Field passesField = net.minecraft.client.renderer.PostChain.class.getDeclaredField("f_110011_"); // 'passes'
                            passesField.setAccessible(true);
                            @SuppressWarnings("unchecked")
                            java.util.List<net.minecraft.client.renderer.PostPass> passes = (java.util.List<net.minecraft.client.renderer.PostPass>) passesField.get(mc.gameRenderer.currentEffect());

                            for (net.minecraft.client.renderer.PostPass pass : passes) {
                                var uniform = pass.getEffect().getUniform("TintRGB");
                                if (uniform != null) {
                                    uniform.set(msg.r, msg.g, msg.b);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Mythos Shader Error: Could not access shader passes via reflection.");
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}