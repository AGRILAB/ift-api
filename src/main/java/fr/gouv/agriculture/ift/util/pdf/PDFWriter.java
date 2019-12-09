package fr.gouv.agriculture.ift.util.pdf;

import com.lowagie.text.DocumentException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;

public class PDFWriter {

    /**
     * Le flux de sortie.
     */
    private final OutputStream output;
    /**
     * Les sources HTML.
     */
    private final byte[] source;
    /**
     * Le chemin d'accès aux resources (images) dans le classpath
     **/
    private final String resourcePath;
    /**
     * La génération a-t-elle été lancée ?
     */
    private boolean hasRun = false;
    /**
     * Le renderer.
     */
    private ITextRenderer renderer;

    private final byte[] qrCode;

    /**
     * Creates a new PDFWriter.
     *
     * @param aOutput the stream to which the result is to be written.
     * @param aSource the source html page that is to be converted into a PDF document.
     */
    public PDFWriter(OutputStream aOutput, byte[] aSource, String resourcePath, byte[] qrCode) {
        this.output = aOutput;
        this.source = aSource;
        this.resourcePath = resourcePath;
        this.qrCode = qrCode;
        setupRenderer();
    }

    /**
     * Génère le PDF.
     */
    private void doWrite() throws DocumentException {
        setDocument();
        renderer.createPDF(output, false);
        renderer.finishPDF();
    }

    /**
     * Initialise les documents.
     */
    private void setDocument() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(source);
        Document document = XMLResource.load(inputStream).getDocument();
        renderer.setDocument(document, this.resourcePath);
        renderer.layout();
    }

    /**
     * Initialise le renderer.
     */
    private void setupRenderer() {
        renderer = new ITextRenderer();
        renderer.getSharedContext().setUserAgentCallback(
                new ResourceUserAgentCallback(renderer.getOutputDevice(), PDFWriter.class));
        renderer.getSharedContext().setReplacedElementFactory(
                new ImageReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory(), resourcePath));
        renderer.getSharedContext().setReplacedElementFactory(
                new QrCodeImageReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory(), resourcePath, qrCode));
    }

    /**
     * Lance la génération des PDF.
     *
     * @return un PDFWriter
     */
    public PDFWriter write() throws DocumentException {
        if (!hasRun) {
            doWrite();
            hasRun = true;
        }
        return this;
    }
}