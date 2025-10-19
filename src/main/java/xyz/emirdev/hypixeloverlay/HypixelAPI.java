package xyz.emirdev.hypixeloverlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HypixelAPI {

    private static final String API_URL = "https://api.hypixel.net/v2/player?uuid=";

    public static String getPlayerStats(String uuid, String apiKey) throws IOException {
        URL url = URI.create(API_URL + uuid).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("API-Key", apiKey);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN && MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("HypixelOverlay > Invalid Hypixel API key.")
                                .withColor(0xFFFF5555),
                        true
                );
            }
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }

    public static List<String> getMultiplePlayerStats(List<String> uuids, String apiKey) {
        List<String> results = new ArrayList<>();
        for (String uuid : uuids) {
            try {
                String result = getPlayerStats(uuid, apiKey);
                results.add(result);
            } catch (IOException e) {
                System.err.println("Failed to get stats for UUID: " + uuid);
                e.printStackTrace();
                return null;
            }
        }
        return results;
    }
}
