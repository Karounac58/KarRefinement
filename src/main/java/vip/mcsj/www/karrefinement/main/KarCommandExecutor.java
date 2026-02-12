package vip.mcsj.www.karrefinement.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vip.mcsj.www.karrefinement.main.commands.CommandFactory;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;
import vip.mcsj.www.karrefinement.main.commands.subcommands.HelpCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KarCommandExecutor implements CommandExecutor, TabCompleter {

    private final CommandFactory commandFactory;


    public KarCommandExecutor() {
        commandFactory = new CommandFactory();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length == 0) {
            return new HelpCommand().execute(commandSender,args);
        }

        String subCommand = args[0].toLowerCase();
        KarAbstractCommand executor = commandFactory.getCommand(subCommand);

        if(executor == null) {
            commandSender.sendMessage("§c未知的命令，使用 /krf help 查看帮助");
            return true;
        }

        String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        return executor.execute(commandSender,remainingArgs);
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
            completions.add("setnbt");
            completions.add("opengui");
            completions.add("openforgegui");
            completions.add("opentransformgui");
            completions.add("adminup");
            completions.add("set");
            completions.add("reload");
            completions.add("script");
            completions.add("getnbt");
            completions.add("clearlore");
            completions.add("openitemgui");
            completions.add("opencompoundpiecegui");
            completions.add("querypotion");
            completions.add("adminquerypotion");
            completions.add("darkchange");
        }else if(strings.length == 2 && !strings[0].equals("darkchange")){
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }else if(strings.length == 2){
            completions.add("set");
            completions.add("seen");
            completions.add("clear");
        }else if(strings.length == 3){
            switch (strings[0].toLowerCase()){
                case "set":
                    completions.add("<等级>");
                    break;
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
                case "giveadhesive":
                    completions.add("<玩家名> <宝石粘合剂名>");
                    break;
                case "givepotion":
                    completions.add("<玩家名> <淬炼药水名>");
                case "givedetachitem":
                    completions.add("<玩家名> 保护符拆卸工具/宝石拆卸工具");
                case "querypotion":
                    completions.add("<玩家名>");
                    break;
                case "adminquerypotion":
                    completions.add("<玩家名>");
                    break;
            }
        }
        return completions;
    }
}
