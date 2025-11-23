package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class OmniscientEyeSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("0147c153-32a2-4524-8ba3-ba4c2f449d7c");
    public OmniscientEyeSkill() {
        super(SkillType.UNIQUE);
    }


    @Nullable
    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/omniscienteye.png");
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 75000.0;
    }

    public double learningCost() {
        return 10000.0;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
    ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public int getMaxMastery() {
        return 1500;
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, (ManasSkill)ExtraSkills.SAGE.get());
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)entity;
                TensuraAdvancementsHelper.grant(player, TensuraAdvancementsHelper.Advancements.MASTER_SMITH);
            }
        }
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingHurtEvent event) {
        if (event.isCanceled())
            return;
        LivingEntity entity = event.getEntity();
        if (!instance.isToggled())
            return;

        DamageSource damageSource = event.getSource();
        // Ignore magic, explosion, or unblockable sources
        if (damageSource.isMagic() || damageSource.isExplosion())
            return;

        // Ignore indirect sources (e.g., arrows)
        if (damageSource.getEntity() == null || damageSource.getEntity() != damageSource.getDirectEntity())
            return;

        // If entity is currently performing an action (e.g., mid-attack or moving fast)
        if (entity.getAttributes().getValue(Attributes.MOVEMENT_SPEED) > 0.25F)
            return;

        // Play dodge sound
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2.0F, 1.0F);

        // Cancel the attack (successful dodge)
        event.setCanceled(true);

        // But if the skill effect allows negating dodge, re-allow hit
        if (SkillUtils.canNegateDodge(entity, damageSource))
            event.setCanceled(false);
    }

    public void onProjectileHit(ManasSkillInstance instance, LivingEntity entity, ProjectileImpactEvent event) {
        if (!instance.isToggled())
            return;
        if (SkillUtils.isProjectileAlwaysHit(event.getProjectile()))
            return;

        // If entity is in a fast state (not vulnerable)
        if (entity.getAttributes().getValue(Attributes.MOVEMENT_SPEED) > 0.25F)
            return;

        // Play dodge sound
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2.0F, 1.0F);

        // Cancel projectile hit
        event.setCanceled(true);
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        if (instance.isToggled())
            return;

        DamageSource source = e.getSource();
        if (source.getEntity() != attacker)
            return;
        if (!DamageSourceHelper.isPhysicalAttack(source))
            return;
        if (attacker.getAttributes().getValue(Attributes.MOVEMENT_SPEED) <= 0.75D)
            return;

        LivingEntity target = e.getEntity();

        // If the target has something that prevents crits, skip
        if (SkillUtils.canNegateCritChance(target))
            return;

        // Apply critical hit multiplier
        double critMultiplier = attacker.getAttributeValue(ManasCoreAttributes.CRIT_MULTIPLIER.get());
        e.setAmount((float) (e.getAmount() * critMultiplier));

        // Play critical hit sound
        target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1.0F, 1.0F);

        // Send critical hit animation packet
        Level level = attacker.level;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getServer().getPlayerList()
                    .broadcastAll(new ClientboundAnimatePacket(target, 4));
        }
    }

    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("tensura.skill.mode.great_sage.analytical_appraisal");
                break;
            case 2:
                var10000 = Component.translatable("tensura.skill.mode.great_sage.analysis");
                break;
            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
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
                            int level = this.isMastered(instance, entity) ? 128 : 32;
                            if (cap.getAnalysisLevel() != level) {
                                cap.setAnalysisLevel(level);
                                cap.setAnalysisDistance(this.isMastered(instance, entity) ? 85 : 75);
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
        } else {
            LivingEntity target = SkillHelper.getTargetingEntity(entity, 100.0, false);
            if (target != null && target.isAlive()) {
                label58: {
                    if (target instanceof Player) {
                        Player player = (Player)target;
                        if (player.getAbilities().invulnerable) {
                            break label58;
                        }
                    }
                    entity.swing(InteractionHand.MAIN_HAND, true);
                    ServerLevel level = (ServerLevel)entity.getLevel();
                    int chance = 75;
                    boolean failed = true;
                    if (entity.getRandom().nextInt(100) <= chance) {
                        List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canCopy).toList();
                        if (!collection.isEmpty()) {
                            this.addMasteryPoint(instance, entity);
                            ManasSkill skill = ((ManasSkillInstance)collection.get(target.getRandom().nextInt(collection.size()))).getSkill();
                            SkillPlunderEvent event = new SkillPlunderEvent(target, entity, false, skill);
                            if (!MinecraftForge.EVENT_BUS.post(event) && SkillUtils.learnSkill(entity, event.getSkill(), instance.getRemoveTime())) {
                                instance.setCoolDown(10);
                                failed = false;
                                if (entity instanceof Player) {
                                    Player player = (Player)entity;
                                    player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{event.getSkill().getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                                }
                                level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        }
                    }

                    if (failed && entity instanceof Player) {
                        Player player = (Player)entity;
                        player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                        instance.setCoolDown(5);
                    }
                }
            }

        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.isToggled()) {
            this.gainMastery(instance, entity);
        }
        return;
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }

        tag.putInt("activatedTimes", time + 1);
    }

    public boolean canCopy(ManasSkillInstance instance) {
        if (!instance.isTemporarySkill() && instance.getMastery() >= 0) {
            ManasSkill var3 = instance.getSkill();
            if (!(var3 instanceof Skill)) {
                return false;
            } else {
                Skill skill = (Skill)var3;
                return skill.getType().equals(SkillType.COMMON) ||
                        skill.getType().equals(SkillType.EXTRA) ||
                        skill.getType().equals(SkillType.INTRINSIC) ||
                        skill.getType().equals(SkillType.RESISTANCE);
            }
        } else {
            return false;
        }
    }
}
