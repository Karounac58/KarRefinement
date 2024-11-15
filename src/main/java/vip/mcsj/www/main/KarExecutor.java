package vip.mcsj.www.main;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vip.mcsj.www.datamanager.*;
import vip.mcsj.www.gui.KarForgeGui;
import vip.mcsj.www.gui.KarForgeInvHolder;
import vip.mcsj.www.gui.KarRefinementGui;
import vip.mcsj.www.gui.KarRefinementInvHolder;
import vip.mcsj.www.utils.KarUtils;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KarExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args[0].equals("givestone")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                return false;
            }
            StoneDataManager manager = new StoneDataManager(args[2]);
            ItemStack stone = manager.createStone();
            if (stone == null) {
                p.sendMessage(ChatColor.RED + "没有这个淬炼石");
                return true;
            }
            int i = Integer.parseInt(args[3]);
            for (int j = 0; j < i; j++) {
                p.getInventory().addItem(manager.createStone());
            }
            return true;
        }
        if (args[0].equals("givepaper")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                return false;
            }
            PaperDataManager paperDataManager = new PaperDataManager(args[2]);
            p.getInventory().addItem(paperDataManager.createProtectedPaper());
            return true;
        }
        if(args[0].equals("givedupaper")){
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null){
                return false;
            }
            p.getInventory().addItem(DUPaperDataManager.createDUPaper(args[2]));
            return true;
        }
        if (args[0].equals("givespestone")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                return false;
            }
            SpecialStoneDataManager speStoneManager = new SpecialStoneDataManager(args[2]);
            p.getInventory().addItem(speStoneManager.createSpeStone());
            return true;
        }
        if(args[0].equals("givesoul")){
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                return false;
            }
            InfiniteSoulManager soulManager = new InfiniteSoulManager(args[2]);
            p.getInventory().addItem(soulManager.createInfiniteSoul());
            return true;
        }
        if (args[0].equals("adminup")) {
            Player p = (Player) sender;
            ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
            if (EquipmentDataManager.isEquipmentLegal(itemInMainHand)) {
                EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand,p);
                manager.injuryUpStar();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                return true;
            }
        }
        if(args[0].equals("reload")){
            Player p = (Player) sender;
            reloadConfig();
            p.sendMessage("§a§l配置文件重载成功");
        }
//        if(args[0].equals("admindown")){
//            Player p = (Player)sender;
//            ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
//            if(EquipmentDataManager.canRefinementDamagedEquipment.contains(itemInMainHand.getType().name())){
//                EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand);
//                manager.injuryDownStar(Integer.parseInt(args[1]));
//                p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_NETHERITE,1,1);
//                return true;
//            }else if(EquipmentDataManager.canRefinementProtectedEquipment.contains(itemInMainHand.getType().name())){
//                EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand);
//                manager.protectDownStar(Integer.parseInt(args[1]));
//                p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_NETHERITE,1,1);
//                return true;
//            }
//        }
        if (args[0].equals("getnbt")) {
            Player p = (Player) sender;
            ItemStack invItem = p.getInventory().getItemInMainHand();
//            int a = NBT.get(invItem, nbt -> nbt.getInteger(args[1]));
            int a = new NBTItem(invItem).getInteger(args[1]);
            p.sendMessage(String.valueOf(a));
            return true;
        }

        if (args[0].equals("clearlore")) {
            Player p = (Player) sender;
            ItemStack invItem = p.getInventory().getItemInMainHand();
            ItemMeta itemMeta = invItem.getItemMeta();
            List<String> lores = itemMeta.getLore();
            lores.clear();
            itemMeta.setLore(lores);
            invItem.setItemMeta(itemMeta);
            return true;
        }
        if (args[0].equals("setnbt")) {
            Player p = (Player) sender;
            ItemStack invItem = p.getInventory().getItemInMainHand();
            NBT.modify(invItem, nbt -> {
                nbt.setInteger(args[1], Integer.parseInt(args[2]));
            });
            return true;
        }

        if (args[0].equals("opengui")) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                return false;
            }
            Inventory inv = Bukkit.createInventory(new KarRefinementInvHolder(), 54, "§c淬炼界面");
            KarRefinementGui.setInvInitial(inv);
            p.openInventory(inv);
            return true;
        }

        if(args[0].equals("openforgegui")){
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                return false;
            }
            Inventory inv = Bukkit.createInventory(new KarForgeInvHolder(),45,"§c§l锻造界面");
            KarForgeGui.initInv(inv);
            p.openInventory(inv);
        }


//        if (args[0].equals("test")) {
//            Player p = (Player) sender;
//            //测试次数
//            int num = Integer.parseInt(args[1]);
//            int start = Integer.parseInt(args[2]);
//            int end = Integer.parseInt(args[3]);
//            double stone1 = StoneDataManager.stoneSuccessList.get((args[4]));
//            double stone2 = StoneDataManager.stoneSuccessList.get((args[5]));
//            double stone3 = StoneDataManager.stoneSuccessList.get((args[6]));
//            KarUtils.testRefinement(num, start, end, stone1, stone2, stone3, p);
//
//        }
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
    public void reloadConfig(){
        StoneDataManager.init();
        EquipmentDataManager.init();
        EquipmentDataManager.initForgeData();
        LevelDataManager.init();
        PaperDataManager.init();
        SpecialStoneDataManager.init();
        InfiniteSoulManager.init();
        DUPaperDataManager.init();
    }
}
