package org.aminesidki.postprep.controller;

import lombok.RequiredArgsConstructor;
import org.aminesidki.postprep.dto.ArticleDTO;
import org.aminesidki.postprep.service.ArticleService;
import org.aminesidki.postprep.service.OcrService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class ArticleController {
    private final ArticleService articleService;
    private final OcrService ocrService;

    @PostMapping("/pdf")
    public ArticleDTO uploadPdf(@RequestBody MultipartFile pdfFile) throws IOException {
        System.out.println(ocrService.scannedDocument(pdfFile));
        return null;
    }
}
