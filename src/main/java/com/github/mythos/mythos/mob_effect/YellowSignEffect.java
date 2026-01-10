package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.config.client.TensuraClientConfig;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class YellowSignEffect extends MobEffect {
    // Initialize to prevent null pointer errors before config loads
    private static List<SoundEvent> randomSounds = new ArrayList<>();
    public static float ITEM_MOVE_CHANCE = 0.2F;

    public YellowSignEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        if (pLivingEntity instanceof Player player && !player.level.isClientSide) {
            playInsanitySound(TensuraSoundEvents.MC_ANIMAL1.get(), player, 0.7F);
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (SkillUtils.isSkillToggled(entity, (ManasSkill) ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get())) {
            pAmplifier -= 2;
        }

        if (pAmplifier >= 0) {
            damageEntity(entity, pAmplifier);

            if (entity instanceof Player player && !player.level.isClientSide) {
                if (!player.isSleeping() && !randomSounds.isEmpty()) {
                    if (player.tickCount % 80 == 0) {
                        SoundEvent event = null;

                        if ((player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)) &&
                                player.getRandom().nextInt(20) == 3) {
                            event = TensuraSoundEvents.MC_DARK4.get();
                        }

                        if (event == null) {
                            event = randomSounds.get(player.getRandom().nextInt(randomSounds.size()));
                        }

                        if (player.getRandom().nextInt(10) == 3) {
                            float volume = (event == TensuraSoundEvents.MC_VOICES3.get()) ? 0.2F : 0.5F;
                            playInsanitySound(event, player, volume);
                        }
                    }
                }
            }
        }
    }

    public static void damageEntity(LivingEntity entity, int amplifier) {
        if (amplifier < 0) return;

        Player sourcePlayer = null;
        if (entity instanceof Player player) {
            if (player.isSleeping()) return;
        }

        sourcePlayer = TensuraEffectsCapability.getEffectSource(entity, (MobEffect) MythosMobEffects.YELLOW_SIGN.get());

        float spiritualDamage = 2.0F + (float)(amplifier * 2);

        if (sourcePlayer == null) {
            DamageSourceHelper.directSpiritualHurt(entity, null, TensuraDamageSources.INSANITY, spiritualDamage);
        } else {
            DamageSourceHelper.directSpiritualHurt(entity, sourcePlayer, TensuraDamageSources.insanity(sourcePlayer), spiritualDamage);
        }

        if (entity.level.getMaxLocalRawBrightness(entity.blockPosition()) < 1 + amplifier * 3) {
            if (sourcePlayer == null) {
                entity.hurt(TensuraDamageSources.INSANITY, (float)amplifier);
            } else {
                entity.hurt(TensuraDamageSources.insanity(sourcePlayer), (float)amplifier);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 40 == 0;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }


    public static void onServerTick(ServerPlayer player) {
        if (player.hasEffect(TensuraMobEffects.INSANITY.get())) {
            nightmareTicks(player);
            if (player.tickCount % 100 == 0 && player.getRandom().nextFloat() <= ITEM_MOVE_CHANCE) {
                moveItems(player);
            }
        }
    }

    private static void moveItems(Player player) {
        Inventory inv = player.getInventory();
        RandomSource random = player.getRandom();

        int slot1 = random.nextInt(9, 36);
        int slot2 = random.nextInt(9, 36);

        ItemStack item1 = inv.getItem(slot1);
        ItemStack item2 = inv.getItem(slot2);

        if (!item1.isEmpty() || !item2.isEmpty()) {
            ItemStack temp = item1.copy();
            inv.setItem(slot1, item2.copy());
            inv.setItem(slot2, temp);
        }
    }

    private static void nightmareTicks(Player player) {
        TensuraEffectsCapability.getFrom(player).ifPresent((cap) -> {
            if (!havingNightmare(player)) {
                if (cap.getInsanityNightmare() != 0) cap.setInsanityNightmare(0);
                if (cap.getInsanityFOV() != 0) cap.setInsanityFOV(0);
                TensuraEffectsCapability.sync(player);
            } else {
                int nightmareTick = cap.getInsanityNightmare();

                if (nightmareTick == 100) {
                    playInsanitySound(TensuraSoundEvents.MC_ADDITION6.get(), player, 1.0F);
                } else if (nightmareTick == 140) {
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300, 1, false, false, false));
                } else if (nightmareTick >= 160) {
                    wakeUp(player);
                    cap.setInsanityNightmare(-1);
                }

                if (nightmareTick > 100 && nightmareTick < 160) {
                    cap.setInsanityFOV(cap.getInsanityFOV() + 1);
                }

                cap.setInsanityNightmare(nightmareTick + 1);
                TensuraEffectsCapability.sync(player);
            }
        });
    }

    private static void wakeUp(Player player) {
        MobEffectInstance instance = player.getEffect(TensuraMobEffects.INSANITY.get());
        int amplifier = instance == null ? 0 : instance.getAmplifier();

        player.stopSleeping();
        damageEntity(player, amplifier + 2);
        playInsanitySound(TensuraSoundEvents.MC_DARK1.get(), player, 1.0F);

        String key = "effect.tensura.insanity.";
        MutableComponent[] messages = {
                Component.translatable(key + "voices"),
                Component.translatable(key + "gaze"),
                Component.translatable(key + "fear"),
                Component.translatable(key + "unknown")
        };

        player.displayClientMessage(messages[player.getRandom().nextInt(messages.length)].withStyle(ChatFormatting.RED), false);
        player.addEffect(new MobEffectInstance(TensuraMobEffects.FEAR.get(), 160, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1, false, false, false));
    }

    public static void loadConfig() {
        randomSounds = TensuraClientConfig.INSTANCE.effectsConfig.sounds.get().stream()
                .map(ResourceLocation::new)
                .map(ForgeRegistries.SOUND_EVENTS::getValue)
                .filter(Objects::nonNull)
                .toList();
    }

    public static boolean havingNightmare(LivingEntity entity) {
        return entity instanceof Player && entity.isSleeping() && entity.hasEffect(TensuraMobEffects.INSANITY.get());
    }

    public static void playInsanitySound(SoundEvent event, Player player, float volume) {
        player.playNotifySound(event, SoundSource.HOSTILE, volume, 1.0F);
    }
}