package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Tests {@link TextResponse}
 *
 * @author Daniel Schild
 */
public class TextResponseTest {
    @Rule
    public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void positiveSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(0,"String1")).thenReturn(true);
            assertTrue(new TextResponse(0, "String1").execute());
        }
    }

    @Test
    public void negativeSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"String2")).thenReturn(false);
            assertFalse(new TextResponse(1, "String2").execute());
        }
    }

    @Test
    public void systemPrintShortTest() {
        systemOutRule.clearLog();
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"012345678901234")).thenReturn(false);
            assertFalse(new TextResponse(1, "012345678901234").execute());
        }
        String output = systemOutRule.getLog().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals("ID: 1 Sending message: \"012345678901234\"", output);
    }

    @Test
    public void systemPrintLongTest() {
        systemOutRule.clearLog();
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"0123456789012345")).thenReturn(false);
            assertFalse(new TextResponse(1, "0123456789012345").execute());
        }
        String output = systemOutRule.getLog().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals("ID: 1 Sending message", output);
    }

    @Test
    public void getterTest() {
        TextResponse r = new TextResponse(0, "String1");
        assertEquals(0, r.getChatId());
        assertEquals("String1", r.getMessage());
    }
}
