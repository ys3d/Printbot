package de.ohrhusten.printbot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import de.ohrhusten.printbot.printingJob.PDFPrintingJob;

import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Listener for updates from the telegram bot-api.
 * @author Daniel Schild
 */
public class PrintingListener implements UpdatesListener {
    private final Set<Long> waitingForFile = new HashSet<>();
    private final Map<Long, String> waitingFileType = new HashMap<>();

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            long chatId = update.message().chat().id();
            if (update.message().text() != null) {
                switch (update.message().text()) {
                    case "/help":
                        helpUpdate(chatId);
                        break;
                    case "/printPDF":
                        printPDFUpdate(chatId);
                        break;
                    case "/cancel":
                        cancelUpdate(chatId);
                        break;
                    default:
                        PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("unknownCommand"));
                        break;
                }
            }
            else if (update.message().document() != null){
                String fileType = waitingFileType.get(chatId);
                if (fileType != null) {
                    switch (fileType) {
                        case "pdf":
                            printPDFFile(update.message(), chatId);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + fileType);
                    }
                }

            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Update to the chat that should be done on /cancel command
     * @param chatId The chat-id
     */
    private void cancelUpdate(long chatId) {
        if (waitingForFile.remove(chatId)) PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("printCancel"));
        else PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("printCancelNoJob"));
    }

    /**
     * Update to the chat that should be done on /help command
     * @param chatId The chat-id
     */
    private void helpUpdate(long chatId) {
        PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("help"));
    }

    /**
     * Update to the chat that should be done on /printPDF command
     * @param chatId The chat-id
     */
    private void printPDFUpdate(long chatId) {
        waitingForFile.add(chatId);
        waitingFileType.put(chatId, "pdf");
        PrintBot.sendMsg(chatId, PrintBot.getLang().getProperty("printEnabled"));
    }

    /**
     * Prints pdf-file and sends matching confirmation-messages to the chat
     * If the file isn't a pdf-file, a exception is send to the users-chat
     * @param message The message containing the file
     * @param chatId The id of the chat to write on
     */
    private void printPDFFile(Message message, long chatId) {
        if (!message.document().mimeType().equals("application/pdf")) {
            PrintBot.sendMsg(chatId, "Wait am Moment thats illegal");
            return;
        }

        GetFile request = new GetFile(message.document().fileId());
        GetFileResponse getFileResponse = PrintBot.getBot().execute(request);

        String urlS = "https://api.telegram.org/file/bot" + PrintBot.getBotToken() + "/" + getFileResponse.file().filePath();
        try {
            BufferedInputStream inStream = new BufferedInputStream(new URL(urlS).openStream());
            PDFPrintingJob job = new PDFPrintingJob(PrintBot.getPrinterName(), (InputStream) inStream);
            job.execute();
        } catch (IOException | PrinterException e) {
            e.printStackTrace();
        }

    }
}
