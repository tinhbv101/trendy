package net.devlord.trendy.model.enums;

import lombok.Getter;

@Getter
public enum AdPosition {
    HOME_TOP("home_top", "Home Page - Top Banner"),
    HOME_SIDEBAR("home_sidebar", "Home Page - Sidebar"),
    TRENDS_BETWEEN("trends_between", "Trends Page - Between Cards"),
    TRENDS_SIDEBAR("trends_sidebar", "Trends Page - Sidebar"),
    GALLERY_SIDEBAR("gallery_sidebar", "Gallery Page - Sidebar"),
    DETAIL_BOTTOM("detail_bottom", "Trend Detail - Bottom");

    private final String code;
    private final String displayName;

    AdPosition(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static AdPosition fromCode(String code) {
        for (AdPosition position : values()) {
            if (position.code.equals(code)) {
                return position;
            }
        }
        throw new IllegalArgumentException("Unknown ad position code: " + code);
    }
}
