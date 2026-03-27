package vip.mcsj.www.karrefinement.main;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.datamanager.*;
import vip.mcsj.www.karrefinement.gui.*;
import vip.mcsj.www.karrefinement.gui.holder.KarCompoundStoneInvHolder;
import vip.mcsj.www.karrefinement.gui.holder.KarRefinementInvHolder;
import vip.mcsj.www.karrefinement.main.commands.CommandFactory;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class KarExecutor implements TabCompleter {
    private final CommandFactory commandFactory;


    public KarExecutor() {
        commandFactory = new CommandFactory();
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
            completions.add("giveadhesive");
            completions.add("givedetachitem");
            completions.add("givepotion");
            completions.add("givefurnace");
            completions.add("setnbt");
            completions.add("opengui");
            completions.add("openforgegui");
            completions.add("opentransformgui");
            completions.add("adminup");
            completions.add("set");
            completions.add("reload");
            completions.add("getnbt");
            completions.add("clearlore");
            completions.add("openitemgui");
            completions.add("opencompoundpiecegui");
            completions.add("querypotion");
            completions.add("adminquerypotion");
        }else if(strings.length == 2){
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }else if(strings.length == 3){
            switch (strings[0].toLowerCase()){
                case "set":
                    completions.add("<等级>");
                    break;
                case "givestone":
                    completions.add("<淬炼石Nbt名> <数量>");
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
                case "givefurnace":
                    completions.add("<熔炉名>");
                    break;
                case "setnbt":
                    completions.add("<nbt键名> <nbt值>");
                    break;
                case "giveadhesive":
                    completions.add("<宝石粘合剂名>");
                    break;
                case "givepotion":
                    completions.add("<淬炼药水名>");
                case "givedetachitem":
                    completions.add("保护符拆卸工具");
                case "querypotion":
                    completions.add("");
                    break;
                case "adminquerypotion":
                    completions.add("");
                    break;
            }
        }
        return completions;
    }
    public void reloadConfig(){
        KarRefinement.instance.initGuiData();
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
        KarTakeItemGui.initItems();
    }
}
