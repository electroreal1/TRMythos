package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.mythos.mythos.registry.MythosItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemEnchantHandler {

    public static void applyTsukumogamiEnchantments(ItemStack toStack) {
        if ((toStack.is(MythosItems.UNDECEMBER.get())) && toStack.getEnchantmentLevel((Enchantment) TensuraEnchantments.TSUKUMOGAMI.get()) < 1) {
            EngravingEnchantment.engrave(toStack, (Enchantment)TensuraEnchantments.TSUKUMOGAMI.get(), 1);
        }
        if ((toStack.is(MythosItems.FRAGARACH.get())) && toStack.getEnchantmentLevel((Enchantment) TensuraEnchantments.TSUKUMOGAMI.get()) < 1) {
            EngravingEnchantment.engrave(toStack, (Enchantment)TensuraEnchantments.TSUKUMOGAMI.get(), 1);
        }
                if ((toStack.is(MythosItems.CATHARSIS.get())) && toStack.getEnchantmentLevel((Enchantment) TensuraEnchantments.TSUKUMOGAMI.get()) < 1) {
            EngravingEnchantment.engrave(toStack, (Enchantment)TensuraEnchantments.TSUKUMOGAMI.get(), 1);
        }

    }


}
