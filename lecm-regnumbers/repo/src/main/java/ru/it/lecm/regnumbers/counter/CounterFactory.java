package ru.it.lecm.regnumbers.counter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.regnumbers.RegNumbersService;
import static ru.it.lecm.regnumbers.RegNumbersService.REGNUMBERS_NAMESPACE;

/**
 *
 * @author vlevin
 */
public class CounterFactory extends BaseBean {

	// необходимые сервисы
	private NamespaceService namespaceService;
	private DictionaryService dictionaryService;
	/**
	 * Ссылка на каталог с счетчиками.
	 */
	private NodeRef countersRootNode;
	/**
	 * ID каталога, в котором хранятся счетчики.
	 */
	public final static String REGNUMBERS_FOLDER_ID = "REGNUMBERS_FOLDER_ID";
	/**
	 * Ссылка на глобальный сквозной счетчик.
	 */
	private NodeRef globalPlainCounter;
	/**
	 * Ссылка на глобальный годовой счетчик.
	 */
	private NodeRef globalYearCounter;
	/**
	 * Ссылки на сквозные счетчики по типу документа.
	 * Ключ - тип в виде префикса (напр. "lecm-document:base")
	 */
	private final Map<String, NodeRef> doctypePlainCounters = new HashMap<String, NodeRef>();
	/**
	 * Ссылки на годовые счетчики по типу документа.
	 * Ключ - тип в виде префикса (напр. "lecm-document:base")
	 */
	private final Map<String, NodeRef> doctypeYearCounters = new HashMap<String, NodeRef>();
	// логгер
	private final static Logger logger = LoggerFactory.getLogger(CounterFactory.class);
	/**
	 * Чем мы будем заменять двоеточие в имени счетчика, которое содержит
	 * название типа документа.
	 */
	private static String COLON_REPLACER = "_";

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);

		countersRootNode = getFolder(REGNUMBERS_FOLDER_ID);

		initPlainCounter();
		initYearCounter();
		initDocTypeCounters();
	}

	public void setNamespaceService(final NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	/**
	 * Получить счетчик заданного типа для заданого экземпляра документа.
	 *
	 * @param type тип счетчика.
	 * @param documentRef экземпляр документа, для которого требуется счетчик.
	 * @return
	 */
	public Counter getCounter(final CounterType type, final NodeRef documentRef) {
		Counter counter;
		NodeRef counterNodeRef;

		switch (type) {
			case PLAIN:
				counter = new PlainCounter(globalPlainCounter, nodeService, transactionService);
				break;
			case YEAR:
				counter = new YearCounter(globalYearCounter, nodeService, transactionService);
				break;
			case DOCTYPE_PLAIN:
				counterNodeRef = doctypePlainCounters.get(getItemTypeAsPrefix(documentRef));
				if (counterNodeRef == null) {
					String errMessage = String.format("No counter for node %s! In must be inherited from  ");
					IllegalArgumentException t = new IllegalArgumentException(errMessage);
					logger.error(errMessage, t);
					throw t;
				}
				counter = new PlainCounter(counterNodeRef, nodeService, transactionService);
				break;
			case DOCTYPE_YEAR:
				counterNodeRef = doctypeYearCounters.get(getItemTypeAsPrefix(documentRef));
				counter = new YearCounter(counterNodeRef, nodeService, transactionService);
				break;
			default:
				throw new RuntimeException("Unknown counter type: " + type);

		}

		return counter;
	}

	/**
	 * Создать (если его еще нет) глобальный сквозной счетчик. Запомнить NodeRef
	 */
	private void initPlainCounter() {
		globalPlainCounter = nodeService.getChildByName(countersRootNode, ContentModel.ASSOC_CONTAINS, CounterType.PLAIN.objectName());
		if (globalPlainCounter == null) {
			globalPlainCounter = createNewCounter(CounterType.PLAIN, null);
			logger.info(String.format("Global plain counter node '%s' created", globalPlainCounter.toString()));
		}
	}

	/**
	 * Создать (если его еще нет) глобальный годовой счетчик. Запомнить NodeRef
	 */
	private void initYearCounter() {
		globalYearCounter = nodeService.getChildByName(countersRootNode, ContentModel.ASSOC_CONTAINS, CounterType.YEAR.objectName());
		if (globalYearCounter == null) {
			globalYearCounter = createNewCounter(CounterType.YEAR, null);
			logger.info(String.format("Global year counter node '%s' created", globalYearCounter.toString()));
		}
	}

	/**
	 * Создать (если их еще нет) сквозной и годовой счетчики для каждого из
	 * типов, унаследованных от lecm-document:base. Запомнить ссылки на них.
	 */
	private void initDocTypeCounters() {
		Collection<QName> documentSubTypes = dictionaryService.getSubTypes(DocumentService.TYPE_BASE_DOCUMENT, true);

		for (QName documentSubType : documentSubTypes) {
			String typePrefixStr = getQnameAsPrefix(documentSubType);
			if (!doctypePlainCounters.containsKey(typePrefixStr)) {
				NodeRef doctypePlainCounterNodeRef = nodeService.getChildByName(countersRootNode, ContentModel.ASSOC_CONTAINS,
						String.format(CounterType.DOCTYPE_PLAIN.objectName(), typePrefixStr.replace(":", COLON_REPLACER)));
				if (doctypePlainCounterNodeRef == null) {
					doctypePlainCounterNodeRef = createNewCounter(CounterType.DOCTYPE_PLAIN, typePrefixStr);
					logger.info(String.format("Plain counter node '%s' for doctype '%s' created", doctypePlainCounterNodeRef.toString(), typePrefixStr));
				}
				doctypePlainCounters.put(typePrefixStr, doctypePlainCounterNodeRef);
			}

			if (!doctypeYearCounters.containsKey(typePrefixStr)) {
				NodeRef doctypeYearCounterNodeRef = nodeService.getChildByName(countersRootNode, ContentModel.ASSOC_CONTAINS,
						String.format(CounterType.DOCTYPE_YEAR.objectName(), typePrefixStr.replace(":", COLON_REPLACER)));
				if (doctypeYearCounterNodeRef == null) {
					doctypeYearCounterNodeRef = createNewCounter(CounterType.DOCTYPE_YEAR, typePrefixStr);
					logger.info(String.format("Year counter node '%s' for doctype '%s' created", doctypeYearCounterNodeRef.toString(), typePrefixStr));
				}
				doctypeYearCounters.put(typePrefixStr, doctypeYearCounterNodeRef);
			}
		}
	}

	/**
	 * Вернуть тип NodeRef'ы в виде префикса.
	 */
	private String getItemTypeAsPrefix(final NodeRef node) {
		return getQnameAsPrefix(nodeService.getType(node));
	}

	/**
	 * Преобразовать из QName в префиксальную форму.
	 */
	private String getQnameAsPrefix(final QName type) {
		return type.toPrefixString(namespaceService);
	}

	/**
	 * Создать в репозитории новый счетчик определенного типа для определенного
	 * вида документа.
	 *
	 * @param counterType тип счетчика.
	 * @param docType типа документа.
	 * @return
	 */
	private NodeRef createNewCounter(final CounterType counterType, final String docType) {
		final QName itemType;
		final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

		switch (counterType) {
			case PLAIN:
				itemType = RegNumbersService.TYPE_PLAIN_COUNTER;
				break;
			case YEAR:
				itemType = RegNumbersService.TYPE_YEAR_COUNTER;
				properties.put(RegNumbersService.PROP_YEAR, getCurYear());
				break;
			case DOCTYPE_PLAIN:
				itemType = RegNumbersService.TYPE_DOCTYPE_PLAIN_COUNTER;
				properties.put(RegNumbersService.PROP_DOCTYPE, docType);
				break;
			case DOCTYPE_YEAR:
				itemType = RegNumbersService.TYPE_DOCTYPE_YEAR_COUNTER;
				properties.put(RegNumbersService.PROP_DOCTYPE, docType);
				properties.put(RegNumbersService.PROP_YEAR, getCurYear());
				break;
			default:
				throw new RuntimeException("Unknown counter type: " + counterType);
		}

		final QName assocQName = QName.createQName(REGNUMBERS_NAMESPACE, String.format(counterType.objectName(), docType));
		properties.put(ContentModel.PROP_NAME, String.format(counterType.objectName(), docType != null ? docType.replace(":", COLON_REPLACER) : null));


		AuthenticationUtil.RunAsWork<NodeRef> plainCounterCreationWrapper = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
				RetryingTransactionCallback<NodeRef> transactionCalback = new RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						ChildAssociationRef associationRef = nodeService.createNode(countersRootNode, ContentModel.ASSOC_CONTAINS, assocQName, itemType, properties);
						return associationRef.getChildRef();
					}
				};
				return transactionHelper.doInTransaction(transactionCalback, false, true);
			}
		};
		return AuthenticationUtil.runAsSystem(plainCounterCreationWrapper);
	}

	/**
	 * Текущий год.
	 */
	private int getCurYear() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return cal.get(Calendar.YEAR);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return countersRootNode;
	}
}
