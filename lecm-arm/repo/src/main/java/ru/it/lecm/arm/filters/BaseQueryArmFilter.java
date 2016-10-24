package ru.it.lecm.arm.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.arm.beans.filters.ArmDocumentsFilter;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 20.02.14
 * Time: 10:30
 */
public class BaseQueryArmFilter implements ArmDocumentsFilter {
    final private static Logger logger = LoggerFactory.getLogger(BaseQueryArmFilter.class);

    final public static String VALUE = "#value";

    @Override
    public String getQuery(String baseQuery, List<String> args) {
        String resultedQuery = "";
        if (baseQuery != null) {
            logger.debug("Filter baseQuery: " + baseQuery);

            if ( baseQuery.isEmpty()){
                return resultedQuery;
            }

            // все остальные данные - значения
            if (args.isEmpty()) {
                return baseQuery;
            }

            if (args.size() == 1) {
                resultedQuery = baseQuery.replaceAll(VALUE, args.get(0));
            } else {
                StringBuilder stringBuilder = new StringBuilder(resultedQuery);
                for (String filterValue : args) {
                    stringBuilder.append(baseQuery.replaceAll(VALUE, filterValue)).append(" OR ");
                }
                resultedQuery = stringBuilder.substring(0, stringBuilder.length() - 4);
            }
        }
        return resultedQuery;
    }
}
