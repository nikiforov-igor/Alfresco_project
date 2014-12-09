package ru.it.lecm.orgstructure.exportimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vlevin
 */
public class ExportImportHelper {

	final private NodeService nodeService;
	final private NamespaceService namespaceService;
	final private SearchService searchService;
	final private OrgstructureBean orgstructureService;

	public ExportImportHelper(NodeService nodeService, NamespaceService namespaceService, SearchService searchService, OrgstructureBean orgstructureService) {
		this.nodeService = nodeService;
		this.namespaceService = namespaceService;
		this.searchService = searchService;
		this.orgstructureService = orgstructureService;
	}

	public void addID(NodeRef node, String id) {
		PropertyMap props = new PropertyMap();
		props.put(ExportImportModel.PROP_ID, id);
		nodeService.addAspect(node, ExportImportModel.ASPECT_ID, props);
	}

	public List<NodeRef> getAllNodesByType(NodeRef rootNode, QName nodeType) {
		final List<NodeRef> result = new ArrayList<>();

		if (rootNode == null) {
			return result;
		}

		final SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		final String searchQuery = String.format("+PATH:\"%s//*\" AND TYPE:\"%s\"", nodeService.getPath(rootNode).toPrefixString(namespaceService), nodeType.toPrefixString(namespaceService));
		sp.setQuery(searchQuery);
		ResultSet results = null;

		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				result.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}

		return result;
	}

	public String getNodeRefID(NodeRef node) {
		String id;

		if (node == null) {
			return null;
		}

		if ("admin".equals(orgstructureService.getEmployeeLogin(node))) {
			id = "admin";
		} else if (nodeService.hasAspect(node, ExportImportModel.ASPECT_ID)) {
			id = (String) nodeService.getProperty(node, ExportImportModel.PROP_ID);
		} else {
			id = (String) nodeService.getProperty(node, ContentModel.PROP_NODE_UUID);
		}

		return id;
	}

	public List<NodeRef> getAllStaff() {
		return getAllNodesByType(orgstructureService.getRootUnit(), OrgstructureBean.TYPE_STAFF_LIST);
	}

	public List<NodeRef> getAllOrgUnits() {
		return getAllNodesByType(orgstructureService.getRootUnit(), OrgstructureBean.TYPE_ORGANIZATION_UNIT);
	}

	public Map<String, NodeRef> getNodeRefsIDs(List<NodeRef> nodeList) {
		Map<String, NodeRef> result = new HashMap<>();

		for (NodeRef node : nodeList) {
			result.put(getNodeRefID(node), node);
		}
		return result;
	}

	public List<NodeRef> getAllEmployees() {
		final NodeRef employeesDir = orgstructureService.getEmployeesDirectory();
		final List<NodeRef> employeesNodes = new ArrayList<>();

		final Set<QName> employeesType = new HashSet<>();
		employeesType.add(OrgstructureBean.TYPE_EMPLOYEE);

		final List<ChildAssociationRef> employeesChildAssocs = nodeService.getChildAssocs(employeesDir, employeesType);

		for (ChildAssociationRef employeeChildAssoc : employeesChildAssocs) {
			employeesNodes.add(employeeChildAssoc.getChildRef());
		}
		return employeesNodes;
	}

	public Map<String, NodeRef> getBusinessRolesIDs(List<NodeRef> businessRolesList) {
		Map<String, NodeRef> result = new HashMap<>();

		for (NodeRef node : businessRolesList) {
			result.put(orgstructureService.getBusinessRoleIdentifier(node), node);
		}

		return result;
	}

}
