package com.github.mythos.mythos.ability.mythos.skill.extra;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.ISpatialStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SpatialStorageSkill extends Skill implements ISpatialStorage {

    public SpatialStorageSkill(SkillType type) {
        super(SkillType.EXTRA);
    }

    @Override
    public int modes() {
        return 1;
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/extra/spatial_storage.png");
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) ExtraSkills.SPATIAL_MANIPULATION.get());

    }

    @Override
    public double getObtainingEpCost() {
        return 1000;
    }

    @Override
    public void onPressed(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        this.openSpatialStorage(entity, instance);
    }

    public @NotNull SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance) {
        SpatialStorageContainer container = new SpatialStorageContainer(63, 99);
        container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10));
        return container;
    }
}
