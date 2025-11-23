package com.github.mythos.mythos.ability.mythos.skill.unique.vassal_line;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;

public class FoundationSkill extends Skill {
    public FoundationSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }
    private static final Random RANDOM = new Random();

    // Configurable trigger chance
    public static final ForgeConfigSpec.DoubleValue vassalAssemblyChance;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        vassalAssemblyChance = builder
                .comment("Chance for Vassal Assembly to trigger on being damaged (0.0 - 1.0)")
                .defineInRange("vassalAssemblyChance", 0.2, 0.0, 1.0);
        builder.build();
    }


    @Override
    public double getObtainingEpCost() {
        return 500000;
    }

    @Override
    public int getMaxMastery() {
        return 2000;
    }


    public void onBeingDamaged(ManasSkillInstance instance, LivingHurtEvent event) {
        if (event.isCanceled()) return;

        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(entity);
        DamageSource source = event.getSource();

        if (source.isBypassInvul() || instance.onCoolDown()) return;

        double chance = vassalAssemblyChance.get();
        if (RANDOM.nextDouble() > chance) return;


        RegistryObject<? extends Skill> vassalSkill = null;

        // Determine which Vassal skill to grant
        if (DamageSourceHelper.isSpiritual(event.getSource())) {
        //    vassalSkill = UniqueSkills.VASSAL_CYCLE;
        } else if (DamageSourceHelper.isHoly(event.getSource())) {
            vassalSkill = Skills.BALANCE;
        } else if (event.getSource().isMagic()) {
            vassalSkill = Skills.BALANCE;
        } else if (!event.getSource().isMagic() && !DamageSourceHelper.isHoly(event.getSource()) && !DamageSourceHelper.isSpiritual(event.getSource())){
            vassalSkill = RANDOM.nextBoolean()
                    ? Skills.UNITY :
                     Skills.EVOLUTION;
        }

        if (vassalSkill != null) {
            Skill skill = vassalSkill.get();
            if (storage.getSkill(skill).isEmpty()) {
                storage.learnSkill(skill);
                instance.setCoolDown(200);
            }
        }
    }
}
