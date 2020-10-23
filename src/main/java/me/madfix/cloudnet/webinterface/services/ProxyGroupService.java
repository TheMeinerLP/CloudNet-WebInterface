package me.madfix.cloudnet.webinterface.services;

import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

final class ProxyGroupService {

    private final WebInterface webInterface;

    ProxyGroupService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    /**
     * Returns the proxy group using the name
     *
     * @param groupName is used to identify the group
     * @return a proxy group
     */
    public CompletableFuture<ProxyGroup> getProxyGroup(String groupName) {
        CompletableFuture<ProxyGroup> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getProxyGroup(groupName));
        return optionalCompletableFuture;
    }

    /**
     * @return a list of proxy groups
     */
    public CompletableFuture<Collection<ProxyGroup>> getProxyGroups() {
        CompletableFuture<Collection<ProxyGroup>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getProxyGroups().values());
        return optionalCompletableFuture;
    }

    /**
     * Returns a list of proxies by the proxy group name
     *
     * @param groupName is used to identify the group
     * @return a list of proxies
     */
    public CompletableFuture<Collection<ProxyServer>> getProxiesFromGroup(String groupName) {
        CompletableFuture<Collection<ProxyServer>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getProxys(groupName));
        return optionalCompletableFuture;
    }

    /**
     * Returns a list of proxies
     *
     * @return a list of proxies
     */
    public CompletableFuture<Collection<ProxyServer>> getProxies() {
        CompletableFuture<Collection<ProxyServer>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getProxys().values());
        return optionalCompletableFuture;
    }

    /**
     * Starts a screen from CloudNet using the proxy id
     *
     * @param proxyId is used to identify the proxy
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> startProxyScreen(String proxyId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        ProxyServer proxy = this.webInterface.getCloud().getProxy(proxyId);
        if (proxy != null) {
            this.webInterface.getCloud()
                             .getScreenProvider()
                             .handleEnableScreen(proxy.getServiceId(), proxy.getWrapper());
            proxy.getWrapper().enableScreen(proxy.getLastProxyInfo());
            optionalCompletableFuture.complete(this.webInterface.getCloud()
                                                                .getScreenProvider()
                                                                .getScreens()
                                                                .containsKey(proxy.getServiceId().getServerId()));
        } else {
            optionalCompletableFuture.cancel(true);
        }
        return optionalCompletableFuture;
    }

    /**
     * Sends a command to the proxy console
     *
     * @param proxyId is used to identify the proxy
     * @param command to be sent to the console
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> writeCommand(String proxyId, String command) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        ProxyServer proxy = this.webInterface.getCloud().getProxy(proxyId);
        if (proxy != null) {
            proxy.getWrapper().writeProxyCommand(command, proxy.getLastProxyInfo());
            optionalCompletableFuture.complete(true);
        } else {
            optionalCompletableFuture.cancel(true);
        }
        return optionalCompletableFuture;
    }

    /**
     * Stops a screen from CloudNet using the proxy id
     *
     * @param proxyId is used to identify the proxy
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> stopProxyScreen(String proxyId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        ProxyServer proxy = this.webInterface.getCloud().getProxy(proxyId);
        if (proxy != null) {
            proxy.getWrapper().disableScreen(proxy.getLastProxyInfo());
            this.webInterface.getCloud().getScreenProvider().disableScreen(proxy.getServiceId().getServerId());
            optionalCompletableFuture.complete(!this.webInterface.getCloud()
                                                                 .getScreenProvider()
                                                                 .getScreens()
                                                                 .containsKey(proxy.getServiceId().getServerId()));
        } else {
            optionalCompletableFuture.cancel(true);
        }
        return optionalCompletableFuture;
    }

    /**
     * Stop all proxies of a group
     *
     * @param proxyGroup is used to identify the group
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> stopProxies(String proxyGroup) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getProxys().values().forEach(ps -> this.webInterface.getCloud().stopProxy(ps));
        optionalCompletableFuture.complete(this.webInterface.getCloud().getProxys(proxyGroup).size() <= 0);
        return optionalCompletableFuture;
    }

    /**
     * Stop the proxy
     *
     * @param proxyId is used to identify the proxy
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> stopProxy(String proxyId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().stopProxy(proxyId);
        optionalCompletableFuture.complete(!this.webInterface.getCloud().getProxys().containsKey(proxyId));
        return optionalCompletableFuture;
    }

    /**
     * Starts a proxy from the group
     *
     * @param proxyGroup is used to identify the group
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> startProxy(String proxyGroup) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getProxys().size();
        this.webInterface.getCloud().startProxyAsync(this.webInterface.getCloud().getProxyGroup(proxyGroup));
        int newSize = this.webInterface.getCloud().getProxys().size();
        optionalCompletableFuture.complete(newSize > lastSize);
        return optionalCompletableFuture;
    }

    /**
     * Starts a fixed number of proxies from the group
     *
     * @param proxyGroup is used to identify the group
     * @param amount     indicates the number
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> startProxies(String proxyGroup, int amount) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getProxys().size();
        for (int i = 0; i < amount; i++) {
            this.webInterface.getCloud().startProxyAsync(this.webInterface.getCloud().getProxyGroup(proxyGroup));
        }
        int newSize = this.webInterface.getCloud().getProxys().size();
        optionalCompletableFuture.complete(newSize > lastSize);
        return optionalCompletableFuture;
    }

    /**
     * Updates a proxy group
     *
     * @param proxyGroup is used to identify the group
     * @return a completable future with an boolean that returns true if the task was successful
     */
    public CompletableFuture<Boolean> updateProxyGroup(ProxyGroup proxyGroup) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getProxyGroups().remove(proxyGroup.getName());
        this.webInterface.getCloud().getConfig().deleteGroup(proxyGroup);
        this.webInterface.getCloud().getConfig().createGroup(proxyGroup);
        this.webInterface.getCloud().getProxyGroups().put(proxyGroup.getName(), proxyGroup);
        for (Wrapper wrapper : this.webInterface.getCloud().getWrappers().values()) {
            wrapper.updateWrapper();
        }
        optionalCompletableFuture.complete(true);
        return optionalCompletableFuture;
    }

}
