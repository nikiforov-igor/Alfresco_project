package ru.it.lecm.statemachine.assign;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.*;

/**
 * User: PMelnikov
 * Date: 24.12.12
 * Time: 13:58
 */
public class AssignWorkflow {

	private static ServiceRegistry serviceRegistry;
	private static OrgstructureBean orgstructureBean;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		AssignWorkflow.serviceRegistry = serviceRegistry;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		AssignWorkflow.orgstructureBean = orgstructureBean;
	}

	/**
	 * Преобразует список бизнес ролей в строковое значение
	 * @param businessRoles
	 * @return
	 */
	public String encode(List<AssociationRef> businessRoles) {
		String result = "";
		NodeService nodeService = serviceRegistry.getNodeService();
		for (AssociationRef role : businessRoles) {
			String name = (String) nodeService.getProperty(role.getTargetRef(), ContentModel.PROP_NAME);
			result += name + ";";
		}
		return result;
	}

	/**
	 * Преобразует строковое значение закодированных бизнес ролей в список бизнес ролей
	 *
	 * @param encodedBusinesRoles
	 * @return
	 */
	public List<NodeRef> decode(String encodedBusinesRoles) {
		ResultSet rs = serviceRegistry.getSearchService().query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_XPATH, "/app:company_home/lecm-dic:Dictionary/lecm-dic:Бизнес_x0020_роли");
		NodeRef rolesHome;
		try {
			if (rs.length() == 0) {
				throw new AlfrescoRuntimeException("Didn't find Company Home");
			}
			rolesHome = rs.getNodeRef(0);
		} finally {
			rs.close();
		}
		List<NodeRef> roles = new ArrayList<NodeRef>();
		NodeService nodeService = serviceRegistry.getNodeService();
		StringTokenizer tokenizer = new StringTokenizer(encodedBusinesRoles, ";");
		while (tokenizer.hasMoreTokens()) {
			String name = tokenizer.nextToken();
			if (!"".equals(name)) {
				NodeRef role = nodeService.getChildByName(rolesHome, ContentModel.ASSOC_CONTAINS, name);
				if (role != null) {
					roles.add(role);
				}
			}
		}
		return roles;
	}

	/**
	 * Декодирует строку бизнес ролей и готовит список NodeRef реальных персон.
	 * @param encodedBusinesRoles
	 * @return
	 */
	public Set<NodeRef> getRealPersons(String encodedBusinesRoles) {
		List<NodeRef> roles = decode(encodedBusinesRoles);
		List<NodeRef> employees = new ArrayList<NodeRef>();
		for (NodeRef role : roles) {
			employees.addAll(orgstructureBean.getEmployeesByBusinessRole(role));
		}

		NodeService nodeService = serviceRegistry.getNodeService();

		HashSet<NodeRef> persons = new HashSet<NodeRef>();
		for (NodeRef employee : employees) {
			List<AssociationRef> person = nodeService.getTargetAssocs(employee, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
			if (person.size() == 1) {
				persons.add(person.get(0).getTargetRef());
			}
		}

		return persons;
	}

}
