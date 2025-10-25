package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CrimsonTyrantSkill extends Skill {
    public CrimsonTyrantSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public double getObtainingEpCost() {
        return 100000.0;
    }

    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, com.github.manasmods.tensura.ability.TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return 1000.0;
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1 -> name = Component.translatable("trmythos.skill.crimson_tyrant.tyrants_malevolence");
            case 2 -> name = Component.translatable("trmythos.skill.crimson_tyrant.blood_sovereignty");
            default -> name = Component.empty();
        }
        return name;
    }

    private static final HashMap<UUID, Integer> killStacks = new HashMap<>();


    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            UUID id = player.getUUID();
            int stacks = killStacks.getOrDefault(id, 0);

            stacks = Math.min(stacks + 1, 100);
            killStacks.put(id, stacks);

            float multiplier = 1.0f + (stacks * 0.01f);
            player.getAttributes().getInstance(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(4.0F * multiplier);
            player.getAttributes().getInstance(Attributes.ATTACK_SPEED).setBaseValue(0.1F * multiplier);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            float healAmount = event.getAmount() * 0.05f;
            player.heal(healAmount);
        }
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() != 1) return false;

        if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) return false;

        if (heldTicks % 100 == 0 && heldTicks > 0) this.addMasteryPoint(instance, entity);

        activateTyrantsMalevolence(instance, entity, heldTicks);

        return true;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() != 2) return;

        entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 2.0f, 0.9f);

        entity.addEffect(new MobEffectInstance(TensuraMobEffects.HAKI_COAT.get(), 1200, 1, false, false, false));
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 1200, 15, false, false, false));
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSPIRATION.get(), 1200, 10, false, false, false));

        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER);
        TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions) TensuraParticles.DARK_RED_LIGHTNING_SPARK.get(),
                entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);
        TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions) TensuraParticles.RED_FIRE.get(),
                entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);

        instance.setCoolDown(1200);
    }

    public static void activateTyrantsMalevolence(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() != 1) return;

        if (heldTicks % 20 == 0) {
            entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        if (heldTicks % 2 == 0) {
            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:demon_lord_haki"), entity.getId(), 0.0, 1.0, 0.0, true));
        }

        List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0),
                (living) -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity));

        if (!list.isEmpty()) {
            double scale = instance.getTag() == null ? 0.0 : instance.getTag().getDouble("scale");
            double multiplier = scale == 0.0 ? 1.0 : Math.min(scale, 1.0);
            double ownerEP = TensuraEPCapability.getEP(entity) * multiplier;

            for (LivingEntity target : list) {
                if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                double targetEP = TensuraEPCapability.getEP(target);
                double difference = ownerEP / targetEP;

                if (difference > 2.0) {
                    int fearLevel = (int) (difference * 0.5 - 1.0);
                    fearLevel = Math.min(fearLevel, TensuraConfig.INSTANCE.mobEffectConfig.maxFear.get());
                    SkillHelper.checkThenAddEffectSource(target, entity, (MobEffect) TensuraMobEffects.FEAR.get(), 200, fearLevel);
                    entity.addEffect(new MobEffectInstance(TensuraMobEffects.FRAGILITY.get(), 1200, 1, false, false, false));
                    HakiSkill.hakiPush(target, entity, fearLevel);
                }
            }
        }
    }
}
