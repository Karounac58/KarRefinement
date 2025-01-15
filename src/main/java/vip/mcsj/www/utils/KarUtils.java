package vip.mcsj.www.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KarUtils {
    //匹配中文字符。提取过滤后的字符串中的中文，即香烟名
    public static String regxChinese(String source){
        // 将上面要匹配的字符串转换成小写
        source = source.toLowerCase();
        // 匹配的字符串的正则表达式
        String regCharset = "[\\u4E00-\\u9FFF]+";
        Pattern p = Pattern.compile(regCharset);
        Matcher m = p.matcher(source);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            sb.append(m.group());
        }
        return sb.toString();
    }

    public static void removeItemRefinement(ItemStack item){
        int j = item.getAmount();
        j--;
        item.setAmount(j);
    }

    public static Integer getIntFromMap(Map<String,Integer> map,String key){
        return map.get(regxChinese(key));
    }

    public static boolean range(int current,int min,int max){
        return Math.max(min,current) == Math.min(current,max);
    }


    /**
     * 单十六进制方法
     * @param message
     * @return
     */
    private String translateRGBToMinecraftColor(String message) {
        // Use regular expression to find RGB color codes and replace them
        //找到十六进制颜色代码
        Pattern pattern = Pattern.compile("&#([0-9A-Fa-f]{6})");
        //创建匹配器
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            //根据十六进制颜色代码查询到原生颜色代码，以便后续的替换
            String replacement = ChatColor.of("#" + hexColor) + "";
            //将十六进制颜色代码替换成原生颜色代码，以便程序识别
            message = message.replace("&#" + hexColor, replacement);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String joinStrings(String[] strings, int startIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < strings.length; i++) {
            if (i > startIndex) {
                builder.append(" ");
            }
            builder.append(strings[i]);
        }
        return builder.toString();
    }

    /**
     * 渐变色方法
     * @param startColor
     * @param endColor
     * @param message
     * @return
     */
    public static String createColorGradientMessage(String startColor, String endColor, String message) {
        int length = message.length();

        StringBuilder gradientMessage = new StringBuilder();
        Color startRGB = hexToColor(startColor);
        Color endRGB = hexToColor(endColor);

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            Color interpolatedColor = interpolateColor(startRGB, endRGB, ratio);

            ChatColor chatColor = ChatColor.of(String.format("#%06X", interpolatedColor.getRGB() & 0xFFFFFF));
            gradientMessage.append(chatColor).append(message.charAt(i));
        }

        return gradientMessage.toString();
    }

    public static String applyTextFormatting(String message) {
        return message.replace("-",ChatColor.BOLD+"▬"+ChatColor.RESET);
    }

    private static Color hexToColor(String hex) {
        int rgb = Integer.parseInt(hex, 16);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new Color(red, green, blue);
    }

    private static Color interpolateColor(Color start, Color end, float ratio) {
        int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
        int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
        int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
        return new Color(red, green, blue);
    }

    public static float nextFloat(int bound){
        Random rand = new Random();
        float shit = rand.nextFloat();
        return shit * bound;
    }

    public static double nextDouble(int bound){
        Random rand = new Random();
        double shit = rand.nextDouble();
        return shit * bound;
    }

    public static Particle autoTwelveParticle(int[] versions){
        if(versions[1] <= 20){
            return Particle.valueOf("VILLAGER_ANGRY");
        }else{
            return Particle.valueOf("ANGRY_VILLAGER");
        }
    }

    public static Particle autoFifteenParticle(int[] versions){
        if(versions[1] <= 20){
            return Particle.valueOf("REDSTONE");
        }else{
            return Particle.valueOf("DUST");
        }
    }

    public static Particle autoEighteenParticle(int[] versions){
        if(versions[1] <= 20){
            return Particle.valueOf("SMOKE_NORMAL");
        }else{
            return Particle.valueOf("SMOKE");
        }
    }
}
