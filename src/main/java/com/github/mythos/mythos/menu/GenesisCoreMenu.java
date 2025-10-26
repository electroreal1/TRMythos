package com.github.mythos.mythos.menu;

import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenesisCoreMenu extends AbstractContainerMenu {
    private List<ResourceLocation> skills;
    private UUID targetUUID;

    public GenesisCoreMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, inventory, inventory.player);
        this.targetUUID = buf.readUUID();
        this.skills = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    public GenesisCoreMenu(int containerId, Inventory inventory, Player player) {
        super((MenuType) MythosMenuTypes.GENESIS_CORE_MENU.get(), containerId);
        this.skills = new ArrayList();
        this.targetUUID = player.getUUID();
    }

    public boolean stillValid(Player player) {
        return true;
    }

    public ItemStack quickMoveStack(Player player, int index) {
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
