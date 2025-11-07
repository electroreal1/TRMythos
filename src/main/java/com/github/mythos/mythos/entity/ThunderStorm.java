package com.github.mythos.mythos.entity;

import com.github.manasmods.tensura.ability.SkillHelper;
import com.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import com.github.manasmods.tensura.event.SkillGriefEvent;
import com.github.manasmods.tensura.network.TensuraNetwork;
import com.github.manasmods.tensura.network.play2client.RequestFxSpawningPacket;
import com.github.manasmods.tensura.registry.effects.TensuraMobEffects;
import com.github.manasmods.tensura.util.damage.TensuraDamageSources;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.mythos.mythos.registry.MythosEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;

public class ThunderStorm extends BarrierEntity {
    public ThunderStorm(Level level, LivingEntity entity) {
        this((EntityType) MythosEntityTypes.THUNDER_STORM.get(), level);
        this.setOwner(entity);
        this.noCulling = false;
    }

    public ThunderStorm(EntityType<? extends ThunderStorm> entityType, Level level) {
        super(entityType, level);
    }

    public boolean canWalkThrough() {
        return true;
    }

    public boolean blockBuilding() {
        return false;
    }

    public boolean isMultipartEntity() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    public void tick() {
        super.tick();
        Entity owner = this.getOwner();
        if (owner != null) {
            this.setPos(owner.getX(), owner.getY() + (double)(owner.getBbHeight() / 2.0F), owner.getZ());
            List<ThunderStorm> thunderStorms = this.getLevel().getEntitiesOfClass(ThunderStorm.class, owner.getBoundingBox(), (entityData) -> {
                return entityData.getOwner() == owner && entityData != this;
            });
            if (!thunderStorms.isEmpty() || !owner.isAlive()) {
                this.discard();
            }
        }

        if (!this.level.isClientSide()) {
            if (this.tickCount % 5 == 0) {
                TensuraNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> {
                    return this;
                }), new RequestFxSpawningPacket(new ResourceLocation("tensura:fallen_hypno"), this.getId(), 0.0, 0.0, 0.0, true));
                new RequestFxSpawningPacket(new ResourceLocation("tensura:fallen_hypno"), this.getId(), 0.0, 0.0, 0.0, true);
                new RequestFxSpawningPacket(new ResourceLocation("tensura:fallen_hypno"), this.getId(), 0.0, 0.0, 0.0, true);
            }

            if (this.tickCount % 10 == 0 && TensuraGameRules.canSkillGrief(this.level)) {
                if (!this.level.isAreaLoaded(this.blockPosition(), 5)) {
                    return;
                }

                BlockPos.betweenClosedStream((new AABB(this.blockPosition())).inflate((double)this.getRadius())).forEach((pos) -> {
                    if (!(this.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) > (double)(this.getRadius() * this.getRadius()))) {
                        if (this.level.getBlockState(pos).is(Blocks.WATER)) {
                            if (this.level.getFluidState(pos).isSource()) {
                                if (!this.level.getBlockState(pos.above()).is(Blocks.WATER)) {
                                    if (!(this.random.nextFloat() > 0.2F)) {
                                        SkillGriefEvent.Pre preGrief = new SkillGriefEvent.Pre(owner, this.getSkill(), pos);
                                        if (!MinecraftForge.EVENT_BUS.post(preGrief)) {
                                            this.level.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
                                            this.level.scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(this.random, 200, 400));
                                            MinecraftForge.EVENT_BUS.post(new SkillGriefEvent.Post(owner, this.getSkill(), pos));
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }

        }
    }

    protected void hitTarget() {
        AABB barrierBox = new AABB(this.getX() - (double)this.getRadius(), this.getY() - (double)this.getRadius(), this.getZ() - (double)this.getRadius(), this.getX() + (double)this.getRadius(), this.getY() + (double)this.getRadius() + (double)this.getHeight(), this.getZ() + (double)this.getRadius());
        List<LivingEntity> targets = this.getLevel().getEntitiesOfClass(LivingEntity.class, barrierBox);
        Iterator var3 = targets.iterator();

        while(var3.hasNext()) {
            LivingEntity target = (LivingEntity)var3.next();
            if (this.canHitEntity(target)) {
                this.applyEffect(target);
            }
        }

    }

    public void applyEffect(LivingEntity entity) {
        Entity owner = this.getOwner();
        if (owner == null || !entity.isAlliedTo(owner) && entity != owner) {
            DamageSource damageSource = TensuraDamageSources.indirectElementalAttack("tensura.lightning_attack", this, this.getOwner(), this.getMpCost() / 10.0, this.getSkill(), true);
            if (entity.hurt(damageSource, this.getDamage()) && this.tickCount % 400 == 0) {
                int chillLevel = 0;
                MobEffectInstance insanity = entity.getEffect((MobEffect)TensuraMobEffects.PARALYSIS.get());
                if (insanity != null) {
                    chillLevel = insanity.getAmplifier() + 1;
                }

                SkillHelper.checkThenAddEffectSource(entity, owner, new MobEffectInstance((MobEffect)TensuraMobEffects.PARALYSIS.get(), 420, chillLevel, false, false, false));
            }

        }
    }
}
