package vip.mcsj.www.karrefinement.datamanager.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseManager {
    void initialize();

    void close() throws SQLException;

    HikariDataSource getDataSource();

    Connection getConnection() throws SQLException;
}
