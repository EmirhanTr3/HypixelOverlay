package xyz.emirdev.hypixeloverlay;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;
import java.util.Objects;

public class StatsHud {

    private static final String[] HEADERS = {"Name", "NW Lvl", "Lvl", "WS", "FKDR", "WLR", "Finals", "Wins", "Beds"};

    public static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.currentScreen instanceof ChatScreen) {
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            if (!HypixelOverlayClient.isHypixel()) {
                String message = "Server is not hypixel.net";
                int textWidth = client.textRenderer.getWidth(message);
                int x = screenWidth - textWidth - 4;
                int y = screenHeight - 25;
                drawContext.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x80000000);
                drawContext.drawText(client.textRenderer, message, x, y, 0xFFFFFFFF, true);
                return;
            }
            List<PlayerStats> playerStats = PlayerDataProvider.getPlayerStats();

            if (playerStats == null || playerStats.isEmpty()) {
                String message = "No stats loaded. Press '"
                        + KeyBindingHelper.getBoundKeyOf(HypixelOverlayClient.getInstance().keybind).getLocalizedText().getString()
                        + "' to fetch.";
                int textWidth = client.textRenderer.getWidth(message);
                int x = screenWidth - textWidth - 4;
                int y = screenHeight - 25;
                drawContext.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x80000000);
                drawContext.drawText(client.textRenderer, message, x, y, 0xFFFFFFFF, true);

                String keyStatusMessage1 = "API Key Status: ";
                String keyStatusMessage2 = HypixelOverlayClient.getInstance().apiKeyStatus.getName();
                int keyStatusTextWidth1 = client.textRenderer.getWidth(keyStatusMessage1);
                int keyStatusTextWidth2 = client.textRenderer.getWidth(keyStatusMessage2);
                int keyStatusX2 = screenWidth - keyStatusTextWidth2 - 4;
                int keyStatusX1 = keyStatusX2 - keyStatusTextWidth1;
                int keyStatusY = screenHeight - 38;
                drawContext.fill(keyStatusX1 - 2, keyStatusY - 2, keyStatusX1 + keyStatusTextWidth1 + keyStatusTextWidth2 + 2, keyStatusY + 10, 0x80000000);
                drawContext.drawText(client.textRenderer, keyStatusMessage1, keyStatusX1, keyStatusY, 0xFFFFFFFF, true);
                drawContext.drawText(client.textRenderer, keyStatusMessage2, keyStatusX2, keyStatusY, HypixelOverlayClient.getInstance().apiKeyStatus.getColor(), true);
                return;
            }

            int tableWidth = 440;
            int tableHeight = (playerStats.size() + 1) * 15 + 5;
            int x = screenWidth - tableWidth - 2;
            int y = screenHeight - 40 - tableHeight;

            String message = "Press '"
                    + KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.sprintKey).getLocalizedText().getString()
                    + " + "
                    + KeyBindingHelper.getBoundKeyOf(HypixelOverlayClient.getInstance().keybind).getLocalizedText().getString()
                    + "' to clear stats.";
            int textWidth = client.textRenderer.getWidth(message);

            drawContext.fill(screenWidth - textWidth - 10, y - 17, screenWidth - 2, y - 2, 0x80000000);
            drawContext.drawText(client.textRenderer, message, screenWidth - textWidth - 6, y - 13, 0xFFFFFFFF, true);

            String keyStatusMessage1 = "API Key Status: ";
            String keyStatusMessage2 = HypixelOverlayClient.getInstance().apiKeyStatus.getName();
            int keyStatusTextWidth1 = client.textRenderer.getWidth(keyStatusMessage1);
            int keyStatusTextWidth2 = client.textRenderer.getWidth(keyStatusMessage2);
            int keyStatusX2 = screenWidth - keyStatusTextWidth2 - textWidth - 10 - 4;
            int keyStatusX1 = keyStatusX2 - keyStatusTextWidth1;
            drawContext.fill(keyStatusX1 - 2, y - 17, keyStatusX1 + keyStatusTextWidth1 + keyStatusTextWidth2 + 2, y - 2, 0x80000000);
            drawContext.drawText(client.textRenderer, keyStatusMessage1, keyStatusX1, y - 13, 0xFFFFFFFF, true);
            drawContext.drawText(client.textRenderer, keyStatusMessage2, keyStatusX2, y - 13, HypixelOverlayClient.getInstance().apiKeyStatus.getColor(), true);

            renderTable(client, drawContext, playerStats, x, y, tableWidth, tableHeight);
        }
    }

    private static void renderTable(MinecraftClient client, DrawContext drawContext, List<PlayerStats> playerStats, int x, int y, int width, int height) {
        drawContext.fill(x, y, x + width, y + height, 0x80000000);

        int[] columnWidths = {110, 50, 40, 40, 40, 40, 40, 40, 40};
        int currentX = x + 5;
        for (int i = 0; i < HEADERS.length; i++) {
            drawContext.drawText(client.textRenderer, HEADERS[i], currentX, y + 5, 0xFFFFFFFF, true);
            currentX += columnWidths[i];
        }

        int currentY = y + 20;
        for (PlayerStats stats : playerStats) {
            currentX = x + 5;
            String[] rowData = {
                    !Objects.equals(stats.rank, "") ? stats.rank + " " + stats.name : stats.name,
                    String.format("%.0f", stats.nwLevel),
                    String.valueOf(stats.level),
                    String.valueOf(stats.winstreak),
                    String.format("%.2f", stats.fkdr),
                    String.format("%.2f", stats.wlr),
                    String.valueOf(stats.finalKills),
                    String.valueOf(stats.wins),
                    String.valueOf(stats.bedBreak)
            };

            for (int i = 0; i < rowData.length; i++) {
                int color = 0xFFFFFFFF;
                if (i == 0 && stats.level > ModConfig.INSTANCE.warnLevel) color = 0xFFFF5555;
                drawContext.drawText(client.textRenderer, rowData[i], currentX, currentY, color, true);
                currentX += columnWidths[i];
            }
            currentY += 15;
        }
    }
}
