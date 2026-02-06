package com.github.mythos.mythos.mixin;

import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.handler.GlobalSoundMap;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class SoundLevelMixin {
    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"))
    private void onPlaySound(Player sourcePlayer, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, CallbackInfo ci) {
        if (sourcePlayer != null && source == SoundSource.PLAYERS) {
            sourcePlayer.getCapability(TensuraSkillCapability.CAPABILITY).ifPresent(cap -> {
                var skillInstance = Skills.ORPHEUS.get();
                skillInstance.triggerSoundFromMixin(sourcePlayer, volume);
            });
        }
    }

    @ModifyVariable(method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"), argsOnly = true)
    private SoundEvent swapSound(SoundEvent original) {
        if (original == null) return original;

        String originalPath = original.getLocation().toString();
        String newPath = GlobalSoundMap.getReplacement(originalPath);

        if (!newPath.equals(originalPath)) {
            ResourceLocation res = new ResourceLocation(newPath);
            if (ForgeRegistries.SOUND_EVENTS.containsKey(res)) {
                return ForgeRegistries.SOUND_EVENTS.getValue(res);
            }
        }
        return original;
    }

}
