package com.github.mythos.mythos.ability.mythos.skill.unique.racial;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.race.MythosRaces;
import io.github.Memoires.trmysticism.registry.entity.MysticismEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.UUID;

public class VanagandrSkill extends Skill {
    public VanagandrSkill(SkillType type) {super(SkillType.UNIQUE);}
    public static final UUID RAGNAROK = UUID.fromString("1b34b928-7ad3-3d9e-8421-b26fd3ad9456");

    //@Override
    //public ResourceLocation getSkillIcon() {
    //    return new ResourceLocation("textures/skills/vanagandr.png");
    //}

    @Override
    public int getMaxMastery() {
        return 1000;
    }

    public double getObtainingEpCost() {
        return 500000;
    }

    public boolean canBeToggled() {return true;}

    public boolean canTick() {return true;}

    public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity) {
        return (instance.getMode() == 2);
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 6 == 0)
            addMasteryPoint(instance, entity);
        tag.putInt("activatedTimes", time + 1);
    }

    public int modes() {return 2;}

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.vanagandr.god_slayer");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.vanagandr.cryokinetic_essence");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent event) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isCold(event.getSource())) {
                if (instance.isMastered(living)) {
                    event.setAmount(event.getAmount() * 3.0F);
                } else {
                    event.setAmount(event.getAmount() * 2.0F);
                }
            }
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Race playerRace = TensuraPlayerCapability.getRace(entity);
        if (playerRace == null || !playerRace.equals(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.HERALD_OF_RAGNAROK_RACE))) {
            Player player = (Player) entity;
            player.displayClientMessage((Component)Component.translatable("trmythos.skill.race_locked").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
            return;
        }
        if (instance.getMode() == 1) {
            godSlayer(instance, entity);
        }
    }

    public void godSlayer(ManasSkillInstance instance, LivingEntity entity) {
        MobEffectInstance effectInstance = entity.getEffect((MobEffect)TensuraMobEffects.MAGIC_INTERFERENCE.get());
        if (effectInstance != null && effectInstance.getAmplifier() >= 1) {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                player.displayClientMessage((Component)Component.translatable("tensura.skill.magic_interference")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
            }
            return;
        }
        if (!entity.hasEffect((MobEffect)MythosMobEffects.GOD_SLAYER.get())) {
            if (SkillHelper.outOfMagicule(entity, instance))
                return;
            instance.setCoolDown(1200);
            entity.setYBodyRot(entity.getYRot());
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.0F, 1.0F);
            entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.GOD_SLAYER.get(), isMastered(instance, entity) ? 7200 : 3600, 1, false, false, false));
            if (entity instanceof Player) {
                Player player = (Player)entity;
                TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                    boolean shouldSync = false;
                    double maxMagicule = entity.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_MAGICULE.get());
                    if (cap.getMagicule() != maxMagicule) {
                        cap.setMagicule(Math.max(maxMagicule, cap.getMagicule()));
                        shouldSync = true;
                    }
                    double maxAura = entity.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_AURA.get());
                    if (cap.getAura() != maxAura) {
                        cap.setMagicule(Math.max(maxAura, cap.getAura()));
                        shouldSync = true;
                    }
                    if (shouldSync)
                        TensuraPlayerCapability.sync(player);
                });
            }
            TensuraParticleHelper.addServerParticlesAroundSelf((Entity)entity, (ParticleOptions)ParticleTypes.CLOUD);
            TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions)TensuraParticles.LIGHTNING_SPARK.get(), entity
                    .getX(), entity.getY(), entity.getZ(), 55, 0.08D, 0.08D, 0.08D, 0.5D, true);
            TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions)TensuraParticles.YELLOW_LIGHTNING_SPARK.get(), entity
                    .getX(), entity.getY(), entity.getZ(), 55, 0.08D, 0.08D, 0.08D, 0.5D, true);
        } else {
            entity.removeEffect((MobEffect)MythosMobEffects.GOD_SLAYER.get());
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        float damage;
        switch (instance.getMode()) {
            case 2:
                if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance))
                    return false;
                if (heldTicks % 100 == 0 && heldTicks > 0)
                    addMasteryPoint(instance, entity);
                damage = instance.isMastered(entity) ? 500.0F : 250.0F;
                BreathEntity.spawnBreathEntity((EntityType) MysticismEntityTypes.ICE_BREATH.get(), entity, instance, damage, magiculeCost(entity, instance));
                entity.level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                return true;
        }
        return true;
    }
}
