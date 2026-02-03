package com.github.mythos.mythos.ability.mythos.skill.ultimate.god;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.manascore.attribute.ManasCoreAttributes;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.util.MythosUtils;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import io.github.Memoires.trmysticism.registry.effects.MysticismMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class Kthanid extends Skill {

    public Kthanid(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public int getMaxMastery() {
        return 5000;
    }

    @Override
    public double getObtainingEpCost() {
        return 50000000.0;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int modes() {
        return 3;
    }

    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Light God, Kthanid").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD);
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("The antithesis to ruin, corruption, sin, and wickedness. Where the world would crumble under the weight of corruption, light breaks through, heralding a new beginning.");
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Essence of Courage");
            case 2 -> Component.literal("Essence of Condemnation");
            case 3 -> Component.literal("End of Evil");
            default -> Component.empty();
        };
    }


    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (entity instanceof ServerPlayer player) {
            TensuraAdvancementsHelper.grant(player, TensuraAdvancementsHelper.Advancements.MASTER_SMITH);
        }

        if (!(entity.getLevel() instanceof ServerLevel serverLevel)) return;
        Component msg = Component.literal("Darkness writhes as a new presence shines upon the world. Fear nestles its way into the empty hearts of the unjust, knowing that the light is soon to swallow them for their misdoings. The God of Light, the true Justice, has been born unto the world.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD);
        serverLevel.players().forEach(player1 -> player1.displayClientMessage(msg, false));

        if (entity instanceof Player player && !instance.isTemporarySkill()) {
            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            Skill greedSkill = Skills.DIKE.get();
            Skill greedSkill1 = UniqueSkills.INFINITY_PRISON.get();
            Skill greedSkill2 = UniqueSkills.GREAT_SAGE.get();
            Skill greedSkill3 = UniqueSkills.ABSOLUTE_SEVERANCE.get();
            Skill greedSkill4 = UniqueSkills.MURDERER.get();
            Skill greedSkill5 = UniqueSkills.UNYIELDING.get();
            storage.getSkill(greedSkill).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill1).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill2).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill3).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill4).ifPresent(storage::forgetSkill);
            storage.getSkill(greedSkill5).ifPresent(storage::forgetSkill);
        }

        GodClassHandler.get(serverLevel).setKthanidObtained(true);
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.DIKE.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.INFINITY_PRISON.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.GREAT_SAGE.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.ABSOLUTE_SEVERANCE.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.MURDERER.get()) &&
                SkillUtils.isSkillMastered(player, UniqueSkills.UNYIELDING.get());
    }

    private double getMaxEP(LivingEntity entity) {
        if (entity instanceof Player p)
            return TensuraPlayerCapability.getBaseMagicule(p) + TensuraPlayerCapability.getBaseAura(p);
        return TensuraEPCapability.getEP(entity);
    }


    private double getCurrentEP(LivingEntity entity) {
        if (entity instanceof Player p)
            return TensuraPlayerCapability.getMagicule(p) + TensuraPlayerCapability.getAura(p);
        return TensuraEPCapability.getEP(entity);
    }

    private void consumeEP(LivingEntity entity, double amount) {
        if (entity instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                double currentMP = cap.getMagicule();
                if (currentMP >= amount) {
                    cap.setMagicule(currentMP - amount);
                } else {
                    double remainder = amount - currentMP;
                    cap.setMagicule(0);
                    cap.setAura(Math.max(0, cap.getAura() - remainder));
                }
            });
        } else {
            double current = TensuraEPCapability.getEP(entity);
            TensuraEPCapability.setLivingEP(entity, Math.max(0, current - amount));
        }
    }


    @Override
    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent event) {
        double critMult = attacker.getAttributeValue(ManasCoreAttributes.CRIT_MULTIPLIER.get());

        event.setAmount((float) (event.getAmount() * critMult));

        if (attacker.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, event.getEntity().getX(), event.getEntity().getY() + 1, event.getEntity().getZ(), 10, 0.2, 0.2, 0.2, 0.1);
        }
    }

    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        LivingEntity user = event.getEntity();
        if (event.isCanceled() || !this.isInSlot(user)) return;

        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {

//            if (SkillUtils.hasSkill(attacker, Skills.QUACHIL_UTTAUS.get())) return;

            if (getCurrentEP(attacker) >= (getCurrentEP(user) * 8.0)) return;

            if (instance.isToggled()) {
                double cost = event.getAmount() * 10.0;
                if (getCurrentEP(user) >= cost) {
                    consumeEP(user, cost);
                    user.level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.2F);
                    event.setCanceled(true);
                    return;
                }
            }

            event.setCanceled(true);
            user.level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.5F);
        }
    }

    @Override
    public void onDeath(ManasSkillInstance instance, LivingDeathEvent event) {
        if (event.isCanceled()) return;
        LivingEntity deadEntity = event.getEntity();
        LivingEntity owner = event.getEntity();
        if (owner == null || !instance.isToggled()) return;

        if (deadEntity != owner && deadEntity.isAlliedTo(owner)) {
            double cost = getMaxEP(owner) * 0.05;
            if (getCurrentEP(owner) >= cost) {
                event.setCanceled(true);
                consumeEP(owner, cost);
                fullRestore(deadEntity);
                owner.level.playSound(null, deadEntity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8F, 1.0F);
            }
        }

        if (deadEntity == owner) {
            List<LivingEntity> allies = deadEntity.level.getEntitiesOfClass(LivingEntity.class, deadEntity.getBoundingBox().inflate(100), e -> e.isAlive() && e.isAlliedTo(owner) && e != owner);

            if (!allies.isEmpty()) {
                LivingEntity anchor = allies.get(0);
                double cost = getMaxEP(anchor) * 0.25;
                if (getCurrentEP(anchor) >= cost) {
                    event.setCanceled(true);
                    consumeEP(anchor, cost);
                    fullRestore(owner);
                    owner.level.playSound(null, owner.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    private void fullRestore(LivingEntity e) {
        e.setHealth(e.getMaxHealth());
        AttributeInstance shp = e.getAttribute(TensuraAttributeRegistry.MAX_SPIRITUAL_HEALTH.get());
        if (shp != null) e.setAbsorptionAmount((float) shp.getValue()); // Framework specific SHP restore
        if (e instanceof Player p) {
            TensuraPlayerCapability.getFrom(p).ifPresent(cap -> {
                cap.setMagicule(cap.getBaseMagicule());
                cap.setAura(cap.getBaseAura());
            });
        }
    }


    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (SkillHelper.outOfMagicule(entity, instance)) return;

        if (instance.getMode() == 1) {
            LivingEntity target = MythosUtils.getLookedAtEntity(entity, 30);
            if (target != null) {
                int kills = TensuraEPCapability.getHumanKill(target);
                float damage = Math.min(kills * 1000.0f, 500000.0f);

                SeveranceCutterProjectile spaceCutter = new SeveranceCutterProjectile(entity.getLevel(), entity);
                spaceCutter.setSpeed(5F);
                spaceCutter.setDamage(damage);
                spaceCutter.setSize(this.isMastered(instance, entity) ? 8.0F : 5.0F);
                spaceCutter.setMpCost(this.magiculeCost(entity, instance));
                spaceCutter.setSkill(instance);
                spaceCutter.setNoGravity(true);
                spaceCutter.setPosAndShoot(entity);
                spaceCutter.setPosDirection(entity, TensuraProjectile.PositionDirection.MIDDLE);

                entity.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.5F);
            }
        }

        if (instance.getMode() == 2) {
            LivingEntity target = getEnhancedTarget(entity, 30);
            if (target != null && getCurrentEP(target) < (getCurrentEP(entity) * 0.5)) {
                consumeEP(target, getMaxEP(target) * 0.75);
                target.hurt(DamageSource.MAGIC, target.getMaxHealth() * 0.5f);
                target.addEffect(new MobEffectInstance(MysticismMobEffects.NECROSIS.get(), 600, 2));
            }
        }
    }

    public LivingEntity getEnhancedTarget(LivingEntity searcher, double range) {
        Vec3 eyePos = searcher.getEyePosition(1.0F);
        Vec3 lookVec = searcher.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(range));

        AABB searchArea = searcher.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(2.0D);
        List<LivingEntity> candidates = searcher.level.getEntitiesOfClass(LivingEntity.class, searchArea, entity -> entity != searcher && entity.isAlive() && entity.isPickable());

        LivingEntity closestTarget = null;
        double minDistance = range;

        for (LivingEntity candidate : candidates) {
            AABB targetBox = candidate.getBoundingBox().inflate(candidate.getPickRadius() + 0.5D);
            Optional<Vec3> hit = targetBox.clip(eyePos, endPos);

            if (targetBox.contains(eyePos)) {
                if (minDistance >= 0.0D) {
                    closestTarget = candidate;
                    minDistance = 0.0D;
                }
            } else if (hit.isPresent()) {
                double distanceToHit = eyePos.distanceTo(hit.get());
                if (distanceToHit < minDistance || minDistance == 0.0D) {
                    if (searcher.hasLineOfSight(candidate)) {
                        closestTarget = candidate;
                        minDistance = distanceToHit;
                    }
                }
            }
        }
        return closestTarget;
    }


    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int ticks) {
        if (instance.getMode() == 3 && instance.isMastered(entity)) {
            if (entity.level.isClientSide) return true;

            ServerLevel serverLevel = (ServerLevel) entity.level;
            var playerList = serverLevel.getServer().getPlayerList();

            switch (ticks) {

                case 1 -> playerList.broadcastSystemMessage(Component.literal("§fBy the light that stood before sin learned its name,"), false);
                case 20 -> playerList.broadcastSystemMessage(Component.literal("§fBy purity unbroken since the first dawn breathed,"), false);
                case 40 -> playerList.broadcastSystemMessage(Component.literal("§fI call upon the radiance that denies corruption,"), false);
                case 60 -> playerList.broadcastSystemMessage(Component.literal("§fLet false divinity be exposed beneath holy sight."), false);
                case 80 -> playerList.broadcastSystemMessage(Component.literal("§fMajin born of taint, your borrowed grace expires."), false);
                case 100 -> playerList.broadcastSystemMessage(Component.literal("§fSeeds of tyranny, return to the soil of nothingness."), false);
                case 120 -> playerList.broadcastSystemMessage(Component.literal("§lDemon kings crowned by blood, your reign ends now."), false);
                case 140 -> playerList.broadcastSystemMessage(Component.literal("§oWhere darkness stood, only beginning shall remain."), false);
                case 159 -> playerList.broadcastSystemMessage(Component.literal("§f§llet heaven's virtue descend and reclaim the land."), false);

            }

            if (ticks >= 160) {
                playerList.getPlayers().forEach(p -> {
                    if (TensuraPlayerCapability.isDemonLordSeed(p) || TensuraPlayerCapability.isTrueDemonLord(p) || TensuraEPCapability.isMajin(p)) {
                        p.die(MythosDamageSources.EndOfEvil());
                        if (MythosSkillsConfig.endOfEvilReset.get()) {
                            TensuraEPCapability.resetEverything(p);
                        }
                    }
                    p.playNotifySound(SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.0f, 1.0f);
                });

                AABB area = entity.getBoundingBox().inflate(1000.0);
                List<LivingEntity> nearbyMobs = serverLevel.getEntitiesOfClass(LivingEntity.class, area, mob -> !(mob instanceof Player));

                for (LivingEntity mob : nearbyMobs) {
                    if (TensuraEPCapability.isMajin(mob)) {
                        mob.die(MythosDamageSources.EndOfEvil());

                        serverLevel.sendParticles(ParticleTypes.FLASH, mob.getX(), mob.getY() + 1, mob.getZ(), 1, 0, 0, 0, 0);
                    }
                }

                instance.setCoolDown(86400);
                return true;
            }
            return true;
        }
        return true;
    }

}