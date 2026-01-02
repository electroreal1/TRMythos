package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.handler.TsunamiShakeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TsunamiShakePacket {
    private final int duration;
    private final float strength;

    public TsunamiShakePacket(int duration, float strength) {
        this.duration = duration;
        this.strength = strength;
    }

    public TsunamiShakePacket(FriendlyByteBuf buf) {
        this.duration = buf.readInt();
        this.strength = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(duration);
        buf.writeFloat(strength); // God i need lodestone my goated library
    }

    public static void handle(TsunamiShakePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TsunamiShakeHandler.start(msg.duration, msg.strength);
        });
        ctx.get().setPacketHandled(true);
    }
}
