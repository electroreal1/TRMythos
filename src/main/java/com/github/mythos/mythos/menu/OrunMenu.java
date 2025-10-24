package com.github.mythos.mythos.menu;

import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class OrunMenu extends AbstractContainerMenu {

    public List<ResourceLocation> skills;
    public UUID targetUUID;

    public OrunMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        this(pContainerId, inventory);
        this.targetUUID = buf.readUUID();
        this.skills = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    public OrunMenu(int pContainerId, Inventory inventory) {
        super((MenuType) MythosMenuTypes.ORUN_MENU.get(), pContainerId);
    }


    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    public boolean check() {
        return true;
    }

    public List<ResourceLocation> getSkills() {
        return this.skills;
    }

    public UUID getTargetUUID() {
        return this.targetUUID;
    }



}
