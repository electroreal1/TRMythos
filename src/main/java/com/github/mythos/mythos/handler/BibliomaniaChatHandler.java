package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public class BibliomaniaChatHandler {

    private static boolean INTERNAL = false;

    private static final String COPIED_SKILLS = "CopiedSkills";
    private static final int MAX_UNIQUES = 5;
    private static final int REVERSE_COST = 100;
    private static final int TEMP_DURATION = 20 * 600; // 10 minutes

    private static final Map<String, Integer> WORD_COSTS = Map.of(
            "Blind", 20,
            "Heal", 10,
            "Explode", 30,
            "Study", 75,
            "Alter", 30
    );

    @SubscribeEvent
    public static void onChat(ServerChatEvent.Submitted event) {
        if (event.isCanceled() || INTERNAL) return;
        if (event.getRawText() == null || event.getRawText().isEmpty()) return;

        ServerPlayer player = event.getPlayer();
        String raw = event.getRawText().trim();
        String[] args = raw.split("\\s+");
        String word = args[0];

        INTERNAL = true;
        try {
            Optional<ManasSkillInstance> opt =
                    SkillAPI.getSkillsFrom(player).getSkill(Skills.BIBLIOMANIA.get());

            if (opt.isEmpty()) return;

            ManasSkillInstance instance = opt.get();
            if (instance.isTemporarySkill()) return;

            CompoundTag tag = instance.getOrCreateTag();

            /* =========================
               MODE 2 — Reverse Invocation
               ========================= */
            if (instance.getMode() == 2) {
                float rp = tag.getFloat("recordPoints");
                if (rp < REVERSE_COST) return;

                String spoken = raw.toLowerCase();
                ListTag list = tag.getList(COPIED_SKILLS, 8);

                for (int i = 0; i < list.size(); i++) {
                    ResourceLocation id = new ResourceLocation(list.getString(i));

                    if (!spoken.equals(reverse(id.getPath()))) continue;

                    ManasSkill skill = SkillAPI.getSkillRegistry().getValue(id);
                    if (skill == null) return;

                    if (SkillUtils.hasSkill(player, skill)) {
                        player.displayClientMessage(
                                Component.literal("That power already resides within you.")
                                        .withStyle(ChatFormatting.DARK_GRAY),
                                false
                        );
                        return;
                    }

                    tag.putFloat("recordPoints", rp - REVERSE_COST);
                    list.remove(i); // 🔥 REMOVE AFTER USE
                    tag.put(COPIED_SKILLS, list);
                    instance.markDirty();

                    SkillUtils.learnSkill(player, skill, -TEMP_DURATION);

                    player.displayClientMessage(
                            Component.literal("The record answers, then fades.")
                                    .withStyle(ChatFormatting.DARK_PURPLE),
                            false
                    );

                    event.setCanceled(true);
                    return;
                }
                return;
            }

            /* =========================
               MODE 3 — Words of Power
               ========================= */
            Integer cost = WORD_COSTS.get(word);
            if (cost == null) return;

            float rp = tag.getFloat("recordPoints");
            if (rp < cost) return;

            tag.putFloat("recordPoints", rp - cost);
            instance.markDirty();

            switch (word) {

                case "Blind" -> {
                    for (LivingEntity target : player.level.getEntitiesOfClass(
                            LivingEntity.class, player.getBoundingBox().inflate(20))) {
                        if (target != player && target.isAlive()) {
                            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200));
                            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 6));
                            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200));
                        }
                    }
                }

                case "Heal" -> {
                    player.setHealth(player.getMaxHealth());
                    player.clearFire();
                    player.getActiveEffects().stream()
                            .filter(e -> !e.getEffect().isBeneficial())
                            .map(e -> e.getEffect())
                            .toList()
                            .forEach(player::removeEffect);
                }

                case "Explode" -> {
                    for (LivingEntity target : player.level.getEntitiesOfClass(
                            LivingEntity.class, player.getBoundingBox().inflate(20))) {
                        if (target != player && target.isAlive()) {
                            player.level.explode(
                                    player,
                                    target.getX(),
                                    target.getY(),
                                    target.getZ(),
                                    3.0F,
                                    Explosion.BlockInteraction.NONE
                            );
                        }
                    }
                }

                case "Alter" -> {
                    if (args.length != 4) return;
                    try {
                        double x = Double.parseDouble(args[1]);
                        double y = Double.parseDouble(args[2]);
                        double z = Double.parseDouble(args[3]);
                        player.teleportTo(x + 0.5, y, z + 0.5);
                        player.playNotifySound(
                                SoundEvents.CHORUS_FRUIT_TELEPORT,
                                SoundSource.PLAYERS, 1, 1
                        );
                        player.fallDistance = 0;
                    } catch (NumberFormatException ignored) {
                    }
                }

                case "Study" -> {
                    List<ResourceLocation> stored = getCopiedSkills(instance);
                    if (stored.size() >= MAX_UNIQUES) {
                        player.displayClientMessage(
                                Component.literal("The record cannot hold more than five uniques.")
                                        .withStyle(ChatFormatting.DARK_GRAY),
                                false
                        );
                        return;
                    }

                    LivingEntity target = SkillHelper.getTargetingEntity(
                            LivingEntity.class, player, 6.0D, 0.25D, false, true);

                    if (target == null) return;

                    SkillStorage targetStorage = SkillAPI.getSkillsFrom(target);
                    if (targetStorage == null) return;

                    Optional<ManasSkillInstance> unique =
                            targetStorage.getLearnedSkills().stream()
                                    .filter(s -> !s.isTemporarySkill())
                                    .filter(s -> s.getSkill() instanceof Skill sk
                                            && sk.getType() == Skill.SkillType.UNIQUE)
                                    .findFirst();

                    if (unique.isEmpty()) {
                        player.displayClientMessage(
                                Component.literal("The subject bears no unique knowledge.")
                                        .withStyle(ChatFormatting.DARK_GRAY),
                                false
                        );
                        return;
                    }

                    addCopiedSkill(instance,
                            unique.get().getSkill().getRegistryName());

                    player.displayClientMessage(
                            Component.literal("You inscribe the record.")
                                    .withStyle(ChatFormatting.DARK_PURPLE),
                            false
                    );
                }
            }

            event.setCanceled(true);

        } finally {
            INTERNAL = false;
        }
    }

    /* =========================
       Helpers
       ========================= */

    public static void addCopiedSkill(ManasSkillInstance instance, ResourceLocation id) {
        CompoundTag tag = instance.getOrCreateTag();
        ListTag list = tag.getList(COPIED_SKILLS, 8);

        for (int i = 0; i < list.size(); i++)
            if (list.getString(i).equals(id.toString())) return;

        list.add(net.minecraft.nbt.StringTag.valueOf(id.toString()));
        tag.put(COPIED_SKILLS, list);
        instance.markDirty();
    }

    public static List<ResourceLocation> getCopiedSkills(ManasSkillInstance instance) {
        CompoundTag tag = instance.getOrCreateTag();
        var list = tag.getList(COPIED_SKILLS, 8);
        return list.stream().map(t -> new ResourceLocation(t.getAsString())).toList();
    }

    private static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }
}
