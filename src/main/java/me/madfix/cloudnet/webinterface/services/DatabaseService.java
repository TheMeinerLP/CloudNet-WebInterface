package me.madfix.cloudnet.webinterface.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.dytanic.cloudnetcore.CloudNet;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.model.InterfaceConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public final class DatabaseService {

    private final WebInterface webInterface;
    private final HikariConfig hikariConfig = new HikariConfig();
    private HikariDataSource hikariDataSource;

    public DatabaseService(WebInterface webInterface) {
        this.webInterface = webInterface;
        loadConfig();
    }

    private void loadConfig() {
        if (this.webInterface.getConfigurationService()
                .getOptionalInterfaceConfiguration().isPresent()) {
            final InterfaceConfiguration interfaceConfiguration = this.webInterface
                    .getConfigurationService().getOptionalInterfaceConfiguration().get();
            this.hikariConfig
                    .setUsername(interfaceConfiguration.getDatabaseConfiguration().getUsername());
            this.hikariConfig
                    .setPassword(interfaceConfiguration.getDatabaseConfiguration().getPassword());
            this.hikariConfig.setJdbcUrl(interfaceConfiguration.getDatabaseConfiguration().getJdbcUrl());
            this.hikariConfig.setConnectionTimeout(
                    interfaceConfiguration.getDatabaseConfiguration().getConnectionTimeout());
            this.hikariConfig
                    .setMinimumIdle(interfaceConfiguration.getDatabaseConfiguration().getMinimumIdle());
            this.hikariConfig.setMaximumPoolSize(
                    interfaceConfiguration.getDatabaseConfiguration().getMaximumPoolSize());
            this.hikariConfig
                    .setMaxLifetime(interfaceConfiguration.getDatabaseConfiguration().getMaxLifetime());
            this.hikariConfig.addDataSourceProperty("cachePrepStmts",
                    interfaceConfiguration.getDatabaseConfiguration().isCachePrepStmts());
            this.hikariConfig.addDataSourceProperty("prepStmtCacheSize",
                    interfaceConfiguration.getDatabaseConfiguration().getPrepStmtCacheSize());
            this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit",
                    interfaceConfiguration.getDatabaseConfiguration().getPrepStmtCacheSqlLimit());
            CloudNet.getLogger().info("The loading of the database configuration was successful!");
            createDataSource();
        }
    }

    private void createDataSource() {
        this.hikariDataSource = new HikariDataSource(this.hikariConfig);
        this.webInterface.getLogger().info("Creating the data source was successful!");
    }

    public Optional<HikariDataSource> getDataSource() {
        return Optional.of(this.hikariDataSource);
    }

    public Optional<Connection> getConnection() {
        Optional<Connection> optionalConnection = Optional.empty();
        if (getDataSource().isPresent()) {
            try {
                optionalConnection = Optional.of(this.hikariDataSource.getConnection());
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return optionalConnection;
    }

}
