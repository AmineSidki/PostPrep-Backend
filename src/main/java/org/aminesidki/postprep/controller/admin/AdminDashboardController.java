package org.aminesidki.postprep.controller.admin;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.ChartDataDTO;
import org.aminesidki.postprep.service.AppUserService;
import org.aminesidki.postprep.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final ArticleService articleService;
    private final AppUserService appUserService;

    @GetMapping
    public Map<String, Long> getGlobalStats(){
        Map<String, Long> stats = new HashMap<>();
        stats.put("articles", articleService.count());
        stats.put("users", appUserService.count());
        return stats;
    }
    @GetMapping("/stats/daily")
    public ResponseEntity<List<ChartDataDTO>> getDailyStats() {
        return ResponseEntity.ok(articleService.getDailyArticleStats());
    }

    @GetMapping("stats/monthly")
    public ResponseEntity<List<ChartDataDTO>> getMonthlyStats() {
        return ResponseEntity.ok(articleService.getMonthlyArticleStats());
    }
}
