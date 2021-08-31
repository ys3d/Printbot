package com.github.ys3d.printbot.updateProcessing;

import com.github.ys3d.printbot.PrintBot;
import com.github.ys3d.printbot.printingJob.PDFPrintingJob;
import com.github.ys3d.printbot.updateProcessing.response.*;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;

import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handleStates and Process updates fetched from the telegram bot-API.
 * Updates ar not fetched directly in this class, but given via the {@link #handle(List)}-function.
 *
 * All communication is performed via elements of the {@link Response} interface
 *
 * @author Daniel Schild
 */
public class UpdateProcessor {
    private final static String TELEGRAM_FILE_URL = "https://api.telegram.org/file/bot";

    private final Map<Long, UpdateProcessor.ChatState> chatStates = new HashMap<>();
    private final Map<Long, String> printerNames = new HashMap<>();


    /**
     * Handles updates
     * @param list All updates that should be handled
     * @return Several {@link Response}s for the given {@link Update}s
     */
    public Response[] handle(List<Update> list) {
        List<Response> responses = new ArrayList<>();
        for (Update update : list) {
            if (PrintBot.isWhitelisted(update)) {
                long chatId;
                if (update.message() != null) {
                    chatId = update.message().from().id();

                    // update.message().document() == null signalizes that no file was sent with the message
                    // It should be checked first if a document is contained in the message because text can also be sent as caption of document,
                    // but should not be analysed then
                    if (update.message().document() != null) {
                        UpdateProcessor.ChatState cs = chatStates.getOrDefault(chatId, UpdateProcessor.ChatState.UNDEFINED);
                        if (cs == UpdateProcessor.ChatState.WAITING_FOR_PDF) {
                            try {
                                responses.add(printPDFFile(update.message(), chatId));
                            } catch (IOException | PrinterException e) {
                                responses.add(new ErrorResponse(chatId, PrintBot.getLang().getProperty("print.fail")));
                            } finally {
                                chatStates.remove(chatId);
                            }
                        } else {
                            responses.add(new QuietErrorResponse("Document not expected"));
                        }
                    } else if (update.message().text() != null) {
                        System.out.println(chatId + ": Message \"" + update.message().text() + "\"");
                        // Switch for all possible commands
                        // In Telegram commands are written with a / + a keyword

                        switch (update.message().text()) {
                            case "/help":
                                responses.add(helpUpdate(chatId));
                                break;
                            case "/printpdf":
                                responses.add(printPDFUpdate(chatId));
                                break;
                            case "/cancel":
                                responses.add(cancelUpdate(chatId));
                                break;
                            default:
                                responses.add(new TextResponse(chatId, PrintBot.getLang().getProperty("unknown.command")));
                                break;
                        }
                    }
                } else if (update.callbackQuery() != null) {
                    chatId = update.callbackQuery().from().id();
                    System.out.println(chatId + ": Callback \"" + update.callbackQuery().data() + "\"");
                    responses.add(new DeleteMessageResponse(chatId, update.callbackQuery().message().messageId()));
                    if (chatStates.get(update.callbackQuery().from().id()) != UpdateProcessor.ChatState.WAITING_FOR_PRINTER) {
                        responses.add(new QuietErrorResponse("Invalid Callback"));
                    } else {
                        String printerName = update.callbackQuery().data();
                        responses.add(new TextResponse(chatId, MessageFormat.format(PrintBot.getLang().getProperty("print.printer.selected"), printerName)));
                        chatStates.replace(chatId, UpdateProcessor.ChatState.WAITING_FOR_PDF);
                        printerNames.put(chatId, printerName);
                        responses.add(new TextResponse(chatId, PrintBot.getLang().getProperty("print.enabled")));
                    }
                }
            } else if (update.message() != null) {
                responses.add(new QuietErrorResponse("Illegal telegram user: " + update.message().from().id()));
            }
        }

        return responses.toArray(new Response[0]);
    }

    /**
     * Update to the chat that should be done on /cancel command
     *
     * @param chatId The chat-id
     * @return The {@link Response}
     */
    private Response cancelUpdate(long chatId) {
        printerNames.remove(chatId);
        if (chatStates.getOrDefault(chatId, UpdateProcessor.ChatState.UNDEFINED) != UpdateProcessor.ChatState.UNDEFINED && chatStates.remove(chatId) != null)
            return new TextResponse(chatId, PrintBot.getLang().getProperty("print.cancel"));
        else return new TextResponse(chatId, PrintBot.getLang().getProperty("print.cancel.no.job"));
    }

    /**
     * Update to the chat that should be done on /help command
     *
     * @param chatId The chat-id
     * @return The {@link Response}
     */
    private Response helpUpdate(long chatId) {
        return new HelpResponse(chatId);
    }

    /**
     * Update to the chat that should be done on /printPDF command
     *
     * @param chatId The chat-id
     * @return The {@link Response}
     */
    private Response printPDFUpdate(long chatId) {
        chatStates.put(chatId, UpdateProcessor.ChatState.WAITING_FOR_PRINTER);
        SendMessage sendMessage = new SendMessage(chatId, PrintBot.getLang().getProperty("print.printer.select"));
        sendMessage.replyMarkup(getPrinterSelectKeyboard(PrintBot.getPrinterName())).allowSendingWithoutReply(false);
        return new MessageResponse(sendMessage);
    }

    /**
     * Prints pdf-file and sends matching confirmation-messages to the chat
     * If the file isn't a pdf-file, an exception is sent to the users-chat
     *
     * @param message The message containing the file
     * @param chatId  The id of the chat to write on
     * @throws IOException      If loading the pdf-document fails
     * @throws PrinterException If printing the file fails
     * @return The {@link Response}
     */
    private Response printPDFFile(Message message, long chatId) throws IOException, PrinterException {
        // Check if file is a pdf
        if (!message.document().mimeType().equals("application/pdf")) {
            return new ErrorResponse(chatId, PrintBot.getLang().getProperty("illegal.file.type"));
        }

        GetFile request = new GetFile(message.document().fileId());
        GetFileResponse getFileResponse = PrintBot.getBot().execute(request);

        String urlS = TELEGRAM_FILE_URL + PrintBot.getBotToken() + "/" + getFileResponse.file().filePath();
        BufferedInputStream inStream = new BufferedInputStream(new URL(urlS).openStream());
        PDFPrintingJob job = new PDFPrintingJob(printerNames.remove(chatId), inStream);
        job.execute();
        return new TextResponse(chatId, PrintBot.getLang().getProperty("print.success"));
    }

    /**
     * Returns the Keyboard to select one of the given printers by their name.
     * The Keyboard could be used in a Telegram-Chat afterwards.
     *
     * @param printerNames The names of the printers
     * @return The Keyboard
     */
    private InlineKeyboardMarkup getPrinterSelectKeyboard(String[] printerNames) {
        InlineKeyboardMarkup toReturn = new InlineKeyboardMarkup();
        for (String printer : printerNames) {
            toReturn.addRow(new InlineKeyboardButton(printer).callbackData(printer));
        }
        return toReturn;
    }

    /**
     * States for Telegram-Chats
     *
     * @author Daniel Schild
     */
    private enum ChatState {
        WAITING_FOR_PRINTER,
        WAITING_FOR_PDF,
        UNDEFINED
    }
}
