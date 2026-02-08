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
    private static final String SEPARATOR = "will encounter a";

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer talker = event.getPlayer();
        String message = event.getRawText();
        String lowerMessage = message.toLowerCase();

        ManasSkillInstance inst = SkillUtils.getSkillOrNull(talker, Skills.AUTHOR.get());
        if (inst != null && inst.getOrCreateTag().getBoolean("isWriting")) {
            if (lowerMessage.contains(SEPARATOR)) {
                event.setCanceled(true);
                executeEnvision(talker, message);
                return;
            }
        }

        talker.server.getPlayerList().getPlayers().forEach(potentialAuthor -> {
            if (potentialAuthor != talker && SkillUtils.hasSkill(potentialAuthor, Skills.AUTHOR.get())) {
                String authorName = potentialAuthor.getName().getString().toLowerCase();
                if (lowerMessage.contains(authorName)) {
                    potentialAuthor.sendSystemMessage(Component.literal("§d[Meta-Awareness] §e" + talker.getName().getString() + " §fplotted at: ")
                            .append(Component.literal(talker.blockPosition().toShortString()).withStyle(ChatFormatting.YELLOW)));
                    potentialAuthor.playNotifySound(SoundEvents.UI_TOAST_IN, SoundSource.PLAYERS, 0.4f, 1.2f);
                }
            }
        });
    }

    private static void executeEnvision(ServerPlayer author, String script) {
        int index = script.toLowerCase().indexOf(SEPARATOR);
        String namePart = script.substring(0, index).trim();
        String effectType = script.toLowerCase().substring(index + SEPARATOR.length()).trim();

        ServerPlayer target = namePart.equalsIgnoreCase("me") ? author : author.server.getPlayerList().getPlayerByName(namePart);

        if (target != null) {
            author.sendSystemMessage(Component.literal("§d[Author] §fThe ink flows... §b" + target.getName().getString() + "§f's destiny is rewritten."));

            ServerLevel level = target.getLevel();
            BlockPos pos = target.blockPosition();

            level.sendParticles(ParticleTypes.ENCHANT, target.getX(), target.getY() + 1, target.getZ(), 50, 0.5, 1.0, 0.5, 0.1);

            switch (getMatchedEffect(effectType)) {
                case "lightning" -> EntityType.LIGHTNING_BOLT.spawn(level, null, null, pos, MobSpawnType.COMMAND, true, true);
                case "fire" -> spawnFireCircle(level, pos);
                case "meteor" -> executeMeteor(level, pos);
                case "earthquake" -> executeEarthquake(target);
                case "hurricane" -> executeHurricane(target);
                case "sinkhole" -> executeSinkhole(target);
                case "explosion" -> level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 5.0f, Explosion.BlockInteraction.NONE);
                case "healing" -> target.heal((float) (target.getMaxHealth() * 0.1));
                default -> author.sendSystemMessage(Component.literal("§c[Author] §7The ink fades... that destiny is not yet written."));
            }

            level.playSound(null, author.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.5f, 0.8f);
        } else {
            assert author != null;
            author.sendSystemMessage(Component.literal("§c[Author] §7Character '" + namePart + "' is not in this chapter."));
        }
    }

    private static String getMatchedEffect(String input) {
        if (input.contains("lightning")) return "lightning";
        if (input.contains("fire") || input.contains("flame")) return "fire";
        if (input.contains("meteor") || input.contains("star")) return "meteor";
        if (input.contains("quake") || input.contains("earthquake")) return "earthquake";
        if (input.contains("wind") || input.contains("hurricane")) return "hurricane";
        if (input.contains("hole") || input.contains("sinkhole")) return "sinkhole";
        if (input.contains("boom") || input.contains("explosion")) return "explosion";
        return "none";
    }

    private static void spawnFireCircle(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            BlockPos firePos = pos.offset(Math.cos(angle) * 2, 0, Math.sin(angle) * 2);
            if (level.isEmptyBlock(firePos)) level.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
        }
        level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
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
