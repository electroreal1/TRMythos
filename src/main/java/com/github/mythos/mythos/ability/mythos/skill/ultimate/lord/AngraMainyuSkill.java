package com.github.mythos.mythos.ability.mythos.skill.ultimate.lord;

import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.event.UnlockSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.registry.skill.ResistanceSkills;
import com.github.mythos.mythos.registry.MythosEngravings;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.mythos.mythos.config.MythosSkillsConfig.EnableUltimateSkillObtainment;

public class AngraMainyuSkill extends Skill {
    private static final String STORED_CURSES = "StoredCurses";
    private static final String TOTAL_CURSE_COUNT = "TotalCurseCount";
    private static final String PLACENTA_TOGGLE = "PlacentaActive";
    private static final UUID INCARNATION_ID = UUID.fromString("d34b55c2-1234-4a21-b331-99c8d2e1f0a2");

    public AngraMainyuSkill(SkillType type) {
        super(type);
    }

    @Override
    public MutableComponent getName() {
        return Component.literal("Angra Mainyu").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
    }

    @Override
    public boolean meetEPRequirement(Player player, double newEP) {
        if (!EnableUltimateSkillObtainment()) return false;
        double currentEP = TensuraEPCapability.getCurrentEP(player);
        if (currentEP < getObtainingEpCost()) {
            return false;
        }
        return SkillUtils.isSkillMastered(player, Skills.SHADOW_AVENGER.get()) && SkillUtils.isSkillMastered(player, Skills.WORLDS_SCAPEGOAT.get());
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public int modes() {
        return 3;
    }

    @Override
    public int nextMode(LivingEntity entity, TensuraSkillInstance instance, boolean reverse) {
        if (reverse)
            return (instance.getMode() == 1) ? 3 : (instance.getMode() - 1);
        else
            return (instance.getMode() == 3) ? 1 : (instance.getMode() + 1);
    }

    @Override
    public double getObtainingEpCost() {
        return 1500000;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity living, UnlockSkillEvent event) {
        if (instance.getMastery() >= 0 && !instance.isTemporarySkill()) {
            if (living instanceof Player player) {
                SkillUtils.learnSkill(player, ResistanceSkills.CORROSION_NULLIFICATION.get());
                SkillUtils.learnSkill(player, ResistanceSkills.DARKNESS_ATTACK_NULLIFICATION.get());
                SkillUtils.learnSkill(player, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get());
            }
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        boolean active = !tag.getBoolean(PLACENTA_TOGGLE);
        tag.putBoolean(PLACENTA_TOGGLE, active);
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();
        boolean active = !tag.getBoolean(PLACENTA_TOGGLE);
        tag.putBoolean(PLACENTA_TOGGLE, active);
    }

    @Override
    public Component getModeName(int mode) {
        return switch (mode) {
            case 1 -> Component.literal("Dark Miracle").withStyle(ChatFormatting.BLACK);
            case 2 -> Component.literal("Verg Avesta").withStyle(ChatFormatting.DARK_PURPLE);
            case 3 -> Component.literal("Curse to the World").withStyle(ChatFormatting.DARK_RED);
            default -> super.getModeName(mode);
        };
    }

    @SubscribeEvent
    public void onEffectApplied(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (entity.level.isClientSide || !SkillUtils.hasSkill(entity, this)) return;

        MobEffectInstance effect = event.getEffectInstance();
        if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            event.setCanceled(true);

            ManasSkillInstance instance = SkillAPI.getSkillsFrom(player).getSkill(this).orElse(null);

            if (instance != null) {
                CompoundTag tag = instance.getOrCreateTag();
                storeCurse(tag, effect.getEffect());

                tag.putInt(TOTAL_CURSE_COUNT, tag.getInt(TOTAL_CURSE_COUNT) + 1);

                applyInvertedBuff(entity, effect.getEffect());

                if (entity instanceof Player p)
                    p.displayClientMessage(Component.literal("Malice absorbed...").withStyle(ChatFormatting.DARK_RED), true);
            }
        }
    }

    private void storeCurse(CompoundTag tag, MobEffect effect) {
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (id == null) return;

        ListTag list = tag.getList(STORED_CURSES, Tag.TAG_STRING);
        String idStr = id.toString();

        boolean exists = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(idStr)) {
                exists = true;
                break;
            }
        }
        if (!exists) list.add(StringTag.valueOf(idStr));
        tag.put(STORED_CURSES, list);
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level.isClientSide || !(entity instanceof Player user)) return;

        CompoundTag tag = instance.getOrCreateTag();

        ListTag uniqueCurses = tag.getList("StoredCurses", Tag.TAG_STRING);
        int uniqueCount = uniqueCurses.size();

        if (user.tickCount % 40 == 0) {
            updateIncarnationStats(user, uniqueCount);
        }

        if (this.isInSlot(entity)) {
            if (user.tickCount % 10 == 0) {
                double range = 25.0;
                List<LivingEntity> nearbyEntities = user.level.getEntitiesOfClass(LivingEntity.class,
                        user.getBoundingBox().inflate(range), e -> e != user && e.isAlive());

                for (LivingEntity target : nearbyEntities) {
                    List<MobEffectInstance> harmfulEffects = target.getActiveEffects().stream()
                            .filter(eff -> eff.getEffect().getCategory() == MobEffectCategory.HARMFUL)
                            .toList();

                    for (MobEffectInstance effect : harmfulEffects) {
                        user.addEffect(new MobEffectInstance(effect));

                        target.removeEffect(effect.getEffect());

                        if (user.level instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.SQUID_INK, target.getX(), target.getY() + 1, target.getZ(), 5, 0.2, 0.2, 0.2, 0.02);
                        }
                    }
                }
            }
        }
    }

    private void updateIncarnationStats(Player player, int uniqueCount) {
        if (uniqueCount <= 0) return;

        double multiplier = uniqueCount * 0.75;

        AttributeInstance strength = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);

        applyModifier(strength, multiplier);
        applyModifier(armor, multiplier);
        applyModifier(health, multiplier);
        applyModifier(speed, multiplier * 0.1);
    }

    private void applyModifier(AttributeInstance attribute, double amount) {
        if (attribute == null) return;
        attribute.removeModifier(INCARNATION_ID);
        attribute.addTransientModifier(new AttributeModifier(INCARNATION_ID, "Cursed Incarnation Boost", amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (instance.getMode() != 2) return false;

        if (heldTicks < 100) {
            if (heldTicks % 20 == 0) {
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0f, 0.5f);
            }
            return true;
        }

        BlockPos center = entity.blockPosition();
        int radius = 10;
        int height = 8;

        if (entity.level instanceof ServerLevel world) {
            CompoundTag tag = instance.getOrCreateTag();
            ListTag savedBlocks = tag.getList("DomainBackups", Tag.TAG_COMPOUND);

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -1; y <= height; y++) {
                        BlockPos currentPos = center.offset(x, y, z);

                        boolean isWall = Math.abs(x) == radius || Math.abs(z) == radius;
                        boolean isCeiling = y == height;
                        boolean isFloor = y == -1;

                        if (isWall || isCeiling) {
                            if (world.getBlockState(currentPos).isAir()) {
                                saveBlock(world, currentPos, savedBlocks);
                                world.setBlockAndUpdate(currentPos, Blocks.BARRIER.defaultBlockState());
                            }
                        }
                        else if (isFloor) {
                            if (world.getRandom().nextInt(15) == 0) {
                                BlockState state = world.getBlockState(currentPos);
                                if (!state.isAir() && !state.is(Blocks.MUD) && state.getDestroySpeed(world, currentPos) >= 0) {
                                    saveBlock(world, currentPos, savedBlocks);
                                    world.setBlockAndUpdate(currentPos, Blocks.MUD.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
            tag.put("DomainBackups", savedBlocks);

            applyCursesToEnemies(instance, entity, radius);
        }
        return true;
    }

    private void saveBlock(ServerLevel world, BlockPos pos, ListTag savedBlocks) {
        BlockState state = world.getBlockState(pos);
        CompoundTag blockData = new CompoundTag();
        blockData.putLong("pos", pos.asLong());
        blockData.putString("state", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString());
        savedBlocks.add(blockData);
    }

    private void applyCursesToEnemies(ManasSkillInstance instance, LivingEntity user, int radius) {
        ListTag curses = instance.getOrCreateTag().getList("StoredCurses", Tag.TAG_STRING);
        List<LivingEntity> enemies = user.level.getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(radius), e -> e != user);

        for (LivingEntity enemy : enemies) {
            for (int i = 0; i < curses.size(); i++) {
                MobEffect eff = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(curses.getString(i)));
                if (eff != null) enemy.addEffect(new MobEffectInstance(eff, 100, 1));
            }
        }
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks) {
        if (heldTicks < 100) return;

        if (entity.level instanceof ServerLevel world) {
            CompoundTag tag = instance.getOrCreateTag();
            if (tag.contains("DomainBackups")) {
                ListTag savedBlocks = tag.getList("DomainBackups", Tag.TAG_COMPOUND);

                for (int i = 0; i < savedBlocks.size(); i++) {
                    CompoundTag blockData = savedBlocks.getCompound(i);
                    BlockPos pos = BlockPos.of(blockData.getLong("pos"));
                    ResourceLocation blockId = new ResourceLocation(blockData.getString("state"));
                    Block originalBlock = ForgeRegistries.BLOCKS.getValue(blockId);

                    if (originalBlock != null) {
                        BlockState current = world.getBlockState(pos);
                        if (current.is(Blocks.MUD) || current.is(Blocks.BARRIER)) {
                            world.setBlockAndUpdate(pos, originalBlock.defaultBlockState());
                        }
                    }
                }
                tag.remove("DomainBackups");
                world.playSound(null, entity.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 0.5f);
            }
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity) {
        CompoundTag tag = instance.getOrCreateTag();

        if (instance.getMode() == 1) {
            int totalCurses = tag.getInt("TotalCurseCount");

            if (totalCurses >= 20) {
                tag.putInt("TotalCurseCount", totalCurses - 20);

                int currentLvl = tag.getInt("EvilOfHumanityLvl");
                tag.putInt("EvilOfHumanityLvl", currentLvl + 1);

                entity.addEffect(new MobEffectInstance(MythosMobEffects.EVIL_OF_HUMANITY.get(), 1200, currentLvl));

                entity.level.playSound(null, entity.blockPosition(),
                        SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 0.1f);

                if (entity instanceof Player p2) {
                    p2.displayClientMessage(Component.literal("The Evils of Humanity manifest... Level " + (currentLvl + 1))
                            .withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD), true);
                }
            } else {
                if (entity instanceof Player p2) {
                    p2.displayClientMessage(Component.literal("Requires 20 Curses to manifest Dark Miracle.")
                            .withStyle(ChatFormatting.RED), true);
                }
            }
        } else if (instance.getMode() == 3) {
            if (entity instanceof Player player) {
                ItemStack stack = player.getMainHandItem();
                if (!stack.isEmpty()) {
                    if (stack.getEnchantmentLevel(MythosEngravings.VAIN.get()) > 0) {
                        player.displayClientMessage(Component.literal("This item is already engraved with vanity.")
                                .withStyle(ChatFormatting.RED), true);
                        return;
                    }
                    stack.enchant(MythosEngravings.VAIN.get(), 1);

                    player.level.playSound(null, player.blockPosition(),
                            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 0.5f);

                    player.displayClientMessage(Component.literal("The world's vanity has been engraved.")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC), true);
                }
            }
        }
    }

    private void applyInvertedBuff(LivingEntity user, MobEffect badEffect) {
        if (badEffect == MobEffects.POISON || badEffect == MobEffects.WITHER)
            user.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        else if (badEffect == MobEffects.MOVEMENT_SLOWDOWN)
            user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
        else if (badEffect == MobEffects.WEAKNESS)
            user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
        else user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
    }
}