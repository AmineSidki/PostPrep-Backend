package org.aminesidki.postprep.config;

import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObjectFactory;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {
    @Bean
    public Tesseract tesseract(){
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(System.getenv("TESSERACT_DIR"));

        return tesseract;
    }

    @Bean
    public LanguageProfileReader languageProfileReader(){
        return new LanguageProfileReader();
    }

    @Bean
    public TextObjectFactory textObjectFactory(){
        return CommonTextObjectFactories.forDetectingOnLargeText();
    }
}
