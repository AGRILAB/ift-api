package fr.gouv.agriculture.ift.util.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.OutputStream;

@Component
public class PDFGeneratorUtil {

    @Autowired
    private TemplateEngine templateEngine;

    public void createPdf(OutputStream out, Context ctx, String templateName, byte[] qrCode) throws Exception {
        byte[] processedHtmlBytes = templateEngine.process(templateName, ctx).getBytes();
        new PDFWriter(out, processedHtmlBytes, "templates/", qrCode).write();
    }
}