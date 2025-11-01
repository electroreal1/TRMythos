package com.github.mythos.mythos.mob_effect.debuff;

import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class RotEffect extends MobEffect {

    public RotEffect(MobEffectCategory pCategory, Color pColor) {
        super(MobEffectCategory.BENEFICIAL, 990000 );
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.getHealth() > 0.0F) {
            int durabilityCorrosion = 15 * (pAmplifier + 1);
            float damage = 2.0F * (float)(pAmplifier + 1);
            Player source = TensuraEffectsCapability.getEffectSource(pLivingEntity, this);
            if (source == null) {
                pLivingEntity.hurt(MythosDamageSources.ROT, damage);
            } else {
                pLivingEntity.hurt(MythosDamageSources.rot(source), damage);
            }

            EquipmentSlot[] var6 = EquipmentSlot.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                EquipmentSlot slot = var6[var8];
                ItemStack slotStack = pLivingEntity.getItemBySlot(slot);
                slotStack.hurtAndBreak(durabilityCorrosion, pLivingEntity, (player) -> {
                    player.broadcastBreakEvent(slot);
                });
            }
        }

    }

    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 20 == 0;
    }
}
