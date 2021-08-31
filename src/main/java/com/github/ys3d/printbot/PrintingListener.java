package com.github.ys3d.printbot;

import com.github.ys3d.printbot.updateProcessing.UpdateProcessor;
import com.github.ys3d.printbot.updateProcessing.response.Response;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.*;

/**
 * Listener for updates from the telegram bot-api.
 *
 * @author Daniel Schild
 */
public class PrintingListener implements UpdatesListener {
    private final UpdateProcessor processor;

    /**
     * Initializes a new {@link PrintingListener}.
     * The needed {@link UpdateProcessor} is initialized freshly
     */
    public PrintingListener() {
        processor = new UpdateProcessor();
    }

    /**
     * Initializes a new {@link PrintingListener}
     * @param updateProcessor The {@link UpdateProcessor}
     */
    public PrintingListener(UpdateProcessor updateProcessor) {
        this.processor = updateProcessor;
    }

    @Override
    public int process(List<Update> list) {
        Response[] responses = processor.handle(list);
        for (Response r : responses) {
            r.execute();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
