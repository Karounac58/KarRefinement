package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;
import vip.mcsj.www.karrefinement.main.commands.KarCommand;

public class HelpCommand extends KarAbstractCommand {

    public HelpCommand() {
        super("help", "karrefinement.help", "/karrefinement help", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }
        sender.sendMessage("§c§l§m  §6§l§m  §e§l§m  §a§l§m  §b§l§m  §e§lKarRefinement§b§l§m  §a§l§m  §e§l§m  §6§l§m  §c§l§m  ");
        sender.sendMessage("§e/krf adminup —— §b为手上物品升星");
        sender.sendMessage("§e/krf set <等级> —— §b为手上物品设置淬炼等级");
        sender.sendMessage("§e/krf reload —— §b重载配置文件");
        sender.sendMessage("§e/krf script —— §b开启/关闭淬炼特效");
        sender.sendMessage("§e/krf openitemgui —— §b打开淬炼物品菜单");
        sender.sendMessage("§e/krf givestone <玩家名> <淬炼石名> <数量> —— §b获取淬炼石");
        sender.sendMessage("§e/krf givepaper <玩家名> <保护符名> —— §b获取保护符");
        sender.sendMessage("§e/krf givedupaper <玩家名> <直升符名> —— §b获取直升符");
        sender.sendMessage("§e/krf givespestone <玩家名> <宝石名> —— §b获取宝石");
        sender.sendMessage("§e/krf givesoul <玩家名> <精魂名> —— §b获取无限耐久精魂");
        sender.sendMessage("§e/krf givedetachitem <玩家名> 保护符拆卸工具 —— §b获取保护符拆卸工具");
        sender.sendMessage("§e/krf giveadhesive <玩家名> <粘合剂名> —— §b获取宝石粘合剂");
        sender.sendMessage("§e/krf givepotion <玩家名> <淬炼药水名> —— §b获取淬炼药水");
        sender.sendMessage("§e/krf givefurnace <玩家名> <熔炉名> —— §b获取淬炼熔炉");
        sender.sendMessage("§e/krf opengui <玩家名> —— §b打开淬炼界面");
        sender.sendMessage("§e/krf openforgegui <玩家名> —— §b打开锻造界面");
        sender.sendMessage("§e/krf opencompoundgui <玩家名> —— §b打开宝石合石界面");
        sender.sendMessage("§e/krf opencompoundpiecegui <玩家名> —— §b打开保护符碎片合成界面");
        sender.sendMessage("§e/krf opentransformgui <玩家名> —— §b打开淬炼移星界面");
        sender.sendMessage("§e/krf querypotion <玩家名> —— §b让玩家查看淬炼药水加成");
        sender.sendMessage("§e/krf adminquerypotion <玩家名> —— §b查看玩家的淬炼药水加成");
        sender.sendMessage("§e/krf darkchange set <玩家名> true/false <次数> —— §b暗改玩家之后N次淬炼是否成功");
        sender.sendMessage("§e/krf darkchange seen <玩家名> —— §b查看玩家暗改淬炼情况");
        sender.sendMessage("§e/krf darkchange clear <玩家名> —— §b清除玩家暗改淬炼");
        return true;
    }

    @Override
    public String getUsage() {
        return this.usage;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean isPlayerOnly() {
        return this.playerOnly;
    }


}
