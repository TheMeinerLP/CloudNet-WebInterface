package me.madfix.cloudnet.webinterface.model;

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
}
