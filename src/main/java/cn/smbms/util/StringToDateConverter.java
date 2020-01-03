package cn.smbms.util;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义日期转换器
 */
public class StringToDateConverter implements Converter<String, Date> {
    private String[] patterns;

    @Override
    public Date convert(String s) {
        Date date = null;
        for (String pattern:patterns) {
            try {
                date = new SimpleDateFormat(pattern).parse(s);
                break;
            } catch (ParseException e) {
                continue;
            }
        }
        return date;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
    }
}
