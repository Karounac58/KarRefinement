package vip.mcsj.www.karrefinement.core;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Particle;
import vip.mcsj.www.karrefinement.datamanager.database.DatabaseManager;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.object.EquipmentMaterial;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.CustomParticle;
import vip.mcsj.www.karrefinement.version.CustomPath;
import vip.mcsj.www.karrefinement.version.CustomSounds;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件上下文
 * 持有插件运行时的全局状态和依赖，替代 KarRefinement 中的静态字段
 */
public class PluginContext {

    private final KarRefinement plugin;
    private final ServiceRegistry registry;

    // 版本兼容层
    private CustomMaterial customMaterial;
    private CustomParticle customParticle;
    private CustomSounds customSounds;
    private CustomPath customPath;
    private MCVersions version;

    // 粒子特效
    private Particle[] particles = new Particle[3];

    // 经济系统
    private Economy economy;

    // 数据库
    private DatabaseManager databaseManager;

    // 装备类型
    private final List<EquipmentMaterial> equipmentTypes = new ArrayList<>();

    // 属性插件标志
    private boolean ap2Enable = false;
    private boolean ap3Enable = false;
    private boolean sxv3Enable = false;
    private boolean caEnabled = false;

    public PluginContext(KarRefinement plugin) {
        this.plugin = plugin;
        this.registry = new ServiceRegistry(plugin.getLogger());
    }

    // ---- Getters / Setters ----

    public KarRefinement getPlugin() {
        return plugin;
    }

    public ServiceRegistry getRegistry() {
        return registry;
    }

    public CustomMaterial getCustomMaterial() {
        return customMaterial;
    }

    public void setCustomMaterial(CustomMaterial customMaterial) {
        this.customMaterial = customMaterial;
    }

    public CustomParticle getCustomParticle() {
        return customParticle;
    }

    public void setCustomParticle(CustomParticle customParticle) {
        this.customParticle = customParticle;
    }

    public CustomSounds getCustomSounds() {
        return customSounds;
    }

    public void setCustomSounds(CustomSounds customSounds) {
        this.customSounds = customSounds;
    }

    public CustomPath getCustomPath() {
        return customPath;
    }

    public void setCustomPath(CustomPath customPath) {
        this.customPath = customPath;
    }

    public MCVersions getVersion() {
        return version;
    }

    public void setVersion(MCVersions version) {
        this.version = version;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public void setParticles(Particle[] particles) {
        this.particles = particles;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<EquipmentMaterial> getEquipmentTypes() {
        return equipmentTypes;
    }

    public boolean isAp2Enable() {
        return ap2Enable;
    }

    public void setAp2Enable(boolean ap2Enable) {
        this.ap2Enable = ap2Enable;
    }

    public boolean isAp3Enable() {
        return ap3Enable;
    }

    public void setAp3Enable(boolean ap3Enable) {
        this.ap3Enable = ap3Enable;
    }

    public boolean isSxv3Enable() {
        return sxv3Enable;
    }

    public void setSxv3Enable(boolean sxv3Enable) {
        this.sxv3Enable = sxv3Enable;
    }

    public boolean isCaEnabled() {
        return caEnabled;
    }

    public void setCaEnabled(boolean caEnabled) {
        this.caEnabled = caEnabled;
    }
}
