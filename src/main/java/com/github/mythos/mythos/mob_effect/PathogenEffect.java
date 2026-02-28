package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.effect.template.TensuraMobEffect;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.handler.ContagionHandler;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class PathogenEffect extends TensuraMobEffect {
    public PathogenEffect() {
        super(MobEffectCategory.HARMFUL, 0x7FFF00);
    }

    @Override
    public void applyEffectTick(LivingEntity victim, int amplifier) {
        if (victim.level.isClientSide) return;

        CompoundTag tag = victim.getPersistentData();
        if (!tag.contains("ContagionSource")) return;

        UUID sourceUUID = tag.getUUID("ContagionSource");
        Player source = victim.level.getPlayerByUUID(sourceUUID);

        if (source != null) {
            // --- PATH OF LUST (Safety & Spread) ---
            int lustLvl = ContagionHandler.getMutationLevel(source, "Lust");
            if (victim.equals(source) && lustLvl > 0) return;

            // Spread Logic: Lust makes the virus jump to others
            float spreadChance = 0.01f + (lustLvl * 0.05f);
            if (victim.getRandom().nextFloat() < spreadChance) {
                int virulence = ContagionHandler.getMutationLevel(source, "Virulence");
                double spreadRadius = 2.0 + (virulence * 0.5);
                List<LivingEntity> targets = victim.level.getEntitiesOfClass(LivingEntity.class, victim.getBoundingBox().inflate(spreadRadius),
                        e -> e != victim && e != source && !e.hasEffect(MythosMobEffects.PATHOGEN.get()));
                if (!targets.isEmpty()) {
                    LivingEntity spreadTo = targets.get(victim.getRandom().nextInt(targets.size()));
                    spreadTo.addEffect(new MobEffectInstance(MythosMobEffects.PATHOGEN.get(), 600, amplifier));
                    spreadTo.getPersistentData().putUUID("ContagionSource", sourceUUID);
                }
            }

            // --- PATH OF ENVY (Attribute Stealing) ---
            int envyLvl = ContagionHandler.getMutationLevel(source, "Envy");
            if (envyLvl > 0 && victim.tickCount % 100 == 0) {
                ContagionHandler.applyEnvySteal(source, victim, envyLvl);
            }

            int wrathLvl = ContagionHandler.getMutationLevel(source, "Wrath");
            if (wrathLvl > 0 && victim.tickCount % 20 == 0) {
                List<LivingEntity> horde = source.level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(15),
                        e -> e.hasEffect(MythosMobEffects.PATHOGEN.get()));
                double regenBonus = horde.size() * (0.5 * wrathLvl);
                TensuraPlayerCapability.getFrom(source).ifPresent(cap -> {
                    double maxMP = source.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
                    double maxAP = source.getAttributeValue(TensuraAttributeRegistry.MAX_AURA.get());

                    cap.setAura(Math.min(cap.getAura() + ((regenBonus)), maxAP));
                    cap.setMagicule(Math.min(cap.getMagicule() + (regenBonus), maxMP));
                });
            }

            // --- PATH OF PRIDE (Burst Damage) ---
            int prideLvl = ContagionHandler.getMutationLevel(source, "Pride");
            float baseDamage = 1.0f + (amplifier * 0.5f);
            if (prideLvl > 0) {
                tag.putFloat("PrideBuffer", tag.getFloat("PrideBuffer") + baseDamage);
                int burstTimer = Math.max(40, 200 - (lustLvl * 20));
                if (victim.tickCount % burstTimer == 0) {
                    float burst = tag.getFloat("PrideBuffer") * (1.0f + (prideLvl * 0.25f));
                    victim.hurt(TensuraDamageSources.infection(source), burst);
                    tag.putFloat("PrideBuffer", 0.0f);
                }
            } else {
                int maliceLvl = ContagionHandler.getMutationLevel(source, "Malice");
                float wrathBonus = wrathLvl > 0 ? (wrathLvl * 1.2f) : 0;
                victim.hurt(TensuraDamageSources.infection(source), baseDamage + (maliceLvl * 1.5f) + wrathBonus);
            }

            int slothLvl = ContagionHandler.getMutationLevel(source, "Sloth");
            if (slothLvl > 0) {
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, slothLvl - 1, false, false));
                if (victim instanceof Player victimPlayer) {
                    victimPlayer.getFoodData().addExhaustion(0.5f * slothLvl);
                }
            }

            int deceptionLvl = ContagionHandler.getMutationLevel(source, "Deception");
            if (deceptionLvl > 0) {
                victim.addEffect(new MobEffectInstance(MythosMobEffects.PATHOGEN_DECEPTION.get(), 100, deceptionLvl - 1, false, false));
            }

            int greedLvl = ContagionHandler.getMutationLevel(source, "Greed");
            if (greedLvl > 0 && victim.tickCount % 40 == 0) {
                double drain = 100.0 * greedLvl;
                SkillHelper.drainMP(victim, source, drain, false);
                SkillHelper.drainAP(victim, source, drain);
                if (victim instanceof Player vp) {
                    vp.giveExperiencePoints(-2 * greedLvl);
                    source.giveExperiencePoints(2 * greedLvl);
                }
            }

            int gluttonyLvl = ContagionHandler.getMutationLevel(source, "Gluttony");
            if (gluttonyLvl > 0 && (victim.isDeadOrDying() || victim.getHealth() < 2.0f)) {
                if (!tag.getBoolean("GluttonyProcessed")) {
                    processGluttony(source, victim, gluttonyLvl);
                    tag.putBoolean("GluttonyProcessed", true);
                }
            }
        }

        victim.level.getEntitiesOfClass(Player.class, victim.getBoundingBox().inflate(8)).forEach(rival -> {
            if (!rival.getUUID().equals(sourceUUID)) {
                ContagionHandler.tryViralTakeover(victim, sourceUUID, rival.getUUID());
            }
        });
    }

    private void processGluttony(Player host, LivingEntity victim, int level) {
        if (host.level.random.nextFloat() < (0.02f * level)) {
            SkillStorage storage = SkillAPI.getSkillsFrom(victim);
            Collection<ManasSkillInstance> learnedSkills = storage.getLearnedSkills();
            if (!learnedSkills.isEmpty()) {
                List<ManasSkillInstance> list = new ArrayList<>(learnedSkills);
                ManasSkillInstance stolen = list.get(host.level.random.nextInt(list.size()));
                if (!com.github.manasmods.tensura.ability.SkillUtils.hasSkill(host, stolen.getSkill())) {
                    com.github.manasmods.tensura.ability.SkillUtils.learnSkill(host, stolen.getSkill());
                    host.displayClientMessage(Component.literal("§d[Contagion Gluttony] §7Assimilated: §f" + Objects.requireNonNull(stolen.getSkill().getName()).getString()), false);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}