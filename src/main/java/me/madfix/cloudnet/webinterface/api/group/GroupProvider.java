package me.madfix.cloudnet.webinterface.api.group;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.model.WebInterfaceGroup;
import me.madfix.cloudnet.webinterface.model.WebInterfaceUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class GroupProvider {

    private final WebInterface webInterface;

    public GroupProvider(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public CompletableFuture<Boolean> isGroupExists(String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement("SELECT groupname from `groups` WHERE groupname = ?")) {
                    statement.setString(1, name);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        completableFuture.complete(resultSet.next());
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The group could not be selected from the database ", e);
                    completableFuture.cancel(true);
                }
            }
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    public CompletableFuture<Optional<List<WebInterfaceGroup>>> getGroups() {
        CompletableFuture<Optional<List<WebInterfaceGroup>>> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement("SELECT id,groupname from `groups`")) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        List<WebInterfaceGroup> groups = new ArrayList<>();
                        while (resultSet.next()) {
                            groups.add(new WebInterfaceGroup(resultSet.getInt("id"), resultSet.getString("groupname")));
                        }
                        completableFuture.complete(Optional.of(groups));
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The groups could not be selected from the database", e);
                    completableFuture.cancel(true);
                }
            }
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    public CompletableFuture<WebInterfaceGroup> getGroup(String name) {
        CompletableFuture<WebInterfaceGroup> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            isGroupExists(name).thenAccept(exists -> {
                if (exists) {
                    Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
                    if (optionalConnection.isPresent()) {
                        try (Connection connection = optionalConnection.get();
                             PreparedStatement statement = connection.prepareStatement("SELECT id,groupname from `groups` WHERE groupname = ?")) {
                            statement.setString(1, name);
                            try (ResultSet resultSet = statement.executeQuery()) {
                                if (resultSet.next()) {
                                    WebInterfaceGroup interfaceGroup = new WebInterfaceGroup(resultSet.getInt("id"), resultSet.getString("groupname"));
                                    completableFuture.complete(interfaceGroup);
                                } else {
                                    completableFuture.cancel(true);
                                }
                            }
                        } catch (SQLException e) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The group could not be created into the database", e);
                            completableFuture.cancel(true);
                        }
                    }
                } else {
                    completableFuture.cancel(true);
                }
            });
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    public CompletableFuture<Boolean> addUserToGroup(WebInterfaceGroup group, WebInterfaceUser user, int potency) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            isGroupExists(group.getName()).thenAccept(exists -> {
                if (exists) {
                    Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
                    if (optionalConnection.isPresent()) {
                        try (Connection connection = optionalConnection.get();
                             PreparedStatement statement = connection.prepareStatement("INSERT INTO `user_groups` (gId,uId,potency) VALUE (?,?,?)")) {
                            statement.setInt(1, group.getId());
                            statement.setInt(2, user.getId());
                            statement.setInt(3, potency);

                            completableFuture.complete(statement.executeUpdate() > 0);
                        } catch (SQLException e) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The user could not be linked to group in the database", e);
                            completableFuture.cancel(true);
                        }
                    }
                } else {
                    completableFuture.cancel(true);
                }
            });
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    public CompletableFuture<Boolean> createGroup(String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            isGroupExists(name).thenAccept(exists -> {
                if (!exists) {
                    Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
                    if (optionalConnection.isPresent()) {
                        try (Connection connection = optionalConnection.get();
                             PreparedStatement statement = connection.prepareStatement("INSERT INTO `groups` (groupname) VALUE (?)")) {
                            statement.setString(1, name);
                            completableFuture.complete(statement.executeUpdate() > 0);
                        } catch (SQLException e) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The group could not be created into the database", e);
                            completableFuture.cancel(true);
                        }
                    }
                } else {
                    completableFuture.cancel(true);
                }
            });
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

}
