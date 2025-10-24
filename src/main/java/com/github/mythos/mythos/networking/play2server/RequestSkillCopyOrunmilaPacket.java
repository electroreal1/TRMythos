package com.github.mythos.mythos.networking.play2server;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.mythos.mythos.ability.skill.ultimate.OrunmilaSkill;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RequestSkillCopyOrunmilaPacket {
    private final ResourceLocation skill;

    public RequestSkillCopyOrunmilaPacket(FriendlyByteBuf buf) {
        this.skill = buf.readResourceLocation();
    }

    public RequestSkillCopyOrunmilaPacket(ResourceLocation skill) {
        this.skill = skill;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.skill);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player != null) {
                this.createSkill(player);
            }

        });
        ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }

    private void createSkill(ServerPlayer player) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        Optional<ManasSkillInstance> optionalOrun = storage.getSkill((ManasSkill) Skills.ORUNMILA.get());
        if (!optionalOrun.isEmpty() && !((ManasSkillInstance)optionalOrun.get()).onCoolDown()) {
            ManasSkillInstance orun = (ManasSkillInstance)optionalOrun.get();
            CompoundTag tag = orun.getOrCreateTag();
            ManasSkill newSkill = (ManasSkill)SkillAPI.getSkillRegistry().getValue(this.skill);
            Optional<ManasSkillInstance> optionalSkillInstance = storage.getLearnedSkills().stream().filter((skillInstance) -> {
                return skillInstance.getSkill().equals(newSkill);
            }).findFirst();
            if (newSkill != null) {
                boolean alreadyHas = (Boolean)storage.getLearnedSkills().stream().filter((skillInstance) -> {
                    return skillInstance.getSkill().equals(newSkill);
                }).findFirst().map((skillInstance) -> {
                    if (skillInstance.getMastery() < 0) {
                        return false;
                    } else {
                        CompoundTag tag1 = skillInstance.getOrCreateTag();
                        return tag1.contains("alreadyHas") ? tag1.getBoolean("alreadyHas") : true;
                    }
                }).orElse(false);
                boolean notLearned = (Boolean)storage.getLearnedSkills().stream().filter((skillInstance) -> {
                    return skillInstance.getSkill().equals(newSkill);
                }).findFirst().map((skillInstance) -> {
                    ManasSkill getSkillSkill = skillInstance.getSkill();
                    if (getSkillSkill instanceof Skill skill) {
                        if (skill.getType() == Skill.SkillType.RESISTANCE) {
                            return true;
                        }
                    }

                    if (skillInstance.getMastery() > 0) {
                        return true;
                    } else {
                        CompoundTag tag1 = skillInstance.getOrCreateTag();
                        return tag1.contains("notLearned") ? tag1.getBoolean("notLearned") : true;
                    }
                }).orElse(false);
                int existingMastery = (Integer)storage.getLearnedSkills().stream().filter((skillInstance) -> {
                    return skillInstance.getSkill().equals(newSkill);
                }).map(ManasSkillInstance::getMastery).findFirst().orElse(0);
                CompoundTag skillTags = (CompoundTag)optionalSkillInstance.map(ManasSkillInstance::getOrCreateTag).orElse(new CompoundTag());
                List<String> tagKeys = new ArrayList(skillTags.getAllKeys());
                Map<String, Object> tagKeyValuePairs = new HashMap();
                Iterator var14 = tagKeys.iterator();

                while(true) {
                    while(var14.hasNext()) {
                        String key = (String)var14.next();
                        Tag tagValue = skillTags.get(key);
                        if (tagValue instanceof StringTag) {
                            tagKeyValuePairs.put(key, ((StringTag)tagValue).getAsString());
                        } else if (tagValue instanceof LongTag) {
                            tagKeyValuePairs.put(key, ((LongTag)tagValue).getAsLong());
                        } else if (tagValue instanceof IntTag) {
                            tagKeyValuePairs.put(key, ((IntTag)tagValue).getAsInt());
                        } else if (tagValue instanceof DoubleTag) {
                            tagKeyValuePairs.put(key, ((DoubleTag)tagValue).getAsDouble());
                        } else if (tagValue instanceof ListTag) {
                            ListTag listTag = (ListTag)tagValue;
                            List<Tag> elements = new ArrayList();
                            Iterator var19 = listTag.iterator();

                            while(var19.hasNext()) {
                                Tag element = (Tag)var19.next();
                                elements.add(element);
                            }

                            tagKeyValuePairs.put(key, elements);
                        }
                    }

                    System.out.println("Existing Mastery: " + existingMastery);
                    System.out.println("Skill Tag Keys and Values: " + tagKeyValuePairs);
                    List<ManasSkillInstance> copiedSkills = (List)storage.getLearnedSkills().stream().filter((skillInstance) -> {
                        return skillInstance.getOrCreateTag().getBoolean("orunSkill");
                    }).peek((skillInstance) -> {
                        CompoundTag skillTag = skillInstance.getOrCreateTag();
                        if (!skillTag.contains("timeAdded")) {
                            skillTag.putLong("timeAdded", System.currentTimeMillis());
                        }

                    }).collect(Collectors.toList());
                    if (copiedSkills.size() >= 5) {
                        ManasSkillInstance oldestSkill = (ManasSkillInstance)copiedSkills.stream().min(Comparator.comparingLong((skill) -> {
                            return skill.getOrCreateTag().getLong("timeAdded");
                        })).orElse((ManasSkillInstance) null);
                        if (oldestSkill != null && !oldestSkill.getTag().getBoolean("alreadyHas")) {
                            storage.forgetSkill(oldestSkill.getSkill());
                        } else if (oldestSkill.getTag().getBoolean("notLearned")) {
                            int learningPoints = oldestSkill.getTag().getInt("learningPoint");
                            oldestSkill.setMastery(learningPoints);
                            oldestSkill.getTag().putBoolean("orunSkill", false);
                        } else {
                            CompoundTag oldSkillTag = oldestSkill.getOrCreateTag();
                            oldSkillTag.putBoolean("orunSkill", false);
                        }
                    }

                    double skillCost = newSkill instanceof Skill ? ((Skill)newSkill).getObtainingEpCost() : 0.0;
                    TensuraPlayerCapability.getFrom(player).ifPresent((cap) -> {
                        double currentMP = cap.getBaseMagicule();
                        if (currentMP < skillCost) {
                            player.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
                        } else {
                            TensuraPlayerCapability.decreaseMagicule(player, skillCost);
                            tag.putString("copied_skill", String.valueOf(newSkill.getRegistryName()));
                            tag.putLong("timeAdded", System.currentTimeMillis());
                            TensuraSkillInstance skillInstance = new TensuraSkillInstance(newSkill);
                            skillInstance.setMastery(existingMastery);
                            CompoundTag newSkillTag = skillInstance.getOrCreateTag();
                            Iterator var16 = tagKeyValuePairs.entrySet().iterator();

                            while(true) {
                                while(var16.hasNext()) {
                                    Map.Entry<String, Object> entry = (Map.Entry)var16.next();
                                    String key = (String)entry.getKey();
                                    Object value = entry.getValue();
                                    if (value instanceof String) {
                                        newSkillTag.putString(key, (String)value);
                                    } else if (value instanceof Boolean) {
                                        newSkillTag.putBoolean(key, (Boolean)value);
                                    } else if (value instanceof Long) {
                                        newSkillTag.putLong(key, (Long)value);
                                    } else if (value instanceof Integer) {
                                        newSkillTag.putInt(key, (Integer)value);
                                    } else if (value instanceof Double) {
                                        newSkillTag.putDouble(key, (Double)value);
                                    } else if (value instanceof List) {
                                        List<Tag> list = (List)value;
                                        ListTag newListTag = new ListTag();
                                        Iterator var22 = list.iterator();

                                        while(var22.hasNext()) {
                                            Tag listItem = (Tag)var22.next();
                                            newListTag.add(listItem);
                                        }

                                        newSkillTag.put(key, newListTag);
                                    }
                                }

                                newSkillTag.putBoolean("orunSkill", true);
                                newSkillTag.putLong("timeAdded", System.currentTimeMillis());
                                newSkillTag.putBoolean("alreadyHas", alreadyHas);
                                newSkillTag.putBoolean("notLearned", notLearned);
                                ManasSkill getSkillSkill = skillInstance.getSkill();
                                Skill skill;
                                if (getSkillSkill instanceof Skill) {
                                    skill = (Skill)getSkillSkill;
                                    if (skill.getType() == Skill.SkillType.RESISTANCE && skillInstance.getMastery() < 0) {
                                        newSkillTag.putBoolean("resSkill", true);
                                        newSkillTag.putInt("resMastery", existingMastery);
                                    }
                                }

                                if (newSkillTag.getBoolean("notLearned")) {
                                    newSkillTag.putInt("learningPoint", existingMastery);
                                }

                                System.out.println("Setting tags for copied skill: " + newSkill.getRegistryName());
                                System.out.println("orunSkill: " + newSkillTag.getBoolean("orunSkill"));
                                System.out.println("timeAdded: " + newSkillTag.getLong("timeAdded"));
                                System.out.println("alreadyHas: " + newSkillTag.getBoolean("alreadyHas"));
                                System.out.println("notLearned: " + newSkillTag.getBoolean("alreadyHas"));
                                System.out.println("finalNotLearned: " + notLearned);
                                System.out.println("finalAlreadyHas: " + alreadyHas);
                                ((OrunmilaSkill) Skills.ORUNMILA.get()).addMasteryPoint(orun, player, 5 + SkillUtils.getBonusMasteryPoint(orun, player, 5));
                                if (notLearned) {
                                    skillInstance.setMastery(1);
                                    storage.updateSkill(skillInstance);
                                    player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{newSkill.getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
                                    player.getLevel().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                    TensuraSkillCapability.getFrom(player).ifPresent((skillCap) -> {
                                        if (skillCap.getSkillInSlot(0) == null) {
                                            skillCap.setInstanceInSlot(skillInstance, 0);
                                        } else if (skillCap.getSkillInSlot(1) == null) {
                                            skillCap.setInstanceInSlot(skillInstance, 1);
                                        } else if (skillCap.getSkillInSlot(2) == null) {
                                            skillCap.setInstanceInSlot(skillInstance, 2);
                                        }

                                        TensuraSkillCapability.sync(player);
                                    });
                                } else if (!alreadyHas) {
                                    skillInstance.setRemoveTime(-2);
                                    if (storage.learnSkill(skillInstance)) {
                                        player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{newSkill.getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
                                        player.getLevel().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                        TensuraSkillCapability.getFrom(player).ifPresent((skillCap) -> {
                                            if (skillCap.getSkillInSlot(0) == null) {
                                                skillCap.setInstanceInSlot(skillInstance, 0);
                                            } else if (skillCap.getSkillInSlot(1) == null) {
                                                skillCap.setInstanceInSlot(skillInstance, 1);
                                            } else if (skillCap.getSkillInSlot(2) == null) {
                                                skillCap.setInstanceInSlot(skillInstance, 2);
                                            }

                                            TensuraSkillCapability.sync(player);
                                        });
                                    }
                                } else {
                                    getSkillSkill = skillInstance.getSkill();
                                    if (getSkillSkill instanceof Skill) {
                                        skill = (Skill)getSkillSkill;
                                        if (skill.getType() == Skill.SkillType.RESISTANCE) {
                                            skillInstance.setMastery(1);
                                        }
                                    }

                                    storage.updateSkill(skillInstance);
                                }

                                skillInstance.setToggled(true);
                                getSkillSkill = skillInstance.getSkill();
                                if (getSkillSkill instanceof Skill) {
                                    skill = (Skill)getSkillSkill;
                                    if (skill.getType() == Skill.SkillType.UNIQUE) {
                                        orun.setCoolDown(180);
                                    } else if (skill.getType() == Skill.SkillType.EXTRA) {
                                        orun.setCoolDown(60);
                                    } else if (skill.getType() == Skill.SkillType.COMMON || skill.getType() == Skill.SkillType.INTRINSIC) {
                                        orun.setCoolDown(10);
                                    }
                                }

                                orun.markDirty();
                                storage.syncChanges();
                                player.closeContainer();
                                return;
                            }
                        }
                    });
                    return;
                }
            }
        }
    }
}


