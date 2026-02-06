package com.github.mythos.mythos.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class SoundSwapperMenu extends AbstractContainerMenu {
    public static final MenuType<SoundSwapperMenu> TYPE = IForgeMenuType.create(SoundSwapperMenu::new);

    public SoundSwapperMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv);
    }

    public SoundSwapperMenu(int id, Inventory inv) {
        super(TYPE, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}