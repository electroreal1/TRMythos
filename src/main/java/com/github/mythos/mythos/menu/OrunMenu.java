package com.github.mythos.mythos.menu;

import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class OrunMenu extends AbstractContainerMenu {
    private List<ResourceLocation> skills;
    private UUID targetUUID;

    public OrunMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        this(pContainerId, inventory);
        this.targetUUID = buf.readUUID();
        this.skills = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    public OrunMenu(int pContainerId, Inventory inventory) {
        super(MythosMenuTypes.ORUN_MENU.get(), pContainerId);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    public List<ResourceLocation> getSkills() { return this.skills; }
    public UUID getTargetUUID() { return this.targetUUID; }
    public boolean check() { return true; }
}