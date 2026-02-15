package vip.mcsj.www.karrefinement.main.commands.subcommands;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import vip.mcsj.www.karrefinement.main.commands.KarAbstractCommand;

public class SetNBTCommand extends KarAbstractCommand {
    public SetNBTCommand() {
        super("setnbt","karrefinement.setnbt","",true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(isPlayerOnly()){
            Player p =  (Player) sender;
            ItemStack invItem = p.getInventory().getItemInMainHand();
            NBT.modify(invItem, nbt -> {
                nbt.setString(args[0], args[1]);
            });
            p.sendMessage("设置成功!");
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
