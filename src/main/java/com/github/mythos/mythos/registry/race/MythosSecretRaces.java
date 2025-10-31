package com.github.mythos.mythos.registry.race;

import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.race.VampireEvoLine.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MythosSecretRaces {

    // Vampires
    public static final ResourceLocation VAMPIRE_BARON = new ResourceLocation(Mythos.MOD_ID, "vampire_baron");
    public static final ResourceLocation VAMPIRE_VISCOUNT = new ResourceLocation(Mythos.MOD_ID, "vampire_viscount");
    public static final ResourceLocation VAMPIRE_EARL = new ResourceLocation(Mythos.MOD_ID, "vampire_earl");
    public static final ResourceLocation VAMPIRE_MARQUIS = new ResourceLocation(Mythos.MOD_ID, "vampire_marquis");
    public static final ResourceLocation VAMPIRE_DUKE = new ResourceLocation(Mythos.MOD_ID, "vampire_duke");
    public static final ResourceLocation VAMPIRE_ARCHDUKE = new ResourceLocation(Mythos.MOD_ID, "vampire_archduke");
    public static final ResourceLocation VAMPIRE_PRINCE = new ResourceLocation(Mythos.MOD_ID, "vampire_prince");
    public static final ResourceLocation VAMPIRE_TRUE_ANCESTOR = new ResourceLocation(Mythos.MOD_ID, "vampire_true_ancestor");

    public static void register(RegisterEvent event) {

        // Vampires
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_baron", new VampireBaron());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_viscount", new VampireViscount());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_earl", new VampireEarl());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_marquis", new VampireMarquis());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_duke", new VampireDuke());
        });
        event.register(((IForgeRegistry) TensuraRaces.RACE_REGISTRY.get()).getRegistryKey(), helper -> {
            helper.register("vampire_true_ancestor", new VampireTrueAncestor());
        });

    }

}
