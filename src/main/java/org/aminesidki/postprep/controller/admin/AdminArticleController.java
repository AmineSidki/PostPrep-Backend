package org.aminesidki.postprep.controller.admin;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.ArticleDTO;
import org.aminesidki.postprep.service.ArticleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin/articles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminArticleController {
    private final ArticleService articleService;

    @GetMapping
    public List<ArticleDTO> getArticles() {
        return articleService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable UUID id) {
        articleService.deleteById(id);
    }

}
