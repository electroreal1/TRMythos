package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class FakerSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("0147c153-32a2-4524-8ba3-ba4c2f449d7c");

    @Nullable
    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/faker.png");
    }

    public FakerSkill() {
        super(Skill.SkillType.UNIQUE);
    }

    public double getObtainingEpCost() {
        return 100000.0;
    }

    public double learningCost() {
        return 1000.0;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    private boolean messageSent = false;

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        if (entity instanceof LivingEntity) {
            if (entity.hasEffect(MythosMobEffects.AVALON_REGENERATION.get())) {
                return;
            } else {
                entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.AVALON_REGENERATION.get(), 1200, 1, false, false, false));
            }
        }
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        MobEffectInstance effectInstance = entity.getEffect((MobEffect)MythosMobEffects.AVALON_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 1)
            entity.removeEffect((MobEffect)MythosMobEffects.AVALON_REGENERATION.get());
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) { // in a tick
        grantSevererIfMastered(instance, entity);
        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.AVALON_REGENERATION.get(), 1200, 1, false, false, false));
        }
        return;
    }

    private void grantSevererIfMastered(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (entity.level.isClientSide()) return;
        if (!this.isMastered(instance, entity)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Skill severer = UniqueSkills.SEVERER.get();

        if (storage.getSkill(severer).isPresent()) return;
        storage.learnSkill(severer);

        player.displayClientMessage(
                Component.translatable("trmythos.skill.faker.mastered.grant.severer").withStyle(ChatFormatting.GOLD),
                false
        );

        player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);

        TensuraSkillCapability.sync(player);
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
                name = Component.translatable("trmythos.skill.mode.faker.analytical_appraisal");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.faker.reinforcement");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.faker.projection");
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
                // Reinforcement
                if (!SkillHelper.outOfMagicule(entity, instance)) {
                    if (instance.getMode() == 2) {
                        ItemStack mainHandItem = entity.getMainHandItem();
                        if (!mainHandItem.isEmpty()) {
                            reinforce(instance, entity, InteractionHand.MAIN_HAND);
                            instance.setCoolDown(this.isMastered(instance, entity) ? 60 : 120);
                            addMasteryPoint(instance, entity);
                        }
                    }
                }
                break;

            case 3:
                // Projection
                if (!(entity instanceof Player caster)) return;
                if (SkillHelper.outOfMagicule(entity, instance)) return;

                // perform projection logic
                performProjection(instance, caster);
                break;
        }
    }

    private void performProjection(ManasSkillInstance instance, Player caster) {
        // server side only
        if (caster.level.isClientSide()) return;

        // ensure enough resource
        if (SkillHelper.outOfMagicule(caster, instance)) return;

        double range = 30;
        Vec3 eyePos = caster.getEyePosition(1.0F);
        Vec3 lookVec = caster.getViewVector(1.0F);
        Vec3 end = eyePos.add(lookVec.scale(range));
        AABB searchBox = caster.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);

        Predicate<Entity> predicate = target ->
                target instanceof LivingEntity living &&
                        living != caster &&
                        !living.getMainHandItem().isEmpty();

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                caster,
                eyePos,
                end,
                searchBox,
                predicate,
                range * range
        );

        if (hit == null || !(hit.getEntity() instanceof LivingEntity targetLiving)) {
            caster.displayClientMessage(Component.translatable("trmythos.skill.faker.projection.no_target").withStyle(ChatFormatting.RED), true);
            caster.level.playSound(null, caster.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 1.0F, 1.0F);
            instance.setCoolDown(10);
            return;
        }

        ItemStack targetItem = targetLiving.getMainHandItem();
        if (targetItem.isEmpty()) {
            caster.displayClientMessage(Component.translatable("trmythos.skill.faker.projection.empty_hand")
                    .withStyle(ChatFormatting.RED), true);
            instance.setCoolDown(10);
            return;
        }

// Check tsukumogami enchantment (prevent copying) or restricted items
        String itemId = Registry.ITEM.getKey(targetItem.getItem()).toString();
        boolean isRestricted = MythosSkillsConfig.getFakerSkillRestrictedItems().contains(itemId);
        boolean hasTsukumogami = EnchantmentHelper.getItemEnchantmentLevel(TensuraEnchantments.TSUKUMOGAMI.get(), targetItem) > 0;

        if (hasTsukumogami || isRestricted) {
            String translationKey = hasTsukumogami
                    ? "trmythos.skill.faker.projection.fail.tsukumogami"
                    : "trmythos.skill.faker.projection.fail.restricted";

            caster.displayClientMessage(Component.translatable(translationKey)
                    .withStyle(ChatFormatting.RED), true);

            caster.level.playSound(null, caster.blockPosition(), SoundEvents.VILLAGER_NO,
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            instance.setCoolDown(10);
            return;
        }


        ItemStack projectedCopy = targetItem.copy();
        // make single count copy, optional
        projectedCopy.setCount(1);

        // tag as projection (optional)
        CompoundTag tag = projectedCopy.getOrCreateTag();
        tag.putBoolean("IsProjection", true);
        tag.putUUID("ProjectedBy", caster.getUUID());
        projectedCopy.setTag(tag);

        // try to put in main hand if empty, else inventory, else drop
        if (caster.getMainHandItem().isEmpty()) {
            caster.setItemInHand(InteractionHand.MAIN_HAND, projectedCopy);
        } else if (!caster.getInventory().add(projectedCopy)) {
            caster.drop(projectedCopy, false);
        }

        caster.displayClientMessage(Component.translatable("trmythos.skill.faker.projection.success", targetLiving.getDisplayName()).withStyle(ChatFormatting.GOLD), true);
        caster.level.playSound(null, caster.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);

        // cooldown and mastery point
        instance.setCoolDown(120);
        addMasteryPoint(instance, caster);
    }

    private static void removeEnchantmentNbt(ItemStack item, Enchantment enchantment, int levelToRemove) {
        if (item == null || item.isEmpty()) return;

        CompoundTag tag = item.getOrCreateTag();
        ListTag enchList = tag.getList("Enchantments", 10); // 10 = TAG_COMPOUND
        String enchKey = Registry.ENCHANTMENT.getKey(enchantment).toString(); // e.g. "minecraft:sharpness"

        for (int i = enchList.size() - 1; i >= 0; i--) {
            CompoundTag e = enchList.getCompound(i);
            String id = e.getString("id");
            int lvl = e.getShort("lvl");
            if (enchKey != null && enchKey.equals(id) && lvl == levelToRemove) {
                enchList.remove(i);
            }
        }

        if (enchList.isEmpty()) {
            tag.remove("Enchantments");
        } else {
            tag.put("Enchantments", enchList);
        }
        item.setTag(tag);
    }

    private void reinforce(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand) {
        if (SkillHelper.outOfMagicule(entity, instance)) return;
        addMasteryPoint(instance, entity);

        ItemStack item = entity.getItemInHand(hand);
        if (item.isEmpty()) return;

        item.getOrCreateTag().putBoolean("Unbreakable", true);

        List<? extends String> configuredEnchantments = MythosSkillsConfig.getFakerSkillReinforceEnchantments();

        for (Enchantment ench : item.getAllEnchantments().keySet()) {
            String enchId = Registry.ENCHANTMENT.getKey(ench).toString();

            // find matching config entry
            for (String entry : configuredEnchantments) {
                String[] parts = entry.split(":");
                if (parts.length != 3) continue;

                String configId = parts[0] + ":" + parts[1];
                int newLevel;
                try {
                    newLevel = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (!enchId.equals(configId)) continue; // only apply matching enchantments

                int currentLevel = item.getEnchantmentLevel(ench);
                if (currentLevel > 0) removeEnchantmentNbt(item, ench, currentLevel);
                if (newLevel > 0) item.enchant(ench, newLevel);
            }
        }

        entity.setItemInHand(hand, item);
        entity.swing(hand);
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}