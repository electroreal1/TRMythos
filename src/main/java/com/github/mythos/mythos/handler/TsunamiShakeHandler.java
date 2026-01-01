package com.github.mythos.mythos.handler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TsunamiShakeHandler {
    private static int ticksLeft = 0;
    private static int totalTicks = 0;
    private static float strength = 0;

    public static void start(int duration, float power) {
        ticksLeft = duration;
        totalTicks = duration;
        strength = power;
    }

    public static void apply(ViewportEvent.ComputeCameraAngles event) {
        if (ticksLeft <= 0) return;

        float progress = 1f - (ticksLeft / (float) totalTicks);
        float intensity = strength * (float) Math.sin(progress * Math.PI);

        // Tsunami-style offsets
        float yawOffset   = (float)(Math.sin(ticksLeft * 0.12) * intensity * 2.5f);
        float pitchOffset = (float)(Math.abs(Math.sin(ticksLeft * 0.08)) * intensity * 3.5f);
        float rollOffset  = (float)(Math.sin(ticksLeft * 0.05) * intensity * 2.0f);

        event.setYaw(event.getYaw() + yawOffset);
        event.setPitch(event.getPitch() - pitchOffset); // downward weight
        event.setRoll(event.getRoll() + rollOffset);

        ticksLeft--;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onCameraUpdate(ViewportEvent.ComputeCameraAngles event) {
        TsunamiShakeHandler.apply(event);
    }
}
