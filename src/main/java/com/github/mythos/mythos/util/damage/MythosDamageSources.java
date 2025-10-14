package com.github.mythos.mythos.util.damage;

import com.github.manasmods.tensura.util.damage.TensuraEntityDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class MythosDamageSources {

    public static final String DESTROY_RECORD = "trmythos.destroy_record";

    public MythosDamageSources() {
    }

    public static DamageSource destroyRecord(Entity pSource) {
        return (new TensuraEntityDamageSource("trmythos.destory_record", pSource)).setNoKnock().setIgnoreBarrier(1.0F).bypassArmor().bypassInvul().bypassMagic();
    }

}
