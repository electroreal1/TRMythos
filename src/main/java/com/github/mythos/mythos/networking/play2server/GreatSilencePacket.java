package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.handler.GlobalEffectHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GreatSilencePacket {

    private final boolean active;

    public GreatSilencePacket(boolean active) {
        this.active = active;
    }

    public GreatSilencePacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.active);
    }

    public static GreatSilencePacket decode(FriendlyByteBuf buf) {
        return new GreatSilencePacket(buf);
    }

    public static void handle(GreatSilencePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                GlobalEffectHandler.isGreatSilenceActive = msg.active;
            });
        });
        ctx.get().setPacketHandled(true);
    }
}