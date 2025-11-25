package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.mythos.mythos.registry.MythosItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class Catharsis extends Skill {
    public static final UUID CATHARSIS = UUID.fromString("db72dacd-39c1-4539-8816-11536820d3ec");

    public Catharsis(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 500000;
    }

    @Override
    public double learningCost() {
        return 10000;
    }

    @Override
    public int modes() {
        return 1;
    }

    @Override
    public Component getModeName(int mode) {
        return Component.translatable("trmythos.skill.catharsis.catharsis");
    }


    private boolean activatedCatharsisSeverance(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (tag.getInt("CathSev") < 100) {
            return false;
        } else {
            return instance.isMastered(entity) && instance.isToggled() ? true : tag.getBoolean("CathSevActivated");
        }
    }

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event, Player player) {
        CompoundTag tag = instance.getOrCreateTag();
        if (!isHoldingCatharsis(player)) return;
        if (this.activatedCatharsisSeverance(instance, entity)) {
            LivingEntity target = event.getEntity();
            AttributeInstance soulHealth = target.getAttribute((Attribute)TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
            AttributeModifier catharsisModifier;

            double baseAmount = event.getAmount();
            double hpSeverPercent = instance.isMastered(entity) ? 0.07 : 0.05;
            double shpSeverPercent = instance.isMastered(entity) ? 0.01 : 0.0;

            if (instance.isMastered(entity)) {
                if (soulHealth != null) {
                    double amount = baseAmount * shpSeverPercent;
                    catharsisModifier = soulHealth.getModifier(CATHARSIS);
                    if (catharsisModifier != null) {
                        amount -= catharsisModifier.getAmount();
                    }

                    catharsisModifier = new AttributeModifier(CATHARSIS, "Catharsis", amount * -1.0, AttributeModifier.Operation.ADDITION);
                    soulHealth.removeModifier(catharsisModifier);
                    soulHealth.addPermanentModifier(catharsisModifier);
                    if (!instance.isMastered(entity) || !instance.isToggled()) {
                        tag.putBoolean("CathSevActivated", false);
                    }

                    this.addMasteryPoint(instance, entity);
                    entity.getLevel().playSound((Player) null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.AMBIENT, 1.0F, 1.0F);
                }
            }
            AttributeInstance health = target.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                double amount = baseAmount * hpSeverPercent;
                catharsisModifier = health.getModifier(CATHARSIS);
                if (catharsisModifier != null) {
                    amount -= catharsisModifier.getAmount();
                }

                AttributeModifier attributemodifier = new AttributeModifier(CATHARSIS, "Catharsis", amount * -1.0, AttributeModifier.Operation.ADDITION);
                health.removeModifier(attributemodifier);
                health.addPermanentModifier(attributemodifier);
                if (!instance.isMastered(entity) || !instance.isToggled()) {
                    tag.putBoolean("CathSevActivated", false);
                }

                this.addMasteryPoint(instance, entity);
                entity.getLevel().playSound((Player)null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.AMBIENT, 1.0F, 1.0F);
            }
        }

    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        if (instance.getOrCreateTag().getBoolean("CatharsisCreated")) {
            if (entity instanceof Player player) {
                player.displayClientMessage(
                        Component.literal("You have already created Catharsis.")
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                        false
                );
                instance.setCoolDown(instance.isMastered(player) ? 15 : 30);
            }
            return;
        }

        boolean given = false;

        if (entity.getMainHandItem().isEmpty()) {
            spawnDummySword(instance, entity, InteractionHand.MAIN_HAND);
            given = true;
        } else if (entity.getOffhandItem().isEmpty()) {
            spawnDummySword(instance, entity, InteractionHand.OFF_HAND);
            given = true;
        } else if (entity instanceof Player player) {
            ItemStack blade = new ItemStack(MythosItems.FRAGARACH.get());
            if (player.getInventory().add(blade)) {
                player.inventoryMenu.broadcastChanges();
                player.swing(InteractionHand.MAIN_HAND, true);
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
                given = true;
            } else {
                player.displayClientMessage(
                        Component.literal("Both hands and inventory are full!")
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                        false
                );
            }
        }

        if (!given) return;

        instance.getOrCreateTag().putBoolean("CatharsisCreated", true);
        instance.setCoolDown(100);
    }

    private void spawnDummySword(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand) {
        if (entity.level.isClientSide) return;
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        ItemStack blade = new ItemStack(MythosItems.CATHARSIS.get());

        Enchantment tsuku = TensuraEnchantments.TSUKUMOGAMI.get();
        EngravingEnchantment.engrave(blade, tsuku, 1);

        entity.setItemInHand(hand, blade);
        entity.swing(hand, true);
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (entity instanceof ServerPlayer player) {
            player.inventoryMenu.broadcastChanges();
        }
    }

    private static boolean isHoldingCatharsis(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        return main.getItem() == MythosItems.CATHARSIS.get() || off.getItem() == MythosItems.CATHARSIS.get();
    }

}