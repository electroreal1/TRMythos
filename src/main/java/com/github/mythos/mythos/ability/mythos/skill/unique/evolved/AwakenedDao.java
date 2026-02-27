package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.blocks.TensuraBlocks;
import com.github.manasmods.tensura.registry.items.TensuraMobDropItems;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class AwakenedDao extends Skill {
    protected static final UUID ACCELERATION1 = UUID.fromString("189e6684-8b53-4abf-9156-07a1c6b1563b");
    protected static final UUID ACCELERATION2 = UUID.fromString("81112168-ecb2-4686-b91d-387f363843a8");

    public AwakenedDao(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 50000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, Skills.NASCENT_DAO.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof Player player && !instance.isTemporarySkill()) {
            VoiceOfTheWorld.announceToPlayer(player,
                    "Confirmed. Skill [Nascent Dao] has successfully evolved into the Skill [Awakened Dao].");
        }
    }

    @Override
    public int getMaxMastery() {
        return 4000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION1, true);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION2, true);
    }
    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION1, false);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION2, false);
    }
    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player = (Player) entity;
        ItemStack held = player.getMainHandItem();
        Item item = held.getItem();

        int epGained;

        if (item == TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 20;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 60;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()) {
            epGained = 120;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get().asItem()) {
            epGained = 180;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get().asItem()) {
            epGained = 540;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else if (item == TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get().asItem()) {
            epGained = 1080;
            this.addMasteryPoint(instance, entity);
            held.shrink(1);
        } else {
            return;
        }

        SkillHelper.gainMaxAP(entity, epGained / 2f);
        SkillHelper.gainMaxMP(entity, epGained / 2f);
    }
}
