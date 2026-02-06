package com.github.mythos.mythos.util;

import net.minecraft.world.damagesource.DamageSource;

public class MythosDamageSourceHelper {

    public MythosDamageSourceHelper() {
    }

    public static boolean isRot(DamageSource damageSource) {
        if (damageSource.getMsgId().contains("rot")) {
            return true;
        } else return false;
    }

    public static boolean isBlood(DamageSource damageSource) {
        if (damageSource.getMsgId().contains("blood")) {
            return true;
        } else if (damageSource.getMsgId().contains("bleed")) {
            return true;
        } else return false;
    }

    public static boolean isSound(DamageSource damageSource) {
        if (damageSource.getMsgId().contains("sound")) {
            return true;
        } else if (damageSource.getMsgId().contains("music")) {
            return true;
        } else if (damageSource.getMsgId().contains("sonic")) {
            return true;
        } else if (damageSource.getMsgId().contains("tensura.mind_requiem")) {
            return true;
        } else if (damageSource.getMsgId().contains("roar")) {
            return true;
        } else return false;
    }
}
