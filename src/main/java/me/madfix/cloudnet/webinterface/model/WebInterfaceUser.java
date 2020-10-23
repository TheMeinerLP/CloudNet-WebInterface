package me.madfix.cloudnet.webinterface.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Hold some information's about a web interface user
 * @since 1.11.5
 * @version 1.0.0
 */
public class WebInterfaceUser {

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

    @Override public String toString() {
        return "WebInterfaceUser{" + "id=" + id + ", username='" + username + '\'' + ", passwordHash=" +
               Arrays.toString(passwordHash) + '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebInterfaceUser that = (WebInterfaceUser) o;
        return id == that.id && Objects.equals(username, that.username) &&
               Arrays.equals(passwordHash, that.passwordHash);
    }

    @Override public int hashCode() {
        int result = Objects.hash(id, username);
        result = 31 * result + Arrays.hashCode(passwordHash);
        return result;
    }
}
