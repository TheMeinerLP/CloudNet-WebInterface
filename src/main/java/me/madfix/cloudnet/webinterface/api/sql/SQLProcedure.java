package me.madfix.cloudnet.webinterface.api.sql;

public final class SQLProcedure {

    public static String SQL_USER_TABLE_PROCEDURE = "CREATE PROCEDURE IF NOT EXISTS `create_tables`()\n" +
            "LANGUAGE SQL\n" +
            "NOT DETERMINISTIC\n" +
            "MODIFIES SQL DATA\n" +
            "SQL SECURITY DEFINER\n" +
            "COMMENT 'Create the table for all user logins'\n" +
            "BEGIN\n" +
            "CREATE TABLE IF NOT EXISTS `users` (id INT(32) PRIMARY KEY AUTO_INCREMENT, username VARCHAR(16) NOT NULL, passwordhash BINARY(64) NOT NULL);\n" +
            "CREATE TABLE IF NOT EXISTS `update` (versionname VARCHAR(16) PRIMARY KEY NOT NULL, apply BOOL);\n" +
            "CREATE TABLE IF NOT EXISTS `user_permission` (id INT(32) PRIMARY KEY AUTO_INCREMENT, userId INT(32), permission VARCHAR(255), FOREIGN KEY (userId) REFERENCES users(id));" +
            "END";

}
