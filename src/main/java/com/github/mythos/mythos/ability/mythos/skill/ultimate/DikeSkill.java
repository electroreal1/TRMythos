package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DikeSkill extends Skill {
    public DikeSkill(SkillType type) {super(SkillType.ULTIMATE);}

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/dike.png");
    }

    @Override
    public double getObtainingEpCost() {return 5000000;}

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {return true;}
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {return instance.isToggled();}

    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.dike.invite_to_righteousness");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.dike.annihilation_ray");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.dike.glimpse_of_heaven");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        // Invite to Righteousness
        if (instance.getMode() == 1) {

        }

        // Glimpse of Heaven
        if (instance.getMode() == 3) {

        }
    }

    public void onTick(ManasSkillInstance instance, Player player) {
        if (instance.isToggled()) {
            boolean inLight = isInBrightLight(player);

            if (inLight) {
                player.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.COSTLESS_REGENERATION.get(), 1200, 1, false, false, false));
            } else {
                return;
            }
        }
    }

    public static boolean isInBrightLight(Player player) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();
        int light = level.getLightEngine().getRawBrightness(pos, 0);

        return light > 10;
    }

    public void onEntityHurt(LivingHurtEvent event, ManasSkillInstance instance) {
        if (instance.isToggled()) {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            float amount = event.getAmount();

            if ((DamageSourceHelper.isLightDamage(source)) || (DamageSourceHelper.isHoly(source))) {
                event.setCanceled(true);

                applyHealth(target, amount);
            }

            if (DamageSourceHelper.isDarkDamage(source)) {
                amount = event.getAmount() * 2.0F;
            }
        }
    }

    private static void applyHealth(LivingEntity entity, float amount) {
        float currentAbsorption = entity.getAbsorptionAmount();
        entity.setAbsorptionAmount(currentAbsorption + amount);
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isLightDamage(e.getSource()) || DamageSourceHelper.isHoly(e.getSource()) || DamageSourceHelper.isSpiritual(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 5.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }
        }
    }
}
