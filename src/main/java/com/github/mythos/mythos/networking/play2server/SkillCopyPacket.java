package com.github.mythos.mythos.networking.play2server;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SkillCopyPacket {
    private ResourceLocation skillId;
    private UUID targetUUID;

    public SkillCopyPacket(ResourceLocation skillId, UUID targetUUID) {
        this.skillId = skillId;
        this.targetUUID = targetUUID;
    }

    public SkillCopyPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.skillId = buf.readResourceLocation();
        this.targetUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.skillId);
        buf.writeUUID(this.targetUUID);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.skillId);
        buf.writeUUID(this.targetUUID);
    }

    public static void handle(SkillCopyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // Debug: Check if packet arrived
            // System.out.println("COPY PACKET RECEIVED: " + msg.skillId);

            if (!SkillUtils.hasSkill(player, Skills.ORUNMILA.get())) return;

            SkillStorage authorStorage = SkillAPI.getSkillsFrom(player);
            Object obj = SkillAPI.getSkillRegistry().getValue(msg.skillId);

            if (authorStorage != null && obj instanceof ManasSkill skillToLearn) {
                if (authorStorage.learnSkill(skillToLearn)) {
                    player.displayClientMessage(Component.literal("§d[Author] §fRecord Replicated: ")
                            .append(Objects.requireNonNull(skillToLearn.getName()).copy().withStyle(ChatFormatting.AQUA)), true);

                    player.level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 1.5F);
                } else {
                    player.displayClientMessage(Component.literal("§c[Author] §fRecord already present in library."), true);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}