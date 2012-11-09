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
 *         Date: 12.10.12
 *         Time: 15:16
 */
public class GetStaffListsAssocsBean extends AbstractWebScript {

	private static final Log log = LogFactory.getLog(GetStaffListsAssocsBean.class);

	private static final String ORGSTRUCTURE_NAMESPACE_URI = "http://www.it.ru/lecm/org/structure/1.0";
	public static final String DEFAULT_STORE_TYPE = "workspace";
	public static final String DEFAULT_STORE_ID = "SpacesStore";

	private static final QName IS_BOSS = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "composition-is-boss");
	private static final QName IS_PRIMARY = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "composition-is-primary");
	private static final QName POSITION = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "composition-position-assoc");
	private static final QName EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "composition-employee-assoc");
	private static final QName EMPLOYEE_FIRST_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-first-name");
	private static final QName EMPLOYEE_MIDDLE_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-middle-name");
	private static final QName EMPLOYEE_LAST_NAME = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "employee-last-name");
	public static final QName UNIT_COMPOSITION_ASSOC = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "unit-composition-assoc");

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
		NodeRef unitRef = new NodeRef(ref);

		JSONArray compositions = new JSONArray();

		List<AssociationRef> unitCompositions =
				nodeService.getTargetAssocs(unitRef, UNIT_COMPOSITION_ASSOC);

		for (AssociationRef unitCompositionAssoc : unitCompositions) {
			NodeRef unitComposition = unitCompositionAssoc.getTargetRef();

			JSONObject uc = new JSONObject();

			Boolean propertyBoss = (Boolean) nodeService.getProperty(unitComposition, IS_BOSS);
			Boolean propertyPrimary = (Boolean) nodeService.getProperty(unitComposition, IS_PRIMARY);
			AssociationRef position = nodeService.getTargetAssocs(unitComposition, POSITION).get(0);

			List<AssociationRef> employees = nodeService.getTargetAssocs(unitComposition, EMPLOYEE);
			boolean hasEmployees = (employees.size() > 0);
			try {
				uc.put("is_boss", propertyBoss != null ? propertyBoss.toString() : "false");
				uc.put("is_primary", propertyPrimary != null ? propertyPrimary.toString() : "false");

				JSONObject posObject = new JSONObject();
				if (position != null) {
					NodeRef pRef = position.getTargetRef();
					String labelValue = (String) nodeService.getProperty(pRef, ContentModel.PROP_NAME);
					posObject.put("nodeRef", pRef);
					posObject.put("name", labelValue);
				}
				uc.put("position", posObject);

				if (hasEmployees) {
					JSONObject employeeObject = new JSONObject();
					AssociationRef employee = employees.get(0);
					if (employee != null) {
						NodeRef pRef = employee.getTargetRef();
						String labelValue = nodeService.getProperty(pRef, EMPLOYEE_LAST_NAME) + " " +
								nodeService.getProperty(pRef, EMPLOYEE_MIDDLE_NAME) + " " +
								nodeService.getProperty(pRef, EMPLOYEE_FIRST_NAME);
						employeeObject.put("nodeRef", pRef);
						employeeObject.put("name", labelValue);
					}
					uc.put("employee", employeeObject);
				}
				uc.put("nodeRef", unitComposition.toString());
				uc.put("parentRef", unitRef.toString());
			} catch (JSONException e) {
				log.error(e);
			}
			compositions.put(uc);
		}
		res.setContentEncoding("utf-8");
		res.getWriter().write(compositions.toString());
	}
}
