package xyz.emirdev.hypixeloverlay;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PlayerStats {

    public final String name;
    public final String rank;
    public final double nwLevel;
    public final int level;
    public final int winstreak;
    public final double fkdr;
    public final double wlr;
    public final int finalKills;
    public final int wins;
    public final int bedBreak;

    private PlayerStats(String name, String rank, double nwLevel, int level, int winstreak, double fkdr, double wlr, int finalKills, int wins, int bedBreak) {
        this.name = name;
        this.rank = rank;
        this.nwLevel = nwLevel;
        this.level = level;
        this.winstreak = winstreak;
        this.fkdr = fkdr;
        this.wlr = wlr;
        this.finalKills = finalKills;
        this.wins = wins;
        this.bedBreak = bedBreak;
    }

    public static PlayerStats fromJson(String json) {
        JsonObject data = new Gson().fromJson(json, JsonObject.class);
        if (data == null || !data.has("player") || data.get("player").isJsonNull()) {
            return null;
        }
        JsonObject hypixelData = data.getAsJsonObject("player");

        String name = getString(hypixelData, "displayname");

        String rank = "";
        String monthlyPackageRank = getString(hypixelData, "monthlyPackageRank");
        if ("SUPERSTAR".equals(monthlyPackageRank)) {
            rank = "MVP++";
        } else if (hypixelData.has("newPackageRank")) {
            rank = getString(hypixelData, "newPackageRank").replace("_PLUS", "+");
        }

        double nwExp = getDouble(hypixelData, "networkExp");
        double nwLevel = nwExp < 0 ? 1 : Math.floor(1 + -3.5 + Math.sqrt((-3.5 * -3.5) + (2.0 / 2500.0) * nwExp));

        JsonObject achievements = getObject(hypixelData, "achievements");
        int level = getInt(achievements, "bedwars_level");

        JsonObject stats = getObject(hypixelData, "stats");
        JsonObject bedwarsStats = getObject(stats, "Bedwars");

        int winstreak = getInt(bedwarsStats, "winstreak");
        int finalKills = getInt(bedwarsStats, "final_kills_bedwars");
        int finalDeaths = getInt(bedwarsStats, "final_deaths_bedwars");
        double fkdr = (finalDeaths == 0) ? finalKills : (double) finalKills / finalDeaths;

        int wins = getInt(bedwarsStats, "wins_bedwars");
        int losses = getInt(bedwarsStats, "losses_bedwars");
        double wlr = (losses == 0) ? wins : (double) wins / losses;

        int bedBreak = getInt(bedwarsStats, "beds_broken_bedwars");

        return new PlayerStats(name, rank, nwLevel, level, winstreak, fkdr, wlr, finalKills, wins, bedBreak);
    }

    private static JsonObject getObject(JsonObject parent, String key) {
        if (parent != null && parent.has(key) && parent.get(key).isJsonObject()) {
            return parent.getAsJsonObject(key);
        }
        return new JsonObject();
    }

    private static String getString(JsonObject parent, String key) {
        if (parent != null && parent.has(key) && parent.get(key).isJsonPrimitive()) {
            return parent.get(key).getAsString();
        }
        return "";
    }

    private static int getInt(JsonObject parent, String key) {
        if (parent != null && parent.has(key) && parent.get(key).isJsonPrimitive()) {
            return parent.get(key).getAsInt();
        }
        return 0;
    }

    private static double getDouble(JsonObject parent, String key) {
        if (parent != null && parent.has(key) && parent.get(key).isJsonPrimitive()) {
            return parent.get(key).getAsDouble();
        }
        return 0.0;
    }

    public String toString() {
        return "PlayerStats{" +
                "name='" + name + '\'' +
                ", rank='" + rank + '\'' +
                ", nwLevel=" + nwLevel +
                ", level=" + level +
                ", winstreak=" + winstreak +
                ", fkdr=" + fkdr +
                ", wlr=" + wlr +
                ", finalKills=" + finalKills +
                ", wins=" + wins +
                ", bedBreak=" + bedBreak +
                '}';
    }
}
