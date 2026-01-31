package org.aminesidki.postprep.config;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
public class LanguageDetectorConfig {
    private final LanguageProfileReader languageProfileReader;

    @Bean
    public LanguageDetector languageDetector() throws IOException {
        return LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfileReader.readAllBuiltIn())
                .build();
    }
}
