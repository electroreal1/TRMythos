package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.github.mythos.mythos.registry.race.MythosSecretRaces;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

        float heal = event.getAmount() * 0.15f; // 15% lifesteal
        player.heal(heal);
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!VAMPIRE_CARNAGE) return;
        if (!(entity instanceof Player player)) return;

        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            Race baron = TensuraRaces.RACE_REGISTRY.get().getValue(MythosSecretRaces.VAMPIRE_BARON);
            if (cap.getRace() != baron) {
                cap.setRace(player, baron, true);
                player.displayClientMessage(Component.literal("A strange urge gnaws at your sanity. With altered senses, the scent of blood seems evermore mouthwatering.")
                        .withStyle(ChatFormatting.RED), false);
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
            case 1 -> Component.translatable("trmythos.skill.carnage.blood");
            case 2 -> Component.translatable("trmythos.skill.carnage.absolute");
            default -> Component.empty();
        };
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return;

        TensuraSkillInstance tsi = (TensuraSkillInstance) instance;

        if (tsi.getMode() == 1) {
            BloodDominion(player, tsi);
        } else if (tsi.getMode() == 2) {
            AbsoluteBloodlord(player, tsi);
        }
    }

    private void BloodDominion(ServerPlayer player, TensuraSkillInstance instance) {
        if (!CarnageBloodDominion) return;

        Level level = player.level;
        double range = 15.0;
        float casterEP = (float) TensuraEPCapability.getCurrentEP(player);

        List<LivingEntity> targets =
                level.getEntitiesOfClass(LivingEntity.class,
                        player.getBoundingBox().inflate(range),
                        e -> e != player && e.isAlive() && !e.isAlliedTo(player)
                );

        level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS, 1f, 1.2f);

        TensuraParticleHelper.addServerParticlesAroundSelf(player, net.minecraft.core.particles.ParticleTypes.CRIMSON_SPORE);

        for (LivingEntity target : targets) {
            if (target instanceof Player t && t.getAbilities().invulnerable) continue;

            float targetEP = target instanceof Player p ?
                    (float) TensuraEPCapability.getCurrentEP(p) : 0f;

            if (targetEP > casterEP * 0.85f) continue;

            // Turn players into vampires
            if (target instanceof Player p) {
                TensuraPlayerCapability.getFrom(p).ifPresent(cap -> {
                    Race vamp = TensuraRaces.VAMPIRE.get();
                    if (cap.getRace() != vamp) cap.setRace(p, vamp, true);
                });
            }

            TensuraParticleHelper.spawnServerParticles(
                    player.level, net.minecraft.core.particles.ParticleTypes.DAMAGE_INDICATOR,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    8, 0.1, 0.1, 0.1, 0.2, true
            );
        }
    }

    private void AbsoluteBloodlord(ServerPlayer player, TensuraSkillInstance instance) {
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

    private static void applyModifier(Player player, Attribute attr,
                                      UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;

        inst.removeModifier(id);

        inst.addTransientModifier(new AttributeModifier(
                id,
                "carnage_bonus",
                amount,
                AttributeModifier.Operation.ADDITION
        ));
    }

    private static void extendOrAddEffect(LivingEntity entity, MobEffect effect, int extra) {
        if (entity == null || effect == null) return;

        MobEffectInstance current = entity.getEffect(effect);
        if (current != null) {
            entity.addEffect(new MobEffectInstance(
                    effect,
                    current.getDuration() + extra,
                    current.getAmplifier(),
                    current.isAmbient(),
                    current.isVisible(),
                    current.showIcon()
            ));
        } else {
            entity.addEffect(new MobEffectInstance(effect, extra, 0));
        }
    }
}