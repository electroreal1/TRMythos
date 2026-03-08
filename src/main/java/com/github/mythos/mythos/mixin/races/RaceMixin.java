package com.github.mythos.mythos.mixin.races;

import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.race.MythosRaces;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Race.class)
public class RaceMixin {

    @Inject(
            method = "getNextEvolutions",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    public void injectMythosEvolution(Player player, CallbackInfoReturnable<List<Race>> cir) {
        Race currentRace = (Race) (Object) this;
        IForgeRegistry<Race> registry = TensuraRaces.RACE_REGISTRY.get();

        ResourceLocation raceId = registry.getKey(currentRace);
        if (raceId == null) return;

        String racePath = raceId.toString().toLowerCase();

        boolean isExcludedKeyword = racePath.contains("titan") ||
                racePath.contains("divinity") ||
                racePath.contains("god");

        if (currentRace.isDivine() && !isExcludedKeyword) {
            List<Race> evolutions = new ArrayList<>(cir.getReturnValue());

            Race namelessDivinity = registry.getValue(MythosRaces.NAMELESS_DIVINITY_RACE);

            if (namelessDivinity != null && !evolutions.contains(namelessDivinity)) {
                evolutions.add(namelessDivinity);
                cir.setReturnValue(evolutions);
            }
        }
    }
}