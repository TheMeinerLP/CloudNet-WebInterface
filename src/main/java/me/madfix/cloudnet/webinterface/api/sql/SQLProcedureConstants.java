package me.madfix.cloudnet.webinterface.api.sql;

/**
 * Hold some sql procedure queries
 * @since 1.11.5
 * @version 1.0.0
 */
public final class SQLProcedureConstants {

    public static String SQL_USER_TABLE_PROCEDURE =
            "CREATE PROCEDURE IF NOT EXISTS `create_tables`() LANGUAGE SQL NOT DETERMINISTIC\n" +
            "MODIFIES SQL DATA SQL SECURITY DEFINER COMMENT 'Create the table for all user logins' BEGIN\n" +
            "CREATE TABLE IF NOT EXISTS `users` (id INT(32) PRIMARY KEY AUTO_INCREMENT, username VARCHAR(16) NOT NULL, passwordhash VARBINARY(60) NOT NULL);\n" +
            // Only 60 Bytes allowed for bcrypt bytes
            "CREATE TABLE IF NOT EXISTS `update` (versionname VARCHAR(16) PRIMARY KEY NOT NULL, apply BOOL);\n" +
            "CREATE TABLE IF NOT EXISTS `user_permission` (id INT(32) PRIMARY KEY AUTO_INCREMENT, userId INT(32), permission VARCHAR(255), FOREIGN KEY (userId) REFERENCES users(id));\n" +
            "END";

}
