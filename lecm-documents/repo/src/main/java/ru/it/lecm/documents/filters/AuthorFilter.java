package ru.it.lecm.documents.filters;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.documents.beans.DocumentFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: DBashmakov
 * Date: 12.07.13
 * Time: 11:38
 */
public class AuthorFilter extends DocumentFilter {
    final private static Logger logger = LoggerFactory.getLogger(AuthorFilter.class);

    public static enum AuthorEnum {
        MY,
        DEPARTMENT,
        ALL,
        FAVOURITE
    }

    @Override
    public String getId() {
        return "docAuthor";
    }

    @Override
    public String getQuery(Object[] args) {
        StringBuilder query = new StringBuilder();
        if (args != null && args.length > 0) {
            String docType = (String) args[0];
            String filterValue = (String) args[1];
            if (docType == null || filterValue == null) {
                return "";
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
                            break;
                        }
                        case FAVOURITE: {
                            String favourites = "org.alfresco.share.documents.favourites";
                            String currentUser = authService.getCurrentUserName();
                            Map<String, Serializable> preferences = preferenceService.getPreferences(currentUser, favourites);
                            String favouriteDocs = preferences.get(favourites).toString();
                            if (favouriteDocs != null && favouriteDocs.length() > 0) {
                                String[] docsRefs = favouriteDocs.split(",");
                                boolean addOR = false;
                                for (String docsRef : docsRefs) {
                                    query.append(addOR ? " OR " : "").append("ID:").append(docsRef.replace(":", "\\:"));
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
                        QName type = QName.createQName(docType, namespaceService);
                        String authorProperty = documentService.getAuthorProperty(type);
                        authorProperty = authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

                        for (NodeRef employeeRef : employees) {
                            query.append(addOR ? " OR " : "").append("@").append(authorProperty).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"");
                            addOR = true;
                        }
                    }
                }
            } catch (Exception ignored) {
                logger.warn("Incorrect filter! Filter args:" + StringUtils.arrayToCommaDelimitedString(args));
            }
        }
        return query.toString();
    }
}
