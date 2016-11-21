package ru.it.lecm.businessjournal.schedule;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.action.scheduled.InvalidCronExpression;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author vkuprin
 */
public class BusinessJournalAuthLoggerSchedule extends AbstractScheduledAction {

    private final static Logger logger = LoggerFactory.getLogger(BusinessJournalAuthLoggerSchedule.class);
//TODO вынести в настройки
    private String cronExpression = "0/1 * * * * ?";
    private final static String appName = "AuthAudit";

    Lock lock = new ReentrantLock();
//

    /*
     * The name of the job
     */
    private String jobName = "business-journal-auth-logger";

    /*
     * The job group
     */
    private String jobGroup = "business-journal";

    /*
     * The name of the trigger
     */
    private String triggerName = "business-journal-auth-logger-trigger";

    /*
     * The name of the trigger group
     */
    private String triggerGroup = "business-journal-trigger";

    /*
     * The scheduler
     */
    private Scheduler scheduler;
    private BusinessJournalService businessJournalService;
    private AuditService auditService;
    private PersonService personService;

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public AuditService getAuditService() {
        return auditService;
    }

    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String getJobGroup() {
        return jobGroup;
    }

    @Override
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    @Override
    public String getTriggerName() {
        return triggerName;
    }

    @Override
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @Override
    public String getTriggerGroup() {
        return triggerGroup;
    }

    @Override
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public BusinessJournalService getBusinessJournalService() {
        return businessJournalService;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

    @Override
    public Trigger getTrigger() {
        try {
            //TODO вынести настройку периодичности куда-нибудь в более правильное место.
            CronTrigger trigger = new CronTrigger(getTriggerName(), getTriggerGroup(), cronExpression);
            trigger.setJobName(getJobName());
            trigger.setJobGroup(getJobGroup());
            return trigger;
        } catch (final ParseException e) {
            throw new InvalidCronExpression("Invalid chron expression: n" + cronExpression);
        }
    }

    @Override
    public List<NodeRef> getNodes() {
//        if (lock.tryLock()) {
//            try {
//                if (auditService.isAuditEnabled()) {
//                    getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//
//                        @Override
//                        public Void execute() throws Throwable {
//                            AuditQueryParameters params = new AuditQueryParameters();
//
//                            params.setApplicationName(appName);
//
//                            auditService.auditQuery(new AuditService.AuditQueryCallback() {
//
//                                @Override
//                                public boolean valuesRequired() {
//                                    return true;
//                                }
//
//                                @Override
//                                public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values) {
//                                    String type = "";
//                                    String login = "";
//                                    String text = "test";
//
//                                    logger.debug("Handle entry " + entryId + "\n" + "Values:");
//                                    for (String key : values.keySet()) {
//                                        if (values.get(key) != null) {
//                                            logger.debug(key + ": " + values.get(key).toString());
//                                            switch (key) {
//                                                case "/authAudit/login/no-error/user":
//                                                    type = EventCategory.LOGIN_SUCCESS;
//                                                    login = values.get(key).toString();
//                                                    text = "Пользователь " + login + " вошёл в систему.";
//
//                                                    break;
//                                                case "/authAudit/error/error/user":
//                                                    type = EventCategory.LOGIN_FAILED;
//                                                    login = values.get(key).toString();
//                                                    text = "Неудачная попытка входа. Login=" + login;
//                                                    break;
//                                                case "/authAudit/logout/args/user":
//                                                    type = EventCategory.LOGOUT;
//                                                    login = values.get(key).toString();
//                                                    text = "Пользователь " + login + " завершил сеанс.";
//                                                    break;
//                                                default:
//                                                    logger.debug(key);
//                                                    break;
//                                            }
//                                        }
//                                    }
//
//                                    //если логин пуст, падает метод personExists в недрах log()
//                                    if (null == login || login.isEmpty()) {
//                                        login = "broken";
//                                    }
//
//                                    businessJournalService.log(new Date(time), login, businessJournalService.getBusinessJournalDirectory(), type, text, Collections.EMPTY_LIST);
//                                    auditService.clearAudit(Collections.singletonList(entryId));
//                                    return true;
//                                }
//
//                                @Override
//                                public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error) {
//                                    throw new AlfrescoRuntimeException(errorMsg, error);
//                                }
//                            }, params, 0);
//
//                            return null;
//                        }
//                    }, false);
//                }
//            } finally {
//                lock.unlock();
//            }
//        }
        return new ArrayList<>();
    }

    @Override
    public Action getAction(NodeRef nodeRef) {
        return getActionService().createAction("businessJournalAuthLoggerSchedulerExecutor");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        register(getScheduler());
    }

}
