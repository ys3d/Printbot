package com.github.ys3d.printbot.printingJob;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.print.*;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests {@link PDFPrintingJob}
 * @author Daniel Schild
 */
public class PDFPrintingJobTest {

    @Test
    public void constructNamePath() throws IOException {
        try (MockedStatic<PrintServiceLookup> dummyPSLookup = Mockito.mockStatic(PrintServiceLookup.class)) {
            try (MockedStatic<PDDocument> dummyPD = Mockito.mockStatic(PDDocument.class)) {
                PrintService service = new TestPrintService("PName");
                dummyPSLookup.when(() -> PrintServiceLookup.lookupPrintServices(null, null))
                        .thenReturn(new PrintService[]{service});
                PDDocument doc = new PDDocument();
                dummyPD.when(() -> PDDocument.load(new File("FPath")))
                        .thenReturn(doc);
                PDFPrintingJob job = new PDFPrintingJob("PName", "FPath");

                assertEquals(service, job.service);
                assertEquals(doc, job.document);

                dummyPSLookup.verify(() -> PrintServiceLookup.lookupPrintServices(eq(null), eq(null)));
                dummyPD.verify(() -> PDDocument.load(eq(new File("FPath"))));
            }
        }
    }

    @Test
    public void constructServicePath() throws IOException {
        try (MockedStatic<PDDocument> dummyPD = Mockito.mockStatic(PDDocument.class)) {
            PrintService service = new TestPrintService("PName");
            PDDocument doc = new PDDocument();
            dummyPD.when(() -> PDDocument.load(new File("FPath")))
                    .thenReturn(doc);
            PDFPrintingJob job = new PDFPrintingJob(service, "FPath");

            assertEquals(service, job.service);
            assertEquals(doc, job.document);

            dummyPD.verify(() -> PDDocument.load(eq(new File("FPath"))));
        }
    }

    @Test
    public void constructServiceStream() throws IOException {
        try (MockedStatic<PDDocument> dummyPD = Mockito.mockStatic(PDDocument.class)) {
            PrintService service = new TestPrintService("PName");
            PDDocument doc = new PDDocument();
            InputStream stream = new PipedInputStream();
            dummyPD.when(() -> PDDocument.load(stream))
                    .thenReturn(doc);
            PDFPrintingJob job = new PDFPrintingJob(service, stream);

            assertEquals(service, job.service);
            assertEquals(doc, job.document);

            dummyPD.verify(() -> PDDocument.load(eq(stream)));
        }
    }

    @Test
    public void constructNameStream() throws IOException {
        try (MockedStatic<PrintServiceLookup> dummyPSLookup = Mockito.mockStatic(PrintServiceLookup.class)) {
            try (MockedStatic<PDDocument> dummyPD = Mockito.mockStatic(PDDocument.class)) {
                PrintService service = new TestPrintService("PName");
                dummyPSLookup.when(() -> PrintServiceLookup.lookupPrintServices(null, null))
                        .thenReturn(new PrintService[]{service});
                PDDocument doc = new PDDocument();
                InputStream stream = new PipedInputStream();
                dummyPD.when(() -> PDDocument.load(stream))
                        .thenReturn(doc);
                PDFPrintingJob job = new PDFPrintingJob("PName", stream);

                assertEquals(service, job.service);
                assertEquals(doc, job.document);

                dummyPSLookup.verify(() -> PrintServiceLookup.lookupPrintServices(eq(null), eq(null)));
                dummyPD.verify(() -> PDDocument.load(eq(stream)));
            }
        }
    }

    @Test
    public void closeDocumentTest() throws IOException {
        PDDocument doc = mock(PDDocument.class);
        try (MockedStatic<PDDocument> dummyPD = Mockito.mockStatic(PDDocument.class)) {
            PrintService service = new TestPrintService("PName");
            dummyPD.when(() -> PDDocument.load(new File("FPath")))
                    .thenReturn(doc);
            PDFPrintingJob job = new PDFPrintingJob(service, "FPath");
            job.close();
        }
        verify(doc, times(1)).close();
    }

    @Test
    public void executeTest() throws IOException, PrinterException {
        PDFPrintingJob job;
        PDDocument doc = mock(PDDocument.class);
        try (MockedStatic<PDDocument> dummyPD = Mockito.mockStatic(PDDocument.class)) {
            PrintService service = new TestPrintService("PName");
            dummyPD.when(() -> PDDocument.load(new File("FPath")))
                    .thenReturn(doc);
            job = new PDFPrintingJob(service, "FPath");
        }
        PrinterJob mockPJob = mock(PrinterJob.class);
        try (MockedStatic<PrinterJob> dummyPJ = Mockito.mockStatic(PrinterJob.class)) {
            dummyPJ.when(PrinterJob::getPrinterJob).thenReturn(mockPJob);
            job.execute();
        }
        verify(mockPJob, times(1)).setPageable(any());
        verify(mockPJob, times(1)).setPrintService(job.service);
        verify(mockPJob, times(1)).print();
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
            if (!(obj instanceof PDFPrintingJobTest.TestPrintService)) {
                return false;
            }
            PDFPrintingJobTest.TestPrintService ps = (PDFPrintingJobTest.TestPrintService) obj;
            return this.name.equals(ps.getName());
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
