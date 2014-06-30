package ru.it.lecm.base.beans;

import java.util.regex.Pattern;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 12:31
 */
public interface SearchQueryProcessorService {
    public final Pattern PROC_PATTERN = Pattern.compile("[{]{2}.+?[}]{2}");

    public String processQuery(String query);

    public String getProcessorQuery(String id, String params);
}
