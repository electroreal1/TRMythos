package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
        // Invite to Righteousness
        if (instance.getMode() == 1) {

        }


    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity living, int heldTicks) {

        if (instance.getMode() == 2) {

        }

        // Glimpse of Heaven
        if (instance.getMode() == 3) {
            if (isMastered(instance, living)) {

                if (heldTicks % 20 == 0 && heldTicks <= 100 && living instanceof ServerPlayer player) {

                    int index = heldTicks / 20;
                    if (index >= GLIMPSE_MESSAGES.length) index = GLIMPSE_MESSAGES.length - 1;

                    player.sendSystemMessage(GLIMPSE_MESSAGES[index]);
                }

                if (heldTicks >= 100) {
                    ServerLevel server = (ServerLevel) living.level;
                    if (living.level.isClientSide) {
                        holyChargeParticles(living, heldTicks);
                    }
                    double radius = 25;
                    double damage = 5000;
                    List<LivingEntity> targets = server.getEntitiesOfClass(
                            LivingEntity.class,
                            living.getBoundingBox().inflate(radius),
                            e -> e != living
                    );
                    for (LivingEntity target : targets) {
                        int kills = TensuraEPCapability.getHumanKill(target);
                        double dmg = damage + (kills * 100);
                        target.hurt(TensuraDamageSources.HOLY_DAMAGE, (float) dmg);
                    }
                    instance.setCoolDown(1200);
                    return true;
                }
            }
        }
        return false;
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

    public static boolean isInBrightLight(Player player) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();
        int light = level.getLightEngine().getRawBrightness(pos, 0);

        return light > 10;
    }

    public void onEntityHurt(LivingHurtEvent event, ManasSkillInstance instance, Player player) {
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
        double y = living.getY() + 1.2;
        double z = living.getZ();

        for (int i = 0; i < 3; i++) {
            double angle = (heldTicks * 0.3) + (i * 2.09);
            double radius = 0.8;

            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;

            level.addParticle(ParticleTypes.END_ROD,
                    px, y, pz,
                    0, 0.01, 0);
        }

        level.addParticle(
                ParticleTypes.GLOW,
                x, y, z,
                0, 0.07, 0
        );
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