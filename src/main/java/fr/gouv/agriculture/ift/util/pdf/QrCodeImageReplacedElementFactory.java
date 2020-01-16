package fr.gouv.agriculture.ift.util.pdf;

import com.google.common.io.BaseEncoding;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.IOException;
import java.io.InputStream;

public class QrCodeImageReplacedElementFactory implements ReplacedElementFactory {

    public static final int BASE64_SRC_PREFIX = 23;
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(QrCodeImageReplacedElementFactory.class.getName());

    private final ReplacedElementFactory superFactory;
    private final String resourcePath;
    private final byte[] qrCode;

    public QrCodeImageReplacedElementFactory(ReplacedElementFactory superFactory, String resourcePath, byte[] qrCode) {
        this.superFactory = superFactory;
        this.resourcePath = resourcePath;
        this.qrCode = qrCode;
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox,
                                                 UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {

        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }

        String nodeName = element.getNodeName();

        if ("img".equals(nodeName)) {
            String src = element.getAttribute("src");
            byte[] bytes = null;

            if (src.contains("QRCODE")) {

                if (src.contains("data:")) {
                    String base64Image = src.substring(BASE64_SRC_PREFIX);
                    bytes = BaseEncoding.base64().decode(base64Image.replaceAll("\\s", ""));

                } else {
                    bytes = qrCode;
                }
            } else {

                InputStream input = null;
                try {
                    input = this.getClass().getClassLoader().getResourceAsStream(this.resourcePath + src);
                    bytes = IOUtils.toByteArray(input);
                } catch (IOException e) {
                    log.error(e);
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }

            if (bytes != null) {
                return getImage(bytes, cssWidth, cssHeight);
            }
        }

        return superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
    }

    private ReplacedElement getImage(byte[] bytes, int cssWidth, int cssHeight) {
        try {

            Image image = Image.getInstance(bytes);
            FSImage fsImage = new ITextFSImage(image);

            if (fsImage != null) {
                if ((cssWidth != -1) || (cssHeight != -1)) {
                    fsImage.scale(cssWidth, cssHeight);
                }
                return new ITextImageElement(fsImage);
            }

        } catch (BadElementException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public void reset() {
        superFactory.reset();
    }

    @Override
    public void remove(Element e) {
        superFactory.remove(e);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        superFactory.setFormSubmissionListener(listener);
    }
}
