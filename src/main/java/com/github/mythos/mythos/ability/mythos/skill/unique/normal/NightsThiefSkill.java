package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NightsThiefSkill extends Skill {
    private static final UUID DAMAGE_MODIFIER_ID = UUID.fromString("370a732d-374b-4721-b312-29d5f0f3cf88");
    private static final UUID ARMOR_MODIFIER_ID = UUID.fromString("c82be970-bd37-4b0e-af79-960a2ad8898b");


    public NightsThiefSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        int radius = instance.isMastered(entity) ? 10 : 5;
        if (!(entity instanceof Player player)) return;
        AABB box = new AABB(
                entity.getX() - radius, entity.getY() - 2, entity.getZ() + radius,
                entity.getX() + radius, entity.getY() + 2, entity.getZ() + radius
        );

        List<ItemEntity> items = entity.getLevel().getEntitiesOfClass(ItemEntity.class, box);

        for (ItemEntity entity1 : items) {
            ItemStack stack = entity1.getItem();
            if (stack.isEmpty()) {
                return;
            }
        }

        if (instance.isToggled()) {
            entity.addEffect(new MobEffectInstance(MysticismMobEffects.TREASURE_DETECTION.get(), 10, 1, false, false, false));


            int preciousCount = 0;
            for (ItemStack stack : player.getInventory().items) {
                if (isPrecious(stack)) {
                    preciousCount += stack.getCount();
                }
            }

            double damageBonus = Math.min(0.1 * preciousCount, 300);
            double armorBonus = Math.min(preciousCount, 100);

            if (damageBonus > 0) {
                Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).addTransientModifier(
                        new AttributeModifier(DAMAGE_MODIFIER_ID, "HeavyPouchDamage", damageBonus, AttributeModifier.Operation.ADDITION)
                );
            }

            if (armorBonus > 0) {
                Objects.requireNonNull(player.getAttribute(Attributes.ARMOR)).addTransientModifier(
                        new AttributeModifier(ARMOR_MODIFIER_ID, "HeavyPouchArmor", armorBonus, AttributeModifier.Operation.ADDITION)
                );
            }
        }
    }


    private static boolean isPrecious(ItemStack stack) {
        Item item = stack.getItem();

        boolean isOre = stack.getTags().anyMatch(tag -> tag.location().toString().equals("forge:ores")) ||
                stack.getTags().anyMatch(tag -> tag.location().toString().equals("forge:ingot"));

        return isOre || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.EMERALD;
    }

    @Override
    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (entity instanceof Player targetPlayer) {

                int oreCount = 0;
                for (ItemStack stack : targetPlayer.getInventory().items) {
                    if (isPrecious(stack)) {
                        oreCount += stack.getCount();
                    }
                }

                double totalDamage = 50 + oreCount;

                targetPlayer.hurt(DamageSource.mobAttack(entity), (float) totalDamage);

                instance.setCoolDown(20);
            }
        }
        if (instance.getMode() == 3) {
            if (entity.isDeadOrDying()) return;

            if (!(entity instanceof Player player)) return;

            ItemStack targetStack = player.getMainHandItem();

            if (targetStack.isEmpty()) return;

            ItemStack stolenItem = targetStack.copy();
            stolenItem.setCount(1);

            targetStack.shrink(1);

            if (targetStack.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }

            if (!player.getInventory().add(stolenItem)) {
                player.drop(stolenItem, false);
            }
            instance.setCoolDown(300);
        }
    }


    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity living, int heldTicks) {
        if (!(living instanceof Player player)) return false;
        if (!(instance.getMode() == 2)) return false;

        int oreCount = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (isPrecious(stack)) {
                oreCount += stack.getCount();
            }
        }

        if (oreCount == 0) return false;

        int durationTicks = oreCount * 3 * 20;
        MobEffectInstance invis = player.getEffect(TensuraMobEffects.PRESENCE_CONCEALMENT.get());

        if (invis == null || invis.getDuration() < durationTicks) {
            player.addEffect(new MobEffectInstance(TensuraMobEffects.PRESENCE_CONCEALMENT.get(), durationTicks, 2, false, false));
        }

        return true;
    }
}
