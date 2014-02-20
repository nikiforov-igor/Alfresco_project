package ru.it.lecm.actions.bean;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;

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
}
