package com.netcracker.cats.util;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnectionUtil {

    private static final Connection CONNECTION;

    static {
        final Properties properties = new Properties();
        try {
            properties.load(JdbcConnectionUtil.class.getResourceAsStream("/persistence.properties"));
            CONNECTION = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't found the file ");
        } catch (SQLException e) {
            throw new IllegalArgumentException("Wrong SQL connection data");
        }
    }

    public Connection getConnection(){
        return CONNECTION;
    }

}
