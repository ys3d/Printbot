package com.github.ys3d.printbot.printingJob;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.print.*;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests {@link PrintingJob}
 * @author Daniel Schild
 */
public class PrintingJobTest {

    @Test
    public void findPrintServiceEmptyResultTest() {
        try (MockedStatic<PrintServiceLookup> dummy = Mockito.mockStatic(PrintServiceLookup.class)) {
            dummy.when(() -> PrintServiceLookup.lookupPrintServices(null, null))
                    .thenReturn(new PrintService[0]);
            PrintingJob pj = new TestPrintingJob("PName");

            assertNull(pj.service);

            dummy.verify(() -> PrintServiceLookup.lookupPrintServices(eq(null), eq(null)));
        }
    }

    @Test
    public void findPrintServiceOnePrinterResultTest() {
        try (MockedStatic<PrintServiceLookup> dummy = Mockito.mockStatic(PrintServiceLookup.class)) {
            dummy.when(() -> PrintServiceLookup.lookupPrintServices(null, null))
                    .thenReturn(new PrintService[]{new TestPrintService("PName")});
            PrintingJob pj = new TestPrintingJob("PName");

            assertEquals(new TestPrintService("PName"), pj.service);

            dummy.verify(() -> PrintServiceLookup.lookupPrintServices(eq(null), eq(null)));
        }
    }

    @Test
    public void findPrintServiceManyPrinterResultTest() {
        PrintService[] ps = new PrintService[25];
        for(int i = 0; i < 25; i++) {
            ps[i] = new TestPrintService("PName" + i);
        }

        try (MockedStatic<PrintServiceLookup> dummy = Mockito.mockStatic(PrintServiceLookup.class)) {
            dummy.when(() -> PrintServiceLookup.lookupPrintServices(null, null))
                    .thenReturn(ps);
            PrintingJob pj = new TestPrintingJob("PName21");

            assertEquals(new TestPrintService("PName21"), pj.service);

            dummy.verify(() -> PrintServiceLookup.lookupPrintServices(eq(null), eq(null)));
        }
    }

    @Test
    public void initWithServiceTest() {
        PrintService ps = new TestPrintService("name");
        assertEquals(ps, new TestPrintingJob(ps).service);
    }

    private static class TestPrintingJob extends PrintingJob {
        protected TestPrintingJob(String printerName) {
            super(printerName);
        }

        protected TestPrintingJob(PrintService service) {
            super(service);
        }

        @Override
        public void execute(){
        }
    }

    private static class TestPrintService implements PrintService {
        private final String name;

        private TestPrintService(String name) {
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
}
