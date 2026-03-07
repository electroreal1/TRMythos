package com.github.mythos.mythos.util.kill_tracker;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class KillTrackerProvider implements ICapabilitySerializable<CompoundTag> {
    public static Capability<IKillTracker> CAPABILITY = CapabilityManager.get(new CapabilityToken<IKillTracker>() {});
    private final IKillTracker instance = new KillTracker();
    private final LazyOptional<IKillTracker> handler = LazyOptional.of(() -> instance);

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CAPABILITY.orEmpty(cap, handler);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("awakenedKills", instance.getAwakenedKills());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.setAwakenedKills(nbt.getInt("awakenedKills"));
    }
}
