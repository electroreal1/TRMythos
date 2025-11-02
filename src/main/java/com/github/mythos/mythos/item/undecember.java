package com.github.mythos.mythos.item;

import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.item.TensuraCreativeTab;
import com.github.manasmods.tensura.item.TensuraToolTiers;
import com.github.manasmods.tensura.item.templates.custom.SimpleLongSwordItem;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.manasmods.tensura.util.TensuraRarity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class undecember extends SimpleLongSwordItem {

    public undecember() {
        super(TensuraToolTiers.PURE_MAGISTEEL, (new Item.Properties()).tab(TensuraCreativeTab.GEAR).fireResistant().rarity(TensuraRarity.UNIQUE));
    }

//    public undecember_sword(Tier tier, int attackDamage, float attackSpeed, double range, double critChance, double critDamage, Item.Properties properties) {
//        super(tier, attackDamage, attackSpeed, range, critChance, critDamage, properties);
//    }
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
    super.onCraftedBy(stack, level, player);
    Enchantment tsuku = TensuraEnchantments.TSUKUMOGAMI.get();
    if (stack.getEnchantmentLevel(tsuku) < 1) {
        EngravingEnchantment.engrave(stack, tsuku, 1);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide) {
            Enchantment tsuku = TensuraEnchantments.TSUKUMOGAMI.get();
            if (stack.getEnchantmentLevel(tsuku) < 1) {
                EngravingEnchantment.engrave(stack, tsuku, 1);
            }
        }
    }




}
