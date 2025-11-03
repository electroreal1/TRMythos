package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.entity.human.CloneEntity;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.menu.OrunMenu;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.mythos.mythos.util.damage.MythosDamageSources.DESTROY_RECORD;

public class OrunmilaSkill extends Skill {
    public OrunmilaSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public double getObtainingEpCost() {
        return 2500000;
    }

    public double learningCost() {
        return 2500000;
    }

//    @Override
//    public ResourceLocation getSkillIcon() {
//        return new ResourceLocation("trmythos", "textures/skill/ultimate/orunmila.png");
//    }

    public boolean meetEPRequirement(@NotNull Player entity, double newEP) {
        return SkillUtils.isSkillMastered(entity, (ManasSkill) Skills.OMNISCIENT_EYE.get())
                && SkillUtils.isSkillMastered(entity, (ManasSkill) ExtraSkills.ALL_SEEING_EYE.get())
                && SkillUtils.isSkillMastered(entity, (ManasSkill) ExtraSkills.UNIVERSAL_PERCEPTION.get())
                && SkillUtils.isSkillMastered(entity, (ManasSkill) com.github.lucifel.virtuoso.registry.skill.ExtraSkills.CONCENTRATOR.get())
                && SkillUtils.hasSkill(entity, (ManasSkill) ExtraSkills.SAGE.get());
    }

    public void onLearnSkill(ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill() && entity instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill previousSkill = (Skill) Skills.OMNISCIENT_EYE.get();
            Objects.requireNonNull(storage);
            storage.forgetSkill(previousSkill);
        }

    }

    public boolean canBeToggled(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    public boolean canTick(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    public void onToggleOn(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (entity instanceof LivingEntity) {
            MobEffectInstance existing = entity.getEffect(TensuraMobEffects.PRESENCE_SENSE.get());
            if (existing == null || existing.getAmplifier() < 7) {
                entity.addEffect(new MobEffectInstance(
                        TensuraMobEffects.PRESENCE_SENSE.get(),
                        1200,
                        7,
                        false,
                        false,
                        false
                ));
            }
        }
    }

    public void onToggleOff(@NotNull ManasSkillInstance instance, LivingEntity entity) {
        if (entity.hasEffect((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get())) {
            int level = ((MobEffectInstance) Objects.requireNonNull(entity.getEffect((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get()))).getAmplifier();
            if (level == 6) {
                entity.removeEffect((MobEffect) TensuraMobEffects.PRESENCE_SENSE.get());
            }
        }
    }

    public void onBeingDamaged(@NotNull ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
            if (instance.isToggled()) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isBypassInvul()) {
                    Entity var5 = damageSource.getDirectEntity();
                    if (var5 instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) var5;
                        double dodgeChance = instance.isMastered(entity) ? 0.9 : 0.75;
                    }
                }
            }
        }
    }

    public void openSkillCopyGUI(ServerPlayer player, LivingEntity target, ManasSkillInstance instance) {
        SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
        if (targetStorage == null) return;

        List<Skill> copyableSkills = new ArrayList<>();

        for (ManasSkillInstance skillInstance : targetStorage.getLearnedSkills()) {
            if (skillInstance == null) continue;

            ManasSkill mSkill = skillInstance.getSkill();

            if (!skillInstance.isTemporarySkill()
                    && skillInstance.getMastery() >= 0
                    && mSkill instanceof Skill s
                    && (s.getType() == Skill.SkillType.COMMON
                    || s.getType() == Skill.SkillType.EXTRA
                    || s.getType() == Skill.SkillType.INTRINSIC
                    || s.getType() == Skill.SkillType.RESISTANCE
                    || s.getType() == Skill.SkillType.UNIQUE)) {

                copyableSkills.add(s);
            }
        }

        if (!copyableSkills.isEmpty()) {
            player.openMenu(new MenuProvider() {

                public AbstractContainerMenu createMenu(int pContainerId, Inventory inventory, Player p) {
                    OrunMenu menu = new OrunMenu(pContainerId, inventory);

                    menu.skills = copyableSkills.stream()
                            .map(Skill::getRegistryName)
                            .collect(Collectors.toList());
                    menu.targetUUID = target.getUUID();


                    return menu;
                }

                @Override
                public @NotNull Component getDisplayName() {
                    return Component.literal("Select Skill to Copy");
                }
            });
        } else {
            player.displayClientMessage(Component.literal("No skills can be copied from this target.")
                    .withStyle(ChatFormatting.RED), true);
        }
    }

    public int modes() {
        return 3;
    }


    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("tensura.skill.analytical_appraisal");
            case 2 -> Component.translatable("trmythos.ski.orunmila.read_record_analysis");
            case 3 -> Component.translatable("trmythos.skill.orunmila.destroy_record");
            default -> Component.empty();
        };
    }



    public int nextMode(@NotNull LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (instance.isMastered(entity)) {
            return instance.getMode() == 3 ? 1 : instance.getMode() + 1;
        } else {
            return instance.getMode() == 1 ? 2 : 1;
        }
    }

    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
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
                            int level = this.isMastered(instance, entity) ? 320 : 160;
                            if (cap.getAnalysisLevel() != level) {
                                cap.setAnalysisLevel(level);
                                cap.setAnalysisDistance(this.isMastered(instance, entity) ? 128 : 90);
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
        }
        if (instance.getMode() != 2) return;
        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        LivingEntity target = SkillHelper.getTargetingEntity(LivingEntity.class, entity, 5.0, 0.2, false, true);
        if (target == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable("trmysticism.skill.viciel.no_target")
                            .withStyle(ChatFormatting.RED), false
            );
            return;
        }

        if (target instanceof CloneEntity) {
            serverPlayer.displayClientMessage(
                    Component.translatable("trmythos.skill.orunmila.clone")
                            .withStyle(ChatFormatting.RED), false
            );
            return;
        }

        SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
        if (targetStorage == null) return;

        List<ManasSkill> copyableSkills = targetStorage.getLearnedSkills().stream()
                .filter(Objects::nonNull)
                .filter(skillInstancex -> !skillInstancex.isTemporarySkill())
                .filter(skillInstancex -> skillInstancex.getMastery() >= 0)
                .filter(skillInstancex -> {
                    ManasSkill s = skillInstancex.getSkill();
                    return s instanceof Skill skill && skill.getType() != Skill.SkillType.ULTIMATE;
                })
                .map(ManasSkillInstance::getSkill)
                .toList();

        if (copyableSkills.isEmpty()) {
            serverPlayer.displayClientMessage(
                    Component.literal("No skills can be copied from this target.")
                            .withStyle(ChatFormatting.RED), false
            );
            return;
        }

        NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.literal("Select Skill to Copy");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) {

                OrunMenu menu = new OrunMenu(id, inv);

                menu.skills = copyableSkills.stream()
                        .filter(s -> s instanceof Skill)
                        .map(s -> ((Skill) s).getRegistryName())
                        .filter(Objects::nonNull)
                        .toList();
                menu.targetUUID = target.getUUID();


                return menu;
            }
        });

        if (instance.getMode() == 3) {
            if (!(entity instanceof ServerPlayer player)) return;

            Level level = player.level;

            double reach = 10D;
            Vec3 eyePos = player.getEyePosition(1.0F);
            Vec3 lookVec = player.getLookAngle();
            Vec3 reachVec = eyePos.add(lookVec.scale(reach));

            AABB aabb = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0D);
            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(player, eyePos, reachVec, aabb,
                    e -> e instanceof LivingEntity && !e.isSpectator() && e.isAlive(), reach * reach);

            if (hitResult == null || !(hitResult.getEntity() instanceof LivingEntity)) {
                player.displayClientMessage(Component.literal("No valid target found.").withStyle(ChatFormatting.GRAY), true);
                return;
            }

            AttributeInstance hpAttr = target.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance shpAttr = target.getAttribute(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());

            if (player.isShiftKeyDown()) {

                if (hpAttr != null && hpAttr.getModifier(UUID.fromString(DESTROY_RECORD)) != null)
                    hpAttr.removePermanentModifier(UUID.fromString(DESTROY_RECORD));

                if (shpAttr != null && shpAttr.getModifier(UUID.fromString(DESTROY_RECORD)) != null)
                    shpAttr.removePermanentModifier(UUID.fromString(DESTROY_RECORD));

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.literal("You restore the torn page of the record.")
                        .withStyle(ChatFormatting.GREEN), true);
            } else {

                if (hpAttr != null) {
                    double reduction = hpAttr.getBaseValue() / 2.0 * -1.0;
                    AttributeModifier permDamage = new AttributeModifier(UUID.fromString(DESTROY_RECORD), "DestroyRecordHP", reduction,
                            AttributeModifier.Operation.ADDITION);
                    hpAttr.addPermanentModifier(permDamage);
                }

                if (shpAttr != null) {
                    double reduction = shpAttr.getBaseValue() / 2.0 * -1.0;
                    AttributeModifier permDamage = new AttributeModifier(UUID.fromString(DESTROY_RECORD), "DestroyRecordSHP", reduction,
                            AttributeModifier.Operation.ADDITION);
                    shpAttr.addPermanentModifier(permDamage);
                }

                target.hurt(MythosDamageSources.destroyRecord(player), (float) (target.getMaxHealth() / 2));
                target.hurt(MythosDamageSources.destroyRecord(player),
                        (float) (TensuraEPCapability.getSpiritualHealth(target) / 2));

                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1.0F, 0.75F);
                player.displayClientMessage(Component.literal("You tear a page from " + target.getName().getString() + "'s record.")
                        .withStyle(ChatFormatting.RED), true);
            }

            instance.setCoolDown(600);
            entity.swing(InteractionHand.MAIN_HAND, true);
        }
    }

}



