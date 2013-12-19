package ru.it.lecm.base.beans;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryListener;
import org.alfresco.repo.dictionary.DictionaryRepositoryBootstrap;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Размещает модели данных в репозитории, где они впоследствии подхватываются альфреской
 * <p/>
 * Created by AZinovin on 12.12.13.
 */
public class ModelToRepositoryLoader implements DictionaryListener {

    private static final Logger logger = LoggerFactory.getLogger(ModelToRepositoryLoader.class);

    private boolean useDefaultModels = false;
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

    private LecmModelsService lecmModelsService;

    private boolean firstRun = true;

    public void setUseDefaultModels(String useDefaultModels) {
        this.useDefaultModels = Boolean.valueOf(useDefaultModels);
    }

    public void setLecmModelsService(LecmModelsService lecmModelsService) {
        this.lecmModelsService = lecmModelsService;
    }

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

    public void setDictionaryRepositoryBootstrap(DictionaryRepositoryBootstrap dictionaryRepositoryBootstrap) {
        this.dictionaryRepositoryBootstrap = dictionaryRepositoryBootstrap;
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

        onDictionaryInit();
//        initMessages();

        register();
    }

    /**
     * Register with the Dictionary
     */
    public void register() {
        dictionaryDAO.register(this);
    }

    @Override
    public void onDictionaryInit() {
        if (repositoryModelsLocation == null) {
            return;
        }
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                // регистрируем модели
                for (final String bootstrapModel : models) {
                    lecmModelsService.loadModelFromLocation(bootstrapModel, firstRun && useDefaultModels);
                }
                return null;
            }
        });
        firstRun = false;
    }

    public void afterDictionaryDestroy() {

    }

    public void afterDictionaryInit() {

    }

    public void initMessages() {

    }


}
