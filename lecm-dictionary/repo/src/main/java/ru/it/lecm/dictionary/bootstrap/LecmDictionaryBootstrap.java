package ru.it.lecm.dictionary.bootstrap;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.dictionary.imports.XmlDictionaryImporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * User: AZinovin
 * Date: 03.10.12
 * Time: 11:19
 */
public class LecmDictionaryBootstrap extends BaseBean {

	private static final transient Logger logger = LoggerFactory.getLogger(LecmDictionaryBootstrap.class);

	private List<String> dictionaries;
	private List<String> createOrUpdateDictionaries;
	private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;

	@SuppressWarnings("UnusedDeclaration")
	public void setDictionaries(List<String> dictionaries) {
		this.dictionaries = dictionaries;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void setCreateOrUpdateDictionaries(List<String> createOrUpdateDictionaries) {
		this.createOrUpdateDictionaries = createOrUpdateDictionaries;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void bootstrap() {
        logger.info("Bootstraping dictionaries");
		AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				final NodeRef rootDir = dictionaryService.getDictionariesRoot();
				if (dictionaries != null) {
					for (final String dictionary : dictionaries) {
                        logger.info("Importing dictionary: {}", dictionary);
						transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
							@Override
							public Object execute() throws Throwable {
								InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionary);
								try {
									XmlDictionaryImporter importer = new XmlDictionaryImporter(inputStream, nodeService, namespaceService, rootDir);
									importer.readDictionary(true);
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
									XmlDictionaryImporter importer = new XmlDictionaryImporter(inputStream, nodeService, namespaceService, rootDir);
									importer.readDictionary(false);
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
}
