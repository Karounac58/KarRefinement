package vip.mcsj.www.karrefinement.object;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.gui.KarTakeItemGuiInvHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomInventory {
    private Inventory inventory;
    private List<ItemStack> allItems;
    private int currentPage = 0;
    private int itemsPerPage = 45; // 54格减去9格用于控制栏

    public CustomInventory(List<ItemStack> items) {
        this.allItems = items;
        createInventory();
    }

    private void createInventory() {
        this.inventory = Bukkit.createInventory(new KarTakeItemGuiInvHolder(), 54, "淬炼物品菜单 - 第 " + (currentPage + 1) + " 页");
        fillItems();
        addControlButtons();
    }

    private void fillItems() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());

        // 清空物品槽位（保留控制按钮位置）
        for (int i = 0; i < itemsPerPage; i++) {
            inventory.setItem(i, null);
        }

        // 填充当前页的物品
        for (int i = startIndex; i < endIndex; i++) {
            int slot = i - startIndex;
            inventory.setItem(slot, allItems.get(i));
        }
    }

    private void addControlButtons() {
        // 上一页按钮
        if (currentPage > 0) {
            ItemStack previousButton = createButton(
                    Material.ARROW,
                    "§e上一页",
                    Arrays.asList("§7点击切换到上一页", "§6当前页: " + (currentPage + 1))
            );
            inventory.setItem(45, previousButton);
        } else {
            // 第一页时禁用上一页按钮
            ItemStack disabledPrevious = createButton(
                    Material.BARRIER,
                    "§c已是第一页",
                    Collections.singletonList("§7没有更多页面了")
            );
            inventory.setItem(45, disabledPrevious);
        }

        // 页面信息显示
        ItemStack pageInfo = createButton(
                Material.PAPER,
                "§6页面信息",
                Arrays.asList(
                        "§7当前页: §e" + (currentPage + 1),
                        "§7总页数: §e" + getTotalPages(),
                        "§7总物品: §e" + allItems.size()
                )
        );
        inventory.setItem(49, pageInfo);

        // 下一页按钮
        if (hasNextPage()) {
            ItemStack nextButton = createButton(
                    Material.ARROW,
                    "§e下一页",
                    Arrays.asList("§7点击切换到下一页", "§6当前页: " + (currentPage + 1))
            );
            inventory.setItem(53, nextButton);
        } else {
            // 最后一页时禁用下一页按钮
            ItemStack disabledNext = createButton(
                    Material.BARRIER,
                    "§c已是最后一页",
                    Collections.singletonList("§7没有更多页面了")
            );
            inventory.setItem(53, disabledNext);
        }

        // 关闭按钮
        ItemStack closeButton = createButton(
                Material.BARRIER,
                "§c关闭菜单",
                Collections.singletonList("§7点击关闭此菜单")
        );
        inventory.setItem(48, closeButton);
    }

    private ItemStack createButton(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public boolean nextPage() {
        if (hasNextPage()) {
            currentPage++;
            updateInventory();
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateInventory();
            return true;
        }
        return false;
    }

    public boolean hasNextPage() {
        return (currentPage + 1) * itemsPerPage < allItems.size();
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) allItems.size() / itemsPerPage);
    }

    public void updateInventory() {
        fillItems();
        addControlButtons();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<ItemStack> getAllItems() {
        return allItems;
    }

    public void setAllItems(List<ItemStack> allItems) {
        this.allItems = allItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }
}
