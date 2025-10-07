package com.github.mythos.mythos.ability.skill.unique;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.magic.Magic;
import com.github.manasmods.tensura.ability.magic.Magic.MagicType;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import com.github.manasmods.tensura.ability.skill.unique.GreatSageSkill;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.event.SkillPlunderEvent;
import com.github.manasmods.tensura.registry.skill.ExtraSkills;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class OmniscientEyeSkill extends Skill {
    protected static final UUID ACCELERATION = UUID.fromString("0147c153-32a2-4524-8ba3-ba4c2f449d7c");
    public OmniscientEyeSkill() {
        super(SkillType.UNIQUE);
    }


    @Nullable
    @Override
    public ResourceLocation getSkillIcon() {
        return new ResourceLocation("trmythos", "textures/skill/unique/omniscienteye.png");
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double getObtainingEpCost() {
        return 75000.0;
    }

    public double learningCost() {
        return 10000.0;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
    ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
    }

    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            SkillUtils.learnSkill(entity, (ManasSkill)ExtraSkills.SAGE.get());
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)entity;
                TensuraAdvancementsHelper.grant(player, TensuraAdvancementsHelper.Advancements.MASTER_SMITH);
            }
        }

    }

    public int modes() {
        return 2;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        return instance.getMode() == 1 ? 2 : 1;
    }

    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("tensura.skill.mode.great_sage.analytical_appraisal");
                break;
            case 2:
                var10000 = Component.translatable("tensura.skill.mode.great_sage.analysis");
                break;
            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (instance.getMode() == 1) {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                TensuraSkillCapability.getFrom(player).ifPresent((cap) -> {
                    int level;
                    if (player.isCrouching()) {
                        level = cap.getAnalysisMode();
                        switch (level) {
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
                        }

                        player.playNotifySound(SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        TensuraSkillCapability.sync(player);
                    } else {
                        level = instance.isMastered(entity) ? 118 : 108;
                        if (cap.getAnalysisLevel() != level) {
                            cap.setAnalysisLevel(level);
                            cap.setAnalysisDistance(instance.isMastered(entity) ? 130 : 120);
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        } else {
                            cap.setAnalysisLevel(100);
                            entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }

                        TensuraSkillCapability.sync(player);
                    }
                });
            }
        } else {
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
                    int chance = 75;
                    boolean failed = true;
                    if (entity.getRandom().nextInt(100) <= chance) {
                        List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canCopy).toList();
                        if (!collection.isEmpty()) {
                            this.addMasteryPoint(instance, entity);
                            ManasSkill skill = ((ManasSkillInstance)collection.get(target.getRandom().nextInt(collection.size()))).getSkill();
                            SkillPlunderEvent event = new SkillPlunderEvent(target, entity, false, skill);
                            if (!MinecraftForge.EVENT_BUS.post(event) && SkillUtils.learnSkill(entity, event.getSkill(), instance.getRemoveTime())) {
                                instance.setCoolDown(1);
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
                        instance.setCoolDown(1);
                    }
                }
            }

        }
    }

    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        if (instance.isToggled()) {
            this.gainMastery(instance, entity);
        }
    }

    private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("activatedTimes");
        if (time % 12 == 0) {
            this.addMasteryPoint(instance, entity);
        }

        tag.putInt("activatedTimes", time + 1);
    }

    public boolean canCopy(ManasSkillInstance instance) {
        if (!instance.isTemporarySkill() && instance.getMastery() >= 0) {
            ManasSkill var3 = instance.getSkill();
            if (!(var3 instanceof Skill)) {
                return false;
            } else {
                Skill skill = (Skill)var3;
                Magic magic = (Magic)var3;
                return skill.getType().equals(SkillType.COMMON) ||
                        skill.getType().equals(SkillType.EXTRA) ||
                        skill.getType().equals(SkillType.INTRINSIC)||
                        magic.getType().equals(MagicType.SPIRITUAL) ||
                        skill.getType().equals(SkillType.RESISTANCE);
                }
            }
            return false;
        }
}
