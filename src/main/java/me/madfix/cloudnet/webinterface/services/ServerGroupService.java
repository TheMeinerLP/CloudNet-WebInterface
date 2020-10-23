package me.madfix.cloudnet.webinterface.services;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

final class ServerGroupService {

    private final WebInterface webInterface;

    ServerGroupService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    /**
     * Returns the group by a name
     *
     * @param groupName is used as an indicator for the group
     * @return a server group
     */
    public CompletableFuture<ServerGroup> getServerGroup(String groupName) {
        CompletableFuture<ServerGroup> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getServerGroup(groupName));
        return optionalCompletableFuture;
    }

    /**
     * Returns collection to server groups
     *
     * @return a list of server groups
     */
    public CompletableFuture<Collection<ServerGroup>> getServerGroups() {
        CompletableFuture<Collection<ServerGroup>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getServerGroups().values());
        return optionalCompletableFuture;
    }

    /**
     * Returns collection on servers based on the group name
     *
     * @param groupName is used as an indicator for the minecraft servers
     * @return a list of servers from a group
     */
    public CompletableFuture<Collection<MinecraftServer>> getServersFromGroup(String groupName) {
        CompletableFuture<Collection<MinecraftServer>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getServers(groupName));
        return optionalCompletableFuture;
    }

    /**
     * Returns collection on servers
     *
     * @return a list of servers
     */
    public CompletableFuture<Collection<MinecraftServer>> getServers() {
        CompletableFuture<Collection<MinecraftServer>> optionalCompletableFuture = new CompletableFuture<>();
        optionalCompletableFuture.complete(this.webInterface.getCloud().getServers().values());
        return optionalCompletableFuture;
    }

    /**
     * Starts the screen of a server
     *
     * @param serverId is used as an indicator for the minecraft server
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> startServerScreen(String serverId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        MinecraftServer server = this.webInterface.getCloud().getServer(serverId);
        if (server != null) {
            this.webInterface.getCloud()
                             .getScreenProvider()
                             .handleEnableScreen(server.getServiceId(), server.getWrapper());
            server.getWrapper().enableScreen(server.getLastServerInfo());
            optionalCompletableFuture.complete(this.webInterface.getCloud()
                                                                .getScreenProvider()
                                                                .getScreens()
                                                                .containsKey(server.getServiceId().getServerId()));
        } else {
            optionalCompletableFuture.cancel(true);
        }
        return optionalCompletableFuture;
    }

    /**
     * Writes a command to the console from the server
     *
     * @param serverId is used as an indicator for the minecraft server
     * @param command  is the relevant command to be transmitted
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> writeCommand(String serverId, String command) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        MinecraftServer minecraftServer = this.webInterface.getCloud().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().writeServerCommand(command, minecraftServer.getLastServerInfo());
            optionalCompletableFuture.complete(true);
        } else {
            optionalCompletableFuture.cancel(true);
        }
        return optionalCompletableFuture;
    }

    /**
     * Stop a screen from a server
     *
     * @param serverId is used as an indicator for the minecraft server
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> stopServerScreen(String serverId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        MinecraftServer minecraftServer = this.webInterface.getCloud().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().disableScreen(minecraftServer.getLastServerInfo());
            this.webInterface.getCloud()
                             .getScreenProvider()
                             .disableScreen(minecraftServer.getServiceId().getServerId());
            optionalCompletableFuture.complete(!this.webInterface.getCloud()
                                                                 .getScreenProvider()
                                                                 .getScreens()
                                                                 .containsKey(minecraftServer.getServiceId()
                                                                                             .getServerId()));
        } else {
            optionalCompletableFuture.cancel(true);
        }
        return optionalCompletableFuture;
    }

    /**
     * Stops all servers in a group
     *
     * @param serverGroup is used as an indicator for the minecraft servers of a group
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> stopServers(String serverGroup) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getServers().values().forEach(ms -> this.webInterface.getCloud().stopServer(ms));
        optionalCompletableFuture.complete(this.webInterface.getCloud().getServers(serverGroup).size() <= 0);
        return optionalCompletableFuture;
    }

    /**
     * Stops the server based on its id
     *
     * @param serverId is used as an indicator for the minecraft server
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> stopServer(String serverId) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().stopServer(serverId);
        optionalCompletableFuture.complete(!this.webInterface.getCloud().getServers().containsKey(serverId));
        return optionalCompletableFuture;
    }

    /**
     * Starts a server from the group
     *
     * @param serverGroup is used as an indicator for the group
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> startServer(String serverGroup) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getServers().size();
        this.webInterface.getCloud().startGameServerAsync(this.webInterface.getCloud().getServerGroup(serverGroup));
        int newSize = this.webInterface.getCloud().getServers().size();
        optionalCompletableFuture.complete(newSize > lastSize);
        return optionalCompletableFuture;
    }

    /**
     * Starts a certain number of servers from the group
     *
     * @param serverGroup is used as an indicator for the group
     * @param amount      is the number of servers to start
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> startServers(String serverGroup, int amount) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        int lastSize = this.webInterface.getCloud().getServers().size();
        for (int i = 0; i < amount; i++) {
            this.webInterface.getCloud().startGameServerAsync(this.webInterface.getCloud().getServerGroup(serverGroup));
        }
        int newSize = this.webInterface.getCloud().getServers().size();
        optionalCompletableFuture.complete(newSize > lastSize);
        return optionalCompletableFuture;
    }

    /**
     * Updates a server group
     *
     * @param serverGroup is used as an indicator for the group
     * @return true is returned if the operation was successful
     */
    public CompletableFuture<Boolean> updateServerGroup(ServerGroup serverGroup) {
        CompletableFuture<Boolean> optionalCompletableFuture = new CompletableFuture<>();
        this.webInterface.getCloud().getServerGroups().remove(serverGroup.getName());
        this.webInterface.getCloud().getConfig().deleteGroup(serverGroup);
        this.webInterface.getCloud().getConfig().createGroup(serverGroup);
        this.webInterface.getCloud().getServerGroups().put(serverGroup.getName(), serverGroup);
        for (Wrapper wrapper : this.webInterface.getCloud().getWrappers().values()) {
            wrapper.updateWrapper();
        }
        optionalCompletableFuture.complete(true);
        return optionalCompletableFuture;
    }
}
