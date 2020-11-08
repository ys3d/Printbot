package de.ohrhusten.printbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import javax.print.PrintService;
import java.io.FileInputStream;
import java.util.Properties;

public class PrintBot {
    private static Properties uiLanguage;
    private static String botToken = "";
    private static String printerName = "";
    private static TelegramBot bot;


    public static void main(String[] args) throws Exception {
        // Load props
        Properties basicProps = new Properties();
        basicProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot.properties"));
        botToken = basicProps.getProperty("token");
        printerName = basicProps.getProperty("printerName");
        String langFile = basicProps.getProperty("langFile");
        bot = new TelegramBot(botToken);

        // Load lang
        uiLanguage = new Properties();
        uiLanguage.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("de.properties"));

        // Register for updates
        bot.setUpdatesListener(new PrintingListener());
    }

    public static TelegramBot getBot() {
        return bot;
    }

    public static Properties getLang() {
        return uiLanguage;
    }

    public static String getBotToken() { return botToken; }

    public static String getPrinterName() { return printerName; }

    /**
     * Sends a message to the chat with the given id
     * @param chatId The id of the chat
     * @param msg The message
     */
    public static boolean sendMsg(long chatId, String msg) { return PrintBot.getBot().execute(new SendMessage(chatId, msg)).isOk(); }
}
