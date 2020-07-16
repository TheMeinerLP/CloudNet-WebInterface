package me.madfix.cloudnet.webinterface.api.update;

import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.concurrent.CompletableFuture;

public abstract class UpdateTask {

    private final String version;

    protected UpdateTask(String version) {
        this.version = version;
    }

    public abstract CompletableFuture<Boolean> preUpdateStep(WebInterface webInterface);

    public abstract CompletableFuture<Boolean> updateStep(WebInterface webInterface);

    public abstract CompletableFuture<Boolean> postUpdateStep(WebInterface webInterface);

    public String getVersion() {
        return version;
    }
}
