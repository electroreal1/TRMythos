package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.manasmods.tensura.registry.items.TensuraToolItems;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.handler.BigTsunamiHandler;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.ScreenShakePacket;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

public class WavebreakerSkill extends Skill {
    public WavebreakerSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("You who command the waves, Ascend");
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Wavebreaker").withStyle(ChatFormatting.DARK_BLUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 80000;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, IntrinsicSkills.WATER_BREATHING.get());
            SkillUtils.learnSkill(entity, CommonSkills.WATER_CURRENT_CONTROL.get());
            SkillUtils.learnSkill(entity, CommonSkills.HYDRAULIC_PROPULSION.get());
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!instance.isToggled()) return;
        if (DamageSourceHelper.isWaterDamage(event.getSource())) {
            event.setAmount(instance.isMastered(entity) ? 6 : 4);
        }
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        SkillStorage storage = SkillAPI.getSkillsFrom(living);
        Skill earthshaker = Skills.EARTHSHAKER.get();
        if (living instanceof Player player) {
            if (TensuraPlayerCapability.isDemonLordSeed(player) || TensuraPlayerCapability.isHeroEgg(player)) {
                TensuraEPCapability.getFrom(player).filter(cap -> cap.getName() != null).ifPresent(cap -> {
                    storage.learnSkill(earthshaker);
                });
            }
        }
    }

    @Override
    public int modes() {
        return 3;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (instance.getMode() == 1) {
            ItemStack trident = new ItemStack(Items.TRIDENT);

            trident.enchant(Enchantments.IMPALING, 5);
            trident.enchant(Enchantments.LOYALTY, 3);
            trident.enchant(Enchantments.RIPTIDE, 3);
            trident.enchant(TensuraEnchantments.BARRIER_PIERCING.get(), 1);

            trident.setHoverName(Component.literal("Unnamed Trident"));
            if (!player.getInventory().add(trident)) {
                player.drop(trident, false);
            }

            instance.setCoolDown(60);
        } else if (instance.getMode() == 2) {
            if (player.level.isClientSide) return;

            ItemStack held = player.getMainHandItem();
            if (isUnnamedTrident(held)) return;

            boolean mastered = instance.isMastered(entity);
            float damage = mastered ? 400f : 200f;

            Level level = player.level;
            Vec3 look = player.getLookAngle().normalize();
            Vec3 origin = player.position().add(0, player.getEyeHeight(), 0);

            double range = 12.0;
            double width = 3.5;

            for (double i = 1; i <= range; i += 0.5) {
                Vec3 pos = origin.add(look.scale(i));

                AABB hitBox = new AABB(
                        pos.x - width, pos.y - width, pos.z - width,
                        pos.x + width, pos.y + width, pos.z + width
                );

                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, hitBox)) {
                    if (target == player) continue;

                    target.hurt(TensuraDamageSources.elementalAttack("tensura.water_attack", player, false), damage);
                    target.push(look.x * 0.6, 0.2, look.z * 0.6);
                }

                ((ServerLevel) level).sendParticles(TensuraParticles.SONIC_SOUND.get(), pos.x, pos.y, pos.z, 1,
                        0.4, 0.2, 0.4, 0.02);

                instance.setCoolDown(3);
            }
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!(entity instanceof Player player)) return false;
        if (player.level.isClientSide) return false;

        if (isUnnamedTrident(player.getMainHandItem())) return false;

        boolean mastered = instance.isMastered(entity);

        int castTime = mastered ? 2 : 10;

        if (heldTicks == 1 && !hasEnoughWater(player)) {
            player.displayClientMessage(Component.literal("Not enough surrounding water.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (heldTicks % 10 == 0) {
            ((ServerLevel) player.level).sendParticles(ParticleTypes.BUBBLE, player.getX(), player.getY(), player.getZ(), 20, 3, 1, 3, 0.05
            );
        }

        if (heldTicks >= castTime) {
            unleashTrace(instance, player);
            instance.setCoolDown(mastered ? 60 : 90);
            return true;
        }

        return false;
    }

    private boolean isUnnamedTrident(ItemStack stack) {
        if (stack.is(Items.TRIDENT)) {
            return true;
        } else if (stack.is(TensuraToolItems.VORTEX_SPEAR.get())) {
            return true;
        }
        if (!stack.hasCustomHoverName()) return false;
        return stack.getHoverName().getString().equals("Unnamed Trident");
    }

    private boolean hasEnoughWater(Player player) {
        Level level = player.level;
        BlockPos center = player.blockPosition();
        int count = 0;

        int radius = 6;

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -2, -radius), center.offset(radius, 2, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER) && state.getFluidState().isSource()) {
                count++;
                if (count >= 45) return true;
            }
        }
        return false;
    }

    private void unleashTrace(ManasSkillInstance instance, Player player) {
        Level level = player.level;
        BlockPos center = player.blockPosition();

        float damage = 1000f;
        double radius = 32.0;

        AABB area = new AABB(
                center.getX() - radius, center.getY() - 4, center.getZ() - radius,
                center.getX() + radius, center.getY() + 6, center.getZ() + radius
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {

            target.hurt(TensuraDamageSources.elementalAttack("tensura.water_attack", player, false), damage);
            target.hurt(TensuraDamageSources.elementalAttack("tensura.earth_attack", player, false), damage);

            Vec3 push = target.position().subtract(player.position()).normalize();
            target.push(push.x * 1.5, 0.6, push.z * 1.5);

            if (target instanceof ServerPlayer sp) {
                MythosNetwork.sendToPlayer(new ScreenShakePacket(80), sp);
            }
        }
        ServerLevel sLevel = (ServerLevel) player.level;
        Vec3 origin = player.position().add(0, 1, 0);
        Vec3 look = player.getLookAngle().normalize();
        new BigTsunamiHandler(sLevel, origin, look, 16, 8, 8, 100, instance.isMastered(player) ? 40 : 80);
        instance.setCoolDown(100);
    }
}
