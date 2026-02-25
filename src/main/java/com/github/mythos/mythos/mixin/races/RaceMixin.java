package com.github.mythos.mythos.mixin.races;

import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.race.MythosRaces;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin({Race.class})
public class RaceMixin {

    @Inject(
            method = "getNextEvolutions",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    public void injectMythosEvolution(Player player, CallbackInfoReturnable<List<Race>> cir) {
        Race currentRace = (Race) (Object) this;
        boolean isDivine = Objects.requireNonNull(currentRace.getRegistryName()).getPath().contains("divine") ||
                currentRace.getRegistryName().getPath().contains("Divine");

        if (isDivine) {
            List<Race> evolutions = new ArrayList<>(cir.getReturnValue());

            IForgeRegistry<Race> registry = TensuraRaces.RACE_REGISTRY.get();
            Race namelessDivinity = registry.getValue(MythosRaces.NAMELESS_DIVINITY_RACE);

            if (namelessDivinity != null && !evolutions.contains(namelessDivinity)) {
                evolutions.add(namelessDivinity);
                cir.setReturnValue(evolutions);
            }
        }
    }
}