package ru.it.lecm.dictionary.bootstrap;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.dictionary.beans.XMLImportBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringTokenizer;


/**
 * User: AZinovin
 * Date: 03.10.12
 * Time: 11:19
 */
public class LecmDictionaryBootstrap extends BaseBean {

	private static final transient Logger logger = LoggerFactory.getLogger(LecmDictionaryBootstrap.class);

	private List<String> dictionaries;
	private List<String> createOrUpdateDictionaries;
    private DictionaryBean dictionaryBean;
    private XMLImportBean xmlImportBean;
    private String rootPath;
    private Repository repositoryHelper;

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

	@SuppressWarnings("UnusedDeclaration")
	public void setCreateOrUpdateDictionaries(List<String> createOrUpdateDictionaries) {
		this.createOrUpdateDictionaries = createOrUpdateDictionaries;
	}

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setXmlImportBean(XMLImportBean xmlImportBean) {
        this.xmlImportBean = xmlImportBean;
    }

    // в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public void bootstrap() {
		AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
                final NodeRef rootDir;
                if (rootPath != null) {
				    rootDir = getNodeByPath(rootPath);
                } else {
                    rootDir = dictionaryBean.getDictionariesRoot();
                }
				if (dictionaries != null) {
					for (final String dictionary : dictionaries) {
                        logger.info("Importing dictionary: {}", dictionary);
						transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
							@Override
							public Object execute() throws Throwable {
								InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionary);
								try {
									XMLImportBean.XMLImporter importer = xmlImportBean.getXMLImporter(inputStream);
									importer.readItems(rootDir, true);
								} catch (Exception e) {
									logger.error("Can not create dictionary: " + dictionary, e);
								} finally {
									try {
										if (inputStream != null) {
											inputStream.close();
										}
									} catch (IOException ignored) {

									}
								}
								return "ok";
							}
						});
					}
				}
				if (createOrUpdateDictionaries != null) {
					for (final String dictionary : createOrUpdateDictionaries) {
                        logger.info("Updating dictionary: {}", dictionary);
						transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
							@Override
							public Object execute() throws Throwable {
								InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionary);
								try {
									XMLImportBean.XMLImporter importer = xmlImportBean.getXMLImporter(inputStream);
									importer.readItems(rootDir, false);
								} catch (Exception e) {
									logger.error("Can not create dictionary: " + dictionary);
								} finally {
									try {
										if (inputStream != null) {
											inputStream.close();
										}
									} catch (IOException ignored) {

									}
								}
								return "ok";
							}
						});
					}
				}
				return null;
			}
		};
		AuthenticationUtil.runAsSystem(raw);

	}

    private NodeRef getNodeByPath(String path) {
        NodeRef result = null;
        StringTokenizer t = new StringTokenizer(path, "/");
        if (t.hasMoreTokens())
        {
            result = repositoryHelper.getCompanyHome();
            while (t.hasMoreTokens() && result != null)
            {
                String name = t.nextToken();
                try
                {
                    result = nodeService.getChildByName(result, ContentModel.ASSOC_CONTAINS, name);
                }
                catch (AccessDeniedException ade)
                {
                    result = null;
                }
            }
        }
        return result;
    }
}
