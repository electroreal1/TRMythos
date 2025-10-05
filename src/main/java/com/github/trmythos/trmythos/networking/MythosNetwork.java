package com.github.trmythos.trmythos.networking;

import com.github.manasmods.tensura.network.play2client.SyncPlayerCapabilityPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MythosNetwork {
    private static final String PROTOCOL_VERSION = ModList.get().getModFileById("trmythos").versionString().replaceAll("\\.", "");
    private static final SimpleChannel INSTANCE;
    private static final AtomicInteger PACKET_ID;

    public MythosNetwork() {
    }

    public static void register() {

    }

    private static <MSG> void registerPacket(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        INSTANCE.registerMessage(PACKET_ID.getAndIncrement(), messageType, encoder, decoder, messageConsumer);
    }

    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendTo(MSG msg, LivingEntity target) {
        if (target instanceof Player) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> {
                return (ServerPlayer) target;
            }), msg);
        }
    }

    public static <MSG> void sendToAllTrackingAndSelf(MSG msg, LivingEntity tracked) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> {
            return tracked;
        }), msg);
    }

    static {
        ResourceLocation var10000 = new ResourceLocation("trmythos", "main_channel");
        Supplier var10001 = () -> {
            return PROTOCOL_VERSION;
        };
        String var10002 = PROTOCOL_VERSION;
        Objects.requireNonNull(var10002);
        Predicate var0 = var10002::equals;
        String var10003 = PROTOCOL_VERSION;
        Objects.requireNonNull(var10003);
        INSTANCE = NetworkRegistry.newSimpleChannel(var10000, var10001, var0, var10003::equals);
        PACKET_ID = new AtomicInteger();
    }

}
