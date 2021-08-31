package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;

/**
 * Response for deleting a message
 *
 * @author Daniel Schild
 */
public class DeleteMessageResponse implements Response{
    private final int messageId;
    private final long chatId;

    /**
     * Initializes a new {@link DeleteMessageResponse}
     * @param chatId The chat where the message should be deleted
     * @param messageId The message that should be deleted
     */
    public DeleteMessageResponse(long chatId, int messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }

    @Override
    public boolean execute() {
        return PrintBot.deleteMsg(chatId, messageId);
    }
}
