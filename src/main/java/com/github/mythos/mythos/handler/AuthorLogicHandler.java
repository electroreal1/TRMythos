package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.ScreenShakePacket;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class AuthorLogicHandler {
    private static boolean isInternalProcessing = false;
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        if (isInternalProcessing) return;

        if (event.isCanceled() || event.getRawText() == null || event.getRawText().isEmpty()) return;

        ServerPlayer talker = event.getPlayer();
        String rawMessage = event.getRawText();
        String lowercaseMessage = rawMessage.toLowerCase();

        isInternalProcessing = true;
        try {
            ManasSkillInstance inst = SkillUtils.getSkillOrNull(talker, Skills.AUTHOR.get());
            if (inst != null && inst.getOrCreateTag().getBoolean("isWriting")) {
                if (lowercaseMessage.contains("will encounter a")) {
                    event.setCanceled(true);
                    executeEnvision(talker, rawMessage);
                    return;
                }
            }

            for (ServerPlayer potentialAuthor : talker.server.getPlayerList().getPlayers()) {
                if (SkillUtils.hasSkill(potentialAuthor, Skills.AUTHOR.get())) {
                    String authorName = potentialAuthor.getName().getString().toLowerCase();

                    if (lowercaseMessage.contains(authorName) && talker != potentialAuthor) {
                        potentialAuthor.sendSystemMessage(Component.literal("§d[Meta-Awareness] §f" + talker.getName().getString() + " mentioned you at: ")
                                .append(Component.literal(talker.blockPosition().toShortString()).withStyle(ChatFormatting.YELLOW)));
                        potentialAuthor.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.5f);
                    }
                }
            }
        } finally {
            isInternalProcessing = false;
        }
    }

    private static void executeEnvision(ServerPlayer author, String script) {
        String separator = "will encounter a";
        String lowerScript = script.toLowerCase();
        int index = lowerScript.indexOf(separator);

        if (index == -1) return;

        String targetName = script.substring(0, index).trim();
        String effectType = lowerScript.substring(index + separator.length()).trim();

        ServerPlayer target = author.server.getPlayerList().getPlayerByName(targetName);

        if (target != null) {
            author.sendSystemMessage(Component.literal("§d[Author] §fWriting destiny for §e" + target.getName().getString() + "..."));

            BlockPos pos = target.blockPosition();
            ServerLevel level = target.getLevel();

            if (effectType.contains("lightning")) {
                EntityType.LIGHTNING_BOLT.spawn(level, null, null, pos, MobSpawnType.COMMAND, true, true);
            } else if (effectType.contains("fire")) {
                level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                level.setBlockAndUpdate(pos.north(), Blocks.FIRE.defaultBlockState());
            } else if (effectType.contains("meteor")) {
                executeMeteor(level, pos);
            } else if (effectType.contains("earthquake")) {
                executeEarthquake(target);
            } else if (effectType.contains("hurricane")) {
                executeHurricane(target);
            } else if (effectType.contains("sinkhole")) {
                executeSinkhole(target);
            } else if (effectType.contains("explosion")) {
                level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 4.0f, Explosion.BlockInteraction.NONE);
            }

            level.playSound(null, author.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
        } else {
            author.sendSystemMessage(Component.literal("§c[Author] The character '" + targetName + "' is not present in this chapter."));
        }
    }


    private static void executeHurricane(LivingEntity target) {
        Level level = target.level;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY(), target.getZ(), 100, 2, 5, 2, 0.1);
            serverLevel.playSound(null, target.blockPosition(), SoundEvents.ELYTRA_FLYING, SoundSource.WEATHER, 2.0F, 0.5F);

            Vec3 lift = new Vec3(0, 0.8, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(lift));
            target.hurtMarked = true;
        }
    }

    private static void executeMeteor(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.WEATHER, 4.0F, 0.5F);
        for (int i = 0; i < 30; i++) {
            level.sendParticles(ParticleTypes.FLAME, pos.getX(), pos.getY() + i, pos.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
        }
        level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 6.0F, true, Explosion.BlockInteraction.NONE);
    }

    private static void executeEarthquake(LivingEntity target) {
        Level level = target.level;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, target.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.WEATHER, 1.0F, 0.1F);

            AABB area = target.getBoundingBox().inflate(15.0);
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, area);

            for (LivingEntity victim : victims) {
                if (victim instanceof ServerPlayer serverPlayer) {
                    MythosNetwork.sendToPlayer(new ScreenShakePacket(10.0f), serverPlayer);
                }

                if (victim.isOnGround()) {
                    victim.hurt(DamageSource.GENERIC, 6.0F);
                    victim.setDeltaMovement(0, 0.4, 0);
                    victim.hurtMarked = true;
                }
            }
        }
    }

    private static void executeSinkhole(LivingEntity target) {
        Level level = target.level;
        if (level instanceof ServerLevel serverLevel) {
            BlockPos center = target.blockPosition().below();
            serverLevel.playSound(null, center, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 2.0F, 0.5F);

            if (TensuraGameRules.canSkillGrief(level)) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        for (int y = 0; y > -10; y--) {
                            BlockPos targetBlock = center.offset(x, y, z);
                            if (!level.getBlockState(targetBlock).is(Blocks.BEDROCK)) {
                                level.setBlockAndUpdate(targetBlock, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
            target.setDeltaMovement(0, -1.5, 0);
            target.hurtMarked = true;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        }
    }

}
