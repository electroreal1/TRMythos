package com.github.mythos.mythos.networking.play2server;

import com.github.lucifel.virtuoso.registry.battlewills.OptimalArts;
import com.github.lucifel.virtuoso.registry.skill.ExtraSkills;
import com.github.lucifel.virtuoso.registry.skill.IntrinsicSkills;
import com.github.lucifel.virtuoso.registry.skill.OptimalSkills;
import com.github.lucifel.virtuoso.registry.skill.UniqueSkills;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.registry.battlewill.UtilityArts;
import com.github.manasmods.tensura.registry.skill.CommonSkills;
import com.github.mythos.mythos.ability.skill.unique.GenesisCoreSkill;
import com.github.mythos.mythos.registry.skill.Skills;
import io.github.Memoires.trmysticism.registry.skill.UltimateSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class RequestGenesisCorePacket {

    private final ResourceLocation skill;

    public RequestGenesisCorePacket(FriendlyByteBuf buf) {
        this.skill = buf.readResourceLocation();
    }

    public RequestGenesisCorePacket(ResourceLocation skill) {
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
        Optional<ManasSkillInstance> optionalRoot = storage.getSkill((ManasSkill) Skills.GENESIS_CORE.get());
        if (!optionalRoot.isEmpty()) {
            ManasSkillInstance root = (ManasSkillInstance)optionalRoot.get();
            if (!root.onCoolDown()) {
                CompoundTag tag = root.getOrCreateTag();
                new ResourceLocation(tag.getString("optimal_skill"));
                ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().getValue(this.skill);
                if (manasSkill == OptimalSkills.OPTIMAL_SUSANOO.get() && SkillUtils.hasSkill(player, (ManasSkill) UltimateSkills.SUSANOO.get())) {
                    storage.forgetSkill((ManasSkill)UltimateSkills.SUSANOO.get());
                }

                if (manasSkill == OptimalSkills.YEHOSHUA.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.lucifel.virtuoso.registry.skill.UltimateSkills.YESHUA.get())) {
                    storage.forgetSkill((ManasSkill)com.github.lucifel.virtuoso.registry.skill.UltimateSkills.YESHUA.get());
                }

                if (manasSkill == OptimalSkills.HAMIEL.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.lucifel.virtuoso.registry.skill.UltimateSkills.HANIEL.get())) {
                    storage.forgetSkill((ManasSkill)com.github.lucifel.virtuoso.registry.skill.UltimateSkills.HANIEL.get());
                }

                if (manasSkill == OptimalSkills.OPTIMAL_COOK.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.manasmods.tensura.registry.skill.UniqueSkills.COOK.get())) {
                    storage.forgetSkill((ManasSkill)com.github.manasmods.tensura.registry.skill.UniqueSkills.COOK.get());
                }

                if (manasSkill == OptimalSkills.EVER_DISTANT.get() && SkillUtils.hasSkill(player, (ManasSkill) IntrinsicSkills.AVALON.get())) {
                    storage.forgetSkill((ManasSkill)IntrinsicSkills.AVALON.get());
                }

                if (manasSkill == OptimalSkills.UTOPIA.get() && SkillUtils.hasSkill(player, (ManasSkill)IntrinsicSkills.IDEAL.get())) {
                    storage.forgetSkill((ManasSkill)IntrinsicSkills.IDEAL.get());
                }

                if (manasSkill == OptimalSkills.PRESSURIZER.get() && SkillUtils.hasSkill(player, (ManasSkill) ExtraSkills.CONCENTRATOR.get())) {
                    storage.forgetSkill((ManasSkill)ExtraSkills.CONCENTRATOR.get());
                }

                if (manasSkill == com.github.manasmods.tensura.registry.skill.ExtraSkills.ULTRASPEED_REGENERATION.get() && SkillUtils.hasSkill(player, (ManasSkill) CommonSkills.SELF_REGENERATION.get())) {
                    storage.forgetSkill((ManasSkill)CommonSkills.SELF_REGENERATION.get());
                }

                if (manasSkill == com.github.manasmods.tensura.registry.skill.ExtraSkills.INFINITE_REGENERATION.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.manasmods.tensura.registry.skill.ExtraSkills.ULTRASPEED_REGENERATION.get())) {
                    storage.forgetSkill((ManasSkill)com.github.manasmods.tensura.registry.skill.ExtraSkills.ULTRASPEED_REGENERATION.get());
                }

                if (manasSkill == OptimalSkills.OPTIMAL_REGENERATION.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.manasmods.tensura.registry.skill.ExtraSkills.INFINITE_REGENERATION.get())) {
                    storage.forgetSkill((ManasSkill)com.github.manasmods.tensura.registry.skill.ExtraSkills.INFINITE_REGENERATION.get());
                }

                if (manasSkill == UniqueSkills.GLORIOUS.get() && SkillUtils.hasSkill(player, (ManasSkill)OptimalSkills.OPTIMAL_REGENERATION.get())) {
                    storage.forgetSkill((ManasSkill)OptimalSkills.OPTIMAL_REGENERATION.get());
                }

                if (manasSkill == UniqueSkills.ROOT.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.manasmods.tensura.registry.skill.UniqueSkills.CREATOR.get())) {
                    storage.forgetSkill((ManasSkill)com.github.manasmods.tensura.registry.skill.UniqueSkills.CREATOR.get());
                }

                if (manasSkill == OptimalSkills.OPTIMAL_GUARDIAN.get() && SkillUtils.hasSkill(player, (ManasSkill)com.github.manasmods.tensura.registry.skill.UniqueSkills.GUARDIAN.get())) {
                    storage.forgetSkill((ManasSkill)com.github.manasmods.tensura.registry.skill.UniqueSkills.GUARDIAN.get());
                }

                if (manasSkill == OptimalArts.OPTIMAL_MOVE.get() && SkillUtils.hasSkill(player, (ManasSkill) UtilityArts.INSTANT_MOVE.get())) {
                    storage.forgetSkill((ManasSkill)UtilityArts.INSTANT_MOVE.get());
                }

                if (manasSkill != null) {
                    TensuraSkillInstance skillInstance = new TensuraSkillInstance(manasSkill);
                    skillInstance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                    if (storage.learnSkill(skillInstance)) {
                        player.displayClientMessage(Component.translatable("tensura.skill.acquire", new Object[]{manasSkill.getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), false);
                        player.getLevel().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                        TensuraSkillCapability.getFrom(player).ifPresent((cap) -> {
                            if (cap.getSkillInSlot(0) == null) {
                                cap.setInstanceInSlot(skillInstance, 0);
                            } else if (cap.getSkillInSlot(1) == null) {
                                cap.setInstanceInSlot(skillInstance, 1);
                            } else if (cap.getSkillInSlot(2) == null) {
                                cap.setInstanceInSlot(skillInstance, 2);
                            }

                            TensuraSkillCapability.sync(player);
                        });
                    }

                    ((GenesisCoreSkill)Skills.GENESIS_CORE.get()).addMasteryPoint(root, player, 5 + SkillUtils.getBonusMasteryPoint(root, player, 5));
                    root.markDirty();
                    storage.syncChanges();
                    player.closeContainer();
                }
            }
        }

    }

}
