package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link DeleteMessageResponse}
 *
 * @author Daniel Schild
 */
public class DeleteMessageResponseTest {

    @Test
    public void positiveSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.deleteMsg(0,1)).thenReturn(true);
            assertTrue(new DeleteMessageResponse(0,1).execute());
        }
    }

    @Test
    public void negativeSendFeedbackTest() {
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.deleteMsg(1,2)).thenReturn(false);
            assertFalse(new DeleteMessageResponse(1,2).execute());
        }
    }
}
