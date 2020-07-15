package me.madfix.cloudnet.webinterface.api.permission;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.model.WebInterfaceUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class PermissionProvider {

    private final WebInterface webInterface;

    public PermissionProvider(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public CompletableFuture<Boolean> addPermission(int userId, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            this.webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
                try (PreparedStatement statement = connection.prepareStatement("SELECT permission from `user_permission` WHERE permission = ? AND userId = ?")) {
                    statement.setString(1, permission);
                    statement.setInt(2, userId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.first()) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The permission already exists in the database for this user!");
                            completableFuture.cancel(true);
                        }
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission for the user could not be selected from the database ", e);
                }
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `user_permission` (userId, permission) VALUES (?,?)")) {
                    statement.setInt(1, userId);
                    statement.setString(2, permission);
                    completableFuture.complete(statement.execute());
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The permission could not be created for the database ", e);
                }
            });
        }
        return completableFuture;
    }
}
