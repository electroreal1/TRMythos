package com.github.mythos.mythos.ability.mythos.skill.ultimate.god;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.data.TensuraTags;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import io.github.Memoires.trmysticism.util.damage.MysticismDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableGodClassObtainment;
import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class Quachil extends Skill {
    public Quachil() {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 50000000;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Corruption God, Quachil Uttaus");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Corruption that erodes all life. The End borne from the worst aspects that man had to offer, gathered and settled like sediment. None can escape this abyss. The only thing you can do is pray, no matter that they fall on deaf ears...");
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/quachil.png");
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableGodClassObtainment()) return false;
        if (!EnableUltimateSkillObtainment()) return false;
        GodClassHandler godClassHandler = new GodClassHandler();
        if (godClassHandler.isQuachilObtained()) return false;
        SkillStorage userStorage = SkillAPI.getSkillsFrom(player);
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }

        List<Skill> learnedSkills = userStorage.getLearnedSkills().stream()
                .map(ManasSkillInstance::getSkill)
                .filter(Objects::nonNull)
                .filter(Skill.class::isInstance)
                .map(Skill.class::cast)
                .toList();

        for (Skill skill : learnedSkills) {
            String name = skill.getName().toString().toLowerCase();
            if (name.contains("pure") || name.contains("purification") ||
                    name.contains("purity") || name.contains("hope") ||
                    name.contains("life") || name.contains("regeneration") ||
                    name.contains("heal") || name.contains("holy") ||
                    name.contains("chef") || name.contains("great") ||
                    name.contains("saviour") || name.contains("chosen") ||
                    name.contains("yehoshuah") || name.contains("blessing") ||
                    name.contains("light") || name.contains("good") ||
                    name.contains("hero") || name.contains("angel")) {
                return false;
            }
        }

        return SkillUtils.isSkillMastered(player, Skills.APOPHIS.get()) &&
                SkillUtils.isSkillMastered(player, UltimateSkills.BEELZEBUB.get()) &&
                SkillUtils.isSkillMastered(player, Skills.MAMMON.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.ENVY.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.PRIDE.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.GLUTTONY.get()) &&
                SkillUtils.isSkillMastered(player, UltimateSkills.SATANAEL.get()) &&
                SkillUtils.isSkillMastered(player, UltimateSkills.ASMODEUS.get());

    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = Skills.APOPHIS.get();
            Skill greedSkill1 = UltimateSkills.ASMODEUS.get();
            Skill greedSkill2 = UniqueSkills.ENVY.get();
            Skill greedSkill3 = UniqueSkills.GLUTTONY.get();
            Skill greedSkill4 = UniqueSkills.PRIDE.get();
            Skill greedSkill5 = UltimateSkills.BELPHEGOR.get();
            Skill greedSkill6 = UltimateSkills.SATANAEL.get();
            Skill greedSkill7 = Skills.MAMMON.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill1).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill2).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill3).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill4).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill5).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill6).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill7).ifPresent(storage::forgetSkill);
            GodClassHandler godClassHandler = new GodClassHandler();
            godClassHandler.setQuachilObtained(true);
        }
    }

    @Override
    public List<MobEffect> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
        List<MobEffect> list = new ArrayList<>();
        list.add(TensuraMobEffects.MAGICULE_POISON.get());
        return list;
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();
            if (!instance.onCoolDown()) {
                if (this.isInSlot(entity)) {
                    DamageSource damageSource = event.getSource();
                    if (!damageSource.isBypassInvul()) {
                        if (damageSource.getEntity() == null || !damageSource.getEntity().getType().is(TensuraTags.EntityTypes.NO_SKILL_PLUNDER)) {
                            if (damageSource instanceof TensuraDamageSource source) {
                                if (!((double) source.getIgnoreBarrier() >= 1.75)) {
                                    ManasSkillInstance targetInstance = source.getSkill();
                                    if (targetInstance != null && !targetInstance.isTemporarySkill() && targetInstance.getSkill() != this) {
                                        Entity var8 = source.getEntity();
                                        if (var8 instanceof Player player) {
                                            if (player.getAbilities().invulnerable) {
                                                return;
                                            }
                                        }

                                        ManasSkill skill = targetInstance.getSkill();
                                        int chance = this.copyChance(entity, instance, skill);
                                        if (entity.getRandom().nextInt(100) >= chance) {
                                            if (chance != 0 && !SkillUtils.hasSkill(entity, skill)) {
                                                this.addMasteryPoint(instance, entity);
                                            }

                                            if (entity instanceof Player player) {
                                                player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                                            }

                                        } else {
                                            SkillPlunderEvent plunderEvent = new SkillPlunderEvent(source.getEntity(), entity, false, skill);
                                            if (!MinecraftForge.EVENT_BUS.post(plunderEvent)) {
                                                if (SkillUtils.learnSkill(entity, plunderEvent.getSkill(), instance.getRemoveTime())) {
                                                    if (entity instanceof Player player) {
                                                        player.displayClientMessage(Component.translatable("tensura.skill.acquire", plunderEvent.getSkill().getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                                                        TensuraSkillCapability.getFrom(player).ifPresent((cap) -> {
                                                            Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(plunderEvent.getSkill());
                                                            if (!optional.isEmpty()) {
                                                                if (cap.getSkillInSlot(0) == null) {
                                                                    cap.setInstanceInSlot(optional.get(), 0);
                                                                } else if (cap.getSkillInSlot(1) == null) {
                                                                    cap.setInstanceInSlot(optional.get(), 1);
                                                                } else if (cap.getSkillInSlot(2) == null) {
                                                                    cap.setInstanceInSlot(optional.get(), 2);
                                                                }

                                                                TensuraSkillCapability.sync(player);
                                                            }
                                                        });
                                                    }

                                                    ManasSkill var11 = plunderEvent.getSkill();
                                                    if (var11 instanceof TensuraSkill tensuraSkill) {
                                                        double mastery = tensuraSkill.getObtainingEpCost() / 10000.0;
                                                        this.addMasteryPoint(instance, entity, (int) (mastery + (double) SkillUtils.getBonusMasteryPoint(instance, entity, (int) mastery)));
                                                        instance.setCoolDown(Math.max((int) (360.0 * mastery), 1));
                                                    } else {
                                                        this.addMasteryPoint(instance, entity);
                                                    }

                                                    entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.PLAYERS, 2.0F, 1.0F);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target instanceof Player player)) return;
        if (!instance.isToggled()) return;
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        boolean isImmune = DamageSourceHelper.isDarkDamage(source) || event.getSource().isMagic() ||
                DamageSourceHelper.isLightDamage(source) || DamageSourceHelper.isHoly(source);

        boolean isBuffed = DamageSourceHelper.isDarkDamage(source) || DamageSourceHelper.isTensuraMagic(source) ||
                DamageSourceHelper.isSpiritual(source) || event.getSource().isMagic();

        float buff = instance.isMastered(player) ? 20 : 10;

        if (isImmune) {
            event.setCanceled(true);

            applyHealth(target, amount);
        }

        if (isBuffed) {
            event.setAmount(event.getAmount() * buff);
        }

        if (event.getSource().isMagic()) {
            event.setAmount(event.getAmount() * 0.25f);
        }
    }

    private int copyChance(LivingEntity owner, ManasSkillInstance pride, ManasSkill targetSkill) {
        if (this.cantCopy(targetSkill)) {
            return 0;
        } else {
            return 100;
        }
    }

    private boolean cantCopy(ManasSkill manasSkill) {
        if (manasSkill instanceof SpiritualMagic) {
            return true;
        } else {
            boolean var10000;
            if (manasSkill instanceof Skill skill) {
                if (skill.getType().equals(SkillType.ULTIMATE)) {
                    var10000 = !MythosSkillsConfig.ALLOW_ULTIMATE_COPYING.get();
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    private static void applyHealth(LivingEntity entity, float amount) {
        float currentAbsorption = entity.getAbsorptionAmount();
        entity.setAbsorptionAmount(currentAbsorption + amount);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!instance.isToggled()) return;
            this.breederReactor(instance, player);
            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:wrath_boost"), entity.getId(), 0.0, 1.0, 0.0, true));

            SkillStorage userStorage = SkillAPI.getSkillsFrom(player);
            List<Skill> learnedSkills = userStorage.getLearnedSkills().stream()
                    .map(ManasSkillInstance::getSkill)
                    .filter(Objects::nonNull)
                    .filter(Skill.class::isInstance)
                    .map(Skill.class::cast)
                    .toList();

            for (Skill skill : learnedSkills) {
                String name = skill.getName().toString().toLowerCase();
                if (name.contains("pure") || name.contains("purification") ||
                        name.contains("purity") || name.contains("hope") ||
                        name.contains("life") || name.contains("regeneration") ||
                        name.contains("heal") || name.contains("holy") ||
                        name.contains("chef") || name.contains("great") ||
                        name.contains("saviour") || name.contains("chosen") ||
                        name.contains("yehoshuah") || name.contains("blessing") ||
                        name.contains("light") || name.contains("good") ||
                        name.contains("hero") || name.contains("angel")) {

                    userStorage.forgetSkill(this);
                }
            }
        }
    }

    private void breederReactor(ManasSkillInstance instance, Player player) {
        if (!UniqueSkills.WRATH.get().isHeld(player) || !UltimateSkills.SATANAEL.get().isHeld(player)) {
            TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                double maxMP = player.getAttributeValue(TensuraAttributeRegistry.MAX_MAGICULE.get());
                double mpGain = maxMP * 0.1;
                cap.setMagicule(cap.getMagicule() + mpGain);
                double rampageChance = cap.getMagicule() > maxMP ? 1.0 : 0.5;
                if (instance.isMastered(player)) {
                    rampageChance *= 1.5;
                }

                if ((double) player.getRandom().nextFloat() < rampageChance) {
                    MobEffectInstance effectInstance = player.getEffect(TensuraMobEffects.RAMPAGE.get());
                    int level = 0;
                    if (effectInstance != null) {
                        level = effectInstance.getAmplifier() + 2;
                    }

                    SkillHelper.addEffectWithSource(player, player, TensuraMobEffects.RAMPAGE.get(), 1200, level, false, false, false, true);
                }

            });
            TensuraPlayerCapability.sync(player);
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (isInSlot(entity)) {
            LivingEntity target = event.getEntity();
            MobEffectInstance drowsiness = target.getEffect(TensuraMobEffects.DROWSINESS.get());
            MobEffectInstance slowness = target.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            MobEffectInstance weakness = target.getEffect(MobEffects.WEAKNESS);

            int lvl1 = 0;
            int lvl2 = 0;
            int lvl3 = 0;

            if (drowsiness != null) {
                lvl1 = drowsiness.getAmplifier() + 1;
            }

            if (slowness != null) {
                lvl2 = slowness.getAmplifier() + 1;
            }

            if (weakness != null) {
                lvl3 = weakness.getAmplifier() + 1;
            }


            SkillHelper.addEffectWithSource(target, entity, TensuraMobEffects.DROWSINESS.get(), 1200, lvl1, false, false, false, false);
            SkillHelper.addEffectWithSource(target, entity, MobEffects.WEAKNESS, 1200, lvl2, false, false, false, false);
            SkillHelper.addEffectWithSource(target, entity, MobEffects.MOVEMENT_SLOWDOWN, 1200, lvl3, false, false, false, false);

            int amount1 = (int) (SkillHelper.getMP(target, false) / 10);
            int amount2 = (int) (SkillHelper.getAP(target, false) / 10);

            SkillHelper.drainMP(target, entity, amount1, false);
            SkillHelper.drainAP(target, entity, amount2);
        }
    }

    @Override
    public int modes() {
        return 3;
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Corruption");
            case 2 -> Component.literal("Weight of Sin");
            case 3 -> Component.literal("Great Decay");
            default -> Component.empty();
        };
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    private int getTotalKills(Player player) {
        return ((ServerPlayer) player).getStats().getValue(Stats.CUSTOM.get(Stats.PLAYER_KILLS));
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        // Weight of Sin
        Level level = entity.level;
        if (instance.getMode() == 2) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 15);
            int conditions = 0;
            SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);

            List<Skill> learnedSkills = targetStorage.getLearnedSkills().stream()
                    .map(ManasSkillInstance::getSkill)
                    .filter(Objects::nonNull)
                    .filter(Skill.class::isInstance)
                    .map(Skill.class::cast)
                    .toList();

            for (Skill skill : learnedSkills) {
                String name = skill.getName().toString().toLowerCase();
                if (name.contains("crimson") || name.contains("dark") || name.contains("apophis") || name.contains("sin") ||
                        name.contains("pride") || name.contains("lust") || name.contains("carnage") || name.contains("sloth") ||
                        name.contains("profanity") || name.contains("gluttony") || name.contains("traitor") || name.contains("betrayal") ||
                        name.contains("villain") || name.contains("avenger") || name.contains("shadow") || name.contains("envy") ||
                        name.contains("greed") || name.contains("wrath") || name.contains("vainglory")) {
                    conditions += 1;
                }
            }

            if (TensuraEPCapability.getHumanKill(target) > 1000) {
                conditions += 1;
            }

            if (target instanceof Player player) {
                if (TensuraPlayerCapability.isTrueDemonLord(player) || TensuraPlayerCapability.isTrueHero(player)) {
                    conditions += 1;
                }

                if (getTotalKills(player) > 5) {
                    conditions += 1;
                }
            }

            for (Skill skill : learnedSkills) {
                String name = skill.getName().toString().toLowerCase();
                if (name.contains("pure") || name.contains("purification") || name.contains("purity") || name.contains("hope") ||
                        name.contains("life") || name.contains("regeneration") || name.contains("heal") || name.contains("holy") ||
                        name.contains("chef") || name.contains("great") || name.contains("saviour") || name.contains("chosen") ||
                        name.contains("yehoshuah") || name.contains("blessing") || name.contains("light") || name.contains("good") ||
                        name.contains("hero") || name.contains("angel")) {
                    conditions += 1;
                }
            }

            if (conditions > 1) {
                applyRandomEffects(target, level.random);
            }

            if (conditions > 2) {
                float hpDmg = target.getMaxHealth();
                float shpDmg = (float) TensuraEPCapability.getSpiritualHealth(target);

                DamageSourceHelper.directSpiritualHurt(target, entity, shpDmg);
                DamageSource damageSource = new DamageSource(TensuraDamageSources.DARK_ATTACK);
                target.hurt(damageSource, hpDmg);
            }

            if (conditions > 3) {
                double apDrain = SkillHelper.getAP(target, true);
                double mpDrain = SkillHelper.getMP(target, true);

                SkillHelper.drainMP(target, entity, mpDrain, false);
                SkillHelper.drainAP(target, entity, apDrain);
            }

            if (conditions > 4 && MythosSkillsConfig.fourthQuachilCondition.get()) {
                List<Skill> uniqueSkills = targetStorage.getLearnedSkills().stream()
                        .map(ManasSkillInstance::getSkill)
                        .filter(Objects::nonNull)
                        .filter(Skill.class::isInstance)
                        .map(Skill.class::cast)
                        .filter(skill -> skill.getType() == SkillType.UNIQUE)
                        .toList();

                if (uniqueSkills.isEmpty()) return;
                int randomIndex = target.getRandom().nextInt(uniqueSkills.size());
                Skill skillToForget = uniqueSkills.get(randomIndex);

                targetStorage.forgetSkill(skillToForget);

                if (target instanceof Player targetPlayer) {
                    targetPlayer.displayClientMessage(Component.literal(skillToForget.getName() + "Has been dissolved..")
                            .withStyle(ChatFormatting.GOLD), false);
                }
            }

            if (conditions > 5 && MythosSkillsConfig.fifthQuachilCondition.get()) {
                TensuraEPCapability.resetEverything(target);

                if (target instanceof Player player) {
                    player.getInventory().clearContent();
                }
            }
        }
    }

    private static void applyRandomEffects(LivingEntity target, RandomSource random) {
        Set<String> blacklist = new HashSet<>(MythosSkillsConfig.blacklistedEffects.get());

        List<MobEffect> availableEffects = ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(Objects::nonNull)
                .filter(effect -> {
                    var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                    return key != null && !blacklist.contains(key.toString());
                })
                .collect(Collectors.toList());

        if (availableEffects.isEmpty()) return;

        Collections.shuffle(availableEffects, new java.util.Random(random.nextLong()));

        int count = 1 + random.nextInt(15);

        for (int i = 0; i < count && i < availableEffects.size(); i++) {
            MobEffect effect = availableEffects.get(i);

            int duration = 100 + random.nextInt(301);

            int amplifier = random.nextInt(3);

            target.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        Level level = entity.level;
        if (level.isClientSide) return true;
        if (instance.getMode() == 1) {
            if (heldTicks % 20 == 0) {
                if (SkillHelper.outOfMagicule(entity, instance)) return false;

                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 0.8F);

                TensuraNetwork.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(
                                new ResourceLocation("tensura:strength_sap"),
                                entity.getId(), 0.0, 23, 0.0, true)
                );

                List<LivingEntity> targets = level.getEntitiesOfClass(
                        LivingEntity.class,
                        entity.getBoundingBox().inflate(50),
                        e -> e.isAlive() && !e.is(entity) && !entity.isAlliedTo(e)
                );

                if (!targets.isEmpty()) {
                    double ep = TensuraEPCapability.getEP(entity);
                    if (ep <= 0) return true;

                    float damagePerSecond = (float) (ep / 1000.0);

                    for (LivingEntity target : targets) {
                        if (target instanceof Player p && p.getAbilities().invulnerable) continue;

                        target.hurt(MysticismDamageSources.destroyerHaki(entity), damagePerSecond);

                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SMOKE, 1.0);

                        float drainPercent = instance.isMastered(entity) ? 5 : 10;

                        float mpDrain = (float) SkillHelper.getMP(target, false) / drainPercent;
                        float apDrain = (float) SkillHelper.getAP(target, false) / drainPercent;

                        SkillHelper.drainAP(target, entity, apDrain);
                        SkillHelper.drainMP(target, entity, mpDrain, false);

                        SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
                        List<ManasSkill> targetSkills = targetStorage.getLearnedSkills().stream()
                                .map(ManasSkillInstance::getSkill)
                                .filter(Objects::nonNull)
                                .filter(skill -> !(skill instanceof Skill s && s.getType() == Skill.SkillType.ULTIMATE))
                                .toList();

                        if (target.isDeadOrDying()) {
                            if (TensuraGameRules.canStealSkill(level)) {
                                for (ManasSkill skill : targetSkills) {
                                    SkillPlunderEvent event = new SkillPlunderEvent(target, entity, true, skill);
                                    if (!MinecraftForge.EVENT_BUS.post(event)) {
                                        SkillUtils.learnSkill(entity, event.getSkill());
                                    }
                                }
                            } else {
                                for (ManasSkill skill : targetSkills) {
                                    SkillPlunderEvent event = new SkillPlunderEvent(target, entity, false, skill);
                                    if (!MinecraftForge.EVENT_BUS.post(event)) {
                                        SkillUtils.learnSkill(entity, event.getSkill());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (heldTicks % 60 == 0 && heldTicks > 0) {
                this.addMasteryPoint(instance, entity);
            }

            return true;
        } else if (instance.getMode() == 3 && this.isMastered(instance, entity)) {
            if (entity.level.isClientSide) return true;

            ServerLevel serverLevel = (ServerLevel) entity.level;
            var playerList = serverLevel.getServer().getPlayerList();

            switch (heldTicks) {

                case 1 ->
                        playerList.broadcastSystemMessage(Component.literal("The bells of the end toll beneath a sky of weeping bile."), false);
                case 20 ->
                        playerList.broadcastSystemMessage(Component.literal("Pride is stripped bare, its golden vanity corroded into ash."), false);
                case 40 ->
                        playerList.broadcastSystemMessage(Component.literal("Envy turns to glass, shattering against the weight of the void."), false);
                case 60 ->
                        playerList.broadcastSystemMessage(Component.literal("Wrath is drowned in the rising tide of a stagnant, black sea."), false);
                case 80 ->
                        playerList.broadcastSystemMessage(Component.literal("Sloth claims the bones of the world, slowing the pulse of time."), false);
                case 100 ->
                        playerList.broadcastSystemMessage(Component.literal("Greed dissolves into the very nothingness it sought to claim."), false);
                case 120 ->
                        playerList.broadcastSystemMessage(Component.literal("Gluttony is hollowed out, leaving a hunger that devours light."), false);
                case 140 ->
                        playerList.broadcastSystemMessage(Component.literal("Lust is forgotten, as the warmth of the soul turns to winter."), false);
                case 159 ->
                        playerList.broadcastSystemMessage(Component.literal("I am the sediment of the ages, the final stain upon existence."), false);
                case 160 ->
                        playerList.broadcastSystemMessage(Component.literal("Behold the Great Decay, where even the gods come to wither."), false);

            }

            if (heldTicks >= 160) {

                AABB area = entity.getBoundingBox().inflate(1000.0);
                List<LivingEntity> nearbyMobs = serverLevel.getEntitiesOfClass(LivingEntity.class, area, mob -> !(mob instanceof Player));
                List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, area);

                for (Player p : nearbyPlayers) {
                    p.hurt(MythosDamageSources.EndOfEvil(), (float) TensuraEPCapability.getEP(p));
                    if (MythosSkillsConfig.greatDecay.get()) {
                        TensuraEPCapability.resetEverything(p);
                    }

                    p.playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.0f, 1.0f);

                }

                for (LivingEntity mob : nearbyMobs) {
                    if (!(mob instanceof Player)) {
                        mob.hurt(MythosDamageSources.EndOfEvil(), (float) TensuraEPCapability.getEP(mob));

                        serverLevel.sendParticles(ParticleTypes.SQUID_INK, mob.getX(), mob.getY() + 1, mob.getZ(), 1, 0, 0, 0, 0);
                    }
                }

                instance.setCoolDown(86400);
                return true;
            }
        }

        return true;
    }

    @Override
    public int getMaxHeldTime(ManasSkillInstance instance, LivingEntity living) {
        int e = 0;
        if (instance.getMode() == 3) {
            e = 170;
        }

        return e;
    }
}
