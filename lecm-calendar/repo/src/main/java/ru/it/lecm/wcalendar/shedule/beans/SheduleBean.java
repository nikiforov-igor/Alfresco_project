package ru.it.lecm.wcalendar.shedule.beans;

import java.util.HashMap;
import java.util.Map;
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
public class SheduleBean extends AbstractWCalCommonBean {

	private final static QName TYPE_SHEDULE = QName.createQName(SHEDULE_NAMESPACE, "shedule");
	private final static String CONTAINER_NAME = "SheduleContainer";
	private final static QName TYPE_SHEDULE_CONTAINER = QName.createQName(WCAL_NAMESPACE, "shedule-container");
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(SheduleBean.class);

	@Override
	public IWCalCommon getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_SHEDULE;
	}
/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для графиков работы.
	 */
	public final void bootstrap() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
//		PropertyCheck.mandatory (this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

		// Создание контейнера (если не существует).
		AuthenticationUtil.runAsSystem(this);
	}

	@Override
	protected Map<String, Object> containerParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CONTAINER_NAME", CONTAINER_NAME);
		params.put("CONTAINER_TYPE", TYPE_SHEDULE_CONTAINER);

		return params;
	}
}
