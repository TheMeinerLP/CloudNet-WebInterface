package me.madfix.cloudnet.webinterface.services;

import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

final class WrapperService {

    private final WebInterface webInterface;

    WrapperService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    /**
     * @return all wrapper names as collection
     */
    public CompletableFuture<Collection<String>> getWrapperNames() {
        CompletableFuture<Collection<String>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getWrappers().keySet());
        return optionalCompletableFuture;
    }

    /**
     * @return all wrapper instances as collection
     */
    public CompletableFuture<Collection<Wrapper>> getWrappers() {
        CompletableFuture<Collection<Wrapper>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getWrappers().values());
        return optionalCompletableFuture;
    }

    /**
     * Adds a wrapper to the system
     *
     * @param wrapperMeta contains all necessary information
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> createWrapper(WrapperMeta wrapperMeta) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getConfig().createWrapper(wrapperMeta);
        optionalCompletableFuture.complete(this.webInterface.getCloud()
                                                            .getConfig()
                                                            .getWrappers()
                                                            .contains(wrapperMeta));
        return optionalCompletableFuture;
    }

    /**
     * Removes a wrapper from the system
     *
     * @param wrapperMeta contains all necessary information
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> deleteWrapper(WrapperMeta wrapperMeta) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getConfig().deleteWrapper(wrapperMeta);
        return optionalCompletableFuture;
    }

    /**
     * Returns a wrapper instance by its id
     *
     * @param wrapperId is used as an indicator for the instance
     * @return the wrapper instance as optional to avoid a null pointer exception
     */
    public CompletableFuture<Wrapper> getWrapper(String wrapperId) {
        CompletableFuture<Wrapper> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getWrappers().get(wrapperId));
        return optionalCompletableFuture;
    }
}
