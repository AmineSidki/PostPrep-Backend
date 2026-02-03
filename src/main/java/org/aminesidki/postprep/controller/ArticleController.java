package org.aminesidki.postprep.controller;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.ArticleDTO;
import org.aminesidki.postprep.enumeration.Status;
import org.aminesidki.postprep.security.CustomUserDetails;
import org.aminesidki.postprep.service.ArticleService;
import org.aminesidki.postprep.service.TextProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {
    private final ArticleService articleService;
    private final TextProcessingService textProcessingService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ArticleDTO> getAll(@AuthenticationPrincipal CustomUserDetails user){
        return articleService.findAll();
    }

    @GetMapping("/myArticles")
    public List<ArticleDTO> getMyArticles(@AuthenticationPrincipal CustomUserDetails user){
        return articleService.findAllByOwner(user.getAppUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #articleService.findById(#id).owner == user.appUser.id")
    public ArticleDTO getArticle(@AuthenticationPrincipal CustomUserDetails user , @PathVariable UUID id){
        return articleService.findById(id);
    }

    @PostMapping("/upload/pdf")
    public ArticleDTO uploadPdf(@AuthenticationPrincipal CustomUserDetails user, @RequestBody MultipartFile pdfFile){
        ArticleDTO articleDto = articleService.save(ArticleDTO.builder().status(Status.PROCESSING).owner(user.getAppUser().getId()).build());
        textProcessingService.processPdf(pdfFile , articleDto.getId());
        return articleDto;
    }

    @PostMapping("/upload/text")
    public ArticleDTO uploadText(@AuthenticationPrincipal CustomUserDetails user, @RequestBody String text){
        ArticleDTO articleDto = articleService.save(ArticleDTO.builder().status(Status.PROCESSING).owner(user.getAppUser().getId()).build());
        textProcessingService.processText(text , articleDto.getId());
        return articleDto;
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deleteArticle(@AuthenticationPrincipal CustomUserDetails user , @PathVariable UUID postId){
        return articleService.delete(user.getAppUser() , postId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(403).build();
    }
}
