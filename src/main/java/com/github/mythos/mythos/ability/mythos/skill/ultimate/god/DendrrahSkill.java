package com.github.mythos.mythos.ability.mythos.skill.ultimate.god;

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
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class DendrrahSkill extends Skill {
    private static final UUID DENDRRAH_DAMAGE_UUID = UUID.fromString("f56df6f3-1847-4bd2-9d6b-48c57b6a91e1");
    private static final UUID DENDRRAH_SPEED_UUID = UUID.fromString("4b8ae36b-3740-4690-84cd-694db333ca1a");
    private static final UUID DENDRRAH_ARMOR_UUID = UUID.fromString("4b8ae90b-3740-4690-84cd-694db333ca1a");

    public DendrrahSkill() {
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
    public int getMaxMastery() {
        return 3000;
    }

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        if (!(player.level instanceof ServerLevel world)) return false;
        GodClassHandler godClassHandler = GodClassHandler.get(world);
        if (!EnableUltimateSkillObtainment()) return false;

        return TensuraEPCapability.getCurrentEP(player) >= getObtainingEpCost() && SkillUtils.isSkillMastered(player, Skills.ARES.get()) && SkillUtils.isSkillMastered(player, Skills.RAVANA.get()) && !godClassHandler.isDendrahhObtained();
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!(entity instanceof Player player) || !(entity.level instanceof ServerLevel serverLevel)) return;

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        SkillAPI.getSkillRegistry().forEach(skill -> {
            String skillName = Objects.requireNonNull(skill.getName()).getString().toLowerCase();
            if (skillName.contains("resistance") || skillName.contains("nullification")) {
                storage.learnSkill(skill);
            }
        });

        Component msg = Component.literal("Violence, suffering, bloodshed... The Apocalypse God has arisen. An indescribable rage fills the hearts of all life!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
        serverLevel.players().forEach(p -> p.displayClientMessage(msg, false));

        if (!instance.isTemporarySkill()) {
            Skill greedSkill = Skills.ARES.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
        }

        GodClassHandler.get(serverLevel).setDendrahhObtained(true);
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (SkillUtils.hasSkill(player, Skills.DENDRRAH.get())) {
            applyModifier(player, Attributes.ATTACK_DAMAGE, DENDRRAH_DAMAGE_UUID, 10.0);
            applyModifier(player, Attributes.ATTACK_SPEED, DENDRRAH_SPEED_UUID, 0.2);
            applyModifier(player, Attributes.ARMOR, DENDRRAH_ARMOR_UUID, 50.0);
        }
    }

    private static void applyModifier(Player player, Attribute attr, UUID id, double amount) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst != null) {
            inst.removeModifier(id);
            inst.addTransientModifier(new AttributeModifier(id, "apocalypse_boost", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent e) {
        if (this.isInSlot(entity)) {
            LivingEntity target = e.getEntity();
            AttributeInstance barrier = target.getAttribute((Attribute) TensuraAttributeRegistry.BARRIER.get());
            if (barrier != null) {
                barrier.setBaseValue(0);
            }
            entity.level.playSound(null, target.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 1.0F, 0.5F);
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

    @Override
    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("War God Release");
            case 2 -> Component.literal("Providence Blade");
            case 3 -> Component.literal("Eternal War");
            case 4 -> Component.literal("Cataclysm");
            default -> Component.empty();
        };
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;
        // War God Release
        if (instance.getMode() == 1) {

        }

        // Providence Blade
        if (instance.getMode() == 2) {
            SeveranceCutterProjectile cutter = new SeveranceCutterProjectile(entity.level, entity);
            double userEP = TensuraEPCapability.getEP(player);
            double targetEP = 0;

            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 30);
            if (target != null) targetEP = TensuraEPCapability.getEP(target);

            float baseDmg = instance.isMastered(entity) ? 2000F : 1000F;
            float epBonus = (float) Math.max(0, (userEP - targetEP) / 1000.0);

            cutter.setDamage(Math.min(100000, baseDmg + epBonus));
            cutter.setSize(this.isMastered(instance, entity) ? 8.0F : 5.0F);
            cutter.setNoGravity(true);
            cutter.setSkill(instance);
            cutter.setPosAndShoot(entity);
            cutter.setPosDirection(entity, TensuraProjectile.PositionDirection.MIDDLE);
            entity.level.addFreshEntity(cutter);
            entity.swing(InteractionHand.MAIN_HAND, true);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        // Cataclysm
        if (instance.getMode() == 4) {
            if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance)) return false;

            if (heldTicks % 2 == 0) {
                TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new RequestFxSpawningPacket(new ResourceLocation("tensura:demon_lord_haki"),
                                entity.getId(), 0.0, 1.0, 0.0, true));
            }

            List<LivingEntity> targets = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(20.0), (living) -> living != entity && living.isAlive() && !living.isAlliedTo(entity));

            for (LivingEntity target : targets) {
                if (heldTicks % 40 == 0) {
                    applyBadRandomEffects(target, entity.level.random);
                    target.hurt(TensuraDamageSources.bloodRay(entity), 1000);
                    HakiSkill.hakiPush(target, entity, 2);
                }
            }
            return true;
        }

        // Eternal War
        if (instance.getMode() == 3) {
            if (heldTicks % 60 == 0) {
                if (!(entity.level instanceof ServerLevel serverLevel)) return true;

                for (ServerPlayer playerTarget : serverLevel.players()) {
                    applyApocalypseEffect(playerTarget);
                }

                AABB area = entity.getBoundingBox().inflate(100.0);
                List<LivingEntity> nearbyMobs = serverLevel.getEntitiesOfClass(LivingEntity.class, area,
                        mob -> !(mob instanceof Player) && mob.isAlive());

                for (LivingEntity mob : nearbyMobs) {
                    applyApocalypseEffect(mob);
                }

                instance.addMasteryPoint(entity);
            }
            return true;
        }

        return true;
    }

    private void applyApocalypseEffect(LivingEntity target) {
        target.addEffect(new MobEffectInstance(TensuraMobEffects.RAMPAGE.get(), 100, 0));

        TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target),
                new RequestFxSpawningPacket(new ResourceLocation("tensura:wrath_boost"),
                        target.getId(), 0.0, 1.0, 0.0, true));
    }

    private void applyBadRandomEffects(LivingEntity target, RandomSource random) {
        List<MobEffect> badEffects = ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(e -> !e.isBeneficial()).toList();

        for (int i = 0; i < 2; i++) {
            MobEffect effect = badEffects.get(random.nextInt(badEffects.size()));
            target.addEffect(new MobEffectInstance(effect, 200, 1));
        }
    }


}




