package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.MythosWeapons;
import com.github.mythos.mythos.util.MythosDamageSourceHelper;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class Sporeblood extends Skill {
    public Sporeblood(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isMastered(entity);
    }


    public void onToggleOn(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        DamageSource source = e.getSource();
        if (source.getDirectEntity() == attacker && DamageSourceHelper.isCorrosion(source) && DamageSourceHelper.isPoison(source) && MythosDamageSourceHelper.isRot(source)) {
            e.setAmount(e.getAmount() * 2.0F);
        }
    }

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        if (this.isInSlot(attacker) || instance.isToggled()) {
            if (e.getSource().getDirectEntity() == attacker) {
                if (DamageSourceHelper.isPhysicalAttack(e.getSource())) {
                    Level level = attacker.getLevel();
                    LivingEntity target = e.getEntity();
                    MobEffect poison = MobEffects.POISON;
                    MobEffect corrosion = TensuraMobEffects.CORROSION.get();
                    MobEffect rot = MythosMobEffects.ROT.get();
                    SkillHelper.checkThenAddEffectSource(target, attacker, poison, 200, 1);
                    SkillHelper.checkThenAddEffectSource(target, attacker, corrosion, 200, 1);
                    SkillHelper.checkThenAddEffectSource(target, attacker, rot, 200, 1);
                    level.playSound((Player)null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                    ((ServerLevel)level).sendParticles((SimpleParticleType) TensuraParticles.POISON_BUBBLE.get(), target.position().x, target.position().y + (double)target.getBbHeight() / 2.0, target.position().z, 20, 0.08, 0.08, 0.08, 0.15);
                    ((ServerLevel)level).sendParticles((SimpleParticleType) TensuraParticles.ACID_BUBBLE.get(), target.position().x, target.position().y + (double)target.getBbHeight() / 2.0, target.position().z, 20, 0.08, 0.08, 0.08, 0.15);
                    CompoundTag tag = instance.getOrCreateTag();
                    int time = tag.getInt("activatedTimes");
                    if (time % 10 == 0) {
                        this.addMasteryPoint(instance, attacker);
                    }

                    tag.putInt("activatedTimes", time + 1);
                }
            }
        }
    }

    @Override
    public int modes() {
        return 1;
    }

    @Override
    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.sporeblood.undecember");
                break;
            default:
                var10000 = Component.empty();
        }
        return var10000;
    }

    @Override
    public int getMaxMastery() {
        return 10000;
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return 4444.0;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            if (entity.getMainHandItem().isEmpty()) {
                this.spawnDummySword(instance, entity, InteractionHand.MAIN_HAND);
                instance.setCoolDown(100);
            } else if (entity.getOffhandItem().isEmpty()) {
                this.spawnDummySword(instance, entity, InteractionHand.OFF_HAND);
                instance.setCoolDown(100);
            }
        }
    }

    private void spawnDummySword(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            this.addMasteryPoint(instance, entity);
            ItemStack blade = new ItemStack((ItemLike) MythosWeapons.UNDECEMBER.get());
            entity.setItemInHand(hand, blade);
            entity.swing(hand, true);
            entity.level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
