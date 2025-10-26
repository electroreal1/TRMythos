package com.github.mythos.mythos.ability.skill.unique;

import com.github.lucifel.virtuoso.ability.skill.VirtuosoUtils;
import com.github.lucifel.virtuoso.registry.battlewills.OptimalArts;
import com.github.lucifel.virtuoso.registry.skill.ExtraSkills;
import com.github.lucifel.virtuoso.registry.skill.OptimalSkills;
import com.github.lucifel.virtuoso.registry.skill.UniqueSkills;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.menu.GenesisCoreMenu;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenesisCoreSkill extends Skill {
    public GenesisCoreSkill(SkillType type) {
        super(type);
    }

//    @Override
//    public ResourceLocation getSkillIcon() {
//        return new ResourceLocation("trmythos", "textures/skill/unique/genesis_core.png");
//    }

    public double getObtainingEpCost() {
        return 100000.0;
    }

    public double learningCost() {
        return 1000.0;
    }

    public int modes() {
        return 2;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 2 ? 1 : 2;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.mode.genesis_core.optimize_skill");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.mode.genesis_core.fuse_skill");
                break;
            default:
                var10000 = Component.translatable("trmythos.skill.mode.genesis_core.optimize_skill");
        }

        return var10000;
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        double var10000;
        switch (instance.getMode()) {
            case 1:
                var10000 = 0;
                break;
            case 2:
                var10000 = 0.0;
                break;
            default:
                var10000 = 0.0;
        }

        return var10000;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            ServerPlayer serverPlayer = (ServerPlayer) entity;
            List<ResourceLocation> list = new ArrayList();
            ResourceLocation optimalMove;
            if (VirtuosoUtils.hasCookBase(serverPlayer)) {
                optimalMove = OptimalSkills.OPTIMAL_COOK.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasIdealBase(serverPlayer)) {
                optimalMove = OptimalSkills.UTOPIA.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasAvalonBase(serverPlayer)) {
                optimalMove = OptimalSkills.EVER_DISTANT.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasConcentratorBase(serverPlayer)) {
                optimalMove = ExtraSkills.CONCENTRATOR.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasSpiralBase(serverPlayer)) {
                optimalMove = ExtraSkills.SPIRAL_DOMINATION.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasSpiral2Base(serverPlayer)) {
                optimalMove = UniqueSkills.SPIN.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasRegenerationBase(serverPlayer)) {
                optimalMove = com.github.manasmods.tensura.registry.skill.ExtraSkills.ULTRASPEED_REGENERATION.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasRegeneration2Base(serverPlayer)) {
                optimalMove = com.github.manasmods.tensura.registry.skill.ExtraSkills.INFINITE_REGENERATION.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasRegeneration3Base(serverPlayer)) {
                optimalMove = OptimalSkills.OPTIMAL_REGENERATION.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasRegeneration4Base(serverPlayer)) {
                optimalMove = UniqueSkills.GLORIOUS.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasGuardianBase(serverPlayer)) {
                optimalMove = OptimalSkills.OPTIMAL_GUARDIAN.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }
            if (MythosUtils.hasGravityDominationAndSpatialDomination(serverPlayer)) {
                optimalMove = com.github.manasmods.tensura.registry.skill.UniqueSkills.SUPPRESSOR.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            if (VirtuosoUtils.hasInstantMoveBase(serverPlayer)) {
                optimalMove = OptimalArts.OPTIMAL_MOVE.getId();
                if (this.canCreateSkill(optimalMove, serverPlayer, instance)) {
                    list.add(optimalMove);
                }
            }

            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(GenesisCoreMenu::new, Component.empty()), (buf) -> {
                buf.writeUUID(entity.getUUID());
                buf.writeCollection(list, FriendlyByteBuf::writeResourceLocation);
            });
        }
    }

    private boolean canCreateSkill(ResourceLocation location, ServerPlayer serverPlayer, ManasSkillInstance instance) {
        ManasSkill skill = (ManasSkill) SkillAPI.getSkillRegistry().getValue(location);
        if (skill == null) {
            return false;
        } else {
            Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(serverPlayer).getSkill(skill);
            if (instance.isMastered(serverPlayer)) {
                return (Boolean)optional.map(ManasSkillInstance::isTemporarySkill).orElse(true);
            } else {
                CompoundTag tag = instance.getTag();
                if (tag != null && tag.contains("created_skill")) {
                    ResourceLocation created = new ResourceLocation(tag.getString("created_skill"));
                    if (created.equals(location)) {
                        return false;
                    }
                }

                return optional.isEmpty();
            }
        }
    }
}
