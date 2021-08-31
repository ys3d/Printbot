package com.github.ys3d.printbot.updateProcessing;

import com.github.ys3d.printbot.PrintBot;
import com.github.ys3d.printbot.testModel.TestCallbackQuery;
import com.github.ys3d.printbot.testModel.TestMessage;
import com.github.ys3d.printbot.testModel.TestUpdate;
import com.github.ys3d.printbot.updateProcessing.response.*;
import com.pengrad.telegrambot.model.Update;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests updates with callback for {@link UpdateProcessor}
 *
 * @author Daniel Schild
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class UpdateProcessorCallbackUpdatesTest {
    private final static Properties LANG = new Properties();
    private final static Update PRINT_UPDATE = new TestUpdate(new TestMessage(1, "/printpdf"));

    static {
        try {
            LANG.load(new FileInputStream("src/test/resources/testlang.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void invalidCallback() {
        Update u = new TestUpdate(new TestCallbackQuery(1, "data", new TestMessage(1, 0)));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(PRINT_UPDATE)).thenReturn(true);
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            UpdateProcessor up = new UpdateProcessor();
            Response[] rArray = up.handle(List.of(u));
            assertEquals(2, rArray.length);
            assertTrue(rArray[0] instanceof DeleteMessageResponse);
            assertTrue(rArray[1] instanceof QuietErrorResponse);
        }
    }

    @Test
    public void validCallback() {
        Update u = new TestUpdate(new TestCallbackQuery(1, "data", new TestMessage(1, 0)));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(PRINT_UPDATE)).thenReturn(true);
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            dummyPB.when(PrintBot::getPrinterName).thenReturn(new String[]{"P1, P2"});
            UpdateProcessor up = new UpdateProcessor();
            up.handle(List.of(PRINT_UPDATE));
            Response[] rArray = up.handle(List.of(u));
            assertEquals(3, rArray.length);
            assertTrue(rArray[0] instanceof DeleteMessageResponse);
            assertTrue(rArray[1] instanceof TextResponse);
            assertTrue(rArray[2] instanceof TextResponse);
            TextResponse r = (TextResponse) rArray[1];
            assertEquals(1, r.getChatId());
            assertEquals(MessageFormat.format(PrintBot.getLang().getProperty("print.printer.selected"), "data"), r.getMessage());
            r = (TextResponse) rArray[2];
            assertEquals(1, r.getChatId());
            assertEquals(LANG.getProperty("print.enabled"), r.getMessage());
        }
    }

    @Test
    public void systemPrintTest() {
        Update u = new TestUpdate(new TestCallbackQuery(1, "data", new TestMessage(1, 0)));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(PRINT_UPDATE)).thenReturn(true);
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            dummyPB.when(PrintBot::getPrinterName).thenReturn(new String[]{"P1, P2"});
            UpdateProcessor up = new UpdateProcessor();
            up.handle(List.of(PRINT_UPDATE));
            systemOutRule.clearLog();
            up.handle(List.of(u));
            String output = systemOutRule.getLog();
            output = output.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
            assertEquals("1: Callback \"data\"", output);
        }
    }
}
