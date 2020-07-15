package me.madfix.cloudnet.webinterface.api.setup;

import me.madfix.cloudnet.webinterface.WebInterface;
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
                try(PreparedStatement statement = connection.prepareStatement(SQLProcedure.SQL_USER_TABLE_PROCEDURE)) {
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
                try(PreparedStatement statement = connection.prepareStatement("CALL `create_user_table`()")) {
                    statement.execute();
                } catch (SQLException e) {
                    this.webInterface.getLogger().log(Level.SEVERE, "User table procedures could not be called", e);
                }
            });
        }
    }
}
