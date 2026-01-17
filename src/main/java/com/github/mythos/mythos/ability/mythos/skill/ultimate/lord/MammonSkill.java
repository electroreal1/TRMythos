package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.ISpatialStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.template.TensuraHorseEntity;
import com.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class MammonSkill extends Skill implements ISpatialStorage {
    public enum CopyResult {
        NONE,
        TEMPORARY,
        PERMANENT
    }
    protected static final UUID MAMMON_MODIFIER_ID = UUID.fromString("3d5b8b72-6c77-4b8b-8a70-8d8f9c6ad452");
    public MammonSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/mammon.png");
    }

    public double getObtainingEpCost() {
        return 4500000.0;
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, UniqueSkills.GREED.get());
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        TensuraEPCapability.getFrom(entity).ifPresent(cap -> {
            if (!cap.isChaos() || !cap.isMajin()) {
                cap.setMajin(true);
            }
        });
        TensuraEPCapability.sync(entity);

        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = UniqueSkills.GREED.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
        }
    }

    @Override
    public int getMasteryOnEPAcquirement(Player entity) {
        return 5;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int modes() {
        return 4;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("§6Mammon, Lord of Greed");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("§6Mammon, Lord of Greed §3is the ultimate evolved Skill of §6Greed§3. §3This powerful skill is capable of stealing even the §aLife §3from its victims. This is truly the manifestation of §6Greed");
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1 -> var10000 = Component.literal("§6Steal §4Life");
            case 2 -> var10000 = Component.literal("§0Skill §6Steal");
            case 3 -> var10000 = Component.literal("§6Spiritual Domination");
            case 4 -> var10000 = Component.literal("§6Death §0Wish");
            case 5 -> var10000 = Component.literal("§6Mammon Flare");
            default -> var10000 = Component.empty();
        }

        return var10000;
    }

    public void onTick(Player player, ManasSkillInstance instance, CompoundTag tag) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);


        List<ManasSkillInstance> skills = new ArrayList<>(storage.getLearnedSkills());

        for (ManasSkillInstance tempInstance : skills) {
            if (tempInstance.isTemporarySkill()) {
                storage.forgetSkill(tempInstance.getSkill());

                player.displayClientMessage(
                        Component.translatable("tensura.skill.temp_lost", tempInstance.getSkill().getName())
                                .withStyle(style -> style.withColor(ChatFormatting.GRAY)),
                        false
                );
            }
        }
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return instance.getMode() == 3 ? 1000.0 : 0.0;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            Level level = entity.level;
            switch (instance.getMode()) {
                case 1 -> this.life(instance, entity, level);
                case 2 -> this.steal(instance, entity);
                case 3 -> this.desire(instance, entity, level);
            }
        }
    }

    public void onHeld(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            CompoundTag tag = instance.getOrCreateTag();
            int heldTicks = tag.getInt("heldTicks");
            if (instance.getMode() == 4) {
                this.death(instance, entity, heldTicks);
            }
        }
    }

    public boolean death(ManasSkillInstance instance, LivingEntity user, int heldTicks) {
        CompoundTag tag = instance.getOrCreateTag();
        int secondsHeld = tag.getInt("heldSeconds");

        LivingEntity target = SkillHelper.getTargetingEntity(LivingEntity.class, user, 5.0, 0.5, true, true);

        if (target == null || (tag.hasUUID("target") && !Objects.equals(tag.getUUID("target"), target.getUUID()))) {
            if (heldTicks % 20 == 0) {
                tag.putInt("heldSeconds", Math.max(0, secondsHeld - 1));

                if (tag.getInt("heldSeconds") <= 0) {
                    tag.remove("target");
                }

                if (user instanceof Player player) {
                    player.displayClientMessage(Component.translatable("tensura.skill.time_held", secondsHeld)
                            .withStyle(style -> style.withColor(ChatFormatting.GOLD)), true);
                }
            }
        } else {
            if (target instanceof Player targetPlayer) {
                if (targetPlayer.getAbilities().instabuild) {
                    return false;
                }
            }

            if (heldTicks % 20 == 0) {
                tag.putInt("heldSeconds", secondsHeld + 1);
                tag.putUUID("target", target.getUUID());
            }

            if (user instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.translatable("tensura.skill.time_held.max", secondsHeld, 10)
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)), true);

                TensuraParticleHelper.addServerParticlesAroundSelfToOnePlayer(serverPlayer, target, ParticleTypes.REVERSE_PORTAL, 1.0);
            }

            if (secondsHeld >= 10) {
                tag.putInt("heldSeconds", 0);
                tag.remove("target");

                if (SkillHelper.outOfMagicule(user, instance)) {
                    return false;
                }

                this.addMasteryPoint(instance, user);

                float executionDamage = target.getMaxHealth() * 10.0F;
                DamageSource source = this.sourceWithMP(TensuraDamageSources.deathWish(user), user, instance);

                if (target.hurt(source, executionDamage)) {
                    TensuraParticleHelper.addServerParticlesAroundSelf(target, TensuraParticles.BLACK_LIGHTNING_EFFECT.get());
                }
            }
        }

        return false;
    }

    public void life(ManasSkillInstance instance, LivingEntity user, Level level) {
        if (!SkillHelper.outOfMagicule(user, instance)) {

            LivingEntity target = SkillHelper.getTargetingEntity(user, 8.0, false);

            if (target != null) {
                if (target instanceof Player targetPlayer) {
                    if (targetPlayer.getAbilities().instabuild) {
                        return;
                    }
                }

                SkillStorage userStorage = SkillAPI.getSkillsFrom(user);
                boolean hasAntiSkill = userStorage.getLearnedSkills().stream()
                        .map(ManasSkillInstance::getSkill)
                        .filter(skill -> skill instanceof Skill)
                        .map(ManasSkill::getRegistryName)
                        .filter(Objects::nonNull)
                        .map(ResourceLocation::getPath)
                        .anyMatch(path -> path.equals("anti_skill"));

                if (hasAntiSkill) {
                    float damageAmount = 20000.0F;

                    target.hurt(DamageSource.OUT_OF_WORLD, damageAmount);
                    user.heal(damageAmount);

                    if (!target.isDeadOrDying() && target.getHealth() > 0.1F) {
                        target.setHealth(-100.0F);

                        Component deathMessage = Component.translatable("death.attack.life_skill", target.getDisplayName(), user.getDisplayName());
                        level.players().forEach(p -> p.sendSystemMessage(deathMessage));
                    }

                    instance.setCoolDown(15);

                    level.playSound(null, user.getX(), user.getY(), user.getZ(),
                            SoundEvents.IRON_GOLEM_DEATH, SoundSource.PLAYERS, 1.0F, 0.7F);

                    TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticles.BLACK_LIGHTNING_EFFECT.get(), 1.5);
                }
            }
        }
    }

    public void desire(ManasSkillInstance instance, LivingEntity user, Level level) {
        float radius = 30.0F;
        AABB area = user.getBoundingBox().inflate(radius);

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, (living) -> !living.isAlliedTo(user) && living.isAlive());

        if (!targets.isEmpty()) {
            double totalCost = this.magiculeCost(user, instance) * (double)targets.size();

            if (!SkillHelper.outOfMagicule(user, totalCost)) {
                this.addMasteryPoint(instance, user);

                level.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 1.0F);

                for (LivingEntity target : targets) {
                    if (CharmSkill.canMindControl(target, level) &&
                            !target.hasEffect((MobEffect)TensuraMobEffects.RAMPAGE.get()) &&
                            !RaceHelper.isSpiritualLifeForm(target)) {

                        int duration = SkillUtils.isSkillToggled(target, (ManasSkill) ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get()) ? 30000 : 60000;

                        SkillHelper.checkThenAddEffectSource(target, user, TensuraMobEffects.MIND_CONTROL.get(), duration, 0);

                        if (target.hasEffect(TensuraMobEffects.MIND_CONTROL.get())) {
                            TensuraEPCapability.getFrom(target).ifPresent((cap) -> {
                                if (!Objects.equals(cap.getTemporaryOwner(), user.getUUID())) {
                                    cap.setTemporaryOwner(user.getUUID());

                                    if (user instanceof Player player) {
                                        if (target instanceof TamableAnimal animal) {
                                            animal.tame(player);
                                        } else if (target instanceof TensuraHorseEntity horse) {
                                            horse.getOwner();
                                        }
                                    }

                                    TensuraEPCapability.sync(target);
                                    user.swing(InteractionHand.MAIN_HAND, true);
                                    TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART);
                                }

                                instance.setCoolDown(25);
                            });
                        }
                    }
                }
            }
        }
    }

    public void steal(ManasSkillInstance instance, LivingEntity user) {
        if (!SkillHelper.outOfMagicule(user, instance)) {

            LivingEntity target = SkillHelper.getTargetingEntity(user, 10.0, false);

            if (target != null && target.isAlive()) {

                if (target instanceof Player targetPlayer) {
                    if (targetPlayer.getAbilities().instabuild) {
                        return;
                    }
                }

                user.swing(InteractionHand.MAIN_HAND, true);

                ServerLevel level = (ServerLevel) user.level;

                CopyResult result = this.copyRandomSkill(instance, user, target);
                this.addMasteryPoint(instance, user);


                int cooldownDuration;
                switch (result) {
                    case PERMANENT:
                        cooldownDuration = 6000;
                        break;
                    case TEMPORARY:
                        cooldownDuration = 30;
                        break;
                    default:
                        cooldownDuration = 10;
                        break;
                }

                instance.setCoolDown(cooldownDuration);

                DamageSourceHelper.markHurt(target, user);

                if (user instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("tensura.ability.activation_failed").withStyle(style -> style.withColor(ChatFormatting.RED)), false);
                }

                level.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event, LivingEntity victim) {

        AttributeInstance attackAttribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackAttribute != null) {
            AttributeModifier modifier = attackAttribute.getModifier(MAMMON_MODIFIER_ID);

            if (modifier != null) {
                DamageSource source = event.getSource();

                if (source.getDirectEntity() == attacker && DamageSourceHelper.isPhysicalAttack(source) &&
                        SkillHelper.outOfMagicule(attacker, modifier.getAmount() * 30.0)) {

                    attackAttribute.removeModifier(MAMMON_MODIFIER_ID);
                }
            }
        }

        victim.addEffect(new MobEffectInstance(MythosMobEffects.MAMMON_FLARE.get(), 100, 0));

        if (TensuraSkillCapability.isSkillInSlot(attacker, UniqueSkills.ANTI_SKILL.get())) {
            victim.addEffect(new MobEffectInstance(TensuraMobEffects.ANTI_SKILL.get(), 100, 0));
        }
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            AttributeModifier modifier = attack.getModifier(MAMMON_MODIFIER_ID);
            if (modifier != null) {
                attack.removeModifier(MAMMON_MODIFIER_ID);
            }

            CompoundTag tag = instance.getOrCreateTag();
            tag.putInt("targetDesire", 0);
            tag.remove("target");
            tag.putInt("heldSeconds", 0);
        }

    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        CompoundTag tag = instance.getOrCreateTag();
        tag.putInt("targetDesire", 0);
        tag.remove("target");
        tag.putInt("heldSeconds", 0);
    }

    public CopyResult copyRandomSkill(ManasSkillInstance instance, LivingEntity user, LivingEntity target) {
        if (user instanceof Player player) {
            Level level = user.level;
            SkillStorage storage = SkillAPI.getSkillsFrom(player);

            List<ManasSkillInstance> availableSkills = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream()
                    .filter(skillInstance -> this.canCopy(player, skillInstance))
                    .toList();

            if (availableSkills.isEmpty()) {
                return CopyResult.NONE;
            } else {
                ManasSkillInstance targetSkillInstance = availableSkills.get(target.getRandom().nextInt(availableSkills.size()));
                ManasSkill targetSkill = targetSkillInstance.getSkill();

                if (SkillUtils.fullyHasSkill(player, targetSkill)) {
                    return CopyResult.NONE;
                } else {
                    TensuraSkillInstance tempInstance = new TensuraSkillInstance(targetSkill);
                    tempInstance.setMastery(25);

                    boolean isPermanentCopy = false;
                    int masteryPenalty = 0;

                    if (targetSkill instanceof Skill skill) {
                        ResourceLocation registryName = skill.getRegistryName();
                        Skill.SkillType type = skill.getType();

                        // 5. Check if the skill is in the Mammon Whitelist (Config)
                        if (registryName != null) {
                            boolean isMastered = skill.isMastered(targetSkillInstance, target);
                            double roll = Math.random();
                            double chance;

                            if (type == SkillType.UNIQUE) {
                                chance = isMastered ? 0.1 : 0.05;
                                if (roll < chance) {
                                    isPermanentCopy = true;
                                    masteryPenalty = 1000;
                                }
                            }
                        }
                    }

                    if (!isPermanentCopy) {
                        tempInstance.setRemoveTime(300);
                    }

                    if (storage.learnSkill(tempInstance)) {
                        if (isPermanentCopy) {
                            instance.setMastery(Math.max(0, instance.getMastery() - masteryPenalty));
                        }

                        SkillAPI.getSkillsFrom(target).getSkill(targetSkill).ifPresent(original -> {
                            original.setCoolDown(300);
                        });

                        MutableComponent copiedMsg = isPermanentCopy
                                ? Component.translatable("trmythos.skill.mammon.perm_copy", targetSkill.getName()).withStyle(ChatFormatting.GOLD)
                                : Component.translatable("trmythos.skill.mammon.copy", targetSkill.getName()).withStyle(ChatFormatting.LIGHT_PURPLE);

                        player.displayClientMessage(copiedMsg, false);

                        level.playSound(null, user.getX(), user.getY(), user.getZ(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, isPermanentCopy ? 2.0F : 1.5F);

                        return isPermanentCopy ? CopyResult.PERMANENT : CopyResult.TEMPORARY;
                    } else {
                        return CopyResult.NONE;
                    }
                }
            }
        } else {
            return CopyResult.NONE;
        }
    }

    private boolean canCopy(Player player, ManasSkillInstance skillInstance) {
        ManasSkill skill = skillInstance.getSkill();

        boolean hasBypass = this.hasAntiSkillOrCreator(player);

        if ((!skillInstance.isTemporarySkill() || hasBypass) && skillInstance.getMastery() >= 0) {

            if (!(skill instanceof Skill baseSkill) || skill instanceof SpiritualMagic) {
                return false;
            }

            Skill.SkillType type = baseSkill.getType();
            double roll = Math.random();


            if (type == SkillType.UNIQUE && roll < 0.8) {
                return true;
            }

            return type != SkillType.UNIQUE && type != SkillType.ULTIMATE;
        }

        return false;
    }

    public @NotNull SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance) {
        SpatialStorageContainer container = new SpatialStorageContainer(1000, 1000);
        return container;
    }

    @SubscribeEvent
    public void onSkillTick(TickEvent.PlayerTickEvent event, ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isTemporarySkill()) {
            SkillAPI.getSkillsFrom(entity).forgetSkill(instance.getSkill());

            if (entity instanceof Player player) {
                player.displayClientMessage(
                        Component.translatable("tensura.skill.temp_lost",
                                instance.getSkill().getName()).withStyle(style -> style.withColor(ChatFormatting.GRAY)), false);
            }
        }
    }

    private boolean hasAntiSkillOrCreator(Player player) {
        return player.getTags().contains("tensura:anti_skill") ||
                player.getTags().contains("tensura:creator");
    }
}
