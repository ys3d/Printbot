package com.github.ys3d.printbot.updateProcessing;

import com.github.ys3d.printbot.PrintBot;
import com.github.ys3d.printbot.testModel.TestMessage;
import com.github.ys3d.printbot.testModel.TestUpdate;
import com.github.ys3d.printbot.updateProcessing.response.HelpResponse;
import com.github.ys3d.printbot.updateProcessing.response.MessageResponse;
import com.github.ys3d.printbot.updateProcessing.response.Response;
import com.github.ys3d.printbot.updateProcessing.response.TextResponse;
import com.pengrad.telegrambot.model.Update;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class UpdateProcessorMessageUpdatesTest {
    private final static Properties LANG = new Properties();

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
    public void helpCommandTest() {
        Update u = new TestUpdate(new TestMessage(1, "/help"));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            Response[] rArray = new UpdateProcessor().handle(List.of(u));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof HelpResponse);
            TextResponse r = (TextResponse) rArray[0];
            assertEquals(1, r.getChatId());
            assertEquals(LANG.getProperty("help"), r.getMessage());
        }
    }

    @Test
    public void cancelNoPrintCommandTest() {
        Update u = new TestUpdate(new TestMessage(1, "/cancel"));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            Response[] rArray = new UpdateProcessor().handle(List.of(u));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof TextResponse);
            TextResponse r = (TextResponse) rArray[0];
            assertEquals(1, r.getChatId());
            assertEquals(LANG.getProperty("print.cancel.no.job"), r.getMessage());
        }
    }

    @Test
    public void cancelWithPrintCommandTest() {
        Update u0 = new TestUpdate(new TestMessage(1, "/printpdf"));
        Update u1 = new TestUpdate(new TestMessage(1, "/cancel"));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u0)).thenReturn(true);
            dummyPB.when(() -> PrintBot.isWhitelisted(u1)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            dummyPB.when(PrintBot::getPrinterName).thenReturn(new String[]{"P1, P2"});
            UpdateProcessor up = new UpdateProcessor();
            up.handle(List.of(u0));
            Response[] rArray = up.handle(List.of(u1));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof TextResponse);
            TextResponse r = (TextResponse) rArray[0];
            assertEquals(1, r.getChatId());
            assertEquals(LANG.getProperty("print.cancel"), r.getMessage());
        }
    }

    @Test
    public void printCommandTest() {
        Update u = new TestUpdate(new TestMessage(1, "/printpdf"));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            dummyPB.when(PrintBot::getPrinterName).thenReturn(new String[]{"P1, P2"});
            Response[] rArray = new UpdateProcessor().handle(List.of(u));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof MessageResponse);
            MessageResponse r = (MessageResponse) rArray[0];
            assertNotNull(r.getMessage().getParameters().get("reply_markup"));
        }
    }

    @Test
    public void invalidCommandTest() {
        Update u = new TestUpdate(new TestMessage(1, "/invalid"));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            Response[] rArray = new UpdateProcessor().handle(List.of(u));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof TextResponse);
            TextResponse r = (TextResponse) rArray[0];
            assertEquals(1, r.getChatId());
            assertEquals(LANG.getProperty("unknown.command"), r.getMessage());
        }
    }

    @Test
    public void messagePrintTest() {
        Update u = new TestUpdate(new TestMessage(1, "test"));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            dummyPB.when(PrintBot::getPrinterName).thenReturn(new String[]{"P1, P2"});
            systemOutRule.clearLog();
            new UpdateProcessor().handle(List.of(u));
            String output = systemOutRule.getLog();
            output = output.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
            assertEquals("1: Message \"test\"", output);
        }
    }
}
