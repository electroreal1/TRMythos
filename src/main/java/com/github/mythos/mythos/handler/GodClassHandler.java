package com.github.mythos.mythos.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class GodClassHandler extends SavedData {
    private static final String DATA_NAME = "mythos_god_data";
    private boolean dendrahhObtained = false;
    private boolean khonsuObtained = false;
    private boolean kthanidObtained = false;

    public GodClassHandler() {}

    // Getters
    public boolean isDendrahhObtained() { return dendrahhObtained; }
    public boolean isKhonsuObtained() { return khonsuObtained; }
    public boolean isKthanidObtained() { return kthanidObtained; }

    // Setters
    public void setDendrahhObtained(boolean value) {
        this.dendrahhObtained = value;
        this.setDirty();
    }

    public void setKhonsuObtained(boolean value) {
        this.khonsuObtained = value;
        this.setDirty();
    }

    public void setKthanidObtained(boolean value) {
        this.kthanidObtained = value;
        this.setDirty();
    }

    public void resetAllOwners() {
        this.dendrahhObtained = false;
        this.khonsuObtained = false;
        this.kthanidObtained = false;
        this.setDirty();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.putBoolean("dendrahhObtained", dendrahhObtained);
        tag.putBoolean("khonsuObtained", khonsuObtained);
        tag.putBoolean("kthanidObtained", kthanidObtained);
        return tag;
    }

    public static GodClassHandler load(CompoundTag tag) {
        GodClassHandler handler = new GodClassHandler();
        handler.dendrahhObtained = tag.getBoolean("dendrahhObtained");
        handler.khonsuObtained = tag.getBoolean("khonsuObtained");
        handler.kthanidObtained = tag.getBoolean("kthanidObtained");
        return handler;
    }

    public static GodClassHandler get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage()
                .computeIfAbsent(GodClassHandler::load, GodClassHandler::new, DATA_NAME);
    }
}