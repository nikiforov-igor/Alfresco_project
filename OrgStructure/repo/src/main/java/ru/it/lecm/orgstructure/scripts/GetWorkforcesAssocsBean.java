package ru.it.lecm.orgstructure.scripts;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author dbashmakov
 *         Date: 18.10.12
 *         Time: 15:51
 */
public class GetWorkforcesAssocsBean extends AbstractWebScript {

	private static final Log log = LogFactory.getLog(GetWorkforcesAssocsBean.class);

	private static final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";
	public static final String DEFAULT_STORE_TYPE = "workspace";
	public static final String DEFAULT_STORE_ID = "SpacesStore";

	private static final QName EMPLOYEES = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce-employee-assoc");
	private static final QName ROLE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workforce-role-assoc");

	private static final QName EMPLOYEE_FIRST_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
	private static final QName EMPLOYEE_MIDDLE_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-middle-name");
	private static final QName EMPLOYEE_LAST_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-last-name");
	private static final QName NAME = ContentModel.PROP_NAME;

	public static final QName WORKFORCE_ASSOC = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "workGroup-workforce-assoc");

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		Map<String, String> templateParams = req.getServiceMatch().getTemplateVars();
		String storeType = templateParams.get("store_type") != null ? templateParams.get("store_type") : DEFAULT_STORE_TYPE;
		String storeId = templateParams.get("store_id") != null ? templateParams.get("store_id") : DEFAULT_STORE_ID;
		String id = templateParams.get("id");
		String ref;
		if (id == null) {
			res.getWriter().write("[]");
			return;
		} else {
			ref = storeType + "://" + storeId + "/" + id;
		}

		NodeService nodeService = serviceRegistry.getNodeService();
		NodeRef projectRef = new NodeRef(ref);

		JSONArray compositions = new JSONArray();

		List<AssociationRef> workForces = nodeService.getTargetAssocs(projectRef, WORKFORCE_ASSOC);

		for (AssociationRef workForceAssoc : workForces) {
			NodeRef workForce = workForceAssoc.getTargetRef();

			JSONObject wf = new JSONObject();

			List<AssociationRef> employees = nodeService.getTargetAssocs(workForce, EMPLOYEES);
			AssociationRef role = nodeService.getTargetAssocs(workForce, ROLE).get(0); // роль в объекте может быть только одна
			try {
				String propertyRole = (String) nodeService.getProperty(role.getTargetRef(), NAME);
				wf.put("workforce_role", propertyRole != null ? propertyRole : "ERROR");

				String employeesString = "";
				if (employees != null) { //если есть сотрудники
					for (AssociationRef employee : employees) {
						NodeRef pRef = employee.getTargetRef();
						String labelValue = nodeService.getProperty(pRef, EMPLOYEE_LAST_NAME) + " " +
								nodeService.getProperty(pRef, EMPLOYEE_MIDDLE_NAME) + " " +
								nodeService.getProperty(pRef, EMPLOYEE_FIRST_NAME);
						employeesString += labelValue + ",";
					}
					employeesString = employeesString.substring(0, employeesString.length() -1);
				}
				wf.put("workforce_employees", employeesString);

				wf.put("nodeRef", workForce.toString());
				wf.put("parentRef", projectRef.toString());
			} catch (JSONException e) {
				log.error(e);
			}
			compositions.put(wf);
		}
		res.setContentEncoding("utf-8");
		res.getWriter().write(compositions.toString());
	}
}
