package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.registry.MythosItems;
import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Gram extends Skill {
    public Gram(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 250000;
    }

    private static boolean isHoldingGram(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        return main.getItem() == MythosItems.GRAM.get() || off.getItem() == MythosItems.GRAM.get();
    }
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event, ManasSkillInstance instance, LivingEntity living) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) ConfluenceUniques.GRAM.get())) {
            if (!isHoldingGram(player)) return;
            LivingEntity target = event.getEntity();
            double playerEP = TensuraPlayerCapability.getCurrentEP(player);

            double targetEP;
            if (target instanceof Player targetPlayer) {
                targetEP = TensuraPlayerCapability.getCurrentEP(targetPlayer);
            } else {
                targetEP = target.getPersistentData().getDouble("EP");
            }

            if (targetEP <= playerEP) return;

            double epDifferencePercent = ((targetEP - playerEP) / playerEP) * 100.0;
            double damageMultiplier = 1.0 + (epDifferencePercent / 100.0);

            String targetRace = String.valueOf(TensuraPlayerCapability.getRace(target));
            if (targetRace != null && MythosSkillsConfig.GRAM_EXTRA_DAMAGE_RACES.get().contains(targetRace)) {
                damageMultiplier *= 1.5;
            }


            float newDamage = (float) (event.getAmount() * damageMultiplier);
            event.setAmount(newDamage);
            instance.addMasteryPoint(player);
        }
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }
    private static double rotation = 0;
    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        Level level = entity.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int shards = 12;
        int streaks = 8;
        double yOffset = 1.2;

        for (int i = 0; i < shards; i++) {
            double angle = i * 2 * Math.PI / shards + rotation;
            double radius = 0.7 + Math.sin(rotation * 2 + i) * 0.15;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 2 + i) * 0.2;
            float size = 0.7f + rand.nextFloat() * 0.2f;
            Vector3f color = rand.nextDouble() < 0.7 ? new Vector3f(0f, 0.8f, 0f) : new Vector3f(0.3f, 1f, 0.5f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }

        for (int i = 0; i < streaks; i++) {
            double px = player.getX() + (rand.nextDouble() - 0.5) * 1.2;
            double pz = player.getZ() + (rand.nextDouble() - 0.5) * 1.2;
            double py = player.getY() + yOffset;
            double length = 0.5 + rand.nextDouble() * 0.7;
            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(0f, 0.6f + (float)rand.nextDouble() * 0.4f, 0f);
            for (int j = 0; j < 3; j++) {
                server.sendParticles(new DustParticleOptions(color, size),
                        px, py + j * 0.15, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }


}
