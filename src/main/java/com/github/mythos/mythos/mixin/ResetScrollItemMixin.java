package com.github.mythos.mythos.mixin;

import com.github.manasmods.tensura.item.custom.ResetScrollItem;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResetScrollItem.class)
public class ResetScrollItemMixin {

    @Inject(method = "resetEverything", at = @At("HEAD"), remap = false)
    private static void onResetEverything(ServerPlayer player, CallbackInfo ci) {
        mythos$clearEvolutionFlags(player);

        VoiceOfTheWorld.delayedAnnouncement(player,
                "Notice.",
                "The records of Individual: " + player.getName().getString() + " are being withdrawn.",
                "Successful. All authorities have been withdrawn.",
                "Beginning reincarnation process..."
        );

        VoiceOfTheWorld.screenShake(player, 2.0f, 20);
    }

    @Inject(method = "resetRace", at = @At("HEAD"), remap = false)
    private static void onResetRace(ServerPlayer player, CallbackInfo ci) {
        mythos$clearEvolutionFlags(player);
    }

    @Unique
    private static void mythos$clearEvolutionFlags(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        tag.remove("Mythos_AcknowledgedTDL");
        tag.remove("Mythos_AcknowledgedHero");
        tag.remove("Mythos_SeedNotified");
        tag.remove("Mythos_EggNotified");
    }

}
