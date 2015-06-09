package ru.it.lecm.orgstructure.policies;

import org.alfresco.util.PropertyCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * Базовый класс для журналируемых политик.
 * @author rabdullin
 */
public abstract class SecurityJournalizedPolicyBase
		extends SecurityNotificationsPolicyBase
{
	protected BusinessJournalService businessJournalService;

	public BusinessJournalService getBusinessJournalService() {
		return businessJournalService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	@Override
	public void init() {
		super.init();
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
	}

}
