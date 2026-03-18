package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.handler.ContagionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MutationPacket {
    private final String path;

    public MutationPacket(String path) {
        this.path = path;
    }

    public static void encode(MutationPacket msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.path);
    }

    public static MutationPacket decode(FriendlyByteBuf buffer) {
        return new MutationPacket(buffer.readUtf());
    }

    public static void handle(MutationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ContagionHandler.handleMutationLogic(player, msg.path);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
