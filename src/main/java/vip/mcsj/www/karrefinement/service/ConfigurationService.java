package vip.mcsj.www.karrefinement.service;

import org.bukkit.configuration.file.YamlConfiguration;
import vip.mcsj.www.karrefinement.core.PluginContext;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一配置管理服务
 * 集中管理所有 YAML 配置文件的加载和重载
 */
public class ConfigurationService implements Service {

    private final PluginContext context;
    private final Map<String, YamlConfiguration> configCache = new HashMap<>();

    public ConfigurationService(PluginContext context) {
        this.context = context;
    }

    @Override
    public void initialize() {
        configCache.clear();
        String path = context.getCustomPath().getPath();

        // 初始化所有配置文件
        FileUtil.initCustomFile(path + "stone.yml", "stone.yml");
        FileUtil.initCustomFile(path + "spestone.yml", "spestone.yml");
        FileUtil.initCustomFile("refinement.yml","refinement.yml");
        FileUtil.initCustomFile(path + "directupgradepaper.yml", "directupgradepaper.yml");
        FileUtil.initCustomFile(path + "items.yml", "items.yml");
        FileUtil.initCustomFile(path + "protectpaper.yml", "protectpaper.yml");
        FileUtil.initCustomFile("forge.yml","forge.yml");
        FileUtil.initCustomFile(path + "infinitesoul.yml", "infinitesoul.yml");
        FileUtil.initCustomFile("transform.yml","transform.yml");
        FileUtil.initCustomFile(path + "detach.yml", "detach.yml");
        FileUtil.initCustomFile(path + "chinesename.yml", "chinesename.yml");
        FileUtil.initCustomFile(path + "adhesive.yml", "adhesive.yml");
        FileUtil.initCustomFile(path + "potion.yml", "potion.yml");
        FileUtil.initCustomFile("message.yml", "message.yml");
        FileUtil.initCustomFile("furnace.yml", "furnace.yml");
        FileUtil.initCustomFile(path + "sound.yml", "sound.yml");

        // GUI配置文件
        FileUtil.initCustomFile(path + "gui/compoundgui.yml", "gui/compoundgui.yml");
        FileUtil.initCustomFile(path + "gui/compoundpiecegui.yml", "gui/compoundpiecegui.yml");
        FileUtil.initCustomFile(path + "gui/forgegui.yml", "gui/forgegui.yml");
        FileUtil.initCustomFile(path + "gui/refinementgui.yml", "gui/refinementgui.yml");
        FileUtil.initCustomFile(path + "gui/transformgui.yml", "gui/transformgui.yml");
    }

    /**
     * 获取指定配置文件（带缓存）
     * @param fileName 文件名
     * @return YAML配置对象
     */
    public YamlConfiguration getConfig(String fileName) {
        YamlConfiguration cached = configCache.get(fileName);
        if (cached != null) {
            return cached;
        }
        YamlConfiguration config = FileUtil.getCustomFileYaml(fileName);
        configCache.put(fileName, config);
        return config;
    }

    /**
     * 清除配置缓存（重载时使用）
     */
    public void clearCache() {
        configCache.clear();
    }

    @Override
    public void reload() {
        clearCache();
        initialize();
    }
}
