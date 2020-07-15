package me.madfix.cloudnet.webinterface.api.sql;

public final class SQLProcedure {

    public static String SQL_USER_TABLE_PROCEDURE = "CREATE PROCEDURE `create_user_table`()\n" +
            "LANGUAGE SQL\n" +
            "NOT DETERMINISTIC\n" +
            "MODIFIES SQL DATA\n" +
            "SQL SECURITY DEFINER\n" +
            "COMMENT 'Create the table for all user logins'\n" +
            "BEGIN\n" +
            "\tCREATE TABLE IF NOT EXISTS `users` (id INT(32) PRIMARY KEY AUTO_INCREMENT, username VARCHAR(16) NOT NULL, passwordhash BINARY(64) NOT NULL);\n" +
            "END";

}
