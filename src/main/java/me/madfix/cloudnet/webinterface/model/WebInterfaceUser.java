package me.madfix.cloudnet.webinterface.model;

public final class WebInterfaceUser {

    private final int id;
    private final String username;
    private final byte[] passwordHash;

    public WebInterfaceUser(int id, String username, byte[] passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
