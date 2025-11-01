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
}
