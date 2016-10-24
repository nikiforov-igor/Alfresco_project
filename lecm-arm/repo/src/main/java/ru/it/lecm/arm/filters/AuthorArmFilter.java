package ru.it.lecm.arm.filters;

import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.arm.beans.filters.ArmDocumentsFilter;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 20.02.14
 * Time: 10:30
 */
public class AuthorArmFilter implements ArmDocumentsFilter {
    final private static Logger logger = LoggerFactory.getLogger(AuthorArmFilter.class);

    public static enum AuthorEnum {
        MY,
        DEPARTMENT,
        ALL,
        FAVOURITE
    }

    protected OrgstructureBean orgstructureService;
    protected AuthenticationService authService;
    protected PreferenceService preferenceService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @Override
    public String getQuery(String authorProperties, List<String> args) {
        StringBuilder resultedQuery = new StringBuilder();
        if (args != null && !args.isEmpty()) {
            logger.debug("Filter args: " + StringUtils.collectionToCommaDelimitedString(args));
	        logger.debug("Filter params: " + authorProperties);

            String filterValue = args.get(0);

            if (filterValue == null) {
                return resultedQuery.toString();
            }

            List<NodeRef> employees = new ArrayList<NodeRef>();
            try {
                String username = authService.getCurrentUserName();
                if (username != null) {
                    NodeRef currentEmployee = orgstructureService.getEmployeeByPerson(username);

                    switch (AuthorEnum.valueOf(filterValue.toUpperCase())) {
                        case MY: {
                            employees.add(currentEmployee);
                            break;
                        }
                        case DEPARTMENT: {
                            List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(currentEmployee);
                            employees.addAll(departmentEmployees);
                            if (employees.isEmpty()) {
                                resultedQuery.append("ID:\"NOT_ID\"");
                            }
                            break;
                        }
                        case FAVOURITE: {
                            String favourites = "org.alfresco.share.documents.favourites";
                            Map<String, Serializable> preferences = preferenceService.getPreferences(username, favourites);
                            String favouriteDocs = preferences.get(favourites).toString();
                            if (favouriteDocs != null && favouriteDocs.length() > 0) {
                                String[] docsRefs = favouriteDocs.split(",");
                                boolean addOR = false;
                                for (String docsRef : docsRefs) {
                                    resultedQuery.append(addOR ? " OR " : "").append("ID:").append(docsRef.replace(":", "\\:"));
                                    addOR = true;
                                }
                            }
                            break;
                        }
                        case ALL: {
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    if (employees.size() > 0) {
                        boolean addOR = false;
	                    for (String field : authorProperties.split(",")) {
		                    String authorProp = field.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
	                        for (NodeRef employeeRef : employees) {
	                            resultedQuery.append(addOR ? " OR " : "").append("@").append(authorProp).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"");
	                            addOR = true;
	                        }
	                    }
                    }
                }
            } catch (Exception ignored) {
                logger.warn("Incorrect filter! Filter args:" + StringUtils.collectionToCommaDelimitedString(args));
            }
        }
        return resultedQuery.toString();
    }
}
