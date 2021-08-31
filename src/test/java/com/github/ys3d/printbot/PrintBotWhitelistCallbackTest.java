package com.github.ys3d.printbot;

import com.github.ys3d.printbot.testModel.TestCallbackQuery;
import com.github.ys3d.printbot.testModel.TestUpdate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Tests the analytical features for {@link Whitelist} in {@link PrintBot}
 * @author Daniel Schild
 */
@RunWith(Parameterized.class)
public class PrintBotWhitelistCallbackTest {
    private final int messageId;
    private final boolean correct;

    @Parameterized.Parameters( name = "Expected result: {1}" )
    public static Collection<Object> data() {
        return Arrays.asList(new Object[] [] {{1, true}, {0, false}});
    }

    public PrintBotWhitelistCallbackTest(int messageId, boolean correct) {
        this.messageId = messageId;
        this.correct = correct;
    }

    @Test
    public void isWhitelistedMessagePositive() throws IOException {
        PrintBot.main(new String[]{"src/test/resources/bot_properties/bot_wlNumbers.properties"});
        assertEquals(correct, PrintBot.isWhitelisted(new TestUpdate(new TestCallbackQuery(messageId))));
    }
}
