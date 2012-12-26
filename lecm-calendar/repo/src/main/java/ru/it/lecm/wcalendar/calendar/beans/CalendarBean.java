/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.wcalendar.calendar.beans;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.wcalendar.IWCalCommon;
import ru.it.lecm.wcalendar.beans.AbstractWCalCommonBean;

/**
 *
 * @author vlevin
 */
public class CalendarBean extends AbstractWCalCommonBean {

	private final static Logger logger = LoggerFactory.getLogger(CalendarBean.class);
	private final static QName TYPE_CALENDAR = QName.createQName(CALENDAR_NAMESPACE, "calendar");

	@Override
	public IWCalCommon getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_CALENDAR;
	}

	public final void bootstrap() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
//		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

		AuthenticationUtil.runAsSystem(this);
	}
}
