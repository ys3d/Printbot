package com.github.ys3d.printbot.updateProcessing.response;

import com.github.ys3d.printbot.PrintBot;

/**
 * Response for a user asking for help
 *
 * @author Daniel Schild
 */
public class HelpResponse extends TextResponse{

    /**
     * Initialises a {@link HelpResponse}
     * @param chatId The user that requested help
     */
    public HelpResponse(long chatId) {
        super(chatId, PrintBot.getLang().getProperty("help"));
    }
}
