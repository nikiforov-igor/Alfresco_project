package ru.it.lecm.businessjournal.schedule;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;

/**
 *
 * @author vkuprin
 */
public class BusinessJournalAuthLoggerSchedule extends BaseTransactionalSchedule {

    private final static Logger logger = LoggerFactory.getLogger(BusinessJournalAuthLoggerSchedule.class);
    private final static String appName = "AuthAudit";

    Lock lock = new ReentrantLock();
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
	
	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    @Override
    public List<NodeRef> getNodesInTx() {
        if (lock.tryLock()) {
            try {
                if (auditService.isAuditEnabled()) {
					AuditQueryParameters params = new AuditQueryParameters();

					params.setApplicationName(appName);

					auditService.auditQuery(new AuditService.AuditQueryCallback() {

						@Override
						public boolean valuesRequired() {
							return true;
						}

						@Override
						public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values) {
							String type = "";
							String login = "";
							String text = "test";

							logger.debug("Handle entry " + entryId + "\n" + "Values:");
							for (String key : values.keySet()) {
								if (values.get(key) != null) {
									logger.debug(key + ": " + values.get(key).toString());
									switch (key) {
										case "/authAudit/login/no-error/user":
											type = EventCategory.LOGIN_SUCCESS;
											login = values.get(key).toString();
											text = "Пользователь " + login + " вошёл в систему.";

											break;
										case "/authAudit/error/error/user":
											type = EventCategory.LOGIN_FAILED;
											login = values.get(key).toString();
											text = "Неудачная попытка входа. Login=" + login;
											break;
										case "/authAudit/logout/args/user":
											type = EventCategory.LOGOUT;
											login = values.get(key).toString();
											text = "Пользователь " + login + " завершил сеанс.";
											break;
										default:
											logger.debug(key);
											break;
									}
								}
							}

							//если логин пуст, падает метод personExists в недрах log()
							if (null == login || login.isEmpty()) {
								login = "broken";
							}

							businessJournalService.log(new Date(time), login, null, type, text, Collections.EMPTY_LIST);
							auditService.clearAudit(Collections.singletonList(entryId));
							return true;
						}

						@Override
						public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error) {
							throw new AlfrescoRuntimeException(errorMsg, error);
						}
					}, params, 0);

					return new ArrayList<>();
                }
            } finally {
                lock.unlock();
            }
        }
        return new ArrayList<>();
    }
}
