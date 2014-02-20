package ru.it.lecm.arm.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.arm.beans.filters.ArmDocumenstFilter;

import java.util.List;

/**
 * User: dbashmakov
 * Date: 20.02.14
 * Time: 10:30
 */
public class BaseQueryArmFilter implements ArmDocumenstFilter {
    final private static Logger logger = LoggerFactory.getLogger(BaseQueryArmFilter.class);

    final public static String VALUE = "#value";

    @Override
    public String getQuery(Object armNode, List<String> args) {
        String resultedQuery = "";
        if (args != null && !args.isEmpty()) {
            logger.debug("Filter args: " + StringUtils.collectionToCommaDelimitedString(args));

            String baseQuery = args.get(0);
            if (baseQuery == null || baseQuery.isEmpty()){
                return  resultedQuery;
            }

            // все остальные данные - значения
            args.remove(0);

            if (args.isEmpty()) {
                return baseQuery;
            }

            if (args.size() == 1) {
                resultedQuery = baseQuery.replaceAll(VALUE, args.get(0));
            } else {
                for (String filterValue : args) {
                    resultedQuery += baseQuery.replaceAll(VALUE, filterValue) + " OR ";
                }
                resultedQuery = resultedQuery.substring(0, resultedQuery.length() - 4);
            }
        }
        return resultedQuery;
    }
}
