package me.madfix.cloudnet.webinterface.services;

import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

final class WrapperService {

    private final WebInterface webInterface;

    WrapperService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<String>>> getWrapperNames() {
        CompletableFuture<Optional<Collection<String>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getWrappers().keySet()));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<Wrapper>>> getWrappers() {
        CompletableFuture<Optional<Collection<Wrapper>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getWrappers().values()));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> createWrapper(WrapperMeta wrapperMeta) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getConfig().createWrapper(wrapperMeta);
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud()
                .getConfig().getWrappers().contains(wrapperMeta)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> deleteWrapper(WrapperMeta wrapperMeta) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getConfig().deleteWrapper(wrapperMeta);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Wrapper>> getWrapper(String wrapperId) {
        CompletableFuture<Optional<Wrapper>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getWrappers().get(wrapperId)));
        return optionalCompletableFuture;
    }
}
