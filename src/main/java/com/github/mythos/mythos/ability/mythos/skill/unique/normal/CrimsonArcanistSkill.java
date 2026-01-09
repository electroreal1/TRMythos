package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.mythos.mythos.registry.MythosParticles;
import com.github.mythos.mythos.util.MythosUtils;
import com.github.mythos.mythos.util.damage.MythosDamageSources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import static com.github.mythos.mythos.ability.mythos.skill.unique.normal.CrimsonTyrantSkill.VAMPIRE_ANCESTOR;

public class CrimsonArcanistSkill extends Skill {
    public CrimsonArcanistSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 100000;
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        if (!(entity instanceof Player player)) return;
        if (instance.isTemporarySkill()) return;
        if (!VAMPIRE_ANCESTOR) return;
        TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {

            double magicules =  TensuraPlayerCapability.getBaseMagicule(player);
            double aura = TensuraPlayerCapability.getBaseAura(player);

            Race vampireRace = TensuraRaces.VAMPIRE.get();
            Race currentRace = cap.getRace();

            if (currentRace != vampireRace) {
                cap.setRace(player, vampireRace, true);
                cap.setBaseAura(aura, player);
                cap.setBaseMagicule(magicules, player);
            }
        });
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (instance.isToggled()) {
            double damageDealt = event.getAmount();
            if (!(event.getEntity() instanceof Player player)) return;
            if (damageDealt >= 100) {
                int factor = (int) (damageDealt / 100);
                int mpToRestore = factor * 50;
                TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                    cap.setMagicule(cap.getMagicule() + mpToRestore);
                });
                TensuraPlayerCapability.sync(player);
            }
        }
    }

    @Override
    public void onTakenDamage(ManasSkillInstance instance, LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (DamageSourceHelper.isPhysicalAttack(event.getSource())) {
                float originalDamage = event.getAmount();
                float reduction = originalDamage * 0.15f;

                event.setAmount(originalDamage - reduction);

                if (!player.level.isClientSide) {
                    ((ServerLevel)player.level).sendParticles(MythosParticles.RED_RUNES.get(),
                            player.getX(), player.getY() + 1, player.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                }
            }
        }
    }

    @Override
    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (!this.isInSlot(entity)) return;
        if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof Player player) {

                float maxhp = player.getMaxHealth();
                float currenthp = player.getHealth();

                float missingHpPercent = (maxhp - currenthp) / maxhp;

                int increments = (int) (missingHpPercent * 10);
                float damageMultiplier = 1.0f + (increments * 0.01f);

                float originalDamage = event.getAmount();
                event.setAmount(originalDamage * damageMultiplier);
            }
        }
    }

    @Override
    public int modes() {
        return 2;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 2 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 2) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity living, int heldTicks) {
        if (instance.getMode() == 1) {
            if (living.level.isClientSide) {
                double angle = heldTicks * 0.2;
                double radius = 1.5;

                double xOffset = Math.cos(angle) * radius;
                double zOffset = Math.sin(angle) * radius;

                living.level.addParticle(MythosParticles.RED_RUNES.get(),
                        living.getX() + xOffset, living.getY() + 1.2, living.getZ() + zOffset,
                        living.getX(), living.getY() + 1.2, living.getZ());

                living.level.addParticle(MythosParticles.RED_RUNES.get(),
                        living.getX() - xOffset, living.getY() + 1.2, living.getZ() - zOffset,
                        living.getX(), living.getY() + 1.2, living.getZ());
            }

            if (heldTicks % 100 == 0 && heldTicks > 0) this.addMasteryPoint(instance, living);

            CompoundTag tag = instance.getOrCreateTag();
            tag.putBoolean("IsGrimoireActive", true);
        }
        return true;
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        CompoundTag tag = instance.getOrCreateTag();
        tag.putBoolean("isGrimoireActive", false);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 2) {
            Entity target = MythosUtils.getLookedAtEntity(entity, 30);

            if (target instanceof LivingEntity victim) {
                double maxMP = entity.getAttributeValue((Attribute)TensuraAttributeRegistry.MAX_MAGICULE.get());
                double damage = maxMP * 0.1;

                float cap = 5000;
                if (damage > cap) damage = cap;

                victim.hurt(MythosDamageSources.blood(), (float) damage);

                if (!entity.level.isClientSide) {
                    ServerLevel world = (ServerLevel) entity.level;

                    world.sendParticles(MythosParticles.RED_RUNES.get(), victim.getX(), victim.getY() + 1.0, victim.getZ(), 25,
                            0.2, 0.5, 0.2, 0.15);

                    world.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                            SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 1.2f);
                }
            }
        }
    }
}