package vip.mcsj.www.karrefinement.api.model;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IRefinementService {

    IRefinementResult refine(OfflinePlayer player, ItemStack equipment, IStone stone, double extraBonus);
}
