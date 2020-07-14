package me.madfix.cloudnet.webinterface.model;

import java.util.Objects;

public final class InterfaceConfiguration {

    private final int restPort;
    private final int webSocketPort;
    private final String host;
    private final boolean mobSystem;
    private final boolean signSystem;
    private final boolean permissionSystem;
    private final DatabaseConfiguration databaseConfiguration;

    public InterfaceConfiguration(int restPort, int webSocketPort, String host,
                                  boolean mobSystem, boolean signSystem, boolean permissionSystem, DatabaseConfiguration databaseConfiguration) {
        this.restPort = restPort;
        this.webSocketPort = webSocketPort;
        this.host = host;
        this.mobSystem = mobSystem;
        this.signSystem = signSystem;
        this.permissionSystem = permissionSystem;
        this.databaseConfiguration = databaseConfiguration;
    }

    public int getRestPort() {
        return restPort;
    }

    public int getWebSocketPort() {
        return webSocketPort;
    }

    public String getHost() {
        return host;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public boolean isMobSystem() {
        return mobSystem;
    }

    public boolean isPermissionSystem() {
        return permissionSystem;
    }

    public boolean isSignSystem() {
        return signSystem;
    }

}
