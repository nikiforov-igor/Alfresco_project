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
 * Шедулер для окончания отсутствий: получить через searchService все ноды типа
 * absence, у которых окончание запланировано на это час, которые активированны,
 * не являются бессрочными и не находятся в архиве. Запустить над ними действие
 * "absenceEndScheduleExecutor".
 *
 * @see ru.it.lecm.wcalendar.absence.schedule.AbsenceEndScheduleExecutor
 *
 * @author vlevin
 */
public class AbsenceEndSchedule extends BaseTransactionalSchedule {

	private IAbsence absenceService;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy\\-MM\\-dd'T'HH");
	private final String searchQueryFormat = "PARENT:\"%s\" AND TYPE:\"%s\" AND @%s:[MIN TO %s] AND NOT (@%s:true) AND @%s:true AND NOT (@lecm\\-dic:active:false)";
	private final static Logger logger = LoggerFactory.getLogger(AbsenceEndSchedule.class);

	@Override
	public List<NodeRef> getNodesInTx() {
		List<NodeRef> nodes = new ArrayList<NodeRef>();
		Date now = new Date();
		NodeRef parentContainer = absenceService.getContainer();

		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		String searchQuery = String.format(searchQueryFormat, parentContainer.toString(), IAbsence.TYPE_ABSENCE.toString(), IAbsence.PROP_ABSENCE_END.toString(),
				dateFormat.format(now), IAbsence.PROP_ABSENCE_UNLIMITED.toString(), IAbsence.PROP_ABSENCE_ACTIVATED.toString());
		logger.trace("Searching absences to be ended: " + searchQuery);
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

	public AbsenceEndSchedule() {
		super();
	}

	public void setAbsenceService(IAbsence absenceService) {
		this.absenceService = absenceService;
	}

}
