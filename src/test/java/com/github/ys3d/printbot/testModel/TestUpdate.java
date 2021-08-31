package com.github.ys3d.printbot.testModel;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Class extending {@link Update} to send functions tests with {@link Update}-Elements
 *
 * @author Daniel Schild
 */
public class TestUpdate extends Update {
    private final Message message;
    private final CallbackQuery callbackQuery;

    public TestUpdate(Message message, CallbackQuery callbackQuery) {
        this.message = message;
        this.callbackQuery = callbackQuery;
    }

    public TestUpdate(Message message) {
        this(message, null);
    }

    public TestUpdate(CallbackQuery callbackQuery) {
        this(null, callbackQuery);
    }

    @Override
    public Message message() {
        return message;
    }

    @Override
    public CallbackQuery callbackQuery() {
        return callbackQuery;
    }

    /**
     * Self-test for {@link TestUpdate}
     */
    public static class TestUpdateTest {
        @Test
        public void selfTest() {
            TestMessage tm1  = new TestMessage(0L, "String1", null);
            TestMessage tm2  = new TestMessage(1L, "String2", null);
            TestCallbackQuery tcq1 = new TestCallbackQuery(0L, "String1");
            TestCallbackQuery tcq2 = new TestCallbackQuery(1L, "String2");
            TestUpdate tu = new TestUpdate(tm1);
            assertEquals(tm1, tu.message());
            assertNull(tu.callbackQuery());
            tu = new TestUpdate(tcq1);
            assertNull(tu.message());
            assertEquals(tcq1, tu.callbackQuery());
            tu = new TestUpdate(tm2, tcq2);
            assertEquals(tm2, tu.message());
            assertEquals(tcq2, tu.callbackQuery());
        }
    }
}
