package me.madfix.cloudnet.webinterface.api.sql;

public final class SQLSelect {

    public static final String SELECT_UPDATE = "SELECT apply from `update` WHERE versionname = ?";
    public static final String SELECT_PERMISSION_FROM_GROUP = "SELECT permission from `group_permission` WHERE gId = ?";
    public static final String SELECT_PERMISSION_FROM_USER = "SELECT permission from `user_permission` WHERE userId = ?";
    public static final String SELECT_PERMISSION_IN_GROUP = "SELECT permission from `group_permission` WHERE permission = ? AND gId = ?";
    public static final String SELECT_PERMISSION_IN_USER = "SELECT permission from `user_permission` WHERE permission = ? AND userId = ?";

}
