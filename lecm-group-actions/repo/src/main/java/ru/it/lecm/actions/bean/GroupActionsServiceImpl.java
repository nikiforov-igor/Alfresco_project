package ru.it.lecm.actions.bean;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * User: pmelnikov
 * Date: 19.02.14
 * Time: 16:03
 */
public class GroupActionsServiceImpl extends BaseBean implements GroupActionsService {

	final protected Logger logger = LoggerFactory.getLogger(GroupActionsServiceImpl.class);

    private DictionaryService dictionaryService;
//    private StateMachineServiceBean stateMachineService;
    private List<String> aspects;
    private NamespaceService namespaceService;
    private DocumentService documentService;

    /**
     * Метод инициализвции сервиса
     * Создает рабочую директорию - если она еще не создана.
     * Записыывает в свойства сервиса nodeRef директории с бизнес-журналами
     */
    public void init() {
    }

    @Override
    public NodeRef getHomeRef() {
        return getServiceRootFolder();
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(GA_ROOT_ID);
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

//    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
//        this.stateMachineService = stateMachineService;
//    }

    public void setAspects(List<String> aspects) {
        this.aspects = aspects;
    }

    @Override
	public List<String> getAspects() {
        return aspects;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    @Override
    public List<NodeRef> getActiveGroupActions(List<NodeRef> forItems, boolean group) {
        return getActiveActions(forItems, group);
    }

    public List<NodeRef> getActiveActions(NodeRef item) {
        List<NodeRef> forItems = new ArrayList<NodeRef>();
        forItems.add(item);
        return getActiveActions(forItems, false);
    }

    private List<NodeRef> getActiveActions(List<NodeRef> forItems, boolean group) {
        if (forItems.size() == 0) return new ArrayList<NodeRef>();
        List<ChildAssociationRef> children = nodeService.getChildAssocs(getHomeRef());
        List<NodeRef> actions = new ArrayList<NodeRef>();
        for (ChildAssociationRef child : children) {
            QName type = nodeService.getType(child.getChildRef());
            if (dictionaryService.isSubClass(type, TYPE_GROUP_ACTION)) {
                if (group) {
                    if (Boolean.TRUE.equals(nodeService.getProperty(child.getChildRef(), GroupActionsService.PROP_IS_GROUP))) {
                        actions.add(child.getChildRef());
                    }
                } else {
                    if (!Boolean.TRUE.equals(nodeService.getProperty(child.getChildRef(), GroupActionsService.PROP_IS_GROUP))) {
                        actions.add(child.getChildRef());
                    }
                }
            }
        }
        actions = filterByType(actions, forItems);
        actions = filterByStatuses(actions, forItems);
        actions = filterByExpression(actions, forItems);
        Collections.sort(actions, new Comparator<NodeRef>() {
            @Override
            public int compare(NodeRef o1, NodeRef o2) {
                Long order1 = (Long) nodeService.getProperty(o1, GroupActionsService.PROP_ORDER);
                Long order2 = (Long) nodeService.getProperty(o2, GroupActionsService.PROP_ORDER);
                return order1.compareTo(order2);
            }
        });
        return actions;
    }

    private List<NodeRef> filterByType(List<NodeRef> actions, List<NodeRef> items) {
        List<NodeRef> result = new ArrayList<>();
        for (NodeRef action : actions) {
            Serializable property = nodeService.getProperty(action, GroupActionsService.PROP_TYPE);
            boolean isRight = false;
			List<String> typesStr = (List<String>) property;
			if (typesStr != null && !typesStr.isEmpty()) {
				Map<QName, TypeDefinition> typeToTypeDef = new HashMap<>();
				for (String typeStr : typesStr) {
					if (!typeStr.isEmpty()) {
						QName typeQName = QName.createQName(typeStr, namespaceService);
						typeToTypeDef.put(typeQName, dictionaryService.getType(typeQName));
					}
				}
				for (NodeRef nodeItem : items) {
					for (Entry<QName, TypeDefinition> typeItem : typeToTypeDef.entrySet()) {
						QName typeQName = typeItem.getKey();
						TypeDefinition typeDef = dictionaryService.getType(typeQName);
                        if (typeDef != null) {
                            QName itemType = nodeService.getType(nodeItem);
                            if (itemType.equals(typeQName) || dictionaryService.isSubClass(itemType, typeQName)) {
                                isRight = true;
                                break;
                            }
                        } else {
                            if (nodeService.hasAspect(nodeItem, typeQName)) {
                                isRight = true;
                                break;
                            }
                        }
                    }
                }
            } else {
				isRight = true;
			}
            if (isRight) {
                result.add(action);
            }
        }
        return result;
    }

    private List<NodeRef> filterByStatuses(List<NodeRef> actions, List<NodeRef> items) {
        List<NodeRef> result = new ArrayList<NodeRef>();
        for (NodeRef action : actions) {
            boolean include = true;
            String statusesField = nodeService.getProperty(action, GroupActionsService.PROP_STATUSES).toString();
            if (!"".equals(statusesField)) {
                String[] splitStatuses = statusesField.split(";");
                HashSet<String> statuses = new HashSet<String>();
                for (String status : splitStatuses) {
                    statuses.add(status.trim());
                }
                for (NodeRef item : items) {
                    String status = nodeService.getProperty(item, StatemachineModel.PROP_STATUS).toString();
                    if (!statuses.contains(status)) {
                        include = false;
                        break;
                    }
                }
            }
            if (include) {
                result.add(action);
            }
        }
        return result;
    }

    private List<NodeRef> filterByExpression(List<NodeRef> actions, List<NodeRef> items) {
        List<NodeRef> result = new ArrayList<NodeRef>();
        for (NodeRef action : actions) {
            boolean include = true;
            String expression = nodeService.getProperty(action, GroupActionsService.PROP_EXPRESSION).toString();
            if (!"".equals(expression)) {
                for (NodeRef item : items) {
                    if (!documentService.execExpression(item, expression)) {
                        include = false;
                        break;
                    }
                }
            }
            if (include) {
                result.add(action);
            }
        }
        return result;
    }

}
