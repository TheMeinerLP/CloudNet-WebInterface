package me.madfix.cloudnet.webinterface.services;

import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

final class ProxyGroupService {

    private final WebInterface webInterface;

    ProxyGroupService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<ProxyGroup>> getProxyGroup(String groupName) {
        CompletableFuture<Optional<ProxyGroup>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getProxyGroup(groupName)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<ProxyGroup>>> getProxyGroups() {
        CompletableFuture<Optional<Collection<ProxyGroup>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getProxyGroups().values()));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<ProxyServer>>> getProxiesFromGroup(String groupName) {
        CompletableFuture<Optional<Collection<ProxyServer>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getProxys(groupName)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<ProxyServer>>> getProxies() {
        CompletableFuture<Optional<Collection<ProxyServer>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getProxys().values()));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> startProxyScreen(String proxyId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        ProxyServer proxy = this.webInterface.getCloud().getProxy(proxyId);
        if (proxy != null) {
            this.webInterface.getCloud().getScreenProvider().handleEnableScreen(proxy.getServiceId(),
                    proxy.getWrapper());
            proxy.getWrapper().enableScreen(proxy.getLastProxyInfo());
            optionalCompletableFuture.complete(Optional.of(
                    this.webInterface.getCloud().getScreenProvider().getScreens()
                            .containsKey(proxy.getServiceId().getServerId())));
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> writeCommand(String proxyId, String command) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        ProxyServer proxy = this.webInterface.getCloud().getProxy(proxyId);
        if (proxy != null) {
            proxy.getWrapper().writeProxyCommand(command, proxy.getLastProxyInfo());
            optionalCompletableFuture.complete(Optional.of(true));
        }  else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> stopProxyScreen(String proxyId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        ProxyServer proxy = this.webInterface.getCloud().getProxy(proxyId);
        if (proxy != null) {
            proxy.getWrapper().disableScreen(proxy.getLastProxyInfo());
            this.webInterface.getCloud().getScreenProvider().disableScreen(proxy.getServiceId().getServerId());
            optionalCompletableFuture.complete(Optional.of(!
                    this.webInterface.getCloud().getScreenProvider().getScreens()
                            .containsKey(proxy.getServiceId().getServerId())));
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> stopProxies(String proxyGroup) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getProxys().values().forEach(ps -> this.webInterface.getCloud().stopProxy(ps));
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getProxys().values().size() <= 0));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> stopProxy(String proxyId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().stopProxy(proxyId);
        optionalCompletableFuture.complete(Optional.of(!this.webInterface.getCloud().getProxys().containsKey(proxyId)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> startProxy(String proxyGroup) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getProxys().size();
        this.webInterface.getCloud().startProxyAsync(this.webInterface.getCloud().getProxyGroup(proxyGroup));
        int newSize = this.webInterface.getCloud().getProxys().size();
        optionalCompletableFuture.complete(Optional.of(newSize > lastSize));
        return optionalCompletableFuture;
    }
    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> startProxies(String proxyGroup, int amount) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getProxys().size();
        for (int i = 0; i < amount; i++) {
            this.webInterface.getCloud().startProxyAsync(this.webInterface.getCloud().getProxyGroup(proxyGroup));
        }
        int newSize = this.webInterface.getCloud().getProxys().size();
        optionalCompletableFuture.complete(Optional.of(newSize > lastSize));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> updateProxyGroup(ProxyGroup proxyGroup) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        CloudNet.getInstance().getProxyGroups().remove(proxyGroup.getName());
        this.webInterface.getCloud().getConfig().deleteGroup(proxyGroup);
        this.webInterface.getCloud().getConfig().createGroup(proxyGroup);
        this.webInterface.getCloud().getProxyGroups().put(proxyGroup.getName(), proxyGroup);
        for (Wrapper wrapper : this.webInterface.getCloud().getWrappers().values()) {
            wrapper.updateWrapper();
        }
        optionalCompletableFuture.complete(Optional.of(true));
        return optionalCompletableFuture;
    }

}
