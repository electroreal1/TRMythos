package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LoserSkill extends Skill {
    public LoserSkill(SkillType type) {
        super(type);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Loser");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("A pathetic creature at heart, everything that touches your wretched soul seems to wither away...");
    }

    @Override
    public double getObtainingEpCost() {
        return 50000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        instance.addMasteryPoint(entity);
        if (SkillUtils.hasSkill(entity, UniqueSkills.CHOSEN_ONE.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.CHOSEN_ONE.get());
            SkillUtils.learnSkill(entity, Skills.FALSE_HERO.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.ABSOLUTE_SEVERANCE.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.ABSOLUTE_SEVERANCE.get());
            SkillUtils.learnSkill(entity, UniqueSkills.SEVERER.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.PREDATOR.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.PREDATOR.get());
            SkillUtils.learnSkill(entity, UniqueSkills.STARVED.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.GLUTTONY.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.GLUTTONY.get());
            SkillUtils.learnSkill(entity, UniqueSkills.STARVED.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.GOURMET.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.GOURMET.get());
            SkillUtils.learnSkill(entity, UniqueSkills.GOURMAND.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.INFINITY_PRISON.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.INFINITY_PRISON.get());
            SkillUtils.learnSkill(entity, io.github.Memoires.trmysticism.registry.skill.UniqueSkills.STAGNATOR.get());
        }

        if (SkillUtils.hasSkill(entity, io.github.Memoires.trmysticism.registry.skill.UniqueSkills.INVERSE.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(io.github.Memoires.trmysticism.registry.skill.UniqueSkills.INVERSE.get());
            SkillUtils.learnSkill(entity, UniqueSkills.REFLECTOR.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.CHEF.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.CHEF.get());
            SkillUtils.learnSkill(entity, UniqueSkills.COOK.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.DIVINE_BERSERKER.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.DIVINE_BERSERKER.get());
            SkillUtils.learnSkill(entity, UniqueSkills.BERSERK.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.BERSERKER.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.BERSERKER.get());
            SkillUtils.learnSkill(entity, UniqueSkills.BERSERK.get());
        }

        if (SkillUtils.hasSkill(entity, UniqueSkills.MARTIAL_MASTER.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(UniqueSkills.MARTIAL_MASTER.get());
            SkillUtils.learnSkill(entity, UniqueSkills.FIGHTER.get());
        }

        if (SkillUtils.hasSkill(entity, Skills.OMNISCIENT_EYE.get())) {
            SkillStorage storage = SkillAPI.getSkillsFrom(entity);
            storage.forgetSkill(Skills.OMNISCIENT_EYE.get());
            SkillUtils.learnSkill(entity, UniqueSkills.OBSERVER.get());
        }

        if (entity instanceof Player player) {
            if ((TensuraPlayerCapability.isDemonLordSeed(player) || TensuraPlayerCapability.isHeroEgg(player)) && instance.isMastered(entity)) {
                SkillUtils.learnSkill(entity, Skills.TENACIOUS.get());
            }
        }
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {return instance.isMastered(entity);}

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        if (!isInSlot(attacker) && !instance.isToggled())
            return;
        if (e.getSource().getEntity() != attacker)
            return;
        if (!DamageSourceHelper.isPhysicalAttack(e.getSource()))
            return;
        LivingEntity target = e.getEntity();
        Level level = attacker.level;
        int loser = instance.isMastered(attacker) ? 2 : 0;
        target.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.PARALYSIS.get(), 200, loser), (Entity)attacker);
        target.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.CORROSION.get(), 200, loser), (Entity)attacker);
        target.addEffect(new MobEffectInstance((MobEffect)MobEffects.WITHER, 200, loser), (Entity)attacker);
        level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.GENERIC_BURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        ServerLevel serverLevel = (ServerLevel) level;
        ((ServerLevel)level).sendParticles((ParticleOptions)TensuraParticles.PARALYSING_BUBBLE.get(), (target.position()).x,
                (target.position()).y + target.getBbHeight() / 2.0D, (target.position()).z, 20, 0.08D, 0.08D, 0.08D, 0.15D);
        ((ServerLevel)level).sendParticles((ParticleOptions)TensuraParticles.ACID_BUBBLE.get(), (target.position()).x,
                (target.position()).y + target.getBbHeight() / 2.0D, (target.position()).z, 20, 0.08D, 0.08D, 0.08D, 0.15D);
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 10 == 0)
            addMasteryPoint(instance, attacker);
        tag.putInt("activatedTimes", time + 1);

        if (e.getSource().getEntity() == attacker && DamageSourceHelper.isPhysicalAttack(e.getSource())) {
            LivingEntity t = e.getEntity();
            int durabilityBreak = (int)Math.max(1.0F, e.getAmount() / 4.0F);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.getType().equals(EquipmentSlot.Type.HAND) ||
                        t.getItemBySlot(slot).canPerformAction(ToolActions.SHIELD_BLOCK)) {
                    ItemStack slotStack = t.getItemBySlot(slot);
                    slotStack.hurtAndBreak(durabilityBreak, t, living -> living.broadcastBreakEvent(slot));
                }
            }
        }
    }

    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent e) {
        if (!isInSlot(e.getEntity()) && !instance.isToggled())
            return;
        Entity entity = e.getSource().getDirectEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity source = (LivingEntity)entity;
            source.getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak(5, source, attacker -> attacker.swing(InteractionHand.MAIN_HAND));
        }
    }

    @Override
    public int modes() {
        return 1;
    }

    public Component getModeName(int mode) {
        MutableComponent name = switch (mode) {
            case 1 -> Component.literal("Self-Deprecation");
            default -> Component.empty();
        };
        return name;
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            Level level = entity.level;

            if (heldTicks % 20 == 0) {
                level.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_SCREAM, SoundSource.PLAYERS, 1.0F, 0.8F);
            }

            if (heldTicks % 4 == 0) {
                TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(new ResourceLocation("tensura:starved_corrosion"), entity.getId(), 0.0, 1.0, 0.0, true));
            }

            if (heldTicks % 10 == 0) {
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0),
                        (target) -> target != entity && target.isAlive() && !target.isAlliedTo(entity));

                if (!list.isEmpty()) {
                    double ownerEP = TensuraEPCapability.getEP(entity);
                    float damage = instance.isMastered(entity) ? 100.0F : 50.0F;

                    for (LivingEntity target : list) {
                        if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                        double targetEP = TensuraEPCapability.getEP(target);
                        double difference = ownerEP / Math.max(targetEP, 1.0);

                        if (difference > 2.0) {

                            int effectLevel = instance.isMastered(entity) ? 2 : 0;

                            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.BURDEN.get(), 100, effectLevel);
                            SkillHelper.checkThenAddEffectSource(target, entity, MobEffects.WEAKNESS, 100, effectLevel);
                            SkillHelper.checkThenAddEffectSource(target, entity, MobEffects.MOVEMENT_SLOWDOWN, 100, effectLevel);

                            target.hurt(TensuraDamageSources.CORROSION, damage);

                            HakiSkill.hakiPush(target, entity, effectLevel);
                        }
                    }
                    this.addMasteryPoint(instance, entity);
                }
            }
        }
        return true;
    }
}
