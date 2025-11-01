package com.github.mythos.mythos.item;

import com.github.manasmods.tensura.item.TensuraCreativeTab;
import com.github.manasmods.tensura.item.TensuraToolTiers;
import com.github.manasmods.tensura.item.templates.custom.SimpleLongSwordItem;
import com.github.manasmods.tensura.util.TensuraRarity;
import net.minecraft.world.item.Item;

public class UndecemberItem extends SimpleLongSwordItem {

    public UndecemberItem() {
        super(TensuraToolTiers.PURE_MAGISTEEL, (new Item.Properties()).tab(TensuraCreativeTab.GEAR).fireResistant().rarity(TensuraRarity.UNIQUE));
    }


}
