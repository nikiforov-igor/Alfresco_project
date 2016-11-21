package ru.it.lecm.orgstructure.processors;

import ru.it.lecm.base.beans.SearchQueryProcessor;

import java.util.Map;

/**
 * User: dbashmakov
 * Date: 30.09.2016
 * Time: 11:38
 */
public class EmployeeHasLoginProcessor extends SearchQueryProcessor {
    @Override
    public String getQuery(Map<String, Object> params) {
        return "ISNOTNULL:\"lecm-orgstr:employee-person-login\" AND @lecm\\-orgstr\\:employee\\-person\\-login:\"?*\" AND NOT @lecm\\-orgstr\\:employee\\-person\\-login:\"\"";
    }
}
