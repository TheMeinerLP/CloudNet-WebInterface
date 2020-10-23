package me.madfix.cloudnet.webinterface.model;

import java.util.Objects;

/**
 * Hold some information'S about a web interface group
 * @since 1.11.5
 * @version 1.0.0
 */
public final class WebInterfaceGroup {

    private final int id;
    private final String name;

    public WebInterfaceGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override public String toString() {
        return "WebInterfaceGroup{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebInterfaceGroup that = (WebInterfaceGroup) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override public int hashCode() {
        return Objects.hash(id, name);
    }
}
