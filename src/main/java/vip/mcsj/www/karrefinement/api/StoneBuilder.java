package vip.mcsj.www.karrefinement.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.StoneDataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 淬炼石 Builder
 * 支持链式构建自定义淬炼石
 */
public class StoneBuilder {

    private final String stoneName;
    private int amount = 1;
    private List<String> extraLore;

    StoneBuilder(String stoneName) {
        this.stoneName = stoneName;
    }

    /**
     * 设置数量
     */
    public StoneBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * 添加额外的 Lore 行
     */
    public StoneBuilder extraLore(String... lines) {
        if (extraLore == null) {
            extraLore = new ArrayList<>();
        }
        for (String line : lines) {
            extraLore.add(line);
        }
        return this;
    }

    /**
     * 构建 ItemStack
     */
    public ItemStack build() {
        StoneDataManager sdm = new StoneDataManager(stoneName);
        ItemStack item = sdm.createStone();
        if (item == null) return null;

        item.setAmount(amount);

        if (extraLore != null && !extraLore.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.addAll(extraLore);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }

        return item;
    }
}
