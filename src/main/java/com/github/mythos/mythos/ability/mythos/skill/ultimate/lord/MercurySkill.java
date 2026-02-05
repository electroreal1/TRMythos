package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class MercurySkill extends Skill {
    public MercurySkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 2000000;
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) UniqueSkills.TRAVELER.get()) &&
                SkillUtils.isSkillMastered(player, (ManasSkill) UniqueSkills.USURPER.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = UniqueSkills.TRAVELER.get();
            Skill greedSkill1 = UniqueSkills.USURPER.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill1).ifPresent(storage::forgetSkill);

            VoiceOfTheWorld.announceToPlayer(player,
                    "Confirmed. Skill [Traveler] and the Skill [Usurper] have successfully fused and evolved into the Skill [Mercury, Lord of Freedom].");
        }
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public @NotNull List<MobEffect> getImmuneEffects(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        if (instance.isToggled()) {
            list.add((MobEffect) TensuraMobEffects.INFINITE_IMPRISONMENT.get());
            list.add((MobEffect) TensuraMobEffects.MAGIC_INTERFERENCE.get());
            list.add((MobEffect) TensuraMobEffects.SPATIAL_BLOCKADE.get());
            list.add((MobEffect) TensuraMobEffects.ENERGY_BLOCKADE.get());
            list.add((MobEffect) TensuraMobEffects.PARALYSIS.get());
            list.add((MobEffect) MysticismMobEffects.TIMESTOP.get());
            list.add((MobEffect) MysticismMobEffects.TIMESTOP_CORE.get());
            list.add(MobEffects.WEAKNESS);
            list.add(MobEffects.MOVEMENT_SLOWDOWN);
            list.add(MobEffects.DIG_SLOWDOWN);
        }
        return list;
    }




}
