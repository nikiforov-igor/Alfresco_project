package ru.it.lecm.base.beans;

import java.io.FileNotFoundException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryRepositoryBootstrap;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.util.PropertyCheck;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Сервис обработки кастомизируемых моделей (задеплоенных в репозиторий)
 *
 * Created by AZinovin on 19.12.13.
 */
public class LecmModelsService extends AbstractLifecycleBean {

    private RepositoryLocation repositoryModelsLocation;

    private Map<String, String> modelsMap = new HashMap<String, String>();
    private Boolean updateExisting = false;
    private NodeService nodeService;
    private DictionaryRepositoryBootstrap dictionaryRepositoryBootstrap;
    private TenantAdminService tenantAdminService;
    private DictionaryDAO dictionaryDAO;
    private TransactionService transactionService;
    private final Logger logger = LoggerFactory.getLogger(LecmModelsService.class);
    private ContentService contentService;
    private NamespaceService namespaceService;
	private boolean forceUpdateCache = false;

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

	public void setForceUpdateCache(boolean forceUpdateCache) {
		this.forceUpdateCache = forceUpdateCache;
	}
	
    public boolean isRestorable(String modelName) {
        return modelsMap.containsKey(modelName.replace(":","_"));
    }

    public void restoreDefaultModel(String modelName) {
        M2Model model;

        String location = modelsMap.get(modelName.replace(":","_"));
        loadModelFromLocation(location, true);
    }

	public void loadModelFromLocation(final String location, boolean updateExisting) {
		this.updateExisting = updateExisting;
		PropertyCheck.mandatory(this, "location", location);
		PropertyCheck.mandatory(this, "repositoryModelsLocation", repositoryModelsLocation);
		
		long startTime = System.currentTimeMillis();
        
        if (logger.isTraceEnabled())
        {
            logger.trace("loadModelFromLocation: ["+Thread.currentThread()+"]");
        }
        
        Collection<QName> modelsBefore = dictionaryDAO.getModels(); // note: on first bootstrap will init empty dictionary
        int modelsBeforeCnt = (modelsBefore != null ? modelsBefore.size() : 0);
		
		InputStream modelStream = getClass().getClassLoader().getResourceAsStream(location);
		if (modelStream == null)
		{
			throw new DictionaryException("Could not find bootstrap model " + location);
		}
		try {
			M2Model model = M2Model.createModel(modelStream);
			if (logger.isDebugEnabled())
			{
				logger.debug("Loading model: "+model.getName()+" (from "+location+")");
			}
						
			QName qName = dictionaryDAO.putModel(model);
			
			String name = model.getName().replace(":", "_");
			modelsMap.put(name, location);
		} catch (DictionaryException e) {
			String msg = String.format("Error load model '%s' to repository", location);
			logger.error(msg);
			throw new DictionaryException(msg, e);
		} finally {
			try
			{
				modelStream.close();
			} 
			catch (IOException ioe)
			{
				logger.warn("Failed to close model input stream for '"+location+"': "+ioe);
			}
		}
		Collection<QName> modelsAfter = dictionaryDAO.getModels();
        int modelsAfterCnt = (modelsAfter != null ? modelsAfter.size() : 0);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Model count: before="+modelsBeforeCnt+", load=1, after="+modelsAfterCnt+" in "+(System.currentTimeMillis()-startTime)+" msecs ["+Thread.currentThread()+"]");
        }
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
    
	@Override
	protected void onBootstrap(ApplicationEvent event)
	{
		logger.trace("onBootstrap count: "+modelsMap.size()+" ,updateExisting: "+updateExisting);
		final NodeRef modelsRoot = getModelsRoot();
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
            @Override
            public Void doWork() throws Exception {
            	return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>(){

					@Override
					public Void execute() throws Throwable {
						for(String name : modelsMap.keySet()) 
						{
							InputStream modelStream = null;
							try {
								String location = modelsMap.get(name);
								final ClassPathResource modelResource = new ClassPathResource(location);
								modelStream = modelResource.getInputStream();
								try {
									final NodeRef node = nodeService.getChildByName(modelsRoot, ContentModel.ASSOC_CONTAINS, name);
									if (node == null || (updateExisting)) {
										logger.trace("Add model '{}' to Data Dictionary", name);
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
												
												if (forceUpdateCache) {
													// ALFFIVE-185
													// DictionaryRepositoryBootstrap.onBootstrap может отработать раньше, чем LecmModelsService.onBootstrap,
													// что может привести к ошибкам компиляции модели в случае, если зависимости ещё не были загружены в репозиторий.
													logger.debug("Force model cache update is enabled");
													InputStream is = null;
													M2Model model = null;
													try {
														is = modelResource.getInputStream();
														model = M2Model.createModel(is);
														dictionaryDAO.putModel(model);
													} catch (Exception e) {
														logger.error("Failed to register model {}", model.getName(), e);
													} finally {
														IOUtils.closeQuietly(is);
													}
												}
												
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
									}
								} catch (Exception e) {
									String msg = String.format("Error bootstrap model '%s'", location);
									logger.error(msg);
									throw new DictionaryException(msg, e);
								}
							} catch (AlfrescoRuntimeException e) {
								throw e;
							} catch (FileNotFoundException e) {
								throw new DictionaryException("Файл модели не найден", e);
							} catch (IOException e) {
								throw new DictionaryException("Не удалось прочитать файл модели", e);
							} finally {
								if(modelStream!=null)
									IOUtils.closeQuietly(modelStream);
							}
					    }
						return null;
					}
				}, false, true);
            }
        });
	}
	
	@Override
	protected void onShutdown(ApplicationEvent event)
	{
	    // NOOP
	}
    
}
