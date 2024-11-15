package vip.mcsj.www.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColorUtil {
    /**
     * 十六进制颜色方法，格式：&#FF0000
     * @param message 带有十六进制颜色符的字符串
     * @return   能直接拿来用的字符串
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

    /**
     * 渐变色方法，格式：#FF0000
     * @param startColor    渐变色起始十六进制数
     * @param endColor      渐变色结束十六进制数
     * @param message       要实现渐变色的字符串
     * @return  渐变色字符串
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


    /**
     * 渐变色粗体方法
     * @param message   渐变色字符串
     * @param identifier   传入要变成粗体的渐变色标识符
     * @return  标识符粗体的渐变色字符串
     */
    public static String applyTextFormatting(String message,String identifier) {
        return message.replace(identifier,ChatColor.BOLD+"▬"+ChatColor.RESET);
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
}
