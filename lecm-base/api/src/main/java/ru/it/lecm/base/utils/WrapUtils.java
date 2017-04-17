package ru.it.lecm.base.utils;

/**
 * Created by APanyukov on 17.04.2017.
 */
public class WrapUtils {

    public static String wrapTitle(String text, String title) {
        return "<span class=\"wrapper-title\" title=\"" + title.replaceAll("\"", "&quot;") + "\">" + text + "</span>";
    }
}
