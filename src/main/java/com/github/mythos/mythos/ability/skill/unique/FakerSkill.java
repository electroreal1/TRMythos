package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class FakerSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("0147c153-32a2-4524-8ba3-ba4c2f449d7c");

    public FakerSkill() {
        // use Skill.SkillType to avoid a missing import for SkillType
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

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
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
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.mode.faker.analytical_appraisal");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.mode.faker.reinforcement");
                break;
            case 3:
                var10000 = Component.translatable("trmythos.skill.mode.faker.projection");
                break;
            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:
                // Only proceed if the entity has enough magicule
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
                // Only proceed if the entity has enough magicule
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
                // (no behavior defined yet)
                break;
        }
    }

    private static void removeEnchantmentNbt(ItemStack item, Enchantment enchantment, int levelToRemove) {
        if (item == null || item.isEmpty()) return;

        CompoundTag tag = item.getOrCreateTag();
        ListTag enchList = tag.getList("Enchantments", 10); // 10 = TAG_COMPOUND
        String enchKey = Registry.ENCHANTMENT.getKey(enchantment).toString(); // e.g. "minecraft:sharpness"

        // iterate backwards and remove entries that match both id and level
        for (int i = enchList.size() - 1; i >= 0; i--) {
            CompoundTag e = enchList.getCompound(i);
            String id = e.getString("id");
            int lvl = e.getShort("lvl"); // lvl stored as short in NBT
            if (enchKey.equals(id) && lvl == levelToRemove) {
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

        // mapping of enchantments -> optimized level
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

            // If optimizedLevel == 0: fully remove the enchantment (do not re-add)
            if (optimizedLevel <= 0) {
                // remove all NBT entries for this enchantment (safest)
                removeEnchantmentNbt(item, ench, currentLevel);

                // also remove from the enchantment map that Minecraft uses to display/enforce enchantments
                Map<Enchantment, Integer> currentMap = EnchantmentHelper.getEnchantments(item);
                currentMap.remove(ench);
                EnchantmentHelper.setEnchantments(currentMap, item);
            } else {
                // remove the old NBT entry(s) for the previous level BEFORE applying the optimized level
                removeEnchantmentNbt(item, ench, currentLevel);

                // now apply the optimized level (this will add a single NBT entry with the optimized level)
                item.enchant(ench, optimizedLevel);
            }
        }

        // Save item back to the hand, animate and play sound
        entity.setItemInHand(hand, item);
        entity.swing(hand);
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
