package com.github.mythos.mythos.ability.mythos.skill.ultimate;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.ability.skill.unique.CookSkill;
import com.github.manasmods.tensura.ability.skill.unique.WrathSkill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import net.minecraft.world.item.ItemStack;
import com.github.manasmods.tensura.entity.magic.projectile.SeveranceCutterProjectile;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.race.TensuraRaces;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;


public class ZepiaSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("8a67e638-7159-4ec8-8556-2c25c457262b");
    public static final UUID COOK = UUID.fromString("7d9edf73-c44a-46ca-93b9-f18ca595ca63");
    public static boolean DeadApostleAncestor = true;

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/ZepiaIconBig.png");
    }

    public ZepiaSkill() {
        super(SkillType.ULTIMATE);
    }

    @Override
    public boolean canBeToggled(@NotNull ManasSkillInstance instance, @NotNull LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 8000000.0;
    }

    public boolean meetEPRequirement(@NotNull Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.ELTNAM.get());

    }

    public double learningCost() {
        return 15000.0;
    }

    public boolean canTick(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        TensuraEPCapability.getFrom(entity).ifPresent(cap -> cap.setChaos(true));
        TensuraEPCapability.sync(entity);
        if (instance.isToggled()) {
            this.gainMastery(instance, entity);
        }
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    public void onToggleOn(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
        entity.addEffect(new MobEffectInstance(MythosMobEffects.DEAD_REGENERATION.get(), 1200, 1, false, false, false));
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            assert server != null;
            PlayerList playerList = server.getPlayerList();

            ClientboundPlayerInfoPacket removePacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, player);

            MutableComponent leaveMessage = Component.translatable("multiplayer.player.left", player.getDisplayName()).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));

            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(removePacket);
                target.sendSystemMessage(leaveMessage);
            }
        }
    }

    public void onToggleOff(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
        MobEffectInstance effectInstance = entity.getEffect(MythosMobEffects.DEAD_REGENERATION.get());
        if (effectInstance != null && effectInstance.getAmplifier() < 1) {
            entity.removeEffect(MythosMobEffects.DEAD_REGENERATION.get());
        }
        if (entity instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            assert server != null;
            PlayerList playerList = server.getPlayerList();

            ClientboundPlayerInfoPacket addPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, player);

            MutableComponent joinMessage = Component.translatable("multiplayer.player.joined", player.getDisplayName()).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
            for (ServerPlayer target : playerList.getPlayers()) {
                target.connection.send(addPacket);
                target.sendSystemMessage(joinMessage);
            }
        }
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent ignoredEvent, Player player) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, ExtraSkills.SAGE.get());
            SkillUtils.learnSkill(entity, ResistanceSkills.DARKNESS_ATTACK_NULLIFICATION.get());
            SkillUtils.learnSkill(entity, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get());
            SkillUtils.learnSkill(entity, ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
            SkillUtils.learnSkill(entity, ResistanceSkills.PAIN_RESISTANCE.get());
            SkillUtils.learnSkill(entity, ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get());
        }
        if (!DeadApostleAncestor) return;
        TensuraPlayerCapability.getFrom(player).ifPresent(cap -> {
            Race vampirePrince = TensuraRaces.RACE_REGISTRY.get().getValue(MythosRaces.VAMPIRE_PRINCE);
            if (cap.getRace() != vampirePrince) {
                assert vampirePrince != null;
                cap.setRace(player, vampirePrince, true);
            }
        });

    }

    public int modes() {
        return 6;
    }

    public int nextMode(@NotNull LivingEntity entity, @NotNull TensuraSkillInstance instance, boolean reverse) {
        if (reverse) return (instance.getMode() == 1) ? 6 : (instance.getMode() - 1);
        else return (instance.getMode() == 6) ? 1 : (instance.getMode() + 1);
    }

    public @NotNull Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.translatable("trmythos.skill.mode.zepia.True_scry_proficiency: Analysis");
            case 2 -> Component.translatable("trmythos.skill.mode.zepia.True_scry_proficiency: Divination");
            case 3 -> Component.translatable("trmythos.skill.mode.zepia.Synthetic_blood_formula");
            case 4 -> Component.translatable("trmythos.skill.mode.zepia.Blood_liar");
            case 5 -> Component.translatable("trmythos.skill.mode.zepia.Cleave");
            case 6 -> Component.translatable("trmythos.skill.mode.zepia.Bad_news_(Malice)");
            default -> Component.empty();
        };
    }

    public void onPressed(ManasSkillInstance instance, @NotNull LivingEntity entity) {
        switch (instance.getMode()) {
            case 1:
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
                                int level = this.isMastered(instance, entity) ? 30 : 29;
                                if (cap.getAnalysisLevel() != level) {
                                    cap.setAnalysisLevel(level);
                                    cap.setAnalysisDistance(this.isMastered(instance, entity) ? 30 : 20);
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
                if (entity instanceof Player targetPlayer) {
                    targetPlayer.displayClientMessage(Component.literal("§5You see them..."), true);
                    processScrying(instance, (Player) entity, entity);
                }
                break;

            case 3:
                boolean success;
                LivingEntity living = SkillHelper.getTargetingEntity(entity, 3.0D, false);
                success = (TensuraEffectsCapability.getSeverance(entity) > 0.0D);
                TensuraEffectsCapability.getFrom(entity).ifPresent(cap -> cap.setSeveranceAmount(0.0D));

                Predicate predicate = effect -> (effect == MobEffectCategory.HARMFUL);
                success = (success || SkillHelper.removePredicateEffect(entity, predicate, magiculeCost(entity, instance)));


                int cost = instance.isMastered(entity) ? (int) (TensuraEPCapability.getEP(entity) * 0.075D) : (int) (TensuraEPCapability.getEP(entity) * 0.085D);
                float healAmount = entity.getMaxHealth() - entity.getHealth();
                SkillHelper.outOfMagiculeStillConsume(entity, cost);

                entity.heal(healAmount);
                success |= healAmount > 0.0F;

                if (success) {
                    TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.HEART, 2.0D);
                    addMasteryPoint(instance, entity);
                    entity.swing(InteractionHand.MAIN_HAND, true);
                } else {
                    assert living != null;
                    success = (TensuraEffectsCapability.getSeverance(living) > 0.0D);
                    TensuraEffectsCapability.getFrom(living).ifPresent(cap -> cap.setSeveranceAmount(0.0D));

                    Predicate<MobEffect> predicate2 = effect -> effect.getCategory() == MobEffectCategory.HARMFUL;
                    success = success || SkillHelper.removePredicateEffect(entity, predicate2, magiculeCost(entity, instance));

                    int cost2 = instance.isMastered(entity) ? (int) (TensuraEPCapability.getEP(entity) * 0.075D) : (int) (TensuraEPCapability.getEP(entity) * 0.085D);

                    float missingHealth = entity.getMaxHealth() - entity.getHealth();
                    double remainingMagicule = SkillHelper.outOfMagiculeStillConsume(entity, cost2);

                    if (remainingMagicule > 0.0D) {
                        missingHealth = entity.getMaxHealth() - entity.getHealth();
                    }

                    living.heal(missingHealth);

                    removeCookedHP(living, instance);

                    success = success || missingHealth > 0.0F;

                    if (success) {
                        TensuraParticleHelper.addServerParticlesAroundSelf(living, ParticleTypes.CAMPFIRE_COSY_SMOKE, 2.0D);
                    }
                }

                instance.setCoolDown(5);
                break;

            default:

                break;
            case 4:
                LivingEntity target = SkillHelper.getTargetingEntity(entity, 100.0, false);
                if (target != null && target.isAlive()) {
                    label58:
                    {
                        if (target instanceof Player player) {
                            if (player.getAbilities().invulnerable) {
                                break label58;
                            }
                        }
                        entity.swing(InteractionHand.MAIN_HAND, true);
                        ServerLevel level = (ServerLevel) entity.getLevel();
                        int chance = 50;
                        boolean failed = true;
                        if (entity.getRandom().nextInt(100) <= chance) {
                            List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canCopy).toList();
                            if (!collection.isEmpty()) {
                                this.addMasteryPoint(instance, entity);
                                ManasSkill skill = collection.get(target.getRandom().nextInt(collection.size())).getSkill();
                                SkillPlunderEvent event = new SkillPlunderEvent(target, entity, false, skill);
                                if (!MinecraftForge.EVENT_BUS.post(event) && SkillUtils.learnSkill(entity, event.getSkill(), instance.getRemoveTime())) {
                                    instance.setCoolDown(10);
                                    failed = false;
                                    if (entity instanceof Player player) {
                                        player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{event.getSkill().getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                                    }
                                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                            }
                        }

                        if (failed && entity instanceof Player player) {
                            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                            instance.setCoolDown(5);
                        }
                    }
                }
                break;
            case 5:
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
            case 6:
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("Bad News (Malice)"), true);
                }
                break;

        }
    }

    private void processScrying(ManasSkillInstance instance, Player player, LivingEntity entity) {
        if (entity instanceof Player) {
            ItemStack held = player.getMainHandItem();
            if (held.hasCustomHoverName()) {
                String name = held.getHoverName().getString().trim();
                ServerLevel level = (ServerLevel) player.getLevel();
                LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5000), e -> e.getName().getString().equalsIgnoreCase(name)).stream().findFirst().orElse(null);

                if (target != null) {
                    if (!SkillHelper.outOfMagicule(player, instance)) {
                        
                        player.displayClientMessage(Component.literal("Target '" + name + "' found at: " + "X=" + target.getX() + ", " + "Y=" + target.getY() + ", " + "Z=" + target.getZ()).withStyle(ChatFormatting.AQUA), true);

                        TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.PORTAL, 1.0D);
                        addMasteryPoint(instance, player);
                        instance.setCoolDown(100);
                        return;
                    }
                }
            }
        }

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

    public static void removeCookedHP(LivingEntity entity, @Nullable ManasSkillInstance ignoredInstance) {
        AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);


        if (healthAttribute != null && healthAttribute.getModifier(COOK) != null) {
            healthAttribute.removeModifier(COOK);

            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.CAMPFIRE_COSY_SMOKE);
        }
    }

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
    public boolean onHeld(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, int heldTicks) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {

            if (instance.getMode() != 6 || instance.onCoolDown()) {
                return false;
            }

            if (heldTicks % 60 == 0 && heldTicks > 0) {
                addMasteryPoint(instance, entity);
            }

            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new RequestFxSpawningPacket(new ResourceLocation("tensura:haki"), entity.getId(), 0.0D, 1.0D, 0.0D, true));

            List<LivingEntity> nearbyEntities = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0D), target -> !target.isAlliedTo(entity) && target.isAlive() && !entity.isAlliedTo(target));

            for (LivingEntity target : nearbyEntities) {
                if (target instanceof Player player && player.getAbilities().instabuild) continue;

                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.INFECTION.get(), 200, 1);

                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.INSANITY.get(), 400, 8);
            }


            return true;
        }

        return false;
    }

}




