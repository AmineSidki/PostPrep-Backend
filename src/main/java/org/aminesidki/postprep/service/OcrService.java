package org.aminesidki.postprep.service;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class OcrService {
    private final Tesseract tesseract;
    private final TextObjectFactory textObjectFactory;
    private final LanguageDetector languageDetector;

    public String scannedDocument(MultipartFile pdfFile){
        try(PDDocument document = PDDocument.load(pdfFile.getInputStream())){
            //2-passes : 1st using english and arabic to get all letters of all alphabets

            tesseract.setLanguage("eng+ara");

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            StringBuilder textOutput = new StringBuilder();

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

                String result = tesseract.doOCR(image);
                textOutput.append(result).append("\n");
            }

            TextObject textObject = textObjectFactory.forText(textOutput.toString());
            Optional<LdLocale> lang = languageDetector.detect(textObject);

            //Once we have the language- hopefully -we reparse it with the correct one
            if(lang.isPresent()){
                Locale locale = new Locale(lang.get().getLanguage());
                tesseract.setLanguage(locale.getISO3Language());
            }else{
                tesseract.setLanguage("eng");
            }

            textOutput = new StringBuilder();
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

                String result = tesseract.doOCR(image);
                textOutput.append(result).append("\n");
            }

            return textOutput.toString();
        }catch (IOException e){
            throw new RuntimeException("IO Failure on file : " + pdfFile.getName());
        } catch (TesseractException e) {
            throw new RuntimeException("OCR Failure on file : " + pdfFile.getName());
        }
    }

}
