package com.github.mythos.mythos.handler;

import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.Mythos;
import com.github.mythos.mythos.registry.MythosMobEffects;
import com.github.mythos.mythos.registry.skill.Magics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

@Mod.EventBusSubscriber(modid = Mythos.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalEffectHandler {
    public static boolean isGreatSilenceActive = false;
    private static final Random RANDOM = new Random();
    public static float shakeIntensity = 0f;
    private static int soundLoopTimer = 0;
    private static final ResourceLocation STRONG_SHADER = new ResourceLocation("minecraft", "shaders/post/blobs.json");
    private static int lastActiveAmplifier = -1;
    private static final Map<UUID, Vec3> FORCED_PATHS = new HashMap<>();
    private static final Map<UUID, UUID> OVERLOAD_TARGETS = new HashMap<>();

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakeIntensity > 0) {
            event.setPitch(event.getPitch() + (RANDOM.nextFloat() - 0.5f) * shakeIntensity);
            event.setYaw(event.getYaw() + (RANDOM.nextFloat() - 0.5f) * shakeIntensity);
            event.setRoll(event.getRoll() + (RANDOM.nextFloat() - 0.5f) * (shakeIntensity * 1.5f));
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(MythosMobEffects.ATROPHY.get());

        if (effect != null) {
            PoseStack stack = event.getPoseStack();
            int amp = effect.getAmplifier();

            stack.pushPose();
            if (amp == 0) {
                stack.scale(1.0f, 1.0f, 0.005f);
            } else {
                stack.scale(0.005f, 1.0f, 0.005f);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity().hasEffect(MythosMobEffects.ATROPHY.get())) {
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public static void onClientChat(ClientChatReceivedEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            String original = event.getMessage().getString();
            event.setMessage(Component.literal("§7§k" + original).withStyle(ChatFormatting.GRAY));
        }

    }

    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            if (event.getSound() != null) {
                String path = event.getSound().getLocation().getPath();
                boolean allowed = path.contains("conduit") || path.contains("warden") || path.contains("beacon");
                if (!allowed) event.setSound(null);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        if (isGreatSilenceActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            long time = mc.level.getGameTime();
            float alpha = (float) (Math.sin(time * 0.4f) * 0.4f + 0.5f);

            if (RANDOM.nextFloat() < 0.05f) alpha = 0.1f;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            assert mc.level != null;
            float timer = mc.level.getGameTime() + mc.getFrameTime();
            float alpha = (float) (Math.sin(timer * 0.5f) * 0.5f + 0.5f);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha * 0.5f);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            if (soundLoopTimer-- <= 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
                mc.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 0.7f, 0.2f);
                soundLoopTimer = 40;
            }
        }

        MobEffectInstance dysphoria = mc.player.getEffect(MythosMobEffects.SPATIAL_DYSPHORIA.get());
        if (dysphoria != null) {
            shakeIntensity = 0.1f * (dysphoria.getAmplifier() + 1);
        } else {
            shakeIntensity = Math.max(0, shakeIntensity - 0.01f);
        }


        if (mc.player.hasEffect(MythosMobEffects.SUNRISE.get())) {
            loadShader(mc, new ResourceLocation("shaders/post/phosphor.json"));
        } else if (mc.player.hasEffect(MythosMobEffects.SUNSET.get())) {
            loadShader(mc, new ResourceLocation("shaders/post/desaturate.json"));
        } else {
            if (mc.gameRenderer.currentEffect() != null && isHaliShader(Objects.requireNonNull(mc.gameRenderer.currentEffect()).getName())) {
                mc.gameRenderer.shutdownEffect();
            }
        }

        if (mc.player != null && GlobalEffectHandler.isGreatSilenceActive) {
            assert mc.level != null;
            if (mc.level.getGameTime() % 40 == 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
            }

            if (mc.level.getGameTime() % 30 == 0) {
                mc.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 0.6f, 0.2f);
            }
        }


        assert mc.player != null;
        MobEffectInstance effect = mc.player.getEffect(MythosMobEffects.SPATIAL_DYSPHORIA.get());
        boolean hasEffect = effect != null;
        int currentAmplifier = hasEffect ? effect.getAmplifier() : -1;

        boolean currentlyHasShader = mc.gameRenderer.currentEffect() != null;
        ResourceLocation activeShaderId = currentlyHasShader ? ResourceLocation.tryParse(Objects.requireNonNull(mc.gameRenderer.currentEffect()).getName()) : null;
        ResourceLocation targetShader = STRONG_SHADER;

        if (hasEffect) {
            if (currentAmplifier != lastActiveAmplifier || activeShaderId == null || !activeShaderId.equals(targetShader)) {
                mc.gameRenderer.loadEffect(targetShader);
                lastActiveAmplifier = currentAmplifier;
            }
        } else if (currentlyHasShader && activeShaderId != null && (activeShaderId.equals(STRONG_SHADER))) {
            mc.gameRenderer.shutdownEffect();
            lastActiveAmplifier = -1;
        }

        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {

            if (soundLoopTimer <= 0) {
                mc.player.playSound(SoundEvents.CONDUIT_AMBIENT, 1.0f, 0.1f);
                mc.player.playSound(SoundEvents.BEACON_AMBIENT, 0.4f, 0.2f);
                soundLoopTimer = 40;
            }
            soundLoopTimer--;
        } else {
            soundLoopTimer = 0;
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (isGreatSilenceActive) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            float time = (mc.level.getGameTime() + Minecraft.getInstance().getFrameTime()) * 0.01f;
            event.setRed((float) Math.abs(Math.sin(time)) * 0.1f);
            event.setGreen(0.01f);
            event.setBlue((float) Math.abs(Math.cos(time)) * 0.2f);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            assert mc.level != null;
            float time = (mc.level.getGameTime() + mc.getFrameTime()) * 0.01f;
            event.setRed((float)Math.abs(Math.sin(time)) * 0.2f);
            event.setGreen(0.05f);
            event.setBlue((float)Math.abs(Math.cos(time)) * 0.2f);
        }
    }

    private static void loadShader(Minecraft mc, ResourceLocation loc) {
        if (mc.gameRenderer.currentEffect() == null || !Objects.requireNonNull(mc.gameRenderer.currentEffect()).getName().equals(loc.toString())) {
            mc.gameRenderer.loadEffect(loc);
        }
    }

    private static boolean isHaliShader(String name) {
        return name.contains("phosphor") || name.contains("desaturate");
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        var effect = entity.getEffect(MythosMobEffects.ATROPHY.get());
        if (effect != null) {
            event.getPoseStack().pushPose();
            int amp = effect.getAmplifier();

            if (amp == 0) {
                event.getPoseStack().scale(0.005f, 10.0f, 0.005f);
                event.getPoseStack().translate(0.005f, 10.0f, 0.005f);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderHotbar(RenderGuiOverlayEvent.Pre event) {
        if (GlobalEffectHandler.isGreatSilenceActive) {
            assert Minecraft.getInstance().level != null;
            long time = Minecraft.getInstance().level.getGameTime();
            float alpha = (time % 5 == 0) ? 0.2f : 1.0f;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderHotbarPost(RenderGuiOverlayEvent.Post event) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onFogDensity(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(MythosMobEffects.GREAT_SILENCE.get())) {
            event.setNearPlaneDistance(0f);
            event.setFarPlaneDistance(10f);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFOVUpdate(ComputeFovModifierEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MobEffectInstance effect = mc.player.getEffect(MythosMobEffects.SPATIAL_DYSPHORIA.get());

        if (effect != null) {
            int amp = effect.getAmplifier();

            float intensity = 1.0f + (amp * 0.5f);
            float speed = 0.1f + (amp * 0.05f);

            float timer = mc.player.tickCount + mc.getFrameTime();

            float wave = (float) Math.sin(timer * speed) * (0.2f * intensity);

            float jitterLimit = 0.05f * intensity;
            float jitter = (mc.player.getRandom().nextFloat() - 0.5f) * jitterLimit;

            event.setNewFovModifier(event.getFovModifier() + wave + jitter);
        }
    }

    @SubscribeEvent
    public static void onQuantumTick(LivingEvent.LivingTickEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level.isClientSide) return;

        if (victim.hasEffect(MythosMobEffects.SCHRODINGERS_LABYRINTH.get())) {
            CompoundTag tag = victim.getPersistentData();

            if (tag.contains("LabyrinthAnchor")) {
                BlockPos anchor = BlockPos.of(tag.getLong("LabyrinthAnchor"));
                double distanceSq = victim.blockPosition().distSqr(anchor);

                if (distanceSq >36) {
                    if (victim.getRandom().nextFloat() < 0.4f) {
                        performQuantumCollapse(victim, anchor);
                    }
                }
            }
        }
    }

    private static void performQuantumCollapse(LivingEntity victim, BlockPos anchor) {
        if (victim.level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.REVERSE_PORTAL, victim.getX(), victim.getY() + 1, victim.getZ(),
                    25, 0.2, 0.5, 0.2, 0.1);
        }

        victim.teleportTo(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5);
        victim.level.playSound(null, anchor, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.8f);
    }

    public static void startForcedPath(Player player, UUID targetUUID, Vec3 destination) {
        FORCED_PATHS.put(targetUUID, destination);
        player.level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0f, 2.0f);
    }

    public static void toggleOverload(Player player, LivingEntity target) {
        if (OVERLOAD_TARGETS.containsKey(player.getUUID())) {
            OVERLOAD_TARGETS.remove(player.getUUID());
        } else {
            OVERLOAD_TARGETS.put(player.getUUID(), target.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        FORCED_PATHS.entrySet().removeIf(entry -> {
            LivingEntity victim = findEntity(entry.getKey());
            if (victim == null || !victim.isAlive() || victim.distanceToSqr(entry.getValue()) < 1.0) return true;

            Vec3 dir = entry.getValue().subtract(victim.position()).normalize().scale(0.6);
            victim.setDeltaMovement(dir.x, victim.getDeltaMovement().y, dir.z);
            victim.hurtMarked = true;
            return false;
        });

        OVERLOAD_TARGETS.forEach((casterUUID, victimUUID) -> {
            Player caster = findCaster(casterUUID);
            LivingEntity victim = findEntity(victimUUID);

            if (caster != null && victim != null) {
                caster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 10, false, false));
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 10, false, false));

                int ticks = victim.getPersistentData().getInt("LaplaceTicks") + 1;
                victim.getPersistentData().putInt("LaplaceTicks", ticks);

                if (ticks % 20 == 0) {
                    float damage = (float) Math.pow(2, ticks / 20.0);
                    victim.hurt(TensuraDamageSources.holyDamage(caster), damage);
                }
            }
        });
    }

    private static LivingEntity findEntity(UUID uuid) {
        for (var level : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) return (LivingEntity) entity;
        }
        return null;
    }

    private static ServerPlayer findCaster(UUID uuid) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        var player = Minecraft.getInstance().player;
        if (player == null || !SkillUtils.hasSkill(player, Magics.LAPLACES_DEMON.get())) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20), e -> e != player).forEach(target -> {
            renderPredictionLine(target, poseStack, camera);
        });
    }

    private static void renderPredictionLine(LivingEntity target, PoseStack poseStack, Vec3 camera) {
        Vec3 pos = target.getPosition(Minecraft.getInstance().getFrameTime());
        Vec3 velocity = target.getDeltaMovement();
        Vec3 predicted = pos.add(velocity.x * 40, velocity.y * 40, velocity.z * 40);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        buffer.vertex(poseStack.last().pose(), (float)pos.x, (float)pos.y + 1.0f, (float)pos.z).color(0, 255, 255, 255).endVertex();
        buffer.vertex(poseStack.last().pose(), (float)predicted.x, (float)predicted.y + 1.0f, (float)predicted.z).color(0, 255, 255, 50).endVertex();

        tesselator.end();
        poseStack.popPose();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}