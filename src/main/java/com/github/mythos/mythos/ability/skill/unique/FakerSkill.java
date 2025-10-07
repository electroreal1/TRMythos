package com.github.mythos.mythos.ability.skill.unique;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
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

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    private boolean messageSent = false;

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) { // in a tick
        grantSevererIfMastered(instance, entity);
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
                // Analytical mode logic (unchanged)
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
            caster.displayClientMessage(Component.translatable("trmythos.skill.faker.projection.empty_hand").withStyle(ChatFormatting.RED), true);
            instance.setCoolDown(10);
            return;
        }

        // Check tsukumogami enchantment (prevent copying) or coins
        String itemId = Registry.ITEM.getKey(targetItem.getItem()).toString();
        boolean isRestricted = MythosSkillsConfig.getFakerSkillRestrictedItems().contains(itemId);
        boolean hasTsukumogami = EnchantmentHelper.getItemEnchantmentLevel(TensuraEnchantments.TSUKUMOGAMI.get(), targetItem) > 0;

        if (isRestricted || hasTsukumogami) {
            String translationKey = isRestricted
                    ? "trmythos.skill.faker.projection.fail.restricted"
                    : "trmythos.skill.faker.projection.fail.tsukumogami";

            caster.displayClientMessage(
                    Component.translatable(translationKey)
                            .withStyle(ChatFormatting.RED),
                    true
            );

            caster.level.playSound(
                    null,
                    caster.blockPosition(),
                    SoundEvents.VILLAGER_NO,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

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

        Map<Enchantment, Integer> upgrades = new LinkedHashMap<>();
        upgrades.put(Enchantments.SHARPNESS, 5);
        upgrades.put(Enchantments.SMITE, 5);
        upgrades.put(Enchantments.BANE_OF_ARTHROPODS, 5);
        upgrades.put(Enchantments.UNBREAKING, 5);
        upgrades.put(Enchantments.BLOCK_EFFICIENCY, 5);
        upgrades.put(Enchantments.MOB_LOOTING, 5);
        upgrades.put(Enchantments.MENDING, 5);
        upgrades.put(Enchantments.POWER_ARROWS, 5);
        upgrades.put(Enchantments.FLAMING_ARROWS, 5);
        upgrades.put(Enchantments.INFINITY_ARROWS, 5);
        upgrades.put(Enchantments.BLOCK_FORTUNE, 5);
        upgrades.put(Enchantments.FISHING_LUCK, 5);
        upgrades.put(Enchantments.RESPIRATION, 5);
        upgrades.put(Enchantments.ALL_DAMAGE_PROTECTION, 5);
        upgrades.put(Enchantments.FIRE_PROTECTION, 5);
        upgrades.put(Enchantments.FALL_PROTECTION, 5);
        upgrades.put(Enchantments.BLAST_PROTECTION, 5);
        upgrades.put(Enchantments.PROJECTILE_PROTECTION, 5);
        upgrades.put(Enchantments.THORNS, 5);
        upgrades.put(Enchantments.DEPTH_STRIDER, 5);
        upgrades.put(Enchantments.FROST_WALKER, 5);
        upgrades.put(Enchantments.SOUL_SPEED, 5);
        upgrades.put(Enchantments.SWEEPING_EDGE, 5);
        upgrades.put(Enchantments.SILK_TOUCH, 1);
        upgrades.put(Enchantments.FISHING_SPEED, 5);
        upgrades.put(Enchantments.AQUA_AFFINITY, 5);
        upgrades.put(Enchantments.KNOCKBACK, 5);
        upgrades.put(Enchantments.FIRE_ASPECT, 5);
        upgrades.put(Enchantments.QUICK_CHARGE, 5);
        upgrades.put(Enchantments.MULTISHOT, 5);
        upgrades.put(Enchantments.PIERCING, 5);

        // Tensura custom enchantments
        upgrades.put(TensuraEnchantments.HOLY_COAT.get(), 3);
        upgrades.put(TensuraEnchantments.HOLY_WEAPON.get(), 2);
        upgrades.put(TensuraEnchantments.MAGIC_WEAPON.get(), 2);
        upgrades.put(TensuraEnchantments.MAGIC_INTERFERENCE.get(), 3);
        upgrades.put(TensuraEnchantments.SOUL_EATER.get(), 3);
        upgrades.put(TensuraEnchantments.SEVERANCE.get(), 5);
        upgrades.put(TensuraEnchantments.BARRIER_PIERCING.get(), 2);
        upgrades.put(TensuraEnchantments.BREATHING_SUPPORT.get(), 5);
        upgrades.put(TensuraEnchantments.CRUSHING.get(), 2);
        upgrades.put(TensuraEnchantments.STURDY.get(), 3);
        upgrades.put(TensuraEnchantments.ENERGY_STEAL.get(), 2);
        upgrades.put(TensuraEnchantments.ELEMENTAL_BOOST.get(), 4);
        upgrades.put(TensuraEnchantments.ELEMENTAL_RESISTANCE.get(), 4);
        upgrades.put(TensuraEnchantments.SLOTTING.get(), 5);
        upgrades.put(TensuraEnchantments.SWIFT.get(), 3);

        for (Map.Entry<Enchantment, Integer> entry : upgrades.entrySet()) {
            Enchantment ench = entry.getKey();
            int optimizedLevel = entry.getValue();

            int currentLevel = item.getEnchantmentLevel(ench); // returns 0 if not present
            if (currentLevel <= 0) continue; // skip enchantments not currently present

            if (optimizedLevel <= 0) {
                removeEnchantmentNbt(item, ench, currentLevel);

                Map<Enchantment, Integer> currentMap = EnchantmentHelper.getEnchantments(item);
                currentMap.remove(ench);
                EnchantmentHelper.setEnchantments(currentMap, item);
            } else {
                removeEnchantmentNbt(item, ench, currentLevel);
                item.enchant(ench, optimizedLevel);
            }
        }

        entity.setItemInHand(hand, item);
        entity.swing(hand);
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
