package me.madfix.cloudnet.webinterface.api.sql;

public final class SQLSelect {

    public static final String SELECT_UPDATE = "SELECT apply from `update` WHERE versionname = ?";
    public static final String SELECT_PERMISSION_FROM_GROUP = "SELECT permission from `group_permission` WHERE gId = ?";
    public static final String SELECT_PERMISSION_FROM_USER = "SELECT permission from `user_permission` WHERE userId = ?";
    public static final String SELECT_PERMISSION_IN_GROUP = "SELECT permission from `group_permission` WHERE permission = ? AND gId = ?";
    public static final String SELECT_PERMISSION_IN_USER = "SELECT permission from `user_permission` WHERE permission = ? AND userId = ?";

    public static final String SELECT_USERNAME_IN_USERS = "SELECT username from `users` WHERE username = ?";
    public static final String SELECT_USER_IN_USERS = "SELECT id, passwordhash from `users` WHERE username = ?";

    public static final String SELECT_GROUP_NAME_IN_GROUPS = "SELECT groupname from `groups` WHERE groupname = ?";
    public static final String SELECT_GROUPS_IN_GROUPS = "SELECT id,groupname from `groups`";
    public static final String SELECT_GROUP_IN_GROUPS = "SELECT id,groupname from `groups` WHERE groupname = ?";

}
