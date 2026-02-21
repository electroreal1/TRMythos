package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShadowAvengerSkill extends Skill {
    private static final UUID SHP_GROWTH_ID = UUID.fromString("7d3a529b-e065-4f76-8f24-6997097e937d");
    private static final String HATRED_KEY = "HatredStacks";
    private static final String DECAY_TIMER_KEY = "HatredDecayTimer";
    private static final String PERSISTENCE_KEY = "IsPersistent";
    private static final String ACCUMULATION_TIMER = "AccumulationTimer";

    public ShadowAvengerSkill(SkillType type) {
        super(type);
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Shadow Avenger");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Cursed by the world, betrayed by humanity, the only thing left is a smear on the planet.");
    }

    @Override
    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Curse of the Avenger").withStyle(ChatFormatting.DARK_RED);
            case 2 -> Component.literal("Mud of the Grail").withStyle(ChatFormatting.DARK_PURPLE);
            default -> super.getModeName(mode);
        };
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        list.add(TensuraMobEffects.MIND_CONTROL.get());
        list.add(TensuraMobEffects.FEAR.get());
        list.add(TensuraMobEffects.INSANITY.get());
        return list;
    }

    @SubscribeEvent
    public void onEffectRemove(MobEffectEvent.Remove event) {
        // Prevents removal of negative status effects from the user
        LivingEntity entity = event.getEntity();
        if (!SkillUtils.fullyHasSkill(entity, this)) return;
        if (event.getEffect() != null && event.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (!SkillUtils.fullyHasSkill(entity, this)) return;

        MobEffectInstance effectInstance = event.getEffectInstance();
        MobEffect effect = effectInstance.getEffect();

        if (effect.getCategory() == MobEffectCategory.HARMFUL) {
            AttributeInstance shpAttr = entity.getAttribute(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
            if (shpAttr != null) {
                double currentAmount = 0;
                AttributeModifier currentMod = shpAttr.getModifier(SHP_GROWTH_ID);
                if (currentMod != null) {
                    currentAmount = currentMod.getAmount();
                    shpAttr.removeModifier(SHP_GROWTH_ID);
                }
                shpAttr.addPermanentModifier(new AttributeModifier(SHP_GROWTH_ID, "Shadow Avenger Growth", currentAmount + 10, AttributeModifier.Operation.ADDITION));
            }
            applySynergyBuff(entity, effect);
        }
    }

    private void applySynergyBuff(LivingEntity entity, MobEffect originalEffect) {
        if (originalEffect == MobEffects.POISON || originalEffect == MobEffects.WITHER) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 1));
        } else if (originalEffect == MobEffects.WEAKNESS) {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 160, 1));
        } else if (originalEffect == MobEffects.MOVEMENT_SLOWDOWN) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 1));
        } else if (originalEffect == MobEffects.BLINDNESS) {
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 160, 0));
        } else {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 0));
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        CompoundTag tag = instance.getOrCreateTag();

        int stacks = tag.getInt("HatredStacks");
        boolean isPersistent = tag.getBoolean("IsPersistent");

        if (stacks >= 15 && isPersistent) {
            double range = 15.0;
            List<LivingEntity> nearbyAllies = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(range),
                    target -> target != entity && target.isAlive() && (target instanceof Player || target.isAlliedTo(entity)));

            if (nearbyAllies.size() >= 3) {
                int evoTimer = tag.getInt("EvolutionTimer");
                evoTimer++;

                if (evoTimer % 20 == 0) {
                    player.displayClientMessage(Component.literal("The malice is shifting... " + (30 - (evoTimer / 20)) + "s left")
                            .withStyle(ChatFormatting.LIGHT_PURPLE), true);
                }

                if (evoTimer >= 600) {
                    evolveToWorldScapegoat(player);
                } else {
                    tag.putInt("EvolutionTimer", evoTimer);
                }
            } else {
                if (tag.getInt("EvolutionTimer") > 0) {
                    tag.putInt("EvolutionTimer", 0);
                    player.displayClientMessage(Component.literal("The ritual was interrupted! Keep your allies close.")
                            .withStyle(ChatFormatting.RED), true);
                }
            }
        } else {
            tag.putInt("EvolutionTimer", 0);
        }


        int cap = instance.isMastered(entity) ? 15 : 10;

        if (instance.isToggled()) {
            int accTimer = tag.getInt(ACCUMULATION_TIMER);
            accTimer++;
            if (accTimer >= 200 && stacks < cap) {
                tag.putInt(HATRED_KEY, stacks + 1);
                tag.putInt(ACCUMULATION_TIMER, 0);
            } else {
                tag.putInt(ACCUMULATION_TIMER, accTimer);
            }
        } else {
            int decayTimer = tag.getInt(DECAY_TIMER_KEY);
            decayTimer++;
            if (decayTimer >= 100 && stacks > 0) {
                tag.putInt(HATRED_KEY, stacks - 1);
                tag.putInt(DECAY_TIMER_KEY, 0);
            } else {
                tag.putInt(DECAY_TIMER_KEY, decayTimer);
            }
        }

        if (tag.getBoolean(PERSISTENCE_KEY) && this.isInSlot(entity)) {
            double drain = TensuraPlayerCapability.getBaseMagicule(player) * 0.02;
            if (SkillHelper.getMP(entity, true) >= drain) {
                SkillHelper.drainMP(entity, null, drain, true);
                if (entity.getHealth() < 1) entity.setHealth(1);
            } else {
                tag.putBoolean(PERSISTENCE_KEY, false);
                entity.hurt(DamageSource.MAGIC, 1000);
            }
        }
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag tag = instance.getOrCreateTag();
        int stacks = tag.getInt(HATRED_KEY);
        int cap = instance.isMastered(entity) ? 15 : 10;

        if (stacks < cap) {
            tag.putInt(HATRED_KEY, stacks + 1);
        }
        tag.putInt(DECAY_TIMER_KEY, 0);
    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (this.isInSlot(entity)) {
            if (DamageSourceHelper.isLightDamage(source) || DamageSourceHelper.isHoly(source)) {
                event.setAmount(event.getAmount() * 1.5f);
            }
            if (instance.isToggled()) {
                event.setAmount(event.getAmount() * 1.15f);
            }
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        CompoundTag tag = instance.getOrCreateTag();
        int stacks = tag.getInt(HATRED_KEY);

        if (stacks > 0) {
            float multiplier = 1.0f + (stacks * 0.08f);

            if (DamageSourceHelper.isSpiritual(event.getSource())) {
                event.setAmount(event.getAmount() * multiplier);
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() != 1) return;
        CompoundTag tag = instance.getOrCreateTag();
        int stacks = tag.getInt(HATRED_KEY);
        if (!(entity instanceof Player player)) return;

        if (stacks <= 0) {
            player.displayClientMessage(Component.literal("Insufficient malice gathered...").withStyle(ChatFormatting.RED), true);
            return;
        }

        Entity targetEntity = MythosUtils.getLookedAtEntity(player, 12);
        if (targetEntity instanceof LivingEntity target) {
            float damage = stacks * 5.0f;

            DamageSource damageSource = new DamageSource(TensuraDamageSources.DARK_ATTACK);

            target.hurt(damageSource, damage);
            DamageSourceHelper.directSpiritualHurt(target, player, stacks * 10.0f);

            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));

            player.level.playSound(null, target.blockPosition(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0f, 0.5f);

            tag.putInt(HATRED_KEY, 0);
            player.displayClientMessage(Component.literal("Malice Released!").withStyle(ChatFormatting.DARK_PURPLE), true);
        }
    }


    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        CompoundTag tag = instance.getOrCreateTag();
        double currentMP = SkillHelper.getMP(entity, false);
        if (!tag.getBoolean(PERSISTENCE_KEY) && currentMP > (TensuraPlayerCapability.getBaseMagicule(player) * 0.2)) {
            event.setCanceled(true);
            tag.putBoolean(PERSISTENCE_KEY, true);
            entity.setHealth(1);
            entity.level.playSound(null, entity.blockPosition(), SoundEvents.WARDEN_DEATH, SoundSource.PLAYERS, 1.0f, 0.1f);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() != 2) return false;
        if (!(entity instanceof Player player)) return false;


        if (heldTicks % 20 == 0) {
            CompoundTag tag = instance.getOrCreateTag();
            int stacks = tag.getInt("HatredStacks");
            int cap = instance.isMastered(entity) ? 15 : 10;
            if (stacks < cap) {
                tag.putInt("HatredStacks", stacks + 1);
            }
        }

        if (heldTicks % 60 == 0 && heldTicks > 0) {
            this.addMasteryPoint(instance, entity);
        }

        if (heldTicks % 10 == 0) {
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.5F);
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.5F);

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:mortal_fear"), entity.getId(), 0.0, 1.0, 0.0, true));

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:demon_lord_haki"), entity.getId(), 0.0, 1.1, 0.0, true));

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:sub_mortal_fear"), entity.getId(), 0.0, 0.9, 0.0, true));
        }

        double radius = instance.isMastered(entity) ? 16.0 : 12.0;
        List<LivingEntity> targets = entity.getLevel().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(radius), (targetData) -> !targetData.is(entity) && targetData.isAlive() && !entity.isAlliedTo(targetData));

        for (LivingEntity target : targets) {
            if (target != null && player.getAbilities().invulnerable) continue;

            assert target != null;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false));

            int hatred = instance.getOrCreateTag().getInt("HatredStacks");
            float damagePerTick = 1.0F + (hatred * 0.2F);

            DamageSource damageSource = new DamageSource(TensuraDamageSources.DARK_ATTACK);

            target.hurt(damageSource, damagePerTick);
            DamageSourceHelper.directSpiritualHurt(target, entity, damagePerTick * 2.0F);
        }

        return true;
    }

    private void evolveToWorldScapegoat(Player player) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);

        storage.learnSkill(Skills.WORLDS_SCAPEGOAT.get());
        storage.syncAll();

        player.getPersistentData().remove("EvolutionTimer");

        player.level.playSound(null, player.blockPosition(),
                SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.5f, 0.5f);

        if (player.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0);
            serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1, player.getZ(), 100, 1.0, 2.0, 1.0, 0.05);
        }

        player.sendSystemMessage(Component.literal("Your hatred finds a purpose beyond vengeance. You choose to carry the weight of their world.")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
    }
}