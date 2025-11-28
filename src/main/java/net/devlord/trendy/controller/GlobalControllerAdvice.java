package net.devlord.trendy.controller;

import net.devlord.trendy.model.enums.AdPosition;
import net.devlord.trendy.service.AdConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final AdConfigService adConfigService;
    
    /**
     * Make AdConfigService available to all templates
     */
    @ModelAttribute("adConfigService")
    public AdConfigService adConfigService() {
        return adConfigService;
    }
    
    /**
     * Make AdPosition enum values available to all templates
     */
    @ModelAttribute("AdPosition")
    public AdPosition[] adPositions() {
        return AdPosition.values();
    }
    
    /**
     * Make individual AdPosition constants available
     */
    @ModelAttribute("HOME_TOP")
    public AdPosition homeTop() {
        return AdPosition.HOME_TOP;
    }
    
    @ModelAttribute("HOME_SIDEBAR")
    public AdPosition homeSidebar() {
        return AdPosition.HOME_SIDEBAR;
    }
    
    @ModelAttribute("TRENDS_BETWEEN")
    public AdPosition trendsBetween() {
        return AdPosition.TRENDS_BETWEEN;
    }
    
    @ModelAttribute("TRENDS_SIDEBAR")
    public AdPosition trendsSidebar() {
        return AdPosition.TRENDS_SIDEBAR;
    }
    
    @ModelAttribute("GALLERY_SIDEBAR")
    public AdPosition gallerySidebar() {
        return AdPosition.GALLERY_SIDEBAR;
    }
    
    @ModelAttribute("DETAIL_BOTTOM")
    public AdPosition detailBottom() {
        return AdPosition.DETAIL_BOTTOM;
    }
}
