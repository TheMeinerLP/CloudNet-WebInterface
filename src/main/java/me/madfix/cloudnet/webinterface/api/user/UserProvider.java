package me.madfix.cloudnet.webinterface.api.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.model.WebInterfaceUser;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class UserProvider {

    private final WebInterface webInterface;

    public UserProvider(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    //TODO: Add documentation
    public CompletableFuture<WebInterfaceUser> getUser(String username) {
        CompletableFuture<WebInterfaceUser> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            final Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement("SELECT id, passwordhash from `users` WHERE username = ?")) {
                    statement.setString(1, username);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.first()) {
                            int id = resultSet.getInt("id");
                            byte[] passwordHash = resultSet.getBytes("passwordhash");
                            completableFuture.complete(new WebInterfaceUser(id, username, passwordHash));
                        }
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The user could not be selected from the database ", e);
                }
            }
        }
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> isUserExists(String username) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement("SELECT username from `users` WHERE username = ?")) {
                    statement.setString(1, username);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        completableFuture.complete(resultSet.next());
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The user could not be selected from the database ", e);
                    completableFuture.cancel(true);
                }
            }
        } else {
            completableFuture.cancel(true);
        }
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> createUser(String username, byte[] passwordHash) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            Optional<Connection> optionalConnection = this.webInterface.getDatabaseService().getConnection();
            if (optionalConnection.isPresent()) {
                try (PreparedStatement statement = optionalConnection.get().prepareStatement("SELECT username from `users` WHERE username = ?")) {
                    statement.setString(1, username);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            this.webInterface.getLogger().log(Level.SEVERE, "The user already exists in the database!");
                            completableFuture.cancel(true);
                        }
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The user could not be selected from the database ", e);
                }
                try (Connection connection = optionalConnection.get();
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO `users` (username, passwordhash) VALUES (?,?)")) {
                    statement.setString(1, username);
                    statement.setBytes(2, passwordHash);

                    completableFuture.complete(statement.executeUpdate() > 0);
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "The user could not be created for the database ", e);
                }
            }
        }
        return completableFuture;
    }

    public CompletableFuture<byte[]> hashPassword(String password) {
        CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();
        this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().ifPresent(cfg -> {
            byte[] passwordHash = BCrypt.withDefaults().hash(6,
                    cfg.getPasswordSalt().getBytes(StandardCharsets.UTF_8),
                    password.getBytes(StandardCharsets.UTF_8));
            completableFuture.complete(passwordHash);
        });
        return completableFuture;
    }
}
