package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.effect.template.Transformation;
import com.github.manasmods.tensura.entity.magic.barrier.DarkCubeEntity;
import com.github.manasmods.tensura.entity.magic.breath.PredatorMistProjectile;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrimsonOracleSkill extends Skill implements Transformation {
    public CrimsonOracleSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 666666;
    }

    @Override
    public int getMaxMastery() {
        return 3000;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }



    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/crimson_oracle.png");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
                DamageSource damageSource = event.getSource();
                if (!damageSource.isBypassInvul() && !damageSource.isMagic()) {
                    Entity var5 = damageSource.getDirectEntity();
                    if (var5 instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity)var5;
                        double dodgeChance = instance.isMastered(entity) ? 99.0 : 0.5;

                        if (!(entity.getRandom().nextDouble() >= dodgeChance)) {
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 2.0F, 1.0F);
                            event.setCanceled(true);
                            SkillHelper.gainMP(entity, -666);
                        }

                        if (entity.hasEffect(MythosMobEffects.ULTIMATE_VILLAIN.get())) {
                            event.setCanceled(true);
                        }
                    }
                }
        }
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            PlayerList playerList = server.getPlayerList();


            ClientboundPlayerInfoPacket removePacket =
                    new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, player);


            MutableComponent leaveMessage = Component.translatable(
                    "multiplayer.player.left", player.getDisplayName()
            ).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));

            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(removePacket);
                target.sendSystemMessage(leaveMessage);
            }
        }
    }



    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            PlayerList playerList = server.getPlayerList();

            ClientboundPlayerInfoPacket addPacket =
                    new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, player);

            MutableComponent joinMessage = Component.translatable(
                    "multiplayer.player.joined", player.getDisplayName()
            ).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(addPacket);
                target.sendSystemMessage(joinMessage);
            }
        }
    }


    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event, LivingEntity entity) {
        LivingEntity target = event.getEntity();
        if (target.level.isClientSide) return;

        if (!(event.getSource().getEntity() instanceof Player player)) return;

        SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
        SkillStorage ownerStorage = SkillAPI.getSkillsFrom(player);
        List<ManasSkillInstance> targetSkills = new ArrayList<>(targetStorage.getLearnedSkills());
        for (ManasSkillInstance instance : targetSkills) {
            if (instance.getMode() != 1) return;
            Skill skill = (Skill) instance.getSkill();

            if (!(skill.getType() == SkillType.COMMON || skill.getType() == SkillType.EXTRA
                    || skill.getType() == SkillType.INTRINSIC || skill.getType() == SkillType.RESISTANCE)) continue;

            SkillPlunderEvent plunderEvent = new SkillPlunderEvent(target, player, false, skill);
            if (!MinecraftForge.EVENT_BUS.post(plunderEvent)) {
                if (SkillUtils.learnSkill(player, plunderEvent.getSkill(), 0)) {
                    player.displayClientMessage(
                            Component.translatable("tensura.skill.acquire", plunderEvent.getSkill().getName())
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                            false
                    );

                    Level world = player.getLevel();
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);

                    world.addParticle(ParticleTypes.SQUID_INK, 0.7, 0.7, 0.7f, 0.7f, 0.2, 0.2);
                }
            }
            PredatorMistProjectile breath = new PredatorMistProjectile(entity.getLevel(), entity);
            breath.setLength(3.0F);
            ManasSkillInstance crimson = this.getCrimson(entity);
            breath.setSkill(crimson != null ? crimson : instance);
            breath.setPos(entity.position().add(0.0, (double) entity.getEyeHeight() * 0.7, 0.0));
            entity.getLevel().addFreshEntity(breath);
            entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            entity.swing(InteractionHand.MAIN_HAND, true);
            this.addMasteryPoint(instance, entity);
            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
        }
    }


    private ManasSkillInstance getCrimson(LivingEntity entity) {
        SkillStorage storage = SkillAPI.getSkillsFrom(entity);
        Optional<ManasSkillInstance> CrimsonOptional = storage.getSkill((ManasSkill) Skills.CRIMSON_ORACLE.get());
        return (ManasSkillInstance)CrimsonOptional.orElse((ManasSkillInstance) null);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @SubscribeEvent
    public void onEntityDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            float damage = event.getAmount();
            float health = player.getHealth();
            if (damage >= health) {
                event.setAmount(health - 1f);
            }
        }
    }


    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        if (!living.hasEffect((MobEffect)TensuraMobEffects.PRESENCE_SENSE.get()) || !living.hasEffect((MobEffect)TensuraMobEffects.TRUE_BLINDNESS.get())) {
            living.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.TRUE_BLINDNESS.get(), 600, 0, false, false, false));
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0, false, false, false));
        }
    }

    @Override
    public int modes() {
        return 3;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.crimson_oracle.maw");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.crimson_oracle.veil");
                break;
            case 3:
                var10000 = Component.translatable("trmythos.skill.crimson_oracle.ultimate_villain");
                break;
            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            PredatorMistProjectile breath = new PredatorMistProjectile(entity.getLevel(), entity);
            breath.setLength(3.0F);
            ManasSkillInstance crimson = this.getCrimson(entity);
            breath.setSkill(crimson != null ? crimson : instance);
            breath.setPos(entity.position().add(0.0, (double) entity.getEyeHeight() * 0.7, 0.0));
            entity.getLevel().addFreshEntity(breath);
            entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            entity.swing(InteractionHand.MAIN_HAND, true);
            this.addMasteryPoint(instance, entity);
            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
        }

        if (instance.getMode() == 2) {
            if (!SkillHelper.outOfMagicule(entity, instance)) {
                entity.swing(InteractionHand.MAIN_HAND, true);
                this.addMasteryPoint(instance, entity);

                int distance = instance.isMastered(entity) ? 20 : 15;
                Entity target = SkillHelper.getTargetingEntity(entity, (double) distance, false, true);
                Vec3 pos;
                if (target != null) {
                    if (target.isOnGround()) {
                        pos = target.position().add(0.0, 4.5, 0.0);
                    } else {
                        pos = target.position().add(0.0, target.getBbHeight() / 2.0, 0.0);
                    }
                } else {
                    BlockHitResult result = SkillHelper.getPlayerPOVHitResult(entity.level, entity, ClipContext.Fluid.NONE, distance);
                    pos = result.getLocation().add(0.0, 4.5, 0.0);
                }

                DarkCubeEntity cube = new DarkCubeEntity(entity.getLevel(), entity);
                cube.setPos(pos);
                cube.setDamage(instance.isMastered(entity) ? 20.0F : 10.0F);
                cube.setMpCost(this.magiculeCost(entity, instance));
                cube.setSkill(instance);
                cube.setLife(600);
                cube.setRadius(7.0F);
                entity.getLevel().addFreshEntity(cube);

                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.PLAYERS, 1.0F, 1.0F);

                instance.setCoolDown(instance.isMastered(entity) ? 2 : 4);
                instance.markDirty();
            }
        }

        if (instance.getMode() == 3) {
            if (!this.failedToActivate(entity, (MobEffect) MythosMobEffects.ULTIMATE_VILLAIN.get())) {
                if (!entity.hasEffect((MobEffect)MythosMobEffects.ULTIMATE_VILLAIN.get())) {
                    if (SkillHelper.outOfMagicule(entity, instance)) {
                        return;
                    }

                    this.addMasteryPoint(instance, entity);
                    instance.setCoolDown(1200);
                    entity.setHealth(entity.getHealth() * 2.0F);
                    if (entity instanceof Player) {
                        Player player = (Player)entity;
                        TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                            cap.setMagicule(cap.getMagicule() * 2.0);
                            cap.setAura(cap.getAura() * 2.0);
                            TensuraPlayerCapability.sync(player);
                        });
                    }

                    entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);

                    if (entity instanceof Player player) {
                        int amplifier = player.getServer().getPlayerList().getPlayerCount();

                        entity.addEffect(new MobEffectInstance(
                                (MobEffect) MythosMobEffects.CHILD_OF_THE_PLANE.get(),
                                this.isMastered(instance, entity) ? 7200 : 3600,
                                amplifier,
                                false,
                                false,
                                false
                        ));
                    }

                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.POOF, 3.0);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION, 3.0);
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions) TensuraParticles.SOLAR_FLASH.get(), 2.0);
                    TensuraParticleHelper.spawnServerParticles(entity.level, (ParticleOptions)TensuraParticles.DARK_RED_LIGHTNING_SPARK.get(), entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true);
                } else {
                    entity.removeEffect((MobEffect)MythosMobEffects.ULTIMATE_VILLAIN.get());
                    entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }

            }
        }
    }
}




