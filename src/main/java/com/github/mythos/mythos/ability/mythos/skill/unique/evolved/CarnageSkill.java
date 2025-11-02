package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.race.MythosRaces;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CarnageSkill extends Skill {

    // Config toggles
    public static boolean VAMPIRE_CARNAGE = true;
    public static boolean CarnageBloodDominion = true;

    private static final HashMap<UUID, Integer> killStacks = new HashMap<>();

    public CarnageSkill(SkillType type) {
        super(type);
    }

    @Override
    public int getMaxMastery() {
        return 4000;
    }

    @Override
    public double getObtainingEpCost() {
        return 500000.0;
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        UUID id = player.getUUID();
        int stacks = killStacks.getOrDefault(id, 0);
        stacks = Math.min(stacks + 1, 100); // stack cap
        killStacks.put(id, stacks);

        double attackBonus = 4.0F + (stacks * 0.05F);
        double speedBonus = 0.1F + (stacks * 0.01F);

        player.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackBonus);
        player.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(speedBonus);

        if (player.getPersistentData().getBoolean("AbsoluteBloodLordActive")) {
            extendOrAddEffectDuration(player, MythosMobEffects.BLOOD_COAT.get(), 200);
            extendOrAddEffectDuration(player, TensuraMobEffects.HAKI_COAT.get(), 200);
            extendOrAddEffectDuration(player, TensuraMobEffects.STRENGTHEN.get(), 200);
            extendOrAddEffectDuration(player, TensuraMobEffects.INSPIRATION.get(), 200);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            float healAmount = event.getAmount() * 0.15f;
            player.heal(healAmount);
        }
    }


    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event, Player player) {
        if (!VAMPIRE_CARNAGE) return;
        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            Race vampireBaron = TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.VAMPIRE_BARON);
            if (cap.getRace() != vampireBaron) {
                cap.setRace(player, vampireBaron, true);
            }
        });
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.carnage.blood");
            case 2 -> Component.translatable("trmythos.skill.carnage.absolute");
            default -> Component.empty();
        };
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public void onPressed(ServerPlayer player, TensuraSkillInstance instance) {
        int mode = instance.getMode();
        if (mode == 1) BloodDominion(player, instance);
        else if (mode == 2) AbsoluteBloodlord(player, instance);
    }

    private void BloodDominion(ServerPlayer player, TensuraSkillInstance instance) {
        double range = 15.0;
        float casterEP = (float) TensuraEPCapability.getCurrentEP(player);

        List<LivingEntity> targets = player.getLevel().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(range),
                e -> e != player && e.isAlive() && !e.isAlliedTo(player)
        );

        player.getLevel().playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.2f);
        TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.CRIMSON_SPORE);

        if (!CarnageBloodDominion) {
            return;
        }

        for (LivingEntity target : targets) {
            if (target instanceof Player targetPlayer && targetPlayer.getAbilities().invulnerable) continue;

            float targetEP = target instanceof Player p ? (float) TensuraEPCapability.getCurrentEP(p) : 0f;
            if (targetEP > casterEP * 0.85f) continue;

            if (target instanceof Player p) {
                TensuraPlayerCapability.getFrom(p).ifPresent(cap -> {
                    Race vampireRace = TensuraRaces.VAMPIRE.get();
                    if (cap.getRace() != vampireRace) {
                        cap.setRace(p, vampireRace, true);
                    }
                });
            }

            TensuraParticleHelper.spawnServerParticles(
                    player.level, ParticleTypes.DAMAGE_INDICATOR,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    8, 0.1, 0.1, 0.1, 0.2, true
            );
        }
    }

    private void AbsoluteBloodlord(ServerPlayer player, TensuraSkillInstance instance) {
        player.getLevel().playSound(null, player.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 2.0f, 0.9f);

        player.addEffect(new MobEffectInstance(MythosMobEffects.BLOOD_COAT.get(), 1200, 1, false, false, false));
        player.addEffect(new MobEffectInstance(TensuraMobEffects.HAKI_COAT.get(), 1200, 1, false, false, false));
        player.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 1200, 25, false, false, false));
        player.addEffect(new MobEffectInstance(TensuraMobEffects.INSPIRATION.get(), 1200, 15, false, false, false));

        player.getPersistentData().putBoolean("AbsoluteBloodLordActive", true);

        TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.EXPLOSION_EMITTER);
        TensuraParticleHelper.spawnServerParticles(
                player.level, (ParticleOptions) TensuraParticles.DARK_RED_LIGHTNING_SPARK.get(),
                player.getX(), player.getY(), player.getZ(),
                55, 0.08, 0.08, 0.08, 0.5, true
        );

        instance.setCoolDown(1200);
    }

    private static void extendOrAddEffectDuration(LivingEntity entity, MobEffect effect, int extraDuration) {
        if (entity == null || effect == null) return;
        MobEffectInstance current = entity.getEffect(effect);
        if (current != null) {
            int newDuration = current.getDuration() + extraDuration;
            entity.addEffect(new MobEffectInstance(effect, newDuration, current.getAmplifier(),
                    current.isAmbient(), current.isVisible(), current.showIcon()));
        } else {
            entity.addEffect(new MobEffectInstance(effect, extraDuration, 0, false, false, false));
        }
    }
}
