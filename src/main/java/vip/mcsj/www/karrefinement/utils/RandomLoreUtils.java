package vip.mcsj.www.karrefinement.utils;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RandomLoreUtils {
    private static final String regex = "\\{Random\\.(\\d+)to(\\d+)\\}";
    private static final Pattern pattern = Pattern.compile(regex);

    public static boolean isRandomLore(List<String> lore){
        boolean result = false;
        for (String s : lore) {
            Matcher matcher = pattern.matcher(s);
            result =  matcher.find();
        }
        return result;
    }

    /**
     * 替换 {Random.XtoY} 为实际随机数
     */
    public static List<String> replaceWithRandom(List<String> list) {
        List<String> lore = new ArrayList<>();
        for (String s : list) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                int min = Integer.parseInt(matcher.group(1));
                int max = Integer.parseInt(matcher.group(2));

                // 生成范围内的随机数
                int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
                lore.add(s.replaceAll(regex, String.valueOf(randomNum)));
            }
        }
        return lore;
    }

    public static void addRandomLoreWithNBT(ItemStack itemStack, List<String> lore) {
        StringBuilder str = new StringBuilder();
        for (String s : lore) {
            str.append(s).append("|");
        }
        NBT.modify(itemStack,nbt -> {
            nbt.setString("randomlore",str.toString());
        });
    }

    public static boolean hasRandomLore(ItemStack itemStack) {
        return new NBTItem(itemStack).hasKey("randomlore");
    }

    public static List<String> getRandomLore(ItemStack itemStack) {
        return Arrays.stream(new NBTItem(itemStack).getString("randomlore").split("\\|")).collect(Collectors.toList());
    }
}
