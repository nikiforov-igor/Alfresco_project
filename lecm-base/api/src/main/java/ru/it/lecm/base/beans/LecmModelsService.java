package ru.it.lecm.base.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryRepositoryBootstrap;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.RepositoryLocation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис обработки кастомизируемых моделей (задеплоенных в репозиторий)
 *
 * Created by AZinovin on 19.12.13.
 */
public class LecmModelsService {

    private RepositoryLocation repositoryModelsLocation;

    private Map<String, String> modelsMap = new HashMap<String, String>();
    private NodeService nodeService;
    private DictionaryRepositoryBootstrap dictionaryRepositoryBootstrap;
    private TenantAdminService tenantAdminService;
    private DictionaryDAO dictionaryDAO;
    private TransactionService transactionService;
    private Logger logger = LoggerFactory.getLogger(LecmModelsService.class);
    private ContentService contentService;
    private NamespaceService namespaceService;

    public void setRepositoryModelsLocation(RepositoryLocation repositoryModelsLocation) {
        this.repositoryModelsLocation = repositoryModelsLocation;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDictionaryRepositoryBootstrap(DictionaryRepositoryBootstrap dictionaryRepositoryBootstrap) {
        this.dictionaryRepositoryBootstrap = dictionaryRepositoryBootstrap;
    }

    public void setTenantAdminService(TenantAdminService tenantAdminService) {
        this.tenantAdminService = tenantAdminService;
    }

    public void setDictionaryDAO(DictionaryDAO dictionaryDAO) {
        this.dictionaryDAO = dictionaryDAO;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public boolean isRestorable(String modelName) {
        return modelsMap.containsKey(modelName.replace(":","_"));
    }

    public M2Model restoreDefaultModel(String modelName) {
        M2Model model;

        String location = modelsMap.get(modelName.replace(":","_"));
        model = loadModelFromLocation(location, true);

        return model;
    }

    public M2Model loadModelFromLocation(final String location, boolean updateExisting) {
        M2Model model = null;
        if (repositoryModelsLocation == null) {
            return null;
        }
        final NodeRef modelsRoot = getModelsRoot();
        if (modelsRoot == null) {
            return null;
        }
        if (location != null) {
            try {
                InputStream modelStream = null;
                try {
                    modelStream = getClass().getClassLoader().getResourceAsStream(location);
                    if (modelStream == null) {
                        throw new DictionaryException("Could not find bootstrap model " + location);
                    }
                    model = M2Model.createModel(modelStream);
                    if (model != null) {
                        final String name = model.getName().replace(":", "_");
                        modelsMap.put(name, location);
                        final NodeRef node = nodeService.getChildByName(modelsRoot, ContentModel.ASSOC_CONTAINS, name);
                        dictionaryRepositoryBootstrap.onDictionaryInit();   //внесено внутрь цикла для правильного распознавания зависимых моделей
                        if (node == null || (updateExisting)) {
                            dictionaryDAO.putModel(model);
                            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                                public Object execute() throws Exception {
                                    InputStream contentInputStream = null;
                                    try {
                                        contentInputStream = getClass().getClassLoader().getResourceAsStream(location);
                                        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                                        props.put(ContentModel.PROP_NAME, name);

                                        NodeRef newNode;
                                        boolean update = false;
                                        if (node == null) {
                                            newNode = nodeService.createNode(modelsRoot, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), ContentModel.TYPE_DICTIONARY_MODEL, props).getChildRef();
                                            update = true;
                                        } else {
                                            newNode = node;
                                            InputStream oldContentInputStream = null;
                                            try {
                                                ContentReader oldContentReader = contentService.getReader(newNode, ContentModel.PROP_CONTENT);
                                                oldContentInputStream = oldContentReader.getContentInputStream();
                                                //сравниваем с имеющейся моделью
                                                while (!update) {
                                                    int newByte = contentInputStream.read();
                                                    int oldByte = oldContentInputStream.read();
                                                    update = newByte != oldByte;
                                                    if (newByte == -1 || oldByte == -1) {
                                                        break;
                                                    }
                                                }
                                            } finally {
                                                contentInputStream.reset();
                                                try {
                                                    if (oldContentInputStream != null) {
                                                        oldContentInputStream.close();
                                                    }
                                                } catch (IOException ioe) {
                                                    logger.warn("Failed to close model input stream for '" + location + "': " + ioe);
                                                }
                                            }
                                        }
                                        //не обновляем, если нет изменений
                                        if (update) {
                                            if (!nodeService.hasAspect(newNode, ContentModel.ASPECT_VERSIONABLE)) {
                                                nodeService.addAspect(newNode, ContentModel.ASPECT_VERSIONABLE, null);
                                            }
                                            ContentWriter contentWriter = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true);
                                            contentWriter.putContent(contentInputStream);
                                            nodeService.setProperty(newNode, ContentModel.PROP_MODEL_ACTIVE, true);
                                        }
                                    } finally {
                                        try {
                                            if (contentInputStream != null) {
                                                contentInputStream.close();
                                            }
                                        } catch (IOException ioe) {
                                            logger.warn("Failed to close model input stream for '" + location + "': " + ioe);
                                        }
                                    }
                                    return null;
                                }
                            }
                                    , false, false);
                        }
                    }
                } finally {
                    try {
                        if (modelStream != null) {
                            modelStream.close();
                        }
                    } catch (IOException ioe) {
                        logger.warn("Failed to close model input stream for '" + location + "': " + ioe);
                    }
                }
            } catch (DictionaryException e) {
                throw new DictionaryException("Could not import bootstrap model " + location, e);
            }
        }
        return model;
    }

    public NodeRef getModelsRoot() {
        NodeRef root = nodeService.getRootNode(repositoryModelsLocation.getStoreRef());
        return resolveQNamePath(root, repositoryModelsLocation.getPathElements());
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
}
