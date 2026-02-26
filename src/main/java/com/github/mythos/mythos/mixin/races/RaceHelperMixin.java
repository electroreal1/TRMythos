//package com.github.mythos.mythos.mixin.races;
//
//import com.github.manasmods.tensura.race.RaceHelper;
//import net.minecraft.world.entity.player.Player;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(RaceHelper.class)
//public class RaceHelperMixin {
//
//    @Inject(
//            method = "canAwaken",
//            at = @At("HEAD"),
//            cancellable = true,
//            remap = false)
//    private static void mythos$interceptAwakening(Player player, boolean isHero, int souls, CallbackInfoReturnable<Boolean> cir) {
//       // if () {
//            cir.setReturnValue(false);
//      //  }
//    }
//
//
//}
