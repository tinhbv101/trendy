package net.devlord.trendy.model.enums;

/**
 * Aspect ratio options for image generation
 */
public enum AspectRatio {
    SQUARE("1:1", 1024, 1024),
    PORTRAIT("9:16", 576, 1024),
    LANDSCAPE("16:9", 1024, 576),
    WIDE("4:3", 1024, 768),
    TALL("3:4", 768, 1024);

    private final String displayName;
    private final int width;
    private final int height;

    AspectRatio(String displayName, int width, int height) {
        this.displayName = displayName;
        this.width = width;
        this.height = height;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getGeminiAspectRatio() {
        return displayName;
    }

    public static AspectRatio fromString(String value) {
        if (value == null) return SQUARE;
        for (AspectRatio ratio : values()) {
            if (ratio.name().equalsIgnoreCase(value) || ratio.displayName.equals(value)) {
                return ratio;
            }
        }
        return SQUARE;
    }
}
