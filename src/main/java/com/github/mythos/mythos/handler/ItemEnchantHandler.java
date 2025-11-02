package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.mythos.mythos.registry.MythosWeapons;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemEnchantHandler {

    public static void applyTsukumogamiEnchantments(ItemStack toStack) {
        if ((toStack.is(MythosWeapons.UNDECEMBER.get())) && toStack.getEnchantmentLevel((Enchantment) TensuraEnchantments.TSUKUMOGAMI.get()) < 1) {
            EngravingEnchantment.engrave(toStack, (Enchantment)TensuraEnchantments.TSUKUMOGAMI.get(), 1);
        }
    }


}
