package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.entity.human.CloneEntity;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.menu.OrunMenu;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class OrunmilaSkill extends Skill {
    private static final String DESTROY_RECORD_UUID = "f5d7629b-e065-4f76-8f24-6997097e937d";

    public OrunmilaSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("tensura.skill.analytical_appraisal");
            case 2 -> Component.literal("Read Record: Analysis");
            case 3 -> Component.literal("Destroy Record");
            default -> Component.empty();
        };
    }

    @Override
    public double getObtainingEpCost() { return 2500000; }

    @Override
    public boolean meetEPRequirement(@NotNull Player entity, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        return SkillUtils.isSkillMastered(entity, Skills.OMNISCIENT_EYE.get())
                && SkillUtils.isSkillMastered(entity, ExtraSkills.ALL_SEEING_EYE.get())
                && SkillUtils.isSkillMastered(entity, ExtraSkills.UNIVERSAL_PERCEPTION.get())
                && SkillUtils.isSkillMastered(entity, com.github.lucifel.virtuoso.registry.skill.VExtraSkills.CONCENTRATOR.get())
                && SkillUtils.hasSkill(entity, ExtraSkills.SAGE.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (entity instanceof Player player) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            storage.forgetSkill(Skills.OMNISCIENT_EYE.get());
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;

        if (instance.isToggled() && entity.tickCount % 20 == 0) {
            AttributeInstance maxMP = entity.getAttribute(TensuraAttributeRegistry.MAX_MAGICULE.get());
            assert maxMP != null;
            double restoreAmount = maxMP.getValue() * 0.01;
            SkillHelper.gainMP(entity, restoreAmount);

            AttributeInstance maxAP = entity.getAttribute(TensuraAttributeRegistry.MAX_AURA.get());
            assert  maxAP != null;
            double restoreAmount1 = maxAP.getValue() * 0.01;
            SkillHelper.gainAP(entity, restoreAmount1);
        }

        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_SENSE.get(), 20, 5, false, false, false));
        }
    }


    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (instance.isToggled() && !event.getSource().isBypassInvul()) {
            double chance = instance.isMastered(event.getEntity()) ? 0.90 : 0.75;
            if (event.getEntity().getRandom().nextDouble() < chance) {
                event.setCanceled(true);
                event.getEntity().level.playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 2.0F);
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (entity instanceof Player player) {
                TensuraSkillCapability.getFrom(player).ifPresent(cap -> {
                    if (player.isCrouching()) {
                        int mode = (cap.getAnalysisMode() + 1) % 3;
                        cap.setAnalysisMode(mode);
                        TensuraSkillCapability.sync(player);
                        entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    } else {
                        int level = instance.isMastered(entity) ? 200 : 100;
                        cap.setAnalysisLevel(cap.getAnalysisLevel() == level ? 0 : level);
                        cap.setAnalysisDistance(instance.isMastered(entity) ? 128 : 90);
                        TensuraSkillCapability.sync(player);
                        entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                });
            }
        }
        if (instance.getMode() == 2 && entity instanceof ServerPlayer player) {
            LivingEntity target = SkillHelper.getTargetingEntity(LivingEntity.class, entity, 15.0, 0.2, false, true);
            if (target != null && !(target instanceof CloneEntity)) {
                openSkillRepository(player, target);
            } else {
                player.displayClientMessage(Component.literal("No record found in sight.").withStyle(ChatFormatting.RED), true);
            }
        }
        if (instance.getMode() == 3 && instance.isMastered(entity)) {
            if (entity instanceof ServerPlayer player) {
                handleDestroyRecord(instance, player);
            }
        }
    }

    private void handleDestroyRecord(ManasSkillInstance instance, ServerPlayer player) {
        double reach = 10D;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.scale(reach));
        AABB aabb = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0D);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(player, eyePos, reachVec, aabb, e -> e instanceof LivingEntity && e.isAlive(), reach * reach);

        if (hitResult != null && hitResult.getEntity() instanceof LivingEntity target) {
            AttributeInstance hpAttr = target.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance shpAttr = target.getAttribute(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
            UUID uuid = UUID.fromString(DESTROY_RECORD_UUID);

            if (player.isShiftKeyDown()) {
                if (hpAttr != null) hpAttr.removePermanentModifier(uuid);
                if (shpAttr != null) shpAttr.removePermanentModifier(uuid);
                player.displayClientMessage(Component.literal("Record Restored.").withStyle(ChatFormatting.GREEN), true);
            } else {
                if (hpAttr != null) {
                    hpAttr.addPermanentModifier(new AttributeModifier(uuid, "DestroyHP", -hpAttr.getBaseValue() * 0.5, AttributeModifier.Operation.ADDITION));
                }
                if (shpAttr != null) {
                    shpAttr.addPermanentModifier(new AttributeModifier(uuid, "DestroySHP", -shpAttr.getBaseValue() * 0.5, AttributeModifier.Operation.ADDITION));
                }
                target.hurt(DamageSource.OUT_OF_WORLD, target.getMaxHealth() * 0.5F);
                player.displayClientMessage(Component.literal("Page Torn.").withStyle(ChatFormatting.RED), true);
                player.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1.0F, 0.7F);
            }
            instance.setCoolDown(600);
            player.swing(InteractionHand.MAIN_HAND, true);
        }
    }

    private void openSkillRepository(ServerPlayer player, LivingEntity target) {
        SkillStorage storage = SkillAPI.getSkillsFrom(target);
        if (storage == null) return;

        List<ResourceLocation> copyableSkills = storage.getLearnedSkills().stream()
                .filter(inst -> inst.getSkill() instanceof Skill s && s.getType() != Skill.SkillType.ULTIMATE)
                .filter(inst -> inst.getMastery() >= 0 && !inst.isTemporarySkill())
                .map(inst -> inst.getSkill().getRegistryName())
                .filter(Objects::nonNull)
                .toList();

        if (copyableSkills.isEmpty()) {
            player.displayClientMessage(Component.literal("Target has no copyable records.").withStyle(ChatFormatting.RED), true);
            return;
        }

        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                (id, inv, p) -> new OrunMenu(id, inv),
                Component.literal("Record Repository")
        ), buf -> {
            buf.writeUUID(target.getUUID());
            buf.writeCollection(copyableSkills, FriendlyByteBuf::writeResourceLocation);
        });

        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public int modes() { return 3; }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        int max = instance.isMastered(entity) ? 3 : 2;
        int current = instance.getMode();
        if (reverse) return current <= 1 ? max : current - 1;
        return current >= max ? 1 : current + 1;
    }
}