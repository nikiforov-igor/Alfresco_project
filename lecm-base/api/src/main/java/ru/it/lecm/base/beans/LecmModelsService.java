package ru.it.lecm.base.beans;

import java.io.FileNotFoundException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryRepositoryBootstrap;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.util.PropertyCheck;
import org.springframework.core.io.ClassPathResource;

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
    private final Logger logger = LoggerFactory.getLogger(LecmModelsService.class);
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
		final NodeRef modelsRoot = getModelsRoot();
		M2Model model = null;
		try {
			PropertyCheck.mandatory(this, "location", location);
			PropertyCheck.mandatory(this, "repositoryModelsLocation", repositoryModelsLocation);
			PropertyCheck.mandatory(this, "modelsRoot", modelsRoot);

			logger.debug("################################################################################");
			logger.trace("Repository models location: '{}'", repositoryModelsLocation.getPath());
			logger.trace("Models root folder in repository: '{}'", nodeService.getPath(modelsRoot).toPrefixString(namespaceService));
			logger.trace("Existing models will {}!", updateExisting ? "BE UPDATED" : "NOT BE UPDATED");
			logger.debug("Load model from location '{}'", location);

			final ClassPathResource modelResource = new ClassPathResource(location);
			InputStream modelStream = modelResource.getInputStream();
			try {
				logger.trace("Creating model definition from model xml file");
				model = M2Model.createModel(modelStream);
				logger.trace("Model definition successfully created");

				final String name = model.getName().replace(":", "_");
				modelsMap.put(name, location);
				logger.trace("Find model '{}' in repository", name);
				final NodeRef node = nodeService.getChildByName(modelsRoot, ContentModel.ASSOC_CONTAINS, name);
				logger.trace("Reinitialize models in dictionary");
				dictionaryRepositoryBootstrap.onDictionaryInit();   //внесено внутрь цикла для правильного распознавания зависимых моделей
				logger.trace("Models reinitialized successfully");

				if (node == null || (updateExisting)) {
					logger.trace("Add model '{}' to dictionary", name);
					QName qName = dictionaryDAO.putModel(model);
					logger.trace("Model '{}' successfully added. Model QName is '{}'", name, qName.toString());
					InputStream contentInputStream = null;
					try {
						contentInputStream = modelResource.getInputStream();

						NodeRef newNode;
						boolean update = false;
						if (node == null) {
							logger.trace("Model '{}' WAS NOT FOUND in repository location", name);
							Map<QName, Serializable> props = new HashMap<>();
							props.put(ContentModel.PROP_NAME, name);

							logger.trace("Add model '{}' to repository location", name);
							newNode = nodeService.createNode(modelsRoot, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), ContentModel.TYPE_DICTIONARY_MODEL, props).getChildRef();
							logger.trace("Model '{}' successfully added to repository", name);
							update = true;
						} else {
							logger.trace("Model '{}' WAS FOUND in repository location", name);
							newNode = node;
							InputStream oldContentInputStream = null;
							try {
								logger.trace("Compare model '{}' content with its repository content", name);
								ContentReader oldContentReader = contentService.getReader(newNode, ContentModel.PROP_CONTENT);
								oldContentInputStream = oldContentReader.getContentInputStream();
								update = !IOUtils.contentEquals(oldContentInputStream, contentInputStream);
							} catch (Exception e) {
								String msg =String.format("Failed to compare model '%s' content with its repository content", name);
								logger.error(msg);
								throw new DictionaryException(msg, e);
							} finally {
								IOUtils.closeQuietly(oldContentInputStream);
								IOUtils.closeQuietly(contentInputStream);
							}
						}

						//не обновляем, если нет изменений
						if (update) {
							logger.trace("Models are DIFFERENT. Model content in repository will BE UPDATED.");
							contentInputStream = modelResource.getInputStream();
							/*
							 ALF-3866
							 если установлена опция lecm.models.useDefaultModels=true и у нас настроена аутентификация с использованием SSO,
							 то в ситуации, когда во время бутстрапа будет обновлено содержимое модели, произойдет зависание системы.
							 Зависание произойдет по вот такой последовтельности действий
							 1) на ноду в репозиторий вешается аспект VERSIONABLE c атрибутами по умолчанию (cm:autoVersion=true, cm:autoVersionOnUpdateProps=true)
							 2) при коммите этой транзакции сработает policy которая установит ноде новую версию и имя пользователя который загрузил новую версию
							 3) вычисление имени пользователя происходит в методе org.alfresco.repo.lock.LockServiceImpl.getUserName(), он, в свою очередь, обращается к authenticationService.getCurrentUserName()
							 4) вызов authenticationService.getCurrentUserName() приводит к зависанию системы на старте с использованием SSO
							*/
							if (!nodeService.hasAspect(newNode, ContentModel.ASPECT_VERSIONABLE)) {
								nodeService.addAspect(newNode, ContentModel.ASPECT_VERSIONABLE, null);
							}
							logger.trace("Update model '{}' content", name);
							ContentWriter contentWriter = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true);
							contentWriter.putContent(contentInputStream);
							logger.trace("Model '{}' content successfully updated", name);
							logger.trace("Activating model '{}'", name);
							nodeService.setProperty(newNode, ContentModel.PROP_MODEL_ACTIVE, true);
							logger.trace("Model '{}' activated successfully", name);
						} else {
							logger.trace("Models are EQUALS. Model content in repository will NOT BE UPDATED.");
						}
					} catch (Exception e) {
						String msg = String.format("Error load model '%s' to repository", name);
						logger.error(msg);
						throw new DictionaryException(msg, e);
					} finally {
						IOUtils.closeQuietly(contentInputStream);
					}
					return null;
				}
			} catch (Exception e) {
				String msg = String.format("Error bootstrap model '%s'", location);
				logger.error(msg);
				throw new DictionaryException(msg, e);
			} finally {
				IOUtils.closeQuietly(modelStream);
			}
			logger.debug("Model from location '{}' successfully loaded", location);
			logger.debug("################################################################################");
		} catch (AlfrescoRuntimeException e) {
			logger.warn(String.format("Failed load model from location '%s': %s", location, e));
			throw e;
		} catch (FileNotFoundException e) {
			String msg = String.format("Could not find bootstrap model '%s': %s", location, e);
			logger.error(msg);
			throw new DictionaryException(msg, e);
		} catch (IOException e) {
			String msg = String.format("Could not import bootstrap model '%s': %s", location, e);
			logger.error(msg);
			throw new DictionaryException(msg, e);
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
