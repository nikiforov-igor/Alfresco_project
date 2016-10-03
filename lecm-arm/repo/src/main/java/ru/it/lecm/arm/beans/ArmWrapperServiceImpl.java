package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.arm.beans.childRules.ArmBaseChildRule;
import ru.it.lecm.arm.beans.childRules.ArmStatusesChildRule;
import ru.it.lecm.arm.beans.node.ArmNode;
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
                Map<QName, Serializable> properties =  service.getCachedProperties(accord);
                Boolean isForSecretaries = (Boolean) properties.get(ArmService.PROP_IS_FOR_SECRETARIES);
                if (isForSecretaries == null || !isForSecretaries) {
                    result.add(wrapArmNodeAsObject(accord, true, onlyMeta));
                } else {
                    List<NodeRef> bossAccords = service.getArmRunAsBossAccordions(accord);
                    for (NodeRef bossAccord : bossAccords) {
                        result.add(wrapArmNodeAsObject(bossAccord, true, onlyMeta));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef) {
        return getChildNodes(node, parentRef, false);
    }

    @Override
    public List<ArmNode> getChildNodes(NodeRef node, NodeRef parentRef, boolean onlyMeta) {
        List<ArmNode> result = new ArrayList<>();

        ArmNode parent = wrapArmNodeAsObject(parentRef, false, onlyMeta);

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            parentFromArm = parent.getNodeRef();
        }

        if (parentFromArm != null) {
            List<NodeRef> staticChilds = service.getChildNodes(parentFromArm); // узлы АРМа
            for (NodeRef staticChild : staticChilds) {
                ArmNode stNode = wrapArmNodeAsObject(staticChild, false, onlyMeta);
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
                    result.add(wrapAnyNodeAsObject(dicChild, parent, onlyMeta));
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

    private boolean hasChildNodes(NodeRef node, NodeRef parentRef) {

        // 1. Дочерние статические элементы из настроек ARM
        NodeRef parentFromArm;
        if (isArmElement(node)) {
            parentFromArm = node;
        } else { // узел справочника или какой-нить другой объект, то реальный родитель - берется последний узел из ARM
            parentFromArm = parentRef;
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
            List<NodeRef> staticChilds = service.getChildNodes(parentFromArm); // узлы АРМа
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
        }


        return false;
    }

    public boolean hasChildNodes(ArmNode node) {
        return hasChildNodes(node.getNodeRef(), node.getArmNodeRef()) ||
                (node.getNodeQuery() != null && !node.getNodeQuery().build(this, node).isEmpty());
    }

    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion) {
        return wrapArmNodeAsObject(nodeRef, isAccordion, false);
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef) {
        return wrapArmNodeAsObject(nodeRef, false, false);
    }

    @Override
    public ArmNode wrapArmNodeAsObject(NodeRef nodeRef, boolean isAccordion, boolean onlyMeta) {
        ArmNode node = new ArmNode();

        Map<QName, Serializable> properties;

        if (!isRunAsAccordion(nodeRef)) {
            properties = service.getCachedProperties(nodeRef);
            node.setTitle((String) properties.get(ContentModel.PROP_NAME));
        } else {
            String[] refs = nodeRef.getId().split("_");
            NodeRef employeeRunAs = new NodeRef(nodeRef.getStoreRef(), refs[1]);
            NodeRef armAccordionRunAs = new NodeRef(nodeRef.getStoreRef(), refs[0]);

            properties = service.getCachedProperties(armAccordionRunAs);

            String armTitle = (String) properties.get(ContentModel.PROP_NAME);
            node.setRunAsEmployee(employeeRunAs);

            node.setTitle(armTitle + " " + service.getCachedProperties(employeeRunAs).get(OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME));

            nodeRef = armAccordionRunAs;
        }

        node.setNodeRef(nodeRef);
        node.setNodeType(service.getCachedType(nodeRef).toPrefixString(namespaceService));
        if (!isAccordion) {
            node.setArmNodeRef(service.getCachedParent(nodeRef)); // для узла Арм - данное поле дублируется. как так узел Арм - реален
        } else {
            node.setArmNodeRef(nodeRef);
        }

        node.setNodeQuery(!isAccordion ? service.getNodeChildRule(nodeRef) : null);
        node.setTypes(getNodeTypes(nodeRef));

        String searchQuery = (String) properties.get(ArmService.PROP_SEARCH_QUERY);
        if (searchQuery != null) {
            node.setSearchQuery(searchQuery.replaceAll("\\n", " ").replaceAll("\\r", " "));
        }
        node.setHtmlUrl((String) properties.get(ArmService.PROP_HTML_URL));
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
        return wrapAnyNodeAsObject(nodeRef, parentNode, false);
    }

    @Override
    public ArmNode wrapAnyNodeAsObject(NodeRef nodeRef, ArmNode parentNode, boolean onlyMeta) {
        ArmNode node = new ArmNode();
        node.setTitle(substitudeService.getObjectDescription(nodeRef));
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
                String parentQuery = searchQuery.toString().replaceAll("\\n", " ").replaceAll("\\r", " ");
                if (!parentQuery.isEmpty()) {
                    sb.append(sb.length() > 0 ? " AND " : "");
                    boolean useBrackets = true;

                    if (parentQuery.startsWith("NOT")) {
                        Matcher m = ArmService.MULTIPLE_NOT_QUERY.matcher(parentQuery.toUpperCase());
                        if (!m.find()) {
                            useBrackets = false;
                        }
                    }

                    sb.append(useBrackets ? "(" : "");
                    sb.append(parentQuery);
                    sb.append(useBrackets ? ")" : "");
                }
            }
        }

        node.setSearchQuery(sb.toString());

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
                String parentQuery = searchQuery.toString().replaceAll("\\n", " ").replaceAll("\\r", " ");
                if (!parentQuery.isEmpty()) {
                    sb.append(sb.length() > 0 ? " AND " : "");
                    boolean useBrackets = true;

                    if (parentQuery.startsWith("NOT")) {
                        Matcher m = ArmService.MULTIPLE_NOT_QUERY.matcher(parentQuery.toUpperCase());
                        if (!m.find()) {
                            useBrackets = false;
                        }
                    }

                    sb.append(useBrackets ? "(" : "");
                    sb.append(parentQuery);
                    sb.append(useBrackets ? ")" : "");
                }
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
            StringBuilder stringBuilder = new StringBuilder("(\"" + node.toString() +"\"");

            for (NodeRef child : dictionaryBean.getAllChildren(node)) {
                stringBuilder.append(" \"").append(child.toString()).append('\"');
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
        List<AssociationRef> queryAssoc = nodeService.getTargetAssocs(nodeRef, ArmService.ASSOC_NODE_CHILD_RULE);
        if (queryAssoc != null && queryAssoc.size() > 0) {
            NodeRef query = queryAssoc.get(0).getTargetRef();
            QName queryType = service.getCachedType(query);
            Map<QName, Serializable> props = service.getCachedProperties(query);

            if (ArmService.TYPE_STATUSES_CHILD_RULE.equals(queryType)) {
                ArmStatusesChildRule node = new ArmStatusesChildRule();
                node.setRule((String) props.get(ArmService.PROP_STATUSES_RULE));
                String selectedStatuses = (String) props.get(ArmService.PROP_SELECTED_STATUSES);
                if (selectedStatuses != null) {
                    List<String> selectedStatusesList = new ArrayList<>();
                    for (String str: selectedStatuses.split(",")) {
                        String status = str.trim();
                        if (status.length() > 0) {
                            selectedStatusesList.add(status);
                        }
                    }

                    node.setSelectedStatuses(selectedStatusesList);
                }

                return node.getQuery();
            }
        }
        return (String) service.getCachedProperties(nodeRef).get(ArmService.PROP_SEARCH_QUERY);
    }

    public String formatQuery(String templateQuery, String value) {
        if (templateQuery.contains(ArmWrapperService.VALUE)) {
            return templateQuery.replaceAll(ArmWrapperService.VALUE, value);
        }
        return templateQuery;
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
}
