package com.github.mythos.mythos.ability.skill.unique;


import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public class EltnamSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("e15c70d7-56a3-4ee9-add5-9d42bbd3edea");

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/eltnam.png");
    }

    public EltnamSkill() {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 130000.0;
    }

    public double learningCost() {
        return 10000.0;
    }

    @Override
    public int getMaxMastery() {
        return 2000;
    }

    private Player target;

    private double baseErrorRate = 1000.0D;

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        TensuraEPCapability.getFrom(entity).ifPresent(cap -> cap.setChaos(true));
        TensuraEPCapability.sync(entity);
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.isToggled()) {
            this.gainMastery(instance, entity);
        }
        return;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        entity.addEffect(new MobEffectInstance(MythosMobEffects.APOSTLE_REGENERATION.get(), 1200, 1, false, false, false));
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        MobEffectInstance effectInstance = entity.getEffect(MythosMobEffects.APOSTLE_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 1) {
            entity.removeEffect(MythosMobEffects.APOSTLE_REGENERATION.get());
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.CHANT_ANNULMENT.get());
        }
    }

    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.eltnam.Scry_proficiency: Analysis");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.eltnam.Scry_proficiency: Divination");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.eltnam.Synthetic_blood_formula");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:
                if (!SkillHelper.outOfMagicule(entity, instance)) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        TensuraSkillCapability.getFrom(player).ifPresent(cap -> {
                            if (player.isCrouching()) {
                                int mode = cap.getAnalysisMode();
                                switch (mode) {
                                    case 1:
                                        cap.setAnalysisMode(2);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.block")
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                    case 2:
                                        cap.setAnalysisMode(0);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.both")
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                    default:
                                        cap.setAnalysisMode(1);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.entity")
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                }
                                player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                TensuraSkillCapability.sync(player);
                            } else {
                                int level = this.isMastered(instance, entity) ? 18 : 8;
                                if (cap.getAnalysisLevel() != level) {
                                    cap.setAnalysisLevel(level);
                                    cap.setAnalysisDistance(this.isMastered(instance, entity) ? 30 : 20);
                                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                } else {
                                    cap.setAnalysisLevel(0);
                                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                                TensuraSkillCapability.sync(player);
                            }
                        });
                    }
                }
                break;

            case 2:
                // call scrying logic when in mode 2 and player pressed
                if (entity instanceof Player) {
                    processScrying(instance, (Player) entity);
                }
                break;

            case 3:
                boolean success;
                int duration;
                LivingEntity target;
                LivingEntity living = SkillHelper.getTargetingEntity(entity, 3.0D, false);

                // Remove effects like Severance, poison, or other harmful debuffs
                success = (TensuraEffectsCapability.getSeverance(entity) > 0.0D);
                TensuraEffectsCapability.getFrom(entity).ifPresent(cap -> cap.setSeveranceAmount(0.0D));

                Predicate predicate = effect -> (effect == MobEffectCategory.HARMFUL); success = (success || SkillHelper.removePredicateEffect(entity, predicate, magiculeCost(entity, instance)));

                // Heal missing HP and consume some magicule
                int cost = instance.isMastered(entity)
                        ? (int) (TensuraEPCapability.getEP(entity) * 0.025D)
                        : (int) (TensuraEPCapability.getEP(entity) * 0.075D);
                float healAmount = entity.getMaxHealth() - entity.getHealth();
                SkillHelper.outOfMagiculeStillConsume(entity, cost);

                entity.heal(healAmount);
                success |= healAmount > 0.0F;

                if (success) {
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.HEART, 2.0D);
                    addMasteryPoint(instance, entity);
                    entity.swing(InteractionHand.MAIN_HAND, true);
                }

                instance.setCoolDown(5);
                break;

            default:
                // no-op for other modes
                break;
        }
    }

    private void processScrying(ManasSkillInstance instance, Player player) {
        ItemStack itemInHand = player.getMainHandItem();

        if (!itemInHand.isEmpty()) {
            String itemName = itemInHand.getHoverName()
                    .getString()
                    .replaceAll("[\\[\\]]", "")
                    .trim()
                    .toLowerCase();

            Player matchedPlayer = getPlayerByName(itemName, player);

            if (matchedPlayer != null) {
                this.target = matchedPlayer;
                double currentErrorRate = this.baseErrorRate;
                boolean scryable = true;

                // EP check
                if (TensuraEPCapability.getCurrentEP(this.target) <= 200000.0D) {
                    scryable = false;
                }

                if (scryable) {
                    if (instance.isMastered(player)) {
                        currentErrorRate /= 2.0D;
                    }

                    ServerLevel serverLevel = (ServerLevel) this.target.getLevel();

                    // Random offset (error)
                    double offsetX = serverLevel.random.nextDouble() * currentErrorRate - currentErrorRate / 2.0D;
                    double offsetY = serverLevel.random.nextDouble() * currentErrorRate - currentErrorRate / 2.0D;
                    double offsetZ = serverLevel.random.nextDouble() * currentErrorRate - currentErrorRate / 2.0D;

                    // Compute “revealed” coordinates
                    double revealedX = this.target.getX() + offsetX;
                    double revealedY = this.target.getY() + offsetY;
                    double revealedZ = this.target.getZ() + offsetZ;

                    if (instance.isMastered(player)) {
                        player.sendSystemMessage(Component.literal(String.format(
                                "Target found at: X=%.2f, Y=%.2f, Z=%.2f in %s",
                                revealedX,
                                revealedY,
                                revealedZ,
                                this.target.getLevel().dimension().location().toString()
                        )));
                    } else {
                        player.sendSystemMessage(Component.literal(String.format(
                                "Target is approximately at: X=%.2f, Y=%.2f, Z=%.2f",
                                revealedX,
                                revealedY,
                                revealedZ
                        )));
                    }

                    addMasteryPoint(instance, player);
                    instance.setCoolDown(120);

                } else {
                    player.sendSystemMessage(Component.literal("The target is immune to scrying!"));
                }
            } else {
                player.sendSystemMessage(Component.literal("No player found with that name!"));
            }
        } else {
            player.sendSystemMessage(Component.literal(
                    "You must hold an item with the player's name in your main hand!"
            ));
        }
    }

    private Player getPlayerByName(String itemName, Player player) {
        ServerLevel world = (ServerLevel) player.getLevel();

        for (ServerPlayer onlinePlayer : world.getServer().getPlayerList().getPlayers()) {
            if (onlinePlayer.getGameProfile().getName().equalsIgnoreCase(itemName)) {
                return onlinePlayer;
            }
        }

        return null;
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }
        tag.putInt("activatedTimes", time + 1);
    }
}
