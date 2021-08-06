package com.github.ys3d.printbot.printingJob;

import java.awt.print.PrinterException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;


/**
 * Job that can be executed on a printer.
 * @author Daniel Schild
 */
abstract class PrintingJob {
    protected final PrintService service;

    /**
     * Initializes a new PrintingJob.
     * @param printerName The name of the printer
     */
    protected PrintingJob(String printerName) {
        this.service = findPrintService(printerName);
    }
    /**
     * Initializes a new PrintingJob.
     * @param service The service of the printer
     */

    protected PrintingJob(PrintService service) {
        this.service = service;
    }

    /**
     * Executes the PrintingJob.
     */
    public abstract void execute() throws PrinterException;

    /**
     * Returns the {@link PrintService} for the given printer-name.
     * @param printerName The name of the searched printer
     * @return The {@link PrintService} or null if no printer was found with the given name
     */
    protected PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
}
