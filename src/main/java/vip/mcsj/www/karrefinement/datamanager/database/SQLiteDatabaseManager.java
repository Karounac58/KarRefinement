package vip.mcsj.www.karrefinement.datamanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLiteDatabaseManager implements DatabaseManager{
    public HikariDataSource dataSource;

    public KarRefinement plugin;

    public SQLiteDatabaseManager(KarRefinement plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        try{
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + "/data.db");
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);

            try(Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()){
                stmt.execute("CREATE TABLE IF NOT EXISTS refinementpotion_data (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "player_uuid TEXT NOT NULL," +
                        "start_timestamp INTEGER NOT NULL," +
                        "end_timestamp INTEGER NOT NULL," +
                        "success_rate REAL NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");");
                stmt.execute("CREATE TABLE IF NOT EXISTS refinementdarkchange_data (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "player_uuid TEXT UNIQUE NOT NULL," +
                        "refining_success BOOLEAN NOT NULL," +
                        "count INTEGER NOT NULL," +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");");
            }
            plugin.getLogger().info("数据库初始化完成");
        }catch(SQLException e){
            plugin.getLogger().log(Level.SEVERE,"数据库初始化失败!",e);
        }
    }

    @Override
    public void close() throws SQLException {
        dataSource.close();
    }

    @Override
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
