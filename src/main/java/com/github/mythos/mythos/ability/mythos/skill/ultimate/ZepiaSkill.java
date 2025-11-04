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
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.manasmods.tensura.ability.skill.unique.WrathSkill;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;



public class ZepiaSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("8a67e638-7159-4ec8-8556-2c25c457262b");
    public static final UUID COOK = UUID.fromString("7d9edf73-c44a-46ca-93b9-f18ca595ca63");
    public static boolean DeadApostleAncestor = true;

    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/ultimate/ZepiaIcon.png");
    }


    public ZepiaSkill() {
        super(SkillType.ULTIMATE);
    }

    @Nullable
    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 8000000.0;
    }

    public boolean meetEPRequirement(Player player, double newEP) {
        // Check EP using Tensura capability
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false; // not enough EP
        }
        return SkillUtils.isSkillMastered(player, (ManasSkill) Skills.ELTNAM.get());

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

            // Create a packet to remove the player from the player info (tab list)
            ClientboundPlayerInfoPacket removePacket =
                    new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, player);

            // Create the "player left the game" message in yellow
            MutableComponent leaveMessage = Component.translatable(
                    "multiplayer.player.left", player.getDisplayName()
            ).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));

            for (ServerPlayer target : playerList.getPlayers()) {
                MobEffectInstance effect = target.getEffect(TensuraMobEffects.PRESENCE_SENSE.get());
                  target.connection.send(removePacket);
                    target.sendSystemMessage(leaveMessage);
                }
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

            // Create a packet to add the player back to the player info (tab list)
            ClientboundPlayerInfoPacket addPacket =
                    new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, player);

            // Create the "player joined the game" message in yellow
            MutableComponent joinMessage = Component.translatable(
                    "multiplayer.player.joined", player.getDisplayName()
            ).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
            for (ServerPlayer target : playerList.getPlayers()) {
                MobEffectInstance effect = target.getEffect(TensuraMobEffects.PRESENCE_SENSE.get());
                 target.connection.send(addPacket);
                    target.sendSystemMessage(joinMessage);
                }
            }
        }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event, Player player) {
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
                cap.setRace(player, vampirePrince, true);
            }
        });

    }

    public int modes() {
        return 6;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 6 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 6) ? 1 : (instance.getMode() + 1);
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
                if (!SkillHelper.outOfMagicule(entity, instance)) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        TensuraSkillCapability.getFrom(player).ifPresent(cap -> {
                            if (player.isCrouching()) {
                                int mode = cap.getAnalysisMode();
                                switch (mode) {
                                    case 1:
                                        cap.setAnalysisMode(2);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.block")
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                    case 2:
                                        cap.setAnalysisMode(0);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.both")
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
                                        break;
                                    default:
                                        cap.setAnalysisMode(1);
                                        player.displayClientMessage(Component.translatable("tensura.skill.analytical.analyzing_mode.entity")
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true);
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
                // call scrying logic when in mode 2 and player pressed
                if (entity instanceof Player) {
                    processScrying(instance, (Player) entity);
                }
                break;

            case 3:
                boolean success;
                int duration;
                LivingEntity targetA, living = SkillHelper.getTargetingEntity(entity, 3.0D, false);
                    // Remove effects like Severance, poison, or other harmful debuffs
                    success = (TensuraEffectsCapability.getSeverance(entity) > 0.0D);
                    TensuraEffectsCapability.getFrom(entity).ifPresent(cap -> cap.setSeveranceAmount(0.0D));

                    Predicate predicate = effect -> (effect == MobEffectCategory.HARMFUL); success = (success || SkillHelper.removePredicateEffect(entity, predicate, magiculeCost(entity, instance)));

                    // Heal missing HP and consume some magicule
                    int cost = instance.isMastered(entity)
                            ? (int) (TensuraEPCapability.getEP(entity) * 0.075D)
                            : (int) (TensuraEPCapability.getEP(entity) * 0.085D);
                    float healAmount = entity.getMaxHealth() - entity.getHealth();
                    SkillHelper.outOfMagiculeStillConsume(entity, cost);

                    entity.heal(healAmount);
                    success |= healAmount > 0.0F;

                    if (success) {
                        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.HEART, 2.0D);
                        addMasteryPoint(instance, entity);
                        entity.swing(InteractionHand.MAIN_HAND, true);
                    }
                 else {
                        // Remove Severance and harmful effects from the target
                        success = (TensuraEffectsCapability.getSeverance(living) > 0.0D);
                        TensuraEffectsCapability.getFrom(living).ifPresent(cap -> cap.setSeveranceAmount(0.0D));

                        Predicate<MobEffect> predicate2 = effect -> effect.getCategory() == MobEffectCategory.HARMFUL;
                        success = success || SkillHelper.removePredicateEffect(entity, predicate2, magiculeCost(entity, instance));

                        // Consume magicules (10% if mastered, 15% otherwise)
                        int cost2 = instance.isMastered(entity)
                                ? (int) (TensuraEPCapability.getEP(entity) * 0.075D)
                                : (int) (TensuraEPCapability.getEP(entity) * 0.085D);

                        // Calculate healing amount
                        float missingHealth = entity.getMaxHealth() - entity.getHealth();
                        double remainingMagicule = SkillHelper.outOfMagiculeStillConsume(entity, cost2);

                        if (remainingMagicule > 0.0D) {
                            missingHealth = entity.getMaxHealth() - entity.getHealth();
                        }

                        // Heal target
                        living.heal(missingHealth);

                        // Remove "Cooked HP" modifier if present
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
                    label58: {
                        if (target instanceof Player) {
                            Player player = (Player)target;
                            if (player.getAbilities().invulnerable) {
                                break label58;
                            }
                        }
                        entity.swing(InteractionHand.MAIN_HAND, true);
                        ServerLevel level = (ServerLevel)entity.getLevel();
                        int chance = 50;
                        boolean failed = true;
                        if (entity.getRandom().nextInt(100) <= chance) {
                            List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canCopy).toList();
                            if (!collection.isEmpty()) {
                                this.addMasteryPoint(instance, entity);
                                ManasSkill skill = ((ManasSkillInstance)collection.get(target.getRandom().nextInt(collection.size()))).getSkill();
                                SkillPlunderEvent event = new SkillPlunderEvent(target, entity, false, skill);
                                if (!MinecraftForge.EVENT_BUS.post(event) && SkillUtils.learnSkill(entity, event.getSkill(), instance.getRemoveTime())) {
                                    instance.setCoolDown(10);
                                    failed = false;
                                    if (entity instanceof Player) {
                                        Player player = (Player)entity;
                                        player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{event.getSkill().getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
                                    }
                                    level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                            }
                        }

                        if (failed && entity instanceof Player) {
                            Player player = (Player)entity;
                            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
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

// Set position and direction
                spaceCutter.setPosAndShoot(entity);
                spaceCutter.setPosDirection(entity, TensuraProjectile.PositionDirection.MIDDLE);

// Spawn the projectile into the world
                entity.getLevel().addFreshEntity(spaceCutter);

// Gain mastery experience and apply cooldown
                instance.addMasteryPoint(entity);
                instance.setCoolDown(10);

// Swing both hands
                entity.swing(InteractionHand.MAIN_HAND, true);
                entity.swing(InteractionHand.OFF_HAND, true);

// Play attack sound
                entity.getLevel().playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );
                break;
            case 6:
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("Bad News (Malice)"), true);
                }
                break;

        }
    }

    private void processScrying(ManasSkillInstance instance, Player player) {
        ItemStack itemInHand = player.getMainHandItem();

        if (!itemInHand.isEmpty()) {
            String itemName = itemInHand.getHoverName()
                    .getString()
                    .replaceAll("[\\[\\]]", "")
                    .trim()
                    .toLowerCase();

            Player matchedPlayer = getPlayerByName(itemName, player);

            if (matchedPlayer != null) {
                this.target = matchedPlayer;
                double currentErrorRate = this.baseErrorRate;
                boolean scryable = true;

                // EP check
                if (TensuraEPCapability.getCurrentEP(this.target) <= 200000.0D) {
                    scryable = false;
                }

                if (scryable) {
                    if (instance.isMastered(player)) {
                        currentErrorRate /= 2.0D;
                    }

                    ServerLevel serverLevel = (ServerLevel) this.target.getLevel();

                    // Random offset (error)
                    double offsetX = serverLevel.random.nextDouble() * currentErrorRate - currentErrorRate / 2.0D;
                    double offsetY = serverLevel.random.nextDouble() * currentErrorRate - currentErrorRate / 2.0D;
                    double offsetZ = serverLevel.random.nextDouble() * currentErrorRate - currentErrorRate / 2.0D;

                    // Compute “revealed” coordinates
                    double revealedX = this.target.getX() + offsetX;
                    double revealedY = this.target.getY() + offsetY;
                    double revealedZ = this.target.getZ() + offsetZ;

                    if (instance.isMastered(player)) {
                        player.sendSystemMessage(Component.literal(String.format(
                                "Target found at: X=%.2f, Y=%.2f, Z=%.2f in %s",
                                revealedX,
                                revealedY,
                                revealedZ,
                                this.target.getLevel().dimension().location().toString()
                        )));
                    } else {
                        player.sendSystemMessage(Component.literal(String.format(
                                "Target is approximately at: X=%.2f, Y=%.2f, Z=%.2f",
                                revealedX,
                                revealedY,
                                revealedZ
                        )));
                    }

                    addMasteryPoint(instance, player);
                    instance.setCoolDown(120);

                } else {
                    player.sendSystemMessage(Component.literal("The target is immune to scrying!"));
                }
            } else {
                player.sendSystemMessage(Component.literal("No player found with that name!"));
            }
        } else {
            player.sendSystemMessage(Component.literal(
                    "You must hold an item with the player's name in your main hand!"
            ));
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
    public static void removeCookedHP(LivingEntity entity, @Nullable ManasSkillInstance instance) {
        // Get the entity's max health attribute
        AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);

        // If the attribute exists and has the COOK modifier, remove it
        if (healthAttribute != null && healthAttribute.getModifier(COOK) != null) {
            healthAttribute.removeModifier(COOK);

            // Spawn smoke particles around the entity
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.CAMPFIRE_COSY_SMOKE);
        }
    }
    public boolean canCopy(ManasSkillInstance instance) {
        if (!instance.isTemporarySkill() && instance.getMastery() >= 0) {
            ManasSkill var3 = instance.getSkill();
            if (!(var3 instanceof Skill)) {
                return false;
            } else {
                Skill skill = (Skill)var3;
                return skill.getType().equals(SkillType.COMMON) ||
                        skill.getType().equals(SkillType.EXTRA) ||
                        (skill.getClass().equals(CookSkill.class) || skill.getClass().equals(WrathSkill.class)) ||
                skill.getType().equals(SkillType.INTRINSIC);
            }
        } else {
            return false;
        }
    }
    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {

            // Stop if invalid mode or cooldown
            if (instance.getMode() != 6 || instance.onCoolDown()) {
                return false;
            }

            // Gain mastery every 3 seconds
            if (heldTicks % 60 == 0 && heldTicks > 0) {
                addMasteryPoint(instance, entity);
            }

            // Send FX packet
            TensuraNetwork.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new RequestFxSpawningPacket(new ResourceLocation("tensura:haki"),
                            entity.getId(), 0.0D, 1.0D, 0.0D, true)
            );

            // Affect nearby entities
            List<LivingEntity> nearbyEntities = entity.getLevel().getEntitiesOfClass(
                    LivingEntity.class,
                    entity.getBoundingBox().inflate(15.0D),
                    target -> !target.isAlliedTo(entity) && target.isAlive() && !entity.isAlliedTo(target)
            );

            for (LivingEntity target : nearbyEntities) {
                // Skip creative/spectator players
                if (target instanceof Player player && player.getAbilities().instabuild)
                    continue;

                SkillHelper.checkThenAddEffectSource(
                        target,
                        entity,
                        TensuraMobEffects.INFECTION.get(),
                        200,
                        1
                );

                SkillHelper.checkThenAddEffectSource(
                        target,
                        entity,
                        TensuraMobEffects.INSANITY.get(),
                        400,
                        8
                );
            }


            return true;
        }

        return false;
    }

}




