package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import com.github.manasmods.tensura.ability.skill.unique.CookSkill;
import com.github.manasmods.tensura.ability.skill.unique.WrathSkill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.entity.magic.skill.HellFlareProjectile;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.UniqueSkills;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.mythos.mythos.ability.mythos.skill.ultimate.ZepiaSkill.ACCELERATION;
import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class TatariSkill extends Skill {

    public static boolean DeadApostleAncestor = true;
    private static final String OSIRIS_EP = "osirisStoredEP";
    private static final String OSIRIS_INDEX = "osirisIndex";
    private static final String LAST_COOLDOWN_ROLL = "lastCooldownRoll";
    private static final int ROLL_INTERVAL_TICKS = 20 * 60 * 5; // 5 minutes
    private static final String TATARI_USES = "tatariUses";
    private static final int TATARI_DURATION = 20 * 60 * 10; // 10 minutes


    public TatariSkill() {
        super(SkillType.ULTIMATE);
    }

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/tatari.png");
    }

    public boolean canBeToggled(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    public double getObtainingEpCost() {
        return 27000000.0;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.ZEPIA.get()) && meetsTatariConditions(player);

    }


    public double learningCost() {
        return 15000.0;
    }

    private Player target;

    private final double baseErrorRate = 1000.0D;

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        TensuraEPCapability.getFrom(entity).ifPresent(cap -> cap.setChaos(true));
        TensuraEPCapability.sync(entity);

        if (!(entity instanceof ServerPlayer player)) return;

        CompoundTag tag = instance.getOrCreateTag();

        if (!instance.isToggled()) return;

        this.gainMastery(instance, entity);


        if (instance.onCoolDown()) return;

        long gameTime = player.level.getGameTime();
        long lastRoll = tag.getLong(LAST_COOLDOWN_ROLL);


        if (gameTime - lastRoll < ROLL_INTERVAL_TICKS) return;


        tag.putLong(LAST_COOLDOWN_ROLL, gameTime);
        instance.markDirty();

        if (player.getRandom().nextFloat() <= 0.25F) {
            instance.setCoolDown(30);

            player.displayClientMessage(Component.literal("Tatari destabilizes... you are exposed.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), true);

            player.level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.2F, 0.8F);
        }
        if (instance.getMode() == 8 && instance.isToggled()) {
            if (instance.getCoolDown() <= 0) {
                instance.setToggled(false);

                if (entity instanceof Player p) {
                    p.displayClientMessage(Component.literal("The night of wallachia ends...").withStyle(ChatFormatting.GRAY), true);
                }
            }
        }

    }


    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        entity.addEffect(new MobEffectInstance(MythosMobEffects.DEAD_REGENERATION.get(), 1200, 1, false, false, false));
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            PlayerList playerList = server.getPlayerList();

            ClientboundPlayerInfoPacket removePacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, player);

            MutableComponent leaveMessage = Component.translatable("multiplayer.player.left", player.getDisplayName()).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));

            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(removePacket);
                target.sendSystemMessage(leaveMessage);
            }
        }
        List<LivingEntity> nearbyEntities = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0D), target -> !target.isAlliedTo(entity) && target.isAlive() && !entity.isAlliedTo(target));

        for (LivingEntity target : nearbyEntities) {
            if (target instanceof Player player && player.getAbilities().invulnerable) continue;

            SkillHelper.checkThenAddEffectSource(target, entity, MobEffects.BLINDNESS, 5, 3);

            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.MAGICULE_POISON.get(), 5, 3);

            SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.INSANITY.get(), 5, 13);
        }
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        MobEffectInstance effectInstance = entity.getEffect(MythosMobEffects.DEAD_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 1) {
            entity.removeEffect(MythosMobEffects.DEAD_REGENERATION.get());
        }
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            PlayerList playerList = server.getPlayerList();

            ClientboundPlayerInfoPacket addPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, player);

            MutableComponent joinMessage = Component.translatable("multiplayer.player.joined", player.getDisplayName()).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(addPacket);
                target.sendSystemMessage(joinMessage);
            }
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event, Player player) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            //???? Ill put something to display to the whole server here or something idk
            // Hi, future hallow here. Im not doing that shit. Sorry XP
            // Hi, future future hallow here. Fuck you past hallow.
            if (!meetsTatariConditions(player)) {

                SkillStorage storage = SkillAPI.getSkillsFrom(player);
                storage.forgetSkill(Skills.TATARI.get());

                player.displayClientMessage(Component.literal("Tatari rejects you. The conditions were not fulfilled.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);

                return;
            }

        }
        if (!DeadApostleAncestor) return;
        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            Race vampireAncestor = TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.VAMPIRE_TRUE_ANCESTOR);
            if (cap.getRace() != vampireAncestor) {
                cap.setRace(player, vampireAncestor, true);
            }
        });

    }

    public int modes() {
        return 7;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 8 : (instance.getMode() - 1);
        else return (instance.getMode() == 8) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.tatari.Consensus");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.tatari.Influence");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.tatari.materialized_fear");
                break;
            case 4:
                name = Component.translatable("trmythos.skill.mode.tatari.fumble_code_apotheosis");
                break;
            case 5:
                name = Component.translatable("trmythos.skill.mode.tatari.bad_news_(Malice)");
                break;
            case 6:
                name = Component.translatable("trmythos.skill.mode.tatari.white_len");
                break;
            case 7:
                name = Component.translatable("trmythos.skill.mode.tatari.osiris");
                break;
            case 8:
                name = Component.translatable("trmythos.skill.mode.tatari.tatari");
                break;
            default:
                name = Component.empty();
        }
        return name;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:
                if (entity instanceof Player player) {
                    ItemStack held = player.getMainHandItem();
                    if (held.hasCustomHoverName()) {
                        String name = held.getHoverName().getString().trim();
                        ServerLevel level = (ServerLevel) player.getLevel();
                        LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5000), e -> e.getName().getString().equalsIgnoreCase(name)).stream().findFirst().orElse(null);
                        if (target != null) {
                            if (!SkillHelper.outOfMagicule(player, instance)) {
                                player.teleportTo(target.getX(), target.getY(), target.getZ());
                                level.playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                                TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.PORTAL, 1.0D);
                                addMasteryPoint(instance, player);
                                instance.setCoolDown(100);

                                return;
                            }
                        }
                    }
                }
                if (!SkillHelper.outOfMagicule(entity, instance)) {
                    if (entity instanceof Player player) {
                        TensuraSkillCapability.getFrom(player).ifPresent(cap -> {
                            if (player.isCrouching()) {
                                int mode = cap.getAnalysisMode();
                                switch (mode) {
                                    case 1:
                                        cap.setAnalysisMode(2);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.block").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                    case 2:
                                        cap.setAnalysisMode(0);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.both").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                    default:
                                        cap.setAnalysisMode(1);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.entity").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                }
                                player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                TensuraSkillCapability.sync(player);
                            } else {
                                int level = this.isMastered(instance, entity) ? 80 : 79;
                                if (cap.getAnalysisLevel() != level) {
                                    cap.setAnalysisLevel(level);
                                    cap.setAnalysisDistance(this.isMastered(instance, entity) ? 80 : 79);
                                    entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                } else {
                                    cap.setAnalysisLevel(0);
                                    entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                                TensuraSkillCapability.sync(player);
                            }
                        });

                    }
                }
                break;

            case 2:
                if (entity instanceof Player) {
                    awakenSubordinates((Player) entity, entity);

                }
                break;

            case 3:
                if (!SkillHelper.outOfMagicule(entity, instance)) {
                    CharmSkill.charm(instance, entity);
                    Level level3 = entity.getLevel();

                    List<LivingEntity> nearbyEntities = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0D), target -> !target.isAlliedTo(entity) && target.isAlive() && !entity.isAlliedTo(target));

                    for (LivingEntity target : nearbyEntities) {
                        if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                        SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.MIND_CONTROL.get(), 200, 1);


                        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART, 0.25D);
                    }

                    addMasteryPoint(instance, entity);
                    instance.setCoolDown(10);
                    level3.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0F, 1.2F);

                    if (entity instanceof Player p) {
                        p.displayClientMessage(Component.literal("You're fooling them...").withStyle(ChatFormatting.LIGHT_PURPLE), true);
                    }
                }
                break;
            case 4:
                if (SkillHelper.outOfMagicule(entity, instance)) return;
                instance.setCoolDown(10);
                Level level = entity.level;

                if (SkillHelper.outOfMagicule(entity, instance)) return;

                HellFlareProjectile projectile = new HellFlareProjectile(level, entity);

                projectile.setDamage(3000.0F);
                projectile.setSpeed(2.0F);
                projectile.setAreaLife(100);
                projectile.setAreaRadius(20.0F);
                projectile.setSkill(instance);
                projectile.setMpCost(magiculeCost(entity, instance));

                projectile.setPosAndShoot(entity);
                level.addFreshEntity(projectile);

                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

                // Particles
                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLAME, 1.0D);


                SeveranceCutterProjectile spaceCutter = new SeveranceCutterProjectile(entity.getLevel(), entity);
                spaceCutter.setSpeed(3.5F);
                spaceCutter.setDamage(isMastered(instance, entity) ? 2000.0F : 1750.0F);
                spaceCutter.setSize(isMastered(instance, entity) ? 10.0F : 8.0F);
                spaceCutter.setMpCost(magiculeCost(entity, instance));
                spaceCutter.setSkill(instance);

                spaceCutter.setPosAndShoot(entity);
                spaceCutter.setPosDirection(entity, TensuraProjectile.PositionDirection.MIDDLE);

                entity.getLevel().addFreshEntity(spaceCutter);

                instance.addMasteryPoint(entity);
                instance.setCoolDown(10);

                entity.swing(InteractionHand.MAIN_HAND, true);
                entity.swing(InteractionHand.OFF_HAND, true);

                entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                break;
            case 5:
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("Bad News (Malice)"), true);
                }
                break;

            case 6: {
                if (!(entity instanceof Player owner)) return;

                if (instance.getMastery() < instance.getMaxMastery() * 0.5) {
                    owner.displayClientMessage(Component.literal("Your mastery of Tatari is insufficient.").withStyle(ChatFormatting.LIGHT_PURPLE), true);
                    return;
                }

                CompoundTag tag = instance.getOrCreateTag();

                if (owner.isShiftKeyDown()) {
                    double stored = tag.getDouble(OSIRIS_EP);
                    owner.displayClientMessage(Component.literal("Osiris EP Stored: " + (long) stored).withStyle(ChatFormatting.DARK_PURPLE), true);
                    return;
                }

                LivingEntity target = SkillHelper.getTargetingEntity(LivingEntity.class, owner, 20.0D, 0.0D, false);

                if (target == null || target == owner) return;

                if (target instanceof Player targetPlayer) {
                    TensuraEPCapability.getFrom(targetPlayer).ifPresent(targetCap -> {

                        double stolenEP = targetCap.getEP() * 0.25D;
                        if (stolenEP <= 0) return;

                        targetCap.setEP(targetPlayer, targetCap.getEP() - stolenEP);

                        TensuraPlayerCapability.getFrom(targetPlayer).ifPresent(mpCap -> {
                            double targetMP = mpCap.getMagicule();
                            double stolenMP = targetMP * 0.25D;

                            mpCap.setMagicule(targetMP - stolenMP);
                            TensuraPlayerCapability.sync(targetPlayer);
                        });

                        TensuraEPCapability.sync(targetPlayer);

                        tag.putDouble(OSIRIS_EP, tag.getDouble(OSIRIS_EP) + stolenEP);
                        instance.markDirty();

                        applyFX(owner, targetPlayer, stolenEP);
                        instance.addMasteryPoint(owner);
                        instance.setCoolDown(600);
                    });

                    break;
                }

                // ===== NON-PLAYER TARGET =====
                AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealthAttr == null) return;

                double maxHealth = maxHealthAttr.getBaseValue();
                double stolen = Math.min(maxHealth * 0.25D, 40.0D);
                double newMax = Math.max(1.0D, maxHealth - stolen);

                maxHealthAttr.setBaseValue(newMax);
                if (target.getHealth() > newMax) target.setHealth((float) newMax);

                tag.putDouble(OSIRIS_EP, tag.getDouble(OSIRIS_EP) + stolen);
                instance.markDirty();

                applyFX(owner, target, stolen);
                instance.addMasteryPoint(owner);
                instance.setCoolDown(600);

                break;
            }


            case 7: {
                if (!(entity instanceof Player owner)) return;

                LivingEntity target = SkillHelper.getTargetingEntity(LivingEntity.class, owner, 15.0D, 0.0D, false);

                if (!(target instanceof Player subordinate)) {
                    owner.displayClientMessage(Component.literal("Target must be a player.").withStyle(ChatFormatting.RED), true);
                    return;
                }

                UUID ownerUUID = TensuraEPCapability.getPermanentOwner(subordinate);
                if (ownerUUID == null || !ownerUUID.equals(owner.getUUID())) {
                    owner.displayClientMessage(Component.literal("Target is not your subordinate.").withStyle(ChatFormatting.RED), true);
                    return;
                }

                CompoundTag tag = instance.getOrCreateTag();
                double storedEP = tag.getDouble(OSIRIS_EP);

                TensuraEPCapability.getFrom(subordinate).ifPresent(targetCap -> {
                    double requiredEP = targetCap.getEP() * 0.8D;

                    if (storedEP < requiredEP) {
                        owner.displayClientMessage(Component.literal("Insufficient stored EP (" + (long) requiredEP + " required)").withStyle(ChatFormatting.RED), true);
                        return;
                    }

                    List<ManasSkillInstance> skills = getOsirisTransferableSkills(owner);
                    if (skills.isEmpty()) {
                        owner.displayClientMessage(Component.literal("No transferable skills available.").withStyle(ChatFormatting.RED), true);
                        return;
                    }

                    int index = Mth.clamp(tag.getInt(OSIRIS_INDEX), 0, skills.size() - 1);
                    tag.putInt(OSIRIS_INDEX, index);
                    ManasSkillInstance selected = skills.get(index);

                    TensuraSkillCapability.getFrom(subordinate).ifPresent(subCap -> {
                        if (SkillUtils.hasSkill(subordinate, selected.getSkill())) {
                            owner.displayClientMessage(Component.literal("Target already has this skill.").withStyle(ChatFormatting.YELLOW), true);
                            return;
                        }

                        SkillUtils.learnSkill(subordinate, selected.getSkill());

                        if (!SkillUtils.hasSkill(subordinate, selected.getSkill())) {
                            owner.displayClientMessage(Component.literal("Skill transfer failed.").withStyle(ChatFormatting.RED), true);
                        }
                    });

                    tag.putDouble(OSIRIS_EP, storedEP - requiredEP);
                    instance.markDirty();

                    applyFX(owner, subordinate, requiredEP);

                    owner.displayClientMessage(Component.literal("Granted ").append(selected.getSkill().getName()).append(" to ").append(subordinate.getName()).withStyle(ChatFormatting.DARK_PURPLE), true);

                    instance.addMasteryPoint(owner);
                    instance.setCoolDown(1200);
                });
                break;
            }
            case 8: {
                if (!(entity instanceof Player owner)) return;

                CompoundTag tag = instance.getOrCreateTag();
                int uses = tag.getInt(TATARI_USES);

                boolean permanentCopy = uses >= 13;

                List<LivingEntity> targets = owner.getLevel().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(50.0D), e -> e.isAlive() && e != owner);

                for (LivingEntity target : targets) {

                    ManasSkillInstance stolen = plunderOneSkill(target);
                    if (stolen != null && !SkillUtils.hasSkill(owner, stolen.getSkill())) {

                        if (permanentCopy) {
                            SkillUtils.learnSkill(owner, stolen.getSkill());
                        } else {
                            SkillUtils.learnSkill(owner, stolen.getSkill(), -TATARI_DURATION);
                        }
                    }

                    if (shouldSubjugate(owner, target, uses)) {
                        TensuraEPCapability.getFrom(target).ifPresent(cap -> cap.setPermanentOwner(owner.getUUID()));

                        if (target instanceof Player p) {
                            p.displayClientMessage(Component.literal("Your will bends to Tatari.").withStyle(ChatFormatting.DARK_RED), true);
                        }
                    }

                    TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SOUL_FIRE_FLAME, 0.8D);
                }

                tag.putInt(TATARI_USES, uses + 1);

                instance.setToggled(true);
                instance.setCoolDown(TATARI_DURATION);
                instance.markDirty();

                owner.displayClientMessage(Component.literal("Tatari engulfs the world for 10 minutes.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), true);

                // ===== POST-13 USE EFFECT =====
                if (uses + 1 == 13) {
                    instance.setMastery(0);

                    owner.displayClientMessage(Component.literal("Tatari has devoured itself. Mastery reset.").withStyle(ChatFormatting.DARK_PURPLE), true);
                }
                instance.setToggled(true);
                instance.setCoolDown(TATARI_DURATION);
                instance.markDirty();
                if (owner.getServer() != null) {
                    Component msg = Component.literal("⚠ Tatari has descended. The world bends to a single will.").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);

                    owner.getServer().getPlayerList().broadcastSystemMessage(msg, false);
                }

                break;
            }

        }
    }

    private void applyFX(Player owner, LivingEntity target, double amount) {
        TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SOUL, 1.2D);

        owner.level.playSound(null, owner.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.8F);

        owner.displayClientMessage(Component.literal("Replicant absorbed " + (int) amount + " energy.").withStyle(ChatFormatting.DARK_PURPLE), true);
    }

    public void awakenSubordinates(Player player, LivingEntity entity) {
        // Access EP capability
        TensuraEPCapability.getFrom(player).ifPresent(cap -> {
            double maxEP = cap.getEP();
            double cost = maxEP * 0.25;

            if (cap.getEP() < cost) {
                player.displayClientMessage(Component.literal("Not enough EP to awaken subordinates!").withStyle(ChatFormatting.RED), true);
                return;
            }

            cap.setEP(player, cap.getEP() - cost);
            TensuraEPCapability.sync(player);

            List<LivingEntity> subs = player.getLevel().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(40.0D), target -> {

                return target instanceof Mob mob && mob.isAlliedTo(player);
            });

            for (LivingEntity sub : subs) {
                sub.getActiveEffects().removeIf(effect -> effect.getEffect().getCategory().equals(MobEffectCategory.HARMFUL));

                sub.heal(sub.getMaxHealth());
                if (sub instanceof Player subplayer) {
                    if (cap.isChaos() || cap.isMajin()) {
                        cap.setChaos(true);

                        TensuraPlayerCapability.getFrom(subplayer).ifPresent(cap2 -> {
                            if (TensuraPlayerCapability.isTrueHero(entity)) {
                                cap2.setTrueHero(false);
                            }
                        });

                    } else {
                        TensuraPlayerCapability.getFrom(subplayer).ifPresent(cap2 -> {
                            if (TensuraEPCapability.getPermanentOwner(target) != entity.getUUID()) return;

                            harvestFestivalSubordinate(target, (Player) entity);
                        });
                    }

                } else {
                    sub.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 60, 1));
                    sub.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60, 1));
                    sub.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 60, 1));
                    cap.setChaos(true);
                    sub.setCustomName(Component.literal("Replicant"));
                    sub.setCustomNameVisible(true);
                }
                TensuraParticleHelper.addServerParticlesAroundSelf(sub, ParticleTypes.ENCHANT, 1.0D);
            }

            player.displayClientMessage(Component.literal("Your subordinates have awakened!").withStyle(ChatFormatting.GOLD), true);
        });
    }


    private Player getPlayerByName(String itemName, Player player) {
        ServerLevel world = (ServerLevel) player.getLevel();

        for (ServerPlayer onlinePlayer : world.getServer().getPlayerList().getPlayers()) {
            if (onlinePlayer.getGameProfile().getName().equalsIgnoreCase(itemName)) {
                return onlinePlayer;
            }
        }

        return null;
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }
        tag.putInt("activatedTimes", time + 1);
    }
//        public static void removeCookedHP(LivingEntity entity, @Nullable ManasSkillInstance instance) {

    /// /            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
    /// /
    /// /
    /// /            if (healthAttribute != null && healthAttribute.getModifier(COOK) != null) {
    /// /                healthAttribute.removeModifier(COOK);
    /// /
    /// /                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.CAMPFIRE_COSY_SMOKE);
    /// /            }
    /// /        }
    public boolean canCopy(ManasSkillInstance instance) {
        if (!instance.isTemporarySkill() && instance.getMastery() >= 0) {
            ManasSkill var3 = instance.getSkill();
            if (!(var3 instanceof Skill skill)) {
                return false;
            } else {
                return skill.getType().equals(SkillType.COMMON) || skill.getType().equals(SkillType.EXTRA) || (skill.getClass().equals(CookSkill.class) || skill.getClass().equals(WrathSkill.class)) || skill.getType().equals(SkillType.INTRINSIC);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {

            if (instance.getMode() == 7 && entity instanceof Player player && player.isShiftKeyDown()) {
                CompoundTag tag = instance.getOrCreateTag();

                if (heldTicks % 5 == 0) {
                    List<ManasSkillInstance> skills = getOsirisTransferableSkills(player);
                    if (!skills.isEmpty()) {
                        int index = (tag.getInt(OSIRIS_INDEX) + 1) % skills.size();
                        tag.putInt(OSIRIS_INDEX, index);

                        player.displayClientMessage(Component.literal("Osiris Selection: ").append(skills.get(index).getSkill().getName()).withStyle(ChatFormatting.GOLD), true);
                    }
                }
            }


            if (instance.getMode() != 5 || instance.onCoolDown()) {
                return false;
            }

            if (heldTicks % 60 == 0 && heldTicks > 0) {
                addMasteryPoint(instance, entity);
            }

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new RequestFxSpawningPacket(new ResourceLocation("tensura:haki"), entity.getId(), 0.0D, 1.0D, 0.0D, true));

            List<LivingEntity> nearbyEntities = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0D), target -> !target.isAlliedTo(entity) && target.isAlive() && !entity.isAlliedTo(target));

            for (LivingEntity target : nearbyEntities) {
                if (target instanceof Player player && player.getAbilities().instabuild) continue;

                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.LUST_EMBRACEMENT.get(), 200, 1);
                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.INSANITY.get(), 400, 8);
                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.MAGIC_INTERFERENCE.get(), 400, 2);
                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.DISINTEGRATING.get(), 400, 1);
            }

            return true;
        }

        return false;
    }

    private void harvestFestivalSubordinate(LivingEntity target, Player owner) {
        if (target instanceof Player) {
            if (TensuraPlayerCapability.isTrueDemonLord((Player) target)) {
                owner.displayClientMessage(Component.translatable("tensura.evolve.demon_lord.already").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            if (TensuraPlayerCapability.isTrueHero(target)) {
                owner.displayClientMessage(Component.translatable("tensura.evolve.demon_lord.hero").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            TensuraPlayerCapability.getFrom(owner).ifPresent((ownerCap) -> {
                int ownerSoulPoints = ownerCap.getSoulPoints();
                int harvestFestivalCost = 100000;
                if (ownerSoulPoints < harvestFestivalCost) {
                    owner.displayClientMessage(Component.translatable("trmythos.skill.mode.apophis.not_enough_souls", harvestFestivalCost / 1000).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                } else {
                    TensuraPlayerCapability.getFrom((Player) target).ifPresent((cap) -> {
                        ownerCap.setSoulPoints(ownerCap.getSoulPoints() - harvestFestivalCost);
                        RaceHelper.awakening((Player) target, false);
                    });
                }
            });
        }
    }

    private List<ManasSkillInstance> getOsirisTransferableSkills(Player owner) {
        SkillStorage storage = SkillAPI.getSkillsFrom(owner);
        List<ManasSkillInstance> result = new ArrayList<>();

        for (ManasSkillInstance inst : storage.getLearnedSkills()) {
            if (!canCopy(inst)) continue;

            ManasSkill skill = inst.getSkill();
            if (skill instanceof Skill s && s.getType() == SkillType.ULTIMATE) {
                if (inst.getMastery() < inst.getMaxMastery() * 0.8) continue;
            }

            result.add(inst);
        }

        return result;
    }


    @Mod.EventBusSubscriber
    public class TatariDeathHandler {

        @SubscribeEvent
        public static void onPlayerDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;


            if (!SkillUtils.hasSkill(player, Skills.TATARI.get())) return;

            ServerLevel level = player.getLevel();

            Component announce = Component.literal("The sky's crimson glow fades as " + player.getName().getString() + " falls, and TATARI is rumor once more...").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);

            for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {
                p.sendSystemMessage(announce);
            }

            SkillStorage storage = SkillAPI.getSkillsFrom(player);
            if (storage.getSkill(Skills.ZEPIA.get()).isPresent()) {
                storage.forgetSkill(Skills.ZEPIA.get());
            }
            if (storage.getSkill(Skills.TATARI.get()).isPresent()) {
                storage.forgetSkill(Skills.TATARI.get());
            }

            List<LivingEntity> affected = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(25.0D), e -> e.isAlive() && e != player);

            for (LivingEntity target : affected) {

                SkillHelper.checkThenAddEffectSource(target, player, MobEffects.BLINDNESS, 200, // 10s
                        1);

                SkillHelper.checkThenAddEffectSource(target, player, TensuraMobEffects.MAGIC_INTERFERENCE.get(), 300, // 15s
                        2);

                SkillHelper.checkThenAddEffectSource(target, player, MobEffects.CONFUSION, 160, // 8s
                        1);

                TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ASH, 1.0D);
            }

            level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 2.0F, 0.7F);

            List<LivingEntity> subs = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(50.0D), e -> {
                UUID owner = TensuraEPCapability.getPermanentOwner(e);
                return owner != null && owner.equals(player.getUUID());
            });

            for (LivingEntity sub : subs) {

                sub.getActiveEffects().removeIf(effect -> effect.getEffect().getCategory() != MobEffectCategory.HARMFUL);

                TensuraEPCapability.getFrom(sub).ifPresent(ep -> {
                    ep.setChaos(false);
                    TensuraEPCapability.sync(sub);
                });

                TensuraParticleHelper.addServerParticlesAroundSelf(sub, ParticleTypes.SMOKE, 1.5D);
            }

        }
    }


    public void onBeingDamaged(@NotNull ManasSkillInstance instance, LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();

        if (!this.isInSlot(entity)) return;

        if (!instance.isToggled()) return;

        if (instance.onCoolDown()) return;

        event.setCanceled(true);

        entity.level.playSound(null, entity.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.6F, 0.5F);
    }

    private @Nullable ManasSkillInstance plunderOneSkill(LivingEntity target) {
        SkillStorage storage = SkillAPI.getSkillsFrom(target);
        List<ManasSkillInstance> candidates = new ArrayList<>();

        for (ManasSkillInstance inst : storage.getLearnedSkills()) {
            if (canCopy(inst)) candidates.add(inst);
        }

        if (candidates.isEmpty()) return null;

        return candidates.get(target.getRandom().nextInt(candidates.size()));
    }

    private boolean shouldSubjugate(Player owner, LivingEntity target, int uses) {
        double ownerEP = TensuraEPCapability.getCurrentEP(owner);
        double targetEP = TensuraEPCapability.getCurrentEP(target);

        double threshold = (uses >= 13) ? 0.75D : 0.50D;
        return targetEP < ownerEP * threshold;
    }

    private boolean meetsTatariConditions(Player player) {

        // I — Malignant Information
        if (TensuraPlayerCapability.isTrueHero(player)) return false;

        ServerPlayer serverPlayer = (ServerPlayer) player;

        int hinataKills = serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(TensuraEntityTypes.HINATA_SAKAGUCHI.get()));

        int shizuKills = serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(TensuraEntityTypes.SHIZU.get()));

        int charybdisKills = serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(TensuraEntityTypes.CHARYBDIS.get()));

        if (hinataKills < 1 || shizuKills < 1 || charybdisKills < 1) return false;

        // II — Widespread Rumor
        int villagerKills = serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.VILLAGER));

        int playerKills = serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.PLAYER));

        int illagerKills = serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.PILLAGER)) + serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.VINDICATOR)) + serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.EVOKER)) + serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.ILLUSIONER));

        int totalHumanKills = villagerKills + playerKills + illagerKills;

        if (totalHumanKills < 1000) return false;


        boolean hasCook = SkillUtils.hasSkill(player, UniqueSkills.COOK.get());
        boolean hasWrath = SkillUtils.hasSkill(player, UniqueSkills.WRATH.get());

        if (!hasCook && !hasWrath) return false;

        // III — Witnessed
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        List<ManasSkill> banned = List.of(UniqueSkills.SHADOW_STRIKER.get(), UniqueSkills.UNYIELDING.get(), UniqueSkills.INFINITY_PRISON.get(), UniqueSkills.SUPPRESSOR.get(), UniqueSkills.OPPRESSOR.get(), UniqueSkills.FALSIFIER.get());

        for (ManasSkill bannedSkill : banned) {
            if (storage.getSkill(bannedSkill).isPresent()) return false;
        }

        // IV — Terminal Point
        MobEffectInstance insanity = player.getEffect(TensuraMobEffects.INSANITY.get());
        return insanity != null && insanity.getAmplifier() >= 24;
    }

}