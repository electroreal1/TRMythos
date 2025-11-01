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

public class UndecemberItem extends SimpleLongSwordItem {

    public UndecemberItem() {
        super(TensuraToolTiers.PURE_MAGISTEEL, (new Item.Properties()).tab(TensuraCreativeTab.GEAR).fireResistant().rarity(TensuraRarity.UNIQUE));
    }

    public static void applyEnchants(ItemStack toStack, ItemStack fromStack, LivingEntity entity, EquipmentSlot toSlot) {
        if (toSlot.getType() == EquipmentSlot.Type.HAND && toStack.getItem() instanceof UndecemberItem) {
            Enchantment tsuku = (Enchantment) TensuraEnchantments.TSUKUMOGAMI.get();

            if (toStack.getEnchantmentLevel(tsuku) < 1) {
                EngravingEnchantment.engrave(toStack, tsuku, 1);
            }
        }
    }


}
