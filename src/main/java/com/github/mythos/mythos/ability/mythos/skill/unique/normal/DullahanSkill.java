package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import net.minecraft.sounds.SoundEvents;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class DullahanSkill extends Skill {

    public DullahanSkill() {
        super(SkillType.UNIQUE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nullable
    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 66000.0;
    }

    public double learningCost() {
        return 10000.0;
    }

    @Override
    public int getMaxMastery() {
        return 5000;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {

    }


    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {

    }

    @Override
    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.UNIVERSAL_PERCEPTION.get());
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


    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.dullahan.iris_out");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.dullahan.spine_whip");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.dullahan.soundless_coach");
                break;
            case 4:
                name = Component.translatable("trmythos.skill.mode.dullahan.god_of_sacrifice");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:
                if (SkillHelper.outOfMagicule(entity, instance)) return;

                if (!(entity instanceof Player player)) return;

                ServerLevel level = (ServerLevel) player.level;

                DullahanSlashProjectile slash = new DullahanSlashProjectile(level, player);

                slash.setSpeed(3.0F);
                slash.setDamage(isMastered(instance, player) ? 340.0F : 180.0F);
                slash.setSize(isMastered(instance, player) ? 8.0F : 6.0F);
                slash.setMpCost(magiculeCost(player, instance));
                slash.setSkill(instance);

                slash.setPosAndShoot(player);
                slash.setPosDirection(player, TensuraProjectile.PositionDirection.MIDDLE);

                level.addFreshEntity(slash);

                level.playSound(null,
                        player.blockPosition(),
                        SoundEvents.PLAYER_ATTACK_SWEEP,
                        player.getSoundSource(),
                        1.0F,
                        0.8F);

                player.swing(InteractionHand.MAIN_HAND, true);

                instance.addMasteryPoint(player);
                instance.setCoolDown(20);

                break;
            case 2:
                if (SkillHelper.outOfMagicule(entity, instance)) return;
                if (!(entity instanceof Player player)) return;
                if (instance.onCoolDown()) return;

                ServerLevel level2 = (ServerLevel) player.level;

                TensuraNetwork.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new RequestFxSpawningPacket(
                                new ResourceLocation("tensura:haki"),
                                player.getId(),
                                0.0D,
                                1.0D,
                                0.0D,
                                true
                        )
                );

                level2.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.WITHER_SPAWN,
                        SoundSource.PLAYERS,
                        1.0F,
                        0.8F);

                List<LivingEntity> targets = level2.getEntitiesOfClass(
                        LivingEntity.class,
                        player.getBoundingBox().inflate(15.0D),
                        target -> target.isAlive() && !target.isAlliedTo(player)
                );

                for (LivingEntity target : targets) {

                    if (target instanceof Player p && p.getAbilities().instabuild) continue;

                    target.hurt(
                            com.github.mythos.mythos.util.damage.MythosDamageSources.Horseman(player),
                            40.0F
                    );

                    SkillHelper.checkThenAddEffectSource(
                            target,
                            player,
                            TensuraMobEffects.FEAR.get(),
                            200,
                            17
                    );

                    target.addEffect(new MobEffectInstance(
                            MobEffects.CONFUSION,
                            200,
                            0,
                            false,
                            true
                    ));

                    target.addEffect(new MobEffectInstance(
                            MobEffects.DARKNESS,
                            200,
                            1,
                            false,
                            true
                    ));
                }

                instance.addMasteryPoint(player);
                instance.setCoolDown(10);

                player.swing(InteractionHand.MAIN_HAND, true);
                break;

        }
    }
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {

        if (event.getEntity().level.isClientSide) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        LivingEntity target = event.getEntity();

        if ("trmythos.horseman".equals(event.getSource().getMsgId())) return;

        ManasSkillInstance instance = SkillAPI
                .getSkillsFrom(player)
                .getLearnedSkills()
                .stream()
                .filter(skill -> skill.getSkill() == Skills.DULLAHAN.get())
                .findFirst()
                .orElse(null);

        if (instance == null || !instance.isToggled()) return;

        float remainingHealth = target.getHealth() - event.getAmount();
        float maxHealth = target.getMaxHealth();

        if (remainingHealth > (maxHealth * 0.10f)) return;

        event.setCanceled(true);

        ServerLevel level = (ServerLevel) player.level;

        level.playSound(null,
                target.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT,
                target.getSoundSource(),
                1.0F,
                1.0F);

        target.hurt(
                com.github.mythos.mythos.util.damage.MythosDamageSources.Horseman(player),
                Float.MAX_VALUE
        );

        if (target instanceof ServerPlayer killed) {

            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.getOrCreateTag().putString("SkullOwner",
                    killed.getGameProfile().getName());

            target.spawnAtLocation(head);

        } else {
            target.spawnAtLocation(createLitPumpkinCreeperHead());
        }
    }
    private ItemStack createLitPumpkinCreeperHead() {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);

        GameProfile profile = new GameProfile(java.util.UUID.randomUUID(), "Victim");
        profile.getProperties().put(
                "textures",
                new Property(
                        "textures",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVhYmIyMmU3MjE0NmNlMzdhZjlhN2Y3ZThhYmUzYjI4NTkzOWNlMWE3N2EwZmIzYTdiODUwODc0YzI2YzZiZCJ9fX0="
                )
        );

        CompoundTag tag = new CompoundTag();
        NbtUtils.writeGameProfile(tag, profile);
        head.getOrCreateTag().put("SkullOwner", tag);

        return head;
    }
    public class DullahanSlashProjectile extends SeveranceCutterProjectile {

        public DullahanSlashProjectile(Level level, LivingEntity shooter) {
            super(level, shooter);
        }

        @Override
        protected void onHitEntity(EntityHitResult result) {
            super.onHitEntity(result);

            if (!(result.getEntity() instanceof LivingEntity target)) return;

            target.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS,
                    200,
                    7,
                    false,
                    true
            ));

            target.addEffect(new MobEffectInstance(
                    TensuraMobEffects.TRUE_BLINDNESS.get(),
                    200,
                    7,
                    false,
                    true
            ));
        }
    }
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() != 3)
            return false;
        if (heldTicks % 20 == 0 && SkillHelper.outOfMagicule(entity, instance))
            return false;
        if (heldTicks % 100 == 0 && heldTicks > 0)
            addMasteryPoint(instance, entity);
        entity.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.PRESENCE_CONCEALMENT.get(), 5, 2, false, false, false));
        return true;
    }

}
