package com.github.ys3d.printbot.testModel;

import com.pengrad.telegrambot.model.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class extending {@link Document} to send functions tests with {@link Document}-Elements
 *
 * @author Daniel Schild
 */
public class TestDocument extends Document {
    private final String mimeType;
    private final String fileId;

    public TestDocument(String mimeType, String fileId) {
        this.mimeType = mimeType;
        this.fileId = fileId;
    }

    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public String fileId() {
        return fileId;
    }

    /**
     * Self-test for {@link TestDocument}
     */
    public static class TestDocumentTest {
        @Test
        public void selfTest() {
            TestDocument td = new TestDocument("String1", "String2");
            assertEquals("String1", td.mimeType());
            assertEquals("String2", td.fileId());
            td = new TestDocument("String2", "String1");
            assertEquals("String2", td.mimeType());
            assertEquals("String1", td.fileId());
        }
    }
}
