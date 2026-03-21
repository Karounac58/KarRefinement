package vip.mcsj.www.karrefinement.api.placeholder;

import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * PAPI 变量注册中心
 * 外部插件通过此接口注册自定义 PlaceholderAPI 变量
 */
public interface PlaceholderRegistry {

    /**
     * 注册一个自定义变量提供者
     */
    void register(PlaceholderProvider provider);

    /**
     * 注销某个插件注册的所有变量
     */
    void unregisterAll(Plugin plugin);

    /**
     * 获取所有已注册的变量提供者
     */
    List<PlaceholderProvider> getProviders();
}
