package com.github.ys3d.printbot.updateProcessing.response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import static org.junit.Assert.*;

/**
 * Tests {@link QuietErrorResponse}
 *
 * @author Daniel Schild
 */
public class QuietErrorResponseTest {
    @Rule
    public SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Test
    public void positiveSendFeedbackTest() {
        assertTrue(new QuietErrorResponse("Test").execute());
    }

    @Test
    public void errorPrintTest() {
        systemErrRule.clearLog();
        assertTrue(new QuietErrorResponse("Test2").execute());
        String output = systemErrRule.getLog().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n").trim();
        assertEquals("Error: \"Test2\"", output);
    }
}
