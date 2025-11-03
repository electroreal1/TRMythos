package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.enchantment.EngravingEnchantment;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosWeapons;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Fragarach extends Skill {

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("e6f95f20-8c4b-4b6d-9f2b-1b2e90c0b5f1");
    private static final double SPEED_MULTIPLIER = 1.5;
    public Fragarach(SkillType type) {
        super(type);
    }

    @Override
    public boolean canBeToggled(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        if (Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED))
                .getModifier(SPEED_MODIFIER_UUID) == null) {
            AttributeModifier modifier = new AttributeModifier(
                    SPEED_MODIFIER_UUID,
                    "Fragarch Speed Boost",
                    SPEED_MULTIPLIER,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(modifier);
        }
    }

    public void onToggleOff(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        AttributeModifier modifier = Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED))
                .getModifier(SPEED_MODIFIER_UUID);
        if (modifier != null) {
            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(modifier);
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.THOUGHT_ACCELERATION.get());
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event, LivingEntity entity) {
        if (!(event.getSource().getEntity() instanceof LivingEntity living)) return;

        if (!TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) ConfluenceUniques.FRAGARACH.get())) return;

        LivingEntity target = event.getEntity();

        float trueDamage = event.getAmount() * 0.10f;

        target.hurt(MythosDamageSources.truthDamage(entity), trueDamage);
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }

        tag.putInt("activatedTimes", time + 1);
    }

    @Override
    public int modes() {
        return 1;
    }
    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.fragarach.fragarach");
                break;
            default:
                var10000 = Component.empty();
        }
        return var10000;
    }

    @Override
    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        return 7777.0;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;
        if (SkillHelper.outOfMagicule(entity, instance)) return;
        if (MythosWeapons.FRAGARACH.get() == null) return;

        if (instance.getOrCreateTag().getBoolean("FragarachCreated")) {
            if (entity instanceof Player player) {
                player.displayClientMessage(
                        Component.literal("You have already created Fragarach.")
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
            ItemStack blade = new ItemStack(MythosWeapons.FRAGARACH.get());
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

        instance.getOrCreateTag().putBoolean("FragarachCreated", true);
        instance.setCoolDown(100);
    }

    private void spawnDummySword(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand) {
        if (entity.level.isClientSide) return;
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        ItemStack blade = new ItemStack(MythosWeapons.FRAGARACH.get());

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
}
