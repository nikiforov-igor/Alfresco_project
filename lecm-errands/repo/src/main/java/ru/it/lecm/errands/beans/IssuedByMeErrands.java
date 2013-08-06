package ru.it.lecm.errands.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentFilter;
import ru.it.lecm.documents.beans.FiltersManager;

/**
 * User: dbashmakov
 * Date: 05.08.13
 * Time: 11:14
 */
public abstract class IssuedByMeErrands extends DocumentFilter{

    final private static Logger logger = LoggerFactory.getLogger(IssuedByMeErrands.class);

    @Override
    public String getQuery(Object[] args) {
        // фильтр работает через ErrandsFilter, c параметрами, заданными в service-context
        DocumentFilter errandsFilter = FiltersManager.getFilterById(ErrandsFilter.ID);
        if (errandsFilter != null) {
            return errandsFilter.getQuery(getParamStr().split("/"));
        } else {
            logger.error("Could not find ErrandsFilter! Check xml config!!!");
        }
        return "";
    }

    @Override
    public String getParamStr() {
        return this.paramStr;
    }
}
