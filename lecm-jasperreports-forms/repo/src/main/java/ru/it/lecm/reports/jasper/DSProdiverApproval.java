package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReport;

import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.jasper.config.JRDSConfigBaseImpl.JRXField;

public class DSProdiverApproval extends DSProviderSearchQueryReportBase {

	private static final Logger logger = LoggerFactory.getLogger(DSProdiverApproval.class);

	private Date periodStart, periodEnd;

	public Date getStart() {
		return periodStart;
	}

	public void setStart( final String value) {
		periodStart = ArgsHelper.makeDate(value, "periodStart");
	}

	public void setEnd( final String value) {
		periodEnd = ArgsHelper.makeDate(value, "periodEnd");
	}

	public Date getEnd() {
		return periodEnd;
	}

	final static String TYPE_APPROVAL = "lecm-al:approval-item";
	final static String FLDStatus = "lecm-al:approval-list-decision"; // <!-- результат согласования документа -->
	final static String VALUE_STATUS_NOTREADY = "NO_DECISION";

	@Override
	protected String buildQueryText() {

		final StringBuilder bquery = new StringBuilder();
		final QName qTYPE = QName.createQName(TYPE_APPROVAL, this.serviceRegistry.getNamespaceService());
		bquery.append( "TYPE:"+ quoted(qTYPE.toString()));

		// выполненные Согласования -> вне статуса 'NO_DECISION'
		bquery.append( " AND NOT @lecm\\-al\\:approval\\-list\\-decision:"+ VALUE_STATUS_NOTREADY+ " ");

		// начало == <!-- дата начала согласования -->
		if (getStart() != null) { // "X to MAX"
			final String stMIN = ArgsHelper.dateToStr( getStart(), "MIN");
			bquery.append( " AND @lecm\\-al\\:approval\\-list\\-approve\\-start:[" + stMIN + " TO MAX]");
		}

		// окончание == <!-- дата начала согласования -->
		if (getEnd() != null) { // "MIN to X"
			final String stMAX = ArgsHelper.dateToStr( getEnd(), "MAX");
			// bquery.append( " AND( (@lecm\\-contract\\:unlimited:true) OR (...) )");
			bquery.append( " AND @lecm\\-al\\:approval\\-list\\-approve\\-start:[ MIN TO " + stMAX + "]");
		}

		return bquery.toString();
	}

	@Override
	protected AlfrescoJRDataSource createDS(JasperReport report)
			throws JRException {
		// задаём период выборки в переменные отчёта ...
		setPeriodDates(report.getVariables());
		return super.createDS(report);
	}

	final static String VARNAME_START = "PERIOD_START";
	final static String VARNAME_END = "PERIOD_END";
	private void setPeriodDates(JRVariable[] variables) {
		if (variables == null || variables.length == 0)
			return;
		for(JRVariable var: variables) { // TODO: задасть свойства properties, т.к. variables проблематично изменять
			if ( VARNAME_START.equalsIgnoreCase(var.getName())) {
				// ((JRBaseVariable) var).setValue(this.periodStart) ;
			} else if ( VARNAME_END.equalsIgnoreCase(var.getName())) {
				// ((JRBaseVariable) var).setValue(this.periodEnd) ;
			} 
		}
	}


	@Override
	protected AlfrescoJRDataSource newJRDataSource( Iterator<ResultSetRow> iterator) {
		return new ApprovalDS(iterator);
	}

	private class ApprovalDS extends AlfrescoJRDataSource {

		public ApprovalDS( Iterator<ResultSetRow> iterator) {
			super(iterator);
		}

		@Override
		// пока включаеми все мета данные в curProps
		protected HashMap<String, Serializable> makeDSRowProps() {
			final HashMap<String, Serializable> result = new HashMap<String, Serializable>();
			for (Map.Entry<String, JRXField> e: this.getMetaFields().entrySet()) {
				final JRXField fld = e.getValue();
				final String fldAlfName = (fld != null) ? fld.getValueLink() : e.getKey();
				result.put( fldAlfName, null);
			}
			return result;
		}
	}
}
