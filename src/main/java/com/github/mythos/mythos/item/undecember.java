package com.github.mythos.mythos.item;

import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.item.TensuraCreativeTab;
import com.github.manasmods.tensura.item.TensuraToolTiers;
import com.github.manasmods.tensura.item.templates.custom.SimpleLongSwordItem;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.manasmods.tensura.util.TensuraRarity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class undecember extends SimpleLongSwordItem {

    public undecember() {
        super(TensuraToolTiers.PURE_MAGISTEEL, (new Item.Properties()).tab(TensuraCreativeTab.GEAR).fireResistant().rarity(TensuraRarity.UNIQUE));
    }

//    public undecember_sword(Tier tier, int attackDamage, float attackSpeed, double range, double critChance, double critDamage, Item.Properties properties) {
//        super(tier, attackDamage, attackSpeed, range, critChance, critDamage, properties);
//    }

    public static void applyEnchants(ItemStack toStack, ItemStack fromStack, LivingEntity entity, EquipmentSlot toSlot) {
        if (toSlot.getType() == EquipmentSlot.Type.HAND && toStack.getItem() instanceof undecember) {
            Enchantment enchant5 = (Enchantment) TensuraEnchantments.TSUKUMOGAMI.get();
            if (toStack.getEnchantmentLevel(enchant5) < 1) {
                EngravingEnchantment.engrave(toStack, enchant5, 1);
            }
        }
    }




}
