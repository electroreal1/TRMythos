package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.mythos.mythos.registry.MythosDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "trmythos")
public class SanctuaryDimensionHandler {
    private static final Map<BlockPos, UUID> placedBlocks = new HashMap<>();

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Level level = Minecraft.getInstance().level;
        if (level != null && level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            event.setBlue(1);
            event.setRed(1);
            event.setGreen(1);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Level level = Minecraft.getInstance().level;
        if (level != null && level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            event.setNearPlaneDistance(0.0F);
            event.setFarPlaneDistance(32.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side.isClient() || event.phase == TickEvent.Phase.START) return;

        ServerLevel level = (ServerLevel) event.level;
        if (level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {

            AABB searchArea = AABB.ofSize(new Vec3(0, 65, 0), 5000, 2560, 5000);

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchArea, (entity) -> true);

            for (LivingEntity entity : entities) {
                if (!isSanctuaryOwner(entity)) {

                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, false));

                    Vec3 currentMove = entity.getDeltaMovement();
                    if (currentMove.y > 0) {
                        entity.setDeltaMovement(currentMove.x, -0.1, currentMove.z);
                    }

                    handleBoundary(entity);
                    applySuppression(entity);
                }
            }
        }
    }

    public static void handleBoundary(LivingEntity entity) {
        double limit = 150.0;
        if (entity.position().distanceTo(new Vec3(0, 65, 0)) > limit) {
            if (!entity.getPersistentData().getBoolean("IsSanctuaryOwner")) {
                Vec3 center = new Vec3(0, 65, 0);
                entity.teleportTo(center.x, center.y, center.z);

                if (entity instanceof Player p) {
                    p.displayClientMessage(Component.literal("There is no escape from this Sanctuary.")
                            .withStyle(ChatFormatting.ITALIC), true);
                }
            }
        }
    }

    private static boolean isSanctuaryOwner(LivingEntity entity) {
        return entity.getPersistentData().getBoolean("IsSanctuaryOwner");
    }

    private static void applySuppression(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.OPPRESSION.get()));
        entity.addEffect(new MobEffectInstance(TensuraMobEffects.SPATIAL_BLOCKADE.get()));
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;

        if (((Level)event.getLevel()).dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            if (event.getEntity() instanceof Player player) {
                placedBlocks.put(event.getPos(), player.getUUID());
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        if (!level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) return;

        Player player = event.getPlayer();
        BlockPos pos = event.getPos();

        boolean isOwner = isSanctuaryOwner(player);

        UUID placerUUID = placedBlocks.get(pos);
        boolean isPlacer = placerUUID != null && placerUUID.equals(player.getUUID());

        if (!isOwner && !isPlacer) {
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("This world does not bend to your touch.")
                    .withStyle(ChatFormatting.GRAY), true);
        } else {
            placedBlocks.remove(pos);
        }
    }

    @SubscribeEvent
    public static void onTeleport(EntityTeleportEvent event) {
        if (event.getEntity().level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {

            event.setCanceled(true);

            if (event.getEntity() instanceof LivingEntity living) {
                living.level.playSound(null, living.getX(), living.getY(), living.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 0.5f);
            }
        }
    }

    @SubscribeEvent
    public static void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        if (event.getPlayer().level.dimension().equals(MythosDimensions.SANCTUARY_KEY)) {
            event.setCanceled(true);
        }
    }
}
