package xyz.emirdev.hypixeloverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerDataProvider {

    private static List<PlayerStats> playerStats = Collections.synchronizedList(new ArrayList<>());

    public static List<PlayerStats> getPlayerStats() {
        return playerStats;
    }

    public static void setPlayerStats(List<PlayerStats> stats) {
        playerStats.clear();
        playerStats.addAll(stats);
    }

    public static void clearStats() {
        playerStats.clear();
    }
}
