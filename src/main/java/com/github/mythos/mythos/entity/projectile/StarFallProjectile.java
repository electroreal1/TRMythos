package com.github.mythos.mythos.entity.projectile;

import com.github.manasmods.tensura.entity.magic.TensuraProjectile;
import com.github.manasmods.tensura.util.damage.DamageSourceHelper;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class StarFallProjectile extends TensuraProjectile {
    public StarFallProjectile(EntityType<? extends StarFallProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public StarFallProjectile(Level levelIn, LivingEntity shooter) {
        super((EntityType) MythosEntityTypes.STARFALL.get(), levelIn);
        this.setOwner(shooter);
    }

    public ResourceLocation[] getTextureLocation() {
        return new ResourceLocation[]{new ResourceLocation("tensura", "textures/entity/projectiles/light_arrow.png")};
    }

    public boolean piercingBlock() {
        return true;
    }

    public boolean shouldDiscardInLava() {
        return false;
    }

    public boolean shouldDiscardInWater() {
        return false;
    }

    protected void hitEntity(Entity entity) {
        if (entity != this.getOwner()) {
            super.hitEntity(entity);
        }
    }

    public String getMagic() {
        return "tensura.space_attack";
    }

    protected void dealDamage(Entity target) {
        if (!(this.damage <= 0.0F)) {
            DamageSource damageSource = TensuraDamageSources.indirectElementalAttack("tensura.light_attack", this, this.getOwner(), true);
            if (target.hurt(DamageSourceHelper.addSkillAndCost(damageSource, this.getMpCost(), this.getSkill()), this.getDamage())) {
                target.invulnerableTime = 0;
            }

        }
        if (!(this.damage <= 0.0F)) {
            DamageSource damagesource = DamageSourceHelper.turnTensura(TensuraDamageSources.indirectElementalAttack(this.getMagic(), this, this.getOwner(), this.getMpCost(), this.getSkill(), this.isSpiritAttack())).setSpatial();
            if (this.isSpiritAttack()) {
                target.hurt(damagesource, this.getDamage());
            } else {
                DamageSourceHelper.dealSplitElementalDamage(target, damagesource, 0.9F, this.getDamage());
            }

        }
    }

}
