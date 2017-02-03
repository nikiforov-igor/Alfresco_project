package ru.it.lecm.documents.expression;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 05.04.13
 * Time: 8:59
 */
public class ExpressionUser {

    private ServiceRegistry serviceRegistry;
    private OrgstructureBean orgstructureBean;
    private NodeRef document;
    private DocumentService documentService;

    public ExpressionUser(NodeRef document, ServiceRegistry serviceRegistry, OrgstructureBean orgstructureBean, DocumentService documentService) {
        this.document = document;
        this.serviceRegistry = serviceRegistry;
        this.orgstructureBean = orgstructureBean;
        this.documentService = documentService;
    }

    /**
     * Возвращает текущего сотрудника для выражения
     * @return
     */
    public ExpressionEmployee currentUser() {
        String login = AuthenticationUtil.getFullyAuthenticatedUser();
        NodeRef employee = orgstructureBean.getEmployeeByPerson(login);
        return new ExpressionEmployee(employee, serviceRegistry, orgstructureBean);
    }


    /**
     * Возвращает сотрудника из ассоциативного поля документа
     * @param formField
     * @return
     */
    public ExpressionEmployee getUser(String formField) {
        QName employeeFieldName = QName.createQName(formField, serviceRegistry.getNamespaceService());
        List<AssociationRef> employees = serviceRegistry.getNodeService().getTargetAssocs(document, employeeFieldName);
        if (employees.size() > 0) {
            NodeRef employee = employees.get(0).getTargetRef();
            return new ExpressionEmployee(employee, serviceRegistry, orgstructureBean);
        } else {
            return null;
        }
    }

	/**
	 * Проверяет, что текущий пользователь является автором документа
	 * @return
	 */
	public boolean isAutor() {
		String login = AuthenticationUtil.getFullyAuthenticatedUser();
		NodeRef creator = documentService.getDocumentAuthor(document);
        String creatorLogin = orgstructureBean.getEmployeeLogin(creator);
		return login.equals(creatorLogin);
	}
}
