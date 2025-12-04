package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.entity.ThunderStorm;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.skill.ExtraSkills;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

public class HeavensWrathSkill extends Skill {

    public HeavensWrathSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isLightningDamage(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 6.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }
        }
        if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) Skills.HEAVENS_WRATH.get())) {
            SkillUtils.reducingResistances(living);
        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        if (storage.getSkill(ExtraSkills.DARKNESS_MANIPULATION.get()).isPresent()) {
            storage.learnSkill(com.github.manasmods.tensura.registry.skill.ExtraSkills.BLACK_LIGHTNING.get());
        }
    }



    @Override
    public int modes() {
        return 2;
    }

    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.heavens_wrath.breath");
            case 2 -> Component.translatable("trmythos.skill.heavens_wrath.storm");
            default -> Component.empty();
        };
    }

    public int nextMode(@NotNull LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (instance.isMastered(entity)) {
            return instance.getMode() == 3 ? 1 : instance.getMode() + 1;
        } else {
            return instance.getMode() == 1 ? 2 : 1;
        }
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) {
                return false;
            } else {
                if (heldTicks % 100 == 0 && heldTicks > 0) {
                    this.addMasteryPoint(instance, entity);
                }

                entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);
                float damage = instance.isMastered(entity) ? 100.0F : 50.0F;
                BreathEntity.spawnBreathEntity((EntityType) TensuraEntityTypes.THUNDER_BREATH.get(), entity, instance, damage, this.magiculeCost(entity, instance));

            }
        }     return true;
    }

    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (instance.getMode() == 2) {

        boolean hasStorm = !entity.getLevel().getEntitiesOfClass(ThunderStorm.class, entity.getBoundingBox(),
                thunderStorm -> thunderStorm.getOwner() == entity).isEmpty();

        if (entity.isShiftKeyDown()) {

            for (ThunderStorm thunderStorm : entity.getLevel().getEntitiesOfClass(ThunderStorm.class, entity.getBoundingBox(),
                    b -> b.getOwner() == entity)) {
                thunderStorm.discard();
            }
        } else {

            if (!hasStorm && !SkillHelper.outOfMagicule(entity, 2000.0F)) {

                SkillHelper.outOfMagicule(entity, 2000.0F);

                ThunderStorm thunderStorm = new ThunderStorm(entity.getLevel(), entity);
                thunderStorm.setOwner(entity);
                float damage = instance.isMastered(entity) ? 500.0F : 250.0f;
                thunderStorm.setDamage(damage);
                entity.getLevel().addFreshEntity(thunderStorm);
                thunderStorm.applyEffect(entity);

                entity.swing(InteractionHand.MAIN_HAND, true);
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);

                this.addMasteryPoint(instance, entity);
                instance.setCoolDown(100);
                instance.getOrCreateTag().putInt("HeldTicks", 0);
                instance.markDirty();
                instance.setCoolDown(50);
            }
        }
        instance.markDirty();
        }
    }

    private boolean hasStorm(LivingEntity owner) {
        return !owner.getLevel().getEntitiesOfClass(ThunderStorm.class, owner.getBoundingBox(), (thunderStorm) -> {
            return thunderStorm.getOwner() == owner;
        }).isEmpty();
    }


}
