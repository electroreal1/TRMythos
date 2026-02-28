package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.handler.ContagionHandler;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContagionSkill extends Skill {
    public ContagionSkill() {
        super(SkillType.UNIQUE);
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("An extremely adaptable infection, lacking any inborn malice or hatred. Simply following its nature. WIP.");
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Contagion");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        onTick(instance, entity);
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        onTick(instance, entity);
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide || !instance.isToggled()) return;

        List<LivingEntity> targets = entity.level.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(15.0),
                (target) -> !target.is(entity) && target.isAlive()
        );

        if (!targets.isEmpty() && !SkillHelper.outOfMagicule(entity, instance)) {
            for (LivingEntity victim : targets) {
                if (victim instanceof Player p && p.getAbilities().invulnerable) continue;

                victim.getPersistentData().putUUID("ContagionSource", entity.getUUID());

                victim.addEffect(new MobEffectInstance(MythosMobEffects.PATHOGEN.get(), 200, 0, false, true));

                if (entity instanceof Player player) {
                    ContagionHandler.awardBiomatter(player, 1);
                }
            }
        }
    }


    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        if (player.isShiftKeyDown()) {
            int bio = ContagionHandler.getBiomatter(player);
            player.displayClientMessage(Component.literal("§2[Contagion] §7Current Biomatter: §a" + bio), true);
        } else {
            if (ContagionHandler.isListeningForMutation(player)) {
                ContagionHandler.setListening(player, false);
            } else {
                ContagionHandler.setListening(player, true);
                ContagionHandler.sendMutationMenu(player);
            }
        }
    }


}
