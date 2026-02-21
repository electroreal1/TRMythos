package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldScapegoatSkill extends Skill {
    private static final String CURSE_COUNT = "CurseStacks";
    private static final String SCAPEGOAT_ACTIVE = "DesignatedActive";
    private static final String SCAPEGOAT_TIMER = "ScapegoatTimer";
    private static final String COMPRESSION_EXTRA = "CompressionExtra";

    public WorldScapegoatSkill(SkillType type) {
        super(type);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("World's Scapegoat").withStyle(ChatFormatting.DARK_RED);
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("When the world falters, it seeks a bearer. When it curses, it chooses one.");
    }

    @Override
    public int modes() {
        return 3;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Designated Scapegoat");
            case 2 -> Component.literal("Curse Draw");
            case 3 -> Component.literal("Burden Compression");
            default -> super.getModeName(mode);
        };
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide || !(entity instanceof Player user)) return;

        CompoundTag tag = instance.getOrCreateTag();
        boolean isActive = tag.getBoolean(SCAPEGOAT_ACTIVE);

        handleRedirectionLogic(user, tag, isActive);


        int harmfulCount = 0;
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                harmfulCount++;
            }
        }
        tag.putInt(CURSE_COUNT, harmfulCount + tag.getInt(COMPRESSION_EXTRA));

        if (isActive) {
            int timer = tag.getInt(SCAPEGOAT_TIMER);
            if (timer > 0) {
                tag.putInt(SCAPEGOAT_TIMER, timer - 1);
            } else {
                tag.putBoolean(SCAPEGOAT_ACTIVE, false);
                if (user instanceof ServerPlayer sp)
                    sp.displayClientMessage(Component.literal("The world seeks a new bearer... (Expired)").withStyle(ChatFormatting.GRAY), true);
            }
        }
    }

    private void handleRedirectionLogic(Player user, CompoundTag tag, boolean isActive) {
        double range = 15.0;
        List<LivingEntity> allies = user.level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(range),
                e -> e != user && e.isAlive() && (e instanceof Player || e.isAlliedTo(user)));

        for (LivingEntity ally : allies) {
            List<MobEffectInstance> effectsToMove = new ArrayList<>(ally.getActiveEffects());
            for (MobEffectInstance effect : effectsToMove) {
                if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    if (isActive) {
                        user.addEffect(new MobEffectInstance(effect));
                        ally.removeEffect(effect.getEffect());
                    } else {
                        user.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier()));
                        if (effect.getDuration() > 20) {
                            int newDuration = effect.getDuration() / 2;
                            ally.removeEffect(effect.getEffect());
                            ally.addEffect(new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.getMode() == 1) {
            boolean currentlyActive = tag.getBoolean(SCAPEGOAT_ACTIVE);
            tag.putBoolean(SCAPEGOAT_ACTIVE, !currentlyActive);
            if (!currentlyActive) {
                tag.putInt(SCAPEGOAT_TIMER, 1200);
                player.displayClientMessage(Component.literal("Designated Scapegoat: Active").withStyle(ChatFormatting.RED), true);
            } else {
                player.displayClientMessage(Component.literal("Designated Scapegoat: Cancelled").withStyle(ChatFormatting.GRAY), true);
            }
        } else if (instance.getMode() == 2) {
            handleRedirectionLogic(player, tag, true);
            player.displayClientMessage(Component.literal("Curses drawn to the bearer.").withStyle(ChatFormatting.DARK_PURPLE), true);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() != 3) return false;

        CompoundTag tag = instance.getOrCreateTag();

        if (heldTicks % 10 == 0) {
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.5F);
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.5F);

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:mortal_fear"), entity.getId(), 0.0, 1.0, 0.0, true));

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:demon_lord_haki"), entity.getId(), 0.0, 1.1, 0.0, true));

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:sub_mortal_fear"), entity.getId(), 0.0, 0.9, 0.0, true));
        }

        if (heldTicks % 200 == 0) {
            int extra = tag.getInt(COMPRESSION_EXTRA);
            if (extra < 10) tag.putInt(COMPRESSION_EXTRA, extra + 1);
        }

        return true;
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        instance.getOrCreateTag().putInt(COMPRESSION_EXTRA, 0);
    }

    @SubscribeEvent
    public void onEffectApplied(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (entity.level.isClientSide) return;
        if (!SkillUtils.hasSkill(entity, this)) return;

        MobEffectInstance incomingEff = event.getEffectInstance();

        if (incomingEff.getEffect().getCategory() != MobEffectCategory.HARMFUL) return;

        MobEffectInstance existingEff = entity.getEffect(incomingEff.getEffect());

        if (existingEff != null) {
            if (incomingEff.getAmplifier() <= existingEff.getAmplifier()) {

                event.setCanceled(true);

                int newAmp = existingEff.getAmplifier() + 1;
                if (newAmp > 15) newAmp = 15;

                int newDur = (int) (existingEff.getDuration() + (incomingEff.getDuration() * 0.5));

                MobEffectInstance stackedInstance = new MobEffectInstance(incomingEff.getEffect(), newDur, newAmp);
                entity.addEffect(stackedInstance);

                if (entity instanceof Player p) {
                    p.displayClientMessage(Component.literal("The curse deepens...")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSurvivalCheck(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (SkillUtils.hasSkill(entity, this)) {
            if (event.getSource().isMagic() && entity.getHealth() - event.getAmount() <= 0) {
                event.setAmount(entity.getHealth() - 1.0f);
            }
        }
    }

    @SubscribeEvent
    public void onHumanKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        LivingEntity target = event.getEntity();
        boolean isHuman = target instanceof Player || target instanceof Villager || target instanceof Pillager;

        if (!isHuman) return;

        if (player.getHealth() > 1.1f) return;

        int harmfulCount = (int) player.getActiveEffects().stream()
                .filter(effect -> effect.getEffect().getCategory() == MobEffectCategory.HARMFUL)
                .count();

        if (harmfulCount >= 15) {
            evolveToShadowAvenger(player);
        }
    }

    private void evolveToShadowAvenger(ServerPlayer player) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);

        storage.learnSkill(Skills.SHADOW_AVENGER.get());
        storage.syncAll();

        player.level.playSound(null, player.blockPosition(),
                SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (player.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1, player.getZ(), 100, 0.5, 1.0, 0.5, 0.2);
            serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 10, 0, 0, 0, 0);
        }

        player.sendSystemMessage(Component.literal("The world's burden becomes too heavy to bear... it ignites into Hatred.")
                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
    }
}