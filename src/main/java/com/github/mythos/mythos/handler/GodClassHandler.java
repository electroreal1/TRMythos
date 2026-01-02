package com.github.mythos.mythos.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class GodClassHandler extends SavedData {
    private static final String DENDRAHH_OBTAINED = "dendrahh_obtained";

    private static boolean DendrahhObtained = false;

    public GodClassHandler() {
    }


    public boolean isDendrahhObtained() {
        return DendrahhObtained;
    }

    public void setDendrahhObtained() {
        DendrahhObtained = true;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("dendrahhObtained", DendrahhObtained);
        return null;
    }

    public static GodClassHandler load(CompoundTag tag) {
        GodClassHandler godClassHandler = new GodClassHandler();
        godClassHandler.DendrahhObtained = tag.getBoolean("dendrahhObtained");
        return godClassHandler;
    }

    public static GodClassHandler get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(GodClassHandler::load, GodClassHandler::new, DENDRAHH_OBTAINED);
    }

}
