package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Tests {@link HelpResponse}
 *
 * @author Daniel Schild
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class HelpResponseTest {
    @Rule
    public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    private static final Properties LANG = new Properties();

    static {
        try {
            LANG.load(new FileInputStream("src/test/resources/testlang.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void positiveSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(0,"help")).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            assertTrue(new HelpResponse(0).execute());
        }
    }

    @Test
    public void negativeSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"help")).thenReturn(false);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            assertFalse(new HelpResponse(1).execute());
        }
    }

    @Test
    public void systemPrintTest() {
        systemOutRule.clearLog();
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.sendMsg(1,"help")).thenReturn(false);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            assertFalse(new HelpResponse(1).execute());
        }
        String output = systemOutRule.getLog().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals("ID: 1 Sending message: \"help\"", output);
    }
}
