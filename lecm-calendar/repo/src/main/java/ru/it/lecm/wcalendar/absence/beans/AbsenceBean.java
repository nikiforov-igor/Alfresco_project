/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.wcalendar.absence.beans;

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
public class AbsenceBean extends AbstractWCalCommonBean {

    private final static Logger logger = LoggerFactory.getLogger(AbsenceBean.class);
    private final static QName TYPE_ABSENCE = QName.createQName(ABSENCE_NAMESPACE, "absence");

    @Override
    public IWCalCommon getWCalendarDescriptor() {
        return this;
    }

    @Override
    public QName getWCalendarItemType() {
        return TYPE_ABSENCE;
    }
    
    public final void bootstrap () {
		PropertyCheck.mandatory (this, "repository", repository);
		PropertyCheck.mandatory (this, "nodeService", nodeService);
//		PropertyCheck.mandatory (this, "namespaceService", namespaceService);
		PropertyCheck.mandatory (this, "transactionService", transactionService);

		//создание контейнера для хранения параметров делегирования
//		AuthenticationUtil.runAsSystem (this);
		AuthenticationUtil.runAsSystem (this);

		//возможно здесь еще будет штука для создания параметров делегирования для уже существующих пользователей
	}        
}
