package ru.it.lecm.arm.filters;

import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.arm.beans.filters.ArmDocumenstFilter;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 20.02.14
 * Time: 10:30
 */
public class AuthorArmFilter implements ArmDocumenstFilter {
    final private static Logger logger = LoggerFactory.getLogger(AuthorArmFilter.class);

    public static enum AuthorEnum {
        MY,
        DEPARTMENT,
        ALL,
        FAVOURITE
    }

    protected OrgstructureBean orgstructureService;
    protected DocumentService documentService;
    protected NamespaceService namespaceService;
    protected AuthenticationService authService;
    protected PreferenceService preferenceService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @Override
    public String getQuery(Object armNode, List<String> args) {
        String resultedQuery = "";
        if (args != null && !args.isEmpty()) {
            logger.debug("Filter args: " + StringUtils.collectionToCommaDelimitedString(args));

            if (armNode == null) {
                logger.debug("Cannot find armNode and get doc Types list");
                return resultedQuery;
            }

            JSONObject currentNode = (JSONObject) armNode;
            List<QName> docTypes = new ArrayList<QName>();
            try {
                String docTypesStr = (String) currentNode.get("types");
                String[] docTypesArray = docTypesStr.split(",");
                for (String type : docTypesArray) {
                    if (type.length() > 0) {
                        QName typeQName = QName.createQName(type, namespaceService);
                        docTypes.add(typeQName);
                    }
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }

            String filterValue = args.get(0);

            if (docTypes.isEmpty() || filterValue == null) {
                return resultedQuery;
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
                                resultedQuery += "ID:\"NOT_ID\"";
                            }
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
                                    resultedQuery += (addOR ? " OR " : "") + "ID:" + docsRef.replace(":", "\\:");
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
                        Set<String> authorPropsSet = new HashSet<String>();
                        for (QName docType : docTypes) {
                            String authorProperty = documentService.getAuthorProperty(docType);
                            authorPropsSet.add(authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-"));
                        }

                        for (String authorProp : authorPropsSet) {
                            for (NodeRef employeeRef : employees) {
                                resultedQuery += (addOR ? " OR " : "") + "@" + authorProp + ":\"" + employeeRef.toString().replace(":", "\\:") + "\"";
                                addOR = true;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                logger.warn("Incorrect filter! Filter args:" + StringUtils.collectionToCommaDelimitedString(args));
            }
        }
        return resultedQuery;
    }
}
