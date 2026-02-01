package org.aminesidki.postprep.controller;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.ArticleDTO;
import org.aminesidki.postprep.enumeration.Status;
import org.aminesidki.postprep.security.CustomUserDetails;
import org.aminesidki.postprep.service.ArticleService;
import org.aminesidki.postprep.service.TextProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class ArticleController {
    private final ArticleService articleService;
    private final TextProcessingService textProcessingService;

    @PostMapping("/pdf")
    public ArticleDTO uploadPdf(@AuthenticationPrincipal CustomUserDetails user, @RequestBody MultipartFile pdfFile){
        ArticleDTO articleDto = articleService.save(ArticleDTO.builder().status(Status.PROCESSING).owner(user.getAppUser().getId()).build());
        textProcessingService.ocrAndProcess(pdfFile , articleDto.getId());
        return articleDto;
    }

    @GetMapping("/myArticles")
    public List<ArticleDTO> getMyArticles(@AuthenticationPrincipal CustomUserDetails user){
        return articleService.findAllByOwner(user.getAppUser());
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deleteArticle(@AuthenticationPrincipal CustomUserDetails user , @PathVariable UUID postId){
        return articleService.delete(user.getAppUser() , postId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }
}
