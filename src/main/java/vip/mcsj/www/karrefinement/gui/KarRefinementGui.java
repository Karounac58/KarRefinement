package vip.mcsj.www.karrefinement.gui;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.main.KarRefinement;
import vip.mcsj.www.karrefinement.main.listener.KarEventListener;
import vip.mcsj.www.karrefinement.object.InvItem;
import vip.mcsj.www.karrefinement.object.MCVersions;
import vip.mcsj.www.karrefinement.object.Stone;
import vip.mcsj.www.karrefinement.service.refinement.RefinementResult;
import vip.mcsj.www.karrefinement.service.refinement.RefinementService;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KarRefinementGui {
    public static Map<String, InvItem> rItems = new HashMap<>();
    public static String title = "";

    public static Boolean enableFastRefine = true;


    public static void init(){
        if(!rItems.isEmpty()){
            rItems.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("gui/refinementgui.yml");
        title = file.getString("Title");
        ConfigurationSection fileCS = file.getConfigurationSection("Item");
        Set<String> keys = fileCS.getKeys(false);
        for (String key : keys) {
            String name = fileCS.getString(key + ".Name");
            Material material = Material.valueOf(fileCS.getString(key + ".Material"));
            int data = fileCS.getInt(key + ".Data");
            int customModelData = fileCS.getInt(key + ".CustomModelData");
            List<String> lore = fileCS.getStringList(key + ".Lore");
            rItems.put(key,new InvItem(name, material, data, customModelData, lore));
        }

        enableFastRefine = KarRefinement.instance.getConfig().getBoolean("settings.enablefastrefine");
    }
    public static void setInvInitial(Inventory inv,Player p) {
        List<Integer> indexs = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 45, 46, 47, 48, 50, 51, 52, 53);
        for (int i = 0; i < 54; i++) {
            if (i == 29 || i == 33) {
                continue;
            }
            if(i == 8){
                InvItem forgeButton = rItems.get("ForgeButton");
                ItemStack invItem = createInvItem(forgeButton,p);
                inv.setItem(8, invItem);
            }
            else if (i == 20) {
                InvItem stoneInfo = rItems.get("StoneInfo");
                ItemStack invItem = createInvItem(stoneInfo,p);
                inv.setItem(20, invItem);
            } else if (i == 24) {
                InvItem equipmentinfo = rItems.get("Equipmentinfo");
                ItemStack invItem = createInvItem(equipmentinfo,p);
                inv.setItem(24, invItem);
            } else if (i == 49) {
                InvItem confirmButton = rItems.get("ConfirmButton");
                ItemStack invItem = createInvItem(confirmButton,p);
                ItemMeta im = invItem.getItemMeta();
                List<String> lore = im.getLore();
                lore = lore.stream().map(s -> s.replace("{chance}",Message.messages.get("refinement_gui_chance_msg")))
                        .collect(Collectors.toList());
                im.setLore(lore);
                invItem.setItemMeta(im);
                inv.setItem(49, invItem);
            } else if (indexs.contains(i)) {
                InvItem barrier = rItems.get("Barrier");
                ItemStack invItem = createInvItem(barrier,p);
                inv.setItem(i,invItem);
            } else {
                InvItem barrier2 = rItems.get("Barrier2");
                ItemStack invItem = createInvItem(barrier2,p);
                inv.setItem(i, invItem);
            }
        }
    }

    public static ItemStack createInvItem(InvItem invItem,Player p){
        ItemStack furance = new ItemStack(invItem.getMaterial(),1,(short)invItem.getData());
        ItemMeta itemMeta = furance.getItemMeta();
        itemMeta.setDisplayName(PlaceholderAPI.setPlaceholders(p,invItem.getName()));
        itemMeta.setLore(PlaceholderAPI.setPlaceholders(p,invItem.getLore()));
        furance.setItemMeta(itemMeta);
        ReflectionUtils.setCustomModelData(furance, invItem.getCustomModelData());
        if(KarRefinement.pv.equals(MCVersions.v1122)){
            if(furance.getType() == Material.valueOf("STAINED_GLASS_PANE")){
                KarUtils.createColorPane(furance,invItem.getData());
            }
        }
        return furance;
    }

    /**
     * @param inv           //用于播放动画
     * @param itemStone     //用于让淬炼方法获取参数
     * @param itemEquipment //用于让淬炼方法获取参数
     * @param p
     */
    public static void playInvVideo(Inventory inv, ItemStack itemStone, ItemStack itemEquipment, Player p) {
        List<Integer> lists = new ArrayList<>();
        List<Integer> removeIndex = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            lists.add(i);
        }
        for (int i = 0; i < 9; i++) {
            lists.remove(Integer.valueOf(i));
            removeIndex.add(i);
        }
        for (int i = 45; i < 54; i++) {
            lists.remove(Integer.valueOf(i));
            removeIndex.add(i);
        }
        lists.remove(Integer.valueOf(29));
        lists.remove(Integer.valueOf(33));
        lists.remove(Integer.valueOf(49));
        lists.remove(Integer.valueOf(20));
        lists.remove(Integer.valueOf(24));
        int size = lists.size();
        removeIndex.add(29);
        removeIndex.add(33);
        removeIndex.add(49);
        removeIndex.add(20);
        removeIndex.add(24);
        InvItem videoItem = rItems.get("VideoItem");
        ItemStack invItem = createInvItem(videoItem,p);
        // 使用同步递归调度替代异步Thread.sleep，保证线程安全
        new BukkitRunnable() {
            int index = 0;
            final List<Integer> remainingSlots = new ArrayList<>(lists);
            @Override
            public void run() {
                // 标识正在淬炼中
                if(index == 0) {
                    KarEventListener.judgeInvRefinementOrNot.put(p, 1);
                }
                // 标识已关闭菜单,关闭即停止动画
                Integer closeState = KarEventListener.judgeInvCloseOrNot.get(p);
                if (closeState == null || closeState == 0) {
                    this.cancel();
                    return;
                }
                if(index >= size) {
//                    KarRefinementMethod(itemStone, itemEquipment, p, 0.0);
                    //如果有淬炼就消耗石头，没淬炼(淬炼前已满星)就不消耗石头
                    if(KarRefinementMethod(p, itemEquipment, StoneDataManager.getStone(itemStone))){
                        KarUtils.removeItemRefinement(itemStone);
                    }
                    setInvInitial(inv, p);
                    KarEventListener.judgeInvRefinementOrNot.put(p, 0);
                    this.cancel();
                    return;
                }
                // 随机选取一个未使用的槽位
                int randIdx = ThreadLocalRandom.current().nextInt(remainingSlots.size());
                int h = remainingSlots.remove(randIdx);
                inv.setItem(h, invItem);
                p.updateInventory();
                KarEventListener.invs.put(p, inv);
                p.playSound(p.getLocation(), KarRefinement.cs.getSounds().get(0), 1, 1);
                index++;
            }
        }.runTaskTimer(KarRefinement.instance, 0L, 2L);

    }

    /**
     * 判断要淬炼石头和装备是否合法
     *
     * @param itemStone
     * @param itemEquipment
     * @param p
     * @return
     */
    public static boolean judgeInventoryClickMethod(ItemStack itemStone, ItemStack itemEquipment, Player p) {
        if (itemStone == null || itemEquipment == null) {
            return false;
        }
        if (!StoneDataManager.isStoneLegal(itemStone)) {
            return false;
        }
        if (!EquipmentDataManager.isEquipmentLegal(itemEquipment)) {
            return false;
        }
        return true;
    }

    /**
     * 淬炼方法
     *
     * @param itemStone
     * @param itemEquipment
     * @param
     */
    public static void KarRefinementMethod(ItemStack itemStone, ItemStack itemEquipment, Player p,double addProb) {
        Random rand = new Random();

        //如果石头不合法，返回
        if (!StoneDataManager.isStoneLegal(itemStone)) {
            return;
        }
        //如果装备没在可淬炼装备列表里，返回
        if (!EquipmentDataManager.isEquipmentLegal(itemEquipment)) {
            return;
        }

        String equipmentIdentifier = EquipmentDataManager.getEquipmentIdentifier(itemEquipment);
        Stone stone = StoneDataManager.getStone(itemStone);
        EquipmentDataManager equipmentManager = new EquipmentDataManager(itemEquipment,p);
        PaperDataManager paperDataManager = new PaperDataManager(itemEquipment);

        //判断装备星级
        int refinementLevel = equipmentManager.carifyEquipmentLevel();
        //装备保护符等级
        int protectPaperLevel = paperDataManager.getPaperLevel();
        if(refinementLevel == LevelDataManager.levels.size()){
            p.sendMessage(Message.messages.get("refinement_maxlevel"));
            return;
        }
        
        // 记录淬炼尝试
        PlayerStatsDataManager.recordAttempt(p);
        
        double success = stone.getProbability().get(refinementLevel);
        BigDecimal decimal = BigDecimal.valueOf(KarUtils.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);

        List<Object> playerDarkChangeData = KarRefinement.dcdm.getPlayerDarkChangeData(p);

        if(playerDarkChangeData != null) {
            boolean isSuccess = (boolean)playerDarkChangeData.get(1);
            int count = (int)playerDarkChangeData.get(2);
            if (isSuccess) {
                if (equipmentManager.injuryUpStar()) {
                    // 记录淬炼成功
                    PlayerStatsDataManager.recordSuccess(p, equipmentManager.carifyEquipmentLevel());
                    
                    p.sendMessage(Message.messages.get("refinement_upstar"));
                    //Player p = (Player)e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    //减去强化石
                    KarUtils.removeItemRefinement(itemStone);
                    if ((equipmentManager.carifyEquipmentLevel()) >= EquipmentDataManager.broadcastLevel) {
//                    Bukkit.broadcastMessage("§f[§c淬炼告示§f] §a恭喜玩家 §e" + p.getName() + " §a用 "+stone.getName()+" §a将装备强化至 §6" + equipmentManager.carifyEquipmentLevel() + "星");
//                        Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(p,Message.messages.get("refinement_broadcast").replace("{player}", p.getName()).replace("{stone}", stone.getName()).replace("{level}", equipmentManager.carifyEquipmentLevel() + "")));
                        String displayName = "";
                        if(itemEquipment.getItemMeta().hasDisplayName()){
                            displayName = itemEquipment.getItemMeta().getDisplayName();
                        }else{
                            displayName = EquipmentDataManager.chinesenames.get(itemEquipment.getType().name().toUpperCase()) == null ? "" : EquipmentDataManager.chinesenames.get(itemEquipment.getType().name().toUpperCase());
                        }
                        Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(p, Message.messages.get("refinement_broadcast")
                                        .replace("{player}", p.getName())
                                        .replace("{stone}", stone.getName())
                                        .replace("{level}", equipmentManager.carifyEquipmentLevel() + "")
                                        .replace("{itemname}", displayName)));
                    }
                }
                KarRefinement.dcdm.updatePlayerDarkChangeData(p,isSuccess,count-1);
                return;
            } else {
                KarRefinement.dcdm.updatePlayerDarkChangeData(p,isSuccess,count-1);
                if (refinementLevel == 0) {
                    // 记录淬炼失败
                    PlayerStatsDataManager.recordFailure(p);
                    
                    p.sendMessage(Message.messages.get("refinement_failed").replace("{level}", 0 + ""));
                    KarUtils.removeItemRefinement(itemStone);
                    return;
                }
                int downLevel = equipmentManager.injuryDownStar(paperDataManager.getPaperLevel(), stone);
                
                // 记录淬炼失败
                PlayerStatsDataManager.recordFailure(p);
                
                p.sendMessage(Message.messages.get("refinement_failed").replace("{level}", downLevel + ""));

                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                //减去强化石
                KarUtils.removeItemRefinement(itemStone);
                return;
            }
        }
        List<Object> objects = KarRefinement.pdm.queryPlayerPotionInfoCache(p);
        double addSuccess = 0;
        if(objects != null){
            addSuccess = (double)objects.get(1) * 100;
        }
        //成功
        if(decimal.doubleValue() < (success + addSuccess + addProb)){
            if(equipmentManager.injuryUpStar()){
                // 记录淬炼成功
                PlayerStatsDataManager.recordSuccess(p, equipmentManager.carifyEquipmentLevel());
                
                p.sendMessage(Message.messages.get("refinement_upstar"));
                //Player p = (Player)e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                //减去强化石
                KarUtils.removeItemRefinement(itemStone);
                if ((equipmentManager.carifyEquipmentLevel()) >= EquipmentDataManager.broadcastLevel) {
//                    Bukkit.broadcastMessage("§f[§c淬炼告示§f] §a恭喜玩家 §e" + p.getName() + " §a用 "+stone.getName()+" §a将装备强化至 §6" + equipmentManager.carifyEquipmentLevel() + "星");
//                    Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(p,Message.messages.get("refinement_broadcast").replace("{player}",p.getName()).replace("{stone}",stone.getName()).replace("{level}",equipmentManager.carifyEquipmentLevel()+"")));
                    String displayName;
                    if(itemEquipment.getItemMeta().hasDisplayName()){
                        displayName = itemEquipment.getItemMeta().getDisplayName();
                    }else{
                        displayName = EquipmentDataManager.chinesenames.get(itemEquipment.getType().name().toUpperCase()) == null ? "" : EquipmentDataManager.chinesenames.get(itemEquipment.getType().name().toUpperCase());
                    }
                    Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(p, Message.messages.get("refinement_broadcast")
                                    .replace("{player}", p.getName())
                                    .replace("{stone}", stone.getName())
                                    .replace("{level}", equipmentManager.carifyEquipmentLevel() + "")
                                    .replace("{itemname}", displayName)));
                }
            }
            //失败
        }else{
            if(refinementLevel == 0){
                // 记录淬炼失败
                PlayerStatsDataManager.recordFailure(p);
                
                p.sendMessage(Message.messages.get("refinement_failed").replace("{level}",0+""));
                KarUtils.removeItemRefinement(itemStone);
                return;
            }
            int downLevel = equipmentManager.injuryDownStar(paperDataManager.getPaperLevel(), stone);
            
            // 记录淬炼失败
            PlayerStatsDataManager.recordFailure(p);
            
            p.sendMessage(Message.messages.get("refinement_failed").replace("{level}",downLevel+""));

            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            //减去强化石
            KarUtils.removeItemRefinement(itemStone);
        }
    }

    /**
     * 淬炼优化方法(在执行这个方法之前，需判断淬炼石头和装备是否合法，执行完这个方法后根据返回值判断是否消费淬炼石)
     * @param player
     * @param itemEquipment
     * @param stone
     * @return 是否有淬炼
     */
    public static Boolean KarRefinementMethod(Player player,ItemStack itemEquipment,Stone stone){
        RefinementResult result = new RefinementService().refine(player, itemEquipment, stone);
        if(result.isMaxLevel()){
            player.sendMessage(Message.messages.get("refinement_maxlevel"));
            return false;
        }
        //记录淬炼尝试
        PlayerStatsDataManager.recordAttempt(player);

        if(result.isSuccess()){
            // 记录淬炼成功
            PlayerStatsDataManager.recordSuccess(player, result.getNewLevel());

            player.sendMessage(Message.messages.get("refinement_upstar"));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            if(result.getNewLevel() >= EquipmentDataManager.broadcastLevel){
                String displayName;
                if(itemEquipment.getItemMeta().hasDisplayName()){
                    displayName = itemEquipment.getItemMeta().getDisplayName();
                }else{
                    displayName = EquipmentDataManager.chinesenames.get(itemEquipment.getType().name().toUpperCase()) == null ? "" : EquipmentDataManager.chinesenames.get(itemEquipment.getType().name().toUpperCase());
                }
                Bukkit.broadcastMessage(PlaceholderAPI.setPlaceholders(player, Message.messages.get("refinement_broadcast")
                        .replace("{player}", player.getName())
                        .replace("{stone}", stone.getName())
                        .replace("{level}", result.getNewLevel() + "")
                        .replace("{itemname}", displayName)));

            }
        }else{
            //记录淬炼失败
            PlayerStatsDataManager.recordFailure(player);

            player.sendMessage(Message.messages.get("refinement_failed").replace("{level}", result.getDownLevels() + ""));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
        }

        return true;
    }

}
