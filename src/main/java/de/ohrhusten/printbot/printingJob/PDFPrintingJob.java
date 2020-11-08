package de.ohrhusten.printbot.printingJob;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Job for Printing PDFs
 */
public class PDFPrintingJob extends PrintingJob {
    private final PDDocument document;

    /**
     * Creates new {@link PDFPrintingJob}
     * @param printerName The name of the printer
     * @param pdfPath The path to the pdf file
     */
    public PDFPrintingJob(String printerName, String pdfPath) throws IOException {
        super(printerName);
        this.document = PDDocument.load(new File(pdfPath));
    }

    /**
     * Creates new {@link PDFPrintingJob}
     * @param service The {@link PrintService} the pdf should be executed on
     * @param pdfPath The path to the pdf file
     */
    public PDFPrintingJob(PrintService service, String pdfPath) throws IOException {
        super(service);
        this.document = PDDocument.load(new File(pdfPath));

    }

    /**
     * Creates new {@link PDFPrintingJob}
     * @param service The {@link PrintService} the pdf should be executed on
     * @param inStream InputStream of the pdf file
     */
    public PDFPrintingJob(PrintService service, InputStream inStream) throws IOException {
        super(service);
        this.document = PDDocument.load(inStream);
    }

    /**
     * Creates new {@link PDFPrintingJob}
     * @param printerName The name of the printer
     * @param inStream InputStream of the pdf file
     */
    public PDFPrintingJob(String printerName, InputStream inStream) throws IOException {
        super(printerName);
        this.document = PDDocument.load(inStream);
    }

    @Override
    public void execute() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(super.service);
        job.print();
    }

    /**
     * Closes all resources
     * @throws IOException If closing the document fails
     */
    public void close() throws IOException {
        document.close();
    }
}
