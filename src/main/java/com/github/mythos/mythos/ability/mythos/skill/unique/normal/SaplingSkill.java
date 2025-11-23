package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SaplingSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("ff31f481-901a-4415-8928-d44ec300ce86");

    public SaplingSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }


    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                if (TensuraEPCapability.isMajin(player)) {
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setBlessed(true);
                    cap.setDemonLordSeed(true);
                });
                } else if (!TensuraEPCapability.isMajin(player)) {
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setBlessed(true);
                    cap.setHeroEgg(true);
                });
                }
                TensuraPlayerCapability.sync(player);
            }

        }
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        if (!instance.isToggled()) return;

        if (!(entity instanceof Player player)) return;

        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
            double regenPerTick = (maxMP * 0.01) / 20.0;

            if (instance.isMastered(entity)) {
                regenPerTick *= 2;
            }

            cap.setMagicule(Math.min(cap.getMagicule() + regenPerTick, maxMP));
        });

        TensuraPlayerCapability.sync(player);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.sapling.engrave");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.sapling.power");
                break;
            default:
                var10000 = Component.empty();
        }
        return var10000;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1 && !entity.getMainHandItem().isEmpty()) {
            this.engrave(instance, entity, InteractionHand.MAIN_HAND);
            instance.setCoolDown(instance.isMastered(entity) ? 60 : 120);
            this.addMasteryPoint(instance, entity);
        }
    }

    private void engrave(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand) {
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        addMasteryPoint(instance, entity);
        ItemStack item = entity.getItemInHand(hand);
        if (item.isEmpty()) return;

        List<? extends String> configuredEnchantments = (List<? extends String>) MythosSkillsConfig.SAPLING_ENGRAVE_LIST;
        if (configuredEnchantments == null || configuredEnchantments.isEmpty()) return;


        String entry = configuredEnchantments.get(entity.getRandom().nextInt(configuredEnchantments.size()));
        String[] parts = entry.split(":");
        if (parts.length != 4) return;

        String enchantId = parts[0] + ":" + parts[1];
        int minLevel;
        int maxLevel;
        try {
            minLevel = Integer.parseInt(parts[2]);
            maxLevel = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            return;
        }

        Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantId));
        if (enchant == null) return;

        int level = instance.isMastered(entity) ? maxLevel : minLevel;
        if (level <= 0) return;

        if (item.getEnchantmentLevel(enchant) <= 0) {
            item.enchant(enchant, level);
        }

        entity.setItemInHand(hand, item);
        entity.swing(hand);
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }


}
