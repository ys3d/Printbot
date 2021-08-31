package com.github.ys3d.printbot.updateProcessing.response;

/**
 * Interface for operations like system-prints and telegram-messages to be executed as a response to operations performed by the user.
 *
 * @author Daniel Schild
 */
public interface Response {
    /**
     * Executes the action
     * @return True if the execution finished successfully, false otherwise
     */
    boolean execute();
}
