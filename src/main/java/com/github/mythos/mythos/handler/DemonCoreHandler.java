package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.daemon.*;
import com.github.mythos.mythos.registry.MythosItems;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class DemonCoreHandler {

    public void onPlayerDeath(LivingDeathEvent event) {

        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof Player player)) return;

        SkillStorage attackerStorage = SkillAPI.getSkillsFrom(player);
        if (attackerStorage == null || attackerStorage.getSkill(Skills.DEMONOLOGIST.get()).isEmpty()) return;

        if (!(event.getEntity() instanceof Player deadPlayer)) return;

        Race deadPlayerRace = TensuraPlayerCapability.getRace((LivingEntity)deadPlayer);

        Level level = player.level;
        if (level.isClientSide) return;

        ItemStack core = new ItemStack(MythosItems.DEMON_CORE.get());
        ItemEntity coreEntity = new ItemEntity(level, deadPlayer.getX(), deadPlayer.getY() + 0.5, deadPlayer.getZ(), core);

        coreEntity.setPickUpDelay(0);

        if (deadPlayerRace.getClass() == LesserDaemonRace.class) {
            level.addFreshEntity(coreEntity);
        } else if (deadPlayerRace.getClass() == GreaterDaemonRace.class) {
            level.addFreshEntity(coreEntity);
        } else if (deadPlayerRace.getClass() == ArchDaemonRace.class) {
            level.addFreshEntity(coreEntity);
        } else if (deadPlayerRace.getClass() == DaemonLordRace.class) {
            level.addFreshEntity(coreEntity);
        } else if (deadPlayerRace.getClass() == DevilLordRace.class) {
            level.addFreshEntity(coreEntity);
        } else {
            return;
        }
    }





}
