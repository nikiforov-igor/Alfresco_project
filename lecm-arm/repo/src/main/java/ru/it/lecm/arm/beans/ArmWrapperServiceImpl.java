package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.base.beans.SearchCounter;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;

/**
 * User: dbashmakov
 * Date: 05.02.14
 * Time: 11:00
 */
public class ArmWrapperServiceImpl implements ArmWrapperService {

    private NodeService nodeService;
    private ArmServiceImpl service;
    private SubstitudeBean substitudeService;
	private NamespaceService namespaceService;
    private DictionaryBean dictionaryBean;
    private SearchCounter searchCounter;
    private OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setService(ArmServiceImpl service) {
        this.service = service;
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setSearchCounter(SearchCounter searchCounter) {
        this.searchCounter = searchCounter;
    }

    public List<ArmNode> getAccordionsByArmCode(String armCode) {
        return getAccordionsByArmCode(armCode, false);
    }

    @Override
    public List<ArmNode> getAccordionsByArmCode(String armCode, boolean onlyMeta) {
        List<ArmNode> result = new ArrayList<>();

        NodeRef arm = service.getArmByCode(armCode);
        if (arm != null) {
            List<NodeRef> accords = service.getArmAccordions(arm);
            for (NodeRef accord : accords) {
                result.add(wrapArmNodeAsObject(accord, true, onlyMeta));
            }
        }
        return result;
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode) {
        return getChildNodes(armNode, parentNode, false);
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef, NodeRef currentSection, NodeRef runAsBoss) {
        Map<QName, Serializable> properties = service.getCachedProperties(node);
        boolean isForDelegation = Boolean.TRUE.equals(properties.get(ArmService.PROP_IS_FOR_SECRETARIES));
        if (isForDelegation && isAccordion(node)) {
            List<ArmNode> result = new ArrayList<>();
            Set<Pair<NodeRef,NodeRef>> bossArmNodePairs = service.getArmRunAsBossNodes(node);
            for (Pair<NodeRef,NodeRef> bossNodePair : bossArmNodePairs) {
                result.add(wrapArmNodeAsObject(bossNodePair.getFirst(), false, false, currentSection, bossNodePair.getSecond()));
            }
            return result;
        }
        return getChildNodes(node, parentRef, false, currentSection, runAsBoss);
    }

    public boolean isArmDelegationRootNode(NodeRef node, NodeRef currentSection) {
       return service.isArmDelegationRootNode(node, currentSection);
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef armNode, NodeRef parentNode, boolean onlyMeta) {
        return getChildNodes(armNode, parentNode, onlyMeta, null, null);
    }

    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef, boolean onlyMeta, NodeRef currentSection, NodeRef runAsEmployee) {
        List<ArmNode> result = new ArrayList<>();

        ArmNode parent = wrapArmNodeAsObject(parentRef, false, onlyMeta);

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            parentFromArm = parent.getNodeRef();
        }
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        String runAsName = null;
        if (runAsEmployee != null && nodeService.exists(runAsEmployee) && !currentEmployee.equals(runAsEmployee)) {
            runAsName = orgstructureBean.getEmployeeLogin(runAsEmployee);
        }
        if (parentFromArm != null) {
            List<NodeRef> staticChilds = new ArrayList<>();
            if (runAsName != null) {
                final String fullyAuthentificatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
                try {
                    AuthenticationUtil.setFullyAuthenticatedUser(runAsName);
                    staticChilds = service.getChildNodes(parentFromArm, runAsEmployee);
                } finally {
                    AuthenticationUtil.setFullyAuthenticatedUser(fullyAuthentificatedUser);
                }
            } else {
                staticChilds = service.getChildNodes(parentFromArm);
            }

            // узлы АРМа
            for (NodeRef staticChild : staticChilds) {
                ArmNode stNode = wrapArmNodeAsObject(staticChild, false, onlyMeta, currentSection, runAsEmployee);
                if (stNode.getNodeQuery() != null) {
                    List<ArmNode> queriedChilds = stNode.getNodeQuery().build(this, stNode);
                    for (ArmNode queriedChild : queriedChilds) {
                        result.add(queriedChild);
                    }
                } else {
                    result.add(stNode);
                }
            }
        }

        //2. Добавить реальных дочерних узлов для иерархического справочника!
        // в остальных случаях у нас не может быть дочерних элементов
        if (node != null && !isArmElement(node)){
            List<NodeRef> children = parent.getNodeQuery().getChildren(node);
            if (children != null) {
                for (NodeRef dicChild : children) {
                    result.add(wrapAnyNodeAsObject(dicChild, parent, parent.getNodeQuery().getSubstituteString(), onlyMeta, null, currentSection));
                }

                Collections.sort(result, new Comparator<ArmNode>() {
                    @Override
                    public int compare(ArmNode o1, ArmNode o2) {
                        return o1.getTitle().toUpperCase().compareTo(o2.getTitle().toUpperCase());
                    }
                });
            }
        }
        return result;
    }

    @Override
    public boolean hasChildNodes(ArmNode node) {
            return hasChildNodes(node.getNodeRef(), node.getArmNodeRef(), null) ||
                    (node.getNodeQuery() != null && !node.getNodeQuery().build(this, node).isEmpty());
    }

    private boolean hasChildNodes(NodeRef node, NodeRef parentRef, String runAsEmployee) {

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            parentFromArm = parentRef;
        }
        NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
        String runAsName = null;
        NodeRef runAsEmployeeRef = null;
        if (runAsEmployee != null && NodeRef.isNodeRef(runAsEmployee)) {
            runAsEmployeeRef = new NodeRef(runAsEmployee);
            if (!currentEmployee.equals(runAsEmployeeRef)) {
                runAsName = orgstructureBean.getEmployeeLogin(runAsEmployeeRef);
            }
        }

        ArmBaseChildRule nodeQuery = service.getNodeChildRule(parentRef);

        //2. Добавить реальных дочерних узлов для иерархического справочника!
        // в остальных случаях у нас не может быть дочерних элементов
        if (node != null && !isArmElement(node)){
            List<NodeRef> childs = nodeQuery.getChildren(node);
            if (childs != null && !childs.isEmpty()) {
                return true;
            }
        }

        if (parentFromArm != null) {
            List<NodeRef> staticChilds = new ArrayList<>();
            if (runAsName != null) {
                final String fullyAuthentificatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
                try {
                    AuthenticationUtil.setFullyAuthenticatedUser(runAsName);
                    staticChilds = service.getChildNodes(parentFromArm, runAsEmployeeRef); // узлы АРМа
                } finally {
                    AuthenticationUtil.setFullyAuthenticatedUser(fullyAuthentificatedUser);
                }
            } else {
                staticChilds = service.getChildNodes(parentFromArm);
            }
                for (NodeRef staticChild : staticChilds) {
                    if (service.getNodeChildRule(staticChild) != null) {
                        ArmNode stNode = wrapArmNodeAsObject(staticChild);
                        List<ArmNode> queriedChilds = stNode.getNodeQuery().build(this, stNode);
                        if (!queriedChilds.isEmpty()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                return false;
        }
        return false;
    }
    @Override
    public boolean hasChildNodes(ArmNode node, String runAs) {
        return hasChildNodes(node.getNodeRef(), node.getArmNodeRef(), runAs) ||
                (node.getNodeQuery() != null && !node.getNodeQuery().build(this, node).isEmpty());
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion) {
        return wrapArmNodeAsObject(nodeRef, isAccordion, false);
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef) {
        return wrapArmNodeAsObject(nodeRef, false, false, null);
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion, boolean onlyMeta) {
        return wrapArmNodeAsObject(nodeRef, isAccordion, onlyMeta, null);
    }


    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion, boolean onlyMeta, NodeRef currentSection) {
        return wrapArmNodeAsObject(nodeRef, isAccordion, onlyMeta, currentSection, null);
    }

    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion, boolean onlyMeta, NodeRef currentSection, NodeRef employeeRunAs) {
        ArmNode node = new ArmNode();
        Map<QName, Serializable> properties = service.getCachedProperties(nodeRef);
        node.setNodeRef(nodeRef);
        boolean isForDelegation = false;
        Map<QName, Serializable> accordionProps = null;
        if (currentSection != null) {
            accordionProps = service.getCachedProperties(currentSection);
            isForDelegation = Boolean.TRUE.equals(accordionProps.get(ArmService.PROP_IS_FOR_SECRETARIES));
            if (!isAccordion && accordionProps.get(ArmService.PROP_ROOT_NODE_HTML_URL) != null && accordionProps.get(ArmService.PROP_ROOT_NODE_HTML_URL).toString().length() > 0 && isArmDelegationRootNode(nodeRef, currentSection)) {
                node.setNodeType(ArmService.TYPE_ARM_HTML_NODE.toPrefixString(namespaceService));
                node.setHtmlUrl((String) accordionProps.get(ArmService.PROP_ROOT_NODE_HTML_URL));
                if (employeeRunAs != null) {
                    node.setTitle((String) service.getCachedProperties(employeeRunAs).get(OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME));
                }
            } else {
                node.setNodeType(service.getCachedType(nodeRef).toPrefixString(namespaceService));
                node.setHtmlUrl((String) properties.get(ArmService.PROP_HTML_URL));
                node.setTitle((String) properties.get(ContentModel.PROP_NAME));
            }
        } else {
            node.setNodeType(service.getCachedType(nodeRef).toPrefixString(namespaceService));
            node.setHtmlUrl((String) properties.get(ArmService.PROP_HTML_URL));
            node.setTitle((String) properties.get(ContentModel.PROP_NAME));
        }
        if (!isAccordion) {
            node.setArmNodeRef(service.getCachedParent(nodeRef)); // для узла Арм - данное поле дублируется. как так узел Арм - реален
        } else {
            node.setArmNodeRef(nodeRef);
        }
        if (employeeRunAs != null) {
            node.setRunAsEmployee(employeeRunAs);
        }

        node.setNodeQuery(!isAccordion ? service.getNodeChildRule(nodeRef) : null);
        node.setTypes(getNodeTypes(nodeRef));

        String searchQuery = (String) properties.get(ArmService.PROP_SEARCH_QUERY);
        if (searchQuery != null) {
            if (isForDelegation) {
                searchQuery = searchQuery.replaceAll("#current-user", "#boss-ref");
            }
            node.setSearchQuery(searchQuery.replaceAll("\\n", " ").replaceAll("\\r", " "));
        }
        node.setReportCodes((String) properties.get(ArmService.PROP_REPORT_CODES));
        node.setSearchType((String) properties.get(ArmService.PROP_SEARCH_TYPE));

        if (!onlyMeta) {
            node.setCounter(service.getNodeCounter(nodeRef));
            node.setColumns(getNodeColumns(nodeRef));
            node.setAvaiableFilters(getNodeFilters(nodeRef));
            node.setCreateTypes(getNodeCreateTypes(nodeRef));
        }
        return node;
    }

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef nodeRef, ArmNode parentNode) {
        return wrapAnyNodeAsObject(nodeRef, parentNode, null);
    }

	@Override
	public ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, String substituteString) {
		return wrapAnyNodeAsObject(node, parent, substituteString, false);
	}

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, String substituteString, boolean onlyMeta) {
        return wrapAnyNodeAsObject(node, parent, substituteString, onlyMeta, null);
    }

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef node, ArmNode parent, String substituteString, boolean onlyMeta, String searchTerm) {
        return wrapAnyNodeAsObject(node, parent, substituteString, onlyMeta, searchTerm, null);
    }

    public ArmNode wrapAnyNodeAsObject(NodeRef nodeRef, ArmNode parentNode, String substituteString, boolean onlyMeta, String searchTerm, NodeRef currentSection) {
        ArmNode node = new ArmNode();
		if (null == substituteString || substituteString.isEmpty()) {
            node.setTitle(substitudeService.getObjectDescription(nodeRef));
		} else {
			node.setTitle(substitudeService.formatNodeTitle(nodeRef, substituteString));
		}
        node.setNodeRef(nodeRef);
        node.setNodeType(service.getCachedType(nodeRef).toPrefixString(namespaceService));
        node.setArmNodeRef(parentNode.getNodeRef());
        node.setTypes(parentNode.getTypes());

        StringBuilder sb = new StringBuilder();
        if (parentNode.getSearchQuery() != null) {
            sb.append(formatQuery(parentNode.getSearchQuery(), nodeRef));
        }

        NodeRef realParent = parentNode.getArmNodeRef();
        if (realParent != null && !realParent.equals(nodeRef)) {
            Object searchQuery = service.getCachedProperties(realParent).get(ArmService.PROP_SEARCH_QUERY);
            if (searchQuery != null) {
                insertQueryToBuffer(sb, searchQuery.toString());
            }
        }
        String searchQueryString = sb.toString();
        if (currentSection != null) {
            Map<QName,Serializable> accordionProps = service.getCachedProperties(currentSection);
            boolean isForDelegation = Boolean.TRUE.equals(accordionProps.get(ArmService.PROP_IS_FOR_SECRETARIES));
            if (isForDelegation) {
                searchQueryString = searchQueryString.replaceAll("#current-user", "#boss-ref");
            }
        }

        node.setSearchQuery(searchQueryString);

        node.setHtmlUrl(parentNode.getHtmlUrl());
        node.setReportCodes(parentNode.getReportCodes());
        node.setSearchType(parentNode.getSearchType());

        if (!onlyMeta) {
            node.setColumns(parentNode.getColumns());
            node.setAvaiableFilters(parentNode.getAvaiableFilters());
            node.setCounter(parentNode.getCounter());
            node.setCreateTypes(parentNode.getCreateTypes());

        }
        return node;
    }

    @Override
    public ArmNode wrapStatusAsObject(String status, ArmNode parentNode) {
        return wrapStatusAsObject(status, parentNode, false);
    }

    @Override
    public ArmNode wrapStatusAsObject(String status, ArmNode parentNode, boolean onlyMeta) {
        ArmNode node = new ArmNode();
        node.setTitle(status);
        node.setNodeRef(null);
        node.setArmNodeRef(parentNode.getNodeRef());
        node.setNodeType(ArmService.TYPE_ARM_NODE.toPrefixString(namespaceService));

        StringBuilder sb = new StringBuilder();
        if (parentNode.getSearchQuery() != null) {
            sb.append("@lecm\\-statemachine\\:status:'").append(status).append("'");
        }

        NodeRef realParent = parentNode.getArmNodeRef();
        if (realParent != null) {
            Object searchQuery = service.getCachedProperties(realParent).get(ArmService.PROP_SEARCH_QUERY);
            if (searchQuery != null) {
                insertQueryToBuffer(sb, searchQuery.toString());
            }
        }

        node.setSearchQuery(sb.toString());

        node.setTypes(parentNode.getTypes());

        if (!onlyMeta) {
            node.setColumns(parentNode.getColumns());
            node.setAvaiableFilters(parentNode.getAvaiableFilters());
            node.setCounter(parentNode.getCounter());
            node.setCreateTypes(parentNode.getCreateTypes());
        }
        return node;
    }

    public String formatQuery(String templateQuery, NodeRef node) {
        String formatedQuery = substitudeService.formatNodeTitle(node, templateQuery);

        if (formatedQuery.contains(VALUE_WITH_SUBTREE_REFS)) {
            StringBuilder stringBuilder = new StringBuilder("(\"*" + node.toString() +"*\"");

            for (NodeRef child : dictionaryBean.getAllChildren(node)) {
                stringBuilder.append(" \"*").append(child.toString()).append("*\"");
            }

            stringBuilder.append(')');
            formatedQuery = formatedQuery.replaceAll(VALUE_WITH_SUBTREE_REFS, stringBuilder.toString());

        }

        if (formatedQuery.contains(VALUE_REF)) {
            formatedQuery = formatedQuery.replaceAll(VALUE_REF, node.toString());
        }
        if (formatedQuery.contains(VALUE_TEXT)) {
            formatedQuery = formatedQuery.replaceAll(VALUE_TEXT, substitudeService.getObjectDescription(node));
        }
        return formatedQuery;
    }

    public String getNodeSearchQuery(NodeRef nodeRef) {
        return service.getNodeSearchQuery(nodeRef);
    }

    public String formatQuery(String templateQuery, String value) {
        if (templateQuery.contains(ArmWrapperService.VALUE)) {
            return templateQuery.replaceAll(ArmWrapperService.VALUE, value);
        }
        return templateQuery;
    }

    @Override
    public String getFullQuery(ArmNode armNode, boolean includeTypes, boolean includeParentQuery) {
        StringBuilder builder = new StringBuilder();
        if (includeTypes) {
            List<String> types = armNode.getTypes();
            StringBuilder typesBuilder = new StringBuilder();
            for (String type : types) {
                typesBuilder.append("TYPE:\"").append(type).append("\"").append(" OR ");
            }
            if (typesBuilder.length() > 0) {
                typesBuilder.delete(typesBuilder.length() - 4, typesBuilder.length());
            }

            if (typesBuilder.length() > 0) {
                includeTypes = false; /*Нам не нужны родительские типы, если получили конкретные*/
                builder.append("(").append(typesBuilder.toString()).append(")");
            }
        }

        insertQueryToBuffer(builder, armNode.getSearchQuery());

        if (includeParentQuery) {
            NodeRef parentNode = service.getCachedParent(armNode.getNodeRef());
            if (parentNode != null) {
                QName parentType = nodeService.getType(parentNode);
                if (parentType.equals(ArmService.TYPE_ARM_NODE)
                        || parentType.equals(ArmService.TYPE_ARM_ACCORDION)) {
                    String parentQuery = getFullQuery(wrapArmNodeAsObject(parentNode, service.isArmAccordion(parentNode)), includeTypes, true);
                    insertQueryToBuffer(builder, parentQuery);
                }
            }
        }
        return builder.toString();
    }

    public long getObjectsCount(ArmNode node) {
        String query = getFullQuery(node, true, true);
        NodeRef employeeRef = node.getRunAsEmployee();
        if (employeeRef != null) {
            query = query.replaceAll("#current-user", "#boss-ref")
                    .replaceAll("#boss-ref", employeeRef.toString());
        }
        return execSearchQuery(query);
    }

    private boolean isArmElement(NodeRef node) {
        return service.isArmElement(node);
    }

    private List<ArmColumn> getNodeColumns(NodeRef node) {
        List<ArmColumn> nodeColumns = service.getNodeColumns(node);
        if (nodeColumns.isEmpty()) {
            NodeRef parent = service.getCachedParent(node);
            if (isArmElement(parent)) {
                return getNodeColumns(parent);
            }
        }
        return nodeColumns;
    }

    private List<ArmFilter> getNodeFilters(NodeRef node) {
        List<ArmFilter> nodeFilters = service.getNodeFilters(node);
        if (nodeFilters.isEmpty()) {
            NodeRef parent = service.getCachedParent(node);
            if (isArmElement(parent)) {
                return getNodeFilters(parent);
            }
        }
        return nodeFilters;
    }

    private List<String> getNodeTypes(NodeRef node) {
        List<String> nodeTypes = service.getNodeTypes(node);
        if (nodeTypes.isEmpty()) {
            NodeRef parent = service.getCachedParent(node);
            if (isArmElement(parent)) {
                return getNodeTypes(parent);
            }
        }
        return nodeTypes;
    }

	private List<String> getNodeCreateTypes(NodeRef node) {
        List<String> results = new ArrayList<>();
        results.addAll(service.getNodeTypes(node));

        NodeRef parent = service.getCachedParent(node);
        if (isArmElement(parent)) {
	        List<String> parentTypes = getNodeCreateTypes(parent);
	        if (parentTypes != null) {
		        for (String type: parentTypes) {
			        if (!results.contains(type)) {
				        results.add(type);
			        }
		        }
	        }
        }

        return results;
    }

    public boolean isRunAsAccordion(NodeRef node) {
        return service.isRunAsArmAccordion(node);
}
    public boolean isAccordion(NodeRef node) {
        return service.isArmAccordion(node);
    }


    private void insertQueryToBuffer(StringBuilder builder, String queryToAppend) {
        if (queryToAppend != null && !queryToAppend.isEmpty()) {
            queryToAppend = queryToAppend.trim().replaceAll("\\n", " ").replaceAll("\\r", " ");
            if (builder.length() > 0) {
                builder.append(" AND ");
            }
            boolean useBrackets = true;
            if (queryToAppend.startsWith("NOT")) {
                Matcher m = ArmService.MULTIPLE_NOT_QUERY.matcher(queryToAppend.toUpperCase());
                if (!m.find()) {
                    useBrackets = false;
                }
            }
            builder.append(useBrackets ? "(" : "");
            builder.append(queryToAppend);
            builder.append(useBrackets ? ")" : "");
        }
    }

    private long execSearchQuery(String queryToExecute) {
        //вызываем counter с дефолтными настройками (как сейчас в АРМе)
        return searchCounter.query(queryToExecute, true, false);
    }
}
