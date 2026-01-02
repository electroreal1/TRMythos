package com.github.mythos.mythos.ability.mythos.skill.ultimate.god;

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
import com.github.manasmods.tensura.config.TensuraConfig;
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class DendrrahSkill extends Skill {
    private static final UUID DENDRRAH_DAMAGE_UUID = UUID.fromString("f56df6f3-1847-4bd2-9d6b-48c57b6a91e1");
    private static final UUID DENDRRAH_SPEED_UUID = UUID.fromString("4b8ae36b-3740-4690-84cd-694db333ca1a");
    private static final UUID DENDRRAH_ARMOR_UUID = UUID.fromString("4b8ae90b-3740-4690-84cd-694db333ca1a");

    public DendrrahSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        MutableComponent msg = Component.literal("Apocalypse").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
        MutableComponent msg1 = Component.literal(" God").withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD);
        MutableComponent msg2 = Component.literal(" D'endrrah").withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.BOLD);

        msg.append(msg1).append(msg2);
        return msg;
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("The End is nearer than ever, and you will be the cause. Each drop of blood you spill spirals into more - a vicious cycle of bloodshed that threatens to consume the natural order of the world. ");
    }

    @Override
    public double getObtainingEpCost() {
        return 10000000;
    }

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        if (!(player.getLevel() instanceof ServerLevel world)) return false;
        GodClassHandler godClassHandler = GodClassHandler.get(world);
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.ARES.get()) &&
                SkillUtils.isSkillMastered(player, (ManasSkill) Skills.RAVANA.get()) && !godClassHandler.isDendrahhObtained();
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!(entity instanceof Player player)) return;
        if (!(entity.getLevel() instanceof ServerLevel world)) return;
        GodClassHandler godClassHandler = GodClassHandler.get(world);
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);

            SkillAPI.getSkillRegistry().forEach(skill -> {
                if (Objects.requireNonNull(skill.getName()).contains(Component.nullToEmpty("resistance")) ||
                        skill.getName().contains(Component.nullToEmpty("nullification"))) {
                    storage.learnSkill(skill);
                }
            });

            if (!(entity.getLevel() instanceof ServerLevel serverLevel)) return;

            Component msg = Component.literal("Violence, suffering, bloodshed, seem to spark at the most minor slight. Each conflict fuels another, in a cycle that seems to drag the world closer to its inevitable end. The Apocalypse God has arisen, and an indescribably rage fills the hearts of all life...").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD);
            serverLevel.players().forEach(player1 -> player1.displayClientMessage(msg, false));
            godClassHandler.setDendrahhObtained();
            CompoundTag tag = instance.getOrCreateTag();
            tag.putBoolean("ChaoticFateActivated", true);
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level.isClientSide) return;

        double attackBonus = 10;
        double speedBonus = 0.1;
        double defenseBonus = 50;

        applyModifier(player, Attributes.ATTACK_DAMAGE, DENDRRAH_DAMAGE_UUID, attackBonus);
        applyModifier(player, Attributes.ATTACK_SPEED, DENDRRAH_SPEED_UUID, speedBonus);
        applyModifier(player, Attributes.ARMOR, DENDRRAH_ARMOR_UUID, defenseBonus);
    }

    private static void applyModifier(Player player, Attribute attr, UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        inst.addTransientModifier(new AttributeModifier(id, "dendrrah_boost", amount, AttributeModifier.Operation.ADDITION));
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent e) {
        if (instance.isToggled()) {
            LivingEntity target = e.getEntity();
            if (!(TensuraEPCapability.getEP(target) > TensuraEPCapability.getEP(entity) * 2.0)) {
                AttributeInstance attribute = target.getAttribute((Attribute) TensuraAttributeRegistry.BARRIER.get());
                if (attribute != null) {
                    attribute.removeModifiers();
                }

                entity.getLevel().playSound((Player) null, entity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 1.0F, 1.0F);
            }
        }
    }

    private boolean activatedChaoticFate(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (tag.getInt("ChaoticFate") < 100) {
            return false;
        } else {
            return instance.isMastered(entity) && instance.isToggled() ? true : tag.getBoolean("ChaoticFateActivated");
        }
    }

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        CompoundTag tag = instance.getOrCreateTag();
        if (this.activatedChaoticFate(instance, entity)) {
            if (!instance.onCoolDown()) {
                LivingEntity target = event.getEntity();
                AttributeInstance health = target.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    double amount = (double) event.getAmount() / 5;
                    AttributeModifier chefModifier = health.getModifier(CookSkill.COOK);
                    if (chefModifier != null) {
                        amount -= chefModifier.getAmount();
                    }

                    AttributeModifier attributemodifier = new AttributeModifier(CookSkill.COOK, "Cook", amount * -1.0, AttributeModifier.Operation.ADDITION);
                    health.removeModifier(attributemodifier);
                    health.addPermanentModifier(attributemodifier);

                    this.addMasteryPoint(instance, entity);
                    instance.setCoolDown(1);
                    entity.getLevel().playSound((Player) null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.AMBIENT, 1.0F, 1.0F);
                }
            }
        }
    }

    public int modes() {
        return 4;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        // War God Release
        if (instance.getMode() == 1) {

        }

        // Providence Blade
        if (instance.getMode() == 2) {
            SeveranceCutterProjectile spaceCutter = new SeveranceCutterProjectile(entity.getLevel(), entity);
            spaceCutter.setSpeed(5F);

//            float userEP = (float) TensuraEPCapability.getEP(player);
//            float targetEP = (float) TensuraEPCapability.getEP(entity);
//
//
//            float epDifference = userEP - targetEP;
           // float damage = this.isMastered(instance, entity) ? 2000 : 1000 + epDifference;
            float damage = this.isMastered(instance, entity) ? 2000 : 1000;
            if (damage > 100000) {
                damage = 100000;
            }
            spaceCutter.setDamage(damage);
            spaceCutter.setSize(this.isMastered(instance, entity) ? 8.0F : 5.0F);
            spaceCutter.setMpCost(this.magiculeCost(entity, instance));
            spaceCutter.setSkill(instance);
            spaceCutter.setNoGravity(true);
            spaceCutter.setPosAndShoot(entity);
            spaceCutter.setPosDirection(entity, TensuraProjectile.PositionDirection.MIDDLE);
            entity.getLevel().addFreshEntity(spaceCutter);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(3);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.swing(InteractionHand.OFF_HAND, true);
            entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() == 3) {
            if (!(entity.getLevel() instanceof ServerLevel serverLevel)) return false;

            if (heldTicks % 100 == 0) {

                for (Entity e : serverLevel.getEntities(null, new AABB(Double.NEGATIVE_INFINITY, 0, Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY, 256, Double.POSITIVE_INFINITY))) {
                    if (e instanceof LivingEntity living) {
                        int duration = 200;
                        int amplifier = 0;
                        living.addEffect(new MobEffectInstance(TensuraMobEffects.RAMPAGE.get(), duration, amplifier));
                    }
                }
            }
        }

        if (instance.getMode() == 4) {
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
                            Level level = target.level;
                            fearLevel = Math.min(fearLevel, TensuraConfig.INSTANCE.mobEffectConfig.maxFear.get());
                            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.FEAR.get(), 200, fearLevel);
                            target.addEffect(new MobEffectInstance(TensuraMobEffects.FRAGILITY.get(), 1200, fearLevel, false, false, false));
                            target.addEffect(new MobEffectInstance(TensuraMobEffects.CURSE.get(), 1200, fearLevel, false, false, false));
                            target.addEffect(new MobEffectInstance(TensuraMobEffects.MOVEMENT_INTERFERENCE.get(), 1200, fearLevel, false, false, false));
                            target.addEffect(new MobEffectInstance(MysticismMobEffects.MARKED_FOR_DEATH.get(), 1200, 1, false, false, false));
                            SkillHelper.checkThenAddEffectSource(target, entity, Objects.requireNonNull(applyBadRandomEffects(target, level.random)));
                            target.hurt(TensuraDamageSources.bloodRay(entity), 1000);
                            HakiSkill.hakiPush(target, entity, fearLevel);
                        }
                    }
                }
            }

        }

        return true;
    }

    private static MobEffectInstance applyBadRandomEffects(LivingEntity target, RandomSource random) {
        Set<String> blacklist = new HashSet<>(MythosSkillsConfig.blacklistedEffects.get());

        List<MobEffect> availableEffects = ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(Objects::nonNull)
                .filter(effect -> !effect.isBeneficial())
                .filter(effect -> {
                    var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                    return key != null && !blacklist.contains(key.toString());
                })
                .collect(Collectors.toList());

        if (availableEffects.isEmpty()) return null;

        Collections.shuffle(availableEffects, new java.util.Random(random.nextLong()));

        int count = 1 + random.nextInt(2);

        for (int i = 0; i < count && i < availableEffects.size(); i++) {
            MobEffect effect = availableEffects.get(i);

            int duration = 100 + random.nextInt(301);
            int amplifier = random.nextInt(3);

            target.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }
        return null;
    }



}




