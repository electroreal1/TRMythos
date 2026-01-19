package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import com.github.manasmods.tensura.ability.skill.unique.CookSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class RavanaSkill extends Skill {
    public RavanaSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public double getObtainingEpCost() {
        return 6162136;
    }

    @Override
    public @NotNull Component getSkillDescription() {
        return Component.literal("When the Sin of Carnage reaches it's apex. Fear becomes law. Blood becomes power. All living beings become prey, some may even say that the Demon God of Carnage has awoken.");
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Ravana");
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.CARNAGE.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = Skills.CARNAGE.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
        }
    }

    @Override
    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.ravana.cataclysm");
            case 2 -> Component.translatable("trmythos.skill.ravana.apex");
            default -> Component.empty();
        };
    }

    @Override
    public void onDamageEntity(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

        float heal = event.getAmount() * 0.15f;
        player.heal(heal);
    }

    private static final UUID CARNAGE_DAMAGE_UUID = UUID.fromString("f25df6f3-1847-4bd2-9d6b-48c57b6a91e1");
    private static final UUID CARNAGE_SPEED_UUID  = UUID.fromString("4b8ae08b-3740-4690-84cd-694db333ca1a");


    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

        double attackBonus = 6;
        double speedBonus  = 0.03;

        applyModifier(player, Attributes.ATTACK_DAMAGE, CARNAGE_DAMAGE_UUID, attackBonus);
        applyModifier(player, Attributes.ATTACK_SPEED, CARNAGE_SPEED_UUID, speedBonus);

        if (player.getPersistentData().getBoolean("SlaughterApex")) {
            extendOrAddEffect(player, MythosMobEffects.BLOOD_COAT.get(), 200);
            extendOrAddEffect(player, TensuraMobEffects.HAKI_COAT.get(), 200);
            extendOrAddEffect(player, TensuraMobEffects.STRENGTHEN.get(), 200);
            extendOrAddEffect(player, TensuraMobEffects.INSPIRATION.get(), 200);
        }
    }

    private static void applyModifier(Player player, Attribute attr, UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        inst.addTransientModifier(new AttributeModifier(id, "ravana_boost", amount, AttributeModifier.Operation.ADDITION));
    }

    private static void extendOrAddEffect(LivingEntity entity, MobEffect effect, int extra) {
        if (entity == null || effect == null) return;
        MobEffectInstance current = entity.getEffect(effect);
        if (current != null) {
            entity.addEffect(new MobEffectInstance(effect, current.getDuration() + extra, current.getAmplifier(), current.isAmbient(), current.isVisible(), current.showIcon()));
        }
    }

    @Override
    public void onBeingDamaged(@NotNull ManasSkillInstance instance, LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        float damage = event.getAmount();
        if (damage <= 0) return;

        float stored = (float) (damage * 0.1);

        addBloodReserve(stored, instance);

        if (player.hasEffect(MythosMobEffects.BLOOD_COAT.get())) {
            DamageSource source = event.getSource();
            Entity attackerEntity = source.getEntity();

            if (!(attackerEntity instanceof LivingEntity attacker)) return;

            CompoundTag tag = instance.getOrCreateTag();
            float reserve = tag.getFloat("BloodReserve");
            if (reserve <= 0) return;

            float reflectPercent = 0.25f;
            float reflectDamage = damage * reflectPercent;

            float actual = Math.min(reflectDamage, reserve);
            if (actual <= 0) return;

            DamageSource reflectSource = TensuraDamageSources.bloodRay(player);

            attacker.hurt(reflectSource, actual);

            tag.putFloat("BloodReserve", reflectDamage - actual);
            instance.markDirty();
        }
    }

    private static void addBloodReserve(float amount, ManasSkillInstance instance) {
        CompoundTag tag = instance.getOrCreateTag();

        float current = tag.getFloat("BloodReserve");
        tag.putFloat("BloodReserve", current + amount);

        instance.markDirty();
    }

    private boolean activatedChaoticFate(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (tag.getInt("ChaoticFate") < 100) {
            return false;
        } else {
            return instance.isMastered(entity) && instance.isToggled() ? true : tag.getBoolean("ChaoticFateActivated");
        }
    }

    @Override
    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        CompoundTag tag = instance.getOrCreateTag();
        if (this.activatedChaoticFate(instance, entity)) {
            if (!instance.onCoolDown()) {
                LivingEntity target = event.getEntity();
                AttributeInstance health = target.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    double amount = event.getAmount() * 0.1;
                    AttributeModifier chefModifier = health.getModifier(CookSkill.COOK);
                    if (chefModifier != null) {
                        amount -= chefModifier.getAmount();
                    }

                    AttributeModifier attributemodifier = new AttributeModifier(CookSkill.COOK, "Cook", amount * -1.0, AttributeModifier.Operation.ADDITION);
                    health.removeModifier(attributemodifier);
                    health.addPermanentModifier(attributemodifier);
                    if (!instance.isMastered(entity) || !instance.isToggled()) {
                        tag.putBoolean("ChaoticFateActivated", false);
                    }

                    this.addMasteryPoint(instance, entity);
                    instance.setCoolDown(1);
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.AMBIENT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {

            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) return false;

            if (heldTicks % 100 == 0 && heldTicks > 0) this.addMasteryPoint(instance, entity);

            if (instance.getMode() == 1) {
                if (heldTicks % 20 == 0) {
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                if (heldTicks % 2 == 0) {
                    TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                            new RequestFxSpawningPacket(new ResourceLocation("tensura:demon_lord_haki"), entity.getId(), 0.0, 1.0, 0.0, true));
                }
                List<LivingEntity> list = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0),
                        (living) -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity));

                if (!list.isEmpty()) {
                    double scale = instance.getTag() == null ? 0.0 : instance.getTag().getDouble("scale");
                    double multiplier = scale == 0.0 ? 1.0 : Math.min(scale, 1.0);
                    double ownerEP = TensuraEPCapability.getEP(entity) * multiplier;

                    for (LivingEntity target : list) {
                        if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                        double targetEP = TensuraEPCapability.getEP(target);
                        double difference = ownerEP / targetEP;

                        if (difference > 2.0) {
                            int fearLevel = (int) (difference * 0.5 - 1.0);
                            fearLevel = Math.min(fearLevel, TensuraConfig.INSTANCE.mobEffectConfig.maxFear.get());
                            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.FEAR.get(), 200, fearLevel);
                            target.addEffect(new MobEffectInstance(TensuraMobEffects.FRAGILITY.get(), 1200, fearLevel, false, false, false));
                            target.addEffect(new MobEffectInstance(TensuraMobEffects.CURSE.get(), 1200, fearLevel, false, false, false));
                            target.addEffect(new MobEffectInstance(TensuraMobEffects.MOVEMENT_INTERFERENCE.get(), 1200, fearLevel, false, false, false));
                            target.addEffect(new MobEffectInstance(MysticismMobEffects.MARKED_FOR_DEATH.get(), 1200, 1, false, false, false));
                            target.hurt(TensuraDamageSources.bloodRay(entity), 400);
                            HakiSkill.hakiPush(target, entity, fearLevel);
                        }
                    }
                }
            }

        }
        return true;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (living.getTags().contains("SlaughterApex")) {
            if (living.hasEffect(MythosMobEffects.BLOOD_COAT.get())) {
                return;
            } else {
                living.getTags().remove("SlaughterApex");
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 2) {
            Level level = entity.level;

            level.playSound(null, entity.blockPosition(), net.minecraft.sounds.SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 2.0f, 0.9f);

            entity.addEffect(new MobEffectInstance(MythosMobEffects.BLOOD_COAT.get(), 1200, 1, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.HAKI_COAT.get(), 1200, 1, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 1200, 40, false, false, false));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.INSPIRATION.get(), 1200, 25, false, false, false));

            entity.getPersistentData().putBoolean("SlaughterApex", true);

            TensuraParticleHelper.addServerParticlesAroundSelf(entity, net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER);

            instance.setCoolDown(1200);
        }
    }
}
