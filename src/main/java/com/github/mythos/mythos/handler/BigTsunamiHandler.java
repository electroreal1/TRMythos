package com.github.mythos.mythos.handler;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class BigTsunamiHandler {
    private final ServerLevel level;
    private final Vec3 origin;
    private final Vec3 look;
    private final double waveRadius, waveHeight, waveWidth;
    private final int particlesPerTick;
    public int ticksLeft;
    private final int totalTicks;

    public BigTsunamiHandler(ServerLevel level, Vec3 origin, Vec3 look, double waveRadius, double waveHeight, double waveWidth, int particlesPerTick, int totalTicks) {
        this.level = level;
        this.origin = origin;
        this.look = look;
        this.waveRadius = waveRadius;
        this.waveHeight = waveHeight;
        this.waveWidth = waveWidth;
        this.particlesPerTick = particlesPerTick;
        this.totalTicks = totalTicks;
        this.ticksLeft = totalTicks;
    }

    public void spawnParticles() {
        double progress = 1.0 - (ticksLeft / (double) totalTicks);
        double sliceDist = waveRadius * progress;

        for (int i = 0; i < particlesPerTick; i++) {
            double angle = (Math.random() - 0.5) * Math.PI * (waveWidth / waveRadius);
            double y = Math.sin((sliceDist / waveRadius) * Math.PI) * waveHeight;

            double offsetX = look.x * sliceDist + Math.cos(angle) * (waveWidth / 2);
            double offsetZ = look.z * sliceDist + Math.sin(angle) * (waveWidth / 2);

            double px = origin.x + offsetX;
            double py = origin.y + y;
            double pz = origin.z + offsetZ;

            double vx = look.x * 0.05;
            double vy = -0.02 * Math.random();
            double vz = look.z * 0.05;

            double choice = Math.random();
            if (choice < 0.6) {
                level.sendParticles(ParticleTypes.SPLASH, px, py, pz, 1, vx, vy, vz, 0.0);
            } else if (choice < 0.85) {
                level.sendParticles(ParticleTypes.CLOUD, px, py + 0.5, pz, 1, vx*0.3, vy*0.3, vz*0.3, 0.02);
            } else {
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, px, py + 0.2, pz, 1, vx*0.2, vy*0.1, vz*0.2, 0.01);
            }
        }
    }

}
