package me.madfix.cloudnet.webinterface.api.update;

import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.concurrent.CompletableFuture;

/**
 * A class to handle dynamically mitigations form old data structures to newer data structures based on versions
 * @version 1.0.0
 * @since 1.11.5
 */
public abstract class UpdateTask {

    private final String version;

    protected UpdateTask(String version) {
        this.version = version;
    }

    /**
     * Pre update step to prepare something before can run the {@link #updateStep(WebInterface)} step
     * @param webInterface is used for get the sql connection
     * @return a {@link CompletableFuture<Boolean>} with true if was the task successfully
     */
    public abstract CompletableFuture<Boolean> preUpdateStep(WebInterface webInterface);

    /**
     * update step to execute code for the next rolling release
     * @param webInterface is used for get the sql connection
     * @return a {@link CompletableFuture<Boolean>} with true if was the task successfully
     */
    public abstract CompletableFuture<Boolean> updateStep(WebInterface webInterface);

    /**
     * post update step to execute code for clean up after the {@link #updateStep(WebInterface)} step
     * @param webInterface is used for get the sql connection
     * @return a {@link CompletableFuture<Boolean>} with true if was the task successfully
     */
    public abstract CompletableFuture<Boolean> postUpdateStep(WebInterface webInterface);

    public String getVersion() {
        return version;
    }
}
