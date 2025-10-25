package com.github.mythos.mythos.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class ChildOfThePlaneRaceHandler extends SavedData {
    private static final String DATA_NAME = "unique_skill_tracker";
    private UUID ownerUUID;

    public static ChildOfThePlaneRaceHandler get(ServerLevel level) {
        return level.getServer().overworld()
                .getDataStorage()
                .computeIfAbsent(ChildOfThePlaneRaceHandler::load, ChildOfThePlaneRaceHandler::new, DATA_NAME);
    }

    public boolean isClaimed() {
        return ownerUUID != null;
    }

    public void claim(UUID playerId) {
        ownerUUID = playerId;
        setDirty();
    }

    public static ChildOfThePlaneRaceHandler load(CompoundTag tag) {
        ChildOfThePlaneRaceHandler tracker = new ChildOfThePlaneRaceHandler();
        if (tag.hasUUID("Owner")) tracker.ownerUUID = tag.getUUID("Owner");
        return tracker;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
        return tag;
    }

}
