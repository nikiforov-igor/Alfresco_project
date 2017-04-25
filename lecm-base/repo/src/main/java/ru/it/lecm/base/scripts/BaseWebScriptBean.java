package ru.it.lecm.base.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptPagingNodes;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.Pair;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.ListOfUsedTypesBean;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmObjectsService;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.getchildren.FilterPropLECM;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;

/**
 * @author dbashmakov
 *         Date: 08.02.13
 *         Time: 12:53
 */
public class BaseWebScriptBean extends BaseWebScript {
    final static protected Logger logger = LoggerFactory.getLogger(BaseWebScriptBean.class);

	private NamespaceService namespaceService;
	private LecmObjectsService lecmObjectsService;
	private DictionaryService dictionaryService;
	private NodeService nodeService;
    private OrgstructureBean orgstructureService;
    private AuthorityService authorityService;
    private Properties globalProperties;
    private RepositoryStructureHelper repositoryStructureHelper;

	final int REQUEST_MAX = 1000;

    private List<TypeMapper> registeredTypes = new ArrayList<TypeMapper>();

	private final ConcurrentMap<QName, String> qNameStringMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, QName> stringQNameMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Boolean> isQNameAspectCache = new ConcurrentHashMap<>();

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

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

	public ScriptPagingNodes getChilds(ScriptNode node, String childQNameType, int maxItems, int skipCount, String sortProp, Boolean sortAsc, Boolean onlyActive) {
		return getChilds(node.getNodeRef(), childQNameType, maxItems, skipCount, sortProp, sortAsc, onlyActive);
	}

    public ScriptPagingNodes getChilds(ScriptNode node, String childQNameType, final String ignoredTypes, int maxItems, int skipCount, String sortProp, Boolean sortAsc, Boolean onlyActive) {
		return getChilds(node.getNodeRef(), childQNameType, ignoredTypes, maxItems, skipCount, sortProp, sortAsc, onlyActive, false);
	}

    public ScriptPagingNodes getChilds(ScriptNode node, String childQNameType, final String ignoredTypes, int maxItems, int skipCount, String sortProp, Boolean sortAsc, Boolean onlyActive, Boolean dontCheckAccess) {
		return getChilds(node.getNodeRef(), childQNameType, ignoredTypes, maxItems, skipCount, sortProp, sortAsc, onlyActive, dontCheckAccess);
	}

    public ScriptPagingNodes getChilds(ScriptNode node, String childQNameType, final String ignoredTypes, int maxItems, int skipCount, String sortProp, Boolean sortAsc, Boolean onlyActive, Boolean dontCheckAccess, Boolean onlyInSameOrg) {
		return getChilds(node.getNodeRef(), childQNameType, ignoredTypes, maxItems, skipCount, sortProp, sortAsc, onlyActive, dontCheckAccess, onlyInSameOrg);
	}

    public ScriptPagingNodes getChilds(final NodeRef nodeRef, final String childQNameType, final String ignoredTypes, final int maxItems, final int skipCount, final String sortProp, final Boolean sortAsc, final Boolean onlyActive, final Boolean doNotCheckAccess) {
        return getChilds(nodeRef, childQNameType, ignoredTypes, maxItems, skipCount, sortProp, sortAsc, onlyActive, doNotCheckAccess, false);
    }

    public ScriptPagingNodes getChilds(final NodeRef nodeRef, final String childQNameType, final String ignoredTypes, final int maxItems, final int skipCount, final String sortProp, final Boolean sortAsc, final Boolean onlyActive, final Boolean doNotCheckAccess, final Boolean onlyInSameOrg) {
        Object[] results;

        QName childType = null;
        QName checkAspect = null;
        if (childQNameType != null) {
            QName type = QName.createQName(childQNameType, namespaceService);
            if (dictionaryService.getType(type) != null) {
                childType = type;
            } else {
                //аспект или что-то иное
                if (dictionaryService.getAspect(type) != null) {
                    checkAspect = type;
                }
            }
        }

        Set<QName> ignoreTypeQNames = new HashSet<QName>(5);
        // Add user defined types to ignore
        if (ignoredTypes != null) {
            String[] ignored = ignoredTypes.split(",");
            for (String ignore : ignored) {
                if (!ignore.isEmpty()) {
                    ignoreTypeQNames.add(QName.createQName(ignore, namespaceService));
                }
            }
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

        if (!doNotCheckAccess)  {
            NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
            Set<String> auth = authorityService.getAuthoritiesForUser(AuthenticationUtil.getFullyAuthenticatedUser());
            if(!auth.contains("GROUP_LECM_GLOBAL_ORGANIZATIONS_ACCESS")) {
                NodeRef empOrganization = orgstructureService.getEmployeeOrganization(currentEmployee);
                filter.add(new FilterPropLECM(OrgstructureAspectsModel.PROP_LINKED_ORGANIZATION_REF, empOrganization != null ? empOrganization.toString() : "NOT_REF", FilterPropLECM.FilterTypeLECM.EQUALS, !onlyInSameOrg));
            }
        }

        PagingRequest pageRequest = new PagingRequest(skipCount, maxItems, null);
        pageRequest.setRequestTotalCountMax(REQUEST_MAX);

        PagingResults<NodeRef> pageOfNodeInfos = null;
        FileFilterMode.setClient(FileFilterMode.Client.script);
        try {
            pageOfNodeInfos = lecmObjectsService.list(nodeRef, childType, checkAspect, ignoreTypeQNames, filter, sortProps, pageRequest);
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

    public ScriptPagingNodes getChilds(final NodeRef nodeRef, final String childQNameType, final int maxItems, final int skipCount, final String sortProp, final Boolean sortAsc, final Boolean onlyActive, final Boolean doNotCheckAccess) {
        return getChilds(nodeRef, childQNameType, null, maxItems, skipCount, sortProp, sortAsc, onlyActive, doNotCheckAccess);
    }

	public ScriptPagingNodes getChilds(final NodeRef nodeRef, final String childQNameType, final int maxItems, final int skipCount, final String sortProp, final Boolean sortAsc, final Boolean onlyActive) {
        return getChilds(nodeRef, childQNameType, maxItems, skipCount, sortProp, sortAsc, onlyActive, false);
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

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
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

	public QName prefixedToQName(String prefixedQName) {
		QName qName = stringQNameMap.get(prefixedQName);
		if (qName == null) {
			qName = QName.resolveToQName(namespaceService, prefixedQName);
			stringQNameMap.putIfAbsent(prefixedQName, qName);
}
		return qName;
	}

	public String qNameToPrefixString(QName qName) {
		String prefixString = qNameStringMap.get(qName);
		if (prefixString == null) {
			prefixString = qName.toPrefixString(namespaceService);
			qNameStringMap.putIfAbsent(qName, prefixString);
		}
		return prefixString;
	}

	public boolean isAspect(String prefixedType) {
		Boolean result = isQNameAspectCache.get(prefixedType);
		if (result == null) {
			QName typeQName = QName.createQName(prefixedType, namespaceService);
			result = dictionaryService.getAspect(typeQName) != null;
			isQNameAspectCache.putIfAbsent(prefixedType, result);
		}
		return result;
	}

    public String getGlobalProperty(String key) {
        return encodeGlobalPropertyValue(this.globalProperties.getProperty(key, null));
    }

    public String getGlobalProperty(String key, String defaultValue) {
        return encodeGlobalPropertyValue(this.globalProperties.getProperty(key, defaultValue));
    }

    private String encodeGlobalPropertyValue(String value) {
        Charset isoCharset = Charset.forName("ISO-8859-1");
        if (value != null && isoCharset.newEncoder().canEncode(value)) {
            try {
                byte[] byteText = value.getBytes(isoCharset);
                return new String(byteText , "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.warn("Error encode global property value", e);
            }
        }

        return value;
    }

    /**
     * Получение ноды по её ID
     *
     * @param nodeId - ID
     * @return ScriptNode
     */
    public ScriptNode getNode(Long nodeId) {
        NodeRef nodeRef = nodeService.getNodeRef(nodeId);
        return nodeRef != null ? new ScriptNode(nodeRef, serviceRegistry, getScope()) : null;
    }

    /**
     * Перемещение документа в глубь структуры текущей папки
     *
     * @param document - что переместить
     * @param path - куда переместить
     */
    public void moveNode(final ScriptNode document, final String path) {
        moveNode(document, new ScriptNode(repositoryStructureHelper.getCompanyHomeRef(), serviceRegistry), path);
    }

    /**
     * Перемещение документа в глубь структуры текущей папки
     *
     * @param document - что переместить
     * @param target - куда переместить (родительская директория)
     * @param path - куда переместить
     */
    public void moveNode(final ScriptNode document, final ScriptNode target, final String path) {
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                NodeRef nodeRef = target.getNodeRef();
                while (tokenizer.hasMoreTokens()) {
                    String folder = tokenizer.nextToken();
                    if (!"".equals(folder)) {
                        NodeRef currentFolder = repositoryStructureHelper.getFolder(nodeRef, folder);
                        if (currentFolder == null) {
                            currentFolder = repositoryStructureHelper.createFolder(nodeRef, folder);
                        }
                        nodeRef = currentFolder;
                    }
                }
                ChildAssociationRef parent = nodeService.getPrimaryParent(document.getNodeRef());
                nodeService.moveNode(document.getNodeRef(), nodeRef, ContentModel.ASSOC_CONTAINS, parent.getQName());
                return null;
            }
        });
    }
	public PropertyDefinition getProperty(QName name) {
		return dictionaryService.getProperty(name);
	}
	
	public PropertyDefinition getProperty(String name) {
		QName qName = QName.createQName(name, namespaceService);
		return dictionaryService.getProperty(qName);
	}
	
	public AssociationDefinition getAssociation(QName name) {
		return dictionaryService.getAssociation(name);
	}
	
	public AssociationDefinition getAssociation(String name) {
		QName qName = QName.createQName(name, namespaceService);
		return dictionaryService.getAssociation(qName);
	}
	
	public String toPrefixString(PropertyDefinition prop){
		return prop.getDataType().getName().toPrefixString(namespaceService);
	}
	
	public String toPrefixString(AssociationDefinition assoc){
		return assoc.getTargetClass().getName().toPrefixString(namespaceService);
	}
	
	public QName createQName(String qname) {
		return QName.createQName(qname, namespaceService);
	}	
	
	public Set<QName> getSubTypes(QName type, boolean bln) {
		return (Set<QName>) dictionaryService.getSubTypes(type, bln);
	}
	
	public Set<QName> getSubTypes(String type, boolean bln) {
		QName qNameType = QName.createQName(type, namespaceService);
		return (Set<QName>) dictionaryService.getSubTypes(qNameType, bln);
	}
	
	public TypeDefinition getType(QName type) {
		return dictionaryService.getType(type);
	}
	
	public TypeDefinition getType(String type) {
		QName qName = QName.createQName(type, namespaceService);
		return dictionaryService.getType(qName);
	}
	
	public String getListConstraintDisplayValue(ListOfValuesConstraint constraint, String value) {
		return constraint.getDisplayLabel(value, dictionaryService);
	}
}
