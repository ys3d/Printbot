package com.github.ys3d.printbot.testModel;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class extending {@link CallbackQuery} to send functions tests with {@link CallbackQuery}-Elements
 *
 * @author Daniel Schild
 */
public class TestCallbackQuery extends CallbackQuery {
    private final long id;
    private final String data;
    private final Message message;

    public TestCallbackQuery(long id, String data, Message message) {
        this.id = id;
        this.data = data;
        this.message = message;
    }

    public TestCallbackQuery(long id, String data) {
        this(id, data, null);
    }

    public TestCallbackQuery(long id) {
        this(id, "", null);
    }

    @Override
    public User from() {
        return new User(id);
    }

    @Override
    public String data() {
        return data;
    }

    @Override
    public Message message() {
        return message;
    }

    /**
     * Self-test for {@link TestCallbackQuery}
     */
    public static class TestCallbackQueryTest {
        @Test
        public void selfTest() {
            Message msg = new TestMessage(1);
            TestCallbackQuery tcq = new TestCallbackQuery(0L, "String", msg);
            assertEquals((Long) 0L, tcq.from().id());
            assertEquals("String", tcq.data());
            assertEquals(msg, tcq.message());
            tcq = new TestCallbackQuery(1L, "String2");
            assertEquals((Long) 1L, tcq.from().id());
            assertEquals("String2", tcq.data());
        }
    }
}
