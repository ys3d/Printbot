package com.github.ys3d.printbot.testModel;

import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMessage extends Message {
    private final long id;
    private final int messageId;
    private final String text;
    private final Document document;

    public TestMessage(long id, int messageId, String text, Document document) {
        this.id = id;
        this.messageId = messageId;
        this.text = text;
        this.document = document;
    }

    public TestMessage(long id, int messageId) {
        this(id, messageId, "", null);
    }

    public TestMessage(long id, String text, Document document) {
        this(id, -1, text, document);
    }

    public TestMessage(long id) {
        this(id, -1, "", null);
    }

    public TestMessage(long id, String text) {
        this(id, -1, text, null);
    }

    public TestMessage(long id, Document document) {
        this(id, -1, "", document);
    }

    @Override
    public User from() {
        return new User(id);
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public Document document() {
        return document;
    }

    @Override
    public Integer messageId() {
        return messageId;
    }

    /**
     * Self-test for {@link TestMessage}
     */
    public static class TestMessageTest {
        @Test
        public void selfTest() {
            TestDocument td1 = new TestDocument("String1", "String2");
            TestDocument td2 = new TestDocument("String2", "String1");
            TestMessage tm  = new TestMessage(0L, 1, "String1", td1);
            assertEquals((Long) 0L, tm.from().id());
            assertEquals("String1", tm.text());
            assertEquals(td1, tm.document);
            assertEquals(1, tm.messageId);
            tm  = new TestMessage(1L, 2, "String2", td2);
            assertEquals((Long) 1L, tm.from().id());
            assertEquals("String2", tm.text());
            assertEquals(td2, tm.document);
            assertEquals(2, tm.messageId);
        }
    }
}
