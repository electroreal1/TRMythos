//package com.github.mythos.mythos.handler;
//
//import net.minecraft.client.Minecraft;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.loading.FMLEnvironment;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Set;
//import java.util.UUID;
//
//@Mod.EventBusSubscriber(
//        modid = "trmythos",
//        bus = Mod.EventBusSubscriber.Bus.FORGE,
//        value = {Dist.CLIENT}
//)
//public class MythosBetaHandler {
//
//    public static final Set<UUID> ALLOWED_UUIDS = Set.of(
//            UUID.fromString("fa0838cb-0c62-46c9-bdfa-9ef05fcaaedd"),
//            UUID.fromString("0acdcb0d-0c24-471e-b285-9ed151438b2a"),
//            UUID.fromString("e313811f-6b1c-4aea-8211-0aaa4f9adb11"),
//            UUID.fromString("3c930a59-4d3d-4e4f-b62b-2f71073e1bbb"),
//            UUID.fromString("e190ed1d-c21c-4030-b3bf-f834e7862fe2"),
//            UUID.fromString("e19f5415-0808-465b-b04e-0bad00bc7b67"),
//            UUID.fromString("7bd51cab-cb84-4ecf-a14b-38862fcdad21"),
//            UUID.fromString("517a7de8-aea4-48fc-85eb-250b7b352dff"),
//            UUID.fromString("c9a7dd27-dc29-46e5-9684-71136886a3c4"),
//            UUID.fromString("6914e96f-355c-44b6-bfcb-b9e933c5e037"),
//            UUID.fromString("24d1cd36-1393-4ba7-9dcb-942876a280be")
//    );
//
//    public MythosBetaHandler() {}
//
//    @SubscribeEvent
//    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
//        // Only run on the client
//        if (FMLEnvironment.dist == Dist.CLIENT) {
//            Minecraft mc = Minecraft.getInstance(); // m_91087_() -> getInstance()
//            UUID playerUUID = mc.player.getGameProfile().getId(); // m_91094_() -> player, m_92548_() -> getGameProfile()
//            startSecurityMonitor(mc, playerUUID);
//        }
//    }
//
//    private static void logUnauthorizedAccess(UUID uuid, String reason) {
//        try {
//            Path logPath = Paths.get("trmythos_logs/unauthorized_access.log");
//            Files.createDirectories(logPath.getParent());
//            String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//            String line = String.format("[%s] UUID: %s — %s%n", time, uuid, reason);
//            Files.writeString(logPath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//        } catch (IOException e) {
//            System.err.println("Failed to log unauthorized access: " + e.getMessage());
//        }
//    }
//
//    private static void startSecurityMonitor(Minecraft mc, UUID uuid) {
//    }
//}
