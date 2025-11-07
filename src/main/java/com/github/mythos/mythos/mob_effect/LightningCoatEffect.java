package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;

public class LightningCoatEffect extends MobEffect {
    public LightningCoatEffect(MobEffectCategory mobEffectCategory, Color color) {
        super(MobEffectCategory.BENEFICIAL, 0x99ccff);
    }

    public static void applyLightningOnHit(LivingEntity target, LivingEntity attacker, int amplifier) {
        float damage = 50.0f;
        target.hurt(TensuraDamageSources.lightning(attacker), damage);
    }

    @SubscribeEvent
    public static void onEntityHit(LivingHurtEvent event, LivingEntity target, LivingEntity attacker) {
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof LivingEntity)) return;

        target = (LivingEntity) event.getEntity();
        attacker = (LivingEntity) sourceEntity;

        if (attacker.hasEffect(MythosMobEffects.LIGHTNING_COAT.get())) {
            int amplifier = attacker.getEffect(MythosMobEffects.LIGHTNING_COAT.get()).getAmplifier();
            applyLightningOnHit(target, attacker, amplifier);
        }
    }



}
