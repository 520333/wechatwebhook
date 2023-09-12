package com.rushbi.dingtalk.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TimeHandler {
    public static Logger log = LoggerFactory.getLogger(TimeHandler.class);

    /**
     * reference: https://www.utctime.net/
     * @param st UTC+0时间
     * @return UTC+8北京时间
     */
    public static String timeAdd(String st) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date stParse = simpleDateFormat.parse(st);
        return simpleDateFormat.format((stParse.getTime() + 28800 * 1000));
    }

    /**
     * @param pattern 正则表达式匹配 (T | .000Z)
     * @param replaceText 要替换的文本
     * @param strDateTime 字符串时间 2023-04-21T017:30:00.000Z
     * @return 正则替换后的时间
     */
    public static String timeRegx(String pattern,String replaceText,String strDateTime) throws ParseException {
        StringBuffer sb = new StringBuffer();
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strDateTime);
        while (matcher.find()) {
            ((Matcher) matcher).appendReplacement(sb, replaceText);
        }
        matcher.appendTail(sb);
        String s = timeAdd(sb.toString());//UTC+8
        log.info("正则后的UTC0时间：" + sb + " UTC+8北京时间：" + s);
        return timeAdd(sb.toString());
    }
}
