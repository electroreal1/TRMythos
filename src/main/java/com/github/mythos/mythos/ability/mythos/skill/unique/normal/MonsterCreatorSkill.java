package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.registry.attribute.TensuraAttributeRegistry;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MonsterCreatorSkill extends Skill {
    protected static final UUID ABSORB_MONSTER = UUID.fromString("59de7343-64d7-4acf-a988-8ea372045463");

    public MonsterCreatorSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Override
    public double getObtainingEpCost() {
        return 750000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity living) {
        TensuraParticleHelper.addParticlesAroundSelf(living, (ParticleOptions) TensuraParticles.SOUL.get());
    }

    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) {
            return instance.getMode() == 1 ? 2 : instance.getMode() - 1;
        } else {
            return instance.getMode() == 2 ? 1 : instance.getMode() + 1;
        }
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.monster_creator.summon");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.monster_creator.absorb");
                break;
            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 2) {
            CompoundTag tag = instance.getOrCreateTag();
            if (entity instanceof Player player) {
                Level level = player.level;
                SkillStorage playerStorage = SkillAPI.getSkillsFrom(player);

                boolean hasMonsterMerged = playerStorage.getLearnedSkills().stream()
                        .anyMatch(skillInstancex -> skillInstancex.getOrCreateTag().getBoolean("MonsterMerged"));

                double reach = 10.0D;
                Vec3 eyePos = player.getEyePosition(1.0F);
                Vec3 lookVec = player.getLookAngle();
                Vec3 endPos = eyePos.add(lookVec.scale(reach));

                AABB area = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(2.5D);
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                        e -> !(e instanceof Player) && e.isAlive());

                if (entities.isEmpty()) {
                    player.displayClientMessage(
                            Component.literal("No target for Absorbing found.").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)), false);
                    return;
                }

                double playerEP = TensuraEPCapability.getCurrentEP(player);
                int fusedCount = 0;

                AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
                AttributeInstance sizeAttr = player.getAttribute(TensuraAttributeRegistry.SIZE.get());
                AttributeInstance attackRangeAttr = player.getAttribute(ForgeMod.ATTACK_RANGE.get());
                AttributeInstance blockrangeAttr = player.getAttribute(ForgeMod.REACH_DISTANCE.get());

                double currentHealthBonus = maxHealthAttr != null ? maxHealthAttr.getBaseValue() - 20.0D : 0;
                double currentAttackBonus = attackAttr != null ? attackAttr.getBaseValue() - 1.0D : 0;
                double currentArmorBonus = armorAttr != null ? armorAttr.getBaseValue() - 0.0D : 0;
                double currentSizeBonus = sizeAttr != null ? sizeAttr.getBaseValue() - 0.0D : 0;
                double currentAttackRangeBonus = attackRangeAttr != null ? attackRangeAttr.getBaseValue() - 0.0D : 0;
                double currentBlockRangeBonus = blockrangeAttr != null ? blockrangeAttr.getBaseValue() - 0.0D : 0;

                for (LivingEntity target : entities) {
                    double targetEP = TensuraEPCapability.getCurrentEP(target);

                    if (targetEP >= playerEP * 0.85D) {
                        player.displayClientMessage(
                                Component.literal("Creature too strong to fuse with.").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        return;
                    }

                    if (maxHealthAttr != null && currentHealthBonus < 1000.0D) {
                        currentHealthBonus = Math.min(currentHealthBonus + 5.0D, 1000.0D);
                        maxHealthAttr.setBaseValue(20.0D + currentHealthBonus);
                    }

                    if (attackAttr != null && currentAttackBonus < 50.0D) {
                        currentAttackBonus = Math.min(currentAttackBonus + 1.0D, 50.0D);
                        attackAttr.setBaseValue(1.0D + currentAttackBonus);
                    }

                    if (armorAttr != null && currentArmorBonus < 250.0D) {
                        currentArmorBonus = Math.min(currentArmorBonus + 1.0D, 250.0D);
                        armorAttr.setBaseValue(0.0D + currentArmorBonus);
                    }

                    if (sizeAttr != null && currentSizeBonus < 10) {
                        currentSizeBonus = Math.min(currentSizeBonus + 0.05F, 10);
                        sizeAttr.setBaseValue(0.0D + currentSizeBonus);
                    }
                    if (attackRangeAttr != null && currentAttackRangeBonus < 20) {
                        currentAttackRangeBonus = Math.min(currentAttackRangeBonus + 0.5F, 20);
                        sizeAttr.setBaseValue(0.0D + currentAttackRangeBonus);
                    }
                    if (blockrangeAttr != null && currentBlockRangeBonus < 10) {
                        currentBlockRangeBonus = Math.min(currentBlockRangeBonus + 0.5F, 20);
                        sizeAttr.setBaseValue(0.0D + currentBlockRangeBonus);
                    }

                    SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
                    for (ManasSkillInstance targetSkill : targetStorage.getLearnedSkills()) {
                        if (!targetSkill.isTemporarySkill() && targetSkill.getMastery() >= 0 && targetSkill.getSkill() != this && canAbsorb(targetSkill)) {
                            boolean learned = SkillUtils.learnSkill(player, targetSkill.getSkill(), -1);
                            if (learned) {
                                player.displayClientMessage(Component.translatable("tensura.skill.acquire",
                                        new Object[]{targetSkill.getSkill().getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                                        false);
                            }
                        }
                    }


                    fusedCount++;
                    target.discard();
                }

                if (fusedCount > 0) {
                    tag.putBoolean("MonsterMerged", true);
                    tag.putInt("MonsterMergeCount", tag.getInt("MonsterMergeCount") + fusedCount);

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);

                    this.addMasteryPoint(instance, player);

                    player.displayClientMessage(
                            Component.literal("Monster has been absorbed")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)), false);

                } else {
                    player.displayClientMessage(
                            Component.translatable("No Valid Monsters.")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                }
            }
        }
        if (instance.getMode() == 1) {
            if (entity instanceof Player player) {
                Set<EntityType<?>> blacklist = MythosSkillsConfig.getSummonBlacklistedEntities().stream()
                        .map(id -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id)))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                List<EntityType<?>> summonable = ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(et -> LivingEntity.class.isAssignableFrom(et.getBaseClass()))
                        .filter(et -> !blacklist.contains(et))
                        .toList();

                if (summonable.isEmpty()) {
                    player.displayClientMessage(
                            Component.literal("No Valid Monsters.")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                    return;
                }

                EntityType<?> randomType = summonable.get(player.getRandom().nextInt(summonable.size()));
                Entity entityToSummon = randomType.create(player.level);

                if (entityToSummon instanceof LivingEntity summoned) {
                    float mobEP = (float) TensuraEPCapability.getCurrentEP(summoned);
                    double maxMP = TensuraPlayerCapability.getMagicule(player);
                    if (maxMP < mobEP) {
                        player.displayClientMessage(
                                Component.literal("Not Enough Magicules.")
                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        return;
                    }

                    TensuraPlayerCapability.setMagicule(player, maxMP - mobEP);
                    double xOffset = player.getRandom().nextDouble() * 4 - 2;
                    double zOffset = player.getRandom().nextDouble() * 4 - 2;
                    summoned.setPos(player.getX() + xOffset, player.getY(), player.getZ() + zOffset);
                    player.level.addFreshEntity(summoned);

                    player.displayClientMessage(
                            Component.literal("Monster summoned Successfully " + summoned.getName().getString())
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);

                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    protected boolean canAbsorb(ManasSkillInstance instance) {
        if (!instance.isTemporarySkill() && instance.getMastery() >= 0) {
            ManasSkill var3 = instance.getSkill();
            Skill devouredSkill = (Skill)var3;
            return devouredSkill.getType().equals(SkillType.INTRINSIC) ||
                    devouredSkill.getType().equals(SkillType.COMMON) ||
                    devouredSkill.getType().equals(SkillType.EXTRA) ||
                    devouredSkill.getType().equals(SkillType.RESISTANCE
                    );
        } else {
            return false;
        }
    }
}






