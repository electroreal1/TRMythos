package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.handler.GlobalEffectHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenShakePacket {
    private final float intensity;

    public ScreenShakePacket(float intensity) {
        this.intensity = intensity;
    }

    public static void encode(ScreenShakePacket msg, FriendlyByteBuf buffer) {
        buffer.writeFloat(msg.intensity);
    }

    public static ScreenShakePacket decode(FriendlyByteBuf buffer) {
        return new ScreenShakePacket(buffer.readFloat());
    }

    public static void handle(ScreenShakePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                GlobalEffectHandler.shakeIntensity = msg.intensity;
            });
        });
        ctx.get().setPacketHandled(true);
    }
}