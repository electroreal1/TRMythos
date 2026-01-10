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

    public ShaderPacket(String shaderLocation) {
        this.shaderLocation = shaderLocation;
    }

    public ShaderPacket(FriendlyByteBuf buf) {
        this.shaderLocation = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.shaderLocation);
    }

    public static ShaderPacket decode(FriendlyByteBuf buf) {
        return new ShaderPacket(buf);
    }

    public static void handle(ShaderPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();

                if (mc.gameRenderer != null && mc.level != null) {
                    try {
                        mc.gameRenderer.loadEffect(new ResourceLocation(msg.shaderLocation));
                    } catch (Exception e) {
                        System.err.println("Failed to load shader: " + msg.shaderLocation);
                        mc.gameRenderer.shutdownEffect();
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}