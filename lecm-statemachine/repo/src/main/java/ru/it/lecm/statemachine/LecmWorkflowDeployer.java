package ru.it.lecm.statemachine;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by dkuchurkin on 12.08.2016.
 */
public class LecmWorkflowDeployer extends AbstractLifecycleBean{

    private static final String WORKFLOW_FOLDER = "workflowStore";

    private final static Logger logger = LoggerFactory.getLogger(LecmWorkflowDeployer.class);

    private NodeService nodeService;
    private Repository repositoryHelper;
    private TransactionService transactionService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    protected void onBootstrap(ApplicationEvent applicationEvent) {
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() {
                UserTransaction userTransaction = transactionService.getUserTransaction();
                try {
                    userTransaction.begin();
                    NodeRef companyHome = repositoryHelper.getCompanyHome();
                    NodeRef workflowRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, WORKFLOW_FOLDER);
                    if (workflowRef == null) {
                        HashMap<QName, Serializable> props = new HashMap<>(1, 1.0f);
                        props.put(ContentModel.PROP_NAME, WORKFLOW_FOLDER);
                        nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(WORKFLOW_FOLDER)), ContentModel.TYPE_FOLDER, props);
                    }
                    userTransaction.commit();
                } catch (Exception e) {
                    try {
                        userTransaction.rollback();
                    } catch (SystemException e1) {
                        logger.error(e.getMessage(), e1);
                    }
                }
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());
    }

    @Override
    protected void onShutdown(ApplicationEvent event) {

    }
}
