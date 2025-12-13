package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "trmythos")
public class CarnageSkill extends Skill {

    public static boolean VAMPIRE_CARNAGE = true;
    public static boolean CarnageBloodDominion = true;

    private static final HashMap<UUID, Integer> killStacks = new HashMap<>();

    // Attribute modifier UUIDs
    private static final UUID CARNAGE_DAMAGE_UUID = UUID.fromString("f25df6f3-1847-4bd2-9d6b-48c57b6a91e1");
    private static final UUID CARNAGE_SPEED_UUID  = UUID.fromString("4b8ae08b-3740-4690-84cd-694db333ca1a");

    public CarnageSkill(SkillType type) {
        super(SkillType.UNIQUE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.CRIMSON_TYRANT.get());
    }

    @Override
    public int getMaxMastery() {
        return 4000;
    }

    @Override
    public double getObtainingEpCost() {
        return 500000.0;
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

        UUID id = player.getUUID();
        int stacks = killStacks.getOrDefault(id, 0);
        stacks = Math.min(stacks + 1, 100);
        killStacks.put(id, stacks);

        // Bonuses scale safely
        double attackBonus = 4.0 + (stacks * 0.05);
        double speedBonus  = 0.1 + (stacks * 0.01);

        applyModifier(player, Attributes.ATTACK_DAMAGE, CARNAGE_DAMAGE_UUID, attackBonus);
        applyModifier(player, Attributes.ATTACK_SPEED, CARNAGE_SPEED_UUID, speedBonus);

        // Special mode buff extension
        if (player.getPersistentData().getBoolean("AbsoluteBloodLordActive")) {
            extendOrAddEffect(player, MythosMobEffects.BLOOD_COAT.get(), 200);
            extendOrAddEffect(player, TensuraMobEffects.HAKI_COAT.get(), 200);
            extendOrAddEffect(player, TensuraMobEffects.STRENGTHEN.get(), 200);
            extendOrAddEffect(player, TensuraMobEffects.INSPIRATION.get(), 200);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

        float heal = event.getAmount() * 0.15f;
        player.heal(heal);
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!VAMPIRE_CARNAGE) return;
        if (!(entity instanceof Player player)) return;
        if (instance.isTemporarySkill()) return;

        double magicules =  TensuraPlayerCapability.getBaseMagicule(player);
        double aura = TensuraPlayerCapability.getBaseAura(player);
        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            Race baron = TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.VAMPIRE_BARON);
            if (cap.getRace() != baron) {
                cap.setRace(player, baron, true);
                player.displayClientMessage(Component.literal("A strange urge gnaws at your sanity. With altered senses, the scent of blood seems evermore mouthwatering.")
                        .withStyle(ChatFormatting.RED), false);
                TensuraPlayerCapability.setAura(player, aura);
                TensuraPlayerCapability.setMagicule(player, magicules);
            }
        });
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.carnage.crimson");
            case 2 -> Component.translatable("trmythos.skill.carnage.absolute");
            default -> Component.empty();
        };
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    private static double rotation = 0;

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (!(living instanceof Player player)) return;
        Level level = living.level;
        if (!(level instanceof ServerLevel server)) return;
        RandomSource rand = player.level.random;
        int tendrils = 6 + rand.nextInt(4);
        int segments = 6;
        double maxHeight = 1.5;
        double baseRadius = 0.5;

        for (int i = 0; i < tendrils; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            double startX = player.getX() + dx * (0.2 + rand.nextDouble() * 0.3);
            double startZ = player.getZ() + dz * (0.2 + rand.nextDouble() * 0.3);
            for (int j = 1; j <= segments; j++) {
                double t = j / (double) segments;
                double px = startX + Math.sin(rotation + j) * 0.05;
                double pz = startZ + Math.cos(rotation + j) * 0.05;
                double py = player.getY() + t * maxHeight + (rand.nextDouble() - 0.5) * 0.1;
                if (rand.nextDouble() < 0.3) continue;
                Vector3f color;
                double r = rand.nextDouble();
                if (r < 0.5) color = new Vector3f(0.6f, 0f, 0f);
                else if (r < 0.85) color = new Vector3f(0.8f, 0f, 0f);
                else color = new Vector3f(0.4f, 0f, 0f);
                server.sendParticles(new DustParticleOptions(color, 0.8f + rand.nextFloat() * 0.2f), px, py, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {

            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) return false;

            if (heldTicks % 100 == 0 && heldTicks > 0) this.addMasteryPoint(instance, entity);

            CrimsonDesolation(instance, entity, heldTicks);

        }
        return true;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 1) {
            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
        }
    }

    public static void CrimsonDesolation(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
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
                    SkillHelper.checkThenAddEffectSource(target, entity, (MobEffect) TensuraMobEffects.FEAR.get(), 200, fearLevel);
                    entity.addEffect(new MobEffectInstance(TensuraMobEffects.FRAGILITY.get(), 1200, 1, false, false, false));
                    HakiSkill.hakiPush(target, entity, fearLevel);
                    }
                }
            }
        }
    }

    public void onPressed(ServerPlayer player, ManasSkillInstance instance) {
        if (!(instance.getMode() == 2)) return;
        Level level = player.level;

        level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.WITHER_DEATH,
                SoundSource.PLAYERS, 2.0f, 0.9f);

        player.addEffect(new MobEffectInstance(MythosMobEffects.BLOOD_COAT.get(), 1200, 1, false, false, false));
        player.addEffect(new MobEffectInstance(TensuraMobEffects.HAKI_COAT.get(), 1200, 1, false, false, false));
        player.addEffect(new MobEffectInstance(TensuraMobEffects.STRENGTHEN.get(), 1200, 25, false, false, false));
        player.addEffect(new MobEffectInstance(TensuraMobEffects.INSPIRATION.get(), 1200, 15, false, false, false));

        player.getPersistentData().putBoolean("AbsoluteBloodLordActive", true);

        TensuraParticleHelper.addServerParticlesAroundSelf(player, net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER);

        instance.setCoolDown(1200);
    }

    private static void applyModifier(Player player, Attribute attr, UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        inst.addTransientModifier(new AttributeModifier(id, "carnage_bonus", amount, AttributeModifier.Operation.ADDITION));
    }

    private static void extendOrAddEffect(LivingEntity entity, MobEffect effect, int extra) {
        if (entity == null || effect == null) return;
        MobEffectInstance current = entity.getEffect(effect);
        if (current != null) {
            entity.addEffect(new MobEffectInstance(effect, current.getDuration() + extra, current.getAmplifier(), current.isAmbient(), current.isVisible(), current.showIcon()));
        } else {
            entity.addEffect(new MobEffectInstance(effect, extra, 0));
        }
    }
}