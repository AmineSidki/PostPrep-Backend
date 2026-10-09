package org.aminesidki.postprep.dto.regular;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public record AiAnalysisResultDTO(
        String title,
        String cleanedContent,
        String language,
        String summary,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<String> keywords,
        String seoTitle,
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<String> categories
) {
}
