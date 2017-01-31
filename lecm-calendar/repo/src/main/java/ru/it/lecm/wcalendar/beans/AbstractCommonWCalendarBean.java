package ru.it.lecm.wcalendar.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.ICommonWCalendar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author vlevin
 */
public abstract class AbstractCommonWCalendarBean extends BaseBean implements ICommonWCalendar {

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
            NodeRef rootNode = getServiceRootFolder();
            Map<String, Object> params = containerParams();
            NodeRef container = nodeService.getChildByName(rootNode, ContentModel.ASSOC_CONTAINS, (String) params.get("CONTAINER_NAME"));
            return container;
		}

	@Override
	public NodeRef createWCalendarContainer() {
		return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>(){
//			TODO: DONE Собственно, вынесено из doWork
			@Override
			public NodeRef doWork() throws Exception {
				final NodeRef rootNode = getServiceRootFolder();
				final Map<String, Object> params = containerParams();

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

	/**
	 * Задание параметров для инициализации контейнера для каленларей или
	 * графиков работы или отсутствия. Используется в doWork(). CONTAINER_NAME -
	 * название контейнеров. CONTAINER_TYPE - тип контейнера. Все три контейнера
	 * создаются в корне хранилища (company home).
	 *
	 * @return HashMap с параметрами контейнера.
	 */
	protected abstract Map<String, Object> containerParams();

	///TODO DONE раньше, этот метод создавал папки, и запускался в транзакции.
        //сейчас - так вроде не нужно. получение вынес в getWCalendarContainer
//      @Override
//	public NodeRef doWork() throws Exception {
////		TODO: DONE Метод разделён и избавлен от транзакции. Пока что попытка создания
////		папок происходит при инициалиазиции бинов AbsenceBean, ScheduleBean, CalendarBean
////		По идее, надо только один раз гарантированно создавать.
//		//repository.init();
//		final NodeRef rootNode = getServiceRootFolder();
//		final Map<String, Object> params = containerParams();
//		NodeRef container = nodeService.getChildByName(rootNode, ContentModel.ASSOC_CONTAINS, (String) params.get("CONTAINER_NAME"));
//		return container;
//	}

	@Override
	public NodeRef getServiceRootFolder() {
            return getFolder(WORK_CALENDAR_FOLDER_ID);
	}

	@Override
	public void initServiceImpl() {
		// TODO: Надо привести сервис календарей к обычной системе папок и избавиться это этой странной логики
		if(getWCalendarContainer() == null){
			createWCalendarContainer();
		}
	}
	
}
