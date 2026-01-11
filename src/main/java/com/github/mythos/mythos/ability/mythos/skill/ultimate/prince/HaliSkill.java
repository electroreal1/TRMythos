package com.github.mythos.mythos.ability.mythos.skill.ultimate.prince;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.human.CloneEntity;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HaliSkill extends Skill {
    public static final UUID TSUKUYOMI = UUID.fromString("ff1ba7e1-9b9a-4580-8f6f-c5db93f93651");
    protected static final UUID ACCELERATION = UUID.fromString("8f08f72d-014b-4b4b-8560-987a0375959d");
    private final Set<String> usedDeathTypes = new HashSet<>();

    public HaliSkill(SkillType ultimate) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public double getObtainingEpCost() {
        return 5000000.0;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Hali, ")
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal("Sunken Sun").withStyle(ChatFormatting.BLACK));
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, UltimateSkills.TSUKUYOMI.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        SkillUtils.learnSkill(entity, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get());
    }

    @Override
    public int modes() {
        return 5;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) {
            return instance.getMode() == 1 ? 5 : instance.getMode() - 1;
        } else {
            return instance.getMode() == 5 ? 1 : instance.getMode() + 1;
        }
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return switch (instance.getMode()) {
            case 1 -> 125000.0;
            case 2 -> 50000.0;
            case 3 -> 0.0;
            case 4 -> 75000.0;
            case 5 -> 800000.0;
            default -> 50000.0;
        };
    }

    @Override
    public Component getModeName(int mode) {
        boolean isShifting = false;
        if (FMLEnvironment.dist == Dist.CLIENT) {
            isShifting = safeCheckShifting();
        }

        return switch (mode) {
            case 1 -> Component.translatable("trmysticism.skill.mode.tsukuyomi.eye_of_the_moon");
            case 2 -> Component.translatable("trmysticism.skill.mode.tsukuyomi.insta_kill");
            case 3 -> Component.literal("Ultraspeed Action");
            case 4 -> Component.translatable("trmysticism.skill.mode.tsukuyomi.parallel_existence");
            case 5 -> isShifting ? Component.literal("Sunrise Sun") : Component.literal("Sunset Moon");
            default -> Component.empty();
        };
    }

    private boolean safeCheckShifting() {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.isShiftKeyDown();
    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        if (DamageSourceHelper.isFireDamage(event.getSource())) {
            event.setAmount((float) (event.getAmount() * 0.5));
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event) {
        if (DamageSourceHelper.isDarkDamage(event.getSource())) {
            event.setAmount(event.getAmount() * 3);
        }

        if (this.isInSlot(attacker) && instance.getMode() == 2 && !instance.onCoolDown()) {
            DamageSource source = event.getSource();
            if (source.getEntity() == attacker && DamageSourceHelper.isPhysicalAttack(source) && !SkillHelper.outOfMagicule(attacker, instance)) {
                LivingEntity target = event.getEntity();
                float targetMaxSHP = (float)target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());

                if (SkillUtils.hasSkill(target, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                    DamageSourceHelper.directSpiritualHurt(target, attacker, 7000.0F + targetMaxSHP * 0.4F);
                } else if (SkillUtils.hasSkill(target, ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                    if (instance.isMastered(attacker)) {
                        DamageSourceHelper.directSpiritualHurt(target, attacker, Float.MAX_VALUE);
                    } else {
                        DamageSourceHelper.directSpiritualHurt(target, attacker, 12000.0F + targetMaxSHP * 0.8F);
                    }
                } else {
                    DamageSourceHelper.directSpiritualHurt(target, attacker, Float.MAX_VALUE);
                }
                instance.setCoolDown(instance.isMastered(attacker) ? 5 : 10);
                if (!target.isAlive()) event.setCanceled(true);
            }
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get(), 300, 1, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.HEAT_SENSE.get(), 300, 1, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.AUDITORY_SENSE.get(), 300, 1, false, false, false));
        }
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 5) {
            // Apply effect to the USER so they broadcast the "Domain Center" visuals
            boolean isShifting = entity.isShiftKeyDown();
            MobEffect domainEffect = isShifting ? MythosMobEffects.SUNRISE.get() : MythosMobEffects.SUNSET.get();
            entity.addEffect(new MobEffectInstance(domainEffect, 40, 0, false, false, true));

            if (heldTicks % 20 == 0) {
                if (SkillHelper.outOfMagicule(entity, instance)) return false;

                AABB area = entity.getBoundingBox().inflate(25.0);
                List<LivingEntity> targets = entity.level.getEntitiesOfClass(LivingEntity.class, area, e -> e != entity);

                for (LivingEntity target : targets) {
                    boolean isMoving = target.getDeltaMovement().lengthSqr() > 0.001;

                    if ((isShifting && !isMoving) || (!isShifting && isMoving)) {

                        float targetMaxSHP = (float) target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
                        double drainPercent = isShifting ? 0.20 : 0.15;

                        DamageSourceHelper.directSpiritualHurt(target, entity, (float) (targetMaxSHP * drainPercent));
                        if (target instanceof Player player) {
                            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                                cap.setMagicule(cap.getMagicule() * (1.0 - drainPercent));
                            });
                        }
                    }
                }
            }
            return true;
        }

        if (instance.getMode() == 4) {
            CompoundTag tag = instance.getTag();
            int clones = tag != null ? tag.getInt("clones") : 10;
            if (entity instanceof Player player) {
                player.displayClientMessage(Component.translatable("tensura.skill.output_number", clones).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
            }
            return true;
        }

        return true;
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 5) {
            instance.setCoolDown(300);
        }

        if (instance.getMode() == 4 && this.isHeld(entity)) {
            Level level = entity.getLevel();
            AttributeInstance recon = entity.getAttribute((Attribute)TensuraAttributeRegistry.SIZE.get());
            if (recon != null && recon.getModifier(TSUKUYOMI) != null) {
                recon.removePermanentModifier(TSUKUYOMI);
                instance.setCoolDown(10);
                level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                this.addMasteryPoint(instance, entity);
                CompoundTag tag = instance.getTag();
                if (tag == null) {
                    this.summonClones(entity, level, 10);
                } else {
                    this.summonClones(entity, level, tag.getInt("clones"));
                }

                AttributeModifier tsukuyomi = new AttributeModifier(TSUKUYOMI, "TsukuyomiClonesEffect", 0.0, AttributeModifier.Operation.ADDITION);
                if (recon != null && recon.getModifier(TSUKUYOMI) == null) {
                    recon.addPermanentModifier(tsukuyomi);
                }
            }
        }
    }


    public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta) {
        if (instance.getMode() == 4) {
            CompoundTag tag = instance.getOrCreateTag();
            int newScale = tag.getInt("clones") + (int)delta;
            if (newScale > 10) {
                newScale = 1;
            } else if (newScale < 1) {
                newScale = 10;
            }

            if (tag.getInt("clones") != newScale) {
                tag.putInt("clones", newScale);
                instance.markDirty();
            }
        }

    }

    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();
            AttributeInstance recon = entity.getAttribute(TensuraAttributeRegistry.SIZE.get());
            if (recon != null && recon.getModifier(TSUKUYOMI) != null) {
                recon.removePermanentModifier(TSUKUYOMI);
            }
        }

        if (!(event.getEntity() instanceof Player player)) return;
        String damageType = event.getSource().getMsgId();
        if (!usedDeathTypes.contains(damageType)) {

            event.setCanceled(true);

            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);

            player.level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.5F);

            usedDeathTypes.add(damageType);

            player.displayClientMessage(Component.literal("§eSelf Necromancing: §0Revived against " + damageType), true);
        }

    }

    private void summonClones(LivingEntity entity, Level level, int number) {
        level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 1.0);
        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 2.0);
        double EP = TensuraEPCapability.getEP(entity) * 0.5 / (double)number;
        EntityType<CloneEntity> type = entity.isShiftKeyDown() ? (EntityType)TensuraEntityTypes.CLONE_SLIM.get() : (EntityType)TensuraEntityTypes.CLONE_DEFAULT.get();

        for(int i = 0; i < number; ++i) {
            CloneEntity clone = new CloneEntity(type, level);
            if (entity instanceof Player player) {
                clone.tame(player);
            }

            clone.setSkill(this);
            clone.copyStatsAndSkills(entity, false);
            TensuraEPCapability.setLivingEP(clone, (double)Math.round(EP));
            clone.setPos(entity.position());
            clone.copySize(entity);
            level.addFreshEntity(clone);
        }

    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            switch (instance.getMode()) {
                case 1 -> this.eyeOfTheMoon(instance, entity);
                case 3 -> this.ultraspeedAction(instance, entity);
            }
        }
    }

    private void ultraspeedAction(ManasSkillInstance instance, LivingEntity entity) {
        if ((entity.isOnGround() || entity.isInWaterOrBubble())) {
            this.addMasteryPoint(instance, entity);
            Level level = entity.getLevel();
            int range = instance.isMastered(entity) ? 80 : 50;
            BlockHitResult result = SkillHelper.getPlayerPOVHitResult(level, entity, ClipContext.Fluid.NONE, range);
            BlockPos resultPos = result.getBlockPos().relative(result.getDirection());
            Vec3 vec3 = SkillHelper.getFloorPos(resultPos);

            if (!entity.getLevel().getWorldBorder().isWithinBounds(new BlockPos(vec3.x(), vec3.y(), vec3.z()))) return;

            Vec3 source = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
            Vec3 offSetToTarget = vec3.subtract(source);

            for(int i = 1; i < Mth.floor(offSetToTarget.length()); ++i) {
                Vec3 particlePos = source.add(offSetToTarget.normalize().scale(i));
                if (level instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SWEEP_ATTACK, particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0);
                }

                AABB aabb = (new AABB(new BlockPos(particlePos))).inflate(3.0);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aabb, t -> t != entity && !t.isAlliedTo(entity));

                for (LivingEntity target : targets) {
                    float bonus = instance.isMastered(entity) ? 1400.0F : 750.0F;
                    float baseDmg = (float)(entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * entity.getAttributeValue(ManasCoreAttributes.CRIT_MULTIPLIER.get()));

                    if (target.hurt(this.sourceWithMP(DamageSource.mobAttack(entity), entity, instance), baseDmg + bonus)) {
                        float targetMaxSHP = (float)target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());

                        if (instance.isMastered(entity)) {
                            if (SkillUtils.hasSkill(target, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                                DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                            } else {
                                DamageSourceHelper.directSpiritualHurt(target, entity, Float.MAX_VALUE);
                            }
                        } else {
                            DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                        }
                    }
                }
            }
            entity.moveTo(vec3);
            entity.swing(InteractionHand.MAIN_HAND, true);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public void eyeOfTheMoon(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.hasEffect(TensuraMobEffects.SHADOW_STEP.get())) {
            this.addMasteryPoint(instance, entity);
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.SHADOW_STEP.get(), 6000, 0, false, false, false));
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            entity.removeEffect(TensuraMobEffects.SHADOW_STEP.get());
        }
    }


}
