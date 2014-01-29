package ru.it.lecm.incoming.external;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.List;

/**
 * User: pmelnikov
 * Date: 28.01.14
 * Time: 10:04
 */
public class EmailRepositoryExecutor extends ActionExecuterAbstractBase {

    private EmailRepositoryReceiver repositoryReceiver;

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {
        repositoryReceiver.receive(nodeRef);
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {

    }

    public void setRepositoryReceiver(EmailRepositoryReceiver repositoryReceiver) {
        this.repositoryReceiver = repositoryReceiver;
    }
}
