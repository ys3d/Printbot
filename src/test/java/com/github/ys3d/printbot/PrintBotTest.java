package com.github.ys3d.printbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Tests {@link PrintBot}
 *
 * @author Daniel Schild
 */
public class PrintBotTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void normalEnInitTest() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot_properties/bot_test_en.properties"));
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_test_en.properties"});

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName()[0]);
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
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName()[0]);
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
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName()[0]);
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("en.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

    @Test
    public void multiPrinterOnlyOnePrinterTest() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot_properties/bot_multiprinter1.properties"));
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_multiprinter1.properties"});

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(1, PrintBot.getPrinterName().length);
        assertEquals(botProps.getProperty("printerName"), PrintBot.getPrinterName()[0]);
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("en.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

    @Test
    public void multiPrinterTwoPrintersTest() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot_properties/bot_multiprinter2.properties"));
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_multiprinter2.properties"});

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(2, PrintBot.getPrinterName().length);
        assertEquals("testPrinterName1", PrintBot.getPrinterName()[0]);
        assertEquals("testPrinterName2", PrintBot.getPrinterName()[1]);
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("en.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

    @Test
    public void multiPrinterSeparator() throws IOException {
        Properties botProps = new Properties();
        botProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("bot_properties/bot_multiprinter3.properties"));
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_multiprinter3.properties"});

        assertEquals(botProps.getProperty("token"), PrintBot.getBotToken());
        assertEquals(2, PrintBot.getPrinterName().length);
        assertEquals("testPrinterName1", PrintBot.getPrinterName()[0]);
        assertEquals("testPrinterName2", PrintBot.getPrinterName()[1]);
        assertEquals(TelegramBot.class, PrintBot.getBot().getClass());
        Properties langExpected = new Properties();
        langExpected.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("en.properties"));
        assertEquals(langExpected, PrintBot.getLang());
    }

    @Test
    public void initPrintTest() throws IOException {
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_multiprinter2.properties"});
        String output = systemOutRule.getLog();
        output = output.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals(
    "%%%%%%%%% Init with following parameters %%%%%%%%%\n" +
            "  multi printer mode:  true\n" +
            "  printer-name:        testPrinterName1\n" +
            "                       testPrinterName2\n" +
            "  language-file:       en.properties\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
            output);
        systemOutRule.clearLog();
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_multiprinter1.properties"});
        output = systemOutRule.getLog();
        output = output.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals(
    "%%%%%%%%% Init with following parameters %%%%%%%%%\n" +
            "  multi printer mode:  true\n" +
            "  printer-name:        testPrinterName\n" +
            "  language-file:       en.properties\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
            output);
    }

    @Test
    public void isWhitelistedEmptyUpdate() throws IOException {
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_wlNumbers.properties"});
        assertFalse(PrintBot.isWhitelisted(new Update()));
    }

    @Test(expected = java.lang.IllegalAccessException.class)
    public void privateConstructorTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> cls = Class.forName("com.github.ys3d.printbot.PrintBot");
        cls.getDeclaredConstructor().newInstance(); // exception here
    }

}
