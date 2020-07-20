package me.madfix.cloudnet.webinterface.api.permission;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.provider.Provider;
import me.madfix.cloudnet.webinterface.api.sql.SQLInsert;
import me.madfix.cloudnet.webinterface.api.sql.SQLSelect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class PermissionProvider extends Provider {

    public PermissionProvider(WebInterface webInterface) {
        super(webInterface);
    }


    //TODO: Add documentation
    public CompletableFuture<List<String>> getGroupPermissions(int groupId) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_FROM_GROUP)) {
                statement.setInt(1, groupId);
                List<String> permissions = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        permissions.add(resultSet.getString("permission"));
                    }
                    completableFuture.complete(permissions);
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The permission for the group could not be selected from the database ", e);
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<List<String>> getUserPermissions(int userId) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_FROM_USER)) {
                statement.setInt(1, userId);
                List<String> permissions = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        permissions.add(resultSet.getString("permission"));
                    }
                    completableFuture.complete(permissions);
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The permission for the user could not be selected from the database ", e);
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }


    //TODO: Add documentation
    public CompletableFuture<Boolean> hasGroupPermission(int groupId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_IN_GROUP)) {
                statement.setString(1, permission);
                statement.setInt(2, groupId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        completableFuture.complete(true);
                    }
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The permission for the group could not be selected from the database ", e);
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> addGroupPermission(int groupId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        hasGroupPermission(groupId, permission).thenAccept(hasPermission -> {
            if (!hasPermission) {
                createConnection().thenAccept(conn -> {
                    try (Connection connection = conn;
                         PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_GROUP_PERMISSION)) {
                        statement.setInt(1, groupId);
                        statement.setString(2, permission);
                        completableFuture.complete(statement.executeUpdate() > 0);
                    } catch (SQLException e) {
                        this.webInterface.getLogger().log(Level.SEVERE, "The permission could not be created for the database ", e);
                        completableFuture.completeExceptionally(e);
                    }
                });
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> hasUserPermission(int userId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_IN_USER)) {
                statement.setString(1, permission);
                statement.setInt(2, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        completableFuture.complete(true);
                    }
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The permission for the user could not be selected from the database ", e);
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> addUserPermission(int userId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        hasUserPermission(userId, permission).thenAccept(hasPermission -> {
            if (!hasPermission) {
                createConnection().thenAccept(conn -> {
                    try (Connection connection = conn;
                         PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_USER_PERMISSION)) {
                        statement.setInt(1, userId);
                        statement.setString(2, permission);
                        completableFuture.complete(statement.execute());
                    } catch (SQLException e) {
                        this.webInterface.getLogger().log(Level.SEVERE, "The permission could not be created for the database ", e);
                        completableFuture.completeExceptionally(e);
                    }
                });
            }
        });

        return completableFuture;
    }
}
