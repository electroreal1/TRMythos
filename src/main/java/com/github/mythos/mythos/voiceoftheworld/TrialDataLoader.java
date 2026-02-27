package com.github.mythos.mythos.voiceoftheworld;

import com.github.mythos.mythos.config.MythosConfig;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TrialDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public TrialDataLoader() {
        super(GSON, "trials");
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        boolean isConfigLoaded = MythosConfig.SPEC.isLoaded();

        if (isConfigLoaded) {
            boolean votw = !MythosSkillsConfig.voice_of_the_world.get();
            if (votw) {
                return;
            }
        }
        WorldTrialRegistry.TRIALS.clear();

        TrialManager.init();

        pObject.forEach((location, element) -> {
            try {
                WorldTrial trial = GSON.fromJson(element, WorldTrial.class);

                WorldTrialRegistry.register(trial);

            } catch (Exception ignored) {

            }
        });
    }
}