package ru.it.lecm.documents.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.documents.beans.DocumentFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DBashmakov
 * Date: 12.07.13
 * Time: 11:38
 */
public class BaseQueryFilter extends DocumentFilter {
    final private static Logger logger = LoggerFactory.getLogger(BaseQueryFilter.class);

    final public static String VALUE = "#value";

    @Override
    public String getId() {
        return "baseQuery";
    }

    @Override
    public String getQuery(Object[] args) {
        String query = "";
        if (args != null && args.length > 0) {
            logger.debug("Filter args:" + StringUtils.arrayToCommaDelimitedString(args));

            String baseQuery = (String) args[0];
            // все остальные данные - значения
            List<String> filterValues = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                if (args[i] != null) {
                    filterValues.add((String) args[i]);
                }
            }
            if (baseQuery == null || filterValues.isEmpty()) {
                return "";
            }
            if (filterValues.size() == 1) {
                query = baseQuery.replaceAll(VALUE, filterValues.get(0));
            } else {
                for (String filterValue : filterValues) {
                    query += "(" + baseQuery.replaceAll(VALUE, filterValue) + ") OR";
                }
                query = query.substring(0, query.length() - 3);
            }
        }
        return query;
    }
}
