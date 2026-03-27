package vip.mcsj.www.karrefinement.datamanager;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.mcsj.www.karrefinement.api.KarRefinementAPI;
import vip.mcsj.www.karrefinement.core.Service;
import vip.mcsj.www.karrefinement.object.Detach;
import vip.mcsj.www.karrefinement.object.DetachItem;
import vip.mcsj.www.karrefinement.object.ProtectPaper;
import vip.mcsj.www.karrefinement.object.SpeStone;
import vip.mcsj.www.karrefinement.utils.FileUtil;
import vip.mcsj.www.karrefinement.utils.KarUtils;
import vip.mcsj.www.karrefinement.utils.ReflectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DetachDataManager implements Service {
    public static Map<String, Detach> paperDetachs = new HashMap<>();
    public static Map<String, Detach> speStoneDetachs = new HashMap<>();
    public static DetachItem paperDetachItem;
    public static DetachItem speStoneDetachItem;
    public static boolean enabled;

    private String identifier;
    public DetachDataManager(String identifier) {
        this.identifier = identifier;
    }

    public DetachDataManager() {
        this.identifier = null;
    }

    @Override
    public void initialize() {
        init();
    }

    public static void init(){
        if(!paperDetachs.isEmpty()){
            paperDetachs.clear();
        }
        if(!speStoneDetachs.isEmpty()){
            speStoneDetachs.clear();
        }
        YamlConfiguration file = FileUtil.getCustomFileYaml("detach.yml");
        enabled = file.getBoolean("enable");
        paperDetachItem = initDetachItem(file,"PaperDetachItem");
        speStoneDetachItem = initDetachItem(file,"SpeStoneDetachItem");
        ConfigurationSection paperCS = file.getConfigurationSection("ProtectPaper");
        ConfigurationSection speStoneCS = file.getConfigurationSection("SpeStone");
        Set<String> paperKeys = paperCS.getKeys(false);
        Set<String> speStoneKeys = speStoneCS.getKeys(false);
        for (String paperKey : paperKeys) {
            paperDetachs.put(paperKey,initDetach(paperCS,paperKey));
        }

        for (String speStoneKey : speStoneKeys) {
            speStoneDetachs.put(speStoneKey,initDetach(speStoneCS,speStoneKey));
        }
    }

    public ItemStack createPaperPiece(){
        Detach detach = paperDetachs.get(this.identifier);
        DetachItem paperDetachItem = detach.getItem();
        ItemStack item = new ItemStack(paperDetachItem.getType(),1,(short) paperDetachItem.getData());
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(paperDetachItem.getName());
        im.setLore(paperDetachItem.getLore());
        item.setItemMeta(im);
        ReflectionUtils.setCustomModelData(item,paperDetachItem.getCmd());
        NBT.modify(item,nbt -> {
            nbt.setInteger("paperpiece",paperDetachItem.getLevel());
        });
        return item;
    }

    public ItemStack createSpeStonePiece(){
        Detach detach = speStoneDetachs.get(this.identifier);

        DetachItem speStoneDetachItem = detach.getItem();
        ItemStack item = new ItemStack(speStoneDetachItem.getType(),1,(short) speStoneDetachItem.getData());
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(speStoneDetachItem.getName());
        im.setLore(speStoneDetachItem.getLore());
        item.setItemMeta(im);
        NBT.modify(item,nbt -> {
            nbt.setInteger("spestonepiece",speStoneDetachItem.getLevel());
            nbt.setString("spestonepiecefrom",detach.getIdentifier());
        });
        return item;
    }

    public static ItemStack createPaperDetachItem(){
        ItemStack item = new ItemStack(paperDetachItem.getType(),1,(short) paperDetachItem.getData());
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(paperDetachItem.getName());
        im.setLore(paperDetachItem.getLore());
        item.setItemMeta(im);
        ReflectionUtils.setCustomModelData(item,paperDetachItem.getCmd());
        NBT.modify(item,nbt -> {
            nbt.setInteger("paperdetachitem",1);
        });
        return item;
    }

    public static ItemStack createSpeStoneDetachItem(){
        ItemStack item = new ItemStack(speStoneDetachItem.getType(),1,(short) speStoneDetachItem.getData());
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(speStoneDetachItem.getName());
        im.setLore(speStoneDetachItem.getLore());
        item.setItemMeta(im);
        ReflectionUtils.setCustomModelData(item,speStoneDetachItem.getCmd());
        NBT.modify(item,nbt -> {
            nbt.setInteger("spestonedetachitem",1);
        });
        return item;
    }

    private static Detach initDetach(ConfigurationSection cs,String key){
        String name = cs.getString(key + ".Item.Name");
        Material type = Material.valueOf(cs.getString(key + ".Item.Type"));
        int data =  cs.getInt(key + ".Item.Data");
        int customModelData =  cs.getInt(key + ".Item.CustomModelData");
        List<String> lore = cs.getStringList(key + ".Item.Lore");

        int level = cs.getInt(key + ".Item.Level");
        DetachItem detachItem =  new DetachItem(name, type, data, customModelData,level, lore);

        Double chance = cs.getDouble(key+".Chance");
        List<Integer> numbers =  cs.getIntegerList(key+".Number");
        int compound =  cs.getInt(key+".Compound");

        return new Detach(key,chance,numbers,compound,detachItem);
    }

    private static DetachItem initDetachItem(YamlConfiguration cs, String key){
        String name = cs.getString(key + ".Name");
        Material type = Material.valueOf(cs.getString(key + ".Type"));
        int data =  cs.getInt(key + ".Data");
        int customModelData =  cs.getInt(key + ".CustomModelData");
        List<String> lore = cs.getStringList(key + ".Lore");
        return new DetachItem(name,type,data,customModelData,lore);
    }

    public static boolean isPaperDetachItemLegal(ItemStack item){
        if(item == null || item.getType() == Material.AIR) {
            return false;
        }

        return new NBTItem(item).getKeys().contains("paperdetachitem");
    }

    public static boolean isSpeStoneDetachItemLegal(ItemStack item){
        if(item == null || item.getType() == Material.AIR) {
            return false;
        }

        return new NBTItem(item).getKeys().contains("spestonedetachitem");
    }

    public static boolean isPaperPieceLegal(ItemStack item){
        if(item == null || item.getType() == Material.AIR) {
            return false;
        }

        return new NBTItem(item).getKeys().contains("paperpiece");
    }

    public static boolean isSpeStonePieceLegal(ItemStack item){
        if(item == null || item.getType() == Material.AIR) {
            return false;
        }

        return new NBTItem(item).getKeys().contains("spestonepiece");
    }

    public static boolean canPaperDetachUp(ItemStack detachItem,ItemStack equipmentItem){
        if(!new NBTItem(equipmentItem).hasKey("protector")){
            return false;
        }

        if(!isPaperDetachItemLegal(detachItem)){
            return false;
        }
        Integer protectLevel = new NBTItem(equipmentItem).getInteger("protector");
        String paperIdentifier = PaperDataManager.getIdentifier(equipmentItem);
        Detach paperDetach = paperDetachs.get(paperIdentifier);
        if(paperDetach == null){
            return false;
        }

        return true;
    }

    public static boolean canSpeStoneDetachUp(ItemStack detachItem,ItemStack equipmentItem){
        if(equipmentItem == null || equipmentItem.getType() == Material.AIR){
            return false;
        }

        if(!isSpeStoneDetachItemLegal(detachItem)){
            return false;
        }

        NBTItem nbtItem = new NBTItem(equipmentItem);
        SpeStone speStone1 = KarRefinementAPI.getService(SpecialStoneDataManager.class).get(equipmentItem);
        Detach speStoneDetach = speStoneDetachs.get(speStone1.getIdentifier());
        if(speStone1 == null){
            return false;
        }

        if(speStoneDetach == null){
            return false;
        }

        return true;
    }

    public ItemStack paperDetachItemUp(ItemStack detachItem,ItemStack equipmentItem){
        Detach paperDetach = paperDetachs.get(this.identifier);
        ProtectPaper protectPaper = PaperDataManager.papers.get(this.identifier);
        String equipmentLore = protectPaper.getName();
        List<Integer> numbers = paperDetach.getNumbers();

        System.out.println(numbers);


        ItemMeta itemMeta = equipmentItem.getItemMeta();
        List<String> lore = itemMeta.getLore();
        //移除物品的保护符lore
        lore.remove(equipmentLore);
        itemMeta.setLore(lore);
        equipmentItem.setItemMeta(itemMeta);
        NBT.modify(equipmentItem,nbt -> {
            nbt.removeKey("protector");
        });
        KarUtils.removeItemRefinement(detachItem);

        ItemStack paperPiece = createPaperPiece();
        paperPiece.setAmount(ThreadLocalRandom.current().nextInt(numbers.get(0),numbers.get(1)));

        return paperPiece;
    }

    public ItemStack speStoneDetachItemUp(ItemStack detachItem,ItemStack equipmentItem){
        Random random = new Random();

        List<SpeStone> speStones = SpecialStoneDataManager.getSpeStones(equipmentItem);

        SpeStone speStone = SpecialStoneDataManager.speStones.get(this.identifier);
        Detach speStoneDetach = speStoneDetachs.get(this.identifier);
        List<String> equipmentLore = speStone.getEquipmentLore();
        List<Integer> numbers = speStoneDetach.getNumbers();


        ItemMeta itemMeta = equipmentItem.getItemMeta();
        List<String> lore = itemMeta.getLore();
        //移除物品的宝石lore
        if(speStones.size() == 1){
            lore.remove(EquipmentDataManager.speStoneLore);
        }
        lore.removeAll(equipmentLore);
        itemMeta.setLore(lore);
        equipmentItem.setItemMeta(itemMeta);
        NBT.modify(equipmentItem,nbt -> {
            nbt.removeKey(speStone.getNbtKey());
        });
        KarUtils.removeItemRefinement(detachItem);

        ItemStack speStonePiece = createSpeStonePiece();
        speStonePiece.setAmount(ThreadLocalRandom.current().nextInt(numbers.get(0),numbers.get(1)));

        return speStonePiece;
    }

    public static int getPaperPieceLevel(ItemStack paperPiece){
        return new NBTItem(paperPiece).getInteger("paperpiece");
    }

    public static List<Object> getSpeStonePieceInfo(ItemStack speStonePiece){
        List<Object> list = new ArrayList<>();
        NBTItem nbtItem = new NBTItem(speStonePiece);
        list.add(nbtItem.getInteger("spestonepiece"));
        list.add(nbtItem.getString("spestonepiecefrom"));
        return list;
    }
}
