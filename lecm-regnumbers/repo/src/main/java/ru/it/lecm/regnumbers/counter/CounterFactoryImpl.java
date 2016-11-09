package ru.it.lecm.regnumbers.counter;

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
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.regnumbers.RegNumbersService;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ru.it.lecm.regnumbers.RegNumbersService.REGNUMBERS_NAMESPACE;

/**
 * Фабрика, создающая в хранилище объекты для счетчиков и возвращающая объекты типа Counter заданного типа для заданного документа.
 *
 * @author vlevin
 */
public class CounterFactoryImpl extends BaseBean implements CounterFactory {

	private final static String GLOBAL_PLAIN_COUNTER = "globalPlainCounter";
	private final static String GLOBAL_YEAR_COUNTER = "globalYearCounter";
	private final static String GLOBAL_SIGNEDDOCFLOW_PLAIN_COUNTER = "signedDocflowPlainCounter";
	private final static String PLAIN_PREFIX = "PLAIN_";
	private final static String YEAR_PREFIX = "YEAR_";

	// необходимые сервисы
	private NamespaceService namespaceService;
	private DictionaryService dictionaryService;

	/**
	 * ID каталога, в котором хранятся счетчики.
	 */
	public final static String REGNUMBERS_FOLDER_ID = "REGNUMBERS_FOLDER_ID";
	/**
	 * Ссылки на сквозные счетчики по типу документа. Структура: Тип в виде префикса (напр. "lecm-document:base") - Метка счетчика - Cсылка на
	 * счетчик. Для счетчиков без тега "Метка счетчика" равна
	 * null.
	 */
	private final Map<String, Map<String, NodeRef>> docTypeCounters = new ConcurrentHashMap<>();

	/**
	 * мапа на совсем глобальные счетчики - глобальный сквозной - глобальный годовой - глобальный для документооборота
	 */
	private final Map<String, NodeRef> globalCounters = new ConcurrentHashMap<>();
	// логгер
	private final static Logger logger = LoggerFactory.getLogger(CounterFactoryImpl.class);
	/**
	 * Чем мы будем заменять двоеточие в имени счетчика, которое содержит название типа документа.
	 */
	private final static char COLON_REPLACER = '_';
	/**
	 * Чем мы отделяем метку от имени типа
	 */
	private final static String TAG_DELIMITER = "$";

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
	}
	
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
					@Override
					public Void doWork() throws Exception {
						getServiceRootFolder();
						return null;
					}
				});
			}
		});
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
	 * @param tag метка счетчика
	 * @return
	 */
	@Override
	public Counter getCounter(final CounterType type, final NodeRef documentRef, final String tag) {
		Counter counter;
		NodeRef counterNodeRef;
		String documentType;
		Map<String, NodeRef> tagCounter;

		switch (type) {
			case PLAIN:
				counterNodeRef = globalCounters.get(GLOBAL_PLAIN_COUNTER);
				if (counterNodeRef == null) {
					counterNodeRef = initPlainCounter();
				}
				counter = new PlainCounter(counterNodeRef, nodeService, transactionService);
				break;
			case YEAR:
				counterNodeRef = globalCounters.get(GLOBAL_YEAR_COUNTER);
				if (counterNodeRef == null) {
					counterNodeRef = initYearCounter();
				}
				counter = new YearCounter(counterNodeRef, nodeService, transactionService);
				break;
			case SIGNED_DOCFLOW:
				counterNodeRef = globalCounters.get(GLOBAL_SIGNEDDOCFLOW_PLAIN_COUNTER);
				if (counterNodeRef == null) {
					counterNodeRef = initSignedDocflowCounter();
				}
				counter = new PlainCounter(counterNodeRef, nodeService, transactionService);
				break;
			case DOCTYPE_PLAIN:
				documentType = getItemTypeAsPrefix(documentRef);
				tagCounter = docTypeCounters.get(documentType);
				if (tagCounter == null) {
					tagCounter = new ConcurrentHashMap<>();
					docTypeCounters.put(documentType, tagCounter);
				}
				if (tagCounter.containsKey(PLAIN_PREFIX + tag)) {
					counterNodeRef = tagCounter.get(PLAIN_PREFIX + tag);
				} else {
					counterNodeRef = getDocTypePlainCounter(documentType, tag);
					tagCounter.put(PLAIN_PREFIX + tag, counterNodeRef);
				}
				if (counterNodeRef == null) {
					String errMessage = String.format("No counter for node %s! In must be inherited from lecm-document:base", documentRef.toString());
					IllegalArgumentException t = new IllegalArgumentException(errMessage);
					logger.error(errMessage, t);
					throw t;
				}
				counter = new PlainCounter(counterNodeRef, nodeService, transactionService);
				break;
			case DOCTYPE_YEAR:
				documentType = getItemTypeAsPrefix(documentRef);
				tagCounter = docTypeCounters.get(documentType);
				if (tagCounter == null) {
					tagCounter = new ConcurrentHashMap<>();
					docTypeCounters.put(documentType, tagCounter);
				}
				if (tagCounter.containsKey(YEAR_PREFIX + tag)) {
					counterNodeRef = tagCounter.get(YEAR_PREFIX + tag);
				} else {
					counterNodeRef = getDocTypeYearCounter(documentType, tag);
					tagCounter.put(YEAR_PREFIX + tag, counterNodeRef);
				}
				if (counterNodeRef == null) {
					String errMessage = String.format("No counter for node %s! In must be inherited from lecm-document:base", documentRef.toString());
					IllegalArgumentException t = new IllegalArgumentException(errMessage);
					logger.error(errMessage, t);
					throw t;
				}
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
	private NodeRef initPlainCounter() {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		return transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
//
//			@Override
//			public NodeRef execute() throws Throwable {
				NodeRef counter = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, CounterType.PLAIN.objectName());
				if (counter == null) {
					counter = createNewCounter(CounterType.PLAIN, null, null);
					NodeRef previousCounter = globalCounters.put(GLOBAL_PLAIN_COUNTER, counter);
					if (previousCounter == null) {
						logger.info("Global plain counter node '{}' created", counter);
					} else {
						logger.warn("Global plain counter has been found. It's id {}. It was updated to {}", previousCounter, counter);
					}
				}
				return counter;
//			}
//		}, false, true);
	}

	/**
	 * Создать (если его еще нет) глобальный годовой счетчик. Запомнить NodeRef
	 */
	private NodeRef initYearCounter() {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		return transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
//
//			@Override
//			public NodeRef execute() throws Throwable {
				NodeRef counter = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, CounterType.YEAR.objectName());
				if (counter == null) {
					counter = createNewCounter(CounterType.YEAR, null, null);
					NodeRef previousCounter = globalCounters.put(GLOBAL_YEAR_COUNTER, counter);
					if (previousCounter == null) {
						logger.info("Global year counter node '{}' created", counter);
					} else {
						logger.warn("Global year counter has been found. It's id {}. It was updated to {}", previousCounter, counter);
					}
				}
				return counter;
//			}
//		}, false, true);
	}

	/**
	 * Создать (если его еще нет) счетчик для документооборота. Запомнить NodeRef
	 */
	private NodeRef initSignedDocflowCounter() {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		return transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
//
//			@Override
//			public NodeRef execute() throws Throwable {
				NodeRef counter = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, CounterType.SIGNED_DOCFLOW.objectName());
				if (counter == null) {
					counter = createNewCounter(CounterType.SIGNED_DOCFLOW, null, null);
					NodeRef previousCounter = globalCounters.put(GLOBAL_SIGNEDDOCFLOW_PLAIN_COUNTER, counter);
					if (previousCounter == null) {
						logger.info("Global signed docflow plain counter node '{}' created", counter);
					} else {
						logger.warn("Global signed docflow plain counter has been found. It's id {}. It was updated to {}", previousCounter, counter);
					}
				}
				return counter;
//			}
//		}, false, true);
	}

	private NodeRef getDocTypePlainCounter(final String docType, final String tag) {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		return transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
//
//			@Override
//			public NodeRef execute() throws Throwable {
				String childName;
				if (tag == null) {
					childName = String.format(CounterType.DOCTYPE_PLAIN.objectName(), docType.replace(':', COLON_REPLACER));
				} else {
					childName = String.format(CounterType.DOCTYPE_PLAIN.objectName(), docType.replace(':', COLON_REPLACER) + TAG_DELIMITER + tag);
				}
				NodeRef counterNodeRef = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, childName);
				if (counterNodeRef == null) {
					counterNodeRef = createNewCounter(CounterType.DOCTYPE_PLAIN, docType, tag);
					logger.info(String.format("Plain counter node '%s' for doctype '%s' with tag '%s' created", counterNodeRef, docType, tag));
				}
				return counterNodeRef;
//			}
//		}, false, true);
	}

	private NodeRef getDocTypeYearCounter(final String docType, final String tag) {
//		RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
//		return transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
//
//			@Override
//			public NodeRef execute() throws Throwable {
				String childName;
				if (tag == null) {
					childName = String.format(CounterType.DOCTYPE_YEAR.objectName(), docType.replace(':', COLON_REPLACER));
				} else {
					childName = String.format(CounterType.DOCTYPE_YEAR.objectName(), docType.replace(':', COLON_REPLACER) + TAG_DELIMITER + tag);
				}
				NodeRef counterNodeRef = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, childName);
				if (counterNodeRef == null) {
					counterNodeRef = createNewCounter(CounterType.DOCTYPE_YEAR, docType, tag);
					logger.info(String.format("Year counter node '%s' for doctype '%s' with tag '%s' created", counterNodeRef, docType, tag));
				}
				return counterNodeRef;
//			}
//		}, false, true);
	}

	/**
	 * Создать тегированные счетчики для определенного типа документов.
	 *
	 * @param documentType Тип документа в префиксальной форме. Должен наследоваться от lecm-document:base
	 * @param tags Список меток счетчиков.
	 */
	@Override
	public void initTaggedCounters(String documentType, List<String> tags) {
		Map<String, NodeRef> tagCounter = docTypeCounters.get(documentType);
		if (tagCounter == null) {
			tagCounter = new ConcurrentHashMap<>();
			docTypeCounters.put(documentType, tagCounter);
		}

		for (String tag : tags) {
			// создание сквозного счетчика
			NodeRef doctypePlainTaggedCounterNodeRef = getDocTypePlainCounter(documentType, tag);
			tagCounter.put(PLAIN_PREFIX + tag, doctypePlainTaggedCounterNodeRef);

			// создание годового счетчика
			NodeRef doctypeYearTaggedCounterNodeRef = getDocTypeYearCounter(documentType, tag);
			tagCounter.put(YEAR_PREFIX + tag, doctypeYearTaggedCounterNodeRef);
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
	 * Создать в репозитории новый счетчик определенного типа для определенного вида документа.
	 *
	 * @param counterType тип счетчика.
	 * @param docType типа документа.
	 * @param tag метка счетчика
	 * @return ссылка на объект созданного счетчика
	 */
	private NodeRef createNewCounter(final CounterType counterType, final String docType, final String tag) {
		final QName itemType;
		final Map<QName, Serializable> properties = new HashMap<>();
		String nodeName, assocName;

		switch (counterType) {
			case PLAIN:
			case SIGNED_DOCFLOW:
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

		if (tag != null) {
			nodeName = String.format(counterType.objectName(), docType.replace(':', COLON_REPLACER) + TAG_DELIMITER + tag);
			assocName = String.format(counterType.objectName(), docType + TAG_DELIMITER + tag);
		} else {
			nodeName = String.format(counterType.objectName(), docType != null ? docType.replace(':', COLON_REPLACER) : null);
			assocName = String.format(counterType.objectName(), docType);
		}
		final QName assocQName = QName.createQName(REGNUMBERS_NAMESPACE, assocName);
		properties.put(ContentModel.PROP_NAME, nodeName);

		AuthenticationUtil.RunAsWork<NodeRef> plainCounterCreationWrapper = new AuthenticationUtil.RunAsWork<NodeRef>() {
			@Override
			public NodeRef doWork() throws Exception {
				ChildAssociationRef associationRef = nodeService.createNode(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, assocQName, itemType, properties);
				return associationRef.getChildRef();
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
		return getFolder(REGNUMBERS_FOLDER_ID);
	}
}
