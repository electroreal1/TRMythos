package com.github.mythos.mythos;

import com.github.mythos.mythos.client.screen.OrunScreen;
import com.github.mythos.mythos.command.MythosCommands;
import com.github.mythos.mythos.config.MythosConfig;
import com.github.mythos.mythos.config.MythosSkillsConfig;
import com.github.mythos.mythos.handler.*;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.registry.ClientOnlyRegistrar;
import com.github.mythos.mythos.registry.MythosParticles;
import com.github.mythos.mythos.registry.MythosRegistery;
import com.github.mythos.mythos.registry.menu.MythosMenuTypes;
import com.github.mythos.mythos.registry.race.MythosRaces;
import com.github.mythos.mythos.shaders.ClientShaderHandler;
import com.github.mythos.mythos.voiceoftheworld.VoiceOfTheWorld;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

@Mod("trmythos")
public class Mythos {
    public static final String MOD_ID = "trmythos";
    public static final String MOD_VERSION = "v1.26.2.36";
    public static final Logger LOGGER = LogManager.getLogger("trmythos");
    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("trmythos", "network"), () -> {
        return "1.0";
    }, (s) -> {
        return true;
    }, (s) -> {
        return true;
    });

    public Mythos() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 1. Common Listeners
        modEventBus.addListener(this::onCommonSetup);
        MythosRegistery.register(modEventBus);
        modEventBus.register(MythosRaces.class);
        MythosParticles.init(modEventBus);

        // 2. Common Handlers (No client imports in these!)
        MinecraftForge.EVENT_BUS.register(CrimsonTyrantHandler.class);
        MinecraftForge.EVENT_BUS.register(CarnageHandler.class);
        MinecraftForge.EVENT_BUS.register(KhaosHandler.class);

        MinecraftForge.EVENT_BUS.register(this);

        // 3. Safe Client Loading via Proxy
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientOnlyRegistrar::registerClientOnlyEvents);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MythosConfig.SPEC, getConfigFileName("mythos-common"));
        LOGGER.info("Mythos has been loaded!");
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        LOGGER.info("Mythos: Attempting to register commands...");
        MythosCommands.register(event.getDispatcher());
        LOGGER.info("Mythos Commands Registered");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!MythosSkillsConfig.voice_of_the_world.get()) return;
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            if (event.player.tickCount % 10000 == 0 && event.player instanceof ServerPlayer serverPlayer) {
                VoiceOfTheWorld.checkAwakeningStatus(serverPlayer); // True Hero and True Demon Lord
                VoiceOfTheWorld.checkHeroEggOrDemonLordSeed(serverPlayer); // Hero Egg and Demon Lord Seed
            }
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }
    private String getConfigFileName(String name) {
        return String.format("%s/%s.toml", "tensura-reincarnated", name);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common Setup");
        event.enqueueWork(MythosNetwork::register);
        if (this.isFirstLaunch()) {
            this.editTOMLFile();
            this.editEngravings();
            this.markAsEdited();
            LOGGER.info("Common setup works and TOML file was edited.");
        } else {
            LOGGER.info("Common setup works. TOML file already edited.");
        }

    }

    private void onClientSetup(FMLClientSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MenuScreens.register(MythosMenuTypes.ORUN_MENU.get(), OrunScreen::new);
        MinecraftForge.EVENT_BUS.register(ClientShaderHandler.class);
        MinecraftForge.EVENT_BUS.register(YellowSignOverlayHandler.class);
        MinecraftForge.EVENT_BUS.register(HaliShaderHandler.class);
        MinecraftForge.EVENT_BUS.register(GlobalEffectHandler.class);
        MinecraftForge.EVENT_BUS.register(KhaosHandler.class);
    }



    private boolean isFirstLaunch() {
        File markerFile = new File("defaultconfigs/tensura-reincarnated/mythos_first_launch_marker");
        return !markerFile.exists();
    }

    private void markAsEdited() {
        File markerFile = new File("defaultconfigs/tensura-reincarnated/mythos_first_launch_marker");

        try {
            if (markerFile.createNewFile()) {
                System.out.println("Marker file created: " + markerFile.getAbsolutePath());
            } else {
                System.out.println("Marker file already exists.");
            }
        } catch (IOException var3) {
            IOException e = var3;
            e.printStackTrace();
            System.out.println("Error creating marker file: " + e.getMessage());
        }

    }

    public void editTOMLFile() {
        File tomlFile = new File("defaultconfigs/tensura-reincarnated/common.toml");
        StringBuilder contentBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(tomlFile));

            String line;
            try {
                while((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append(System.lineSeparator());
                }
            } catch (Throwable var19) {
                try {
                    reader.close();
                } catch (Throwable var15) {
                    var19.addSuppressed(var15);
                }

                throw var19;
            }

            reader.close();
        } catch (IOException var20) {
            IOException e = var20;
            e.printStackTrace();
            System.out.println("Error reading the TOML file: " + e.getMessage());
            return;
        }


        String content = contentBuilder.toString();
        String[] newStarting = new String[]{"trmythos:canine", "trmythos:maiden", "trmythos:lesser_serpent","trmythos:godling","trmythos:metalloid", "trmythos:revenant","trmythos:fableborn"};
        String[] newRandom = new String[]{"trmythos:canine", "trmythos:maiden", "trmythos:lesser_serpent","trmythos:godling","trmythos:metalloid", "trmythos:revenant","trmythos:fableborn"};
        String[] newSkills = new String[]{"trmythos:omniscient_eye", "trmythos:faker", "trmythos:purity","trmythos:bloodsucker", "trmythos:profanity","trmythos:opportunist","trmythos:sporeblood","trmythos:fragarach","trmythos:excalibur","trmythos:gram", "trmythos:heavens_wrath","trmythos:zephyros","trmythos:introvert","trmythos:stargazer","trmythos:tenacious","trmythos:demonologist","trmythos:sagittarius", "trmythos:npc_life", "trmythos:celestial_path_blue", "trmythos:celestial_cultivation_orange", "trmythos:celestial_mutation_red", "trmythos:author", "trmythos:alchemist", "trmythos:hoarder", "trmythos:nights_thief", "trmythos:pretender_king", "trmythos:false_hero", "trmythos:crimson_arcanist", "trmythos:gaze", "trmythos:yellow_sign", "trmythos:author"};
        String[] creatorSkills = new String[]{"trmythos:opportunist", "trmythos:heavens_wrath","trmythos:zephyros","trmythos:introvert","trmythos:stargazer","trmythos:tenacious","trmythos:demonologist"};
        String startingRacesKey = "startingRaces = [";
        String randomRacesKey = "possibleRandomRaces = [";
        String reincarnationSkillsKey = "reincarnationSkills = [";
        String creatorSkillsKey = "skillCreatorSkills = [";
        content = this.addItemsToTOMLList(content, startingRacesKey, newStarting);
        content = this.addItemsToTOMLList(content, randomRacesKey, newRandom);
        content = this.addItemsToTOMLList(content, reincarnationSkillsKey, newSkills);
        content = this.addItemsToTOMLList(content, creatorSkillsKey, creatorSkills);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(tomlFile));

            try {
                writer.write(content);
            } catch (Throwable var17) {
                try {
                    writer.close();
                } catch (Throwable var16) {
                    var17.addSuppressed(var16);
                }

                throw var17;
            }

            writer.close();
        } catch (IOException var18) {
            IOException e = var18;
            e.printStackTrace();
            System.out.println("Error writing to the TOML file: " + e.getMessage());
        }

        System.out.println("Items added to TOML lists successfully.");
    }

    public void editEngravings() {
        File tomlFile = new File("defaultconfigs/tensura-reincarnated/common.toml");
        StringBuilder contentBuilder = new StringBuilder();

        //    try {
        //      BufferedReader reader = new BufferedReader(new FileReader(tomlFile));

        //     String line;
        //      try {
        //        while((line = reader.readLine()) != null) {
        //          contentBuilder.append(line).append(System.lineSeparator());
        //        }
        //     } catch (Throwable var21) {
        //      try {
        //           reader.close();
        //     } catch (Throwable var17) {
        //          var21.addSuppressed(var17);
        //       }

        //       throw var21;
    }
//
    //     reader.close();
    //  } catch (IOException var22) {
    //     IOException e = var22;
    //      e.printStackTrace();
    //       System.out.println("Error reading the TOML file: " + e.getMessage());
    //        return;
    //     }

    //  String content = contentBuilder.toString();
    //  String[] commonEngraveNew = new String[0];
    //   String[] uncommonEngraveNew = new String[0];
    //     String[] rareEngraveNew = new String[0];
    //    String[] veryRareEngraveNew = new String[0];
    //   String[] blacklistedEngraveNew = new String[]{""};
    //   String commonKey = "enchantments.commonEngrave";
    // String uncommonKey = "enchantments.uncommonEngrave";
    //   String rareKey = "enchantments.rareEngrave";
    //   String veryRareKey = "enchantments.veryRareEngrave";
    //   String blacklistedKey = "enchantments.researcherEnchant";
    //   content = this.addItemsToTOMLEngravings(content, commonKey, commonEngraveNew);
    //   content = this.addItemsToTOMLEngravings(content, uncommonKey, uncommonEngraveNew);
    //   content = this.addItemsToTOMLEngravings(content, rareKey, rareEngraveNew);
    //   content = this.addItemsToTOMLEngravings(content, veryRareKey, veryRareEngraveNew);
    //  content = this.addBlacklistToTOMLEngravings(content, blacklistedKey, blacklistedEngraveNew);

    //   try {
    //       BufferedWriter writer = new BufferedWriter(new FileWriter(tomlFile));

    //       try {
    //       writer.write(content);
    //    } catch (Throwable var19) {
    //         try {
    //             writer.close();
    //        } catch (Throwable var18) {
    //           var19.addSuppressed(var18);
    //        }

    //       throw var19;
    //     }

    //    writer.close();
    //   } catch (IOException var20) {
    //     IOException e = var20;
    //      e.printStackTrace();
    //     System.out.println("Error writing to the TOML file: " + e.getMessage());
    // }

    //   System.out.println("Engraving enchantments added to TOML successfully.");
    //   }

//    private String addItemsToTOMLEngravings(String content, String enchantmentSection, String[] newItems) {
//        int sectionIndex = content.indexOf("[" + enchantmentSection + "]");
//        if (sectionIndex == -1) {
//            return content;
//        } else {
//            int enchantmentsLineStart = content.indexOf("enchantments = [", sectionIndex);
//            if (enchantmentsLineStart == -1) {
//                return content;
//            } else {
//                int lineEnd = content.indexOf("]", enchantmentsLineStart);
//                if (lineEnd == -1) {
//                    return content;
//                } else {
//                    String currentListStr = content.substring(enchantmentsLineStart + "enchantments = [".length(), lineEnd).trim();
//                    Set<String> currentEnchantments = new HashSet();
//                    String[] var9;
//                    int var10;
//                    int var11;
//                    String enchantment;
//                    if (!currentListStr.isEmpty()) {
//                        var9 = currentListStr.split(",");
//                        var10 = var9.length;
//
//                        for(var11 = 0; var11 < var10; ++var11) {
//                            enchantment = var9[var11];
//                            currentEnchantments.add(enchantment.trim().replace("\"", ""));
//                        }
//                    }
//
//                    var9 = newItems;
//                    var10 = newItems.length;
//
//                    for(var11 = 0; var11 < var10; ++var11) {
//                        enchantment = var9[var11];
//                        currentEnchantments.add(enchantment);
//                    }
//
//                    StringBuilder newEnchantmentsLine = new StringBuilder("enchantments = [");
//                    boolean first = true;
//
//                    for(Iterator var15 = currentEnchantments.iterator(); var15.hasNext(); first = false) {
//                        enchantment = (String)var15.next();
//                        if (!first) {
//                            newEnchantmentsLine.append(", ");
//                        }
//
//                        newEnchantmentsLine.append("\"").append(enchantment).append("\"");
//                    }
//
//                    newEnchantmentsLine.append("]");
//                    String var10000 = content.substring(0, enchantmentsLineStart);
//                    return var10000 + newEnchantmentsLine.toString() + content.substring(lineEnd + 1);
//                }
//            }
//        }
//    }
//
//    private String addBlacklistToTOMLEngravings(String content, String enchantmentSection, String[] newItems) {
//        int sectionIndex = content.indexOf("[" + enchantmentSection + "]");
//        if (sectionIndex == -1) {
//            return content;
//        } else {
//            int enchantmentsLineStart = content.indexOf("blacklist = [", sectionIndex);
//            if (enchantmentsLineStart == -1) {
//                return content;
//            } else {
//                int lineEnd = content.indexOf("]", enchantmentsLineStart);
//                if (lineEnd == -1) {
//                    return content;
//                } else {
//                    String currentListStr = content.substring(enchantmentsLineStart + "blacklist = [".length(), lineEnd).trim();
//                    Set<String> currentEnchantments = new HashSet();
//                    String[] var9;
//                    int var10;
//                    int var11;
//                    String enchantment;
//                    if (!currentListStr.isEmpty()) {
//                        var9 = currentListStr.split(",");
//                        var10 = var9.length;
//
//                        for(var11 = 0; var11 < var10; ++var11) {
//                            enchantment = var9[var11];
//                            currentEnchantments.add(enchantment.trim().replace("\"", ""));
//                        }
//                    }
//
//                    var9 = newItems;
//                    var10 = newItems.length;
//
//                    for(var11 = 0; var11 < var10; ++var11) {
//                        enchantment = var9[var11];
//                        currentEnchantments.add(enchantment);
//                    }
//
//                    StringBuilder newEnchantmentsLine = new StringBuilder("blacklist = [");
//                    boolean first = true;
//
//                    for(Iterator var15 = currentEnchantments.iterator(); var15.hasNext(); first = false) {
//                        enchantment = (String)var15.next();
//                        if (!first) {
//                            newEnchantmentsLine.append(", ");
//                        }
//
//                        newEnchantmentsLine.append("\"").append(enchantment).append("\"");
//                    }
//
//                    newEnchantmentsLine.append("]");
//                    String var10000 = content.substring(0, enchantmentsLineStart);
//                    return var10000 + newEnchantmentsLine.toString() + content.substring(lineEnd + 1);
//                }
//            }
//        }
//    }

    private String addItemsToTOMLList(String content, String listKey, String[] newItems) {
        int index = content.indexOf(listKey);
        if (index == -1) {
            System.out.println("List identifier '" + listKey + "' not found.");
            return content;
        } else {
            int endIndex = content.indexOf("]", index) + 1;
            if (endIndex == 0) {
                System.out.println("Closing bracket not found for list: " + listKey);
                return content;
            } else {
                String listContent = content.substring(index, endIndex);
                String[] var7 = newItems;
                int var8 = newItems.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    String newItem = var7[var9];
                    if (!listContent.contains(newItem)) {
                        listContent = listContent.replace("]", ", \"" + newItem + "\"]");
                    }
                }

                return content.replace(content.substring(index, endIndex), listContent);
            }
        }
    }
}
