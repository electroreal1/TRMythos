package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.attribute.AttributeModifierHelper;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.data.TensuraTags;
import com.github.manasmods.tensura.entity.human.CloneEntity;
import com.github.manasmods.tensura.event.PossessionEvent;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.dimensions.TensuraDimensions;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.attribute.TensuraAttributeModifierIds;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Demonologist extends Skill {
    public Demonologist(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/demonologist.png");
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1 -> name = Component.translatable("trmythos.skill.demonologist.possession");
            case 2 -> name = Component.translatable("trmythos.skill.demonologist.abyss");
            default -> name = Component.empty();
        }
        return name;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (!(living instanceof Player player)) return;
        Level level = living.level;
        if (!(level instanceof ServerLevel server)) return;

        RandomSource rand = level.random;

        int points = 5;
        double radius = 0.8;
        int segments = 50;
        double y = living.getY() + 2.05;
        double tilt = Math.toRadians(30);


        for (int i = 0; i < points; i++) {
            double angle = i * 2 * Math.PI / points; // rotation + i * 2 * Math.PI / points;
            double px = living.getX() + Math.cos(angle) * radius;
            double pz = living.getZ() + Math.sin(angle) * radius;
            double py = y + Math.sin(tilt) * radius;


            int count = 1 + rand.nextInt(3);
            for (int c = 0; c < count; c++) {
                server.sendParticles(
                        new DustParticleOptions(new Vector3f(1f, 0f, 0f), 1f),
                        px, py + (rand.nextDouble() - 0.5) * 0.02, pz,
                        10, 0, 0, 0, 0
                );
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity owner) {
        if (instance.getMode() == 1) {
            if (owner instanceof Player player) {
                Level level = owner.getLevel();
                if (!level.dimension().equals(TensuraDimensions.HELL) && !level.dimension().equals(TensuraDimensions.LABYRINTH)) {
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                        if (cap.isSpiritualForm()) {
                            LivingEntity target = SkillHelper.getTargetingEntity(owner, 5.0, false);
                            if (target == null || !target.isAlive()) {
                                return;
                            }

                            if (!this.canPossess(target, player)) {
                                player.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                                return;
                            }

                            PossessionEvent event = new PossessionEvent(target, player);
                            if (MinecraftForge.EVENT_BUS.post(event)) {
                                return;
                            }

                            ((ServerPlayer)player).teleportTo((ServerLevel)level, target.position().x, target.position().y, target.position().z, target.getYRot(), target.getXRot());
                            player.hurtMarked = true;
                            level.playSound((Player)null, target.getX(), target.getY(), target.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SQUID_INK, 2.0, 20);
                            this.copyStatsAndSkills(target, player);
                            CloneEntity.copyEffects(target, player);
                            if (target instanceof CloneEntity) {
                                CloneEntity clone = (CloneEntity)target;
                                if (clone.isOwnedBy(player)) {
                                    clone.copyEquipmentsOntoOwner(player, false);
                                    clone.resetOwner((UUID)null);
                                }
                            }

                            if (target instanceof Player) {
                                Player targetPlayer = (Player)target;
                                TensuraPlayerCapability.getFrom(targetPlayer).ifPresent((targetCap) -> {
                                    targetCap.setSpiritualForm(true);
                                    targetCap.applyBaseAttributeModifiers(targetPlayer);
                                    targetPlayer.getAbilities().mayfly = true;
                                    targetPlayer.getAbilities().flying = true;
                                    targetPlayer.onUpdateAbilities();
                                    SkillStorage storage = SkillAPI.getSkillsFrom(targetPlayer);
                                    Iterator var3 = List.copyOf(storage.getLearnedSkills()).iterator();

                                    while(var3.hasNext()) {
                                        ManasSkillInstance temp = (ManasSkillInstance)var3.next();
                                        if (temp.isTemporarySkill()) {
                                            if (temp.getTag() != null) {
                                                temp.getTag().remove("SpatialStorage");
                                            }

                                            storage.forgetSkill(temp);
                                        }
                                    }

                                });
                                TensuraPlayerCapability.sync(targetPlayer);
                            } else {
                                target.skipDropExperience();
                                if (!target.hurt(TensuraDamageSources.SOUL_SCATTER, target.getMaxHealth() * 10.0F)) {
                                    target.die(TensuraDamageSources.SOUL_SCATTER);
                                    target.discard();
                                } else {
                                    target.deathTime = 19;
                                }
                            }

                            cap.setSpiritualForm(false);
                            if (!player.isCreative() && !player.isSpectator()) {
                                player.getAbilities().mayfly = false;
                                player.getAbilities().flying = false;
                                player.onUpdateAbilities();
                            }
                        } else {
                            double EP = TensuraEPCapability.getEP(owner);
                            EntityType<CloneEntity> type = (EntityType) TensuraEntityTypes.CLONE_DEFAULT.get();
                            CloneEntity clonex = new CloneEntity(type, level);
                            clonex.setLife((Integer) TensuraConfig.INSTANCE.skillsConfig.bodyDespawnTick.get() * 20);
                            clonex.tame(player);
                            clonex.setSkill(this);
                            clonex.setImmobile(true);
                            clonex.setHealth(owner.getHealth());
                            clonex.copyEquipments(owner);
                            EquipmentSlot[] var10 = EquipmentSlot.values();
                            int var11 = var10.length;

                            for(int var12 = 0; var12 < var11; ++var12) {
                                EquipmentSlot slot = var10[var12];
                                player.setItemSlot(slot, ItemStack.EMPTY);
                            }

                            TensuraEPCapability.setLivingEP(clonex, Math.max(EP / 100.0, 100.0));
                            clonex.copyStatsAndSkills(owner, CloneEntity.CopySkill.INTRINSIC, true);
                            clonex.setRemainingFireTicks(owner.getRemainingFireTicks());
                            CloneEntity.copyEffects(player, clonex);
                            AttributeInstance cloneHP = clonex.getAttribute(Attributes.MAX_HEALTH);
                            Race race = TensuraPlayerCapability.getRace(player);
                            if (cloneHP != null && race != null) {
                                AttributeModifier cloneModifier = cloneHP.getModifier(TensuraAttributeModifierIds.RACE_BASE_HEALTH_MODIFIER_ID);
                                if (cloneModifier != null) {
                                    double raceHP = race.getBaseHealth() - player.getAttributeBaseValue(Attributes.MAX_HEALTH);
                                    if (cloneModifier.getAmount() == raceHP) {
                                        clonex.setLife(-1);
                                        instance.getOrCreateTag().putUUID("OriginalBody", clonex.getUUID());
                                        instance.markDirty();
                                    }
                                }
                            }

                            clonex.moveTo(owner.position().x, owner.position().y, owner.position().z, owner.getYRot(), owner.getXRot());
                            level.addFreshEntity(clonex);
                            cap.setSpiritualForm(true);
                            if (!player.isCreative() && !player.isSpectator()) {
                                player.getAbilities().mayfly = true;
                                player.getAbilities().flying = true;
                                player.onUpdateAbilities();
                            }

                            cap.applyBaseAttributeModifiers(player);
                            level.playSound((Player)null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                            SkillStorage storage = SkillAPI.getSkillsFrom(player);
                            Iterator var23 = List.copyOf(storage.getLearnedSkills()).iterator();

                            while(var23.hasNext()) {
                                ManasSkillInstance temp = (ManasSkillInstance)var23.next();
                                if (temp.isTemporarySkill()) {
                                    if (temp.getTag() != null) {
                                        temp.getTag().remove("SpatialStorage");
                                    }

                                    storage.forgetSkill(temp);
                                }
                            }
                        }

                        TensuraPlayerCapability.sync(player);
                    });
                } else {
                    player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                }
            }
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity owner, int heldTicks) {
        if (instance.getMode() == 2) {
            if (!(owner instanceof Player player)) return false;
            Level level = owner.level;
            if (!(level instanceof ServerLevel server)) return false;

            double radius = 10.0;
            double pullStrength = 0.1;
            double hellDistance = 2.0;

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,
                    owner.getBoundingBox().inflate(radius),
                    e -> e != owner);

            RandomSource rand = level.random;

            for (LivingEntity target : entities) {
                Vec3 dir = new Vec3(owner.getX() - target.getX(),
                        owner.getY() - target.getY(),
                        owner.getZ() - target.getZ());

                double distance = dir.length();
                if (distance < 0.001) continue;


                double angle = Math.atan2(dir.z, dir.x) + Math.PI / 2;
                double spiralX = Math.cos(angle) * 0.2;
                double spiralZ = Math.sin(angle) * 0.2;

                Vec3 motion = dir.normalize().scale(pullStrength).add(spiralX, 0, spiralZ);
                target.setDeltaMovement(motion.x, motion.y * 0.1, motion.z);


                for (int i = 0; i < 3; i++) {
                    double px = target.getX() + (rand.nextDouble() - 0.5) * 0.5;
                    double py = target.getY() + rand.nextDouble() * 1.0;
                    double pz = target.getZ() + (rand.nextDouble() - 0.5) * 0.5;
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0, 0.05, 0);
                }


                if (distance <= hellDistance) {
                    ResourceKey<Level> hellDimension = TensuraDimensions.HELL;
                    ServerLevel hellLevel = server.getServer().getLevel(hellDimension);
                    if (hellLevel == null) continue;


                    for (int i = 0; i < 20; i++) {
                        double ox = (rand.nextDouble() - 0.5);
                        double oy = rand.nextDouble() * 1.5;
                        double oz = (rand.nextDouble() - 0.5);
                        level.addParticle(ParticleTypes.PORTAL,
                                target.getX() + ox, target.getY() + oy, target.getZ() + oz,
                                0, 0.05, 0);
                    }

                    if (target instanceof ServerPlayer serverPlayer) {
                        serverPlayer.teleportTo(hellLevel,
                                target.getX(), target.getY() + 1, target.getZ(),
                                target.getYRot(), target.getXRot());
                    } else if (target instanceof Mob mob) {
                        Mob newMob = (Mob) mob.getType().create(hellLevel);
                        if (newMob != null) {
                            newMob.moveTo(target.getX(), target.getY() + 1, target.getZ(),
                                    target.getYRot(), target.getXRot());
                            hellLevel.addFreshEntity(newMob);
                            target.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }
                }
            }


            for (int i = 0; i < 15; i++) {
                double ox = (rand.nextDouble() - 0.5) * radius;
                double oz = (rand.nextDouble() - 0.5) * radius;
                level.addParticle(ParticleTypes.SOUL,
                        owner.getX() + ox, owner.getY() + 0.5, owner.getZ() + oz,
                        0, -0.05, 0);
            }
        }
        return true;
    }

    private boolean canPossess(LivingEntity target, Player player) {
        if (target.getType().is(TensuraTags.EntityTypes.NO_POSSESSION)) {
            return false;
        } else if (RaceHelper.isSpiritualLifeForm(target)) {
            return false;
        } else if (player.isCreative()) {
            return true;
        } else {
            if (target instanceof CloneEntity) {
                CloneEntity clone = (CloneEntity)target;
                if (clone.getSkill() != this) {
                    return false;
                }

                if (clone.getOwner() == player) {
                    return true;
                }
            }

            if (SkillUtils.isSkillToggled(target, (ManasSkill) ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                return false;
            } else {
                double amplifier = 1.0;
                if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                    amplifier = 0.5;
                }

                if (target instanceof Player) {
                    Player targetPlayer = (Player)target;
                    if (!targetPlayer.isCreative() && !target.isSpectator()) {
                        int requirement = 0;
                        if ((double)target.getHealth() < (double)(target.getMaxHealth() * 0.1F) * amplifier) {
                            ++requirement;
                        }

                        if (TensuraEPCapability.getSpiritualHealth(target) < target.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) * 0.10000000149011612 * amplifier) {
                            ++requirement;
                        }

                        if (TensuraEPCapability.getEP(target) < TensuraEPCapability.getEP(player) * 0.25 * amplifier) {
                            ++requirement;
                        }

                        return requirement >= 2;
                    }
                }

                if ((double)target.getHealth() < (double)target.getMaxHealth() * 0.1 * amplifier) {
                    return true;
                } else if (TensuraEPCapability.getSpiritualHealth(target) < target.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get()) * 0.10000000149011612 * amplifier) {
                    return true;
                } else {
                    return TensuraEPCapability.getEP(target) < TensuraEPCapability.getEP(player) * 0.25 * amplifier;
                }
            }
        }
    }

    private boolean canCopySkill(ManasSkillInstance instance, LivingEntity target, boolean clone) {
        if (clone) {
            return instance.isTemporarySkill();
        } else if (target instanceof Player) {
            Player player = (Player)target;
            return instance.isTemporarySkill() ? true : TensuraPlayerCapability.getIntrinsicList(player).contains(SkillUtils.getSkillId(instance.getSkill()));
        } else {
            return true;
        }
    }

    public void copyStatsAndSkills(LivingEntity target, Player owner) {
        this.applyBaseAttributeModifiers(owner, target);
        owner.setHealth(Math.max(target.getHealth(), 0.0F));
        Iterator var3 = target.getActiveEffects().iterator();

        while(var3.hasNext()) {
            MobEffectInstance instance = (MobEffectInstance)var3.next();
            owner.addEffect(new MobEffectInstance(instance));
        }

        boolean clone = target instanceof CloneEntity;
        Iterator var8 = List.copyOf(SkillAPI.getSkillsFrom(target).getLearnedSkills()).iterator();

        while(true) {
            ManasSkillInstance instance;
            do {
                do {
                    if (!var8.hasNext()) {
                        return;
                    }

                    instance = (ManasSkillInstance)var8.next();
                } while(!this.canCopySkill(instance, target, clone));
            } while(instance.getMastery() < 0 && instance.getMastery() != -100);

            ManasSkillInstance copy = TensuraSkillInstance.fromNBT(instance.toNBT());
            if (!copy.isTemporarySkill()) {
                copy.setRemoveTime(-2);
            }

            if (SkillUtils.learnSkill(owner, copy)) {
                owner.displayClientMessage(Component.translatable("tensura.skill.temporary.success_drain", new Object[]{copy.getSkill().getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
            }
        }
    }

    public void applyBaseAttributeModifiers(LivingEntity owner, LivingEntity target) {
        Map.Entry map;
        double value;
        for(Iterator var3 = this.getStatList().entrySet().iterator(); var3.hasNext(); AttributeModifierHelper.setModifier(owner, (Attribute)map.getKey(), new AttributeModifier((UUID)((Triple)map.getValue()).getLeft(), (String)((Triple)map.getValue()).getMiddle(), value - owner.getAttributeBaseValue((Attribute)map.getKey()), AttributeModifier.Operation.ADDITION))) {
            map = (Map.Entry)var3.next();
            AttributeInstance attribute = target.getAttribute((Attribute)map.getKey());
            if (attribute == null) {
                value = (Double)((Triple)map.getValue()).getRight();
            } else {
                value = target.getAttributeBaseValue((Attribute)map.getKey());
                AttributeModifier raceStat = attribute.getModifier((UUID)((Triple)map.getValue()).getLeft());
                if (raceStat != null) {
                    value += raceStat.getAmount();
                }
            }

            if (((Attribute)map.getKey()).equals(Attributes.MOVEMENT_SPEED) && !(target instanceof Player)) {
                value = value / 0.23 * 0.1;
            } else if (((Attribute)map.getKey()).equals(Attributes.ATTACK_DAMAGE) && !this.isOwnClone(owner, target)) {
                value = Math.min(value, (Double)TensuraConfig.INSTANCE.racesConfig.maxAttackPossession.get());
            } else if (((Attribute)map.getKey()).equals(Attributes.MAX_HEALTH) && !this.isOwnClone(owner, target)) {
                value = Math.min(value, (Double)TensuraConfig.INSTANCE.racesConfig.maxHeathPossession.get());
            }
        }

        AttributeInstance jumpStrength = target.getAttribute(Attributes.JUMP_STRENGTH);
        double jump;
        if (jumpStrength == null) {
            AttributeInstance jumpPower = target.getAttribute((Attribute) ManasCoreAttributes.JUMP_POWER.get());
            if (jumpPower == null) {
                jump = 0.42;
            } else {
                jump = target.getAttributeBaseValue((Attribute)ManasCoreAttributes.JUMP_POWER.get());
                AttributeModifier raceStat = jumpPower.getModifier(TensuraAttributeModifierIds.RACE_JUMP_HEIGHT_MODIFIER_ID);
                if (raceStat != null) {
                    jump += raceStat.getAmount();
                }
            }
        } else {
            jump = Math.max(target.getAttributeBaseValue(Attributes.JUMP_STRENGTH) / 0.7 * 0.42, 0.42);
        }

        AttributeModifierHelper.setModifier(owner, (Attribute)ManasCoreAttributes.JUMP_POWER.get(), new AttributeModifier(TensuraAttributeModifierIds.RACE_JUMP_HEIGHT_MODIFIER_ID, "tensura:race_jump_power", jump - owner.getAttributeBaseValue((Attribute)ManasCoreAttributes.JUMP_POWER.get()), AttributeModifier.Operation.ADDITION));
    }

    public boolean isOwnClone(LivingEntity owner, LivingEntity target) {
        boolean var10000;
        if (target instanceof CloneEntity clone) {
            if (clone.isOwnedBy(owner)) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    private Map<Attribute, Triple<UUID, String, Double>> getStatList() {
        return Map.of(Attributes.MAX_HEALTH, Triple.of(TensuraAttributeModifierIds.RACE_BASE_HEALTH_MODIFIER_ID, "tensura:race_base_health", 1.0), Attributes.ATTACK_DAMAGE, Triple.of(TensuraAttributeModifierIds.RACE_ATTACK_DAMAGE_MODIFIER_ID, "tensura:race_attack_damage", 0.1), Attributes.KNOCKBACK_RESISTANCE, Triple.of(TensuraAttributeModifierIds.RACE_KNOCKBACK_RESISTANCE_MODIFIER_ID, "tensura:race_knockback_resistance", 0.0), Attributes.MOVEMENT_SPEED, Triple.of(TensuraAttributeModifierIds.RACE_MOVEMENT_SPEED_MODIFIER_ID, "tensura:race_movement_speed", 0.1));
    }
}
