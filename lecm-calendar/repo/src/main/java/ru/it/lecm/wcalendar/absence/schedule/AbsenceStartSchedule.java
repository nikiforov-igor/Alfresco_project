package ru.it.lecm.wcalendar.absence.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseTransactionalSchedule;
import ru.it.lecm.wcalendar.absence.IAbsence;

/**
 * Шедулер для старта отсутствий: получить через searchService все ноды типа
 * absence, у которых начало совпадает с сегодняшней датой и которые не
 * находятся в архиве. Запустить над ними действие
 * "absenceStartScheduleExecutor".
 *
 * @see ru.it.lecm.wcalendar.absence.schedule.AbsenceStartScheduleExecutor
 *
 * @author vlevin
 */
public class AbsenceStartSchedule extends BaseTransactionalSchedule {

	private IAbsence absenceService;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy\\-M\\-dd'T'HH");
	private final String searchQueryFormat = "PARENT:\"%s\" AND TYPE:\"%s\" AND @%s:[MIN TO %s] AND @%s:[%s TO MAX] AND @%s:false AND NOT (@lecm\\-dic:active:false)";
	private final static Logger logger = LoggerFactory.getLogger(AbsenceStartSchedule.class);

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		Date now = new Date();
		NodeRef parentContainer = absenceService.getContainer();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, parentContainer.toString(), IAbsence.TYPE_ABSENCE.toString(),
				IAbsence.PROP_ABSENCE_BEGIN.toString(), dateFormat.format(now), IAbsence.PROP_ABSENCE_END, dateFormat.format(now), IAbsence.PROP_ABSENCE_ACTIVATED.toString());
		logger.trace("Searching absences to be started: " + searchQuery);
		sp.setQuery(searchQuery);
		ResultSet results = null;
		try {
			results = searchService.query(sp);
			for (ResultSetRow row : results) {
				NodeRef currentNodeRef = row.getNodeRef();
				nodes.add(currentNodeRef);
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}
		return nodes;
	}

	public AbsenceStartSchedule() {
		super();
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

}
