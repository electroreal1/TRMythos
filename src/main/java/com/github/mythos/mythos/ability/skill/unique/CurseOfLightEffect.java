package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class CurseOfLightEffect {
    /** Call this every tick for players that own the Bloodsucker skill. */
    public static void tick(ManasSkillInstance instance, Player player) {
        Level level = player.level;
        if (level.isClientSide() || player.isCreative() || player.isSpectator())
            return;

        boolean mastered = instance.isMastered(player);
        boolean inSunlight = isUnderSun(player);

        if (inSunlight) {
            applySunlightCurse(player, mastered);
        } else {
            applyDarkBuffs(player, mastered);
        }
    }

    public static boolean shouldBurn(Player player) {
        if (player.isInWaterRainOrBubble())
            return false;

        if (player.isCreative() || player.isSpectator())
            return false;

        return isUnderSun(player);
    }

    public static boolean isUnderSun(LivingEntity entity) {
        Level level = entity.level;

        // Quick rejects
        if (level.isClientSide) return false;           // server-side only
        if (entity.isDeadOrDying()) return false;      // dead entities don't count
        if (entity.isInvulnerable()) return false;     // mimic original invulnerable check

        // Spectator check for players
        if (entity instanceof Player player && player.isSpectator()) return false;

        // World must have a sky (not Nether/End)
        if (!level.dimensionType().hasSkyLight()) return false;

        // Optional: skip non-interactive entities (if you have that helper)
        // if (noInteractiveMode(entity)) return false;

        // Position to sample light from (use eye position vector rounded to blockpos)
        BlockPos pos = new BlockPos(entity.getX(), entity.getEyeY(), entity.getZ());

        // If the entity is swimming/in water/rain/bubble, treat as not exposed
        if (entity.isInWaterRainOrBubble()) return false;

        // Check sky light (0..15) and convert to fraction
        int skyLight = level.getBrightness(LightLayer.SKY, pos); // 0..15
        float skyFraction = skyLight / 15.0F;

        // The original used a threshold ~0.5 ; replicate that using skyFraction
        if (skyFraction <= 0.5F) return false;

        // Ensure the blockpos can see sky from below water (handles partial submersion)
        if (!level.canSeeSkyFromBelowWater(pos)) return false;

        // Passed all checks => under sunlight
        return true;
    }

    /** Deals 1 % of max HP as true flame damage every 5 s. */
    private static void applySunlightCurse(Player player, boolean mastered) {
        Level level = player.level;
        if (level.isClientSide() || player.isCreative() || player.isSpectator())
            return;

        // Every 100 ticks = 5 seconds
        if (player.tickCount % 100 != 0)
            return;

        float maxHP = player.getMaxHealth();
        float dmg = Math.max(0.5F, maxHP * 0.01F); // at least half a heart

        // Force fire tick to start
        player.setSecondsOnFire(4);

        // Deal actual flame damage (lava source is reliable for entities)
        player.hurt(DamageSource.LAVA, dmg);

        // Play a fire sound effect
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 0.6F, 1.0F);
    }

    /** Grants Strengthen 5/10 and Resistance 1/3 while out of sunlight. */
    private static void applyDarkBuffs(Player player, boolean mastered) {
        int strengthenAmp = mastered ? 9 : 4;   // +1 because 0-based
        int resistanceAmp = mastered ? 2 : 0;

        player.addEffect(new MobEffectInstance(
                TensuraMobEffects.STRENGTHEN.get(), 120, strengthenAmp, true, false, true));
        player.addEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, 120, resistanceAmp, true, false, true));
    }
}