package ru.it.lecm.wcalendar.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.ICommonWCalendar;

/**
 *
 * @author vlevin
 */
public abstract class AbstractCommonWCalendarBean extends BaseBean implements ICommonWCalendar, AuthenticationUtil.RunAsWork<NodeRef> {

	protected Repository repository;
	protected OrgstructureBean orgstructureService;
	protected BusinessJournalService businessJournalService;
	public static final String WORK_CALENDAR_FOLDER_ID = "WORK_CALENDAR_FOLDER_ID";
	// Получить логгер, чтобы писать, что с нами происходит.
	final private static Logger logger = LoggerFactory.getLogger(AbstractCommonWCalendarBean.class);

	/**
	 * Получить экземпляр Repository от Spring-а для последующей работы с
	 * репозиторием.
	 *
	 * @param repository передается Spring-ом
	 */
	public void setRepositoryHelper(Repository repository) {
		this.repository = repository;
	}

	/**
	 * Получить экземпляр OrgstructureBean от Spring-а для работы с
	 * оргструктурой.
	 *
	 * @param orgstructureService передается Spring-ом
	 */
	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	/**
	 * Получить экземпляр BusinessJournalService от Spring-а для работы с
	 * бизнес-журналом.
	 *
	 * @param businessJournalService передается Spring-ом
	 */
	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public NodeRef getWCalendarContainer() {
		NodeRef calendarContainer = null;
		try {
			calendarContainer = doWork();
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
		}
		return calendarContainer;
	}

	/**
	 * Задание параметров для инициализации контейнера для каленларей или
	 * графиков работы или отсутствия. Используется в doWork(). CONTAINER_NAME -
	 * название контейнеров. CONTAINER_TYPE - тип контейнера. Все три контейнера
	 * создаются в корне хранилища (company home).
	 *
	 * @return HashMap с параметрами контейнера.
	 */
	protected abstract Map<String, Object> containerParams();

	@Override
	public NodeRef doWork() throws Exception {
		repository.init();
		final NodeRef rootNode = getServiceRootFolder();
		final Map<String, Object> params = containerParams();
		NodeRef container = nodeService.getChildByName(rootNode, ContentModel.ASSOC_CONTAINS, (String) params.get("CONTAINER_NAME"));
		if (container == null) {
			RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
			container = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
				@Override
				public NodeRef execute() throws Throwable {
					NodeRef parentRef = rootNode; //the parent node
					QName assocTypeQName = ContentModel.ASSOC_CONTAINS; //the type of the association to create. This is used for verification against the data dictionary.
					QName assocQName = QName.createQName(WCAL_NAMESPACE, (String) params.get("CONTAINER_NAME")); //the qualified name of the association
					QName nodeTypeQName = (QName) params.get("CONTAINER_TYPE"); //a reference to the node type
					Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1); //optional map of properties to keyed by their qualified names
					properties.put(ContentModel.PROP_NAME, (String) params.get("CONTAINER_NAME"));
					ChildAssociationRef associationRef = nodeService.createNode(parentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
					NodeRef rootContainer = associationRef.getChildRef();
					logger.debug(String.format("container node '%s' created", rootContainer.toString()));
					return rootContainer;
				}
			});
		}
		return container;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(WORK_CALENDAR_FOLDER_ID);
	}
}
