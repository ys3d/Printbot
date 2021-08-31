package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Tests {@link MessageResponse}
 *
 * @author Daniel Schild
 */
public class MessageResponseTest {
    @Rule
    public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void positiveSendFeedbackTest() {
        SendMessage sendMessage = new SendMessage(0, "String1");
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(sendMessage)).thenReturn(true);
            assertTrue(new MessageResponse(sendMessage).execute());
        }
    }

    @Test
    public void negativeSendFeedbackTest() {
        SendMessage sendMessage = new SendMessage(1, "String2");
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(sendMessage)).thenReturn(false);
            assertFalse(new MessageResponse(sendMessage).execute());
        }
    }

    @Test
    public void errorPrintTest() {
        systemOutRule.clearLog();
        SendMessage sendMessage = new SendMessage(1, "String2");
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(sendMessage)).thenReturn(false);
            assertFalse(new MessageResponse(sendMessage).execute());
        }
        String output = systemOutRule.getLog().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals("ID: 1 Sending message: \"String2\"", output);
    }
}
