package com.github.mythos.mythos.mixin.races;

import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.human.DivineHumanRace;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.race.MythosRaces;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({DivineHumanRace.class})
public class DivineHumanEvoMixin {
    public DivineHumanEvoMixin(){
    }

    @Inject(
            method = {"getNextEvolutions"},
            at = {@At("RETURN")},
            cancellable = true,
            remap = false
    )
    public void getNextEvolutions(Player player, CallbackInfoReturnable<List<Race>> cir) {
        List<Race> list = (List)cir.getReturnValue();
        list.add((Race)(IForgeRegistry) TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.NAMELESS_DIVINITY_RACE));
        cir.setReturnValue(list);
    }
}
