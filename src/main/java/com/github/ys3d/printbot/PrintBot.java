package com.github.ys3d.printbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * PrintBot main class
 *
 * @author Daniel Schild
 */
public final class PrintBot {
    private static final String STANDARD_PROPERTIES_FILE_NAME = "bot.properties";
    private static final String STANDARD_WHITELIST_FILE_NAME = "whitelist.txt";

    private static Properties uiLanguage;
    private static String botToken;
    private static String[] printerName;
    private static TelegramBot bot;
    private static Whitelist whiteList;

    private PrintBot() {
    }

    public static void main(String[] args) throws IOException {
        // Load props
        Properties basicProps = new Properties();
        if (args != null && args.length >= 1) {
            basicProps.load(new FileInputStream(args[0]));
        } else {
            basicProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(STANDARD_PROPERTIES_FILE_NAME));
        }


        if (basicProps.containsKey("whitelist")) {
            whiteList = new Whitelist(new FileInputStream(basicProps.getProperty("whitelist")));
        } else {
            whiteList = new Whitelist(Thread.currentThread().getContextClassLoader().getResourceAsStream(STANDARD_WHITELIST_FILE_NAME));
        }

        boolean multiPrinterMode = basicProps.containsKey("multiPrinterSeparator");

        botToken = basicProps.getProperty("token");
        String langFile = basicProps.getProperty("langFile");
        if (multiPrinterMode) {
            String separator = basicProps.getProperty("multiPrinterSeparator");
            printerName = basicProps.getProperty("printerName").split(separator);
        } else {
            printerName = new String[1];
            printerName[0] = basicProps.getProperty("printerName");
        }

        // Load lang
        uiLanguage = new Properties();
        uiLanguage.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(langFile));

        // Initialize Bot and register for updates
        bot = new TelegramBot(botToken);
        bot.setUpdatesListener(new PrintingListener());

        //Print init message
        System.out.println("%%%%%%%%% Init with following parameters %%%%%%%%%");
        System.out.println("  multi printer mode:  " + multiPrinterMode);
        System.out.print("  printer-name:        ");
        for (int i = 0; i < printerName.length; i++) {
            if (i != 0) System.out.print("                       ");
            System.out.println(printerName[i]);
        }
        System.out.println("  language-file:       " + langFile);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    /**
     * Checks if a given {@link Update} is from a whitelisted Telegram-user
     *
     * @param update The update to check
     * @return True, if the user is correctly whitelisted
     */
    public static boolean isWhitelisted(Update update) {
        long id;
        if (update.message() != null) {
            id = update.message().from().id();
        } else if (update.callbackQuery() != null) {
            id = update.callbackQuery().from().id();
        } else {
            return false;
        }
        return getWhiteList().isWhitelisted(id + "");
    }

    public static Whitelist getWhiteList() {
        return whiteList;
    }

    public static TelegramBot getBot() {
        return bot;
    }

    public static Properties getLang() {
        return uiLanguage;
    }

    public static String getBotToken() {
        return botToken;
    }

    public static String[] getPrinterName() {
        return printerName;
    }

    /**
     * Sends a message to the chat with the given id
     *
     * @param chatId The id of the chat
     * @param msg    The message
     * @return True, if the operation finished successfully
     */
    public static boolean sendMsg(long chatId, String msg) {
        return PrintBot.getBot().execute(new SendMessage(chatId, msg)).isOk();
    }

    /**
     * Sends a message
     *
     * @param sendMessage The {@link SendMessage} that will be executed by the {@link TelegramBot}
     * @return True, if the operation finished successfully
     */
    public static boolean sendMsg(SendMessage sendMessage) {
        return PrintBot.getBot().execute(sendMessage).isOk();
    }

    /**
     * Delete a message
     *
     * @param chatId    The chat-id, of the chat in which the message is contained
     * @param messageId The id of the message that should be deleted
     * @return True, if the operation finished successfully
     */
    public static boolean deleteMsg(long chatId, int messageId) {
        return PrintBot.getBot().execute(new DeleteMessage(chatId, messageId)).isOk();
    }
}
