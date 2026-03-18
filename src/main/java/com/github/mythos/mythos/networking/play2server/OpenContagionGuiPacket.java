package com.github.mythos.mythos.networking.play2server;

import com.github.mythos.mythos.client.screen.ContagionSreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class OpenContagionGuiPacket {
    private final int biomatter;
    private final Map<String, Integer> levels;

    public OpenContagionGuiPacket(int biomatter, Map<String, Integer> levels) {
        this.biomatter = biomatter;
        this.levels = levels;
    }

    public static OpenContagionGuiPacket decode(FriendlyByteBuf buffer) {
        int bio = buffer.readInt();
        Map<String, Integer> levels = buffer.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
        return new OpenContagionGuiPacket(bio, levels);
    }

    public static void encode(OpenContagionGuiPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.biomatter);
        buffer.writeMap(msg.levels, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(new ContagionSreen(biomatter, levels));
        });
        return true;
    }
}
