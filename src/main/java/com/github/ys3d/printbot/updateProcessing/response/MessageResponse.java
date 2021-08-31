package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;
import com.pengrad.telegrambot.request.SendMessage;

/**
 * Response witch sends a given message. This one is especially used to send messages with InlineKeyboards
 *
 * @author Daniel Schild
 */
public class MessageResponse implements Response {
    private final SendMessage message;

    /**
     * Initializes a new {@link MessageResponse}
     * @param message The message that should be sent
     */
    public MessageResponse(SendMessage message) {
        this.message = message;
    }

    @Override
    public boolean execute() {
        System.out.println("ID: " + message.getParameters().get("chat_id") + " Sending message: \"" + message.getParameters().get("text") + "\"");
        return PrintBot.sendMsg(message);
    }

    /**
     * Returns the message
     * @return The message
     */
    public SendMessage getMessage() {
        return message;
    }
}
