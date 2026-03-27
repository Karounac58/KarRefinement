package vip.mcsj.www.karrefinement.datamanager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.api.model.IPlayerStats;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.main.KarRefinement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家淬炼统计数据管理器
 * 负责记录和查询玩家的淬炼统计信息
 */
public class PlayerStatsDataManager implements Service {
    
    // 缓存玩家统计数据，提高查询性能
    private static final ConcurrentHashMap<UUID, PlayerStats> statsCache = new ConcurrentHashMap<>();
    
    /**
     * 玩家统计数据对象
     */
    public static class PlayerStats implements IPlayerStats {
        private final UUID playerUUID;
        private int totalAttempts;      // 总尝试次数
        private int highestLevel;       // 最高淬炼等级
        private int totalSuccesses;     // 总成功次数
        private int totalFailures;      // 总失败次数
        private int currentSuitLevel;   // 当前套装效果等级
        
        public PlayerStats(UUID playerUUID) {
            this.playerUUID = playerUUID;
            this.totalAttempts = 0;
            this.highestLevel = 0;
            this.totalSuccesses = 0;
            this.totalFailures = 0;
            this.currentSuitLevel = 0;
        }
        
        public PlayerStats(UUID playerUUID, int totalAttempts, int highestLevel, 
                          int totalSuccesses, int totalFailures, int currentSuitLevel) {
            this.playerUUID = playerUUID;
            this.totalAttempts = totalAttempts;
            this.highestLevel = highestLevel;
            this.totalSuccesses = totalSuccesses;
            this.totalFailures = totalFailures;
            this.currentSuitLevel = currentSuitLevel;
        }
        
        // Getters
        public UUID getPlayerUUID() { return playerUUID; }
        public int getTotalAttempts() { return totalAttempts; }
        public int getHighestLevel() { return highestLevel; }
        public int getTotalSuccesses() { return totalSuccesses; }
        public int getTotalFailures() { return totalFailures; }
        public int getCurrentSuitLevel() { return currentSuitLevel; }
        
        // Setters
        public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }
        public void setHighestLevel(int highestLevel) { this.highestLevel = highestLevel; }
        public void setTotalSuccesses(int totalSuccesses) { this.totalSuccesses = totalSuccesses; }
        public void setTotalFailures(int totalFailures) { this.totalFailures = totalFailures; }
        public void setCurrentSuitLevel(int currentSuitLevel) { this.currentSuitLevel = currentSuitLevel; }
        
        // 便捷方法
        public void incrementAttempts() { this.totalAttempts++; }
        public void incrementSuccesses() { this.totalSuccesses++; }
        public void incrementFailures() { this.totalFailures++; }
        
        public void updateHighestLevel(int level) {
            if (level > this.highestLevel) {
                this.highestLevel = level;
            }
        }
    }
    
    /**
     * 初始化数据库表
     */
    @Override
    public void initialize() {
        initializeTable();
    }

    @Override
    public void shutdown() {
        clearCache();
    }

    public static void initializeTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS player_refinement_stats (" +
                "player_uuid VARCHAR(36) PRIMARY KEY," +
                "total_attempts INT DEFAULT 0," +
                "highest_level INT DEFAULT 0," +
                "total_successes INT DEFAULT 0," +
                "total_failures INT DEFAULT 0," +
                "current_suit_level INT DEFAULT 0," +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = KarRefinement.dm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.execute();
        } catch (SQLException e) {
            KarRefinement.instance.getLogger().severe("创建玩家统计表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取玩家统计数据（优先从缓存获取）
     */
    public static PlayerStats getPlayerStats(UUID playerUUID) {
        // 先检查缓存
        PlayerStats cached = statsCache.get(playerUUID);
        if (cached != null) {
            return cached;
        }
        
        // 从数据库加载
        return loadPlayerStatsFromDB(playerUUID);
    }
    
    /**
     * 从数据库加载玩家统计数据
     */
    private static PlayerStats loadPlayerStatsFromDB(UUID playerUUID) {
        String sql = "SELECT * FROM player_refinement_stats WHERE player_uuid = ?";
        
        try (Connection conn = KarRefinement.dm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    PlayerStats stats = new PlayerStats(
                            playerUUID,
                            rs.getInt("total_attempts"),
                            rs.getInt("highest_level"),
                            rs.getInt("total_successes"),
                            rs.getInt("total_failures"),
                            rs.getInt("current_suit_level")
                    );
                    // 放入缓存
                    statsCache.put(playerUUID, stats);
                    return stats;
                }
            }
        } catch (SQLException e) {
            KarRefinement.instance.getLogger().severe("加载玩家统计数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 数据库中没有记录，创建新的
        PlayerStats newStats = new PlayerStats(playerUUID);
        statsCache.put(playerUUID, newStats);
        savePlayerStats(newStats); // 保存到数据库
        return newStats;
    }
    
    /**
     * 保存玩家统计数据到数据库
     */
    public static void savePlayerStats(PlayerStats stats) {
        String sql = "INSERT INTO player_refinement_stats " +
                "(player_uuid, total_attempts, highest_level, total_successes, total_failures, current_suit_level) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "total_attempts = VALUES(total_attempts), " +
                "highest_level = VALUES(highest_level), " +
                "total_successes = VALUES(total_successes), " +
                "total_failures = VALUES(total_failures), " +
                "current_suit_level = VALUES(current_suit_level), " +
                "last_updated = CURRENT_TIMESTAMP";
        
        // SQLite 版本使用不同的语法
        String sqliteSQL = "INSERT OR REPLACE INTO player_refinement_stats " +
                "(player_uuid, total_attempts, highest_level, total_successes, total_failures, current_suit_level) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        String storageType = KarRefinement.instance.getConfig().getString("settings.data.storage", "SQLite");
        String actualSQL = storageType.equalsIgnoreCase("MySQL") ? sql : sqliteSQL;
        
        try (Connection conn = KarRefinement.dm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(actualSQL)) {
            pstmt.setString(1, stats.getPlayerUUID().toString());
            pstmt.setInt(2, stats.getTotalAttempts());
            pstmt.setInt(3, stats.getHighestLevel());
            pstmt.setInt(4, stats.getTotalSuccesses());
            pstmt.setInt(5, stats.getTotalFailures());
            pstmt.setInt(6, stats.getCurrentSuitLevel());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            KarRefinement.instance.getLogger().severe("保存玩家统计数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 异步保存玩家统计数据到数据库（避免持锁时间过长）
     */
    private static void savePlayerStatsAsync(PlayerStats statsCopy) {
        new BukkitRunnable() {
            @Override
            public void run() {
                savePlayerStats(statsCopy);
            }
        }.runTaskAsynchronously(KarRefinement.instance);
    }
    
    /**
     * 记录淬炼尝试（增加总尝试次数）
     */
    public static void recordAttempt(OfflinePlayer player) {
        PlayerStats stats = getPlayerStats(player.getUniqueId());
        synchronized (stats) {
            stats.incrementAttempts();
        }
        savePlayerStatsAsync(stats);
    }
    
    /**
     * 记录淬炼成功
     */
    public static void recordSuccess(OfflinePlayer player, int newLevel) {
        PlayerStats stats = getPlayerStats(player.getUniqueId());
        synchronized (stats) {
            stats.incrementSuccesses();
            stats.updateHighestLevel(newLevel);
        }
        savePlayerStatsAsync(stats);
    }
    
    /**
     * 记录淬炼失败
     */
    public static void recordFailure(OfflinePlayer player) {
        PlayerStats stats = getPlayerStats(player.getUniqueId());
        synchronized (stats) {
            stats.incrementFailures();
        }
        savePlayerStatsAsync(stats);
    }
    
    /**
     * 更新当前套装效果等级
     */
    public static void updateSuitLevel(OfflinePlayer player, int suitLevel) {
        PlayerStats stats = getPlayerStats(player.getUniqueId());
        synchronized (stats) {
            stats.setCurrentSuitLevel(suitLevel);
        }
        savePlayerStatsAsync(stats);
    }
    
    /**
     * 获取玩家总尝试次数
     */
    public static int getTotalAttempts(UUID playerUUID) {
        return getPlayerStats(playerUUID).getTotalAttempts();
    }
    
    /**
     * 获取玩家最高淬炼等级
     */
    public static int getHighestLevel(UUID playerUUID) {
        return getPlayerStats(playerUUID).getHighestLevel();
    }
    
    /**
     * 获取玩家总成功次数
     */
    public static int getTotalSuccesses(UUID playerUUID) {
        return getPlayerStats(playerUUID).getTotalSuccesses();
    }
    
    /**
     * 获取玩家总失败次数
     */
    public static int getTotalFailures(UUID playerUUID) {
        return getPlayerStats(playerUUID).getTotalFailures();
    }
    
    /**
     * 获取玩家当前套装效果等级
     */
    public static int getCurrentSuitLevel(UUID playerUUID) {
        return getPlayerStats(playerUUID).getCurrentSuitLevel();
    }
    
    /**
     * 从缓存中移除玩家数据（玩家退出时调用）
     */
    public static void removeFromCache(UUID playerUUID) {
        statsCache.remove(playerUUID);
    }
    
    /**
     * 清空缓存（重载插件时调用）
     */
    public static void clearCache() {
        statsCache.clear();
    }
}
