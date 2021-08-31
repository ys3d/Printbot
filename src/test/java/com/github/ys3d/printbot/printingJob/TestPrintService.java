package com.github.ys3d.printbot.printingJob;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

/**
 * Class implementing {@link PrintService} interface.
 * Class can be used to test functions using {@link PrintService}-instances and identifying them by their name.
 *
 * @author Daniel Schild
 */
class TestPrintService implements PrintService {
    private final String name;

    TestPrintService(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DocPrintJob createPrintJob() {
        return null;
    }

    @Override
    public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) {
    }

    @Override
    public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) {
    }

    @Override
    public PrintServiceAttributeSet getAttributes() {
        return null;
    }

    @Override
    public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
        return null;
    }

    @Override
    public DocFlavor[] getSupportedDocFlavors() {
        return new DocFlavor[0];
    }

    @Override
    public boolean isDocFlavorSupported(DocFlavor flavor) {
        return false;
    }

    @Override
    public Class<?>[] getSupportedAttributeCategories() {
        return new Class[0];
    }

    @Override
    public boolean isAttributeCategorySupported(Class<? extends Attribute> category) {
        return false;
    }

    @Override
    public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
        return null;
    }

    @Override
    public Object getSupportedAttributeValues(Class<? extends Attribute> category, DocFlavor flavor, AttributeSet attributes) {
        return null;
    }

    @Override
    public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor, AttributeSet attributes) {
        return false;
    }

    @Override
    public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) {
        return null;
    }

    @Override
    public ServiceUIFactory getServiceUIFactory() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestPrintService)) {
            return false;
        }
        TestPrintService ps = (TestPrintService) obj;
        return this.name.equals(ps.getName());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
