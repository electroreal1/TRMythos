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
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class DikeSkill extends Skill {
    public DikeSkill(SkillType type) {super(SkillType.ULTIMATE);}

    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/dike.png");
    }

    @Override
    public double getObtainingEpCost() {return 5000000;}

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.PURITY_SKILL.get()) && TensuraPlayerCapability.isTrueHero(player);
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = Skills.PURITY_SKILL.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
        }
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {return true;}
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {return instance.isToggled();}

    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.dike.invite_to_righteousness");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.dike.annihilation_ray");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.dike.glimpse_of_heaven");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        Player player;
        SkillStorage storage;
        LivingEntity target;

        // Invite to Righteousness
        if (instance.getMode() == 1) {
            if (entity instanceof Player) {
                player = (Player)entity;
            } else {
                return;
            }
            target = (LivingEntity) SkillHelper.getTargetingEntity(LivingEntity.class, (LivingEntity)player, 20.0D, 0.0D, false);
            if (TensuraEPCapability.getPermanentOwner(target) != entity.getUUID())
                return;

            awakeningSubordinate(target, (Player)entity);
        }

        // Annihilation Ray
        if (instance.getMode() == 2) {
            CompoundTag tag = instance.getOrCreateTag();
            instance.getOrCreateTag().putInt("BeamID", 0);
            instance.markDirty();
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity living, int heldTicks) {
        // Annihilation Ray
        if (instance.getMode() == 2) {
            if (living instanceof Player player) {
                if (heldTicks > 0 && heldTicks % 100 == 0) {
                    addMasteryPoint(instance, living);
                }
                double cost = magiculeCost(living, instance);
                spawnSolarBeam(player, instance, cost);
                living.level.playSound(null, living.getX(), living.getY(), living.getZ(),
                        SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 0.5F);
            }
            return true;
        }

        // Glimpse of Heaven
        if (instance.getMode() == 3 && living instanceof ServerPlayer player) {

            if (!isMastered(instance, living)) return true;
            if (heldTicks % 20 == 0 && heldTicks <= 100) {
                int index = heldTicks / 20;
                index = Math.min(index, GLIMPSE_MESSAGES.length - 1);
                player.sendSystemMessage(GLIMPSE_MESSAGES[index]);
            }

            if (heldTicks >= 100) {
                ServerLevel server = (ServerLevel) living.level;

                if (!living.level.isClientSide) {
                    holyExplosion(server, living.position(), 25);
                } else {
                    holyChargeParticles(living, heldTicks);
                }

                double baseDamage = 5000;
                double radius = 25;
                List<LivingEntity> targets = server.getEntitiesOfClass(LivingEntity.class, living.getBoundingBox().inflate(radius), e -> e != living);

                for (LivingEntity target : targets) {
                    int kills = TensuraEPCapability.getHumanKill(target);
                    double dmg = baseDamage + (kills * 100);
                    target.hurt(TensuraDamageSources.HOLY_DAMAGE, (float) dmg);
                }

                instance.setCoolDown(1200);
                return true;
            }
        }
        return true;
    }

    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        super.onRelease(instance, entity, heldTicks);
        if (instance.getMode() == 2 && !instance.onCoolDown()) {
            instance.setCoolDown(10);
        }
    }

    public void onTick(ManasSkillInstance instance, Player player) {
        if (instance.isToggled()) {
            boolean inLight = isInBrightLight(player);

            if (inLight) {
                player.addEffect(new MobEffectInstance((MobEffect) MythosMobEffects.COSTLESS_REGENERATION.get(), 1200, 1, false, false, false));
            } else {
                return;
            }
        }
    }

    private void awakeningSubordinate(LivingEntity target, Player owner) {
        if (target instanceof Player) {
            if (TensuraPlayerCapability.isTrueDemonLord((Player)target)) {
                owner.displayClientMessage(Component.translatable("tensura.evolve.demon_lord.already").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            if (TensuraPlayerCapability.isTrueHero(target)) {
                owner.displayClientMessage(Component.translatable("tensura.evolve.demon_lord.hero").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            TensuraPlayerCapability.getFrom(owner).ifPresent((ownerCap) -> {
                double ownerMaxMagicules = ownerCap.getBaseMagicule();
                double awakeningMpCost = 10000000.0F;
                if (ownerMaxMagicules < awakeningMpCost) {
                    owner.displayClientMessage(Component.translatable("trmythos.skill.mode.dike.not_enough_mp", new Object[]{awakeningMpCost / 1000}).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                } else {
                    TensuraPlayerCapability.getFrom((Player)target).ifPresent((cap) -> {
                            ownerCap.setMagicule(ownerCap.getBaseMagicule() - awakeningMpCost);
                            RaceHelper.awakening((Player)target, true);
                    });
                }
            });
        }
    }

    private void spawnSolarBeam(Player player, ManasSkillInstance instance, double cost) {
        if (player.hasEffect((MobEffect) TensuraMobEffects.MAGIC_INTERFERENCE.get())) {
            player.displayClientMessage((Component)Component.translatable("tensura.skill.magic_interference")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), true);
            return;
        }
        BeamProjectile.spawnLastingBeam((EntityType) TensuraEntityTypes.SOLAR_BEAM.get(), 500.0F, 2.0F, (LivingEntity)player, instance, cost, cost, 0);

    }

    public static boolean isInBrightLight(Player player) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();
        int light = level.getLightEngine().getRawBrightness(pos, 0);

        return light > 10;
    }


    public void onBeingDamaged(LivingHurtEvent event, ManasSkillInstance instance, Player player) {
        if (isInSlot(player)) {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            float amount = event.getAmount();

            if ((DamageSourceHelper.isLightDamage(source)) || (DamageSourceHelper.isHoly(source))) {
                event.setCanceled(true);

                applyHealth(target, amount);
            }

            if (DamageSourceHelper.isDarkDamage(source)) {
                amount = event.getAmount() * 2.0F;
            }

            if (event.getSource().isMagic()) {
                event.setAmount(event.getAmount() * 0.25f);
            }
        }
    }

    private static void applyHealth(LivingEntity entity, float amount) {
        float currentAbsorption = entity.getAbsorptionAmount();
        entity.setAbsorptionAmount(currentAbsorption + amount);
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity living, LivingHurtEvent e) {
        if (instance.isToggled()) {
            if (DamageSourceHelper.isLightDamage(e.getSource()) || DamageSourceHelper.isHoly(e.getSource()) || DamageSourceHelper.isSpiritual(e.getSource())) {
                if (instance.isMastered(living)) {
                    e.setAmount(e.getAmount() * 5.0F);
                } else {
                    e.setAmount(e.getAmount() * 4.0F);
                }
            }

            if (e.getSource().isMagic()) {
                e.setAmount(e.getAmount() * 0.25f);
            }
        }
    }

    private void holyChargeParticles(LivingEntity living, int heldTicks) {
        Level level = living.level;

        double x = living.getX();
        double y = living.getY() + 0.2;
        double z = living.getZ();

        for (int i = 0; i < 3; i++) {
            double angle = (heldTicks * 0.3) + (i * 2.09);
            double radius = 0.8;

            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;

            level.addParticle(ParticleTypes.END_ROD, px, y, pz, 0, 0.01, 0);}

        level.addParticle(ParticleTypes.GLOW, x, y, z, 0, 0.07, 0);
    }

    private void holyExplosion(ServerLevel server, Vec3 pos, double radius) {
        for (int i = 0; i < 200; i++) {
            double angle = server.random.nextDouble() * Math.PI * 2;
            double dist = server.random.nextDouble() * radius;

            double px = pos.x + Math.cos(angle) * dist;
            double pz = pos.z + Math.sin(angle) * dist;
            double py = pos.y + 0.2;

            server.sendParticles(
                    ParticleTypes.GLOW,
                    px, py, pz,
                    1,
                    0.1, 0.1, 0.1,
                    0.01
            );

            server.sendParticles(
                    ParticleTypes.END_ROD,
                    px, py + 0.5, pz,
                    1,
                    0.05, 0.2, 0.05,
                    0.02
            );
        }
    }

    private static final Component[] GLIMPSE_MESSAGES = new Component[]{
            Component.literal("§eI call upon the eternal firmament, where light was born."),
            Component.literal("§6The skies tremble with the hymn of creation’s first dawn."),
            Component.literal("§fStars blaze like lanterns, guiding all towards the sacred."),
            Component.literal("§bThe heavens unfold, revealing their boundless, radiant truth."),
            Component.literal("§dGlimpse of Heaven.")
    };



}

