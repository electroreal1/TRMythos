package com.github.mythos.mythos.item;

import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.item.TensuraCreativeTab;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.TensuraRarity;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class voidHeart extends Item {
    public voidHeart() {
        super((new Item.Properties()).tab(TensuraCreativeTab.MISCELLANEOUS).rarity(TensuraRarity.UNIQUE).stacksTo(1));
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.literal("Void Heart");
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("A heart that seems to belong to none existence, I wonder what happens if you implant it..").withStyle(ChatFormatting.GRAY));
    }

    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    public int getUseDuration(ItemStack pStack) {
        return 10000;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CROSSBOW;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
        ItemStack stack = player.getItemInHand(pHand);
        if (player.getCooldowns().isOnCooldown(stack.getItem()) && !player.getAbilities().instabuild) {
            return InteractionResultHolder.fail(stack);
        } else {
            player.startUsingItem(pHand);
            return InteractionResultHolder.consume(stack);
        }
    }

    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        TensuraParticleHelper.addServerParticlesAroundSelf(pLivingEntity, ParticleTypes.ENCHANTED_HIT);
    }

    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level level, @NotNull LivingEntity entity, int pTimeLeft) {
        int useTicks = this.getUseDuration(pStack) - pTimeLeft;
        if (useTicks >= 10) {
            if (entity instanceof Player player) {
                if (!player.getAbilities().instabuild) {
                    player.hurt(TensuraDamageSources.OUT_OF_ENERGY, player.getMaxHealth() / 2.0F);
                    player.getCooldowns().addCooldown(pStack.getItem(), 2400);
                    pStack.shrink(1);
                }

                MythosUtils.setEmpty(player, true);
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    SkillHelper.outOfMagiculeStillConsume(player, cap.getMagicule() - 200.0);
                    TensuraPlayerCapability.sync(player);
                });
                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER, 0.0);
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticles.SOLAR_FLASH.get(), 1.0);
            }

        }
    }
}
