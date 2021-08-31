package com.github.ys3d.printbot.updateProcessing;

import com.github.ys3d.printbot.PrintBot;
import com.github.ys3d.printbot.testModel.TestCallbackQuery;
import com.github.ys3d.printbot.testModel.TestDocument;
import com.github.ys3d.printbot.testModel.TestMessage;
import com.github.ys3d.printbot.testModel.TestUpdate;
import com.github.ys3d.printbot.updateProcessing.response.ErrorResponse;
import com.github.ys3d.printbot.updateProcessing.response.QuietErrorResponse;
import com.github.ys3d.printbot.updateProcessing.response.Response;
import com.pengrad.telegrambot.model.Update;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests updates with documents for {@link UpdateProcessor}
 *
 * @author Daniel Schild
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class UpdateProcessorDocumentUpdatesTest {
    private final static Properties LANG = new Properties();
    private final static Update PRINT_UPDATE = new TestUpdate(new TestMessage(1, "/printpdf"));
    private final static Update PRINTER_CALLBACK = new TestUpdate(new TestCallbackQuery(1, "P1", new TestMessage(1, 1)));

    static {
        try {
            LANG.load(new FileInputStream("src/test/resources/testlang.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongStateDocumentUpdate() {
        Update u = new TestUpdate(new TestMessage(1, new TestDocument("mimeType", "fileId")));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            Response[] rArray = new UpdateProcessor().handle(List.of(u));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof QuietErrorResponse);
        }
    }

    @Test
    public void wrongMimeTypeUpdate() {
        Update u = new TestUpdate(new TestMessage(1, new TestDocument("mimeType", "fileId")));
        try (MockedStatic<PrintBot> dummyPB = Mockito.mockStatic(PrintBot.class)) {
            dummyPB.when(() -> PrintBot.isWhitelisted(PRINT_UPDATE)).thenReturn(true);
            dummyPB.when(() -> PrintBot.isWhitelisted(u)).thenReturn(true);
            dummyPB.when(PrintBot::getLang).thenReturn(LANG);
            dummyPB.when(PrintBot::getPrinterName).thenReturn(new String[]{"P1, P2"});
            UpdateProcessor up = new UpdateProcessor();
            up.handle(List.of(PRINT_UPDATE));
            up.handle(List.of(PRINTER_CALLBACK));
            Response[] rArray = up.handle(List.of(u));
            assertEquals(1, rArray.length);
            assertTrue(rArray[0] instanceof ErrorResponse);
            ErrorResponse r = (ErrorResponse) rArray[0];
            assertEquals(1, r.getChatId());
            assertEquals(LANG.getProperty("illegal.file.type"), r.getMessage());
        }
    }
}
