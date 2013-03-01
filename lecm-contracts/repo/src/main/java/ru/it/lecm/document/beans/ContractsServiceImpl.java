package ru.it.lecm.document.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mshafeev
 * Date: 28.02.13
 * Time: 15:01
 */
public class ContractsServiceImpl extends BaseBean {
    final protected Logger logger = LoggerFactory.getLogger(ContractsServiceImpl.class);

    private Repository repositoryHelper;

    public static final String CONTRACTS_ROOT_NAME = "Contracts";

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    /**
     * Создание каталога для договоров (внутри /CompanyHome)
     * @return
     */
    public void init() {
        final String rootName = CONTRACTS_ROOT_NAME;
        repositoryHelper.init();
        final NodeRef companyHome = repositoryHelper.getCompanyHome();
        AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
            @Override
            public NodeRef doWork() throws Exception {
                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                    @Override
                    public NodeRef execute() throws Throwable {
                        NodeRef nodeRef;
                            // еще раз пытаемся получить директорию (на случай если она уже была создана другим потоком
                            nodeRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS,
                                    rootName);
                            if (nodeRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, rootName);
                                QName nodeTypeQName = ContentModel.TYPE_FOLDER;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
                                properties.put(ContentModel.PROP_NAME, rootName);
                                ChildAssociationRef associationRef = nodeService.createNode(companyHome, assocTypeQName, assocQName, nodeTypeQName, properties);
                                nodeRef = associationRef.getChildRef();
                            }
                        return nodeRef;
                    }
                });
            }
        };
        AuthenticationUtil.runAsSystem(raw);
    }

}
