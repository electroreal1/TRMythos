 package com.github.mythos.mythos.mixin.races;

 import com.github.manasmods.tensura.race.Race;
 import com.github.manasmods.tensura.race.vampire.VampireRace;
 import com.github.manasmods.tensura.registry.race.TensuraRaces;
 import com.github.mythos.mythos.registry.race.MythosRaces;
 import net.minecraft.world.entity.player.Player;
 import net.minecraftforge.registries.IForgeRegistry;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 import java.util.List;

 @Mixin(VampireRace.class)
public abstract class VampireEvoMixin {

  public VampireEvoMixin() {
  }

  @Inject(
          method = {"getNextEvolutions"},
          at = {@At("RETURN")},
          cancellable = true,
          remap = false
  )
  public void getNextEvolutions(Player player, CallbackInfoReturnable<List<Race>> cir) {
   List list = (List)cir.getReturnValue();
   list.add((Race)((IForgeRegistry<?>) TensuraRaces.RACE_REGISTRY.get()).getValue(MythosRaces.VAMPIRE_BARON));
   cir.setReturnValue(list);
  }
 }

