package com.github.mythos.mythos.handler;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "trmythos"
)
public class ParanoiaHandler {

    public static String NO_DAMAGE_TIMER_KEY = "NoDamageTimer";

    public ParanoiaHandler(){
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        LivingEntity var2 = event.getEntity();
        if (var2 instanceof Player player) {
            CompoundTag tag = player.getPersistentData();
            tag.putLong("NoDamageTimer", player.level.getGameTime());
        }

    }
}
