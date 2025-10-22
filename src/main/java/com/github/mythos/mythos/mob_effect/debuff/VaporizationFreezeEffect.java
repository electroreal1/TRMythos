package com.github.mythos.mythos.mob_effect.debuff;

import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.race.ITensuraPlayerCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

public class VaporizationFreezeEffect extends SkillMobEffect {
    protected static final UUID VAPORIZATIONFREEZE = UUID.fromString("3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf");

    public VaporizationFreezeEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        addAttributeModifier(Attributes.MAX_HEALTH, "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED, "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier((Attribute)ForgeMod.REACH_DISTANCE.get(), "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier((Attribute)ForgeMod.SWIM_SPEED.get(), "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier((Attribute)ManasCoreAttributes.JUMP_POWER.get(), "3f5b674c-8437-3c5b-b88c-c15b6f1bfcaf", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.level.isClientSide) {
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, pAmplifier, false, true, true ));
            if (TensuraMobEffects.FRAGILITY.get() != null) {
                pLivingEntity.addEffect(new MobEffectInstance(TensuraMobEffects.FRAGILITY.get(), 200, pAmplifier, false, true, true));
            }
            pLivingEntity.level.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (pLivingEntity instanceof Player) {
            Player player = (Player)pLivingEntity;
            if ((player.getAbilities()).flying) {
                (player.getAbilities()).flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return (pDuration % 10 == 0);
    }

    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}