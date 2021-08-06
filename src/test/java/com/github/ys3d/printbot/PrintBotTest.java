package com.github.ys3d.printbot;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Tests {@link PrintBot}
 * @author Daniel Schild
 */
public class PrintBotTest {

    @Test
    public void normalEnInitTest() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot_properties/bot_test_en.properties"));
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_test_en.properties"});

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName());
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("en.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

    @Test
    public void normalDeInitTest() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot_properties/bot_test_de.properties"));
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_test_de.properties"});

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName());
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("de.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

    @Test
    public void whitelistLoadOnInitTest() throws IOException {
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_wl2.properties"});
        Whitelist wl = PrintBot.getWhiteList();
        assertTrue(wl.isWhitelisted("WL1"));
        assertTrue(wl.isWhitelisted("WL2"));
        assertEquals(2, wl.size());

        PrintBot.main(new String[0]);
        wl = PrintBot.getWhiteList();
        assertTrue(wl.isWhitelisted("WL1"));
        assertFalse(wl.isWhitelisted("WL2"));
        assertEquals(1, wl.size());
    }

    @Test
    public void initNoArgumentTest() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot.properties"));
        PrintBot.main(new String[0]);

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName());
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("en.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

}
