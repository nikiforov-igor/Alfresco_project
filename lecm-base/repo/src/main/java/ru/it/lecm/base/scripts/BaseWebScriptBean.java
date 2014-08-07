package ru.it.lecm.base.scripts;

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptPagingNodes;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.Pair;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Context;
import ru.it.lecm.base.ListOfUsedTypesBean;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmObjectsService;
import ru.it.lecm.base.beans.getchildren.FilterPropLECM;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.*;

/**
 * @author dbashmakov
 *         Date: 08.02.13
 *         Time: 12:53
 */
public class BaseWebScriptBean extends BaseWebScript {
	private NamespaceService namespaceService;
	private LecmObjectsService lecmObjectsService;
	private DictionaryService dictionaryService;
	private NodeService nodeService;
    private OrgstructureBean orgstructureService;
    private AuthorityService authorityService;

	final int REQUEST_MAX = 1000;

    private List<TypeMapper> registeredTypes = new ArrayList<TypeMapper>();

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setLecmObjectsService(LecmObjectsService lecmObjectsService) {
		this.lecmObjectsService = lecmObjectsService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

	public ScriptPagingNodes getChilds(ScriptNode node, String childQNameType, int maxItems, int skipCount, String sortProp, Boolean sortAsc, Boolean onlyActive) {
		return getChilds(node.getNodeRef(), childQNameType, maxItems, skipCount, sortProp, sortAsc, onlyActive);
	}

	public ScriptPagingNodes getChilds(final NodeRef nodeRef, final String childQNameType, final int maxItems, final int skipCount, final String sortProp, final Boolean sortAsc, final Boolean onlyActive) {
		Object[] results;

		QName childType = null;
		if (childQNameType != null) {
			childType = QName.createQName(childQNameType, namespaceService);
		}

		List<Pair<QName, Boolean>> sortProps = null; // note: null sortProps => get all in default sort order
		if (sortProp != null) {
			sortProps = new ArrayList<Pair<QName, Boolean>>(1);
			sortProps.add(new Pair<QName, Boolean>(QName.createQName(sortProp, namespaceService), sortAsc));
		}

		List<FilterProp> filter = new ArrayList<FilterProp>();
		if (onlyActive) {
			filter.add(new FilterPropLECM(BaseBean.IS_ACTIVE, Boolean.TRUE, FilterPropLECM.FilterTypeLECM.EQUALS, Boolean.TRUE));
		}
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        Set<String> auth = authorityService.getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());
//        if (!orgstructureService.isEmployeeHasBusinessRole(currentEmployee, "BR_GLOBAL_ORGANIZATIONS_ACCESS", false, false)) {
        if(!auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {
            NodeRef empOrganization = orgstructureService.getEmployeeOrganization(currentEmployee);
            filter.add(new FilterPropLECM(OrgstructureAspectsModel.PROP_LINKED_ORGANIZATION_REF, empOrganization != null ? empOrganization.toString() : "NOT_REF", FilterPropLECM.FilterTypeLECM.EQUALS, Boolean.TRUE));
        }

		PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, null);
		pageRequest.setRequestTotalCountMax(REQUEST_MAX);

		PagingResults<NodeRef> pageOfNodeInfos = null;
		FileFilterMode.setClient(FileFilterMode.Client.script);
		try {
			pageOfNodeInfos = lecmObjectsService.list(nodeRef, childType, filter, sortProps, pageRequest);
		} finally {
			FileFilterMode.clearClient();
		}

		List<NodeRef> nodeInfos = pageOfNodeInfos.getPage();
		int size = nodeInfos.size();
		results = new Object[size];
		for (int i = 0; i < size; i++) {
			NodeRef ref = nodeInfos.get(i);
			results[i] = new ScriptNode(ref, serviceRegistry, getScope());
		}

		int totalResultCountLower = -1;
		int totalResultCountUpper = -1;

		Pair<Integer, Integer> totalResultCount = pageOfNodeInfos.getTotalResultCount();
		if (totalResultCount != null) {
			totalResultCountLower = (totalResultCount.getFirst() != null ? totalResultCount.getFirst() : -1);
			totalResultCountUpper = (totalResultCount.getSecond() != null ? totalResultCount.getSecond() : -1);
		}

		return new ScriptPagingNodes(Context.getCurrentContext().newArray(getScope(), results), pageOfNodeInfos.hasMoreItems(), totalResultCountLower, totalResultCountUpper);
	}


    public ScriptPagingNodes getNotLecmChilds(ScriptNode node,
                                              boolean files,
                                              boolean folders,
                                              String ignoreTypes,
                                              int maxItems,
                                              int skipCount,
                                              String sortProp,
                                              Boolean sortAsc,
                                              String queryExecutionId) {
        Object[] results;

        Set<QName> ignoreTypeQNames = new HashSet<QName>(5);
        if (ignoreTypes != null && !ignoreTypes.isEmpty()) {
            String[] ignored = ignoreTypes.split(",");
            for (String ig : ignored) {
                if (!ig.isEmpty()) {
                    ignoreTypeQNames.add(QName.createQName(ig, namespaceService));
                }
            }
        }

        ignoreTypeQNames.addAll(lecmObjectsService.buildLecmObjectTypes());

        List<Pair<QName, Boolean>> sortProps = null; // note: null sortProps => get all in default sort order
        if (sortProp != null) {
            sortProps = new ArrayList<Pair<QName, Boolean>>(1);
            sortProps.add(new Pair<QName, Boolean>(QName.createQName(sortProp, namespaceService), sortAsc));
        }

        PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, queryExecutionId);
        pageRequest.setRequestTotalCountMax(REQUEST_MAX);

        PagingResults<NodeRef> pageOfNodeInfos = null;
        FileFilterMode.setClient(FileFilterMode.Client.script);
        try {
            pageOfNodeInfos = lecmObjectsService.list(node.getNodeRef(), files, folders, ignoreTypeQNames, sortProps, pageRequest);
        } finally {
            FileFilterMode.clearClient();
        }

        List<NodeRef> nodeInfos = pageOfNodeInfos.getPage();
        int size = nodeInfos.size();
        results = new Object[size];
        for (int i = 0; i < size; i++) {
            NodeRef ref = nodeInfos.get(i);
            results[i] = new ScriptNode(ref, serviceRegistry, getScope());
        }

        int totalResultCountLower = -1;
        int totalResultCountUpper = -1;

        Pair<Integer, Integer> totalResultCount = pageOfNodeInfos.getTotalResultCount();
        if (totalResultCount != null) {
            totalResultCountLower = (totalResultCount.getFirst() != null ? totalResultCount.getFirst() : -1);
            totalResultCountUpper = (totalResultCount.getSecond() != null ? totalResultCount.getSecond() : -1);
        }

        return new ScriptPagingNodes(Context.getCurrentContext().newArray(getScope(), results), pageOfNodeInfos.hasMoreItems(), totalResultCountLower, totalResultCountUpper);
    }

    public Object[] getRegisteredTypes() {
        if (registeredTypes == null || registeredTypes.size() == 0) {
            registeredTypes = new ArrayList<TypeMapper>();

            Map<String, String> registered = ListOfUsedTypesBean.getTypes();
            for (Map.Entry<String, String> typeEntry : registered.entrySet()) {
                registeredTypes.add(new TypeMapper(typeEntry.getKey(), typeEntry.getValue()));
            }
        }
        return registeredTypes.toArray();
    }

    public String dateToISOString(Object dateObj) {
        Date date = (Date) Context.jsToJava(dateObj, Date.class);
        return date != null ? DateFormatISO8601.format(date) : null;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public class TypeMapper {
        private String name;
        private String title;

        TypeMapper(String name, String title) {
            this.name = name;
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (title != null ? title.hashCode() : 0);
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            TypeMapper that = (TypeMapper) obj;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            if (title != null ? !title.equals(that.title) : that.title != null) return false;
            return true;
        }
    }

	public String getNodeTypeLabel(String nodeRef) {
		ParameterCheck.mandatory("nodeRef", nodeRef);

		QName nodeType = nodeService.getType(new NodeRef(nodeRef));
		if (nodeType != null) {
			TypeDefinition typeDef = dictionaryService.getType(nodeType);
			if (typeDef != null) {
				return  typeDef.getTitle();
			}
		}
		return null;
	}

	public String getTypeLabel(String type) {
		ParameterCheck.mandatory("type", type);

		QName nodeType = QName.createQName(type, namespaceService);
		if (nodeType != null) {
			TypeDefinition typeDef = dictionaryService.getType(nodeType);
			if (typeDef != null) {
				return  typeDef.getTitle();
			}
		}
		return null;
	}
}
