package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareInfo {
    private String token;
    private String username;
    private String trendName;
    private String shareUrl;
}

