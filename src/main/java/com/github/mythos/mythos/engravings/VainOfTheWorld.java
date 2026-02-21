package com.github.mythos.mythos.engravings;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class VainOfTheWorld extends EngravingEnchantment {
    public VainOfTheWorld() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public ChatFormatting getNameFormatting() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }

    @Override
    public void doAdditionalAttack(ItemStack stack, LivingEntity attacker, Entity target, int pLevel, float damage) {
        if (!(target instanceof LivingEntity victim)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(attacker);
        var skillInstance = storage.getSkill(Skills.ANGRA_MAINYU.get()).orElse(null);

        if (skillInstance != null) {
            CompoundTag tag = skillInstance.getOrCreateTag();
            int totalCurses = tag.getInt("TotalCurseCount");

            float bonusDamage = totalCurses / 10.0f;

            DamageSource damageSource = new DamageSource(TensuraDamageSources.DARK_ATTACK);
            victim.hurt(damageSource, bonusDamage);


            int amplifier = Math.min(10, totalCurses / 80);
            victim.addEffect(new MobEffectInstance(MythosMobEffects.EVIL_OF_HUMANITY.get(), 100, amplifier));

            if (attacker.level instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SQUID_INK, victim.getX(), victim.getY() + 1, victim.getZ(), 15, 0.3, 0.3, 0.3, 0.05);

                attacker.level.playSound(null, victim.blockPosition(),
                        SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 0.5f, 0.1f);
            }
        }
    }


}
