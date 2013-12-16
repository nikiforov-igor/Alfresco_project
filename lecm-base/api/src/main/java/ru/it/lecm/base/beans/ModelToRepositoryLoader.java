package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.*;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Размещает модели данных в репозитории, где они впоследствии подхватываются альфреской
 * <p/>
 * Created by AZinovin on 12.12.13.
 */
public class ModelToRepositoryLoader implements DictionaryListener {

    private static final Logger logger = LoggerFactory.getLogger(ModelToRepositoryLoader.class);
    private List<String> models = new ArrayList<String>();
    private List<String> messages = new ArrayList<String>();

    private RepositoryLocation repositoryModelsLocation;

    private RepositoryLocation repositoryMessagesLocation;

    private DictionaryDAO dictionaryDAO = null;

    private ContentService contentService;

    private NodeService nodeService;

    private TenantAdminService tenantAdminService;

    private NamespaceService namespaceService;

    private MessageService messageService;

    private TransactionService transactionService;

    private BehaviourFilter behaviourFilter;
    private DictionaryRepositoryBootstrap dictionaryRepositoryBootstrap;

    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
        this.behaviourFilter = behaviourFilter;
    }

    public void setRepositoryModelsLocation(RepositoryLocation repositoryModelsLocation) {
        this.repositoryModelsLocation = repositoryModelsLocation;
    }

    public void setRepositoryMessagesLocation(RepositoryLocation repositoryMessagesLocation) {
        this.repositoryMessagesLocation = repositoryMessagesLocation;
    }

    public void setDictionaryDAO(DictionaryDAO dictionaryDAO) {
        this.dictionaryDAO = dictionaryDAO;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setTenantAdminService(TenantAdminService tenantAdminService) {
        this.tenantAdminService = tenantAdminService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public void setLabels(List<String> labels) {
        this.messages = labels;
    }

    /**
     * Размещение моделей в репозиторий
     */
    public void init() {
        AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                    public Object execute() throws Exception {

                        onDictionaryInit();
                        initMessages();
                        return null;
                    }
                }, false, false);
                return null;
            }
        };
        AuthenticationUtil.runAsSystem(raw);
        register();
    }

    public void register() {
        dictionaryDAO.register(this);
    }

    @Override
    public void onDictionaryInit() {
        if (repositoryModelsLocation == null) {
            return;
        }
        NodeRef root = nodeService.getRootNode(repositoryModelsLocation.getStoreRef());
        final NodeRef modelsRoot = resolveQNamePath(root, repositoryModelsLocation.getPathElements());
        if (modelsRoot == null) {
            return;
        }
        dictionaryRepositoryBootstrap.init();
        // register models
        for (final String bootstrapModel : models) {
            try {
                InputStream modelStream = null;
                try {
                    modelStream = getClass().getClassLoader().getResourceAsStream(bootstrapModel);
                    if (modelStream == null) {
                        throw new DictionaryException("Could not find bootstrap model " + bootstrapModel);
                    }
                    M2Model model = M2Model.createModel(modelStream);

                    if (model.getTypes() != null && !model.getTypes().isEmpty()) {
                        String name = model.getTypes().get(0).getName().replace(":", "_");
                        NodeRef node = nodeService.getChildByName(modelsRoot, ContentModel.ASSOC_CONTAINS, name);
                        if (node == null) {
                            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                            props.put(ContentModel.PROP_NAME, name);

                            NodeRef newNode = nodeService.createNode(modelsRoot, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), ContentModel.TYPE_DICTIONARY_MODEL, props).getChildRef();
                            ContentWriter contentWriter = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true);
                            modelStream.reset();
                            contentWriter.putContent(modelStream);
                            nodeService.setProperty(newNode, ContentModel.PROP_MODEL_ACTIVE, true);
                            dictionaryDAO.putModel(model);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (modelStream != null) {
                            modelStream.close();
                        }
                    } catch (IOException ioe) {
                        logger.warn("Failed to close model input stream for '" + bootstrapModel + "': " + ioe);
                    }
                }
            } catch (DictionaryException e) {
                throw new DictionaryException("Could not import bootstrap model " + bootstrapModel, e);
            }
        }

    }

    public void afterDictionaryDestroy() {

    }

    public void afterDictionaryInit() {

    }

    public void initMessages() {

    }

    protected NodeRef resolveQNamePath(NodeRef rootNodeRef, String[] pathPrefixQNameStrings) {
        if (pathPrefixQNameStrings.length == 0) {
            throw new IllegalArgumentException("Path array is empty");
        }
        // walk the path
        NodeRef parentNodeRef = rootNodeRef;
        for (String pathPrefixQNameString : pathPrefixQNameStrings) {
            QName pathQName;
            if (tenantAdminService.isEnabled()) {
                String[] parts = QName.splitPrefixedQName(pathPrefixQNameString);
                if ((parts.length == 2) && (parts[0].equals(NamespaceService.APP_MODEL_PREFIX))) {
                    String pathUriQNameString = String.valueOf(QName.NAMESPACE_BEGIN) + NamespaceService.APP_MODEL_1_0_URI + QName.NAMESPACE_END + parts[1];

                    pathQName = QName.createQName(pathUriQNameString);
                } else {
                    pathQName = QName.createQName(pathPrefixQNameString, namespaceService);
                }
            } else {
                pathQName = QName.createQName(pathPrefixQNameString, namespaceService);
            }

            List<ChildAssociationRef> childAssocRefs = nodeService.getChildAssocs(parentNodeRef, RegexQNamePattern.MATCH_ALL, pathQName);
            if (childAssocRefs.size() != 1) {
                return null;
            }
            parentNodeRef = childAssocRefs.get(0).getChildRef();
        }
        return parentNodeRef;
    }

    public void setDictionaryRepositoryBootstrap(DictionaryRepositoryBootstrap dictionaryRepositoryBootstrap) {
        this.dictionaryRepositoryBootstrap = dictionaryRepositoryBootstrap;
    }
}
