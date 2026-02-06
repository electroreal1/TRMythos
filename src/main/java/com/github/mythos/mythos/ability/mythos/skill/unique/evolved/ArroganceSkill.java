package com.github.mythos.mythos.ability.mythos.skill.unique.evolved;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.SkillActivationEvent;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.github.manasmods.manascore.skill.SkillRegistry.SKILLS;

public class ArroganceSkill extends Skill {
    @Nullable
    @Override
    public MutableComponent getName() {
        return Component.literal("Arrogance");
    }

    @Override
    public Component getModeName(int mode) {
        return Component.literal("Vanity Reflection");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("Why should I admire a spark when I can claim the flame? Your efforts are not yours to keep; they are simply the materials I require to build my own divinity.");
    }

    public ArroganceSkill(SkillType type) {
        super(type);
    }

    @Override
    public double getObtainingEpCost() {
        return 500000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, UniqueSkills.PRIDE.get());
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof Player player && !instance.isTemporarySkill()) {
            VoiceOfTheWorld.announceToPlayer(player,
                    "Confirmed. Sin Series Skill [Pride] has successfully evolved into Sin Series Skill [Arrogance].");
        }
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;

        CompoundTag tag = instance.getOrCreateTag();
        ListTag attackList = tag.getList("RegisteredAttacks", Tag.TAG_STRING);

        if (attackList.isEmpty()) return;

        if (entity.isShiftKeyDown() && instance.isMastered(entity)) {
            for (int i = 0; i < attackList.size(); i++) {
                castVanity(entity, String.valueOf(attackList));
            }
            tag.remove("RegisteredAttacks");
        } else {
            int lastIndex = attackList.size() - 1;
            castVanity(entity, attackList.getString(lastIndex));
            attackList.remove(lastIndex);
        }
        instance.markDirty();
    }

    private void castVanity(LivingEntity user, String attackType) {
        if (user.level.isClientSide) return;
        ServerLevel level = (ServerLevel) user.level;

        float baseDmg = user.getPersistentData().getFloat("LastRegisteredDamage");
        if (baseDmg <= 0) baseDmg = 10.0F;

        if (attackType.startsWith("entity:")) {
            String entityId = attackType.replace("entity:", "");
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityId));

            if (type != null) {
                Entity projectile = type.create(level);
                if (projectile != null) {
                    projectile.moveTo(user.getX(), user.getEyeY(), user.getZ(), user.getYRot(), user.getXRot());

                    if (projectile instanceof Projectile p) {
                        p.setOwner(user);
                        p.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 2.5F, 1.0F);
                    }

                    level.addFreshEntity(projectile);
                    user.level.playSound(null, user.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.5F);
                    return;
                }
            }
        }

        String type = attackType.replace("type:", "");
        switch (type) {
            case "arrow":
            case "thrown":
                Arrow arrow = new Arrow(level, user);
                arrow.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 3.0F, 1.0F);
                arrow.setBaseDamage(baseDmg);
                level.addFreshEntity(arrow);
                break;

            case "fireball":
                LargeFireball fireball = new LargeFireball(level, user, user.getLookAngle().x, user.getLookAngle().y, user.getLookAngle().z, 1);
                fireball.setPos(user.getX(), user.getEyeY(), user.getZ());
                level.addFreshEntity(fireball);
                break;

            case "magic":
            case "indirectMagic":
            case "sonic_boom":
                float finalBaseDmg = baseDmg;
                level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(6.0), e -> e != user)
                        .forEach(target -> target.hurt(DamageSource.indirectMagic(user, user), finalBaseDmg));
                level.sendParticles(ParticleTypes.FLASH, user.getX(), user.getY() + 1, user.getZ(), 1, 0, 0, 0, 0);
                break;

            default:
                float finalBaseDmg1 = baseDmg;
                level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(4.0), e -> e != user)
                        .forEach(target -> {
                            target.hurt(DamageSource.mobAttack(user), finalBaseDmg1);
                            target.knockback(1.5, user.getX() - target.getX(), user.getZ() - target.getZ());
                        });
                break;
        }

        user.level.playSound(null, user.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.2F);
    }

    @Override
    public void onBeingDamaged(ManasSkillInstance instance, LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player && !player.level.isClientSide) {
            CompoundTag tag = instance.getOrCreateTag();
            ListTag attackList = tag.getList("RegisteredAttacks", Tag.TAG_STRING);

            int listLimit = instance.isMastered(player) ? 50 : 25;
            if (attackList.size() < listLimit) {
                String entry;
                Entity direct = event.getSource().getDirectEntity();

                if (direct instanceof Projectile) {
                    ResourceLocation loc = ForgeRegistries.ENTITY_TYPES.getKey(direct.getType());
                    entry = "entity:" + (loc != null ? loc.toString() : "minecraft:arrow");
                } else {
                    entry = "type:" + event.getSource().getMsgId();
                }

                attackList.add(StringTag.valueOf(entry));
                tag.put("RegisteredAttacks", attackList);
                tag.putFloat("LastRegisteredDamage", event.getAmount());

                instance.markDirty();
            }
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        super.onTick(instance, entity);
        if (entity.level.isClientSide) return;
        if (!this.isInSlot(entity)) return;

        double range = instance.isMastered(entity) ? 30.0 : 15.0;

        List<LivingEntity> targets = entity.level.getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(range), t -> t != entity);

        for (LivingEntity target : targets) {
            CompoundTag data = target.getPersistentData();

            if (data.contains("Arrogance_RecentSkill") && instance.getCoolDown() <= 0) {
                String skillIdStr = data.getString("Arrogance_RecentSkill");
                Skill detectedSkill = (Skill) SKILLS.getEntries();

                if (detectedSkill != null && !isBlacklisted(detectedSkill)) {
                    copySkillLogic(instance, (Player) entity, detectedSkill);
                    data.remove("Arrogance_RecentSkill");
                }
            }

            if (data.contains("Arrogance_SkillTimer")) {
                int timer = data.getInt("Arrogance_SkillTimer");
                if (timer <= 0) {
                    data.remove("Arrogance_RecentSkill");
                    data.remove("Arrogance_SkillTimer");
                } else {
                    data.putInt("Arrogance_SkillTimer", timer - 1);
                }
            }
        }
    }

    private void copySkillLogic(ManasSkillInstance instance, Player player, Skill targetSkill) {
        double cost = targetSkill.getObtainingEpCost();
        int cooldownTicks = (int) (cost * 2.5);

        player.displayClientMessage(Component.literal("§6Arrogance: §fMirrored §e" +
                Objects.requireNonNull(targetSkill.getName()).getString()), true);
        SkillUtils.learnSkill(player, targetSkill);
        instance.setCoolDown(cooldownTicks);
    }

    private boolean isBlacklisted(Skill skill) {
        String registryName = Objects.requireNonNull(skill.getRegistryName()).toString();

        if (MythosSkillsConfig.ARROGANCE_BLACKLIST().contains(registryName)) {
            return true;
        }

        return skill.getType() == Skill.SkillType.ULTIMATE;
    }


    @SubscribeEvent
    public static void onSkillActivate(SkillActivationEvent event) {
        if (event.getEntity().level.isClientSide) return;
        CompoundTag persistentData = event.getEntity().getPersistentData();
        persistentData.putString("Arrogance_RecentSkill", Objects.requireNonNull(event.getSkillInstance().getSkill().getName()).toString());
        persistentData.putInt("Arrogance_SkillTimer", 2);
    }
}
