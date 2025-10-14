package com.github.mythos.mythos.menu;

import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrunMenu extends AbstractContainerMenu {

    public List<ResourceLocation> skills;
    public UUID targetUUID;
    public boolean isPaste;

    public OrunMenu(int id, Inventory playerInventory) {
        super(MythosMenuTypes.ORUN_MENU.get(), id);
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    public Arrays getSkills() {
        return null;
    }
}
