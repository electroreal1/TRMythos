package com.github.mythos.mythos.ability.mythos.skill.ultimate.king;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.particle.TensuraParticles;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.menu.SoundSwapperMenu;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class OrpheusSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("e85d6c3a-fbdc-4e7b-9e3f-2d50b6578425");

    public OrpheusSkill(SkillType type) {
        super(SkillType.ULTIMATE);
    }

    @Override
    public double getObtainingEpCost() {
        return 5000000;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, UltimateSkills.APOLLO.get());
    }

    public int getMaxMastery() {
        return 2000;
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity, UnlockSkillEvent event) {
        Skill previousSkill = UltimateSkills.APOLLO.get();
        Skill currentSkill = Skills.ORPHEUS.get();
        SkillStorage storage = SkillAPI.getSkillsFrom(entity);
        if (!SkillUtils.hasSkill(entity, currentSkill)) {
            if (entity instanceof Player player) {
                if (player.isCreative()) {
                    return;
                }

                if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
                    if (storage.getSkill(previousSkill).isPresent()) {
                        storage.forgetSkill(previousSkill);
                    }

                    player.displayClientMessage(Component.translatable("trmysticism.skill.ultimate_upgrade", previousSkill.getName(), currentSkill.getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                }
            }

        }
    }

    @Nullable
    @Override
    public MutableComponent getColoredName() {
        return Component.literal("Orpheus");
    }

    @Override
    public int modes() {
        return 4;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 4 : (instance.getMode() - 1);
        else return (instance.getMode() == 4) ? 1 : (instance.getMode() + 1);
    }

    public double magiculeCost(LivingEntity entity, ManasSkillInstance instance) {
        double var10000 = switch (instance.getMode()) {
            case 1 -> 50000.0;
            case 2 -> 100000.0;
            case 3 -> 150000.0;
            case 4 -> 300000.0;
            default -> 0.0;
        };

        return var10000;
    }

    public Component getModeName(int mode) {
        MutableComponent name = switch (mode) {
            case 1 -> Component.literal("?");
            case 2 -> Component.literal("??");
            case 3 -> Component.literal("Sound Amplification");
            case 4 -> Component.literal("Sound Weaver");
            default -> Component.empty();
        };
        return name;
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.AUDITORY_SENSE.get(), 200, 4, false, false, false));
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        this.onTick(instance, entity);
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        entity.removeEffect(TensuraMobEffects.AUDITORY_SENSE.get());
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    public static void triggerSoundAttack(Player player, float volume, float rangeMultiplier, ManasSkillInstance instance) {
        CompoundTag tag = instance.getOrCreateTag();

        boolean isToggledOn = tag.getBoolean("SoundEnabled");
        boolean isModeActive = instance.getMode() == 3;

        if (isToggledOn || isModeActive) {
            if (!TensuraSkillCapability.isSkillInSlot(player, Skills.ORPHEUS.get())) return;

            if (player.tickCount % 5 != 0) return;

            Level level = player.level;
            float radius = volume * rangeMultiplier;
            float damage = volume * 6.0f;

            if (level.isClientSide) {
                for (int i = 0; i < 20; i++) {
                    level.addParticle(TensuraParticles.SOUND_REQUIEM.get(),
                            player.getX(), player.getY() + 1, player.getZ(),
                            (Math.random() - 0.5) * 0.5, 0.1, (Math.random() - 0.5) * 0.5);
                }
            } else {
                AABB area = player.getBoundingBox().inflate(radius);
                level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player).forEach(target -> {
                    target.hurt(TensuraDamageSources.sonicBlast(player), damage);

                    Vec3 dir = target.position().subtract(player.position()).normalize();
                    target.knockback(volume * 0.5f, -dir.x, -dir.z);
                });

                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, volume * 0.5f, 1.5f);
            }
        }
    }

    public void triggerSoundFromMixin(Player player, float volume) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        storage.getSkill(Skills.ORPHEUS.get()).ifPresent(instance -> {
            triggerSoundAttack(player, volume, 15.0f, instance);
        });
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {
            Level level = entity.level;
            switch (instance.getMode()) {
                case 1:
                    this.sonicBlast(instance, entity, level);
                    break;
                case 2:
                    this.soundWave(instance, entity, level);
                    break;
                case 3:
                    this.toggleSoundAmplification(instance, entity, level);
                    break;
                case 4:
                    if (entity instanceof ServerPlayer serverPlayer) {
                        NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inv, p) ->
                                new SoundSwapperMenu(id, inv), Component.literal("Sound Weaver")), buf -> {});
                    }
                    break;
            }
        }
    }

    @Override
    public Component getSkillDescription() {
        return SkillHelper.comingSoon();
    }

    private void toggleSoundAmplification(ManasSkillInstance instance, LivingEntity entity, Level level) {
        if (!instance.isMastered(entity)) return;
        if (!(entity instanceof Player player)) return;
        CompoundTag tag = instance.getOrCreateTag();

        boolean currentState = tag.getBoolean("SoundEnabled");
        tag.putBoolean("SoundEnabled", !currentState);

        String message = !currentState ? "§aSound Amplification Enabled" : "§cSound Amplification Disabled";
        player.displayClientMessage(Component.literal(message), true);
    }

    private void sonicBlast(ManasSkillInstance instance, LivingEntity entity, Level level) {
        this.addMasteryPoint(instance, entity);
        instance.setCoolDown(1);
        double range = instance.isMastered(entity) ? 32 : 24.0;
        Vec3 target = entity.position().add(entity.getLookAngle().scale(range));
        Vec3 source = entity.position().add(0.0, 1.600000023841858, 0.0);
        Vec3 offSetToTarget = target.subtract(source);
        Vec3 normalizes = offSetToTarget.normalize();
        entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 5.0F, 1.0F);

        for(int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); ++particleIndex) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            ((ServerLevel)level).sendParticles(TensuraParticles.SOUND_REQUIEM.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            AABB aabb = (new AABB(new BlockPos(particlePos.x, particlePos.y, particlePos.z))).inflate(2.0);
            List<LivingEntity> livingEntityList = level.getEntitiesOfClass(LivingEntity.class, aabb, (entityData) -> !entityData.is(entity));
            if (!livingEntityList.isEmpty()) {

                for (LivingEntity pLivingEntity : livingEntityList) {
                    if (!RaceHelper.isSpiritualLifeForm(pLivingEntity)) {
                        pLivingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1, false, false, false));
                        DamageSource damagesource = TensuraDamageSources.mindRequiem(entity);
                        pLivingEntity.hurt(this.sourceWithMP(damagesource, entity, instance), this.isMastered(instance, entity) ? 450.0F : 300.0F);
                    }
                }
            }
        }

    }

    private void soundWave(ManasSkillInstance instance, LivingEntity entity, Level level) {
        this.addMasteryPoint(instance, entity);
        int radius = instance.isMastered(entity) ? 40 : 35;
        instance.setCoolDown(2);
        Vec3 source = entity.position().add(0.0, 1.6, 0.0);
        entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 5.0F, 1.0F);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius), (e) -> !e.is(entity) && e.isAlive());

        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1, false, false, false));
            DamageSource damageSource = TensuraDamageSources.mindRequiem(entity);
            if (target.hurt(this.sourceWithMP(damageSource, entity, instance), 1200.0F)) {
                DamageSourceHelper.directSpiritualHurt(target, entity, 600.0F);
            }
        }

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel)level;

            for(int i = 0; i < 360; i += 10) {
                for(int j = -45; j <= 45; j += 15) {
                    double rad = Math.toRadians(i);
                    double pitch = Math.toRadians(j);
                    double x = Math.cos(rad) * Math.cos(pitch);
                    double y = Math.sin(pitch);
                    double z = Math.sin(rad) * Math.cos(pitch);
                    Vec3 dir = (new Vec3(x, y, z)).normalize();
                    Vec3 pos = source.add(dir.scale(10.0));
                    serverLevel.sendParticles(TensuraParticles.SOUND_REQUIEM.get(), pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }


}

