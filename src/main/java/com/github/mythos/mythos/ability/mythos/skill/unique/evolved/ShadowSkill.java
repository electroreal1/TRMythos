package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.battlewill.MeleeArts;
import com.github.manasmods.tensura.registry.battlewill.UtilityArts;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Iterator;
import java.util.List;

public class ShadowSkill extends Skill {
    public ShadowSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.NPC_LIFE.get()) &&
                SkillUtils.isSkillMastered(player, (ManasSkill) UtilityArts.HAZE.get()) &&
                SkillUtils.isSkillMastered(player, (ManasSkill) MeleeArts.AURA_SWORD.get());
    }

    @Override
    public double getObtainingEpCost() {
        return 10000000;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        SkillUtils.learnSkill(living, ExtraSkills.STICKY_STEEL_THREAD.get());
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        instance.addMasteryPoint(entity);

        List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0), (living) -> {
            return !living.is(entity) && living.isAlive() && living.isAlliedTo(entity);
        });
        if (!list.isEmpty()) {
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                LivingEntity target = (LivingEntity)var5.next();
                if (SkillHelper.isSubordinate(target, entity)) {
                    return;
                }

                target.addEffect(new MobEffectInstance((MobEffect) MobEffects.DAMAGE_BOOST, 240, 10, false, false, false), entity);
                target.addEffect(new MobEffectInstance((MobEffect) MobEffects.DAMAGE_RESISTANCE, 240, 2, false, false, false), entity);
            }
        }
    }



    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (DamageSourceHelper.isPhysicalAttack(event.getSource())) {
            event.setAmount(event.getAmount() * (instance.isMastered(player) ? 5 : 4));
        }
        if (DamageSourceHelper.isFireDamage(event.getSource())) {
            event.setAmount(event.getAmount() * (instance.isMastered(player) ? 5 : 4));
        }
        if (DamageSourceHelper.isSpatialDamage(event.getSource())) {
            event.setAmount(event.getAmount() * (instance.isMastered(player) ? 5 : 4));
        }
        if (DamageSourceHelper.isGravityDamage(event.getSource())) {
            event.setAmount(event.getAmount() * (instance.isMastered(player) ? 5 : 4));
        }
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            PlayerList playerList = server.getPlayerList();


            ClientboundPlayerInfoPacket removePacket =
                    new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, player);


            MutableComponent leaveMessage = Component.translatable(
                    "multiplayer.player.left", player.getDisplayName()
            ).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));

            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(removePacket);
                target.sendSystemMessage(leaveMessage);
            }
        }
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            PlayerList playerList = server.getPlayerList();

            ClientboundPlayerInfoPacket addPacket =
                    new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, player);

            MutableComponent joinMessage = Component.translatable(
                    "multiplayer.player.joined", player.getDisplayName()
            ).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(addPacket);
                target.sendSystemMessage(joinMessage);
            }
        }
    }


}
