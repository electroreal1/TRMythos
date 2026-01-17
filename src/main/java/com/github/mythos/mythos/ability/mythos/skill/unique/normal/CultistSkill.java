package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.data.TensuraTags;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.enchantment.TensuraEnchantments;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class CultistSkill extends Skill {
    public CultistSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    private static final String SP_KEY = "sacrifice_points";
    private static final UUID SACRIFICE_BUFF_ID = UUID.fromString("4a83b264-6f3f-47c1-bc2e-f86ac7c8ac56");

    @Override
    public double learningCost() {
        return 75000;
    }

    @Override
    public int getMaxMastery() {
        return 1000;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("§5§lCultist");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("§dThis is §5worship§d! §4Desire§d! §5Obsession§d in its purest of forms! §cManifested§d!");
    }

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/cultist.png");
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.isToggled()) {
            int amplifier = instance.isMastered(entity) ? 5 : 3;
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSANITY.get(), 200, amplifier - 1, false, false, true));
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) {
            return instance.getMode() == 1 ? 2 : instance.getMode() - 1;
        } else {
            return instance.getMode() == 2 ? 1 : instance.getMode() + 1;
        }
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1 -> var10000 = Component.literal("Yield");
            case 2 -> var10000 = Component.literal("Bless");
            default -> var10000 = Component.empty();
        }

        return var10000;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof Player player) {
            switch (instance.getMode()) {
                case 1 -> this.yield(player, instance);
                case 2 -> this.bless(player, instance);
            }

        }
    }

    private void yield(Player player, ManasSkillInstance instance) {
        double hp = player.getHealth();
        double shp = TensuraEPCapability.getSpiritualHealth(player);
        double percent = instance.isMastered(player) ? 0.15 : 0.25;

        float healthDamage = (float) Math.min(hp - 0.1, hp * percent);
        float soulDamage = (float) Math.min(shp, shp * percent);

        if (healthDamage > 0.0f) {
            player.hurt(DamageSource.GENERIC, healthDamage);
        }

        if (soulDamage > 0.0f) {
            DamageSourceHelper.directSpiritualHurt(player, player, soulDamage, 0);
        }

        CompoundTag tag = instance.getOrCreateTag();
        int sp = tag.getInt("sacrifice_points");
        tag.putInt("sacrifice_points", sp + 1);
        player.swing(InteractionHand.MAIN_HAND, true);
        player.displayClientMessage(Component.literal("§cYou offered blood and soul. SP is now §4{0}" + (sp + 1)), true);
        TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.FLAME, 1.0);
        TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.SMOKE, 1.0);
    }

    private void bless(Player player, ManasSkillInstance instance) {
        ItemStack held = player.getMainHandItem();

        if (held.isEmpty()) {
            player.displayClientMessage(Component.literal("§7You must hold an item to bless it."), true);
        } else {
            int currentLevel = this.getSoulEaterLevel(held);
            int max = instance.isMastered(player) ? 5 : 3;

            if (currentLevel >= max) {
                player.displayClientMessage(Component.literal("§7This item has reached the maximum Soul Eater level."), true);
            } else {
                int cost = currentLevel + 1;
                CompoundTag tag = instance.getOrCreateTag();
                int sp = tag.getInt("sacrifice_points");

                if (sp < cost) {
                    player.displayClientMessage(Component.translatable("trmythos.cultist.bless.not_enough_sp", cost), true);
                } else {
                    tag.putInt("sacrifice_points", sp - cost);
                    this.setSoulEaterLevel(held, currentLevel + 1);

                    player.displayClientMessage(Component.translatable("trmythos.cultist.bless.success", currentLevel + 1), true);

                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    private int getSoulEaterLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(TensuraEnchantments.SOUL_EATER.get(), stack);
    }

    private void setSoulEaterLevel(ItemStack stack, int level) {
        if (level > 0) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

            enchantments.put(TensuraEnchantments.SOUL_EATER.get(), level);

            EnchantmentHelper.setEnchantments(enchantments, stack);
        }
    }

    public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingDeathEvent e) {
        LivingEntity victim = e.getEntity();

        if (instance.isMastered(owner)) {
            DamageSource source = e.getSource();

            if (source.getDirectEntity() != owner) {

                if (!victim.getType().is(TensuraTags.EntityTypes.NO_SKILL_PLUNDER)) {

                    if (victim instanceof Player && victim.level.getGameRules().getBoolean(TensuraGameRules.SKILL_STEAL) == false) {

                        var ownerCap = TensuraEPCapability.getFrom(owner);
                        var victimCap = TensuraEPCapability.getFrom(victim);

                        if (ownerCap.isPresent() && victimCap.isPresent()) {
                            double stolenAmount = TensuraEPCapability.getEP(victim) * 0.5;


                            if (owner instanceof Player player) {
                                player.displayClientMessage(Component.translatable("trmythos.cultist.absorb_ep", (int)stolenAmount), true);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled() && (DamageSourceHelper.isDarkDamage(e.getSource()) || DamageSourceHelper.isSpiritual(e.getSource()))) {
            float multiplier = instance.isMastered(living) ? 2.0F : 1.5F;
            e.setAmount(e.getAmount() * multiplier);
        }
    }

}
