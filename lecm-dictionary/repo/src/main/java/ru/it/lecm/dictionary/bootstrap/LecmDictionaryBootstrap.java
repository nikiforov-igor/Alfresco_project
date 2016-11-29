package ru.it.lecm.dictionary.bootstrap;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.dictionary.beans.XMLImportBean;
import ru.it.lecm.dictionary.beans.XMLImportListener;
import ru.it.lecm.dictionary.beans.XMLImporterInfo;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;


/**
 * User: AZinovin
 * Date: 03.10.12
 * Time: 11:19
 */
public class LecmDictionaryBootstrap extends BaseBean {

	private static final transient Logger logger = LoggerFactory.getLogger(LecmDictionaryBootstrap.class);

	private List<String> dictionaries;
    private DictionaryBean dictionaryBean;
    private XMLImportBean xmlImportBean;
    private String rootPath;
    private Repository repositoryHelper;
    private boolean bootstrapOnStart;

    private XMLImportListener xmlImportListener;

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

	@SuppressWarnings("UnusedDeclaration")
	public void setDictionaries(List<String> dictionaries) {
		this.dictionaries = dictionaries;
	}

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setXmlImportBean(XMLImportBean xmlImportBean) {
        this.xmlImportBean = xmlImportBean;
    }

    public void setBootstrapOnStart(boolean bootstrapOnStart) {
        this.bootstrapOnStart = bootstrapOnStart;
    }

    public void setXmlImportListener(XMLImportListener xmlImportListener) {
        this.xmlImportListener = xmlImportListener;
    }

    // в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
	
	private NodeRef rootDir;
	
	@Override
	protected void onBootstrap(ApplicationEvent event)
	{
		if (!bootstrapOnStart) {
            logger.warn("Bootstrap disabled. Use 'lecm.dictionaries.bootstrapOnStart=true' in alfresco-global.properties file to enable it.");
            return; //пропускаем
        }
		AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				if (dictionaries != null) {
					logger.info("!!!!!!!!! dictionaries: "+dictionaries);
					RetryingTransactionHelper rth1 = transactionService.getRetryingTransactionHelper();
					rth1.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
						@Override
						public Object execute() throws Throwable {
			                if (rootPath != null) {
			                	logger.info("!!!!!!!!! rootPath: "+rootPath);
							    rootDir = getNodeByPath(rootPath);
			                } else {
			                	logger.info("!!!!!!!!! dictionariesRoot");
			                    rootDir = dictionaryBean.getDictionariesRoot();
			                }
			                return null;
						}
					},false,true);
					
					for (final String dictionary : dictionaries) {
						RetryingTransactionHelper rth2 = transactionService.getRetryingTransactionHelper();
						rth2.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
							@Override
							public Object execute() throws Throwable {
								logger.debug("Importing dictionary: {}", dictionary);
								final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionary);
								try {
									XMLImportBean.XMLImporter importer = xmlImportBean.getXMLImporter(inputStream);
									XMLImporterInfo info = importer.readItems(rootDir);
									logger.trace("{} import finished. {}", dictionary, info);
								} catch (Exception e) {
									logger.error("Can not import dictionary: " + dictionary, e);
								} finally {
									IOUtils.closeQuietly(inputStream);
								}
								return null;
							}
						},false,true);
					}
				
				}
                if (xmlImportListener != null) {
                	xmlImportListener.execute(); // оповещаем о завершении импорта
                }				
			return null;
			}
		};
		AuthenticationUtil.runAsSystem(raw);

	}

    private NodeRef getNodeByPath(String path) {
        NodeRef result = null;
        List<String> directoryPaths = new ArrayList<String>();
        StringTokenizer t = new StringTokenizer(path, "/");
        if (t.hasMoreTokens())
        {
            result = repositoryHelper.getCompanyHome();
            while (t.hasMoreTokens() && result != null)
            {
                String name = t.nextToken();
//                try
//                {
//                    result = nodeService.getChildByName(result, ContentModel.ASSOC_CONTAINS, name);
//                }
//                catch (AccessDeniedException ade)
//                {
//                    result = null;
//                }
                directoryPaths.add(name);
            }
            try
            {
            	result = createPath(result, directoryPaths);
            }
            catch (Exception e)
            {
            	logger.error("!!!!!", e);
            	result = null;
            }
        }
        return result;
    }
    
	@Override
    public NodeRef createPath(String nameSpace, NodeRef root, List<String> directoryPaths) throws WriteTransactionNeededException {

            NodeRef directoryRef = root;
            for (String pathString : directoryPaths) {
            	logger.info("!!!!!!!!!!!!!! pathString: "+pathString);
                NodeRef pathDir = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                if (pathDir == null) {
                    QName assocQName = QName.createQName(nameSpace, pathString);
                    Map<QName, Serializable> properties = new HashMap<>(1);
                    properties.put(ContentModel.PROP_NAME, pathString);
                    try {
                    	logger.info("!!!!!!!!!!!!!! properties: "+properties);
                        directoryRef = nodeService.createNode(directoryRef, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
                    } catch (DuplicateChildNodeNameException e) {
                        //есть вероятность, что папка создана другим потоком/транзакцией
                    	logger.info("!!!!!!!!!!!!!! pathString 2: "+pathString);
                        directoryRef = nodeService.getChildByName(directoryRef, ContentModel.ASSOC_CONTAINS, pathString);
                    }
                } else {
                	logger.info("!!!!!!!!!!!!!! pathDir : "+pathDir);
                    directoryRef = pathDir;
                }
                logger.info("!!!!!!!!!!!!!! directoryRef: "+directoryRef);
            }
            return directoryRef;
        }
}
