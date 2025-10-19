package xyz.emirdev.hypixeloverlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class BedwarsStats {

    public static void get() {
        ClientWorld world = MinecraftClient.getInstance().world;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        if (world == null) {
            System.out.println("ClientWorld is null.");
            player.sendMessage(Text.literal("ClientWorld is null.").withColor(0xFFFF5555), true);
            return;
        }

        if (MinecraftClient.getInstance().getNetworkHandler() == null) return;

        List<String> playerUuids = MinecraftClient.getInstance().getNetworkHandler().getPlayerUuids().stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        if (playerUuids.isEmpty()) {
            System.out.println("No players found in the current world.");
            player.sendMessage(Text.literal("No players found in the current world.").withColor(0xFFFF5555), true);
            PlayerDataProvider.clearStats();
            return;
        }

        new Thread(() -> {
            System.out.println("Fetching stats for " + playerUuids.size() + " players...");
            player.sendMessage(Text.literal("Fetching stats for " + playerUuids.size() + " players...").withColor(0xFF88FFAA), true);
            List<String> playerStatsJson = HypixelAPI.getMultiplePlayerStats(playerUuids, HypixelOverlayClient.getInstance().hypixelAPIKey);

            if (playerStatsJson == null) return;

            List<PlayerStats> playerStats = playerStatsJson.stream()
                    .map(PlayerStats::fromJson)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            PlayerDataProvider.setPlayerStats(playerStats);

            System.out.println("Successfully processed and stored stats for " + playerStats.size() + " players.");
            player.sendMessage(Text.literal("Successfully obtained stats for " + playerStats.size() + " players.").withColor(0xFF88FFAA), true);
        }).start();
    }
}
