package fr.gouv.agriculture.ift.util.pdf;

import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.CSSResource;

/**
 * @author jboulay
 */
public class ResourceUserAgentCallback extends ITextUserAgent {

    /** La classe de ressource. */
    private final Class resourceClass;

    /**
     * @param outputDevice le device de sortie
     * @param aResourceClass la classe de ressource
     */
    @SuppressWarnings("rawtypes")
    public ResourceUserAgentCallback(ITextOutputDevice outputDevice, Class aResourceClass) {
        super(outputDevice);
        this.resourceClass = aResourceClass;
    }

    @Override
    public CSSResource getCSSResource(String uri) {
        return new CSSResource(resourceClass.getClassLoader().getResourceAsStream(uri));
    }

}