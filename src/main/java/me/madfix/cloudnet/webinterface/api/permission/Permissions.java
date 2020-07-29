package me.madfix.cloudnet.webinterface.api.permission;

public enum Permissions {
    GET_WEB_USER("get.user"),
    GET_WEB_USERS("get.userS");

    private final String permissionString;

    Permissions(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getPermissionString() {
        return permissionString;
    }
}
