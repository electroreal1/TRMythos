package com.github.mythos.mythos.ability.confluence.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.api.entity.subclass.IElementalSpirit;
import com.github.manasmods.tensura.block.CharybdisCoreBlock;
import com.github.manasmods.tensura.block.entity.CharybdisCoreBlockEntity;
import com.github.manasmods.tensura.block.entity.PrayingPathBlockEntity;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.data.TensuraTags;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Convergence extends Skill {
    public Convergence(SkillType skillType) {
        super(SkillType.UNIQUE);
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Come to me, for we are meant to become one.");
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    @Override
    public int modes() {
        return 3;
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.convergence.black");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.convergence.white");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.convergence.fuse");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }


    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity living, int heldTicks) {
        Level level = living.level;
        double radius = 5.0;
        double damage = 4.0;
        double velocity = 0.5;

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                living.getBoundingBox().inflate(radius),
                e -> e != living && e.isAlive()
        );

        if (instance.getMode() == 1) {
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.SQUID_INK,
                    living.getX(), living.getY() + 1.0, living.getZ(),
                    50, 1.0, 1.0, 1.0, 0.1
            );

            living.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.5F + (float) Math.random() * 0.5F);

            for (LivingEntity target : targets) {
                Vec3 direction = living.position().subtract(target.position()).normalize();
                target.setDeltaMovement(direction.scale(velocity));
                target.hurt(TensuraDamageSources.CORROSION, (float) damage);
            }
        }

        if (instance.getMode() == 2) {
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.END_ROD,
                    living.getX(), living.getY() + 1.0, living.getZ(),
                    50, 1.0, 1.0, 1.0, 0.05
            );

            living.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 0.8F + (float) Math.random() * 0.4F);

            for (LivingEntity target : targets) {
                Vec3 direction = target.position().subtract(living.position()).normalize();
                target.setDeltaMovement(direction.scale(velocity));
                target.hurt(TensuraDamageSources.CORROSION, (float) damage);
            }
        }

        return true;
    }



    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 3) {
            Player player;
            label317:
            {
                Level level = entity.getLevel();
                BlockHitResult result = SkillHelper.getPlayerPOVHitResult(level, entity, ClipContext.Fluid.NONE, 5.0);
                BlockPos pos = result.getBlockPos();
                BlockEntity var21 = level.getBlockEntity(pos);
                if (var21 instanceof CharybdisCoreBlockEntity) {
                    CharybdisCoreBlockEntity core = (CharybdisCoreBlockEntity) var21;
                    switch ((SculkSensorPhase) level.getBlockState(pos).getValue(CharybdisCoreBlock.MODE)) {
                        case INACTIVE:
                            this.fusingCore(entity, (List) TensuraConfig.INSTANCE.blocksConfig.fusingInactiveCoreSkills.get(), core.getEP());
                            break;
                        case ACTIVE:
                            this.fusingCore(entity, (List) TensuraConfig.INSTANCE.blocksConfig.fusingActiveCoreSkills.get(), core.getEP());
                            break;
                        case COOLDOWN:
                            this.fusingCore(entity, (List) TensuraConfig.INSTANCE.blocksConfig.fusingInertCoreSkills.get(), (Double) TensuraConfig.INSTANCE.blocksConfig.fusingCoreEP.get());
                    }

                    level.destroyBlock(pos, false);
                    TensuraParticleHelper.addServerParticlesAroundPos(level.random, level, Vec3.atCenterOf(pos), ParticleTypes.SCULK_SOUL, 1.0);
                    TensuraParticleHelper.addServerParticlesAroundPos(level.random, level, Vec3.atCenterOf(pos), (ParticleOptions) TensuraParticles.SOUL.get(), 1.0);
                    level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                    return;
                }

                LivingEntity target = SkillHelper.getTargetingEntity(entity, 4.0, false);
                if (target == null || !target.isAlive()) {
                    if (entity instanceof Player) {
                        player = (Player) entity;
                        player.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                    }

                    return;
                }

                if (target instanceof Player p) {
                    if (p.getAbilities().invulnerable) {
                        return;
                    }
                }

                if (!RaceHelper.isSpiritual(target) || (double) target.getHealth() > (double) target.getMaxHealth() * 0.25) {
                    break label317;
                }

                if (target instanceof IElementalSpirit spirit) {
                    if (spirit.getSummoningTick() > 0) {
                        break label317;
                    }
                }

                if (target.hurt(TensuraDamageSources.synthesise(entity), target.getMaxHealth() * 10.0F)) {
                    if (target instanceof IElementalSpirit spirit) {
                        spirit = (IElementalSpirit) target;
                        if (entity instanceof Player) {
                            player = (Player) entity;
                            Player finalPlayer = player;
                            IElementalSpirit finalSpirit = spirit;
                            TensuraSkillCapability.getFrom(player).ifPresent((cap) -> {
                                if (cap.setSpiritLevel(finalPlayer, finalSpirit.getElemental().getId(), finalSpirit.getSpiritLevel().getId())) {
                                    TensuraSkillCapability.sync(finalPlayer);
                                    PrayingPathBlockEntity.grantSpiritMagic(finalPlayer, finalSpirit.getElemental(), finalSpirit.getSpiritLevel());
                                    PrayingPathBlockEntity.grantManipulation(finalPlayer, finalSpirit.getElemental());
                                }

                            });
                        }
                    }

                    if (!target.getType().is(TensuraTags.EntityTypes.NO_SKILL_PLUNDER)) {
                        List<ManasSkill> learntSkill = new ArrayList();
                        Iterator var30 = SkillAPI.getSkillsFrom(target).getLearnedSkills().iterator();

                        while (var30.hasNext()) {
                            ManasSkillInstance targetSkill = (ManasSkillInstance) var30.next();
                            if (!targetSkill.isTemporarySkill() && targetSkill.getMastery() >= 0 && targetSkill.getSkill() != this) {
                                ManasSkill var37 = targetSkill.getSkill();
                                if (var37 instanceof Skill) {
                                    Skill skill = (Skill) var37;
                                    SkillPlunderEvent event = new SkillPlunderEvent(target, entity, TensuraGameRules.canStealSkill(level), skill);
                                    if (!MinecraftForge.EVENT_BUS.post(event) && SkillUtils.learnSkill(entity, event.getSkill(), instance.getRemoveTime())) {
                                        learntSkill.add(event.getSkill());
                                    }
                                }
                            }
                        }

                        if (TensuraGameRules.canStealSkill(level)) {
                            learntSkill.forEach((skillx) -> {
                                SkillAPI.getSkillsFrom(target).forgetSkill(skillx);
                            });
                            SkillAPI.getSkillsFrom(target).syncChanges();
                        }
                    }

                    CompoundTag tag = instance.getOrCreateTag();
                    CompoundTag synthesisedList;
                    if (tag.contains("synthesisedList")) {
                        synthesisedList = (CompoundTag) tag.get("synthesisedList");
                        if (synthesisedList == null) {
                            return;
                        }

                        String targetID = EntityType.getKey(target.getType()).toString();
                        if (synthesisedList.contains(targetID)) {
                            return;
                        }

                        synthesisedList.putBoolean(targetID, true);
                    } else {
                        synthesisedList = new CompoundTag();
                        synthesisedList.putBoolean(EntityType.getKey(target.getType()).toString(), true);
                        tag.put("synthesisedList", synthesisedList);
                    }

                    double difference = Math.min(SkillUtils.getEPGain(target, entity), (Double) TensuraConfig.INSTANCE.skillsConfig.maximumEPSteal.get());
                    if (target instanceof Player) {
                        Player playerTarget = (Player) target;
                        if (TensuraGameRules.canEpSteal(level)) {
                            DamageSourceHelper.markHurt(target, entity);
                            SkillHelper.gainMaxMP(entity, difference / 2.0);
                            SkillHelper.gainMaxAP(entity, difference / 2.0);
                            TensuraEPCapability.setSkippingEPDrop(target, true);
                            TensuraPlayerCapability.getFrom(playerTarget).ifPresent((cap) -> {
                                double reducedAura = cap.getBaseAura() - difference / 2.0;
                                double reducedMana = cap.getBaseMagicule() - difference / 2.0;
                                if (reducedAura < 0.0) {
                                    reducedMana -= reducedAura * -1.0;
                                    reducedAura = 100.0;
                                } else if (reducedMana < 0.0) {
                                    reducedAura -= reducedMana * -1.0;
                                    reducedMana = 100.0;
                                }

                                double minusMP = cap.getBaseMagicule() - reducedMana;
                                cap.setMagicule(cap.getMagicule() - minusMP);
                                double minusAP = cap.getBaseAura() - reducedAura;
                                cap.setAura(cap.getAura() - minusAP);
                                cap.setBaseMagicule(reducedMana, playerTarget);
                                cap.setBaseAura(reducedAura, playerTarget);
                            });
                            TensuraPlayerCapability.sync(playerTarget);
                            this.addMasteryPoint(instance, entity);
                            instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
                            entity.swing(InteractionHand.MAIN_HAND, true);
                            level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER, 1.0);
                        } else if (entity instanceof Player) {
                            player = (Player) entity;
                            player.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        }
                    } else {
                        if (target.getType().is(TensuraTags.EntityTypes.NO_EP_PLUNDER)) {
                            return;
                        }

                        DamageSourceHelper.markHurt(target, entity);
                        SkillHelper.gainMaxMP(entity, difference / 2.0);
                        SkillHelper.gainMaxAP(entity, difference / 2.0);
                        TensuraEPCapability.getFrom(target).ifPresent((cap) -> {
                            cap.setEP(target, cap.getEP() - difference);
                        });
                        this.addMasteryPoint(instance, entity);
                        instance.setCoolDown(instance.isMastered(entity) ? 3 : 5);
                        entity.swing(InteractionHand.MAIN_HAND, true);
                        level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER, 1.0);
                    }
                }
            }
        }
    }

    private void fusingCore(LivingEntity entity, List<? extends String> strings, double EP) {
        List<ManasSkill> list = strings.stream().map((skillx) -> {
            return (ManasSkill)SkillAPI.getSkillRegistry().getValue(new ResourceLocation(skillx));
        }).filter(Objects::nonNull).toList();
        if (!list.isEmpty()) {
            Iterator var6 = list.iterator();

            while(var6.hasNext()) {
                ManasSkill skill = (ManasSkill)var6.next();
                if (SkillUtils.learnSkill(entity, skill) && entity instanceof Player) {
                    Player player = (Player)entity;
                    player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{skill.getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                }
            }
        }

        SkillHelper.gainMaxMP(entity, EP);
        SkillHelper.gainMP(entity, EP, false);
    }

}
