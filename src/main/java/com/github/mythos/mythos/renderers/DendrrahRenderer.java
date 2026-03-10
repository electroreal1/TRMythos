package com.github.mythos.mythos.renderers;

import com.github.manasmods.tensura.entity.client.player.PlayerLikeModel;
import com.github.manasmods.tensura.entity.client.player.PlayerLikeRenderer;
import com.github.mythos.mythos.entity.boss.DendrrahEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class DendrrahRenderer extends PlayerLikeRenderer<DendrrahEntity> {

    public DendrrahRenderer(EntityRendererProvider.Context pContext, boolean slim) {
        super(pContext, new PlayerLikeModel(pContext.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(pContext.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(pContext.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    @Override
    protected boolean shouldShowName(DendrrahEntity pEntity) {
        return true;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DendrrahEntity entity) {
        return entity.getTextureLocation();
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(DendrrahEntity entity, InteractionHand pHand) {
        if (entity.getAttack() != 0) {
            return switch (entity.getAttack()) {
                case 1, 8 -> ArmPose.EMPTY;
                case 2 -> ArmPose.THROW_SPEAR;
                case 4, 6 -> ArmPose.BOW_AND_ARROW;
                case 7 -> ArmPose.SPYGLASS;
                case 9 -> pHand == InteractionHand.OFF_HAND ? ArmPose.THROW_SPEAR : ArmPose.EMPTY;
                default -> pHand == InteractionHand.OFF_HAND ? ArmPose.BOW_AND_ARROW : ArmPose.EMPTY;
            };
        } else {
            ItemStack itemstack = entity.getItemInHand(pHand);
            if (itemstack.isEmpty()) {
                return ArmPose.EMPTY;
            } else if (!entity.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return ArmPose.CROSSBOW_HOLD;
            } else {
                if (entity.getUsedItemHand() == pHand && entity.getUseItemRemainingTicks() > 0) {
                    ArmPose armPose = switch (itemstack.getUseAnimation()) {
                        case BLOCK -> ArmPose.BLOCK;
                        case BOW -> ArmPose.BOW_AND_ARROW;
                        case SPEAR -> ArmPose.THROW_SPEAR;
                        case CROSSBOW -> ArmPose.CROSSBOW_CHARGE;
                        case SPYGLASS -> ArmPose.SPYGLASS;
                        case TOOT_HORN -> ArmPose.TOOT_HORN;
                        default -> null;
                    };
                    if (armPose != null) return armPose;
                }
                ArmPose forgeArmPose = IClientItemExtensions.of(itemstack).getArmPose(entity, pHand, itemstack);
                return forgeArmPose != null ? forgeArmPose : ArmPose.ITEM;
            }
        }
    }
}