package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.battlewill.Battewill;
import com.github.manasmods.tensura.ability.magic.Magic;
import com.github.mythos.mythos.ability.confluence.skill.ConfluenceUniques;
import com.github.mythos.mythos.ability.mythos.skill.unique.normal.BibliomaniaSkill;
import com.github.mythos.mythos.ability.mythos.skill.unique.normal.ChildOfThePlaneSkill;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

import static com.github.manasmods.tensura.ability.SkillUtils.isSkillToggled;
import static com.github.manasmods.tensura.capability.skill.TensuraSkillCapability.isSkillInSlot;

@Mixin(value = {SkillUtils.class}, priority = 8)
public abstract class SkillUtilsMixin {

    public SkillUtilsMixin() {
    }

    @Shadow
    public static boolean hasSkill(Entity entity, ManasSkill manasSkill) {
        return false;
    }

    @Shadow
    public static boolean isSkillMastered(LivingEntity entity, ManasSkill manasSkill) {
        return false;
    }

    @ModifyReturnValue(method = {"getEarningLearnPoint"}, at = {@At("RETURN")}, remap = false)
    private static int trmythos$modifyEarningLearnPoint(int original, ManasSkillInstance instance, LivingEntity entity, boolean isMode) {
        int point = original;

        if (hasSkill(entity, (ManasSkill) Skills.ORUNMILA.get())) {
            point = original + 20;
        }
        if (hasSkill(entity, (ManasSkill) Skills.ELTNAM.get())) {
            point += 6;
        }
        if (hasSkill(entity, (ManasSkill) Skills.ZEPIA.get())) {
            point += 9;
        }
        if (hasSkill(entity, (ManasSkill) Skills.OMNISCIENT_EYE.get())) {
            point += 10;
        }
        if (hasSkill(entity, (ManasSkill) Skills.TRUE_DAO.get())) {
            point += 5;
        }
        if (hasSkill(entity, (ManasSkill) Skills.ORIGIN_DAO.get())) {
            point += 10;
        }
        if (hasSkill(entity, (ManasSkill) Skills.DEMONOLOGIST.get())) {
            point += 5;
        }
        if (hasSkill(entity, (ManasSkill) Skills.SAGITTARIUS.get())) {
            point += 8;
        }
        if (hasSkill(entity, (ManasSkill) ConfluenceUniques.CELESTIAL_CULTIVATION_ORANGE.get()) && instance.getSkill() instanceof Magic) {
            point += 999;
        }
        if (hasSkill(entity, (ManasSkill) ConfluenceUniques.CELESTIAL_MUTATION_RED.get()) && instance.getSkill() instanceof Battewill) {
            point += 999;
        }
        if (hasSkill(entity, (ManasSkill) Skills.FALSE_HERO.get())) {
            point += 15;
        }


        return point;
    }

    @ModifyReturnValue(method = {"getBonusMasteryPoint"}, at = {@At("RETURN")}, remap = false)
    private static int trmythos$modifyBonusMasteryPoint(int original, ManasSkillInstance instance, LivingEntity entity) {
        int point = original;

        if (isSkillToggled(entity, (ManasSkill) Skills.ORUNMILA.get())) {
            point += 20;
        }
        if (hasSkill(entity, (ManasSkill) Skills.ELTNAM.get())) {
            point += 6;
        }
        if (hasSkill(entity, (ManasSkill) Skills.ZEPIA.get())) {
            point += 9;
        }
        if (hasSkill(entity, (ManasSkill) Skills.OMNISCIENT_EYE.get())) {
            point += 10;
        }
        if (hasSkill(entity, (ManasSkill) Skills.OMNISCIENT_EYE.get()) && instance.getSkill() instanceof Magic) {
            point += 999;
        }
        if (hasSkill(entity, (ManasSkill) Skills.TRUE_DAO.get())) {
            point += 5;
        }
        if (hasSkill(entity, (ManasSkill) Skills.ORIGIN_DAO.get())) {
            point += 10;
        }
        if (hasSkill(entity, (ManasSkill) Skills.DEMONOLOGIST.get())) {
            point += 5;
        }
        if (hasSkill(entity, (ManasSkill) Skills.SAGITTARIUS.get())) {
            point += 8;
        }
        if (hasSkill(entity, (ManasSkill) Skills.FALSE_HERO.get())) {
            point += 15;
        }
        if (hasSkill(entity, (ManasSkill) ConfluenceUniques.CELESTIAL_CULTIVATION_ORANGE.get()) && instance.getSkill() instanceof Magic) {
            point += 999;
        }
        if (hasSkill(entity, (ManasSkill) ConfluenceUniques.CELESTIAL_MUTATION_RED.get()) && instance.getSkill() instanceof Battewill) {
            point += 999;
        }


        return point;
    }


    @ModifyReturnValue(method = {"canNegateDodge"}, at = {@At("RETURN")}, remap = false)
    private static boolean trmythos$modifyCanNegateDodge(boolean original, LivingEntity entity, DamageSource source) {
        Entity var4 = source.getEntity();
        if (var4 instanceof LivingEntity attacker) {
            if (isSkillInSlot(attacker, (ManasSkill) Skills.ORUNMILA.get())) {
                original = true;
            }
            if (isSkillInSlot(attacker, (ManasSkill) ConfluenceUniques.FRAGARACH.get())) {
                if (Math.random() < 0.5) {
                    original = true;
                }
            }
            if (isSkillInSlot(attacker, (ManasSkill) Skills.PRETENDER_KING.get())) {
                original = true;
            }
            if (isSkillInSlot(attacker, (ManasSkill) Skills.DENDRRAH.get())) {
                original = true;
            }

            if (isSkillInSlot(attacker, Skills.KHONSU.get())) {
                original = true;
            }
        }

        return original;
    }

    @ModifyReturnValue(method = {"reducingResistances"}, at = {@At("RETURN")}, remap = false)
    private static boolean NullToResistAndResistToNothing(boolean original, LivingEntity entity) {
        original = false;
        if (entity.hasEffect(MythosMobEffects.BLOOD_COAT.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.ORUNMILA.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.CHILD_OF_THE_PLANE.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.TRUE_DAO.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.PERSEVERANCE.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.BALANCE.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.PRETENDER_KING.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, (ManasSkill) Skills.FALSE_HERO.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, Skills.LUCIA.get())) {
            original = true;
        }
        if (isSkillInSlot(entity, Skills.KHONSU.get())) {
            original = true;
        }


        return original;
    }


    @ModifyReturnValue(method = {"hasWarpShot"}, at = {@At("RETURN")}, remap = false)
    private static boolean trmythos$hasWarpShot(boolean original, LivingEntity entity) {
        SkillStorage storage = SkillAPI.getSkillsFrom(entity);
        Optional<ManasSkillInstance> sagittarius = storage.getSkill((ManasSkill) Skills.SAGITTARIUS.get());
        if (sagittarius.isPresent()) {
            return true;
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = {"getMagiculeGain"}, at = {@At("RETURN")}, remap = false)
    private static float MythosMagiculeGain(float original, Player player, boolean majin) {
        if (hasSkill(player, (ManasSkill) Skills.NASCENT_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.AWAKENED_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.TRUE_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.ORIGIN_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.STARGAZER.get())) {
            original *= 2.0F;
        }
        if (hasSkill(player, (ManasSkill) Skills.FALSE_HERO.get())) {
            original *= 2.0F;
        }
        if (hasSkill(player, (ManasSkill) Skills.TENACIOUS.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.TENACIOUS.get())) {
                original *= 3;
            } else {
                original *= 2;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.PERSEVERANCE.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.PERSEVERANCE.get())) {
                original *= 6;
            } else {
                original *= 4;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.DOMINATE.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.DOMINATE.get())) {
                original *= 4;
            } else {
                original *= 3;
            }
        }
        if (hasSkill(player, (ManasSkill) ConfluenceUniques.CELESTIAL_PATH_BLUE.get())) {
            original += 10;
        }

        if (hasSkill(player, (ManasSkill) Skills.NPC_LIFE.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.NPC_LIFE.get())) {
                original *= 4;
            } else {
                original *= 2;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.PRETENDER_KING.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.PRETENDER_KING.get())) {
                original += 10;
            } else {
                original += 5;
            }
        }


        original += ChildOfThePlaneSkill.getChildOfThePlaneBoost(player, true, majin);
        original += BibliomaniaSkill.getBibliomaniaBoost(player, true, majin);
        return original;
    }

    @ModifyReturnValue(method = {"getAuraGain"}, at = {@At("RETURN")}, remap = false)
    private static float MythosAuraGain(float original, Player player, boolean majin) {
        if (hasSkill(player, (ManasSkill) Skills.NASCENT_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.AWAKENED_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.TRUE_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.ORIGIN_DAO.get())) {
            original = 0;
        }
        if (hasSkill(player, (ManasSkill) Skills.STARGAZER.get())) {
            original *= 2.0F;
        }
        if (hasSkill(player, (ManasSkill) Skills.TENACIOUS.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.TENACIOUS.get())) {
                original *= 3;
            } else {
                original *= 2;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.PERSEVERANCE.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.PERSEVERANCE.get())) {
                original *= 6;
            } else {
                original *= 4;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.DOMINATE.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.DOMINATE.get())) {
                original *= 4;
            } else {
                original *= 3;
            }
        }
        if (hasSkill(player, (ManasSkill) ConfluenceUniques.CELESTIAL_PATH_BLUE.get())) {
            original += 10;
        }
        if (hasSkill(player, (ManasSkill) Skills.NPC_LIFE.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.NPC_LIFE.get())) {
                original *= 4;
            } else {
                original *= 2;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.PRETENDER_KING.get())) {
            if (isSkillMastered(player, (ManasSkill) Skills.PRETENDER_KING.get())) {
                original += 10;
            } else {
                original += 5;
            }
        }
        if (hasSkill(player, (ManasSkill) Skills.FALSE_HERO.get())) {
            original *= 2.0F;
        }

        original += ChildOfThePlaneSkill.getChildOfThePlaneBoost(player, false, majin);
        original += BibliomaniaSkill.getBibliomaniaBoost(player, true, majin);
        return original;
    }

}

