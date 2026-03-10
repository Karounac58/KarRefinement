package vip.mcsj.www.karrefinement.datamanager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.Level;

/**
 * KarRefinement PlaceholderAPI 扩展
 * 提供玩家淬炼统计数据的变量
 */
public class KarRefinementStats extends PlaceholderExpansion {
    
    private final KarRefinement plugin;
    
    public KarRefinementStats(KarRefinement plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "karrefinement";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "Karounac58";
    }
    
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true; // 持久化，防止重载时注销
    }
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        
        switch (params.toLowerCase()) {
            case "total_attempts":
                return String.valueOf(PlayerStatsDataManager.getTotalAttempts(player.getUniqueId()));
                
            case "highest_level":
                return String.valueOf(PlayerStatsDataManager.getHighestLevel(player.getUniqueId()));
                
            case "total_successes":
                return String.valueOf(PlayerStatsDataManager.getTotalSuccesses(player.getUniqueId()));
                
            case "total_failures":
                return String.valueOf(PlayerStatsDataManager.getTotalFailures(player.getUniqueId()));

            case "current_suit_level":
                // 使用LevelDataManager.getMinLevel()获取当前装备最低淬炼等级
                if (player.isOnline()) {
                    Player onlinePlayer = player.getPlayer();
                    if (onlinePlayer != null) {
                        Level minLevel = LevelDataManager.getMinLevel(onlinePlayer);
                        return minLevel != null ? String.valueOf(minLevel.getRefinementLevel()) : "0";
                    }
                }
                return "0";
                
            case "success_rate":
                // 计算成功率
                int successes = PlayerStatsDataManager.getTotalSuccesses(player.getUniqueId());
                int attempts = PlayerStatsDataManager.getTotalAttempts(player.getUniqueId());
                if (attempts == 0) {
                    return "0.00%";
                }
                double rate = (double) successes / attempts * 100;
                return String.format("%.2f", rate);
                
            default:
                return null;
        }
    }
}
