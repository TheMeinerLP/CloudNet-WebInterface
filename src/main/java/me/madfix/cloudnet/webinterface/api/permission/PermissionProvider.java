package me.madfix.cloudnet.webinterface.api.permission;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.sql.SQLInsert;
import me.madfix.cloudnet.webinterface.api.sql.SQLSelect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class PermissionProvider {

    private final WebInterface webInterface;

    public PermissionProvider(WebInterface webInterface) {
        this.webInterface = webInterface;
    }


    public CompletableFuture<Optional<List<String>>> getGroupPermissions(int groupid) {
        CompletableFuture<Optional<List<String>>> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            final Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_FROM_GROUP)) {
                    statement.setInt(1, groupid);
                    List<String> permissions = new ArrayList<>();
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            permissions.add(resultSet.getString("permission"));
                        }
                        completableFuture.complete(Optional.of(permissions));
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission for the group could not be selected from the database ", e);
                }
            }
        }
        return completableFuture;
    }

    public CompletableFuture<Optional<List<String>>> getUserPermissions(int userId) {
        CompletableFuture<Optional<List<String>>> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            final Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_FROM_USER)) {
                    statement.setInt(1, userId);
                    List<String> permissions = new ArrayList<>();
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            permissions.add(resultSet.getString("permission"));
                        }
                        completableFuture.complete(Optional.of(permissions));
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission for the user could not be selected from the database ", e);
                }
            }
        }
        return completableFuture;
    }

    public CompletableFuture<Boolean> addGroupPermission(int groupId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            final Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (PreparedStatement statement = optionalConnection.get().prepareStatement(SQLSelect.SELECT_PERMISSION_IN_GROUP)) {
                    statement.setString(1, permission);
                    statement.setInt(2, groupId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The permission already exists in the database for this group!");
                            completableFuture.cancel(true);
                        }
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission for the group could not be selected from the database ", e);
                }
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_GROUP_PERMISSION)) {
                    statement.setInt(1, groupId);
                    statement.setString(2, permission);
                    completableFuture.complete(statement.executeUpdate() > 0);
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission could not be created for the database ", e);
                }
            }
        }
        return completableFuture;
    }


    public CompletableFuture<Boolean> addUserPermission(int userId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            this.webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_PERMISSION_IN_USER)) {
                    statement.setString(1, permission);
                    statement.setInt(2, userId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The permission already exists in the database for this user!");
                            completableFuture.cancel(true);
                        }
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission for the user could not be selected from the database ", e);
                }
                try (PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_USER_PERMISSION)) {
                    statement.setInt(1, userId);
                    statement.setString(2, permission);
                    completableFuture.complete(statement.execute());
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission could not be created for the database ", e);
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        this.webInterface.getLogger().log(Level.SEVERE, "Something is wrong on closing a connection to sql ", ex);
                    }
                }
            });
        }
        return completableFuture;
    }
}
