package ru.it.lecm.actions.bean;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 19.02.14
 * Time: 16:03
 */
public class GroupActionsServiceImpl extends BaseBean implements GroupActionsService {

    private NodeRef homeRef = null;
    private DictionaryService dictionaryService;
    private StateMachineServiceBean stateMachineService;
    private List<String> aspects;
    private NamespaceService namespaceService;

    /**
     * Метод инициализвции сервиса
     * Создает рабочую директорию - если она еще не создана.
     * Записыывает в свойства сервиса nodeRef директории с бизнес-журналами
     */
    public void init() {
        homeRef = getFolder(GA_ROOT_ID);
    }

    @Override
    public NodeRef getHomeRef() {
        return homeRef;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return homeRef;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public void setAspects(List<String> aspects) {
        this.aspects = aspects;
    }

    public List<String> getAspects() {
        return aspects;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public List<NodeRef> getActiveGroupActions(List<NodeRef> forItems) {
        if (forItems.size() == 0) return new ArrayList<NodeRef>();
        List<ChildAssociationRef> children = nodeService.getChildAssocs(getHomeRef());
        List<NodeRef> actions = new ArrayList<NodeRef>();
        for (ChildAssociationRef child : children) {
            if (Boolean.TRUE.equals(nodeService.getProperty(child.getChildRef(), GroupActionsService.PROP_IS_GROUP))) {
                actions.add(child.getChildRef());
            }
        }
        actions = filterByType(actions, forItems);
        return actions;
    }

    private List<NodeRef> filterByType(List<NodeRef> actions, List<NodeRef> items) {
        List<NodeRef> result = new ArrayList<NodeRef>();
        for (NodeRef action : actions) {
            String type = nodeService.getProperty(action, GroupActionsService.PROP_TYPE).toString();
            QName typeQName = QName.createQName(type, namespaceService);
            boolean isRight = true;
            for (NodeRef item : items) {
                TypeDefinition typeDef = dictionaryService.getType(typeQName);
                if (typeDef != null) {
                    QName itemType = nodeService.getType(item);
                    if (!itemType.equals(typeQName) || !dictionaryService.isSubClass(itemType, typeQName)) {
                        isRight = false;
                        break;
                    }
                } else {
                    if (!nodeService.hasAspect(item, typeQName)) {
                        isRight = false;
                        break;
                    }
                }
            }
            if (isRight) {
                result.add(action);
            }
        }
        return result;
    }

    private List<NodeRef> filterByStatuses(List<NodeRef> actions, List<NodeRef> items) {
        List<NodeRef> result = new ArrayList<NodeRef>();
        return result;
    }

    private List<NodeRef> filterByExpression(List<NodeRef> actions, List<NodeRef> items) {
        List<NodeRef> result = new ArrayList<NodeRef>();
        return result;
    }

}
