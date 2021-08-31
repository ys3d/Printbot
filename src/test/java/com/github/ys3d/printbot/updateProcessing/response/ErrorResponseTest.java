package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Tests {@link ErrorResponse}
 *
 * @author Daniel Schild
 */
public class ErrorResponseTest {
    @Rule
    public SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Test
    public void positiveSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(0,"String1")).thenReturn(true);
            assertTrue(new ErrorResponse(0,"String1").execute());
        }
    }

    @Test
    public void negativeSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"String2")).thenReturn(false);
            assertFalse(new ErrorResponse(1,"String2").execute());
        }
    }

    @Test
    public void errorPrintTest() {
        systemErrRule.clearLog();
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"String2")).thenReturn(false);
            assertFalse(new ErrorResponse(1,"String2").execute());
        }
        String output = systemErrRule.getLog().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals("ID: 1 Error: \"String2\"", output);
    }

    @Test
    public void getterTest() {
        ErrorResponse r = new ErrorResponse(0, "String");
        assertEquals(0, r.getChatId());
        assertEquals("String", r.getMessage());
    }
}
