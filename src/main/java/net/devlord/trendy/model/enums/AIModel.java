package net.devlord.trendy.model.enums;

/**
 * AI Model options for image generation
 */
public enum AIModel {
    GEMINI_2_5_FLASH("gemini-2.5-flash-image-preview", "Google Gemini 2.5 Flash Image"),
    GEMINI_3_PRO("gemini-3-pro-image-preview", "Gemini 3 Pro Image");

    private final String modelId;
    private final String displayName;

    AIModel(String modelId, String displayName) {
        this.modelId = modelId;
        this.displayName = displayName;
    }

    public String getModelId() {
        return modelId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEndpoint() {
        return "https://generativelanguage.googleapis.com/v1beta/models/" + modelId + ":generateContent";
    }

    public static AIModel fromString(String value) {
        if (value == null) return GEMINI_2_5_FLASH;
        for (AIModel model : values()) {
            if (model.name().equalsIgnoreCase(value) || model.modelId.equals(value)) {
                return model;
            }
        }
        return GEMINI_2_5_FLASH;
    }
}
