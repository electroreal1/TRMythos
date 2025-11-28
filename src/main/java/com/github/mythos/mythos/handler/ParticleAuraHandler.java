package com.github.mythos.mythos.handler;

import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.mythos.mythos.ability.confluence.skill.unique.ConfluenceUniques;
import com.github.mythos.mythos.registry.skill.FusedSkills;
import com.github.mythos.mythos.registry.skill.Skills;
import com.mojang.math.Vector3f;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;
import java.util.function.Consumer;

public class ParticleAuraHandler {

    private static double rotation = 0;
    private static int tickCounter = 0;
    private static int delay = 0;

    private static void spawnAuraIfHasSkill(Player player, ServerLevel server, Skill skill, Consumer<Vec3> auraSpawner) {
        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        if (storage.getSkill(skill).isPresent()) {
            Vec3 playerPos = player.position().add(0, player.getEyeHeight() * 0.5, 0);
            auraSpawner.accept(playerPos);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level;
//        if (!(level instanceof ServerLevel server)) return;
        if (!(player instanceof LocalPlayer)) return;
        ServerLevel server = Objects.requireNonNull(level.getServer()).overworld();

        SkillStorage storage = SkillAPI.getSkillsFrom(player);
        tickCounter++;
        if (tickCounter %2 != 0) return;

        spawnAuraIfHasSkill(player, server, Skills.DEMONOLOGIST.get(), pos -> spawnDemonologistAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.PURITY_SKILL.get(), pos -> spawnPurityAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.CARNAGE.get(), pos -> spawnCarnageAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.INDRA.get(), pos -> spawnIndraAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.ARES.get(), pos -> spawnAresAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.PROFANITY.get(), pos -> spawnProfanityAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.CHILD_OF_THE_PLANE.get(), pos -> spawnChildOfThePlaneAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.NASCENT_DAO.get(), pos -> spawnNascentDaoAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.HEAVENS_WRATH.get(), pos -> spawnHeavensWrathAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.ZEPHYROS.get(), pos -> spawnZephyrosAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.AWAKENED_DAO.get(), pos -> spawnAwakenedDaoAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.TRUE_DAO.get(), pos -> spawnTrueDaoAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.ORIGIN_DAO.get(), pos -> spawnOriginDaoAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, Skills.BLOODSUCKER.get(), pos -> spawnBloodSuckerAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, FusedSkills.PARANOIA.get(), pos -> spawnParanoiaAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, ConfluenceUniques.SPOREBLOOD.get(), pos -> spawnSporebloodAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, ConfluenceUniques.CATHARSIS.get(), pos -> spawnCatharsisAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, ConfluenceUniques.GRAM.get(), pos -> spawnGramAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, ConfluenceUniques.FRAGARACH.get(), pos -> spawnFragarachAura(player, server, pos));
        spawnAuraIfHasSkill(player, server, ConfluenceUniques.EXCALIBUR.get(), pos -> spawnExcaliburAura(player, server, pos));

        rotation += 0.03;
        if (rotation > 2 * Math.PI) rotation -= 2 * Math.PI;
    }

    private static void spawnDemonologistAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        double yOffset = 2.0;
        float size = 0.8f;

        double[][] points = new double[5][2];
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2;
            points[i][0] = Math.cos(angle);
            points[i][1] = Math.sin(angle);
        }

        int[] order = {0, 2, 4, 1, 3, 0};

        for (int i = 0; i < order.length - 1; i++) {
            double[] start = points[order[i]];
            double[] end = points[order[i + 1]];
            int segments = 10;
            for (int s = 0; s <= segments; s++) {
                double t = s / (double) segments;
                double px = player.getX() + ((1 - t) * start[0] + t * end[0]) * 1.5;
                double pz = player.getZ() + ((1 - t) * start[1] + t * end[1]) * 1.5;
                double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.1;
                if (rand.nextDouble() < 0.3) continue;
                Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(1f, 0f, 0f) : new Vector3f(0.6f, 0f, 0f);
                server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnPurityAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int points = 30;
        double radius = 1.5;
        double yOffset = 1.8;
        for (int i = 0; i < points; i++) {
            if (rand.nextDouble() > 0.4) continue;
            double angle = i * 2 * Math.PI / points + rotation;
            double px = player.getX() - Math.cos(angle) * radius;
            double pz = player.getZ() - Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.2;
            float size = 1f;
            Vector3f color;
            double r = rand.nextDouble();
            if (r < 0.5) color = new Vector3f(1f, 0.9f, 0f);
            else if (r < 0.8) color = new Vector3f(1f, 0.6f, 0f);
            else if (r < 0.95) color = new Vector3f(1f, 0.3f, 0f);
            else color = new Vector3f(1f, 1f, 1f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnCarnageAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int tendrils = 6 + rand.nextInt(4);
        int segments = 6;
        double maxHeight = 1.5;
        double baseRadius = 0.5;

        for (int i = 0; i < tendrils; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            double startX = player.getX() + dx * (0.2 + rand.nextDouble() * 0.3);
            double startZ = player.getZ() + dz * (0.2 + rand.nextDouble() * 0.3);
            for (int j = 1; j <= segments; j++) {
                double t = j / (double) segments;
                double px = startX + Math.sin(rotation + j) * 0.05;
                double pz = startZ + Math.cos(rotation + j) * 0.05;
                double py = player.getY() + t * maxHeight + (rand.nextDouble() - 0.5) * 0.1;
                if (rand.nextDouble() < 0.3) continue;
                Vector3f color;
                double r = rand.nextDouble();
                if (r < 0.5) color = new Vector3f(0.6f, 0f, 0f);
                else if (r < 0.85) color = new Vector3f(0.8f, 0f, 0f);
                else color = new Vector3f(0.4f, 0f, 0f);
                server.sendParticles(new DustParticleOptions(color, 0.8f + rand.nextFloat() * 0.2f), px, py, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnIndraAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int cloudParticles = 20;
        double yOffset = 2.2;
        double behindOffset = 0.5;

        for (int i = 0; i < cloudParticles; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.4;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius - behindOffset;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.2;

            if (rand.nextDouble() < 0.2) {

                int segments = 3 + rand.nextInt(3);
                for (int j = 0; j < segments; j++) {
                    double lx = px + (rand.nextDouble() - 0.5) * 0.2;
                    double lz = pz + (rand.nextDouble() - 0.5) * 0.2;
                    double ly = py - j * 0.5;
                    server.sendParticles(new DustParticleOptions(new Vector3f(0.8f, 0.8f, 1f), 0.6f), lx, ly, lz, 1, 0, 0, 0, 0);
                }
            } else {

                float size = 0.5f + rand.nextFloat() * 0.3f;
                Vector3f color = new Vector3f(0.5f, 0.5f, 0.5f);
                server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnAresAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int points = 16;
        double radius = 1.0;
        double yOffset = 1.2;

        for (int i = 0; i < points; i++) {
            double angle = i * 2 * Math.PI / points + rotation;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 3 + i) * 0.2;
            float size = 0.7f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(1f, 0.2f + rand.nextFloat() * 0.3f, 0f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }

        int bursts = 6;
        for (int i = 0; i < bursts; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 0.5 + rand.nextDouble();
            double px = player.getX() + Math.cos(angle) * distance;
            double pz = player.getZ() + Math.sin(angle) * distance;
            double py = player.getY() + yOffset + rand.nextDouble() * 0.5;
            float size = 0.8f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(1f, 0.1f + rand.nextFloat() * 0.3f, 0f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnProfanityAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 5 != 0) return;

        RandomSource rand = player.level.random;
        int clusters = 8;
        double yBase = 1.2;

        for (int i = 0; i < clusters; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yBase + (rand.nextDouble() - 0.5) * 0.5;

            int strokes = 2 + rand.nextInt(3);
            for (int s = 0; s < strokes; s++) {
                double offsetX = (rand.nextDouble() - 0.5) * 0.15;
                double offsetY = (rand.nextDouble() - 0.5) * 0.3;
                double offsetZ = (rand.nextDouble() - 0.5) * 0.15;

                float size = 0.6f + rand.nextFloat() * 0.3f;
                Vector3f color;
                double r = rand.nextDouble();
                if (r < 0.4) color = new Vector3f(0.5f, 0f, 0.5f);
                else if (r < 0.7) color = new Vector3f(0.6f, 0f, 0.8f);
                else color = new Vector3f(0.3f, 0.6f, 0.2f);

                server.sendParticles(new DustParticleOptions(color, size), px + offsetX, py + offsetY, pz + offsetZ, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnChildOfThePlaneAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 5 != 0) return;

        RandomSource rand = player.level.random;
        int portals = 10;
        double yBase = 1.5;

        for (int i = 0; i < portals; i++) {
            double angle = i * 2 * Math.PI / portals + rotation;
            double radius = 0.5 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yBase + Math.sin(rotation * 2 + i) * 0.2;

            double vx = (rand.nextDouble() - 0.5) * 0.05;
            double vy = (rand.nextDouble() - 0.5) * 0.05;
            double vz = (rand.nextDouble() - 0.5) * 0.05;

            server.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.PORTAL, px, py, pz, 1, vx, vy, vz, 0.0);

            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            float size = 0.6f + rand.nextFloat() * 0.3f;

            server.sendParticles(
                    new DustParticleOptions(new Vector3f(r, g, b), size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnNascentDaoAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 4 != 0) return;

        RandomSource rand = player.level.random;
        int particles = 25;
        double maxRadius = 3.0;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = rand.nextDouble() * maxRadius;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5);

            double dx = (player.getX() - px) * 0.05;
            double dz = (player.getZ() - pz) * 0.05;
            double dy = (player.getY() + yOffset - py) * 0.05;

            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(0.6f + rand.nextFloat() * 0.4f, 1f, 0.3f);

            server.sendParticles(
                    new DustParticleOptions(color, size),
                    px, py, pz,
                    1, dx, dy, dz, 0.01
            );
        }
    }

    private static void spawnHeavensWrathAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 5 != 0) return;

        RandomSource rand = player.level.random;
        int arcs = 6;
        double maxDistance = 3.0;
        double yOffset = 1.5;

        for (int i = 0; i < arcs; i++) {
            double targetX = player.getX() + (rand.nextDouble() - 0.5) * maxDistance * 2;
            double targetZ = player.getZ() + (rand.nextDouble() - 0.5) * maxDistance * 2;
            double targetY = player.getY() + yOffset + (rand.nextDouble() - 0.5);

            double px = player.getX();
            double py = player.getY() + yOffset;
            double pz = player.getZ();

            int segments = 5 + rand.nextInt(3);
            for (int s = 0; s < segments; s++) {
                double t = s / (double) segments;
                double dx = px + (targetX - px) * t + (rand.nextDouble() - 0.5) * 0.2;
                double dy = py + (targetY - py) * t + (rand.nextDouble() - 0.5) * 0.2;
                double dz = pz + (targetZ - pz) * t + (rand.nextDouble() - 0.5) * 0.2;

                float size = 0.8f + rand.nextFloat() * 0.2f;
                Vector3f color = new Vector3f(1f, 1f, 0.7f);
                server.sendParticles(new DustParticleOptions(color, size), dx, dy, dz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnZephyrosAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 3 != 0) return;
        RandomSource rand = player.level.random;
        int streams = 8;
        double yOffset = 1.2;

        for (int i = 0; i < streams; i++) {
            double angle = rotation + i * 2 * Math.PI / streams;
            double radius = 0.7 + Math.sin(rotation * 2 + i) * 0.1;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 2 + i) * 0.2;

            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(0.7f, 0.9f, 1f) : new Vector3f(0.9f, 0.9f, 1f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnAwakenedDaoAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 2 != 0) return;
        RandomSource rand = player.level.random;
        int particles = 20;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double radius = 1.0 + rand.nextDouble() * 0.5;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.5;

            float size = 0.7f + rand.nextFloat() * 0.2f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(0.6f, 1f, 0.3f) : new Vector3f(1f, 1f, 0.4f);

            double motionX = (player.getX() - px) * 0.05;
            double motionY = (player.getY() + yOffset - py) * 0.05;
            double motionZ = (player.getZ() - pz) * 0.05;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
    }

    private static void spawnTrueDaoAura(Player player, ServerLevel server, Vec3 pos) {
        delay++;
        RandomSource rand = player.level.random;
        int particles = 35;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double radius = 1.0 + rand.nextDouble();
            double angle = rand.nextDouble() * 2 * Math.PI;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.7;

            float size = 0.8f + rand.nextFloat() * 0.2f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(1f, 1f, 0.6f) : new Vector3f(1f, 1f, 1f);

            double motionX = (player.getX() - px) * 0.07;
            double motionY = (player.getY() + yOffset - py) * 0.07;
            double motionZ = (player.getZ() - pz) * 0.07;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
    }

    private static void spawnOriginDaoAura(Player player, ServerLevel server, Vec3 pos) {
        delay++;
        RandomSource rand = player.level.random;
        int particles = 50;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double radius = 1.5 + rand.nextDouble() * 2.0;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5);

            float size = 0.9f + rand.nextFloat() * 0.2f;
            Vector3f color;
            double r = rand.nextDouble();
            if (r < 0.5) color = new Vector3f(1f, 1f, 0.8f);
            else if (r < 0.8) color = new Vector3f(1f, 1f, 1f);
            else color = new Vector3f(0.6f, 1f, 1f);

            double motionX = (player.getX() - px) * 0.15;
            double motionY = (player.getY() + yOffset - py) * 0.15;
            double motionZ = (player.getZ() - pz) * 0.15;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
    }

    private static void spawnParanoiaAura(Player player, ServerLevel server, Vec3 pos) {
        if (delay++ % 2 != 0) return;
        RandomSource rand = player.level.random;
        int particles = 20;
        double yOffset = 1.2;

        for (int i = 0; i < particles; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 1.2;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.6;

            float size = 0.5f + rand.nextFloat() * 0.4f;
            Vector3f color = rand.nextDouble() < 0.5 ? new Vector3f(0.4f, 0f, 0.5f) : new Vector3f(0.3f, 0f, 0.3f);

            double motionX = (rand.nextDouble() - 0.5) * 0.02;
            double motionY = (rand.nextDouble() - 0.5) * 0.02;
            double motionZ = (rand.nextDouble() - 0.5) * 0.02;

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, motionX, motionY, motionZ, 0);
        }
    }

    private static void spawnSporebloodAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int clusters = 8;
        double yOffset = 1.2;

        for (int i = 0; i < clusters; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble();
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 2 + i) * 0.2;

            float size = 0.5f + (float)(Math.sin(rotation * 3 + i) * 0.2 + 0.2);

            double r = rand.nextDouble();
            Vector3f color;
            if (r < 0.33) color = new Vector3f(0.5f, 0f, 0.5f);
            else if (r < 0.66) color = new Vector3f(0.6f, 0f, 0.7f);
            else color = new Vector3f(0.3f, 0f, 0.4f);

            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);

            if (rand.nextDouble() < 0.2) {
                int burstParticles = 6 + rand.nextInt(4);
                for (int b = 0; b < burstParticles; b++) {
                    double burstRadius = 0.1 + rand.nextDouble() * 0.5;
                    double burstAngle = rand.nextDouble() * 2 * Math.PI;
                    double bx = px + Math.cos(burstAngle) * burstRadius;
                    double bz = pz + Math.sin(burstAngle) * burstRadius;
                    double by = py + (rand.nextDouble() - 0.5) * 0.3;

                    float burstSize = 0.3f + rand.nextFloat() * 0.3f;
                    Vector3f burstColor;
                    double rc = rand.nextDouble();
                    if (rc < 0.33) burstColor = new Vector3f(0.5f, 0f, 0.5f);
                    else if (rc < 0.66) burstColor = new Vector3f(0.6f, 0f, 0.7f);
                    else burstColor = new Vector3f(0.3f, 0f, 0.4f);

                    double rise = rand.nextDouble() * 0.15;

                    server.sendParticles(new DustParticleOptions(burstColor, burstSize), bx, by + rise, bz, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    private static void spawnCatharsisAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int shards = 20;
        double yOffset = 1.2;

        for (int i = 0; i < shards; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 0.2 + rand.nextDouble();
            double px = player.getX() + Math.cos(angle) * distance;
            double pz = player.getZ() + Math.sin(angle) * distance;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.4;

            float size = 0.5f + rand.nextFloat() * 0.3f;

            double rShade = 0.4 + rand.nextDouble() * 0.3; // darker reds
            double gShade = 0.0;
            double bShade = 0.0;

            Vector3f color = new Vector3f((float) rShade, (float) gShade, (float) bShade);

            double offsetX = (rand.nextDouble() - 0.5) * 0.2;
            double offsetZ = (rand.nextDouble() - 0.5) * 0.2;
            double offsetY = (rand.nextDouble() - 0.5) * 0.2;

            server.sendParticles(new DustParticleOptions(color, size), px + offsetX, py + offsetY, pz + offsetZ, 1, 0, 0, 0, 0);

            if (rand.nextDouble() < 0.2) {
                double streakLength = 0.5 + rand.nextDouble() * 0.5;
                server.sendParticles(new DustParticleOptions(color, size),
                        px + Math.cos(angle) * streakLength, py + offsetY, pz + Math.sin(angle) * streakLength,
                        1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnExcaliburAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        double yOffset = 1.5;

        int crownPoints = 12;
        double crownRadius = 0.6;
        for (int i = 0; i < crownPoints; i++) {
            double angle = i * 2 * Math.PI / crownPoints + rotation;
            double px = player.getX() + Math.cos(angle) * crownRadius;
            double pz = player.getZ() + Math.sin(angle) * crownRadius;
            double py = player.getY() + yOffset + 0.6; // crown height
            float size = 0.8f + rand.nextFloat() * 0.2f;
            server.sendParticles(new DustParticleOptions(new Vector3f(1f, 0.95f, 0.6f), size),
                    px, py, pz, 1, 0, 0, 0, 0);
        }

        int groundPoints = 10;
        double groundRadius = 1.0;
        for (int i = 0; i < groundPoints; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + (rand.nextDouble() * 0.5);
            float size = 0.6f + rand.nextFloat() * 0.2f;
            server.sendParticles(new DustParticleOptions(new Vector3f(1f, 1f, 0.8f), size),
                    px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnGramAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int shards = 12;
        int streaks = 8;
        double yOffset = 1.2;

        for (int i = 0; i < shards; i++) {
            double angle = i * 2 * Math.PI / shards + rotation;
            double radius = 0.7 + Math.sin(rotation * 2 + i) * 0.15;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 2 + i) * 0.2;
            float size = 0.7f + rand.nextFloat() * 0.2f;
            Vector3f color = rand.nextDouble() < 0.7 ? new Vector3f(0f, 0.8f, 0f) : new Vector3f(0.3f, 1f, 0.5f);
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }

        for (int i = 0; i < streaks; i++) {
            double px = player.getX() + (rand.nextDouble() - 0.5) * 1.2;
            double pz = player.getZ() + (rand.nextDouble() - 0.5) * 1.2;
            double py = player.getY() + yOffset;
            double length = 0.5 + rand.nextDouble() * 0.7;
            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(0f, 0.6f + (float)rand.nextDouble() * 0.4f, 0f);
            for (int j = 0; j < 3; j++) {
                server.sendParticles(new DustParticleOptions(color, size),
                        px, py + j * 0.15, pz, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnFragarachAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        double yOffset = 1.2;

        for (int i = 0; i < 5; i++) {
            if (rand.nextDouble() < 0.3) continue;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.5 + rand.nextDouble() * 0.5;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + rand.nextDouble() * 0.5;
            float size = 0.6f + rand.nextFloat() * 0.3f;
            server.sendParticles(new DustParticleOptions(new Vector3f(0.7f, 0.9f, 1f), size), px, py, pz, 1, 0, 0.03, 0, 0.01);
        }

        for (int i = 0; i < 8; i++) {
            if (rand.nextDouble() < 0.5) continue;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.3;
            float size = 0.5f + rand.nextFloat() * 0.3f;
            server.sendParticles(new DustParticleOptions(new Vector3f(0.8f, 1f, 1f), size), px, py, pz, 1, 0, 0, 0, 0);
        }

        for (int i = 0; i < 10; i++) {
            if (rand.nextDouble() < 0.4) continue;
            double angle = i * 2 * Math.PI / 10 + player.level.random.nextDouble() * 0.5;
            double radius = 0.4 + Math.sin(rotation * 2 + i) * 0.2;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 3 + i) * 0.15;
            float size = 0.4f + rand.nextFloat() * 0.2f;
            server.sendParticles(new DustParticleOptions(new Vector3f(0.6f, 0.85f, 1f), size), px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnBloodSuckerAura(Player player, ServerLevel server, Vec3 pos) {
        RandomSource rand = player.level.random;
        int droplets = 10;
        int orbs = 6;
        double yOffset = 1.2;
        double radius = 0.6;

        for (int i = 0; i < droplets; i++) {
            double angle = i * 2 * Math.PI / droplets + rotation;
            double px = player.getX() + Math.cos(angle) * radius;
            double pz = player.getZ() + Math.sin(angle) * radius;
            double py = player.getY() + yOffset + Math.sin(rotation * 2 + i) * 0.15;
            float size = 0.6f + rand.nextFloat() * 0.3f;
            Vector3f color = new Vector3f(0.5f + rand.nextFloat() * 0.5f, 0f, 0f);
            if (rand.nextDouble() < 0.3) continue;
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }

        for (int i = 0; i < orbs; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double r = 0.3 + rand.nextDouble() * 0.7;
            double px = player.getX() + Math.cos(angle + rotation) * r;
            double pz = player.getZ() + Math.sin(angle + rotation) * r;
            double py = player.getY() + yOffset + (rand.nextDouble() - 0.5) * 0.3;
            float size = 0.8f - (float) (rand.nextDouble() * 0.5f);
            Vector3f color = new Vector3f(0.7f + rand.nextFloat() * 0.3f, 0f, 0f); // deep red
            if (rand.nextDouble() < 0.4) continue; // flicker
            server.sendParticles(new DustParticleOptions(color, size), px, py, pz, 1, 0, 0, 0, 0);
        }

    }
}
