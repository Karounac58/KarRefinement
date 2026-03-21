package vip.mcsj.www.karrefinement.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.datamanager.PaperDataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 保护符 Builder
 * 支持链式构建自定义保护符
 */
public class PaperBuilder {

    private final String paperName;
    private int amount = 1;
    private List<String> extraLore;

    PaperBuilder(String paperName) {
        this.paperName = paperName;
    }

    /**
     * 设置数量
     */
    public PaperBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * 添加额外的 Lore 行
     */
    public PaperBuilder extraLore(String... lines) {
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
        PaperDataManager pdm = new PaperDataManager(paperName);
        ItemStack item = pdm.createProtectedPaper();
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
