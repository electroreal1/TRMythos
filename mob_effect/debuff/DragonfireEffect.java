package com.github.mythos.mythos.mob_effect.debuff;

import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.effect.template.SkillMobEffect;
import com.github.manasmods.tensura.registry.items.TensuraConsumableItems;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class DragonfireEffect extends SkillMobEffect {
    public DragonfireEffect(MobEffectCategory pCatagory, int pColor) {
        super(pCatagory, pColor);
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        float damage = (float) (pAmplifier + 1);
        Player source = TensuraEffectsCapability.getEffectSource(pLivingEntity, this);
        if (source == null) {
            pLivingEntity.hurt(MythosDamageSources.DRAGONFIRE, damage);
        } else {
            pLivingEntity.hurt(MythosDamageSources.dragonFire(source), damage);
        }
    }

    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 20 == 0;
    }

    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> itemStacks = new ArrayList();
        itemStacks.add(new ItemStack((ItemLike) TensuraConsumableItems.HOLY_MILK.get()));
        itemStacks.add(new ItemStack((ItemLike)TensuraConsumableItems.HOLY_MILK_BUCKET.get()));
        return itemStacks;
    }
}
