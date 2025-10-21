package xyz.emirdev.hypixeloverlay;

public enum APIKeyStatus {
    UNKNOWN("Unknown", 0xFFAAAAAA),
    VALID("Valid", 0xFF55FF55),
    INVALID("Invalid", 0xFFFF5555),
    ERROR("Error", 0xFFBB0000);

    private final String name;
    private final int color;

    APIKeyStatus(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
