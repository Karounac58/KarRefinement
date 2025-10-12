package vip.mcsj.www.karrefinement.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColorUtil {
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
