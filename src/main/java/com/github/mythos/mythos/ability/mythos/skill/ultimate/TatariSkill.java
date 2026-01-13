package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
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
import com.github.manasmods.tensura.entity.magic.barrier.FlareCircleEntity;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.race.RaceHelper;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mythos.mythos.ability.mythos.skill.ultimate.ZepiaSkill.ACCELERATION;
import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class TatariSkill extends Skill {

    public static boolean DeadApostleAncestor = true;

    public TatariSkill() {
        super(SkillType.ULTIMATE);
    }

    @Nullable
    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
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
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.ZEPIA.get());

    }


    public double learningCost() {
        return 15000.0;
    }

    private Player target;

    private double baseErrorRate = 1000.0D;

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        TensuraEPCapability.getFrom(entity).ifPresent(cap -> cap.setChaos(true));
        TensuraEPCapability.sync(entity);
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.isToggled()) {
            this.gainMastery(instance, entity);
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
        return 6;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 6 : (instance.getMode() - 1);
        else return (instance.getMode() == 6) ? 1 : (instance.getMode() + 1);
    }

    public Component getModeName(int mode) {
        MutableComponent name;
        switch (mode) {
            case 1:
                name = Component.translatable("trmythos.skill.mode.zepia.True_scry_proficiency: Analysis");
                break;
            case 2:
                name = Component.translatable("trmythos.skill.mode.zepia.True_scry_proficiency: Divination");
                break;
            case 3:
                name = Component.translatable("trmythos.skill.mode.zepia.Synthetic_blood_formula");
                break;
            case 4:
                name = Component.translatable("trmythos.skill.mode.zepia.Blood_liar");
                break;
            case 5:
                name = Component.translatable("trmythos.skill.mode.zepia.Cleave");
                break;
            case 6:
                name = Component.translatable("trmythos.skill.mode.zepia.Bad_news_(Malice)");
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
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
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
                                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                } else {
                                    cap.setAnalysisLevel(0);
                                    entity.getLevel().playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
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
                instance.setCoolDown(2);
                FlareCircleEntity barrier = new FlareCircleEntity(entity.getLevel(), entity);
                barrier.setDamage(2000.0F);
                barrier.setRadius(3.5F);
                barrier.setHeight(10.0F);
                barrier.setSkill(instance);
                barrier.setMpCost(magiculeCost(entity, instance));

                barrier.setPos(entity.getX(), entity.getY(), entity.getZ());

                if (!entity.level.isClientSide) entity.level.addFreshEntity(barrier);

                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
                SeveranceCutterProjectile spaceCutter = new SeveranceCutterProjectile(entity.getLevel(), entity);
                spaceCutter.setSpeed(2.5F);
                spaceCutter.setDamage(isMastered(instance, entity) ? 1000.0F : 750.0F);
                spaceCutter.setSize(isMastered(instance, entity) ? 8.0F : 5.0F);
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

        }
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

                if (target instanceof Mob mob && mob.isAlliedTo(player)) {
                    return true;
                }
                return false;
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
////            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
////
////
////            if (healthAttribute != null && healthAttribute.getModifier(COOK) != null) {
////                healthAttribute.removeModifier(COOK);
////
////                TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.CAMPFIRE_COSY_SMOKE);
////            }
////        }
    public boolean canCopy(ManasSkillInstance instance) {
        if (!instance.isTemporarySkill() && instance.getMastery() >= 0) {
            ManasSkill var3 = instance.getSkill();
            if (!(var3 instanceof Skill)) {
                return false;
            } else {
                Skill skill = (Skill) var3;
                return skill.getType().equals(SkillType.COMMON) || skill.getType().equals(SkillType.EXTRA) || (skill.getClass().equals(CookSkill.class) || skill.getClass().equals(WrathSkill.class)) || skill.getType().equals(SkillType.INTRINSIC);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {

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
                    owner.displayClientMessage(Component.translatable("trmythos.skill.mode.apophis.not_enough_souls", new Object[]{harvestFestivalCost / 1000}).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                } else {
                    TensuraPlayerCapability.getFrom((Player) target).ifPresent((cap) -> {
                        ownerCap.setSoulPoints(ownerCap.getSoulPoints() - harvestFestivalCost);
                        RaceHelper.awakening((Player) target, false);
                    });
                }
            });
        }
    }
}






