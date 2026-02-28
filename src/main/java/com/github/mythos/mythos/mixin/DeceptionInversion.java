package com.github.mythos.mythos.mixin;

import com.github.mythos.mythos.registry.MythosMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class DeceptionInversion {

    @Inject(method = "tick", at = @At("TAIL"))
    private void trmythos$invertControls(boolean isSneaking, float sneakMultiplier, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(MythosMobEffects.PATHOGEN_DECEPTION.get())) {
            KeyboardInput input = (KeyboardInput) (Object) this;

            input.forwardImpulse = -input.forwardImpulse;
            input.leftImpulse = -input.leftImpulse;

            if (player.isShiftKeyDown()) {
                input.jumping = !input.jumping;
            }
        }
    }
}
