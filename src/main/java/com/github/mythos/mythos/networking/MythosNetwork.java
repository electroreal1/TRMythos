package com.github.mythos.mythos.networking;

import com.github.mythos.mythos.networking.play2server.GreatSilencePacket;
import com.github.mythos.mythos.networking.play2server.ScreenShakePacket;
import com.github.mythos.mythos.networking.play2server.ShaderPacket;
import com.github.mythos.mythos.networking.play2server.SkillCopyPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MythosNetwork {
    private static final String PROTOCOL_VERSION = "1"; // Simplified for compatibility
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("trmythos", "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final AtomicInteger PACKET_ID = new AtomicInteger();

    public static void register() {
        registerPacket(SkillCopyPacket.class,
                SkillCopyPacket::toBytes,
                SkillCopyPacket::new,
                SkillCopyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        registerPacket(ScreenShakePacket.class,
                ScreenShakePacket::encode,
                ScreenShakePacket::decode,
                ScreenShakePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        registerPacket(ShaderPacket.class,
                ShaderPacket::encode,
                ShaderPacket::decode,
                ShaderPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        registerPacket(GreatSilencePacket.class,
                GreatSilencePacket::encode,
                GreatSilencePacket::decode,
                GreatSilencePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private static <MSG> void registerPacket(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> direction) {
        INSTANCE.registerMessage(PACKET_ID.getAndIncrement(), messageType, encoder, decoder, messageConsumer, direction);
    }


    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG> void sendToAll(MSG msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToAllTrackingAndSelf(MSG msg, LivingEntity tracked) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> tracked), msg);
    }
}