package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static com.github.manasmods.tensura.ability.skill.unique.CookSkill.COOK;

public class FalseHeroSkill extends Skill {
    public FalseHeroSkill(SkillType type) {
        super(SkillType.UNIQUE);
    }

    @Nullable
    @Override
    public MutableComponent getColoredName() {
        return Component.literal("False Hero");
    }

    @Override
    public Component getSkillDescription() {
        return Component.literal("You were supposed to be the hero of the world, the one who shall bring light in dark times. And yet you failed everyone. A monster and a mistake, that is what you are.");
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (living instanceof Player player) {
            TensuraPlayerCapability.getFrom(player).ifPresent(cap -> cap.setBlessed(true));
            TensuraEPCapability.getFrom(player).ifPresent(cap -> cap.setChaos(true));

            TensuraEPCapability.sync(player);
            TensuraPlayerCapability.sync(player);
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide) return;

        Player player = (entity instanceof Player p) ? p : null;
        if (player == null) return;

        Random random = (Random) entity.getRandom();

        if (entity.tickCount % 20 == 0) {
            if (random.nextInt(100) < 5) {
                if (!instance.isMastered(entity)) {
                    instance.setMastery(Math.max(0, instance.getMastery() - 10));
                } else {
                    TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
                        double magicule = cap.getBaseMagicule() * 0.975;
                        double aura = cap.getBaseAura() * 0.975;
                        cap.setBaseMagicule(magicule, entity);
                        cap.setBaseAura(aura, entity);
                    });
                }
            }
        }

        AABB area = player.getBoundingBox().inflate(32.0);
        List<Mob> mobs = player.level.getEntitiesOfClass(Mob.class, area);
        for (Mob mob : mobs) {
            if (mob.getTarget() != player) {
                mob.setTarget(player);
            }
        }
    }

    private boolean activatedChaoticFate(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (tag.getInt("ChaoticFate") < 100) {
            return false;
        } else {
            return instance.isMastered(entity) && instance.isToggled() || tag.getBoolean("ChaoticFateActivated");
        }
    }

    public void onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingHurtEvent event) {
        if (instance.getMode() == 2) return;
        CompoundTag tag = instance.getOrCreateTag();
        if (this.activatedChaoticFate(instance, entity)) {
            if (!instance.onCoolDown()) {
                LivingEntity target = event.getEntity();
                AttributeInstance health = target.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    double amount = event.getAmount();
                    AttributeModifier chefModifier = health.getModifier(COOK);
                    if (chefModifier != null) {
                        amount -= chefModifier.getAmount();
                    }

                    AttributeModifier attributemodifier = new AttributeModifier(COOK, "Cook", amount * -1.0, AttributeModifier.Operation.ADDITION);
                    health.removeModifier(attributemodifier);
                    health.addPermanentModifier(attributemodifier);
                    if (!instance.isMastered(entity) || !instance.isToggled()) {
                        tag.putBoolean("ChaoticFateActivated", false);
                    }

                    this.addMasteryPoint(instance, entity);
                    instance.setCoolDown(1);
                    entity.getLevel().playSound(null, entity.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.AMBIENT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 2) {
            Strength(entity, instance);
        }
    }

    private void Strength(LivingEntity caster, ManasSkillInstance instance) {
        if (caster.getRandom().nextFloat() < 0.05f) {
            if (caster instanceof Player player) {
                player.displayClientMessage(Component.literal("§cThe world ignores your plea..."), true);
            }
            return;
        }

        Level world = caster.level;
        world.explode(caster, caster.getX(), caster.getY(), caster.getZ(), 4.0F, Explosion.BlockInteraction.NONE);

        AABB area = caster.getBoundingBox().inflate(6.0);
        world.getEntitiesOfClass(LivingEntity.class, area).forEach(e -> {
            if (e != caster) {
                e.hurt(DamageSource.IN_FIRE, 33.3f);
                e.hurt(DamageSource.DROWN, 33.3f);
                e.hurt(TensuraDamageSources.lightning(caster), 33.3f);
            }
        });
    }
}