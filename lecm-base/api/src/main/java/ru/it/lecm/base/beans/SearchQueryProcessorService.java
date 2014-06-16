package ru.it.lecm.base.beans;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 12:31
 */
public interface SearchQueryProcessorService {

    public String processQuery(String query);

    public String getProcessorQuery(String id, String params);
}
