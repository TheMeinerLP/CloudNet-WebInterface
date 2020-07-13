package me.madfix.cloudnet.webinterface.services;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

final class ServerGroupService {

    private final WebInterface webInterface;

    ServerGroupService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<ServerGroup>> getServerGroup(String groupName) {
        CompletableFuture<Optional<ServerGroup>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getServerGroup(groupName)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<ServerGroup>>> getServerGroups() {
        CompletableFuture<Optional<Collection<ServerGroup>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getServerGroups().values()));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<MinecraftServer>>> getServersFromGroup(String groupName) {
        CompletableFuture<Optional<Collection<MinecraftServer>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getServers(groupName)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Collection<MinecraftServer>>> getServers() {
        CompletableFuture<Optional<Collection<MinecraftServer>>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getServers().values()));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> startServerScreen(String serverId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        MinecraftServer server = this.webInterface.getCloud().getServer(serverId);
        if (server != null) {
            this.webInterface.getCloud().getScreenProvider().handleEnableScreen(server.getServiceId(), server.getWrapper());
            server.getWrapper().enableScreen(server.getLastServerInfo());
            optionalCompletableFuture.complete(Optional.of(
                    this.webInterface.getCloud().getScreenProvider().getScreens()
                            .containsKey(server.getServiceId().getServerId())));
        } else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> writeCommand(String serverId, String command) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        MinecraftServer minecraftServer = this.webInterface.getCloud().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().writeServerCommand(command, minecraftServer.getLastServerInfo());
            optionalCompletableFuture.complete(Optional.of(true));
        }  else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> stopServerScreen(String serverId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        MinecraftServer minecraftServer = this.webInterface.getCloud().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().disableScreen(minecraftServer.getLastServerInfo());
            this.webInterface.getCloud().getScreenProvider().disableScreen(minecraftServer.getServiceId().getServerId());
            optionalCompletableFuture.complete(Optional.of(!
                    this.webInterface.getCloud().getScreenProvider().getScreens()
                            .containsKey(minecraftServer.getServiceId().getServerId())));
        }  else optionalCompletableFuture.cancel(true);
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> stopServers(String serverGroup) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getServers().values().forEach(ms -> this.webInterface.getCloud().stopServer(ms));
        optionalCompletableFuture.complete(Optional.of(this.webInterface.getCloud().getServers(serverGroup).size() <= 0));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> stopServer(String serverId) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().stopServer(serverId);
        optionalCompletableFuture.complete(Optional.of(!this.webInterface.getCloud().getServers().containsKey(serverId)));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> startServer(String serverGroup) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getServers().size();
        this.webInterface.getCloud().startGameServerAsync(this.webInterface.getCloud().getServerGroup(serverGroup));
        int newSize = this.webInterface.getCloud().getServers().size();
        optionalCompletableFuture.complete(Optional.of(newSize > lastSize));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> startServers(String serverGroup, int amount) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getServers().size();
        for (int i = 0; i < amount; i++) {
            this.webInterface.getCloud().startGameServerAsync(this.webInterface.getCloud().getServerGroup(serverGroup));
        }
        int newSize = this.webInterface.getCloud().getServers().size();
        optionalCompletableFuture.complete(Optional.of(newSize > lastSize));
        return optionalCompletableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Optional<Boolean>> updateServerGroup(ServerGroup serverGroup) {
        CompletableFuture<Optional<Boolean>> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getServerGroups().remove(serverGroup.getName());
        this.webInterface.getCloud().getConfig().deleteGroup(serverGroup);
        this.webInterface.getCloud().getConfig().createGroup(serverGroup);
        this.webInterface.getCloud().getServerGroups().put(serverGroup.getName(), serverGroup);
        for (Wrapper wrapper : this.webInterface.getCloud().getWrappers().values()) {
            wrapper.updateWrapper();
        }
        optionalCompletableFuture.complete(Optional.of(true));
        return optionalCompletableFuture;
    }
}
