package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * DullahanHandler
 * If a Dullahan holds a gold tool, it is dropped immediately
 */
public final class DullahanHandler {

    /* ============================================================
     *  Demon Lord Seed
     * ============================================================ */
    @Mixin(value = TensuraPlayerCapability.class, priority = 900)
    public abstract static class DemonLordSeed {

        @ModifyReturnValue(
                method = "isDemonLordSeed()Z",
                at = @At("RETURN"),
                remap = false
        )
        private boolean mythos$dullahanIsDemonLordSeed(boolean original) {
            Entity owner = (Entity) (Object) this;
            return original || SkillUtils.hasSkill(
                    owner,
                    (ManasSkill) Skills.DULLAHAN.get()
            );
        }
    }

    /* ============================================================
     *  Drop Gold Tools When Held
     * ============================================================ */
    @Mixin(net.minecraft.world.entity.LivingEntity.class)
    public abstract static class GoldToolPenalty {

        private static boolean isGoldenTool(Item item) {
            return item == Items.GOLDEN_SWORD ||
                    item == Items.GOLDEN_PICKAXE ||
                    item == Items.GOLDEN_AXE ||
                    item == Items.GOLDEN_SHOVEL ||
                    item == Items.GOLDEN_HOE;
        }
        @Inject(method = "tick", at = @At("HEAD"))
        private void mythos$goldToolDebuffs(CallbackInfo ci) {
            LivingEntity entity = (LivingEntity)(Object)this;

            if (!(entity instanceof Player player)) return;

            if (player.level.isClientSide) return;

            if (!SkillUtils.hasSkill(player, (ManasSkill) Skills.DULLAHAN.get()))
                return;

            ItemStack held = player.getMainHandItem();
            boolean holdingGoldTool = !held.isEmpty() && isGoldenTool(held.getItem());

            if (holdingGoldTool) {

                // Mining Fatigue
                player.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN,
                        60,
                        1,
                        false,
                        false,
                        true
                ));

                // Slowness
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        60,
                        1,
                        false,
                        false,
                        true
                ));
            }
        }

        }
    }

