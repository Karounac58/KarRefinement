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
import vip.mcsj.www.karrefinement.gui.*;
import vip.mcsj.www.karrefinement.main.listener.*;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.CustomParticle;
import vip.mcsj.www.karrefinement.version.CustomPath;
import vip.mcsj.www.karrefinement.version.CustomSounds;

import java.util.logging.Logger;

public class KarRefinement extends JavaPlugin{
    private static final Logger log = Logger.getLogger("Minecraft");
    public static KarRefinement instance;
    //12星淬炼特效
    public static Particle[] particles = new Particle[3];

    public static CustomMaterial cm = new GuiResource().getMaterial();

    public static CustomSounds cs = new GuiResource().getSound();

    public static CustomParticle cp = new ParticleResource().get();

    public static CustomPath customPath = new GuiResource().getPath();

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
        FileUtil.initCustomFile(customPath.getPath()+"stone.yml","stone.yml");
        FileUtil.initCustomFile(customPath.getPath()+"spestone.yml","spestone.yml");
        FileUtil.initCustomFile("refinement.yml");
        FileUtil.initCustomFile(customPath.getPath()+"directupgradepaper.yml","directupgradepaper.yml");
        FileUtil.initCustomFile("items.yml");
        FileUtil.initCustomFile(customPath.getPath()+"protectpaper.yml","protectpaper.yml");
        FileUtil.initCustomFile("forge.yml");
        FileUtil.initCustomFile(customPath.getPath()+"infinitesoul.yml","infinitesoul.yml");
        FileUtil.initCustomFile("transform.yml");
        FileUtil.initCustomFile(customPath.getPath()+"detach.yml","detach.yml");
        FileUtil.initCustomFile(customPath.getPath()+"chinesename.yml","chinesename.yml");
        FileUtil.initCustomFile(customPath.getPath()+"adhesive.yml","adhesive.yml");
        //gui数据
        FileUtil.initCustomFile(customPath.getPath()+"gui/compoundgui.yml","gui/compoundgui.yml");
        FileUtil.initCustomFile(customPath.getPath()+"gui/compoundpiecegui.yml","gui/compoundpiecegui.yml");
        FileUtil.initCustomFile(customPath.getPath()+"gui/forgegui.yml","gui/forgegui.yml");
        FileUtil.initCustomFile(customPath.getPath()+"gui/refinementgui.yml","gui/refinementgui.yml");
        FileUtil.initCustomFile(customPath.getPath()+"gui/transformgui.yml","gui/transformgui.yml");
        log.info(" --------------------------------------------------------------------------");
        log.info("  _  __          _____       __ _                                 _  ");
        log.info(" | |/ /         |  __ \\     / _(_)                               | |  ");
        log.info(" | ' / __ _ _ __| |__) |___| |_ _ _ __   ___ _ __ ___   ___ _ __ | |_ ");
        log.info(" |  < / _` | '__|  _  // _ \\  _| | '_ \\ / _ \\ '_ ` _ \\ / _ \\ '_ \\| __|");
        log.info(" | . \\ (_| | |  | | \\ \\  __/ | | | | | |  __/ | | | | |  __/ | | | |_ ");
        log.info(" |_|\\_\\__,_|_|  |_|  \\_\\___|_| |_|_| |_|\\___|_| |_| |_|\\___|_| |_|\\__|");
        log.info("");
        log.info(" --------------------------------------------------------------------------");
        initGuiData();
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

    public void initGuiData(){
        KarRefinementGui.init();
        KarForgeGui.init();
        KarTransformStarGui.init();
        KarCompoundStoneGui.init();
        KarCompoundPieceGui.init();
    }
}
