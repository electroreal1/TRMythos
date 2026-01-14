package com.github.mythos.mythos.ability.mythos.skill.unique.normal;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.github.mythos.mythos.handler.BibliomaniaChatHandler.getCopiedSkills;

public class BibliomaniaSkill extends Skill {
    public BibliomaniaSkill() {
        super(SkillType.UNIQUE);
    }


    @Nullable
    @Override
    public double getObtainingEpCost() {
        return 6666.0;
    }

    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
        return true;
    }

    public double learningCost() {
        return 10000.0;
    }

    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return false;
    }

    protected boolean canActivateInRaceLimit(ManasSkillInstance instance) {
        return instance.getMode() == 1;
    }

    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {

    }

    @Override
    public int getMaxMastery() {
        return 1500;
    }

    public boolean meetEPRequirement(Player player, double newEP, LivingEntity entity) {
        int eatenSpiderEyes = 0;
        if (player.level.isClientSide) {
            if (player instanceof LocalPlayer localPlayer) {
                eatenSpiderEyes = localPlayer.getStats().getValue(Stats.ITEM_USED.get(Items.SPIDER_EYE));
            }
        } else if (player instanceof ServerPlayer serverPlayer) {
            eatenSpiderEyes = serverPlayer.getStats().getValue(Stats.ITEM_USED.get(Items.SPIDER_EYE));
        }
        if (eatenSpiderEyes >= 10) {
            player.displayClientMessage(Component.literal("You teeter at the precipice. One articulation of the word suffices.").withStyle(ChatFormatting.DARK_PURPLE), false);
            return true;
        }
        return false;
    }


    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {

    }

    public void onLearnSkill(@NotNull ManasSkillInstance instance, @NotNull LivingEntity entity, @NotNull UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (entity instanceof ServerPlayer player) {
                TensuraAdvancementsHelper.grant(player, TensuraAdvancementsHelper.Advancements.MASTER_SMITH);
            }
        }
    }

    public void onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingHurtEvent e) {
        LivingEntity target = e.getEntity();
        if (!isInSlot(attacker)) return;
        if (attacker instanceof Player player) {
            if (!SkillHelper.outOfMagicule(player, instance))
                SkillHelper.addEffectWithSource(target, player, TensuraMobEffects.CORROSION.get(), 100, 2);
            SkillHelper.addEffectWithSource(target, player, TensuraMobEffects.FATAL_POISON.get(), 300, 2);
        }
    }

    public static float getBibliomaniaBoost(Player player, boolean magicule, boolean majin) {
        TensuraSkill skill = Skills.BIBLIOMANIA.get();
        Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(skill);
        if (optional.isEmpty()) {
            return 0.0F;
        } else if (majin) {
            return magicule ? 0.10F : 0.07F;
        } else {
            return magicule ? 0.10F : 0.08F;
        }
    }

    public int modes() {
        return 3;
    }

    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        CompoundTag tag = instance.getOrCreateTag();
        float recordPoints = tag.getFloat("recordPoints");
        boolean Pronounceable = recordPoints >= 1000.0F;
        int mode = instance.getMode();
        if (reverse) {
            switch (mode) {
                case 1:
                    return Pronounceable ? 5 : 4;
                case 2:
                    return 1;
                case 3:
                    return 2;
                case 4:
                    return 3;
                case 5:
                    return 4;
            }
        }
        switch (mode) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return Pronounceable ? 5 : 1;
            case 5:
                return 1;
        }
        return 1;
    }


    public Component getModeName(int mode) {
        MutableComponent var10000;
        switch (mode) {
            case 1:
                var10000 = Component.translatable("trmythos.skill.mode.bibliomania.bookmark");
                break;
            case 2:
                var10000 = Component.translatable("trmythos.skill.mode.bibliomania.glossary");
                break;
            case 3:
                var10000 = Component.translatable("trmythos.skill.mode.bibliomania.for_my_happiness");
                break;
            case 4:
                var10000 = Component.translatable("trmythos.skill.mode.bibliomania.bird_in_the_window");
                break;
            case 5:
                var10000 = Component.translatable("trmythos.skill.mode.bibliomania.pronounce");
                break;
            default:
                var10000 = Component.empty();
        }

        return var10000;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        int time = tag.getInt("time_lasted");
        if (time % 3 == 0) {
            float current = tag.getFloat("recordPoints");
            float updated = Math.min(current + 1.0F, 1000.0F);
            tag.putFloat("recordPoints", updated);
        }
        tag.putInt("time_lasted", time + 1);
        instance.markDirty();
    }

    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        CompoundTag tag = instance.getOrCreateTag();
        float recordPoints = tag.getFloat("recordPoints");

        // MODE 1 — existing behavior
        if (instance.getMode() == 1) {
            player.displayClientMessage(
                    Component.literal("Record Points: " + recordPoints)
                            .withStyle(ChatFormatting.BLACK),
                    true
            );
            return;
        }
        // MODE 2 — Glossary
        if (instance.getMode() == 2) {

            List<ResourceLocation> skills = getCopiedSkills(instance);

            player.displayClientMessage(
                    Component.literal("=== Glossary ===")
                            .withStyle(ChatFormatting.DARK_PURPLE),
                    false
            );

            if (skills.isEmpty()) {
                player.displayClientMessage(
                        Component.literal("No recorded uniques.")
                                .withStyle(ChatFormatting.DARK_GRAY),
                        false
                );
                return;
            }

            for (ResourceLocation id : skills) {
                player.displayClientMessage(
                        Component.literal("- " + id.toString())
                                .withStyle(ChatFormatting.LIGHT_PURPLE),
                        false
                );
            }

            return;
        }


        // MODE 3 — Words of Power list
        if (instance.getMode() == 3) {

            player.displayClientMessage(
                    Component.literal("=== Words of Power ===")
                            .withStyle(ChatFormatting.DARK_PURPLE),
                    false
            );

            for (WordOfPower word : WORDS_OF_POWER) {
                boolean canUse = recordPoints >= word.cost();

                ChatFormatting color = canUse
                        ? ChatFormatting.DARK_PURPLE
                        : ChatFormatting.BLACK;

                player.displayClientMessage(
                        Component.literal(word.name() + " (" + word.cost() + " RP)")
                                .withStyle(color),
                        false
                );
            }
        }
    }

    private record WordOfPower(String name, int cost) {
    }

    private static final List<WordOfPower> WORDS_OF_POWER = List.of(
            new WordOfPower("Power Word: Study", 75),
            new WordOfPower("Power Word: Explode", 30),
            new WordOfPower("Power Word: Blind", 20),
            new WordOfPower("Power Word: Heal", 10),
            new WordOfPower("Power Word: Alter", 30)
    );

    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (!SkillHelper.outOfMagicule(entity, instance)) {

            if (instance.getMode() != 4 || instance.onCoolDown()) {
                return false;
            }

            if (heldTicks % 40 == 0 && heldTicks > 0) {
                CompoundTag tag = instance.getOrCreateTag();
                float currentP = tag.getFloat("recordPoints");
                float updatedP = Math.min(currentP + 100.0F, 1000.0F);
                tag.putFloat("recordPoints", updatedP);
                instance.markDirty();
            }
            TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new RequestFxSpawningPacket(new ResourceLocation("tensura:haki"), entity.getId(), 0.0D, 1.0D, 0.0D, true));

            List<LivingEntity> nearbyEntities = entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(15.0D), target -> !target.isAlliedTo(entity) && target.isAlive() && !entity.isAlliedTo(target));

            for (LivingEntity target : nearbyEntities) {
                if (target instanceof Player player && player.getAbilities().invulnerable) continue;

                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.MAGIC_INTERFERENCE.get(), 200, 1);

                SkillHelper.checkThenAddEffectSource(target, entity, TensuraMobEffects.MAGICULE_POISON.get(), 400, 3);
            }


            return true;
        }

        return false;
    }
}





