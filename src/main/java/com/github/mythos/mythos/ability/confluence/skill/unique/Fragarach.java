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
import com.github.mythos.mythos.registry.MythosItems;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
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

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event) {
        if (this.isInSlot(attacker)) {
            float chance = instance.isMastered(attacker) ? 0.75F : 0.5F;
            if (!(attacker.getRandom().nextFloat() > chance)) {
                if (SkillHelper.drainMP(event.getEntity(), attacker, 0.01, true) && attacker instanceof Player) {
                    Player player = (Player)attacker;
                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

            }
        }
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
    public static void onHurt(LivingHurtEvent event, LivingEntity entity, Player player) {
        if (!(event.getSource().getEntity() instanceof LivingEntity living)) return;
        boolean holdingFragarach = isHoldingFragarach(player);

        if (!TensuraSkillCapability.isSkillInSlot(living, (ManasSkill) ConfluenceUniques.FRAGARACH.get()) && !holdingFragarach) return;

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

        instance.getOrCreateTag().putBoolean("FragarachCreated", true);
        instance.setCoolDown(100);
    }

    private void spawnDummySword(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand) {
        if (entity.level.isClientSide) return;
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        ItemStack blade = new ItemStack(MythosItems.FRAGARACH.get());

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

    private static boolean isHoldingFragarach(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        return main.getItem() == MythosItems.FRAGARACH.get() || off.getItem() == MythosItems.FRAGARACH.get();
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
        double yOffset = 1.2;

        for (int i = 0; i < 5; i++) {
            if (rand.nextDouble() < 0.3) continue;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.5 + rand.nextDouble() * 0.5;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + rand.nextDouble() * 0.5;
            float size = 0.6f + rand.nextFloat() * 0.3f;
            server.sendParticles(new DustParticleOptions(new Vector3f(0.7f, 0.9f, 1f), size), px, py, pz, 1, 0, 0.03, 0, 0.01);
        }

        for (int i = 0; i < 8; i++) {
            if (rand.nextDouble() < 0.5) continue;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.3;
            float size = 0.5f + rand.nextFloat() * 0.3f;
            server.sendParticles(new DustParticleOptions(new Vector3f(0.8f, 1f, 1f), size), px, py, pz, 1, 0, 0, 0, 0);
        }

        for (int i = 0; i < 10; i++) {
            if (rand.nextDouble() < 0.4) continue;
            double angle = i * 2 * Math.PI / 10 + player.level.random.nextDouble() * 0.5;
            double radius = 0.4 + Math.sin(rotation * 2 + i) * 0.2;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 3 + i) * 0.15;
            float size = 0.4f + rand.nextFloat() * 0.2f;
            server.sendParticles(new DustParticleOptions(new Vector3f(0.6f, 0.85f, 1f), size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }
}
