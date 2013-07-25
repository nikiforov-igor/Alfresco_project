package ru.it.lecm.documents.filters;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DBashmakov
 * Date: 12.07.13
 * Time: 11:38
 */
public class AuthorFilter extends DocumentFilter {

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
    public String getQuery(Object... args) {
        String docType = (String) args[0];
        String filterValue = (String) args[1];
        if (docType == null || filterValue == null) {
            return "";
        }

        String query = "";
        List<NodeRef> employees = new ArrayList<NodeRef>();

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
                    query += (addOR ? " OR " : "") + "@" + authorProperty + ":\"" + employeeRef.toString().replace(":", "\\:") + "\"";
                    addOR = true;
                }
            }
        }
        return query;
    }
}
