package vip.mcsj.www.karrefinement.main.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.datamanager.Message;
import vip.mcsj.www.karrefinement.effect.ScriptRunnable;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

import java.util.UUID;

public class ScriptCommand extends KarAbstractCommand {
    public ScriptCommand() {
        super("script","karrefinement.script","§e/krf script —— §b开启/关闭淬炼特效",true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!checkPermission(sender)){
            return true;
        }
        if(isPlayerOnly()){
            Player p = requirePlayer(sender);
            if(p != null){
                UUID uuid = p.getUniqueId();
                ScriptRunnable.playerScriptSituation.put(uuid, !ScriptRunnable.playerScriptSituation.get(uuid));
                p.sendMessage(Message.messages.get("effect_switch").replace("{status}", ScriptRunnable.playerScriptSituation.get(uuid) ? "开启" : "关闭"));
            }
        }
        return true;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean isPlayerOnly() {
        return playerOnly;
    }
}
