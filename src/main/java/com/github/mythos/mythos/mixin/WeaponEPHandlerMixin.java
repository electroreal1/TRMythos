package com.github.mythos.mythos.mixin;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.handler.WeaponEPHandler;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;

@Mixin({WeaponEPHandler.class})
public class WeaponEPHandlerMixin {


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected static void entityGetEP(LivingEntity killer, LivingEntity target, double totalEP) {
        double maxMPGain = killer.level.getGameRules().getInt(TensuraGameRules.MAX_MP_GAIN);
        double maxAPGain = killer.level.getGameRules().getInt(TensuraGameRules.MAX_AP_GAIN);

        boolean hasSoulGrantingSkill =
                SkillAPI.getSkillsFrom(killer).getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry().getValue(Skills.UNDERWORLD_PRINCE.getId()))).isPresent() ||
                        SkillAPI.getSkillsFrom(killer).getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry().getValue(Skills.ALCHEMIST.getId()))).isPresent() ||
                        SkillAPI.getSkillsFrom(killer).getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry().getValue(Skills.ARES.getId()))).isPresent() ||
                        SkillAPI.getSkillsFrom(killer).getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry().getValue(Skills.DULLAHAN.getId()))).isPresent() ||
                        SkillAPI.getSkillsFrom(killer).getSkill(Objects.requireNonNull(SkillAPI.getSkillRegistry()
                                .getValue(Skills.CRACKED_PHILOSOPHER_STONE.getId()))).isPresent();

        if (killer instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap -> {
                if (cap.isDemonLordSeed() || hasSoulGrantingSkill) {
                    double soulsGained= SkillUtils.getEPGain(
                            target, killer, false) * TensuraConfig.INSTANCE.awakeningConfig.epToSoulRate.get() / 100;

                    soulsGained = Math.min(soulsGained, maxMPGain + maxAPGain);

                    int currentSouls = cap.getSoulPoints();
                    cap.setSoulPoints((int) Math.min((double) currentSouls + soulsGained, 2.147483647E9));

                    if (player.level.getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE) && cap.getSoulPoints() >= 20000000) {
                        if (SkillUtils.learnSkill(player, UniqueSkills.MERCILESS.get())) {
                            player.displayClientMessage(Component.translatable("tensura.skill.acquire", UniqueSkills.MERCILESS.get()
                                    .getName()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                        }
                    }
                }

                Race race = cap.getRace();
                if (race != null) {
                    boolean isMajin = TensuraEPCapability.isMajin(killer);
                    double mpGain = Math.min(totalEP * (double) SkillUtils.getMagiculeGain(player, isMajin), maxMPGain);
                    double apGain = Math.min(totalEP * (double) SkillUtils.getAuraGain(player, isMajin), maxAPGain);

                    cap.setBaseMagicule(cap.getBaseMagicule() + mpGain, player);
                    cap.setBaseAura(cap.getBaseAura() + apGain, player);
                    TensuraPlayerCapability.sync(player);
                }
            }));
            TensuraEPCapability.updateEP(player);
        } else if (totalEP * (double) TensuraGameRules.getEPGain(killer.level) >= 1.0) {
            double gainedEP = Math.min(totalEP * (double) TensuraGameRules.getEPGain(killer.level), maxMPGain + maxAPGain);
            TensuraEPCapability.setLivingEP(killer, (double) Math.round(TensuraEPCapability.getEP(killer) + gainedEP));

            LivingEntity owner = SkillHelper.getSubordinateOwner(killer);
            if (owner instanceof Player playerOwner) {
                TensuraPlayerCapability.getFrom(playerOwner).ifPresent((cap) -> {
                    if (cap.isDemonLordSeed() || hasSoulGrantingSkill) {
                        double soulRate = TensuraConfig.INSTANCE.awakeningConfig.epToSoulRate.get() / 100.0;
                        double newSoulTotal = (double) cap.getSoulPoints() + Math.min(totalEP * soulRate, maxMPGain + maxAPGain) * 1;
                        cap.setSoulPoints((int) Math.min(newSoulTotal, 2.147483647E9));
                        TensuraPlayerCapability.sync(playerOwner);
                    }
                });
            }

        }
    }
}
