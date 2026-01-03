package vip.mcsj.www.karrefinement.main;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
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
import vip.mcsj.www.karrefinement.object.EquipmentMaterial;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;
import vip.mcsj.www.karrefinement.version.CustomMaterial;
import vip.mcsj.www.karrefinement.version.CustomParticle;
import vip.mcsj.www.karrefinement.version.CustomPath;
import vip.mcsj.www.karrefinement.version.CustomSounds;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KarRefinement extends JavaPlugin {
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

//    public static List<ItemType> types = new ArrayList<>();

    public static final List<EquipmentMaterial> types = new ArrayList<>();

    public static boolean ap2Enable = false;
    public static boolean ap3Enable = false;

    public static boolean sxv3Enable = false;

    public static boolean caEnabled = false;
    @Override
    public void onEnable() {
        instance = this;


        log.info(String.format("[%s] - 插件启动中...",getDescription().getName()));
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - 未找到Vault依赖！停止运行.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupAttributePlus();
        setupSXAttribute();
        setupCraneAttribute();

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
        Bukkit.getPluginManager().registerEvents(new JoinMessageListener(),this);
        Bukkit.getPluginCommand("karrefinement").setExecutor(new KarCommandExecutor());
        saveDefaultConfig();
        ScriptRunnable.enbaleScript = KarRefinement.instance.getConfig().getBoolean("settings.enablescript");
        FileUtil.initCustomFile(customPath.getPath()+"stone.yml","stone.yml");
        FileUtil.initCustomFile(customPath.getPath()+"spestone.yml","spestone.yml");
        FileUtil.initCustomFile("refinement.yml");
        FileUtil.initCustomFile(customPath.getPath()+"directupgradepaper.yml","directupgradepaper.yml");
        FileUtil.initCustomFile(customPath.getPath()+"items.yml","items.yml");
        FileUtil.initCustomFile(customPath.getPath()+"protectpaper.yml","protectpaper.yml");
        FileUtil.initCustomFile("forge.yml");
        FileUtil.initCustomFile(customPath.getPath()+"infinitesoul.yml","infinitesoul.yml");
        FileUtil.initCustomFile("transform.yml");
        FileUtil.initCustomFile(customPath.getPath()+"detach.yml","detach.yml");
        FileUtil.initCustomFile(customPath.getPath()+"chinesename.yml","chinesename.yml");
        FileUtil.initCustomFile(customPath.getPath()+"adhesive.yml","adhesive.yml");
        FileUtil.initCustomFile(customPath.getPath()+"potion.yml","potion.yml");
        FileUtil.initCustomFile("message.yml","message.yml");
        FileUtil.initCustomFile("furnace.yml","furnace.yml");
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
        FurnaceDataManager.init();
        setRecipe();
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

    private void setupCraneAttribute(){
        if(this.getServer().getPluginManager().getPlugin("CraneAttribute") != null) {
            this.getServer().getConsoleSender().sendMessage("§7[§e" + this.getName() + "§7]§a检测到CraneAttribute插件，属性模块加载");
            caEnabled = true;
        }
    }

    public void initGuiData(){
        KarRefinementGui.init();
        KarForgeGui.init();
        KarTransformStarGui.init();
        KarCompoundStoneGui.init();
        KarCompoundPieceGui.init();
    }

    public void setRecipe() {
        int index = 0; // 添加索引确保唯一
        for (EquipmentMaterial type : types) {
            ItemStack result = type.toItemStack();

            FurnaceRecipe recipe = createFurnaceRecipe(type, index);
            if (recipe == null) continue;

            if (isLegacyVersion()) {
                for (int durability = 0; durability <= type.getMaterial().getMaxDurability(); durability++) {
                    recipe.setInput(type.getMaterial(), durability);
                    try {
                        instance.getServer().addRecipe(recipe);
                    } catch (IllegalStateException ex) {
                    }
                }
            } else {
                try {
                    instance.getServer().addRecipe(recipe);
                } catch (IllegalStateException ex) {
                    // 忽略重复配方警告
                }
            }
            index++;
        }
    }

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    private FurnaceRecipe createFurnaceRecipe(EquipmentMaterial type, int index) {
        ItemStack result = type.toItemStack();

        if (isLegacyVersion()) {
            return new FurnaceRecipe(result, type.getMaterial());
        } else {
            try {
                Class<?> namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");

                Constructor<?> keyConstructor = namespacedKeyClass.getConstructor(
                        org.bukkit.plugin.Plugin.class,
                        String.class
                );

                // 使用 材料名 + typeInBag + 索引 确保唯一
                String uniqueId = "smelt_" + type.getMaterial().name().toLowerCase() + "_" + index;
                Object key = keyConstructor.newInstance(instance, uniqueId);

                Constructor<FurnaceRecipe> recipeConstructor = FurnaceRecipe.class.getConstructor(
                        namespacedKeyClass,
                        ItemStack.class,
                        Material.class,
                        float.class,
                        int.class
                );

                return recipeConstructor.newInstance(key, result, type.getMaterial(), 0.1f, 200);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static Boolean legacy = null;

    private boolean isLegacyVersion() {
        if (legacy == null) {
            try {
                // NamespacedKey 在 1.12 存在但 FurnaceRecipe 的新构造函数在 1.13+ 才有
                FurnaceRecipe.class.getConstructor(
                        NamespacedKey.class,
                        ItemStack.class,
                        Material.class,
                        float.class,
                        int.class
                );
                legacy = false;
            } catch (NoSuchMethodException e) {
                legacy = true;
            }
        }
        return legacy;
    }

    public static class ItemType {

        public String typeInBag;
        public String baseType;
        public Material type;
        private byte data = 0;

        public ItemType(String typeInBag, String baseType) {
            this.typeInBag = typeInBag;
            this.baseType = baseType;

            if (baseType.contains(":")) {
                String[] args = baseType.split(":");
                String strType = args[0];
                String strData = args[1];
                type = Material.matchMaterial(strType);
                data = (byte) Integer.parseInt(strData);
            } else {
                type = Material.matchMaterial(baseType);
            }
        }

        @SuppressWarnings("deprecation")
        public ItemStack toItemStack() {
            ItemStack item = new ItemStack(type);
            if (data != 0) {
                item.setDurability(data);
            }
            return item;
        }
    }
}