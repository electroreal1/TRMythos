package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.handler.GlobalSoundMap;
import com.github.mythos.mythos.networking.MythosNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record SoundSwapPacket(String original, String replacement, boolean isRemoval) {
    public static void handle(SoundSwapPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (msg.isRemoval) {
                GlobalSoundMap.remove(msg.original);
            } else {
                GlobalSoundMap.add(msg.original, msg.replacement);
            }
            MythosNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncSoundMapPacket(GlobalSoundMap.getMap()));
        });
        ctx.get().setPacketHandled(true);
    }
}