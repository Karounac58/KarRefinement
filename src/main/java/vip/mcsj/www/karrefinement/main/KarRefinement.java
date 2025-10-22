package vip.mcsj.www.karrefinement.main;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.datamanager.database.DatabaseManager;
import vip.mcsj.www.karrefinement.datamanager.database.MySQLDatabaseManager;
import vip.mcsj.www.karrefinement.datamanager.database.SQLiteDatabaseManager;
import vip.mcsj.www.karrefinement.effect.ParticleResource;
import vip.mcsj.www.karrefinement.effect.ScriptRunnable;
import vip.mcsj.www.karrefinement.effect.SyncEffectRunnable;
import vip.mcsj.www.karrefinement.gui.*;
import vip.mcsj.www.karrefinement.main.listener.*;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.CustomParticle;
import vip.mcsj.www.karrefinement.version.CustomPath;
import vip.mcsj.www.karrefinement.version.CustomSounds;

import java.sql.SQLException;
import java.util.logging.Level;
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

    public static PotionDataManager pdm = new PotionDataManager();

    public static DarkChangeDataManager dcdm = new DarkChangeDataManager();

    public static Economy econ = null;

    public static DatabaseManager dm;

    public static boolean ap2Enable = false;
    public static boolean ap3Enable = false;

    public static boolean sxv3Enable = false;
    @Override
    public void onEnable(){
        instance = this;
        log.info(String.format("[%s] - 插件启动中...",getDescription().getName()));
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - 未找到Vault依赖！停止运行.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupAttributePlus();
        setupSXAttribute();

        Bukkit.getPluginManager().registerEvents(new KarEventListener(),this);
        Bukkit.getPluginManager().registerEvents(new DirectUpgradePaperEvent(),this);
        Bukkit.getPluginManager().registerEvents(new FurnaceListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarCompoundGUIListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarTakeItemGuiListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarTransformGuiListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarDetachListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarCompoundPieceListener(),this);
        Bukkit.getPluginManager().registerEvents(new AdhesiveListener(),this);
        Bukkit.getPluginManager().registerEvents(new KarPotionListener(),this);
        Bukkit.getPluginCommand("karrefinement").setExecutor(new KarCommandExecutor());
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
        FileUtil.initCustomFile(customPath.getPath()+"potion.yml","potion.yml");
        FileUtil.initCustomFile("message.yml","message.yml");
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
        PotionDataManager.init();
        Message.init();
        KarTakeItemGui.initItems();
        String storage = getConfig().getString("settings.data.storage");
        if(storage.equals("SQLite")) {
            dm = new SQLiteDatabaseManager(this);
        }else if(storage.equals("MySQL")){
            dm = new MySQLDatabaseManager(this);
        }
        dm.initialize();
        initThread();

        log.info(String.format("[%s] - 插件启动成功...",getDescription().getName()));

    }

    @Override
    public void onDisable() {
        try{
            if(dm.getDataSource() != null && dm.getDataSource().isClosed()){
                dm.close();
            }
        }catch (SQLException e){
            getLogger().log(Level.SEVERE,"关闭数据库连接失败!",e);
        }
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
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,new SyncEffectRunnable(),0,80);
//        new BukkitRunnable(){
//            @Override
//            public void run() {
//                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                    EffectDataManager.handlePlayerPotionEffect(onlinePlayer);
//                }
//            }
//        }.runTaskTimer(this,0,80);

        //删除过期淬炼增幅药水信息
        new BukkitRunnable(){
            @Override
            public void run() {
                pdm.deleteOudatedPlayerPotionInfo();
            }
        }.runTaskTimerAsynchronously(this,0,20);

        //删除过期暗改信息
        new BukkitRunnable(){
            @Override
            public void run() {
                dcdm.deleteOudatedPlayerDarkChangeData();
            }
        }.runTaskTimerAsynchronously(this,0,20);
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

    private void setupAttributePlus(){
        if (this.getServer().getPluginManager().getPlugin("AttributePlus") != null) {
            if (this.getServer().getPluginManager().getPlugin("AttributePlus").getDescription().getVersion().startsWith("2")) {
                this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到AttributePlus2插件，属性模块加载");
                ap2Enable = true;
            }
            if (this.getServer().getPluginManager().getPlugin("AttributePlus").getDescription().getVersion().startsWith("3")) {
                this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到AttributePlus3插件，属性模块加载");
                ap3Enable = true;
            }
        }
    }

    private void setupSXAttribute(){
        if (this.getServer().getPluginManager().getPlugin("SX-Attribute") != null) {
            if (this.getServer().getPluginManager().getPlugin("SX-Attribute").getDescription().getVersion().startsWith("3")) {
                this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到SX-AttributeV3.X插件，属性模块加载");
                sxv3Enable = true;
            }
        }
    }

    public void initGuiData(){
        KarRefinementGui.init();
        KarForgeGui.init();
        KarTransformStarGui.init();
        KarCompoundStoneGui.init();
        KarCompoundPieceGui.init();
    }
}
