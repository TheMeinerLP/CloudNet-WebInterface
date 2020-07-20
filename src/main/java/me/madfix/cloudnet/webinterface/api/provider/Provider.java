package me.madfix.cloudnet.webinterface.api.provider;

import me.madfix.cloudnet.webinterface.WebInterface;

import java.sql.Connection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class Provider {

    protected final WebInterface webInterface;

    public Provider(WebInterface webInterface) {
        this.webInterface = webInterface;
    }


    protected CompletableFuture<Connection> createConnection() {
        CompletableFuture<Connection> future = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            optionalConnection.ifPresent(future::complete);
            optionalConnection.orElseThrow(() -> {
                NullPointerException nullPointerException = new NullPointerException("The SQL connection is empty");
                future.completeExceptionally(nullPointerException);
                return nullPointerException;
            });
        } else {
            future.completeExceptionally(new NullPointerException("The interface configuration is empty"));
        }
        return future;
    }

}
