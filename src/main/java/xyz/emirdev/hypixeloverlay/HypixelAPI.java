package xyz.emirdev.hypixeloverlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HypixelAPI {

    private static final String BASE_URL = "https://api.hypixel.net/v2";
    private static final String PLAYER_ENDPOINT = BASE_URL + "/player?uuid=";
    private static final String PLAYER_COUNTS_ENDPOINT = BASE_URL + "/counts";

    public static String getPlayerStats(String uuid, String apiKey) throws IOException {
        try {
            URL url = URI.create(PLAYER_ENDPOINT + uuid).toURL();
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
                    HypixelOverlayClient.getInstance().apiKeyStatus = APIKeyStatus.INVALID;
                }
                throw new IOException("GET request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            if (e instanceof IOException)
                HypixelOverlayClient.getInstance().apiKeyStatus = APIKeyStatus.ERROR;
            throw e;
        }
    }

    public static List<String> getMultiplePlayerStats(List<String> uuids, String apiKey) {
        HypixelOverlayClient.getInstance().apiKeyStatus = validateAPIKey(apiKey);
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

    public static APIKeyStatus validateAPIKey(String apiKey) {
        try {
            URL url = URI.create(PLAYER_COUNTS_ENDPOINT).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("API-Key", apiKey);

            int responseCode = connection.getResponseCode();
            return switch (responseCode) {
                case HttpURLConnection.HTTP_OK -> APIKeyStatus.VALID;
                case HttpURLConnection.HTTP_FORBIDDEN -> APIKeyStatus.INVALID;
                default -> APIKeyStatus.UNKNOWN;
            };
        } catch (IOException e) {
            return APIKeyStatus.ERROR;
        }
    }
}
