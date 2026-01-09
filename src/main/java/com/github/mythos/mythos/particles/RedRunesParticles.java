package com.github.mythos.mythos.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class RedRunesParticles extends TextureSheetParticle {
    private final double xStart, yStart, zStart;
    private final SpriteSet sprites;

    protected RedRunesParticles(ClientLevel level, double x, double y, double z,
                                double xTarget, double yTarget, double zTarget, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.xStart = x;
        this.yStart = y;
        this.zStart = z;
        this.xd = xTarget;
        this.yd = yTarget;
        this.zd = zTarget;

        this.lifetime = 30;
        this.quadSize = 0.3f;
        this.hasPhysics = false;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
            float progress = (float)this.age / (float)this.lifetime;
            this.x = this.xStart + (this.xd - this.xStart) * progress;
            this.y = this.yStart + (this.yd - this.yStart) * progress;
            this.z = this.zStart + (this.zd - this.zStart) * progress;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new RedRunesParticles(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}