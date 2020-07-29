package me.madfix.cloudnet.webinterface.api.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.provider.Provider;
import me.madfix.cloudnet.webinterface.api.sql.SQLInsert;
import me.madfix.cloudnet.webinterface.api.sql.SQLSelect;
import me.madfix.cloudnet.webinterface.model.WebInterfaceUser;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class UserProvider extends Provider {


    public UserProvider(WebInterface webInterface) {
        super(webInterface);
    }

    public CompletableFuture<List<WebInterfaceUser>> getUsers() {
        CompletableFuture<List<WebInterfaceUser>> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_USERS_IN_USERS)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<WebInterfaceUser> webInterfaceUsers = new ArrayList<>();
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String username = resultSet.getString("username");
                        byte[] passwordHash = resultSet.getBytes("passwordhash");
                        webInterfaceUsers.add(new WebInterfaceUser(id,username,passwordHash));
                    }
                    completableFuture.complete(webInterfaceUsers);
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "Users could not be selected from the database ", e);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<WebInterfaceUser> getUser(String username) {
        CompletableFuture<WebInterfaceUser> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_USER_IN_USERS)) {
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
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> isUserExists(String username) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        createConnection().thenAccept(conn -> {
            try (Connection connection = conn;
                 PreparedStatement statement = connection.prepareStatement(SQLSelect.SELECT_USERNAME_IN_USERS)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    completableFuture.complete(resultSet.next());
                }
            } catch (SQLException e) {
                this.webInterface.getLogger().log(Level.SEVERE, "The user could not be selected from the database ", e);
                completableFuture.cancel(true);
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<Boolean> createUser(String username, byte[] passwordHash) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        isUserExists(username).thenAccept(exists -> {
            if (!exists) {
                createConnection().thenAccept(conn -> {
                    try (Connection connection = conn;
                         PreparedStatement statement = connection.prepareStatement(SQLInsert.INSERT_USER_IN_USERS)) {
                        statement.setString(1, username);
                        statement.setBytes(2, passwordHash);

                        completableFuture.complete(statement.executeUpdate() > 0);
                    } catch (SQLException e) {
                        this.webInterface.getLogger().log(Level.SEVERE, "The user could not be created for the database ", e);
                    }
                });
            }
        });
        return completableFuture;
    }

    //TODO: Add documentation
    public CompletableFuture<byte[]> hashPassword(String password) {
        CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();
        this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().ifPresent(cfg -> {
            completableFuture.complete(BCrypt.withDefaults().hash(6,
                    password.getBytes(StandardCharsets.UTF_8)));
        });
        return completableFuture;
    }
}
