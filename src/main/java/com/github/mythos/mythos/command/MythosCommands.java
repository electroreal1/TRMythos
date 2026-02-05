package com.github.mythos.mythos.command;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.handler.GodClassHandler;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.ShaderPacket;
import com.github.mythos.mythos.registry.skill.Skills;
import com.github.mythos.mythos.voiceoftheworld.TrialManager;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import com.github.mythos.mythos.voiceoftheworld.WorldTrial;
import com.github.mythos.mythos.voiceoftheworld.WorldTrialRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class MythosCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mythos")

                // Godclass
                .then(Commands.literal("godclass")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("reset")
                                .then(Commands.literal("all").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).resetAllOwners();
                                    context.getSource().sendSuccess(Component.literal("§c[Mythos] All God statuses have been wiped."), true);
                                    VoiceOfTheWorld.broadcast(context.getSource().getServer(),
                                            "Notice. All Divine Authorities have been withdrawn. The seats of the Gods are now vacant.");
                                    return 1;
                                }))
                                .then(Commands.literal("dendrahh").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setDendrahhObtained(false);
                                    context.getSource().sendSuccess(Component.literal("§e[Mythos] Dendrahh status reset."), true);
                                    return 1;
                                }))
                                .then(Commands.literal("khonsu").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setKhonsuObtained(false);
                                    context.getSource().sendSuccess(Component.literal("§e[Mythos] Khonsu status reset."), true);
                                    return 1;
                                }))
                                .then(Commands.literal("kthanid").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setKthanidObtained(false);
                                    context.getSource().sendSuccess(Component.literal("§e[Mythos] Kthanid status reset."), true);
                                    return 1;
                                }))
                        )
                        .then(Commands.literal("status").executes(context -> {
                            GodClassHandler handler = GodClassHandler.get(context.getSource().getLevel());
                            String status = String.format("§7Dendrahh: %b | Khonsu: %b | Kthanid: %b",
                                    handler.isDendrahhObtained(), handler.isKhonsuObtained(), handler.isKthanidObtained());
                            context.getSource().sendSuccess(Component.literal(status), false);
                            return 1;
                        }))
                )

                // Bibliomania
                .then(Commands.literal("bibliomania")
                        .then(Commands.literal("get").executes(context -> {
                            if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                                return 0;
                            }

                            Optional<ManasSkillInstance> skillOpt = SkillAPI.getSkillsFrom(player).getSkill(Skills.BIBLIOMANIA.get());

                            if (skillOpt.isPresent()) {
                                CompoundTag tag = skillOpt.get().getOrCreateTag();
                                float rp = tag.getFloat("recordPoints");
                                context.getSource().sendSuccess(Component.literal("§d[Bibliomania] §fYour current Record Points: §b" + rp), false);
                            } else {
                                context.getSource().sendFailure(Component.literal("§cError: You do not possess the Bibliomania skill."));
                            }
                            return 1;
                        }))

                        .then(Commands.literal("set")
                                .requires(source -> source.hasPermission(4))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0, 1000))
                                                .executes(context -> {
                                                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                                                    float amount = FloatArgumentType.getFloat(context, "amount");
                                                    Optional<ManasSkillInstance> skillOpt = SkillAPI.getSkillsFrom(targetPlayer).getSkill(Skills.BIBLIOMANIA.get());

                                                    if (skillOpt.isPresent()) {
                                                        ManasSkillInstance inst = skillOpt.get();
                                                        CompoundTag tag = inst.getOrCreateTag();
                                                        tag.putFloat("recordPoints", amount);
                                                        inst.markDirty();
                                                        context.getSource().sendSuccess(Component.literal("§d[Bibliomania] §fSet Record Points for " + targetPlayer.getScoreboardName() + " to §b" + amount), true);
                                                    } else {
                                                        context.getSource().sendFailure(Component.literal("§cError: Target does not have the Bibliomania skill."));
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )


                // World Trials
                .then(Commands.literal("trials")
                        .then(Commands.literal("get").executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            VoiceOfTheWorld.delayedAnnouncement(player,
                                    "Inquiry received.",
                                    "Scanning internal soul progress...",
                                    "Current Trials: " + WorldTrialRegistry.getActiveTrialStatus(player)
                            );
                            return 1;
                        }))

                        .then(Commands.literal("list").executes(context -> {
                            if (WorldTrialRegistry.TRIALS.isEmpty()) {
                                context.getSource().sendSuccess(Component.literal("§c[Mythos] No trials currently registered."), false);
                                return 0;
                            }

                            context.getSource().sendSuccess(Component.literal("§b--- Global Trial Registry ---"), false);
                            WorldTrialRegistry.TRIALS.forEach((id, trial) -> {
                                context.getSource().sendSuccess(Component.literal("§7- §f" + id + " §8(§d" + trial.getName() + "§8)"), false);
                            });
                            return 1;
                        })))

                .then(Commands.literal("pause")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("state", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    TrialManager.setPaused(state);
                                    String msg = state ? "§cPAUSED" : "§aRESUMED";
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos] §fWorld Trials are now " + msg), true);
                                    return 1;
                                })))
                .then(Commands.literal("simulate")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("trialId", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                            String trialId = StringArgumentType.getString(context, "trialId");
                                            WorldTrial trial = WorldTrialRegistry.TRIALS.get(trialId);

                                            if (trial != null) {
                                                trial.checkProgress(target, trial.getRequirement());
                                                context.getSource().sendSuccess(Component.literal("§a[Mythos] Simulated completion of " + trialId + " for " + target.getScoreboardName()), true);
                                            } else {
                                                context.getSource().sendFailure(Component.literal("§cError: Trial not found."));
                                            }
                                            return 1;
                                        }))))

                .requires(source -> source.hasPermission(4))
                // Clear
                .then(Commands.literal("clear")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                    TrialManager.clearActiveTrial(target);
                                    context.getSource().sendSuccess(Component.literal("§a[Mythos] Cleared active trials for " + target.getScoreboardName()), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("reset_all")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("target", EntityArgument.player()).executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            CompoundTag tag = target.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG);

                            tag.getAllKeys().stream()
                                    .filter(key -> key.startsWith("Trial_"))
                                    .toList()
                                    .forEach(tag::remove);

                            TrialManager.clearActiveTrial(target);
                            context.getSource().sendSuccess(Component.literal("§a[Mythos] All trial data purged for " + target.getScoreboardName()), true);
                            return 1;
                        })))
                // Set
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("trialId", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                            String trialId = StringArgumentType.getString(context, "trialId");

                                            if (!WorldTrialRegistry.TRIALS.containsKey(trialId)) {
                                                context.getSource().sendFailure(Component.literal("§cError: Trial ID '" + trialId + "' not found in registry."));
                                                return 0;
                                            }

                                            TrialManager.clearActiveTrial(target);
                                            TrialManager.initiateTrial(target, trialId);

                                            context.getSource().sendSuccess(Component.literal("§a[Mythos] Forced trial '" + trialId + "' onto " + target.getScoreboardName()), true);
                                            return 1;
                                        })
                                )
                        )
                )


                // Config
                .then(Commands.literal("config")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("status").executes(context -> {
                            StringBuilder sb = new StringBuilder("§6--- Mythos Config Status ---\n");
                            sb.append("§7Vampire Ancestor: ").append(MythosSkillsConfig.VampireAncestor.get() ? "§aON" : "§cOFF").append("\n");
                            sb.append("§7Dead Apostle: ").append(MythosSkillsConfig.DeadApostleAncestor.get() ? "§aON" : "§cOFF").append("\n");
                            sb.append("§7Ultimate Obtainment: ").append(MythosSkillsConfig.EnableUltimateSkillObtainment.get() ? "§aON" : "§cOFF");
                            sb.append("§7Ultimate Obtainment: ").append(MythosSkillsConfig.endOfEvilReset.get() ? "§aON" : "§cOFF");
                            sb.append("§7Ultimate Obtainment: ").append(MythosSkillsConfig.ApophisEmbodiment.get() ? "§aON" : "§cOFF");
                            sb.append("§7Ultimate Obtainment: ").append(GodClassHandler.get(context.getSource().getLevel()).isAnnouncementsEnabled() ? "§aON" : "§cOFF");
                            sb.append("§7Ultimate Obtainment: ").append(MythosSkillsConfig.endOfEvilReset.get() ? "§aON" : "§cOFF");
                            sb.append("§7Ultimate Obtainment: ").append(MythosSkillsConfig.endOfEvilReset.get() ? "§aON" : "§cOFF");
                            context.getSource().sendSuccess(Component.literal(sb.toString()), false);
                            return 1;
                        }))
                        // Vampire Ancestor
                        .then(Commands.literal("vampire_ancestor")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.VampireAncestor.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fVampireAncestor set to: " + val), true);
                                    return 1;
                                })))
                        // Dead Apostle Ancestor
                        .then(Commands.literal("dead_apostle_ancestor")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.DeadApostleAncestor.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fDeadApostleAncestor set to: " + val), true);
                                    return 1;
                                })))
                        // Vampire Carnage
                        .then(Commands.literal("vampire_carnage")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.VampireCarnage.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fVampireCarnage set to: " + val), true);
                                    return 1;
                                })))
                        // End of Evil Reset
                        .then(Commands.literal("end_of_evil_reset")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.endOfEvilReset.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fendOfEvilReset set to: " + val), true);
                                    return 1;
                                })))
                        // Apophis Embodiment
                        .then(Commands.literal("apophis_embodiment")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.ApophisEmbodiment.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fApophisEmbodiment set to: " + val), true);
                                    return 1;
                                })))
                        // Enable Ultimate Skill Obtainment
                        .then(Commands.literal("enable_ultimate_skills")
                                .then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                                    boolean val = BoolArgumentType.getBool(context, "value");
                                    MythosSkillsConfig.EnableUltimateSkillObtainment.set(val);
                                    context.getSource().sendSuccess(Component.literal("§6[Mythos Config] §fEnableUltimateSkillObtainment set to: " + val), true);
                                    return 1;
                                })))

                        .then(Commands.literal("announce_ultimates")
                                .requires(source -> source.hasPermission(4))
                                .then(Commands.literal("on").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setAnnouncementsEnabled(true);
                                    context.getSource().sendSuccess(Component.literal("§a[Mythos] Announce Ultimate Skills is now enabled."), true);
                                    return 1;
                                }))
                                .then(Commands.literal("off").executes(context -> {
                                    GodClassHandler.get(context.getSource().getLevel()).setAnnouncementsEnabled(false);
                                    context.getSource().sendSuccess(Component.literal("§c[Mythos] Announce Ultimate Skills is now disabled."), true);
                                    return 1;
                                }))
                        )
                )

                // ADMIN COMMANDS

                .then(Commands.literal("inspect")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("target", EntityArgument.player()).executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            CompoundTag tag = target.getPersistentData().getCompound(ServerPlayer.PERSISTED_NBT_TAG);

                            context.getSource().sendSuccess(Component.literal("§b--- Inspecting " + target.getScoreboardName() + " ---"), false);
                            context.getSource().sendSuccess(Component.literal("§7Active Trial: §f" + TrialManager.getActiveTrialID(target)), false);
                            context.getSource().sendSuccess(Component.literal("§7First Login: §f" + tag.getBoolean("Mythos_FirstLoginHandled")), false);
                            return 1;
                        }))
                )

                .then(Commands.literal("ep")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("target", EntityArgument.player())

                                .then(Commands.literal("remove")
                                        .then(Commands.literal("mp").then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                            double result = Math.max(0, TensuraPlayerCapability.getBaseMagicule(target) - DoubleArgumentType.getDouble(context, "amount"));
                                            TensuraPlayerCapability.setMagicule(target, result);
                                            context.getSource().sendSuccess(Component.literal("§c[EP] §fReduced MP for " + target.getScoreboardName()), true);
                                            return 1;
                                        })))
                                        .then(Commands.literal("ap").then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                            double result = Math.max(0, TensuraPlayerCapability.getBaseAura(target) - DoubleArgumentType.getDouble(context, "amount"));
                                            TensuraPlayerCapability.setAura(target, result);
                                            context.getSource().sendSuccess(Component.literal("§c[EP] §fReduced AP for " + target.getScoreboardName()), true);
                                            return 1;
                                        })))
                                        .then(Commands.literal("total").then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                            double sub = DoubleArgumentType.getDouble(context, "amount") / 2.0;
                                            TensuraPlayerCapability.setMagicule(target, Math.max(0, TensuraPlayerCapability.getBaseMagicule(target) - sub));
                                            TensuraPlayerCapability.setAura(target, Math.max(0, TensuraPlayerCapability.getBaseAura(target) - sub));
                                            context.getSource().sendSuccess(Component.literal("§c[EP] §fReduced total EP for " + target.getScoreboardName()), true);
                                            return 1;
                                        })))

                                        .then(Commands.literal("get")
                                                .executes(context -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                    double mp = TensuraPlayerCapability.getBaseMagicule(target);
                                                    double ap = TensuraPlayerCapability.getBaseAura(target);
                                                    double total = mp + ap;

                                                    context.getSource().sendSuccess(Component.literal("§b--- EP Status: " + target.getScoreboardName() + " ---"), false);
                                                    context.getSource().sendSuccess(Component.literal("§7Magicules (MP): §b" + String.format("%.0f", mp)), false);
                                                    context.getSource().sendSuccess(Component.literal("§7Aura (AP): §e" + String.format("%.0f", ap)), false);
                                                    context.getSource().sendSuccess(Component.literal("§7Total EP: §a" + String.format("%.0f", total)), false);
                                                    return 1;
                                                }))


                                        .then(Commands.literal("set")
                                                .then(Commands.literal("magicules")
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).executes(context -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            TensuraPlayerCapability.setMagicule(target, amount);
                                                            context.getSource().sendSuccess(Component.literal("§b[EP] §fSet Magicules for " + target.getScoreboardName() + " to " + amount), true);
                                                            return 1;
                                                        })))
                                                .then(Commands.literal("aura")
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).executes(context -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            TensuraPlayerCapability.setAura(target, amount);
                                                            context.getSource().sendSuccess(Component.literal("§e[EP] §fSet Aura for " + target.getScoreboardName() + " to " + amount), true);
                                                            return 1;
                                                        })))
                                                .then(Commands.literal("total")
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0)).executes(context -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            double split = amount / 2.0;
                                                            TensuraPlayerCapability.setMagicule(target, split);
                                                            TensuraPlayerCapability.setAura(target, split);
                                                            context.getSource().sendSuccess(Component.literal("§a[EP] §fSet Total EP for " + target.getScoreboardName() + " to " + amount + " (50/50 split)"), true);
                                                            return 1;
                                                        })))
                                        )

                                        .then(Commands.literal("add")
                                                .then(Commands.literal("magicules")
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg()).executes(context -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            TensuraPlayerCapability.setMagicule(target, TensuraPlayerCapability.getBaseMagicule(target) + amount);
                                                            context.getSource().sendSuccess(Component.literal("§b[EP] §fAdded " + amount + " Magicules to " + target.getScoreboardName()), true);
                                                            return 1;
                                                        })))
                                                .then(Commands.literal("aura")
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg()).executes(context -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            TensuraPlayerCapability.setAura(target, TensuraPlayerCapability.getBaseAura(target) + amount);
                                                            context.getSource().sendSuccess(Component.literal("§e[EP] §fAdded " + amount + " Aura to " + target.getScoreboardName()), true);
                                                            return 1;
                                                        })))
                                                .then(Commands.literal("total")
                                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg()).executes(context -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                            double split = amount / 2.0;
                                                            TensuraPlayerCapability.setMagicule(target, TensuraPlayerCapability.getBaseMagicule(target) + split);
                                                            TensuraPlayerCapability.setAura(target, TensuraPlayerCapability.getBaseAura(target) + split);
                                                            context.getSource().sendSuccess(Component.literal("§a[EP] §fAdded " + amount + " Total EP to " + target.getScoreboardName()), true);
                                                            return 1;
                                                        })))
                                        )
                                )
                        ))

                .then(Commands.literal("soulscan")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("target", EntityArgument.player()).executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");

                            String raceName = Objects.requireNonNull(Objects.requireNonNull(TensuraPlayerCapability.getRace(target)).getRegistryName()).toString();
                            double mag = TensuraPlayerCapability.getBaseMagicule(target);
                            double aura = TensuraPlayerCapability.getBaseAura(target);
                            boolean isSeed = TensuraPlayerCapability.isDemonLordSeed(target);
                            boolean isEgg = TensuraPlayerCapability.isHeroEgg(target);
                            boolean isDemon = TensuraPlayerCapability.isTrueDemonLord(target);
                            boolean isHero = TensuraPlayerCapability.isTrueHero(target);

                            StringBuilder uniqueSkills = new StringBuilder();
                            StringBuilder ultimateSkills = new StringBuilder();

                            SkillAPI.getSkillsFrom(target).getLearnedSkills().forEach(instance -> {
                                Skill skill = (Skill) instance.getSkill();
                                String displayName = Objects.requireNonNull(skill.getColoredName()).getString();

                                if (skill.getType() == Skill.SkillType.ULTIMATE) {
                                    ultimateSkills.append("§6[").append(displayName).append("] ");
                                } else if (skill.getType() == Skill.SkillType.UNIQUE) {
                                    uniqueSkills.append("§d[").append(displayName).append("] ");
                                }
                            });

                            StringBuilder sb = new StringBuilder();
                            sb.append("§b--- Soul Signature: §f").append(target.getScoreboardName()).append(" §b---\n");
                            sb.append("§7Race: §d").append(raceName).append("\n");
                            sb.append("§7Energy: §b").append(String.format("%.0f", mag)).append("M §f/ §e").append(String.format("%.0f", aura)).append("A\n");

                            sb.append("§7Potential: ");
                            if (isSeed) sb.append("§4[Demon Lord Seed] ");
                            if (isEgg) sb.append("§6[Hero's Egg] ");
                            if (isDemon) sb.append("§4[True Demon Lord] ");
                            if (isHero) sb.append("§6[True Hero] ");
                            if (!isSeed && !isEgg && !isHero && !isDemon) sb.append("§8None");
                            sb.append("\n");

                            sb.append("§7Ultimates: ").append(!ultimateSkills.isEmpty() ? ultimateSkills : "§8None").append("\n");
                            sb.append("§7Uniques: ").append(!uniqueSkills.isEmpty() ? uniqueSkills : "§8None");

                            context.getSource().sendSuccess(Component.literal(sb.toString()), false);
                            return 1;
                        })))

                .then(Commands.literal("announce")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("priority", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("WORLD").suggest("ACQUISITION").suggest("PROGRESS");
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String priorityStr = StringArgumentType.getString(context, "priority");
                                            String message = StringArgumentType.getString(context, "message");
                                            VoiceOfTheWorld.Priority priority = VoiceOfTheWorld.Priority.valueOf(priorityStr.toUpperCase());

                                            // Broadcast to all players
                                            context.getSource().getServer().getPlayerList().getPlayers().forEach(player ->
                                                    VoiceOfTheWorld.delayedAnnouncement(player, priority, message)
                                            );

                                            // context.getSource().sendSuccess(Component.literal("§a[Mythos] Queued global announcement."), true);
                                            return 1;
                                        }))))

                .then(Commands.literal("dimension_stats")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> {
                            var players = context.getSource().getServer().getPlayerList().getPlayers();
                            Map<String, Integer> counts = new HashMap<>();

                            for (ServerPlayer p : players) {
                                String dim = p.level.dimension().location().toString();
                                counts.put(dim, counts.getOrDefault(dim, 0) + 1);
                            }

                            StringBuilder sb = new StringBuilder("§b--- Dimension Population ---\n");
                            counts.forEach((dim, count) -> sb.append("§7").append(dim).append(": §f").append(count).append("\n"));
                            context.getSource().sendSuccess(Component.literal(sb.toString()), false);
                            return 1;
                        }))

                .then(Commands.literal("world")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("shaders")
                                .then(Commands.literal("tint")
                                        .then(Commands.argument("r", FloatArgumentType.floatArg(0, 1))
                                                .then(Commands.argument("g", FloatArgumentType.floatArg(0, 1))
                                                        .then(Commands.argument("b", FloatArgumentType.floatArg(0, 1))
                                                                .then(Commands.argument("targets", EntityArgument.players())
                                                                        .executes(context -> {
                                                                            float r = FloatArgumentType.getFloat(context, "r");
                                                                            float g = FloatArgumentType.getFloat(context, "g");
                                                                            float b = FloatArgumentType.getFloat(context, "b");
                                                                            var players = EntityArgument.getPlayers(context, "targets");

                                                                            players.forEach(p -> {
                                                                                MythosNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p),
                                                                                        new ShaderPacket("trmythos:shaders/post/master_sky.json", r, g, b));
                                                                            });
                                                                            return 1;
                                                                        })))))))

                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("scream").executes(context -> {
                            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                                VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.WORLD,
                                        "§4[ CRITICAL ERROR ]",
                                        "§cSoul-Space partition dissolved.",
                                        "§4Entering the Void.");

                                player.playNotifySound(net.minecraft.sounds.SoundEvents.ENDER_DRAGON_DEATH, SoundSource.MASTER, 1.2f, 0.1f);
                                player.playNotifySound(net.minecraft.sounds.SoundEvents.GHAST_SCREAM, SoundSource.MASTER, 0.8f, 0.5f);
                                player.playNotifySound(net.minecraft.sounds.SoundEvents.WITHER_SPAWN, SoundSource.MASTER, 1.0f, 0.2f);

                                MythosNetwork.sendToPlayer(new ShaderPacket("trmythos:shaders/post/master_sky.json", 0.6f, 0.0f, 0.0f), player);

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(3500);
                                        MythosNetwork.sendToPlayer(new ShaderPacket("trmythos:shaders/post/master_sky.json", 0.0f, 0.0f, 0.0f), player);
                                    } catch (InterruptedException e) { e.printStackTrace(); }
                                }).start();
                            }
                            return 1;
                        }))

                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("restore").executes(context -> {
                            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {

                                VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.WORLD,
                                        "§b[ SYSTEM RECOVERY ]",
                                        "§fRe-establishing soul-space partitions...",
                                        "§7Calibrating visual sensors.");

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(2000);

                                        MythosNetwork.sendToPlayer(new ShaderPacket("trmythos:shaders/post/master_sky.json", 0.2f, 0.2f, 0.2f), player);
                                        player.playNotifySound(net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE, net.minecraft.sounds.SoundSource.MASTER, 0.5f, 0.5f);
                                        Thread.sleep(3000);

                                        MythosNetwork.sendToPlayer(new ShaderPacket("trmythos:shaders/post/master_sky.json", 0.5f, 0.5f, 0.6f), player);
                                        Thread.sleep(3000);

                                        MythosNetwork.sendToPlayer(new ShaderPacket("none", 1.0f, 1.0f, 1.0f), player);
                                        player.playNotifySound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.MASTER, 0.8f, 1.2f);

                                        VoiceOfTheWorld.delayedAnnouncement(player, VoiceOfTheWorld.Priority.WORLD,
                                                "§aNotice.",
                                                "§fWorld stability restored.",
                                                "§7Soul core synchronized.");

                                    } catch (InterruptedException e) { e.printStackTrace(); }
                                }).start();
                            }
                            return 1;
                        })))

        );
    }
}