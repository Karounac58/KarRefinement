package vip.mcsj.www.karrefinement.datamanager.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.PlayerStatsDataManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQLDatabaseManager implements DatabaseManager {
    public HikariDataSource dataSource;

    public KarRefinement plugin;

    public MySQLDatabaseManager(KarRefinement plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("settings.data.MySQL");
        setupDataSource(config);

        try(Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()){
            stmt.execute("CREATE TABLE IF NOT EXISTS refinementpotion_data (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "start_timestamp BIGINT NOT NULL," +
                    "end_timestamp BIGINT NOT NULL," +
                    "success_rate DECIMAL(3,2) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS refinementdarkchange_data (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "refining_success TINYINT(1) NOT NULL," +
                    "count INT NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");
            
            // 初始化玩家统计表
            PlayerStatsDataManager.initializeTable();
        }catch(SQLException e){
            plugin.getLogger().log(Level.SEVERE,"数据库初始化失败!",e);
        }
    }

    @Override
    public void close() throws SQLException {
        this.dataSource.close();
    }

    @Override
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void setupDataSource(ConfigurationSection config) {
        String host = config.getString("Host");
        String port = config.getString("Port");
        String database = config.getString("Database");
        String username = config.getString("Username");
        String password = config.getString("Password");
        String parameters = config.getString("Parameters", "");

        // 添加必要的连接参数
        if (!parameters.isEmpty() && !parameters.startsWith("?")) {
            parameters = "?" + parameters;
        }

        // 添加必要的安全参数
        parameters += (parameters.isEmpty() ? "?" : "&")
                + "allowPublicKeyRetrieval=true"
                + "&useSSL=false"
                + "&serverTimezone=UTC";

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + parameters;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        // 连接池配置
        ConfigurationSection poolConfig = config.getConfigurationSection("Pool");
        if (poolConfig != null) {
            hikariConfig.setMaximumPoolSize(poolConfig.getInt("MaximumPoolSize", 10));
            hikariConfig.setMinimumIdle(poolConfig.getInt("MinimumIdle", 5));
            hikariConfig.setMaxLifetime(poolConfig.getLong("MaxLifetime", 1800000));
            hikariConfig.setConnectionTimeout(poolConfig.getLong("ConnectionTimeout", 30000));
            hikariConfig.setIdleTimeout(poolConfig.getLong("IdleTimeout", 600000));
        }

        // 高级配置
        ConfigurationSection advancedConfig = config.getConfigurationSection("Advanced");
        if (advancedConfig != null) {
            hikariConfig.setAutoCommit(advancedConfig.getBoolean("AutoCommit", true));
            hikariConfig.setConnectionTestQuery(advancedConfig.getString("ConnectionTestQuery", "SELECT 1"));
            hikariConfig.addDataSourceProperty("cachePrepStmts", advancedConfig.getBoolean("CachePrepStmts", true));
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", advancedConfig.getInt("PrepStmtCacheSize", 250));
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", advancedConfig.getInt("PrepStmtCacheSqlLimit", 2048));
        }

        dataSource = new HikariDataSource(hikariConfig);
    }
}
