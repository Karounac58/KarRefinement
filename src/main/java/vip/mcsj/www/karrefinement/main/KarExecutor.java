package vip.mcsj.www.karrefinement.main;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
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
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.gui.*;
import vip.mcsj.www.karrefinement.main.listener.KarTakeItemGuiListener;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class KarExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            return true;
        }
        //只有一个子命令
        if(args.length == 1){
            Player p = (Player) sender;
            switch (args[0]){
                case "adminup":
                    ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                    if (EquipmentDataManager.isEquipmentLegal(itemInMainHand)) {
                        EquipmentDataManager manager = new EquipmentDataManager(itemInMainHand,p);
                        manager.injuryUpStar();
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        return true;
                    }
                    break;
                case "reload":
                    reloadConfig();
                    KarTakeItemGui.initItems();
                    p.sendMessage("§a§l配置文件重载成功");
                    break;
                case "getnbt":
                    ItemStack invItem = p.getInventory().getItemInMainHand();
                    int a = new NBTItem(invItem).getInteger(args[1]);
                    p.sendMessage(String.valueOf(a));
                    break;
                case "clearlore":
                    ItemStack invItem1 = p.getInventory().getItemInMainHand();
                    ItemMeta itemMeta = invItem1.getItemMeta();
                    List<String> lores = itemMeta.getLore();
                    lores.clear();
                    itemMeta.setLore(lores);
                    invItem1.setItemMeta(itemMeta);
                    return true;
                case "openitemgui":
                    KarTakeItemGui.openKarTakeItemGui(p);
                    p.sendMessage("§a你打开了淬炼物品菜单");
                    return true;

            }
        }
        //有多个子命令
        if(args.length >= 2){
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                sender.sendMessage("§c找不到这个玩家");
                return true;
            }
            switch(args[0].toLowerCase()){
                case "givestone":
                    StoneDataManager stoneManager = new StoneDataManager(args[2]);
                    ItemStack stone = stoneManager.createStone();
                    if (stone == null) {
                        p.sendMessage(ChatColor.RED + "没有这个淬炼石");
                        return true;
                    }
                    int i = Integer.parseInt(args[3]);
                    for (int j = 0; j < i; j++) {
                        p.getInventory().addItem(stoneManager.createStone());
                    }
                    break;
                case "givepaper":
                    PaperDataManager paperDataManager = new PaperDataManager(args[2]);
                    p.getInventory().addItem(paperDataManager.createProtectedPaper());
                    break;
                case "givedupaper":
                    p.getInventory().addItem(DUPaperDataManager.createDUPaper(args[2]));
                    break;
                case "givespestone":
                    SpecialStoneDataManager speStoneManager = new SpecialStoneDataManager(args[2]);
                    p.getInventory().addItem(speStoneManager.createSpeStone());
                    break;
                case "givesoul":
                    InfiniteSoulManager soulManager = new InfiniteSoulManager(args[2]);
                    p.getInventory().addItem(soulManager.createInfiniteSoul());
                    break;
                case "setnbt":
                    ItemStack invItem = p.getInventory().getItemInMainHand();
                    NBT.modify(invItem, nbt -> {
                        nbt.setInteger(args[1], Integer.parseInt(args[2]));
                    });
                    break;
                case "opengui":
                    Inventory inv = Bukkit.createInventory(new KarRefinementInvHolder(), 54, "§c淬炼界面");
                    KarRefinementGui.setInvInitial(inv);
                    p.openInventory(inv);
                    break;
                case "openforgegui":
                    Inventory inv1 = Bukkit.createInventory(new KarForgeInvHolder(),45,"§c§l锻造界面");
                    KarForgeGui.initInv(inv1);
                    p.openInventory(inv1);
                    break;
                case "opencompoundgui":
                    Inventory inv2 = Bukkit.createInventory(new KarCompoundStoneInvHolder(),54,"§c§l宝石合成界面");
                    KarCompoundStoneGui.initial(inv2);
                    KarCompoundStoneGui.openGuiForPlayer(inv2,p);
                    break;
                case "opentransformgui":
                    Inventory inv3 = Bukkit.createInventory(new KarTransformStarGui.KarTransformStarGuiInvHolder(), 27, "§c§l移星界面");
                    KarTransformStarGui.init(inv3);
                    p.openInventory(inv3);
                    break;
            }
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
        List<String> completions = new ArrayList<>();
        if(strings.length == 1){
            completions.add("help");
            completions.add("givestone");
            completions.add("givepaper");
            completions.add("givedupaper");
            completions.add("givespestone");
            completions.add("givesoul");
            completions.add("setnbt");
            completions.add("opengui");
            completions.add("openforgegui");
            completions.add("opentransformgui");
            completions.add("adminup");
            completions.add("reload");
            completions.add("getnbt");
            completions.add("clearlore");
            completions.add("openitemgui");
        }else if(strings.length == 2){
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }else if(strings.length == 3){
            switch (strings[0].toLowerCase()){
                case "givestone":
                    completions.add("<淬炼石名> <数量>");
                    break;
                case "givepaper":
                    completions.add("<保护符名>");
                    break;
                case "givedupaper":
                    completions.add("<直升符名>");
                    break;
                case "givespestone":
                    completions.add("<宝石名>");
                    break;
                case "givesoul":
                    completions.add("<精魂名>");
                    break;
                case "setnbt":
                    completions.add("<nbt键名> <nbt值>");
                    break;
            }
        }
        return completions;
    }
    public void reloadConfig(){
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
    }
}
