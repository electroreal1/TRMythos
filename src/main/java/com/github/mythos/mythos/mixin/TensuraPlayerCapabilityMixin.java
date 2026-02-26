//package com.github.mythos.mythos.mixin;
//
//import com.github.manasmods.tensura.capability.ep.ITensuraEPCapability;
//import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
//import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
//import com.github.manasmods.tensura.handler.CapabilityHandler;
//import com.github.manasmods.tensura.race.Race;
//import com.github.mythos.mythos.race.CustomDefaultRace;
//import com.github.mythos.mythos.race.ICustomAlignmentAccessor;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import static com.github.manasmods.tensura.capability.ep.TensuraEPCapability.getFrom;
//import static com.github.manasmods.tensura.capability.race.TensuraPlayerCapability.CAPABILITY;
//import static com.github.manasmods.tensura.capability.race.TensuraPlayerCapability.sync;
//
//@Mixin(TensuraPlayerCapability.class)
//public class TensuraPlayerCapabilityMixin implements ICustomAlignmentAccessor {
//
//    @Shadow private Race race;
//    @Shadow private double baseAura;
//    @Shadow private double baseMagicule;
//
//    @Inject(method = "setRace", at = @At("HEAD"), cancellable = true, remap = false)
//    private void mythos$onSetRace(LivingEntity entity, Race newRace, boolean resetStat, CallbackInfo ci) {
//        if (newRace instanceof CustomDefaultRace customRace && customRace.isCustomAlignment()) {
//            this.race = newRace;
//
//            if (resetStat) {
//                this.baseAura = newRace.getMaxBaseAura();
//                this.baseMagicule = newRace.getMaxBaseMagicule();
//
//                getFrom(entity).ifPresent((cap) -> {
//                    cap.setMajin(true);
//                    TensuraEPCapability.sync(entity);
//                });
//            }
//
//            if (entity instanceof ServerPlayer player) {
//                sync(player);
//                TensuraEPCapability.updateEP(entity);
//            }
//
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "resetEverything", at = @At("HEAD"), remap = false)
//    private static void mythos$onReset(Player player, CallbackInfo ci) {
//        player.getCapability(TensuraPlayerCapability.CAPABILITY).ifPresent(cap -> {
//            if (cap instanceof ICustomAlignmentAccessor accessor) {
//                accessor.setCustomAlignment(false);
//            }
//        });
//    }
//
//    public boolean customAlignment = false;
//
//    public CompoundTag serializeNBT() {
//        CompoundTag tag = new CompoundTag();
//        tag.putBoolean("custom_alignment", this.customAlignment);
//        return tag;
//    }
//
//    public void deserializeNBT(CompoundTag tag) {
//        this.customAlignment = tag.getBoolean("custom_alignment");
//    }
//
//    private boolean isCustomAlignment(LivingEntity entity) {
//        ITensuraEPCapability capability = (ITensuraEPCapability) CapabilityHandler.getCapability(entity, CAPABILITY);
//        return capability != null && this.customAlignment;
//    }
//
//    private static void setCustomAlignment(LivingEntity pLivingEntity, boolean value) {
//        getFrom(pLivingEntity).ifPresent((cap) -> {
//            cap.setMajin(value);
//        });
//        sync((Player) pLivingEntity);
//    }
//
//    @Override
//    public void setCustomAlignment(Boolean value) {
//        this.customAlignment = value;
//    }
//
//    public boolean isCustomAlignment() {
//        return customAlignment;
//    }
//
//
//}
