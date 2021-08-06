package com.github.ys3d.printbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * PrintBot main class
 * @author Daniel Schild
 */
public final class PrintBot {
    private static final String STANDARD_PROPERTIES_FILE_NAME = "bot.properties";
    private static final String STANDARD_WHITELIST_FILE_NAME = "whitelist.txt";

    private static Properties uiLanguage;
    private static String botToken;
    private static String printerName;
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

        botToken = basicProps.getProperty("token");
        printerName = basicProps.getProperty("printerName");
        String langFile = basicProps.getProperty("langFile");

        // Load lang
        uiLanguage = new Properties();
        uiLanguage.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(langFile));

        // Initialize Bot and register for updates
        bot = new TelegramBot(botToken);
        bot.setUpdatesListener(new PrintingListener());
        System.out.println("%%%%%%%%% Init with following parameters %%%%%%%%%");
        System.out.println("  printer-name:  " + printerName);
        System.out.println("  language-file: " + langFile);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    public static Whitelist getWhiteList() { return whiteList; }

    public static TelegramBot getBot() { return bot; }

    public static Properties getLang() { return uiLanguage; }

    public static String getBotToken() { return botToken; }

    public static String getPrinterName() { return printerName; }

    /**
     * Sends a message to the chat with the given id
     * @param chatId The id of the chat
     * @param msg The message
     */
    public static boolean sendMsg(long chatId, String msg) { return PrintBot.getBot().execute(new SendMessage(chatId, msg)).isOk(); }
}
