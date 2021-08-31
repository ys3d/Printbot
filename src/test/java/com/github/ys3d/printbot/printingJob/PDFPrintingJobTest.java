package com.github.ys3d.printbot.printingJob;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.print.*;

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
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

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
        assertEquals("Printing PDF-Job", systemOutRule.getLog().trim());
    }
}
