package com.github.mythos.mythos.ability.mythos.skill.ultimate.god;

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
import com.github.manasmods.tensura.ability.skill.resist.AbnormalConditionNullification;
import com.github.manasmods.tensura.ability.skill.resist.SpiritualAttackNullification;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.human.CloneEntity;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.blocks.TensuraBlocks;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
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
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.mythos.mythos.ability.mythos.skill.ultimate.prince.HaliSkill.applyDrain;
import static net.minecraft.ChatFormatting.*;

public class Khonsu extends Skill {
    public Khonsu(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public static final UUID TSUKUYOMI = UUID.fromString("ff1ba7e1-9b9a-4580-8f6f-c5db93f93651");
    protected static final UUID ACCELERATION = UUID.fromString("8f08f72d-014b-4b4b-8560-987a0375959d");
    private final Set<String> usedDeathTypes = new HashSet<>();

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public double getObtainingEpCost() {
        return 20000000.0;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Assassin God, Khonsu");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("In the heart of the deepest eclipse, there is a silence that swallows the soul before the blade ever touches the throat. It is the moon’s cold promise: that which is hidden by the night belongs to the night, and that which the night claims, even the gods cannot recall.");
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
        return SkillUtils.isSkillMastered(player, Skills.HALI.get());
    }



    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        SkillUtils.learnSkill(entity, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get());

        if (entity instanceof Player player) {
            triggerHaliLearningSequence(player);
        }

        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = Skills.HALI.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
        }
    }

    private void triggerHaliLearningSequence(Player player) {
        player.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 0.5f);

        player.sendSystemMessage(Component.literal("« Notice »").withStyle(WHITE, (BOLD)));

        player.sendSystemMessage(Component.literal("Initiating Final Synthesis: - Base Logic: [Tsukuyomi] ... Deleted.\n").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("Refinement Catalyst:  ").withStyle(WHITE).append("[Hali]").withStyle(YELLOW).append("... Integrated.\n ").withStyle(WHITE));

        player.sendSystemMessage(Component.literal("Sacrifice: 20,000,000 Magicules ... Accepted.:\n.").withStyle(WHITE, BOLD));

        player.sendSystemMessage(Component.literal("Confirmed: The laws of the Lunar Cycle have been rewritten. You have moved beyond the reflection of the moon and into its shadow.").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("Skill [Assassin God, Khonsu] has been manifested.\n").withStyle(DARK_GRAY, ITALIC));

        player.sendSystemMessage(Component.literal("System Overwrite:\n").withStyle(GRAY));

        player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 0.5f);

        player.sendSystemMessage(Component.literal("« Notification: \"The spirit hath died once to become eternal. The hunt begins in the dark that never breaks.\" »\n").withStyle(GRAY));

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

        player.sendSystemMessage(Component.literal("Condition Met: 100% Synchronization with the Lunar Void.\n").withStyle(GRAY));

        player.sendSystemMessage(Component.literal("Confirmed: The limitations of the physical and spiritual planes have been dissolved.\n ").append(Component.literal("[Sunken Sun]").withStyle(WHITE, (BOLD))));

        player.sendSystemMessage(Component.literal("« Message: \"The spirit dieth not in part, but in whole. You are the hunger that outlives the light.\" »\n").withStyle(WHITE, BOLD));

        player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 0.5f);

    }

    @Override
    public int modes() {
        return 7;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) {
            return instance.getMode() == 1 ? 7 : instance.getMode() - 1;
        } else {
            return instance.getMode() == 7 ? 1 : instance.getMode() + 1;
        }
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return switch (instance.getMode()) {
            case 1 -> 125000.0;
            case 2 -> 50000.0;
            case 3 -> 0.0;
            case 4 -> 75000.0;
            case 5 -> 400000.0;
            case 6 -> 1000000;
            case 7 -> 500000;
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
            case 3 -> Component.literal("Inevitable Action");
            case 4 -> Component.translatable("trmysticism.skill.mode.tsukuyomi.parallel_existence");
            case 5 -> isShifting ? Component.literal("Sunrise Sun") : Component.literal("Sunset Moon");
            case 6 -> Component.literal("Eternal Midnight");
            case 7 -> Component.literal("Dark Desire");
            default -> Component.empty();
        };
    }

    private boolean safeCheckShifting() {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.isShiftKeyDown();
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        if (!instance.isToggled()) return list;
        list.addAll(AbnormalConditionNullification.getAbnormalEffects());
        list.addAll(SpiritualAttackNullification.SPIRITUAL_NULL);
        return list;
    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        if (DamageSourceHelper.isFireDamage(event.getSource())) {
            event.setAmount((float) (event.getAmount() * 0.01));
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();

        if (target == null) return;

        if (DamageSourceHelper.isDarkDamage(source)) {
            event.setAmount(event.getAmount() * 8.0F);
        }

        if (DamageSourceHelper.isPhysicalAttack(source)) {
            CompoundTag tag = instance.getOrCreateTag();
            int bonusStacks = tag.getInt("darknessStacks");
            float totalPercent = 0.15F + (bonusStacks * 0.01F);

            float targetMaxSHP = (float) target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
            float damageValue = targetMaxSHP * totalPercent;

            DamageSourceHelper.directSpiritualHurt(target, attacker, damageValue);

            if (attacker.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SQUID_INK, target.getX(), target.getY() + 1, target.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
            }
        }

        if (this.isInSlot(attacker) && instance.getMode() == 2 && !instance.onCoolDown()) {
            if (source.getEntity() == attacker && DamageSourceHelper.isPhysicalAttack(source) && !SkillHelper.outOfMagicule(attacker, instance)) {

                boolean isCrouching = attacker.isShiftKeyDown();
                boolean isMastered = instance.isMastered(attacker);
                float targetMaxSHP = (float) target.getAttributeValue(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());

                if (isCrouching) {
                    if (isMastered || !SkillUtils.hasSkill(target, ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                        EntityDamageSource khonsuPhysical = new EntityDamageSource("khonsu_physical_kill", attacker);
                        khonsuPhysical.bypassArmor();
                        target.hurt(khonsuPhysical, Float.MAX_VALUE);
                    } else {
                        DamageSourceHelper.directSpiritualHurt(target, attacker, 12000.0F + targetMaxSHP * 0.8F);
                    }
                } else {
                    if (SkillUtils.hasSkill(target, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                        DamageSourceHelper.directSpiritualHurt(target, attacker, 7000.0F + targetMaxSHP * 0.4F);
                    } else if (SkillUtils.hasSkill(target, ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get()) && !isMastered) {
                        DamageSourceHelper.directSpiritualHurt(target, attacker, 12000.0F + targetMaxSHP * 0.8F);
                    } else {
                        DamageSourceHelper.directSpiritualHurt(target, attacker, Float.MAX_VALUE);
                    }
                }

                instance.setCoolDown(isMastered ? 5 : 10);

                if (!target.isAlive()) {
                    event.setCanceled(true);
                } else {
                    applyKhonsuFailureDebuffs(attacker);
                }
            }
        }
    }

    private void applyKhonsuFailureDebuffs(LivingEntity attacker) {
        AABB area = attacker.getBoundingBox().inflate(50.0);
        List<LivingEntity> nearbyEntities = attacker.level.getEntitiesOfClass(LivingEntity.class, area, e -> e != attacker);

        for (LivingEntity nearbyTarget : nearbyEntities) {
            nearbyTarget.addEffect(new MobEffectInstance(TensuraMobEffects.ANTI_SKILL.get(), 500, 1, false, false, false));
            nearbyTarget.addEffect(new MobEffectInstance(TensuraMobEffects.MAGIC_INTERFERENCE.get(), 500, 1, false, false, false));
            nearbyTarget.addEffect(new MobEffectInstance(TensuraMobEffects.MAGIC_DARKNESS.get(), 500, 1, false, false, false));
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        boolean isClient = entity.level.isClientSide;

        if (instance.isToggled()) {
            if (entity.tickCount % 10 == 0) {
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 3000, 4, false, false, false));
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.HEAT_SENSE.get(), 3000, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(TensuraMobEffects.AUDITORY_SENSE.get(), 3000, 1, false, false, false));
            }
        }

        if (isClient) return;

        CompoundTag tag = instance.getOrCreateTag();
        long currentTime = entity.level.getGameTime();
        boolean isDirty = false;

        int timer = tag.getInt("darknessTimer") + 1;
        int requiredTicks = instance.isMastered(entity) ? 600 : 1200;

        if (timer >= requiredTicks) {
            int currentStacks = tag.getInt("darknessStacks") + 1;
            tag.putInt("darknessStacks", currentStacks);
            tag.putInt("darknessTimer", 0);

            if (entity instanceof Player player) {
                player.displayClientMessage(Component.literal("§8Primordial Darkness deepens... (§f+" + currentStacks + "%§8)"), true);
            }
            isDirty = true;
        } else {
            tag.putInt("darknessTimer", timer);
        }

        long midnightEnd = tag.getLong("MidnightEndTime");
        if (currentTime < midnightEnd) {
            if (entity.level instanceof ServerLevel serverLevel) {
                if (currentTime % 20 == 0) {
                    serverLevel.setDayTime(18000);
                    if (serverLevel.isRaining()) serverLevel.setRainLevel(0);

                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL, 1.0);
                }
            }
        }

        if (tag.getBoolean("DarkDesireListening")) {
            long startTime = tag.getLong("DarkDesireStartTime");

            if (currentTime > startTime + 600) {
                tag.putBoolean("DarkDesireListening", false);
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("§7Your desire fades into the void... (Timeout)"), true);
                }
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                isDirty = true;
            } else {
                entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);

                if (currentTime % 10 == 0 && entity.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SQUID_INK, entity.getX(), entity.getY() + 1, entity.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
                }
            }
        }

        if (isDirty) instance.markDirty();
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

            if (entity.hasEffect(MythosMobEffects.SUNSET.get()) && isShifting) entity.removeEffect(MythosMobEffects.SUNSET.get());
            if (entity.hasEffect(MythosMobEffects.SUNRISE.get()) && !isShifting) entity.removeEffect(MythosMobEffects.SUNRISE.get());

            entity.addEffect(new MobEffectInstance(domainEffect, 40, 0, false, false, true));

            if (heldTicks % 20 == 0) {

                AABB area = entity.getBoundingBox().inflate(12.5);
                List<LivingEntity> targets = entity.level.getEntitiesOfClass(LivingEntity.class, area, e -> e != entity);

                for (LivingEntity target : targets) {
                    boolean isMoving = target.getDeltaMovement().horizontalDistanceSqr() > 0.005;

                    if (!isShifting && isMoving) {
                        applyDrain(entity, target, 0.25, true);
                    }
                    else if (isShifting && !isMoving) {
                        applyDrain(entity, target, 0.25, false);
                    }
                }
            }
            return true;
        }

        if (instance.getMode() != 4) {
            return false;
        } else {
            CompoundTag tag = instance.getTag();
            int clones = tag != null ? tag.getInt("clones") : 20;
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.displayClientMessage(Component.translatable("tensura.skill.output_number", new Object[]{clones}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
            }

            return true;
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
                int cloneCount = (tag != null) ? tag.getInt("clones") : 20;

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

            if (newScale > 20) {
                newScale = 1;
            } else if (newScale < 1) {
                newScale = 20;
            }

            if (tag.getInt("clones") != newScale) {
                tag.putInt("clones", newScale);
                instance.markDirty();
            }
        }
    }

    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        long currentTime = player.level.getGameTime();
        long endTime = instance.getOrCreateTag().getLong("MidnightEndTime");

        if (currentTime < endTime) {
            event.setCanceled(true);

            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);

            player.level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0F, 2.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.LARGE_SMOKE, 2.0);

            player.displayClientMessage(Component.literal("§4ETERNAL REVIVAL"), true);
            return;
        }

        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();
            AttributeInstance recon = entity.getAttribute(TensuraAttributeRegistry.SIZE.get());
            if (recon != null && recon.getModifier(TSUKUYOMI) != null) {
                recon.removePermanentModifier(TSUKUYOMI);
            }
        }

        String damageType = event.getSource().getMsgId();
        if (!usedDeathTypes.contains(damageType)) {

            event.setCanceled(true);

            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);

            player.level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.5F);

            usedDeathTypes.add(damageType);

            player.displayClientMessage(Component.literal("§eSelf Necromancing: §0Revived against " + damageType), true);
        }


        if (!event.isCanceled()) {
            return;
        } else {
            CompoundTag tag = instance.getOrCreateTag();
            tag.putInt("darknessStacks", 0);
            tag.putInt("darknessTimer", 0);
            instance.markDirty();
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
                    break;
                case 6:
                    if (instance.getMode() == 6 && instance.isMastered(entity)) {
                        if (!SkillHelper.outOfMagicule(entity, instance)) {
                            long endTime = entity.level.getGameTime() + 3600;
                            instance.getOrCreateTag().putLong("MidnightEndTime", endTime);

                            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.0F, 0.5F);

                            if (entity instanceof Player player) {
                                player.displayClientMessage(Component.literal("§0The cycle of death has been severed. Eternal Midnight begins.").withStyle(ChatFormatting.BOLD), true);
                            }

                            if (entity.level instanceof ServerLevel serverLevel) {
                                String activatorName = entity.getDisplayName().getString();

                                MutableComponent message = Component.literal("§8[§0!§8] §f" + activatorName + " §7has invoked §0Eternal Midnight§7. §8The sun shall rise no more.").withStyle(Style.EMPTY.withBold(true).withItalic(true));

                                serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
                                    player.displayClientMessage(message, true);

                                    player.playNotifySound(SoundEvents.END_PORTAL_SPAWN, SoundSource.AMBIENT, 1.0F, 0.5F);
                                });
                            }

                            instance.setCoolDown(2400);
                            instance.markDirty();
                        }
                    }
                case 7:
                    if (instance.getMode() == 7 && instance.isMastered(entity)) {
                        if (!SkillHelper.outOfMagicule(entity, instance)) {
                            CompoundTag tag = instance.getOrCreateTag();
                            tag.putBoolean("DarkDesireListening", true);
                            tag.putLong("DarkDesireStartTime", entity.level.getGameTime());

                            entity.setDeltaMovement(0, 0, 0);
                            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.5F);

                            if (entity instanceof Player player) {
                                player.displayClientMessage(Component.literal("§8[§4!§8] §0Speak the name of the one you desire to cease... (30s)").withStyle(ChatFormatting.ITALIC), true);
                            }
                            instance.markDirty();
                        }
                    }
            }
        }
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event, ManasSkillInstance instance) {
        ServerPlayer user = event.getPlayer();

        if (instance != null && instance.getOrCreateTag().getBoolean("DarkDesireListening")) {
            event.setCanceled(true);

            String targetName = event.getRawText().trim();
            CompoundTag tag = instance.getOrCreateTag();

            ServerPlayer victim = null;
            for (ServerPlayer p : Objects.requireNonNull(user.getServer()).getPlayerList().getPlayers()) {
                if (p.getName().getString().equalsIgnoreCase(targetName)) {
                    victim = p;
                    break;
                }
            }
            if (victim != null) {
                double userMP = TensuraEPCapability.getEP(user);
                double victimMP = TensuraEPCapability.getEP(victim);

                if (victimMP < (userMP * 0.8)) {
                    victim.die(DamageSource.OUT_OF_WORLD);

                    victim.level.playSound(null, victim.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 0.1F);

                    instance.setMastery(instance.getSkill().getMaxMastery() / 2);

                    user.displayClientMessage(Component.literal("§0Desire fulfilled. §8" + targetName + " has been erased."), true);
                } else {
                    user.displayClientMessage(Component.literal("§cTheir soul is too heavy for your desire to claim."), true);
                }
            } else {
                user.displayClientMessage(Component.literal("§7The void finds no one by that name."), true);
            }

            tag.putBoolean("DarkDesireListening", false);
            instance.setCoolDown(1200);
            instance.markDirty();
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
        if ((entity.isOnGround() || entity.isInWaterOrBubble() || entity.isFallFlying()) && !SkillHelper.outOfAura(entity, instance)) {
            this.addMasteryPoint(instance, entity);
            Level level = entity.getLevel();
            int range = instance.isMastered(entity) ? 200 : 100;
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
                    TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SQUID_INK, 3.0);
                    TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SQUID_INK, 2.0);
                    AABB aabb = (new AABB(new BlockPos(particlePos))).inflate(Math.max(entity.getAttributeValue(ForgeMod.ATTACK_RANGE.get()), 2.0));
                    List<LivingEntity> livingEntityList = level.getEntitiesOfClass(LivingEntity.class, aabb, (targetx) -> {
                        return !targetx.is(entity) && !targetx.isAlliedTo(entity);
                    });
                    if (!livingEntityList.isEmpty()) {
                        float bonus = instance.isMastered(entity) ? 5000F : 3000.0F;
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
                                        EntityDamageSource khonsuPhysical = new EntityDamageSource("khonsu_physical_kill", entity);
                                        khonsuPhysical.bypassArmor();
                                        target.hurt(khonsuPhysical, Float.MAX_VALUE);
                                    } else {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                                    }
                                } else if (SkillUtils.hasSkill(target, (ManasSkill) ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                                    if (entity.getRandom().nextFloat() > 0.5F) {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, Float.MAX_VALUE);
                                        EntityDamageSource khonsuPhysical = new EntityDamageSource("khonsu_physical_kill", entity);
                                        khonsuPhysical.bypassArmor();
                                        target.hurt(khonsuPhysical, Float.MAX_VALUE);
                                    } else {
                                        DamageSourceHelper.directSpiritualHurt(target, entity, targetMaxSHP * 0.5F);
                                    }
                                } else {
                                    DamageSourceHelper.directSpiritualHurt(target, entity, Float.MAX_VALUE);
                                    EntityDamageSource khonsuPhysical = new EntityDamageSource("khonsu_physical_kill", entity);
                                    khonsuPhysical.bypassArmor();
                                    target.hurt(khonsuPhysical, Float.MAX_VALUE);
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
        if (!entity.hasEffect((MobEffect) TensuraMobEffects.SHADOW_STEP.get())) {
            if (SkillHelper.outOfMagicule(entity, instance)) {
                return;
            }

            this.addMasteryPoint(instance, entity);
            entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.SHADOW_STEP.get(), 6000, 0, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) TensuraMobEffects.PRESENCE_CONCEALMENT.get(), 6000, 5, false, false, false));
            entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.KHONSU.get(), 6000, 0, false, false, false));
        } else {
            entity.removeEffect((MobEffect) TensuraMobEffects.SHADOW_STEP.get());
            entity.removeEffect(TensuraMobEffects.PRESENCE_CONCEALMENT.get());
            entity.removeEffect(MythosMobEffects.KHONSU.get());
            entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.5F);
        }

    }


}
