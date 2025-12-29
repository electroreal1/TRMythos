package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.ISpatialStorage;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HoarderSkill extends Skill implements ISpatialStorage {

    public HoarderSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public @NotNull SpatialStorageContainer getSpatialStorage(@NotNull ManasSkillInstance instance) {
        SpatialStorageContainer container = new SpatialStorageContainer(99, 99);
        container.fromTag(instance.getOrCreateTag().getList("Hoarder_Storage", 10));
        return container;
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setBlessed(true);
                });
                TensuraPlayerCapability.sync(player);
            }

        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (instance.isToggled()) {
            if (!(living instanceof Player player)) return;
            if (player.level.isClientSide) return;

            int range = instance.isMastered(player) ? 10 : 5;

            AABB area = player.getBoundingBox().inflate(range);

            List<ItemEntity> items = player.level.getEntitiesOfClass(ItemEntity.class, area, item -> !item.hasPickUpDelay());

            for (ItemEntity item : items) {
                Vec3 itemPos = item.position();
                Vec3 playerPos = player.position();

                Vec3 direction = playerPos.subtract(itemPos);

                double distance = direction.length();
                if (distance < 0.5) continue;

                Vec3 motion = direction.normalize().scale(0.08);

                item.setDeltaMovement(
                        item.getDeltaMovement().add(motion)
                );

                item.hasImpulse = true;
            }
        }

        if (!(living instanceof Player player)) return;
        if (player.level.isClientSide) return;

        int range = 8;
        AABB area = player.getBoundingBox().inflate(range);
        boolean mastered = instance.isMastered(player);
        List<LivingEntity> entities = player.level.getEntitiesOfClass(LivingEntity.class, area, e -> e.isAlive());

        for (LivingEntity target : entities) {
            if (mastered && target == player) continue;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, true, false));
        }
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event, ManasSkillInstance instance) {
        Player player = event.getEntity();
        ItemEntity itemEntity = event.getItem();
        ItemStack incoming = itemEntity.getItem();

        if (instance == null || instance.isMastered(player)) return;

        if (!isEverythingFull(player, instance)) return;

        if (!player.addItem(incoming.copy())) return;

        itemEntity.discard();

        if (player.getRandom().nextBoolean()) {
            dropRandomInventoryItem(player);
        } else {
            dropRandomSpatialItem(instance, player);
        }
    }

    public void dropRandomSpatialItem(ManasSkillInstance instance, Player player) {
        SpatialStorageContainer container = this.getSpatialStorage(instance);
        if (container.isEmpty()) return;

        List<Integer> filledSlots = new ArrayList<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty()) {
                filledSlots.add(i);
            }
        }

        if (filledSlots.isEmpty()) return;

        int slot = filledSlots.get(player.getRandom().nextInt(filledSlots.size()));
        ItemStack stack = container.getItem(slot);

        ItemStack dropped = stack.split(1);

        if (stack.isEmpty()) {
            container.setItem(slot, ItemStack.EMPTY);
        }

        ItemEntity entity = player.drop(dropped, false);
        if (entity != null) {
            entity.setDeltaMovement(player.getRandom().nextGaussian() * 0.05, 0.15, player.getRandom().nextGaussian() * 0.05);
        }

        instance.markDirty();
    }

    public static void dropRandomInventoryItem(Player player) {
        List<Integer> filledSlots = new ArrayList<>();

        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (!player.getInventory().items.get(i).isEmpty()) {
                filledSlots.add(i);
            }
        }

        if (filledSlots.isEmpty()) return;

        int slot = filledSlots.get(player.getRandom().nextInt(filledSlots.size()));
        ItemStack stack = player.getInventory().items.get(slot);

        ItemStack dropped = stack.split(1);

        if (stack.isEmpty()) {
            player.getInventory().items.set(slot, ItemStack.EMPTY);
        }

        ItemEntity entity = player.drop(dropped, false);
        if (entity != null) {
            entity.setDeltaMovement(player.getRandom().nextGaussian() * 0.05, 0.15, player.getRandom().nextGaussian() * 0.05);
        }
    }

    public boolean isEverythingFull(Player player, ManasSkillInstance instance) {
        return isInventoryFull(player)
                && isSpatialFull(instance);
    }

    public static boolean isInventoryFull(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) return false;
        }
        return true;
    }

    public boolean isSpatialFull(ManasSkillInstance instance) {
        for (int i = 0; i < this.getSpatialStorage(instance).getContainerSize(); i++) {
            if (this.getSpatialStorage(instance).getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 2 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 2) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (entity.isShiftKeyDown()) {
                openSpatialStorage(entity, instance);
                return;
            } else {
                SpatialStorageContainer container = this.getSpatialStorage(instance);
                int radius = instance.isMastered(entity) ? 10 : 5;

                AABB box = new AABB(
                        entity.getX() - radius, entity.getY() - 2, entity.getZ() + radius,
                        entity.getX() + radius, entity.getY() + 2, entity.getZ() + radius
                );

                List<ItemEntity> items = entity.getLevel().getEntitiesOfClass(ItemEntity.class, box);

                for (ItemEntity entity1 : items) {
                    ItemStack stack = entity1.getItem();
                    if (stack.isEmpty()) continue;

                    ItemStack remaining = container.addItem(stack);

                    if (remaining.isEmpty()) {
                        entity1.discard();
                    } else {
                        entity1.setItem(remaining);
                    }
                }

                instance.markDirty();
                instance.setCoolDown(10);
            }
        }

        if (instance.getMode() == 2) {
            ItemStack stack = entity.getMainHandItem();

            if (stack.isEmpty()) return;

            if (instance.isMastered(entity)) {
                stack.enchant(TensuraEnchantments.TSUKUMOGAMI.get(), 1);
            } else {
                stack.enchant(Enchantments.LOYALTY, 1);
            }

            instance.markDirty();
            instance.addMasteryPoint(entity);
            instance.setCoolDown(10);
        }

    }

}
