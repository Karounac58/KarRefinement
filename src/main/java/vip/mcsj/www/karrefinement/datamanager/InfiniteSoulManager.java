package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.object.InfiniteSoul;
import vip.mcsj.www.karrefinement.utils.FileUtil;

import java.util.*;

public class InfiniteSoulManager {
    public static Map<String, InfiniteSoul> infiniteSouls = new HashMap<>();

    public static Set<Material> soulMaterials = new HashSet<>();
    private ItemStack equipmentItem;

    private String soulName;

    public InfiniteSoulManager(ItemStack equipmentItem) {
        this.equipmentItem = equipmentItem;
    }

    public InfiniteSoulManager(String soulName) {
        this.soulName = soulName;
    }

    public static void init(){
        if(!infiniteSouls.isEmpty()){
            infiniteSouls.clear();
        }
        YamlConfiguration customFileYaml = FileUtil.getCustomFileYaml("infinitesoul.yml");
        Set<String> keys = customFileYaml.getKeys(false);
        for (String key : keys) {
            String name = customFileYaml.getString(key+".Name");
            Material type = Material.valueOf(customFileYaml.getString(key+".Type"));
            int data = customFileYaml.getInt(key+".Data");
            int customModelData = customFileYaml.getInt(key+".CustomModelData");
            int level = customFileYaml.getInt(key+".Level");
            List<String> lores = customFileYaml.getStringList(key+".Lore");
            InfiniteSoul infiniteSoul = new InfiniteSoul(key,name,lores,type,data,customModelData,level);
            infiniteSouls.put(key,infiniteSoul);
            soulMaterials.add(type);
        }
    }

    public ItemStack createInfiniteSoul(){
        InfiniteSoul infiniteSoul = infiniteSouls.get(this.soulName);
        ItemStack soulItem = new ItemStack(infiniteSoul.getType(), 1, (short) infiniteSoul.getData());
        ItemMeta im = soulItem.getItemMeta();
        im.setDisplayName(infiniteSoul.getName());
        im.setLore(infiniteSoul.getLore());
        soulItem.setItemMeta(im);
        NBT.modify(soulItem,nbt -> {
            nbt.setInteger("infinite",infiniteSoul.getLevel());
        });

        return soulItem;
    }

    public static boolean isInfiniteSoulLegal(ItemStack soulItem){
        if(soulItem == null || soulItem.getType() == Material.AIR) {
            return false;
        }
        if(!soulMaterials.contains(soulItem.getType())){
            return false;
        }
        int soulLevel = new NBTItem(soulItem).getInteger("infinite");
        for (String s : infiniteSouls.keySet()) {
            if(infiniteSouls.get(s).getLevel() == soulLevel){
                return true;
            }
        }
        return false;
    }

    public static String getSoulIdentifier(ItemStack itemSoul){
        int soulLevel = new NBTItem(itemSoul).getInteger("infinite");
        for (String s : infiniteSouls.keySet()) {
            if(infiniteSouls.get(s).getLevel() == soulLevel){
                return s;
            }
        }
        return null;
    }

    public static String getSoulName(ItemStack itemSoul){
        int soulLevel = new NBTItem(itemSoul).getInteger("infinite");
        for (String s : infiniteSouls.keySet()) {
            InfiniteSoul soul = infiniteSouls.get(s);
            if(soul.getLevel() == soulLevel){
                return soul.getName();
            }
        }
        return null;
    }

    public int getSoulLevel(){
        return new NBTItem(this.equipmentItem).getInteger("infinite");
    }

    public static boolean infiniteSoulUp(ItemStack itemSoul,ItemStack equipmentItem){
        ItemMeta equipmentMeta = equipmentItem.getItemMeta();
        int refinementLevel = new NBTItem(equipmentItem).getInteger("refinement");
        int nowLevel = new NBTItem(equipmentItem).getInteger("infinite");
        int willLevel = new NBTItem(itemSoul).getInteger("infinite");
        String soulIdentifier = getSoulIdentifier(itemSoul);
        List<String> lores;

        if(refinementLevel == 0){
            return false;
        }
        //如果淬炼等级大于精魂等级
        if(refinementLevel > willLevel){
            return false;
        }

        if(nowLevel == 0){
            lores = equipmentMeta.getLore();
            lores.add(infiniteSouls.get(soulIdentifier).getName());
            equipmentMeta.setLore(lores);
            equipmentItem.setItemMeta(equipmentMeta);
            NBT.modify(equipmentItem,nbt -> {
                nbt.setInteger("infinite",willLevel);
            });
            return true;
        }else{
            lores = equipmentMeta.getLore();
            if(willLevel > nowLevel){
                for (String s : infiniteSouls.keySet()) {
                    if(infiniteSouls.get(s).getLevel() == nowLevel){
                        lores.remove(infiniteSouls.get(s).getName());
                    }
                }
                lores.add(infiniteSouls.get(soulIdentifier).getName());
                equipmentMeta.setLore(lores);
                equipmentItem.setItemMeta(equipmentMeta);
                NBT.modify(equipmentItem,nbt -> {
                    nbt.setInteger("infinite",willLevel);
                });
                return true;
            }
            return false;
        }
    }
}
