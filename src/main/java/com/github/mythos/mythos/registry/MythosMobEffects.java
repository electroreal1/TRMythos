package com.github.mythos.mythos.registry;

import com.github.mythos.mythos.mob_effect.*;
import com.github.mythos.mythos.mob_effect.debuff.BloodDrainEffect;
import com.github.mythos.mythos.mob_effect.debuff.DragonfireEffect;
import com.github.mythos.mythos.mob_effect.debuff.RotEffect;
import com.github.mythos.mythos.mob_effect.debuff.VaporizationFreezeEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;

public class MythosMobEffects {
    public static void register(IEventBus modEventBus) {
        registry.register(modEventBus);
    }

    private static final DeferredRegister<MobEffect> registry = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "trmythos");

    public static final RegistryObject<MobEffect> AVALON_REGENERATION = registry.register("avalon_regeneration", () ->
            new AvalonRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));
    public static final RegistryObject<MobEffect> APOSTLE_REGENERATION = registry.register("apostle_regeneration", () ->
            new ApostleRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(40, 2, 66)).getRGB()));
    public static final RegistryObject<MobEffect> DEAD_REGENERATION = registry.register("dead_apostle_regeneration", () ->
            new DeadRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(80, 2, 66)).getRGB()));
    public static final RegistryObject<MobEffect> RAPID_REGENERATION = registry.register("rapid_regeneration", () ->
            new RapidRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));
    public static final RegistryObject<MobEffect> VAPORIZATION_FREEZE = registry.register("vaporization_freeze", () ->
            new VaporizationFreezeEffect(MobEffectCategory.HARMFUL, (new Color(255, 144, 6)).getRGB()));
    public static final RegistryObject<MobEffect> BLOOD_DRAIN = registry.register("blood_drain", () ->
            new BloodDrainEffect(MobEffectCategory.HARMFUL, (new Color(255, 165, 3)).getRGB()));
    public static final RegistryObject<MobEffect> CHILD_OF_THE_PLANE = registry.register("child_of_the_plane_effect", () ->
            new ChildOfThePlaneEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 165, 3)).getRGB()));
    public static final RegistryObject<MobEffect> DRAGONFIRE = registry.register("dragonfire", () ->
            new DragonfireEffect(MobEffectCategory.HARMFUL, (new Color(255, 0, 0)).getRGB()));
    public static final RegistryObject<MobEffect> BLOOD_COAT = registry.register("blood_coat", () ->
            new BloodCoatEffect(MobEffectCategory.BENEFICIAL, (new Color(220, 20, 60)).getRGB()));
    public static final RegistryObject<MobEffect> COMPLETE_REGENERATION = registry.register("complete_regeneration", () ->
            new CompleteRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 0, 0)).getRGB()));
    public static final RegistryObject<MobEffect> ROT = registry.register("rot", () ->
            new RotEffect(MobEffectCategory.HARMFUL, (new Color(255, 0, 0))));
    public static final RegistryObject<MobEffect> EXCALIBUR_REGENERATION = registry.register("excalibur_regeneration", () ->
            new ExcaliburRegeneration(MobEffectCategory.BENEFICIAL, (new Color(15, 100, 100))));
    public static final RegistryObject<MobEffect> ENERGIZED_REGENERATION = registry.register("energized_regeneration", () ->
            new EnergizedRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));
    public static final RegistryObject<MobEffect> EMPOWERMENT_REGENERATION = registry.register("empowerment_regeneration", () ->
            new EmpowermentRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 166, 4)).getRGB()));
    public static final RegistryObject<MobEffect> LIGHTNING_COAT = registry.register("lightning_coat", () ->
            new LightningCoatEffect(MobEffectCategory.BENEFICIAL, (new Color(15,100, 125))));
    public static final RegistryObject<MobEffect> THUNDER_GOD = registry.register("thunder_god", () ->
            new ThunderGodEffect(MobEffectCategory.BENEFICIAL, (new Color(15,100, 150))));
    public static final RegistryObject<MobEffect> GOD_SLAYER = registry.register("god_slayer", () ->
            new GodSlayerEffect(MobEffectCategory.BENEFICIAL, (new Color(15, 255, 100)).getRGB()));
    public static final RegistryObject<MobEffect> COSTLESS_REGENERATION = registry.register("costless_regeneration", () ->
            new CostlessRegenerationEffect(MobEffectCategory.BENEFICIAL, (new Color(255, 0, 0)).getRGB()));
    public static final RegistryObject<MobEffect> ULTIMATE_VILLAIN = registry.register("ultimate_villain", () ->
            new UltimateVillainEffect(MobEffectCategory.BENEFICIAL, (new Color(100, 0, 0)).getRGB()));
    public static final RegistryObject<MobEffect> FINAL_SEAL_DOOM = registry.register("final_seal_doom", () ->
            new FinalSealDoomEffect(MobEffectCategory.HARMFUL, (new Color(100, 0, 0)).getRGB()));
    public static final RegistryObject<MobEffect> SPATIAL_DYSPHORIA = registry.register("spatial_dysphoria", () ->
            new SpatialDysphoriaEffect(MobEffectCategory.NEUTRAL, (new Color(90, 20, 90).getRGB())));
    public static final RegistryObject<MobEffect> NON_EUCLIDEAN_STEP = registry.register("non_euclidean_step", () ->
            new NonEuclideanStepEffect(MobEffectCategory.NEUTRAL, (new Color(90, 20, 90).getRGB())));
    public static final RegistryObject<MobEffect> BOUNDARY_ERASURE_SINK = registry.register("boundary_erasure_sink", () ->
            new BoundaryErasureSinkEffect(MobEffectCategory.NEUTRAL, (new Color(90, 20, 90).getRGB())));
    public static final RegistryObject<MobEffect> ATROPHY = registry.register("atropohy", () ->
            new AtrophyEffect(MobEffectCategory.NEUTRAL, (new Color(90, 20, 90).getRGB())));
    public static final RegistryObject<MobEffect> GREAT_SILENCE = registry.register("great_silence", () ->
            new GreatSilenceEffect(MobEffectCategory.NEUTRAL, (new Color(90, 20, 90).getRGB())));
    public static final RegistryObject<MobEffect> YELLOW_SIGN = registry.register("yellow_sign", () ->
            new YellowSignEffect(MobEffectCategory.NEUTRAL, (new Color(255, 165, 15)).getRGB()));
    public static final RegistryObject<MobEffect> SUNSET = registry.register("sunset", () ->
            new SunriseEffect(MobEffectCategory.NEUTRAL, (new Color(255, 165, 15)).getRGB()));
    public static final RegistryObject<MobEffect> SUNRISE = registry.register("sunrise", () ->
            new SunsetEffect(MobEffectCategory.NEUTRAL, (new Color(255, 165, 15)).getRGB()));
    public static final RegistryObject<MobEffect> KHONSU = registry.register("khonsu", () ->
            new EyeOfTheMoonKhonsuEffect(MobEffectCategory.NEUTRAL, (new Color(255, 165, 15)).getRGB()));


    public static void init(IEventBus modEventBus) {
        registry.register(modEventBus);
    }
}
