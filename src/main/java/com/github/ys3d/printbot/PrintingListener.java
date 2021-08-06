package com.github.ys3d.printbot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.github.ys3d.printbot.printingJob.PDFPrintingJob;

import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Listener for updates from the telegram bot-api.
 * @author Daniel Schild
 */
public class PrintingListener implements UpdatesListener {
    private final Map<Long, String> waitingWithFileType = new HashMap<>();

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            if (update.message() != null && PrintBot.getWhiteList().isWhitelisted(update.message().from().id() + "")) {
                long chatId = update.message().chat().id();

                // update.message().document() == null signalizes that no file was send with the message
                // It should be checked first if a document is contained in the message because text can also be send as caption of document,
                // but should not be analysed then

                if (update.message().document() != null) {
                    String fileType = waitingWithFileType.get(chatId);
                    if (fileType != null) {
                        switch (fileType) {
                            case "pdf":
                                try {
                                    printPDFFile(update.message(), chatId);
                                    PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("print.success"));
                                } catch (IOException | PrinterException e) {
                                    e.printStackTrace();
                                    PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("print.fail"));
                                }
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + fileType);
                        }
                    }

                } else if (update.message().text() != null) {
                    System.out.println("Got Message: \"" + update.message().text() + "\"");
                    // Switch for all possible commands
                    switch (update.message().text()) {
                        case "/help":
                            helpUpdate(chatId);
                            break;
                        case "/printpdf":
                            printPDFUpdate(chatId);
                            break;
                        case "/cancel":
                            cancelUpdate(chatId);
                            break;
                        default:
                            PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("unknown.command"));
                            break;
                    }
                }
            }
            else if (update.message() != null){
                System.out.println("Illegal telegram user: " + update.message().from().id());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Update to the chat that should be done on /cancel command
     *
     * @param chatId The chat-id
     */
    private void cancelUpdate(long chatId) {
        if (waitingWithFileType.remove(chatId) != null)
            PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("print.cancel"));
        else PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("print.cancel.no.job"));
    }

    /**
     * Update to the chat that should be done on /help command
     *
     * @param chatId The chat-id
     */
    private void helpUpdate(long chatId) {
        PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("help"));
    }

    /**
     * Update to the chat that should be done on /printPDF command
     *
     * @param chatId The chat-id
     */
    private void printPDFUpdate(long chatId) {
        waitingWithFileType.put(chatId, "pdf");
        PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("print.enabled"));
    }

    /**
     * Prints pdf-file and sends matching confirmation-messages to the chat
     * If the file isn't a pdf-file, a exception is send to the users-chat
     *
     * @param message The message containing the file
     * @param chatId  The id of the chat to write on
     * @throws IOException If loading the pdf-document fails
     * @throws PrinterException If printing the file fails
     */
    private void printPDFFile(Message message, long chatId) throws IOException, PrinterException {
        // Check if file is a pdf
        if (!message.document().mimeType().equals("application/pdf")) {
            PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("illegal.file.type"));
            return;
        }

        GetFile request = new GetFile(message.document().fileId());
        GetFileResponse getFileResponse = PrintBot.getBot().execute(request);

        String urlS = "https://api.telegram.org/file/bot" + PrintBot.getBotToken() + "/" + getFileResponse.file().filePath();
        BufferedInputStream inStream = new BufferedInputStream(new URL(urlS).openStream());
        PDFPrintingJob job = new PDFPrintingJob(PrintBot.getPrinterName(), inStream);
        job.execute();

    }
}
