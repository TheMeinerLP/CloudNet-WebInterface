package me.madfix.cloudnet.webinterface.api.setup;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.builder.PasswordGenerator;
import me.madfix.cloudnet.webinterface.api.sql.SQLProcedure;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public final class SetupHandler {

    private final WebInterface webInterface;

    public SetupHandler(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public void setupPreSql() {
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            this.webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(SQLProcedure.SQL_USER_TABLE_PROCEDURE)) {
                    statement.execute();
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "User table procedures could not be created", e);
                }
            });

        }
    }

    public void setupPostSql() {
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            this.webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
                try (PreparedStatement statement = connection.prepareStatement("CALL `create_tables`()")) {
                    statement.execute();
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "User table procedures could not be called", e);
                }
            });
        }
    }

    public void setupPreAdminUser() {
        this.webInterface.getUserProvider().userExists("admin").thenAccept(exists -> {
            if (!exists) {
                PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                        .useDigits(true)
                        .useLower(true)
                        .usePunctuation(true)
                        .useUpper(true)
                        .build();
                String password = passwordGenerator.generate(8);
                this.webInterface.getLogger().log(Level.INFO, "No admin user was found!");
                this.webInterface.getLogger().log(Level.INFO, "An Admin user is created with all rights!");
                this.webInterface.getLogger().log(Level.INFO, "The password for the user \"admin\" is: {0}", new Object[]{password});
                this.webInterface.getUserProvider().hashPassword(password).thenAccept(passwordHash -> {
                    this.webInterface.getUserProvider().createUser("admin", passwordHash).thenAccept(success -> {
                        if (success) {
                            this.webInterface.getUserProvider().geUser("admin").thenAccept(interfaceUser -> {
                                this.webInterface.getPermissionProvider().addPermission(interfaceUser.getId(), "*").thenAccept(permissionSuccess -> {
                                    if (permissionSuccess) {
                                        this.webInterface.getLogger().log(Level.INFO, "The user \"admin\" was successfully created with all rights!");
                                    }
                                });
                            });
                        }
                    });
                });
            }
        });
    }
}
