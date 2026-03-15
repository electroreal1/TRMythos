package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.menu.WhiteoutOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WhiteoutPacket {
    private final float intensity;

    public WhiteoutPacket(float intensity) {
        this.intensity = intensity;
    }

    public static void encode(WhiteoutPacket msg, FriendlyByteBuf buffer) {
        buffer.writeFloat(msg.intensity);
    }

    public static WhiteoutPacket decode(FriendlyByteBuf buffer) {
        return new WhiteoutPacket(buffer.readFloat());
    }

    public static void handle(WhiteoutPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WhiteoutOverlay.intensity = msg.intensity;
        });
        ctx.get().setPacketHandled(true);
    }}
