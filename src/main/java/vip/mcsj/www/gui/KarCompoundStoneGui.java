package vip.mcsj.www.gui;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.plugin.NBTAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.datamanager.StoneDataManager;
import vip.mcsj.www.main.KarRefinement;
import vip.mcsj.www.main.listener.KarEventListener;
import vip.mcsj.www.object.Compound;
import vip.mcsj.www.object.KarHandler;
import vip.mcsj.www.object.Stone;
import vip.mcsj.www.utils.FileUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static vip.mcsj.www.main.KarRefinement.cm;
import static vip.mcsj.www.main.KarRefinement.cs;
import static vip.mcsj.www.main.listener.KarEventListener.judgeForgeInvCloseOrNot;
import static vip.mcsj.www.main.listener.KarEventListener.judgeInvForgeOrNot;

public class KarCompoundStoneGui implements Listener {
    private Inventory inv;
    private Player p;
    public static Map<UUID,Integer> compoundOrNot = new ConcurrentHashMap<>();

    public static Map<String, Compound> compoundMap = new HashMap<>();

    public static List<Compound> compounds = new ArrayList<>();

    public static void initCompoundData(){
        FileUtil.FileInitialize("compound.yml");
        if(!compoundMap.isEmpty()){
            compoundMap.clear();
        }
        YamlConfiguration compoundYml = FileUtil.getCustomFileYaml("compound.yml");
        Set<String> keys = compoundYml.getKeys(false);
        for (String key : keys) {
            int id = compoundYml.getInt(key+".id");
            String higherStoneKey = compoundYml.getString(key+".compound");
            Double chance = compoundYml.getDouble(key+".chance");
            Compound compound = new Compound(id,key,higherStoneKey,chance);
            compoundMap.put(key,compound);
        }
        compounds = compoundMap.values().stream().sorted(Comparator.comparingInt(Compound::getId)).collect(Collectors.toList());
    }

    public static void initial(Inventory inv) {
        List<Integer> redIndexs = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
        List<Integer> greenIndexs = Arrays.asList(37,38,39,40,41,42,43);
        for (int i = 0; i < 54; i++) {
            if (i == 16 || i == 34 || i == 19) {
                continue;
            }
            if (redIndexs.contains(i)) {
                inv.setItem(i, cm.getItems().get(0));
            } else if(greenIndexs.contains(i)){
                inv.setItem(i, cm.getItems().get(1));
            }else{
                inv.setItem(i,cm.getItems().get(3));
            }
        }
    }

    public static void startCompound(Inventory inv,Player p,Boolean b){
        ItemStack first = inv.getItem(16);
        ItemStack second = inv.getItem(34);
        if(inv.getItem(16) == null || inv.getItem(34) == null){
            p.closeInventory();
            p.sendMessage(ChatColor.RED+"请放入同阶淬炼石");
            return;
        }
        NBTItem firstNBT = new NBTItem(first);
        NBTItem secondNBT = new NBTItem(second);
        if(!firstNBT.getString("refinementstone").equals(secondNBT.getString("refinementstone"))){
            p.closeInventory();
            p.sendMessage(ChatColor.RED+"请放入同阶淬炼石");
            return;
        }
        //开始合成
        compoundOrNot.put(p.getUniqueId(),1);
        //1.播放动画
        if(b) {
            playInvVideo(inv,p);
        }
        if(b) {
            //2.合成
            new BukkitRunnable() {
                @Override
                public void run() {
                    String stoneKey = compound(inv,p,firstNBT.getString("refinementstone"));
                    if (stoneKey != "Shit" && stoneKey != null) {
                        String stoneName = StoneDataManager.stones.get(stoneKey).getName();
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        //3.播报消息
                        p.sendMessage("§e§l你成功合成了" + stoneName);
                    }else if(stoneKey == null){
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        p.sendMessage("§c§l无法合成更高级的宝石！");
                    }else{
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                        p.sendMessage("§c&l合成失败，宝石破碎！");
                    }
                    //4.初始化
                    initial(inv);
                    compoundOrNot.put(p.getUniqueId(),0);
                }
            }.runTaskLater(KarRefinement.instance, 60);
        }else{
            String stoneKey = compound(inv,p,firstNBT.getString("refinementstone"));
            if (stoneKey != "Shit" && stoneKey != null) {
                String stoneName = StoneDataManager.stones.get(stoneKey).getName();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                //3.播报消息
                p.sendMessage("§e§l你成功合成了" + stoneName);
            }else if(stoneKey == null){
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                p.sendMessage("§c§l无法合成更高级的宝石！");
            }else{
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                p.sendMessage("§c&l合成失败，宝石破碎！");
            }
            //4.初始化
            initial(inv);
            compoundOrNot.put(p.getUniqueId(),0);
        }

    }

    public static void playInvVideo(Inventory inv,Player p) {
        List<Integer> indexs = Arrays.asList(25,24,23,22,21);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < indexs.size(); i++) {
                    inv.setItem(indexs.get(i), cm.getItems().get(1));
                    p.playSound(p.getLocation(), cs.getSounds().get(0), 1, 1);
                    p.updateInventory();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(KarRefinement.instance);

    }

    public static String compound(Inventory inv,Player p,String key){
        if(compoundMap.get(key) == null){
            return null;
        }
        Double chance = compoundMap.get(key).getChance();
        Random rand = new Random();
        if(rand.nextDouble()*100 < chance){
            Compound com = new Compound();
            for (Compound compound : compounds) {
                if(compound.getStoneKey().equals(key)){
                    com = compound;
                }
            }
            if(com.getHigherStoneKey() != null) {
                StoneDataManager sdm = new StoneDataManager(com.getHigherStoneKey());
                p.playSound(p.getLocation(), cs.getSounds().get(0), 1, 1);
                inv.setItem(16,subtractItemAmount(inv.getItem(16)));
                inv.setItem(34,subtractItemAmount(inv.getItem(34)));
                inv.setItem(19,sdm.createStone());
                return com.getHigherStoneKey();
            }
        }else{
            inv.setItem(16,subtractItemAmount(inv.getItem(16)));
            inv.setItem(34,subtractItemAmount(inv.getItem(34)));
        }
        return "Shit";
    }

    public static void openGuiForPlayer(Inventory inv,Player p){
        initial(inv);
        p.openInventory(inv);
    }

    private static ItemStack subtractItemAmount(ItemStack item){
        item.setAmount(item.getAmount()-1);
        return item;
    }
}
