package com.github.mythos.mythos.util.damage;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.tensura.util.damage.TensuraDamageSource;
import com.github.manasmods.tensura.util.damage.TensuraEntityDamageSource;
import com.github.mythos.mythos.ability.confluence.skill.unique.ConfluenceUniques;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class MythosDamageSources {

    public static final String DESTROY_RECORD = "trmythos.destroy_record";
    public static String TRUTH_DAMAGE = "trmythos.truth";
    public static final DamageSource DRAGONFIRE = (new TensuraDamageSource("trmythos.dragonfire")).setIsFire().bypassEnchantments().setMagic().setNoAggro();
    public static final DamageSource ROT = (new TensuraDamageSource("trmythos.rot")).setNotTensuraMagic().bypassArmor().bypassEnchantments().setMagic();



    public MythosDamageSources() {
    }

    public static DamageSource destroyRecord(Entity pSource) {
        return (new TensuraEntityDamageSource("trmythos.destory_record", pSource)).setNoKnock().setIgnoreBarrier(1.0F).bypassArmor().bypassInvul().bypassMagic();
    }

    public static DamageSource dragonFire(Entity entity) {
        return (new TensuraEntityDamageSource("trmythos.dragonfire", entity)).setNoKnock().bypassArmor().setIsFire().setMagic().setNoAggro();
    }

    public static DamageSource rot(Entity pSource) {
        return (new TensuraEntityDamageSource("trmythos.rot", pSource)).setNoKnock().setNotTensuraMagic().bypassArmor().bypassEnchantments().setMagic();
    }
    public static DamageSource truthDamage(Entity pSource) {
        return (new TensuraEntityDamageSource("trmythos.truth", pSource)).setSkill(new ManasSkillInstance((ManasSkill) ConfluenceUniques.FRAGARACH.get())).setNoKnock().setIgnoreBarrier(1.0F).bypassArmor().bypassInvul().bypassMagic();
    }

}
