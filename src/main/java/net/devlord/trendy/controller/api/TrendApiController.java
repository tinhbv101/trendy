package net.devlord.trendy.controller.api;

import net.devlord.trendy.model.dto.TrendDTO;
import net.devlord.trendy.service.TrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trends")
@RequiredArgsConstructor
@Tag(name = "Trends", description = "Trend management APIs")
public class TrendApiController {

    private final TrendService trendService;

    @Operation(
            summary = "Get all active trends",
            description = "Retrieve a list of all active fashion trends available for image generation"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of trends",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrendDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<TrendDTO>> getAllActiveTrends() {
        List<TrendDTO> trends = trendService.getAllActiveTrends();
        return ResponseEntity.ok(trends);
    }

    @Operation(
            summary = "Get trend by ID",
            description = "Retrieve detailed information about a specific trend by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved trend",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrendDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trend not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrendDTO> getTrendById(
            @Parameter(description = "ID of the trend to retrieve", required = true)
            @PathVariable Long id) {
        TrendDTO trend = trendService.getTrendDTOById(id);
        if (trend != null) {
            return ResponseEntity.ok(trend);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Search trends",
            description = "Search for trends by name or keywords"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved search results",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrendDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search query"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<TrendDTO>> searchTrends(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<TrendDTO> trends = trendService.searchTrends(keyword);
        return ResponseEntity.ok(trends);
    }

    @Operation(
            summary = "Get trends by category",
            description = "Retrieve all trends belonging to a specific category"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved trends by category",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrendDTO.class)
                    )
            )
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TrendDTO>> getTrendsByCategory(
            @Parameter(description = "Category name", required = true)
            @PathVariable String category) {
        List<TrendDTO> trends = trendService.getTrendsByCategory(category);
        return ResponseEntity.ok(trends);
    }

    @Operation(
            summary = "Get trending items",
            description = "Get the most popular trends based on usage count"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved trending items",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrendDTO.class)
                    )
            )
    })
    @GetMapping("/trending")
    public ResponseEntity<List<TrendDTO>> getTrendingItems(
            @Parameter(description = "Number of trending items to retrieve (default: 10)")
            @RequestParam(defaultValue = "10") int limit) {
        List<TrendDTO> trends = trendService.getTrendingTrends(limit);
        return ResponseEntity.ok(trends);
    }
}

