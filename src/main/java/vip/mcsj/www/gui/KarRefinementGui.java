package vip.mcsj.www.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import vip.mcsj.www.datamanager.EquipmentDataManager;
import vip.mcsj.www.datamanager.LevelDataManager;
import vip.mcsj.www.datamanager.PaperDataManager;
import vip.mcsj.www.datamanager.StoneDataManager;
import vip.mcsj.www.main.events.KarEventListener;
import vip.mcsj.www.main.KarRefinement;
import vip.mcsj.www.object.Stone;
import vip.mcsj.www.utils.KarUtils;

import static vip.mcsj.www.main.events.KarEventListener.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class KarRefinementGui {
    public static void setInvInitial(Inventory inv) {
        List<Integer> indexs = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 45, 46, 47, 48, 50, 51, 52, 53);
        for (int i = 0; i < 54; i++) {
            if (i == 29 || i == 33) {
                continue;
            }
            if(i == 8){
                ItemStack anvil = new ItemStack(Material.ANVIL);
                ItemMeta itemMeta = anvil.getItemMeta();
                itemMeta.setDisplayName("§c锻造界面");
                anvil.setItemMeta(itemMeta);
                inv.setItem(8, anvil);
            }
            else if (i == 20) {
                ItemStack furance = new ItemStack(Material.FURNACE);
                ItemMeta itemMeta = furance.getItemMeta();
                itemMeta.setDisplayName("§c§l☼§d§l淬炼石§c§l☼");
                furance.setItemMeta(itemMeta);
                inv.setItem(20, furance);
            } else if (i == 24) {
                ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
                ItemMeta itemMeta = ironSword.getItemMeta();
                itemMeta.setDisplayName("§c§l☼§6§l淬炼装备§c§l☼");
                ironSword.setItemMeta(itemMeta);
                inv.setItem(24, ironSword);
            } else if (i == 49) {
                ItemStack glowstone = new ItemStack(Material.GLOWSTONE_DUST);
                ItemMeta itemMeta = glowstone.getItemMeta();
                itemMeta.setDisplayName("§6左键进行淬炼 §f| §b右键快速淬炼");
                List<String> lore = new ArrayList<>();
                lore.add("§e- §a左边放淬炼石，右边放待淬炼装备");
                lore.add("§e- §a进行淬炼时请§c不要退出界面或拿出物品，");
                lore.add("§e- §a否则有概率会造成淬炼石或装备丢失！");
                itemMeta.setLore(lore);
                glowstone.setItemMeta(itemMeta);
                inv.setItem(49, glowstone);
            } else if (indexs.contains(i)) {
                inv.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
            } else {
                inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
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
        new BukkitRunnable() {
            @Override
            public void run() {
                //标识正在淬炼中
                judgeInvRefinementOrNot.put(p, 1);
                for (int i = 0; i < size; i++) {
                    //标识已关闭菜单,关闭即停止动画
                    if (Objects.requireNonNullElse(judgeInvCloseOrNot.get(p), 0) == 0) {
                        return;
                    }
                    Random random = new Random();
                    //累加数，用于判断是否跳出循环
                    int x = 0;
                    //随机数，用于抽取list中内容
                    int h = random.nextInt(54);
                    //当原index列表不包含 且 移除index列表包含时
                    while (!lists.contains(Integer.valueOf(h)) || removeIndex.contains(Integer.valueOf(h))) {
                        h = random.nextInt(54);
                    }
                    inv.setItem(h, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    p.updateInventory();
                    lists.remove(Integer.valueOf(h));
                    KarEventListener.invs.put(p, inv);
                    try {
                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == size - 1) {
                        KarRefinementMethod(itemStone, itemEquipment, p);
                        setInvInitial(inv);
                        //标识已淬炼完毕
                        judgeInvRefinementOrNot.put(p, 0);
                    }
                }
            }
        }.runTaskAsynchronously(KarRefinement.instance);

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
    public static void KarRefinementMethod(ItemStack itemStone, ItemStack itemEquipment, Player p) {
        Random rand = new Random();

        //如果石头不合法，返回
        if (!StoneDataManager.isStoneLegal(itemStone)) {
            return;
        }
        //如果装备没在可淬炼装备列表里，返回
        if (!EquipmentDataManager.isEquipmentLegal(itemEquipment)) {
            return;
        }
        if(itemEquipment.getEnchantments().containsKey(Enchantment.THORNS)){
            p.sendMessage("§c带有荆棘附魔的装备无法淬炼！");
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
            p.sendMessage(ChatColor.RED+"此装备已经满级，无法继续淬炼");
            return;
        }
        double success = stone.getProbability().get(refinementLevel);
        BigDecimal decimal = BigDecimal.valueOf(rand.nextDouble(100)).setScale(2, RoundingMode.HALF_UP);
            //成功
        if((99-success) < decimal.doubleValue()){
            if(equipmentManager.injuryUpStar()){
                p.sendMessage(ChatColor.GREEN + "淬炼成功，装备上星！");
                //Player p = (Player)e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                //减去强化石
                KarUtils.removeItemRefinement(itemStone);
                if ((equipmentManager.carifyEquipmentLevel()) >= 6) {
                    Bukkit.broadcastMessage("§f[§c淬炼告示§f] §a恭喜玩家 §e" + p.getName() + " §a用 "+stone.getName()+" §a将装备强化至 §6" + equipmentManager.carifyEquipmentLevel() + "星");
                }
            }
            //失败
        }else{
            if(refinementLevel == 0){
                p.sendMessage(ChatColor.RED + "淬炼失败，装备掉0星");
                KarUtils.removeItemRefinement(itemStone);
                return;
            }
            int downLevel = equipmentManager.injuryDownStar(paperDataManager.getPaperLevel(), stone);
            p.sendMessage(ChatColor.RED + "淬炼失败，装备掉" + downLevel + "星");

            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            //减去强化石
            KarUtils.removeItemRefinement(itemStone);
        }
    }

}
