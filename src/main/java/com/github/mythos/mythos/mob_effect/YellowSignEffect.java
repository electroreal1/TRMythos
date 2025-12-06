package com.github.mythos.mythos.mob_effect;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YellowSignEffect extends MobEffect{
    private static List<SoundEvent> randomSounds;
    public static float ITEM_MOVE_CHANCE = 0.2F;

    public YellowSignEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        if (pLivingEntity instanceof Player player) {
            playInsanitySound((SoundEvent) TensuraSoundEvents.MC_ANIMAL1.get(), player, 0.7F);
        }

    }

    public void applyEffectTick(LivingEntity entity, int pAmplifier) {
        if (SkillUtils.isSkillToggled(entity, (ManasSkill) ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get())) {
            pAmplifier -= 2;
        }

        if (pAmplifier >= 0) {
            damageEntity(entity, pAmplifier);
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (!player.isSleeping()) {
                    if (randomSounds != null && !randomSounds.isEmpty()) {
                        MobEffectInstance instance = player.getEffect(this);
                        if (instance != null && instance.getDuration() % 80 == 0) {
                            SoundEvent event = null;
                            if ((player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)) && player.getRandom().nextIntBetweenInclusive(1, 20) == 3) {
                                event = (SoundEvent)TensuraSoundEvents.MC_DARK4.get();
                            }

                            if (event == null) {
                                event = (SoundEvent)randomSounds.get(player.getRandom().nextInt(randomSounds.size()));
                            }

                            if (player.getRandom().nextInt(10) == 3) {
                                float volume = event == TensuraSoundEvents.MC_VOICES3.get() ? 0.2F : 0.5F;
                                playInsanitySound(event, player, volume);
                            }

                        }
                    }
                }
            }
        }
    }

    public static void damageEntity(LivingEntity entity, int amplifier) {
        if (amplifier > 0) {
            Player source;
            if (entity instanceof Player) {
                source = (Player)entity;
                if (source.isSleeping()) {
                    return;
                }
            }

            source = TensuraEffectsCapability.getEffectSource(entity, (MobEffect) TensuraMobEffects.INSANITY.get());
            float spiritualDamage = 2.0F + (float)(amplifier * 2);
            if (source == null) {
                DamageSourceHelper.directSpiritualHurt(entity, (Entity)null, TensuraDamageSources.INSANITY, spiritualDamage);
            } else {
                DamageSourceHelper.directSpiritualHurt(entity, source, TensuraDamageSources.insanity(source), spiritualDamage);
            }

            if (entity.getLevel().getMaxLocalRawBrightness(entity.blockPosition()) < 1 + amplifier * 3) {
                if (source == null) {
                    entity.hurt(TensuraDamageSources.INSANITY, (float)amplifier);
                } else {
                    entity.hurt(TensuraDamageSources.insanity(source), (float)amplifier);
                }
            }

        }
    }

    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 40 == 0;
    }

    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    public static void onServerTick(ServerPlayer player) {
        MobEffectInstance insanity = player.getEffect((MobEffect)TensuraMobEffects.INSANITY.get());
        if (insanity != null) {
            nightmareTicks(player);
            if (player.tickCount % 100 == 0 && player.getRandom().nextFloat() <= ITEM_MOVE_CHANCE) {
                moveItems(player);
            }

        }
    }

    private static void moveItems(Player player) {
        Inventory inv = player.getInventory();
        NonNullList<ItemStack> items = inv.items;
        RandomSource random = player.getRandom();
        int slot1 = random.nextInt(9, items.size());
        int slot2 = random.nextInt(9, items.size());
        ItemStack item1 = inv.getItem(slot1).copy();
        ItemStack item2 = inv.getItem(slot2).copy();
        if (!item1.isEmpty() || !item2.isEmpty()) {
            inv.setItem(slot1, item2);
            inv.setItem(slot2, item1);
        }
    }

    private static void nightmareTicks(Player player) {
        TensuraEffectsCapability.getFrom(player).ifPresent((cap) -> {
            if (!havingNightmare(player)) {
                if (cap.getInsanityNightmare() > 0) {
                    cap.setInsanityNightmare(0);
                }

                if (cap.getInsanityFOV() > 0) {
                    cap.setInsanityFOV(0);
                }

                TensuraEffectsCapability.sync(player);
            } else {
                int nightmareTick = cap.getInsanityNightmare();
                switch (nightmareTick) {
                    case 100:
                        playInsanitySound((SoundEvent)TensuraSoundEvents.MC_ADDITION6.get(), player, 1.0F);
                        break;
                    case 140:
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300, 1, false, false, false));
                        break;
                    case 160:
                        wakeUp(player);
                }

                if (nightmareTick > 100 && nightmareTick < 160) {
                    cap.setInsanityFOV(cap.getInsanityFOV() + 1);
                }

                cap.setInsanityNightmare(nightmareTick + 1);
                if (cap.getInsanityNightmare() > 160) {
                    cap.setInsanityNightmare(-1);
                }

                TensuraEffectsCapability.sync(player);
            }
        });
    }

    private static void wakeUp(Player player) {
        MobEffectInstance instance = player.getEffect((MobEffect)TensuraMobEffects.INSANITY.get());
        int amplifier = instance == null ? 1 : instance.getAmplifier();
        player.stopSleeping();
        damageEntity(player, amplifier + 2);
        playInsanitySound((SoundEvent)TensuraSoundEvents.MC_DARK1.get(), player, 1.0F);
        String key = "effect.tensura.insanity.";
        List<MutableComponent> randomMessages = new ArrayList(List.of(Component.translatable(key + "voices"), Component.translatable(key + "gaze"), Component.translatable(key + "fear"), Component.translatable(key + "unknown")));
        int randomMessage = player.getRandom().nextInt(randomMessages.size());
        player.displayClientMessage(((MutableComponent)randomMessages.get(randomMessage)).withStyle(ChatFormatting.RED), false);
        player.addEffect(new MobEffectInstance((MobEffect)TensuraMobEffects.FEAR.get(), 160, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1, false, false, false));
    }


    public static boolean havingNightmare(LivingEntity entity) {
        MobEffectInstance insanity = entity.getEffect((MobEffect)TensuraMobEffects.INSANITY.get());
        return insanity != null && entity.isSleeping();
    }

    public static void playInsanitySound(SoundEvent event, Player player, float volume) {
        player.playNotifySound(event, SoundSource.HOSTILE, volume, 1.0F);
    }
}
