package com.github.ys3d.printbot.updateProcessing.response;

/**
 * A quiet {@link Response} in case of an error
 * A quiet response is a response witch sends no message over Telegram. It just interacts local.
 *
 * @author Daniel Schild
 */
public class QuietErrorResponse  implements Response {
    private final String message;

    /**
     * Initializes a new {@link QuietErrorResponse}
     * @param message The message that will be printed with System.err.println
     */
    public QuietErrorResponse(String message) {
        this.message = message;
    }

    @Override
    public boolean execute() {
        System.err.println("Error: \"" + message + "\"");
        return true;
    }
}
