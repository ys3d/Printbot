package com.github.ys3d.printbot.updateProcessing;

import com.github.ys3d.printbot.PrintBot;
import com.github.ys3d.printbot.testModel.TestMessage;
import com.github.ys3d.printbot.testModel.TestUpdate;
import com.github.ys3d.printbot.updateProcessing.response.QuietErrorResponse;
import com.github.ys3d.printbot.updateProcessing.response.Response;
import com.pengrad.telegrambot.model.Update;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the whitelist sorting-feature of {@link UpdateProcessor}
 *
 * @author Daniel Schild
 */
public class UpdateProcessorWhitelistTest {
    private UpdateProcessor up = new UpdateProcessor();

    @Before
    public void before() {
        up = new UpdateProcessor();
    }

    @Test
    public void emptyUpdateListTest() {
        assertEquals(0, up.handle(new ArrayList<>()).length);
    }

    @Test
    public void invalidSingleMessageUpdateTest() {
        Update u1 = new TestUpdate(new TestMessage(1));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u1)).thenReturn(false);
            Response[] r = up.handle(List.of(u1));
            assertEquals(1, r.length);
            assertTrue(r[0] instanceof QuietErrorResponse);
        }
    }

    @Test
    public void invalidMultiMessageUpdateTest() {
        Update u1 = new TestUpdate(new TestMessage(1));
        Update u2 = new TestUpdate(new TestMessage(2));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u1)).thenReturn(false);
            dummyPB.when(() -> PrintBot.isWhitelisted(u2)).thenReturn(false);
            Response[] r = up.handle(List.of(u1, u2));
            assertEquals(2, r.length);
            assertTrue(r[0] instanceof QuietErrorResponse);
            assertTrue(r[1] instanceof QuietErrorResponse);
        }
    }
}
