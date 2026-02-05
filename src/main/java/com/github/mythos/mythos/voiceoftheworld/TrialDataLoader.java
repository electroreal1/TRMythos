package com.github.mythos.mythos.voiceoftheworld;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import java.util.Map;

public class TrialDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public TrialDataLoader() {
        super(GSON, "trials");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        WorldTrialRegistry.TRIALS.clear();

        TrialDefinitions.init();

        pObject.forEach((location, element) -> {
            try {
                WorldTrial trial = GSON.fromJson(element, WorldTrial.class);

                WorldTrialRegistry.register(trial);

            } catch (Exception e) {

            }
        });
    }
}