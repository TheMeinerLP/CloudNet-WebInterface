package me.madfix.cloudnet.webinterface.api.group;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.provider.Provider;
import me.madfix.cloudnet.webinterface.api.sql.SQLInsert;
import me.madfix.cloudnet.webinterface.api.sql.SQLSelect;
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

public final class GroupProvider extends Provider {

    public GroupProvider(WebInterface webInterface) {
        super(webInterface);
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> isGroupExists(String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_GROUP_NAME_IN_GROUPS)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    completableFuture.complete(resultSet.next());
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The group could not be selected from the database ", e);
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<List<WebInterfaceGroup>> getGroups() {
        CompletableFuture<List<WebInterfaceGroup>> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_GROUPS_IN_GROUPS)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<WebInterfaceGroup> groups = new ArrayList<>();
                    while (resultSet.next()) {
                        groups.add(new WebInterfaceGroup(resultSet.getInt("id"), resultSet.getString("groupname")));
                    }
                    completableFuture.complete(groups);
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The groups could not be selected from the database", e);
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<WebInterfaceGroup> getGroup(String name) {
        CompletableFuture<WebInterfaceGroup> completableFuture = new CompletableFuture<>();
        isGroupExists(name).thenAccept(exists -> {
            if (exists) {
                createConnection().thenAccept(conn -> {
                    try (Connection connection = conn;
                         PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_GROUP_IN_GROUPS)) {
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
                        completableFuture.completeExceptionally(e);
                    }
                });
            } else {
                completableFuture.completeExceptionally(new NullPointerException("The specified group was not found"));
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> addUserToGroup(WebInterfaceGroup group, WebInterfaceUser user, int potency) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        isGroupExists(group.getName()).thenAccept(exists -> {
            if (exists) {
                createConnection().thenAccept(conn -> {
                    try (Connection connection = conn;
                         PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_USER_IN_GROUP)) {
                        statement.setInt(1, group.getId());
                        statement.setInt(2, user.getId());
                        statement.setInt(3, potency);

                        completableFuture.complete(statement.executeUpdate() > 0);
                    } catch (SQLException e) {
                        this.webInterface.getLogger().log(Level.SEVERE, "The user could not be linked to group in the database", e);
                        completableFuture.completeExceptionally(e);
                    }
                });
            } else {
                completableFuture.completeExceptionally(new NullPointerException("The specified group was not found"));
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> createGroup(String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        isGroupExists(name).thenAccept(exists -> {
            if (!exists) {
                createConnection().thenAccept(conn -> {
                    try (Connection connection = conn;
                         PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_GROUP_IN_GROUPS)) {
                        statement.setString(1, name);
                        completableFuture.complete(statement.executeUpdate() > 0);
                    } catch (SQLException e) {
                        this.webInterface.getLogger().log(Level.SEVERE, "The group could not be created into the database", e);
                        completableFuture.cancel(true);
                    }
                });
            } else {
                completableFuture.completeExceptionally(new RuntimeException("The specified group already exists"));
            }
        });
        return completableFuture;
    }

}
