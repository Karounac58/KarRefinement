package vip.mcsj.www.karrefinement.main.commands.subcommands;

import de.tr7zw.nbtapi.NBTItem;
import github.saukiya.sxattribute.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class GetNbtCommand extends KarAbstractCommand {

    public GetNbtCommand() {
        super("getnbt", "karrefinement.getnbt", "", true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player p   = (Player) sender;
        p.sendMessage(args[0] + ":" + new NBTItem(p.getInventory().getItemInMainHand()).getString(args[0]));
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
