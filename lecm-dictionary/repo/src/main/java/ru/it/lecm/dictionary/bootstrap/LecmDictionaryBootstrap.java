package ru.it.lecm.dictionary.bootstrap;

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.dictionary.imports.XmlDictionaryImporter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * User: AZinovin
 * Date: 03.10.12
 * Time: 11:19
 */
public class LecmDictionaryBootstrap {

	private static final Log log = LogFactory.getLog(LecmDictionaryBootstrap.class);

	NodeService nodeService;
	Repository repositoryHelper;

	private List<String> dictionaries;
	private TransactionService transactionService;
	private NamespaceService namespaceService;

	@SuppressWarnings("UnusedDeclaration")
	public void setDictionaries(List<String> dictionaries) {
		this.dictionaries = dictionaries;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void bootstrap() {
		AuthenticationUtil.RunAsWork<Object> raw = new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				if (dictionaries != null) {
					for (final String dictionary : dictionaries) {
						transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
							@Override
							public Object execute() throws Throwable {
								InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionary);
								try {
									XmlDictionaryImporter importer = new XmlDictionaryImporter(inputStream, repositoryHelper, nodeService, namespaceService);
									importer.readDictionary(true);
								} catch (XMLStreamException e) {
									log.warn("Cann not create dictionary: " + dictionary);
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
				return null;  //To change body of implemented methods use File | Settings | File Templates.
			}
		};
		AuthenticationUtil.runAsSystem(raw);

	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
}
