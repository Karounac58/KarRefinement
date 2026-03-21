package vip.mcsj.www.karrefinement.api.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * 自定义 PAPI 变量提供者
 * 外部插件实现此接口来注册自定义 PlaceholderAPI 变量
 */
public interface PlaceholderProvider {

    /**
     * 注册此变量的插件
     */
    Plugin getPlugin();

    /**
     * 变量名前缀
     * 最终变量格式: %karrefinement_<prefix>%
     * 或: %karrefinement_<prefix>_<args>%
     *
     * 示例: prefix = "daily_count" → %karrefinement_daily_count%
     */
    String getPrefix();

    /**
     * 解析变量值
     *
     * @param player 请求变量的玩家
     * @param args   前缀之后的额外参数（可能为空字符串）
     *               例如 %karrefinement_daily_count_sword% 中 args = "sword"
     * @return 变量值，返回 null 表示不处理
     */
    String onRequest(OfflinePlayer player, String args);
}
