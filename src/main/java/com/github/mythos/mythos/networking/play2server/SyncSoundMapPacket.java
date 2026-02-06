package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.handler.GlobalSoundMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncSoundMapPacket {
    private final Map<String, String> data;

    public SyncSoundMapPacket(Map<String, String> data) {
        this.data = data;
    }

    public SyncSoundMapPacket(FriendlyByteBuf buf) {
        this.data = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            data.put(buf.readUtf(), buf.readUtf());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(data.size());
        data.forEach((k, v) -> {
            buf.writeUtf(k);
            buf.writeUtf(v);
        });
    }

    public static void handle(SyncSoundMapPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GlobalSoundMap.setMap(msg.data);
        });
        ctx.get().setPacketHandled(true);
    }
}