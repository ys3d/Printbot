package com.github.ys3d.printbot;

import com.github.ys3d.printbot.updateProcessing.UpdateProcessor;
import com.github.ys3d.printbot.updateProcessing.response.Response;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Tests {@link PrintingListener}
 *
 * @author Daniel Schild
 */
public class PrintingListenerTest {
    @Test
    public void processEmptyTest() {
        UpdateProcessor up = Mockito.mock(UpdateProcessor.class);
        PrintingListener pl = new PrintingListener(up);
        List<Update> l = new ArrayList<>();
        when(up.handle(l)).thenReturn(new Response[0]);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, pl.process(l));
    }

    @Test
    public void processWithResponse() {
        UpdateProcessor up = Mockito.mock(UpdateProcessor.class);
        PrintingListener pl = new PrintingListener(up);
        List<Update> l = new ArrayList<>();
        Response rMock = Mockito.mock(Response.class);
        Response[] responses = {rMock};
        when(up.handle(l)).thenReturn(responses);
        assertEquals(UpdatesListener.CONFIRMED_UPDATES_ALL, pl.process(l));
        Mockito.verify(rMock, times(1)).execute();
    }
}
