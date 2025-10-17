package vip.mcsj.www.karrefinement.main;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.effect.ParticleResource;
import vip.mcsj.www.karrefinement.effect.ScriptRunnable;
import vip.mcsj.www.karrefinement.gui.GuiResource;
import vip.mcsj.www.karrefinement.gui.KarCompoundStoneGui;
import vip.mcsj.www.karrefinement.gui.KarTakeItemGui;
import vip.mcsj.www.karrefinement.main.listener.*;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.CustomParticle;
import vip.mcsj.www.karrefinement.version.CustomSounds;

import java.util.logging.Logger;

public class KarRefinement extends JavaPlugin{
    private static final Logger log = Logger.getLogger("Minecraft");
    public static JavaPlugin instance;
    //12星淬炼特效
    public static Particle[] particles = new Particle[3];

    public static CustomMaterial cm = new GuiResource().getMaterial();

    public static CustomSounds cs = new GuiResource().getSound();

    public static CustomParticle cp = new ParticleResource().get();

    public static MCVersions pv = ReflectionUtils.judgeVersion();

    public static Economy econ = null;
    @Override
    public void onEnable(){
        instance = this;
        log.info(String.format("[%s] - 插件启动中...",getDescription().getName()));
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - 未找到Vault依赖！停止运行.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new KarEventListener(),this);
        Bukkit.getPluginManager().registerEvents(new DirectUpgradePaperEvent(),this);
        Bukkit.getPluginManager().registerEvents(new FurnaceListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarCompoundGUIListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarTakeItemGuiListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarTransformGuiListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarDetachListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarCompoundPieceListener(),this);
        Bukkit.getPluginManager().registerEvents(new AdhesiveListener(),this);
        Bukkit.getPluginCommand("karrefinement").setExecutor(new KarExecutor());
        saveDefaultConfig();
        FileUtil.initCustomFile("stone.yml");
        FileUtil.initCustomFile("spestone.yml");
        FileUtil.initCustomFile("refinement.yml");
        FileUtil.initCustomFile("directupgradepaper.yml");
        FileUtil.initCustomFile("items.yml");
        FileUtil.initCustomFile("protectpaper.yml");
        FileUtil.initCustomFile("forge.yml");
        FileUtil.initCustomFile("infinitesoul.yml");
        FileUtil.initCustomFile("transform.yml");
        FileUtil.initCustomFile("detach.yml");
        FileUtil.initCustomFile("chinesename.yml");
        FileUtil.initCustomFile("adhesive.yml");
        log.info(" --------------------------------------------------------------------------");
        log.info("  _  __          _____       __ _                                 _  ");
        log.info(" | |/ /         |  __ \\     / _(_)                               | |  ");
        log.info(" | ' / __ _ _ __| |__) |___| |_ _ _ __   ___ _ __ ___   ___ _ __ | |_ ");
        log.info(" |  < / _` | '__|  _  // _ \\  _| | '_ \\ / _ \\ '_ ` _ \\ / _ \\ '_ \\| __|");
        log.info(" | . \\ (_| | |  | | \\ \\  __/ | | | | | |  __/ | | | | |  __/ | | | |_ ");
        log.info(" |_|\\_\\__,_|_|  |_|  \\_\\___|_| |_|_| |_|\\___|_| |_| |_|\\___|_| |_|\\__|");
        log.info("");
        log.info(" --------------------------------------------------------------------------");
        StoneDataManager.init();
        EquipmentDataManager.init();
        EquipmentDataManager.initForgeData();
        EquipmentDataManager.initTransformData();
        LevelDataManager.init();
        PaperDataManager.init();
        SpecialStoneDataManager.init();
        InfiniteSoulManager.init();
        DUPaperDataManager.init();
        KarCompoundStoneGui.initCompoundData();
        DetachDataManager.init();
        AdhesiveDataManager.init();
        KarTakeItemGui.initItems();
        initThread();

        log.info(String.format("[%s] - 插件启动成功...",getDescription().getName()));

    }

    public void initThread(){
//        new BukkitRunnable(){
//            @Override
//            public void run() {
//                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                    EffectDataManager.handlePlayerEffect(onlinePlayer);
//                }
//            }
//        }.runTaskTimer(this,0,80);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new ScriptRunnable(),0,2);
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    EffectDataManager.handlePlayerPotionEffect(onlinePlayer);
                }
            }
        }.runTaskTimer(this,0,80);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
