package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;

/**
 * A {@link Response} that sends a message in the given chat
 *
 * @author Daniel Schild
 */
public class TextResponse implements Response {
    private static final int MAX_MESSAGE_LENGTH_SYSTEM_PRINT = 15;

    private final long chatId;
    private final String message;

    /**
     * Initialises a {@link TextResponse}
     * @param chatId The user that requested help
     * @param message The message that should be sent to the user
     */
    public TextResponse(long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    @Override
    public boolean execute() {
        if (message.length() <= MAX_MESSAGE_LENGTH_SYSTEM_PRINT) System.out.println("ID: " + chatId + " Sending message: \"" + message + "\"");
        else System.out.println("ID: " + chatId + " Sending message");
        return PrintBot.sendMsg(chatId, message);
    }

    /**
     * Returns the chatId
     * @return The id
     */
    public long getChatId() {
        return chatId;
    }

    /**
     * Returns the message
     * @return The message
     */
    public String getMessage() {
        return message;
    }
}
