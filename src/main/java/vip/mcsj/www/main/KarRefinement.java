package vip.mcsj.www.main;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.datamanager.*;
import vip.mcsj.www.main.listener.DirectUpgradePaperEvent;
import vip.mcsj.www.main.listener.FurnaceListener;
import vip.mcsj.www.main.listener.KarEventListener;
import vip.mcsj.www.main.listener.PreventOffHand;
import vip.mcsj.www.utils.FileUtil;
import vip.mcsj.www.utils.KarUtils;
import vip.mcsj.www.utils.ReflectionUtils;

import java.util.Arrays;
import java.util.logging.Logger;

public class KarRefinement extends JavaPlugin{
    private static final Logger log = Logger.getLogger("Minecraft");
    public static JavaPlugin instance;

    //12星淬炼特效
    public static Particle[] particles = new Particle[3];
    @Override
    public void onEnable(){
        instance = this;
        log.info(String.format("[%s] - 插件启动中...",getDescription().getName()));
        Bukkit.getPluginManager().registerEvents(new KarEventListener(),this);
        Bukkit.getPluginManager().registerEvents(new PreventOffHand(),this);
        Bukkit.getPluginManager().registerEvents(new DirectUpgradePaperEvent(),this);
        Bukkit.getPluginManager().registerEvents(new FurnaceListener(),this);
        Bukkit.getPluginCommand("karrefinement").setExecutor(new KarExecutor());
        FileUtil.initCustomFile("stone.yml");
        FileUtil.initCustomFile("spestone.yml");
        FileUtil.initCustomFile("refinement.yml");
        FileUtil.initCustomFile("directupgradepaper.yml");
        FileUtil.initCustomFile("items.yml");
        FileUtil.initCustomFile("protectpaper.yml");
        FileUtil.initCustomFile("forge.yml");
        FileUtil.initCustomFile("infinitesoul.yml");

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
        LevelDataManager.init();
        PaperDataManager.init();
        SpecialStoneDataManager.init();
        InfiniteSoulManager.init();
        DUPaperDataManager.init();
        initThread();


        getParticle();

        log.info(String.format("[%s] - 插件启动成功...",getDescription().getName()));

    }

    public void initThread(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    EffectDataManager.handlePlayerEffect(onlinePlayer);
                }
            }
        }.runTaskTimer(this,0,80);
    }

    public void getParticle(){
        int[] spigotVersion = ReflectionUtils.getSpigotVersion();
        particles[0] = KarUtils.autoTwelveParticle(spigotVersion);
        particles[1] = KarUtils.autoFifteenParticle(spigotVersion);
        particles[2] = KarUtils.autoEighteenParticle(spigotVersion);
    }
}
