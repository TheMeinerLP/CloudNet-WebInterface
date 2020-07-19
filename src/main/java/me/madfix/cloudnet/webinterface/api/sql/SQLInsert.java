package me.madfix.cloudnet.webinterface.api.sql;

public final class SQLInsert {

    public static final String INSERT_UPDATE = "INSERT INTO `update` (versionname,apply) VALUE (?,?)";
    public static final String INSERT_GROUP_PERMISSION = "INSERT INTO `group_permission` (gId, permission) VALUES (?,?)";
    public static final String INSERT_USER_PERMISSION = "INSERT INTO `user_permission` (userId, permission) VALUES (?,?)";

    public static final String INSERT_USER_IN_USERS = "INSERT INTO `users` (username, passwordhash) VALUES (?,?)";

    public static final String INSERT_USER_IN_GROUP = "INSERT INTO `user_groups` (gId,uId,potency) VALUE (?,?,?)";
    public static final String INSERT_GROUP_IN_GROUPS = "INSERT INTO `groups` (groupname) VALUE (?)";

}
