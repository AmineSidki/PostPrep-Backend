package org.aminesidki.postprep.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public record AiAnalysisResult(
        String title,
        String cleanedContent,
        String language,
        String summary,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<String> keywords,
        String seoTitle,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<String> categories
) {}
