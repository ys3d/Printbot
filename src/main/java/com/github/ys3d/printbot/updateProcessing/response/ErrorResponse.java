package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;

/**
 * Response if an operation is illegal or can not be executed due to soft or hardware problems
 *
 * @author Daniel Schild
 */
public class ErrorResponse implements Response{
    private final long chatId;
    private final String message;

    /**
     * Initializes a new {@link ErrorResponse}
     * @param chatId The chat corresponding to the error
     * @param message The message
     */
    public ErrorResponse(long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }


    @Override
    public boolean execute() {
        System.err.println("ID: " + chatId + " Error: \"" + message + "\"");
        return PrintBot.sendMsg(chatId, message);
    }

    /**
     * Returns chatId
     * @return The chatId
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
