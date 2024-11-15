package vip.mcsj.www.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     *
     * @param source    要匹配中文的字符串
     * @return  中文
     */
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
}
