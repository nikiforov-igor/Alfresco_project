package ru.it.lecm.subscriptions.schedule;

import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParserException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

/**
 * User: PMelnikov
 * Date: 17.01.13
 * Time: 11:09
 */
public class BusinessJournalSchedule extends AbstractScheduledAction {

	private final static Logger logger = LoggerFactory.getLogger(BusinessJournalSchedule.class);
	/*
	 * The search service.
	 */
	private SearchService searchService;
	private NodeService nodeService;
    private NamespaceService namespaceService;
    private NotificationsService notificationsService;
    private OrgstructureBean orgstructureService;
    private SubscriptionsService subscriptionsService;

	/*
	 * The cron expression
	 */
	private String cronExpression;

	/*
	 * The name of the job
	 */
	private String jobName = "business-journal-receiver";

	/*
	 * The job group
	 */
	private String jobGroup = "subscriptions";

	/*
	 * The name of the trigger
	 */
	private String triggerName = "business-journal-receiver-triger";

	/*
	 * The name of the trigger group
	 */
	private String triggerGroup = "subscriptions-triger";

	/*
	 * The scheduler
	 */
	private Scheduler scheduler;
	private BusinessJournalService businessJournalService;

	/**
	 * Default constructore
	 */
	public BusinessJournalSchedule() {
		super();
	}

	/**
	 * Set the search service.
	 *
	 * @param searchService
	 */
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	/**
	 * Set the business journal service.
	 *
	 * @param businessJournalService
	 */

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setSubscriptionsService(SubscriptionsService subscriptionsService) {
        this.subscriptionsService = subscriptionsService;
    }

    /**
	 * Set the node service.
	 *
	 * @param nodeService
	 */
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Set the namespaceService service.
	 *
	 * @param namespaceService
	 */
	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}


	/**
	 * Get the scheduler.
	 *
	 * @return - the scheduler.
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Set the scheduler.
	 *
	 * @param scheduler
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}



	/* (non-Javadoc)
	 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getTrigger()
	 */
	@Override
	public Trigger getTrigger() {
		try {
			return new CronTrigger(getTriggerName(), getTriggerGroup(), getCronExpression());
		} catch (final ParseException e) {
			throw new InvalidCronExpression("Invalid chron expression: n" + getCronExpression());
		}
	}

	/* (non-Javadoc)
	 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getNodes()
	 */

    /**
     * Выборка записей из бизнес журнала записей по которым еще не проводилась рассылка оповещенний
     * @return
     */
    @Override
    public List<NodeRef> getNodes() {
//		TODO: DONE Не совсем понятно, зачем вся логика перенесена сюда, всегда возвращает пустой лист,
//		поэтому Executor никогда и не будет вызываться. getNodes() скорее всего в транзакцию не оборачивается, поэтому пока оставил здесь
//              Логика здесь, я так понимаю, потому, что не всегда обрабатываться будут ноды (в случае внешнего хранилища) 
//              и Executor до них не доберётся просто так.
//              Транзакции здесь действительно нет.
        RetryingTransactionHelper transactionHelper = getTransactionService().getRetryingTransactionHelper();
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Throwable {
                NodeRef root = subscriptionsService.getSubscriptionRootRef();
                if (nodeService.hasAspect(root, SubscriptionsService.ASPECT_LAST_RECORD_ID)) {
                    Long lastId = (Long) nodeService.getProperty(root, SubscriptionsService.PROP_LAST_RECORD_ID);
                    List<BusinessJournalRecord> nodes = businessJournalService.getRecordsAfter(lastId);
                    for (BusinessJournalRecord record : nodes) {
                        executeSubscription(record);
                        nodeService.setProperty(root, SubscriptionsService.PROP_LAST_RECORD_ID, record.getNodeId());
                    }

                } else {
                    List<BusinessJournalRecord> nodes = businessJournalService.getRecordsAfter(0L);
                    Long lastId = 0L;
                    if (nodes.size() > 0) {
                        lastId = nodes.get(nodes.size() - 1).getNodeId();
                    }
                    Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                    props.put(SubscriptionsService.PROP_LAST_RECORD_ID, lastId);
                    nodeService.addAspect(root, SubscriptionsService.ASPECT_LAST_RECORD_ID, props);
                }
                return null;
            }
        }, false, true);
        return new ArrayList<NodeRef>();
    }

    protected void executeSubscription(BusinessJournalRecord record) {
        String author = record.getInitiatorText();
        NodeRef initiator = record.getInitiator();
        String description = record.getRecordDescription();
        Date date = record.getDate();
        NodeRef mainObject = record.getMainObject();

        Set<NodeRef> subscriptions = new HashSet<NodeRef>();
        subscriptions.addAll(findSubscriptionsToType(record.getObjectType(), record.getEventCategory()));
        subscriptions.addAll(findSubscriptionsToInitiator(initiator));

        //При подписке на сотрудника и рабочую группу в нее должны попадать не те записи Б-Ж, в которых данный сотрудник является основным объектом, а те, в которых он является инициатором
        if (mainObject != null && nodeService.exists(mainObject) && !this.orgstructureService.isEmployee(mainObject) && !this.orgstructureService.isWorkGroup(mainObject)) {
            //добавляем подписки на объект
            subscriptions.addAll(subscriptionsService.getSubscriptionsToObject(mainObject));
        }
        sendNotificationsBySubscriptions(subscriptions, author, initiator, description, mainObject, date);
    }

    //обработка подписок на действия сотрудника/группы/подразделения
    private Set<NodeRef> findSubscriptionsToInitiator(NodeRef initiator) {
        Set<NodeRef> subscriptions = new HashSet<NodeRef>();
        if (initiator == null || !nodeService.exists(initiator)) {
            return subscriptions;
        }
        //заполнение списка возможных объектов подписки по инициатору
        Set<NodeRef> initiators = new HashSet<NodeRef>();
        initiators.add(initiator);
        //рабочие группы, в которые входит инициатор
        List<NodeRef> workGroups = orgstructureService.getEmployeeWorkGroups(initiator);
        initiators.addAll(workGroups);
        //подразделения, в которые входит инициатор (включая родительские)
        List<NodeRef> units = orgstructureService.getEmployeeUnits(initiator, false);
        for (NodeRef unit : units) {
            initiators.add(unit);
            while ((unit = orgstructureService.getParentUnit(unit)) != null) {
                if (!initiators.add(unit)) {
                    break;
                }
            }
        }

        for (NodeRef initRef : initiators) {
            subscriptions.addAll(subscriptionsService.getSubscriptionsToObject(initRef));
        }
        return subscriptions;
    }

    private void sendNotificationsBySubscriptions(Set<NodeRef> subscriptions, String author, NodeRef initiatorRef, String description, NodeRef mainObject, Date date) {
        for (NodeRef subscription : subscriptions) {
            List<NodeRef> notificationTypes = assocsToCollection(subscription, SubscriptionsService.ASSOC_NOTIFICATION_TYPE);
            List<NodeRef> employees = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_EMPLOYEE);
            List<NodeRef> positions = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_POSITION);
            List<NodeRef> units = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_ORGANIZATION_UNIT);
            List<NodeRef> workgroups = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_WORK_GROUP);
            List<NodeRef> businessRoles = assocsToCollection(subscription, SubscriptionsService.ASSOC_DESTINATION_BUSINESS_ROLE);
            Notification notification = new Notification();
            notification.setObjectRef(mainObject);
            notification.setAuthor(author);
            notification.setInitiatorRef(initiatorRef);
            notification.setDescription(description);
            notification.setFormingDate(date);
            notification.setTypeRefs(notificationTypes);
            notification.setRecipientEmployeeRefs(employees);
            notification.setRecipientPositionRefs(positions);
            notification.setRecipientOrganizationUnitRefs(units);
            notification.setRecipientWorkGroupRefs(workgroups);
            notification.setRecipientBusinessRoleRefs(businessRoles);
            notificationsService.sendNotification(notification);
        }
    }

    private Set<NodeRef> findSubscriptionsToType(NodeRef byType, NodeRef byCategory) {
        NodeRef subscriptionsRoot = subscriptionsService.getSubscriptionRootRef();
        String path = nodeService.getPath(subscriptionsRoot).toPrefixString(namespaceService);
        String type = SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE.toPrefixString(namespaceService);

        String subscriptionType = SubscriptionsService.ASSOC_OBJECT_TYPE.toPrefixString(namespaceService) + "-ref";
        String subscriptionCategory = SubscriptionsService.ASSOC_EVENT_CATEGORY.toPrefixString(namespaceService) + "-ref";

        String typeAttribute = "@" + subscriptionType.replace(":", "\\:");
        String categoryAttribute = "@" + subscriptionCategory.replace(":", "\\:");

        String query = " +PATH:\"" + path + "//*\" AND TYPE:\"" + type + "\"";

        query += " AND (ISNULL:" + subscriptionType.replace(":", "\\:") + " OR "
                + typeAttribute + ":\"\"";

        if (byType != null) {
            query += " OR (" + typeAttribute + ":\"" + byType.toString() + "\"";
            query += " AND (ISNULL:" + subscriptionCategory.replace(":", "\\:")
                    + " OR " + categoryAttribute + ":\"\"";
            if (byCategory != null) {
                query += " OR " + categoryAttribute + ":\"" + byCategory.toString() + "\"";
            }
            query += "))";
        }

        query += ")";

        SearchParameters parameters = new SearchParameters();
        parameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        parameters.setQuery(query);
        ResultSet resultSet = null;
        Set<NodeRef> subscriptions = new HashSet<NodeRef>();
        try {
            resultSet = searchService.query(parameters);
            for (ResultSetRow row : resultSet) {
                subscriptions.add(row.getNodeRef());
            }
        } catch (LuceneQueryParserException ignored) {
        } catch (Exception e1) {
            logger.error("Error while send notification for business journal's record", e1);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return subscriptions;
    }

    private List<NodeRef> assocsToCollection(NodeRef parent, QName assocName) {
        ArrayList<NodeRef> result = new ArrayList<NodeRef>();
        List<AssociationRef> refs = nodeService.getTargetAssocs(parent, assocName);
        for (AssociationRef ref : refs) {
            NodeRef assocNodeRef = ref.getTargetRef();
            result.add(assocNodeRef);
        }
        return result;
    }


    /* (non-Javadoc)
	 * @see org.alfresco.repo.action.scheduled.AbstractScheduledAction#getAction(org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	public Action getAction(NodeRef nodeRef) {
		// Use the template to build its action
		return getActionService().createAction("businessJournalScheduleExecutor");
	}

	/**
	 * Set the cron expression - see the wiki for examples.
	 *
	 * @param cronExpression
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}


	/**
	 * Get the cron expression.
	 *
	 * @return - the cron expression.
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Set the job name.
	 *
	 * @param jobName
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Get the job name
	 *
	 * @return - the job name.
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Set the job group.
	 *
	 * @param jobGroup
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * Get the job group.
	 *
	 * @return - the job group.
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * Set the trigger name.
	 *
	 * @param triggerName
	 */
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	/**
	 * Get the trigger name
	 *
	 * @return - the trigger name.
	 */
	public String getTriggerName() {
		return triggerName;
	}

	/**
	 * Set the trigger group.
	 *
	 * @param triggerGroup
	 */
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	/**
	 * Get the name of the trigger group.
	 *
	 * @return - the trigger group.
	 */
	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	/**
	 * Register with the scheduler.
	 *
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
	}

}
