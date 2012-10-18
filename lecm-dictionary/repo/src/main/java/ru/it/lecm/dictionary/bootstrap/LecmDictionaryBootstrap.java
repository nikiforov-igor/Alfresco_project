package ru.it.lecm.dictionary.bootstrap;

import com.thoughtworks.xstream.XStream;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AZinovin
 * Date: 03.10.12
 * Time: 11:19
 */
public class LecmDictionaryBootstrap {

	NodeService nodeService;
	Repository repositoryHelper;

	private List<String> dictionaries;
	private static final String DICTIONARY_NAMESPACE_URI = "http://www.it.ru/lecm/dictionary/1.0";
	private static final QName DICTIONARY = QName.createQName(DICTIONARY_NAMESPACE_URI, "dictionary");
	private static final QName DESCRIPTION = QName.createQName(DICTIONARY_NAMESPACE_URI, "description");
	private static final QName TYPE = QName.createQName(DICTIONARY_NAMESPACE_URI, "type");
	private static final QName SHOW_CONTROL_IN_SEPARATE_WINDOW = QName.createQName(DICTIONARY_NAMESPACE_URI, "show_control_in_separate_window");
	private TransactionService transactionService;
	private final static String DICTIONARIES_ROOT_NAME = "Dictionary";

	public void setDictionaries(List<String> dictionaries) {
		this.dictionaries = dictionaries;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void bootstrap() {
		if (dictionaries != null) {
			for (String dictionary : dictionaries) {
				XStream xStream = new XStream();
				xStream.alias("dictionary", DictionaryDescriptor.class);
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dictionary);
				DictionaryDescriptor dictionaryDescriptor = (DictionaryDescriptor) xStream.fromXML(inputStream);
				createDictionary(dictionaryDescriptor);
			}
		}
	}

	private void createDictionary(final DictionaryDescriptor dictionaryDescriptor) {
		final NodeRef root = getDictionariesRoot();
		if (nodeService.getChildByName(root, ContentModel.ASSOC_CONTAINS, dictionaryDescriptor.getName()) == null) {
			transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
				@Override
				public Object execute() throws Throwable {
					Map<QName, Serializable> properties = new HashMap<QName, Serializable>(3);
					properties.put(ContentModel.PROP_NAME, dictionaryDescriptor.getName());
					properties.put(DESCRIPTION, dictionaryDescriptor.getDescription());
					properties.put(TYPE, dictionaryDescriptor.getType());
					properties.put(SHOW_CONTROL_IN_SEPARATE_WINDOW, dictionaryDescriptor.isShowControlInSeparateWindow());
					nodeService.createNode(root, ContentModel.ASSOC_CONTAINS, QName.createQName(DICTIONARY_NAMESPACE_URI, dictionaryDescriptor.getName()), DICTIONARY, properties);
					return "ok";
				}
			});
		}
	}


	private NodeRef getDictionariesRoot() {
		repositoryHelper.init();
		final NodeRef companyHome = repositoryHelper.getCompanyHome();
		final NodeRef[] dictionariesRoot = {nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, DICTIONARIES_ROOT_NAME)};
		if (dictionariesRoot[0] == null) {
			final Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
			properties.put(ContentModel.PROP_NAME, DICTIONARIES_ROOT_NAME);
			transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
				@Override
				public Object execute() throws Throwable {
					ChildAssociationRef associationRef = nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS, ContentModel.TYPE_FOLDER, properties);
					dictionariesRoot[0] = associationRef.getChildRef();
					return "ok";
				}
			});
		}
		return dictionariesRoot[0];
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
}
