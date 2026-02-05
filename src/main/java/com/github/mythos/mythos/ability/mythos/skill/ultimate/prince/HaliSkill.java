package com.github.mythos.mythos.ability.mythos.skill.ultimate.prince;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
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
import com.github.manasmods.tensura.registry.blocks.TensuraBlocks;
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
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.minecraft.ChatFormatting.*;

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
        return Component.literal("Hali").withStyle(YELLOW).append(", ").withStyle(WHITE).append(Component.literal("Sunken Sun").withStyle(BLACK));
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("In one kind of death the spirit also dieth, and this it hath been known to do while yet the body was in vigor for many years.");
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
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

        if (entity instanceof Player player) {
            triggerHaliLearningSequence(player);
        }

        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = UltimateSkills.TSUKUYOMI.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
        }
    }

    private void triggerHaliLearningSequence(Player player) {
        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 0.5f);

        player.sendSystemMessage(Component.literal("« Notice »").withStyle(WHITE, (BOLD)));

        player.sendSystemMessage(Component.literal("Condition Met: Mastery of Ultimate Skill [Tsukuyomi] has reached the threshold of Singularity.").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("Evolutionary Path Branching: The logic of \"Moon Shadow\" is being overwritten by the ").append(Component.literal("[Sunken Sun]").withStyle(WHITE, (BOLD))));

        player.sendSystemMessage(Component.literal("\nConfirmed: You have obtained the Skill ").withStyle(WHITE, BOLD));
        player.sendSystemMessage(Component.literal("[Hali").withStyle(YELLOW).append(", ").withStyle(WHITE).append("Sunken Sun].\n").withStyle(BLACK));

        player.sendSystemMessage(Component.literal("\nSystem Alert: Your spiritual body has ceased to reflect external magicules.").withStyle(GRAY));
        player.sendSystemMessage(Component.literal("You have entered the state of Lunar Stillness. The \"Voice of the World\" will now become muffled as you drift toward the Far Side of the Moon.").withStyle(DARK_GRAY, ITALIC));

        player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 0.5f);

        player.sendSystemMessage(Component.literal("\n« Success: The shadow has detached from the light. »").withStyle(GRAY));
    }

    @Override
    public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof Player player) {
            triggerHaliMasterySequence(player);
        }
    }

    private void triggerHaliMasterySequence(Player player) {
        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 0.5f);

        player.sendSystemMessage(Component.literal("« Notice »").withStyle(WHITE, (BOLD)));

        player.sendSystemMessage(Component.literal("Individual confirmed as having transcended the \"Cycle of the Sun.\"\n").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("Condition Met: The lungs of the spirit have adapted to the vacuum. Confirmed: Skill [Hali] has been fully integrated into the soul-circuit.\n ").withStyle(WHITE));

        player.sendSystemMessage(Component.literal("System Analysis:\n.").withStyle(WHITE, BOLD));

        player.sendSystemMessage(Component.literal("Biological Nullification: The Individual no longer consumes the \"Breath of Life.\"\n").withStyle(GRAY));
        player.sendSystemMessage(Component.literal("Sensory Redaction: Ambient noise and vibrations now bypass the user. You have become a \"Silent Singularity.\"\n").withStyle(DARK_GRAY, ITALIC));

        player.sendSystemMessage(Component.literal("Evolutionary Bridge: The pathway to the moon’s \"Dark Side\" is now open.\n").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("Current Status: The Individual is now categorized as a [Dweller of the Unseen]. Your presence is no longer recorded by the atmosphere, and your shadows are no longer cast by the light.\n").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("« Notification: \"In the silence where the spirit dieth, you have found the strength to remain.\" »\n").withStyle(GRAY));

        player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 0.5f);

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
                float targetMaxSHP = (float) target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());

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
                if (!target.isAlive()) {
                    event.setCanceled(true);
                }
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
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 5) {
            boolean isShifting = entity.isCrouching();

            MobEffect domainEffect = isShifting ? MythosMobEffects.SUNRISE.get() : MythosMobEffects.SUNSET.get();

            if (entity.hasEffect(MythosMobEffects.SUNSET.get()) && isShifting)
                entity.removeEffect(MythosMobEffects.SUNSET.get());
            if (entity.hasEffect(MythosMobEffects.SUNRISE.get()) && !isShifting)
                entity.removeEffect(MythosMobEffects.SUNRISE.get());

            entity.addEffect(new MobEffectInstance(domainEffect, 40, 0, false, false, true));

            if (heldTicks % 20 == 0) {

                AABB area = entity.getBoundingBox().inflate(12.5);
                List<LivingEntity> targets = entity.level.getEntitiesOfClass(LivingEntity.class, area, e -> e != entity);

                for (LivingEntity target : targets) {
                    boolean isMoving = target.getDeltaMovement().horizontalDistanceSqr() > 0.005;

                    if (!isShifting && isMoving) {
                        applyDrain(entity, target, 0.15, true);
                    } else if (isShifting && !isMoving) {
                        applyDrain(entity, target, 0.15, false);
                    }
                }
            }
            return true;
        }

        if (instance.getMode() != 4) {
            return false;
        } else {
            CompoundTag tag = instance.getTag();
            int clones = tag != null ? tag.getInt("clones") : 10;
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.displayClientMessage(Component.translatable("tensura.skill.output_number", new Object[]{clones}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
            }

            return true;
        }
    }

    public static void applyDrain(LivingEntity source, LivingEntity target, double percent, boolean isSpiritual) {
        if (isSpiritual) {
            float maxSHP = (float) target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
            DamageSourceHelper.directSpiritualHurt(target, source, (float) (maxSHP * percent));
        } else {
            target.setHealth((float) (target.getMaxHealth() * percent));
        }

        if (target instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                cap.setMagicule(cap.getMagicule() * (1.0 - percent));
                cap.setAura(cap.getAura() * (1.0 - percent));
            });
        }
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 5 && this.isHeld(entity)) {
            instance.setCoolDown(300);
        }

        if (instance.getMode() == 4 && this.isHeld(entity)) {
            Level level = entity.getLevel();
            AttributeInstance recon = entity.getAttribute(TensuraAttributeRegistry.SIZE.get());

            if (recon != null && recon.getModifier(TSUKUYOMI) != null) {
                recon.removePermanentModifier(TSUKUYOMI);
                instance.setCoolDown(10);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);

                AABB searchArea = entity.getBoundingBox().inflate(100.0);
                List<CloneEntity> activeClones = level.getEntitiesOfClass(CloneEntity.class, searchArea, clone -> clone.getOwner() == entity); // Only target clones owned by this player

                for (CloneEntity clone : activeClones) {
                    TensuraParticleHelper.addServerParticlesAroundSelf(clone, ParticleTypes.SQUID_INK, 1.0);
                    clone.discard();
                }
            } else {
                this.addMasteryPoint(instance, entity);
                CompoundTag tag = instance.getTag();
                int cloneCount = (tag != null) ? tag.getInt("clones") : 10;

                this.summonClones(entity, level, cloneCount);

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
            int newScale = tag.getInt("clones") + (int) delta;

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

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {

            if (event.level.getDayTime() % 24000 == 0) {

                this.usedDeathTypes.clear();

            }
        }
    }

    private void summonClones(LivingEntity entity, Level level, int number) {
        level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 1.0);
        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 2.0);
        double EP = TensuraEPCapability.getEP(entity) * 1 / (double) number;
        EntityType<CloneEntity> type = entity.isShiftKeyDown() ? (EntityType) TensuraEntityTypes.CLONE_SLIM.get() : (EntityType) TensuraEntityTypes.CLONE_DEFAULT.get();

        for (int i = 0; i < number; ++i) {
            CloneEntity clone = new CloneEntity(type, level);
            if (entity instanceof Player player) {
                clone.tame(player);
            }

            clone.setSkill(this);
            clone.copyStatsAndSkills(entity, false);
            TensuraEPCapability.setLivingEP(clone, (double) Math.round(EP));
            clone.setPos(entity.position());
            clone.copySize(entity);
            level.addFreshEntity(clone);
        }


    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            switch (instance.getMode()) {
                case 1:
                    this.eyeOfTheMoon(instance, entity);
                    break;
                case 3:
                    this.ultraspeedAction(instance, entity);
            }
        }
    }

    @Override
    public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMastery() < 0) {
            return false;
        } else {
            return instance.getMode() == 1 || instance.getMode() == 3;
        }
    }

    private void ultraspeedAction(ManasSkillInstance instance, LivingEntity entity) {
        if ((entity.isOnGround() || entity.isInWaterOrBubble()) && !SkillHelper.outOfAura(entity, instance)) {
            this.addMasteryPoint(instance, entity);
            Level level = entity.getLevel();
            int range = instance.isMastered(entity) ? 80 : 50;
            BlockHitResult result = SkillHelper.getPlayerPOVHitResult(level, entity, ClipContext.Fluid.NONE, (double) range);
            BlockPos resultPos = result.getBlockPos().relative(result.getDirection());
            Vec3 vec3 = SkillHelper.getFloorPos(resultPos);
            if (!level.getBlockState(resultPos).getMaterial().isReplaceable()) {
                vec3 = SkillHelper.getFloorPos(resultPos.above());
            }

            if (level.getBlockState(resultPos).is((Block) TensuraBlocks.LABYRINTH_BARRIER_BLOCK.get())) {
                level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else if (!entity.getLevel().getWorldBorder().isWithinBounds(new BlockPos(vec3.x(), vec3.y(), vec3.z()))) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.displayClientMessage(Component.translatable("tensura.skill.teleport.out_border").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                }
            } else {
                Vec3 source = entity.position().add(0.0, (double) (entity.getBbHeight() / 2.0F), 0.0);
                Vec3 offSetToTarget = vec3.subtract(source);

                for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); ++particleIndex) {
                    Vec3 particlePos = source.add(offSetToTarget.normalize().scale((double) particleIndex));
                    ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
                    TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SWEEP_ATTACK, 3.0);
                    TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SWEEP_ATTACK, 2.0);
                    AABB aabb = (new AABB(new BlockPos(particlePos))).inflate(Math.max(entity.getAttributeValue((Attribute) ForgeMod.ATTACK_RANGE.get()), 2.0));
                    List<LivingEntity> livingEntityList = level.getEntitiesOfClass(LivingEntity.class, aabb, (targetx) -> {
                        return !targetx.is(entity) && !targetx.isAlliedTo(entity);
                    });
                    if (!livingEntityList.isEmpty()) {
                        float bonus = instance.isMastered(entity) ? 1400F : 750.0F;
                        float amount = (float) (entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * entity.getAttributeValue((Attribute) ManasCoreAttributes.CRIT_MULTIPLIER.get()));
                        Iterator var16 = livingEntityList.iterator();

                        while (var16.hasNext()) {
                            LivingEntity target = (LivingEntity) var16.next();
                            float targetMaxSHP = (float) target.getAttributeValue((Attribute) TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
                            if (target.hurt(this.sourceWithMP(DamageSource.mobAttack(entity), entity, instance), amount + bonus)) {
                                if (!instance.isMastered(entity)) {
                                    DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                                } else if (SkillUtils.hasSkill(target, (ManasSkill) ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                                    if (entity.getRandom().nextFloat() > 0.25F) {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, Float.MAX_VALUE);
                                    } else {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                                    }
                                } else if (SkillUtils.hasSkill(target, (ManasSkill) ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                                    if (entity.getRandom().nextFloat() > 0.5F) {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, Float.MAX_VALUE);
                                    } else {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                                    }
                                } else {
                                    DamageSourceHelper.directSpiritualHurt(target, entity, Float.MAX_VALUE);
                                }

                                ItemStack stack = entity.getMainHandItem();
                                stack.getItem().hurtEnemy(stack, target, entity);
                                entity.getLevel().playSound((Player) null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, entity.getSoundSource(), 1.0F, 1.0F);
                                if (level instanceof ServerLevel serverLevel) {
                                    serverLevel.getChunkSource().broadcastAndSend(entity, new ClientboundAnimatePacket(entity, 4));
                                }
                            }
                        }
                    }
                }

                entity.resetFallDistance();
                entity.unRide();
                entity.moveTo(vec3);
                entity.hasImpulse = true;
                entity.swing(InteractionHand.MAIN_HAND, true);
                level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }

    }

    public void eyeOfTheMoon(ManasSkillInstance instance, LivingEntity entity) {
        Level level = entity.getLevel();

        if (!entity.hasEffect(TensuraMobEffects.SHADOW_STEP.get())) {
            if (SkillHelper.outOfMagicule(entity, instance)) {
                return;
            }

            this.addMasteryPoint(instance, entity);

            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

            entity.addEffect(new MobEffectInstance(TensuraMobEffects.SHADOW_STEP.get(), 6000, 0,
                    false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_CONCEALMENT.get(), 6000,
                    3, false, false, false));
        } else {
            entity.removeEffect(TensuraMobEffects.SHADOW_STEP.get());
            entity.removeEffect(TensuraMobEffects.PRESENCE_CONCEALMENT.get());
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.5F);
        }
    }


}
