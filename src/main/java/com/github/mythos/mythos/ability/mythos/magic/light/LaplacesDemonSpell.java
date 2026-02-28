package com.github.mythos.mythos.ability.mythos.magic.light;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.magic.MagicElemental;
import com.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import com.github.mythos.mythos.handler.GlobalEffectHandler;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class LaplacesDemonSpell extends SpiritualMagic {
    public LaplacesDemonSpell() {
        super(null, SpiritLevel.LORD);
    }

    @Override
    public MagicElemental getElemental() {
        return MagicElemental.LIGHT;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player) || player.level.isClientSide) return;

        if (player.isShiftKeyDown()) {
            LivingEntity target = MythosUtils.getLookedAtEntity(player, 15);
            if (target != null) {
                GlobalEffectHandler.toggleOverload(player, target);
            }
            return;
        }

        if (!instance.getOrCreateTag().contains("TargetUUID")) {
            LivingEntity target = MythosUtils.getLookedAtEntity(player, 20);
            if (target != null) {
                instance.getOrCreateTag().putUUID("TargetUUID", target.getUUID());
                player.displayClientMessage(Component.literal("§b[Laplace] §7Subject selected. Define vector..."), true);
            }
        } else {
            HitResult ray = player.pick(20.0D, 0.0F, false);
            if (ray.getType() == HitResult.Type.BLOCK) {
                GlobalEffectHandler.startForcedPath(player, instance.getOrCreateTag().getUUID("TargetUUID"), ((BlockHitResult)ray).getLocation());
                instance.getOrCreateTag().remove("TargetUUID");
            }
        }
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("I probably work.");
    }
}
