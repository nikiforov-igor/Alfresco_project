package ru.it.lecm.reports.jasper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.filter.AssocDataFilter.AssocKind;
import ru.it.lecm.reports.jasper.filter.AssocDataFilterImpl;
import ru.it.lecm.reports.jasper.utils.Utils;

import java.util.Date;
import java.util.Iterator;

/**
 * Отчёт по реестру договоров
 * Параметры отчёта:
 *   "contractSubject" - тематика договора
 *   "contractType" - тип договора
 *   "contractContractor" - контрагент
 *   "contractActualOnly" - только актуальные
 *   "contractSum" - сумма (минимальная)
 *   "end" - стартовая дата, example "2013-04-03T00:00:00.000+06:00"
 *   "start" - конечна дата
 * @author rabdullin
  */
public class DSProviderReestrDogovorov extends DSProviderSearchQueryReportBase {

	private static final Logger logger = LoggerFactory.getLogger(DSProviderReestrDogovorov.class);

	/**
	 * Фильтр поиска:
	 * "contractSubject" - тематика договора
	 * "contractType" - тип договора
	 * "contractContractor" - контрагент
	 * 
	 * "contractActualOnly" - только актуальные
	 * "contractSum" - сумма
	 * 
	 * "end" - стартовая дата, fmt like "2013-04-30T00:00:00.000+06:00"
	 * "start" - конечна дата, fmt the same
	 */
	private class SearchFilter  {

		Date dateStart, dateEnd;
		Double contractSum;
		Boolean contractActualOnly;
		NodeRef contractSubject, contractType, contragent;

		public void clear() {
			dateStart = dateEnd = null;
			contractSum = null;
			contractSubject = contractType = contragent = null;
			contractActualOnly = null;
		}

		public AssocDataFilterImpl makeAssocFilter() {

			final boolean hasSubject = (contractSubject != null);
			final boolean hasType = (contractType != null);
			final boolean hasCAgent = (contragent != null);
			final boolean hasAny = hasSubject || hasType || hasCAgent;
			if (!hasAny) // в фильтре ничего не задачно -> любые данные подойдут
				return null;

			final AssocDataFilterImpl result = new AssocDataFilterImpl( serviceRegistry);

			final NamespaceService ns = serviceRegistry.getNamespaceService();

			if (hasSubject) {
				final QName qnCSubject = QName.createQName( "lecm-contract-dic:contract-subjects", ns); // Тематика договора, "lecm-contract:subjectContract-assoc"
				final QName qnAssocCSubject = QName.createQName( "lecm-contract:subjectContract-assoc", ns);
				result.addAssoc( qnCSubject, qnAssocCSubject, contractSubject, AssocKind.target);
			}

			if (hasType) {
				final QName qnCType = QName.createQName( "lecm-contract-dic:contract-type", ns); // Вид договора 
				final QName qnAssocCType = QName.createQName( "lecm-contract:typeContract-assoc", ns);
				result.addAssoc( qnCType, qnAssocCType, contractType, AssocKind.target);
			}

			if (hasCAgent) {
				final QName qnCAgent = QName.createQName( "lecm-contractor:contractor-type", ns); // Контрагенты, "lecm-contract:partner-assoc"
				final QName qnAssocCAgent = QName.createQName( "lecm-contract:partner-assoc", ns);
				result.addAssoc( qnCAgent, qnAssocCAgent, contragent, AssocKind.target);
			}

			return result;
		}

	}

	private final SearchFilter filter = new SearchFilter();

	public void setStart( final String value) {
		filter.dateStart = ArgsHelper.makeDate(value, "dateStart");
	}

	public void setEnd( final String value) {
		filter.dateEnd = ArgsHelper.makeDate(value, "dateEnd");
	}

	public void setContractSubject(String value) {
		filter.contractSubject = ArgsHelper.makeNodeRef(value, "contractSubject");
	}

	public void setContractType(String value) {
		filter.contractType = ArgsHelper.makeNodeRef(value, "contractType");
	}

	public void setContractContractor(String value) {
		filter.contragent = ArgsHelper.makeNodeRef(value, "contragent");
	}

	//"contractActualOnly" - только актуальные
	public void setContractActualOnly(String value) {
		filter.contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
	}

	//"contractSum" - сумма
	public void setContractSum(String value) {
		try {
			filter.contractSum = Utils.isStringEmpty(value) ? null : Double.parseDouble(value);
			if (filter.contractSum == 0) // значение ноль эквивалентно NULL
				filter.contractSum = null;
		} catch(Throwable e) {
			logger.error( String.format( "unexpected double value '%s' for contractSum -> ignored as NULL", value), e);
			filter.contractSum = null;
		}
	}

	final static String JRFLD_Executor_Family = "col_Executor_Family";
	final static String JRFLD_Executor_Name= "col_Executor_Name";
	final static String JRFLD_Executor_Otchestvo = "col_Executor_Otchestvo";
	final static String JRFLD_Executor_Staff = "col_Executor_Staff";


	@Override
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {

		final QName QFLD_CREATOR = QName.createQName("cm:creator", getServiceRegistry().getNamespaceService());

		final AlfrescoJRDataSource dataSource = new AlfrescoJRDataSource(iterator)  {

			/**
			 * Получить название поля (в списке атрибутов curProps объекта), 
			 * соот-щее jasper-названию колонки  
			 * @param jrFldName название колонки для Jasper (оно упрощено относительно "полного" названия в curProps)
			 * @return
			 */
			private String getAlfAttrNameByJRKey(String jrFldName) {
				return (!getMetaFields().containsKey(jrFldName)) ? jrFldName : getMetaFields().get(jrFldName).getValueLink();
			}

			@Override
			protected boolean loadAlfNodeProps(NodeRef id) {
				final boolean flag = super.loadAlfNodeProps(id);
				if (flag) {
					// подгрузим Исполнителя по его login-у
					final NodeService nodeSrv = serviceRegistry.getNodeService();
					final String loginCreator = Utils.coalesce( nodeSrv.getProperty(id, QFLD_CREATOR), null);
					if (loginCreator != null) { // получение Исполнителя по его login
						final NodeRef person = getServiceRegistry().getPersonService().getPerson(loginCreator);
						if (person != null) {
							final NodeRef executorEmplId = getOrgstructureService().getEmployeeByPerson(person);
							final BasicEmployeeInfo docExecutor = new BasicEmployeeInfo(executorEmplId);
							docExecutor.loadProps(nodeSrv, getOrgstructureService());
							// сохраним ФИО ...
							curProps.put( getAlfAttrNameByJRKey(JRFLD_Executor_Name), docExecutor.firstName);
							curProps.put( getAlfAttrNameByJRKey(JRFLD_Executor_Otchestvo), docExecutor.middleName);
							curProps.put( getAlfAttrNameByJRKey(JRFLD_Executor_Family), docExecutor.lastName);

							// curProps.put( getAlfAttrNameByJRKey(JRFLD_Executor_Staff_ID), (docExecutor.staffId != null) ? docExecutor.staffId.getId() : "" );
							curProps.put( getAlfAttrNameByJRKey(JRFLD_Executor_Staff), docExecutor.staffName);

							// curProps.put( getAlfAttrNameByJRKey(JRName_Executor_OU_ID), (docExecutor.unitId != null) ? docExecutor.unitId.getId() : "" );
							// curProps.put( getAlfAttrNameByJRKey(JRName_ExecutorC_OU_Name), docExecutor.unitName);
						}
					}
				} 
				return flag;
			}

			@Override
			public Object getFieldValue(JRField jrf) throws JRException {
				// при запросе Исполнителя выполняем подгрузку Сотрудника
				// вместо login
				final Object result = super.getFieldValue(jrf);
				if (result != null && jrf.getName().startsWith("col_Employee")) {
					// Исполнитель
				}
				return result;
			}
	};


		if (filter != null)
			dataSource.setFilter(filter.makeAssocFilter());
		return dataSource;
	}


	final static String TYPE_CONRACT = "lecm-contract:document";

	/**
	 * Построить Lucene-запрос по данным фильтра.
	 * Example: 
	 *    TYPE:"{http://www.it.ru/logicECM/contract/1.0}document" AND  @lecm\-contract\:totalSum:(0 TO 23450000)   AND @lecm\-contract\:endDate:[ NOW/DAY TO *]
	 * @return
	 */
	// TODO: функцией экранировать символы в названиях атрибутов
	@Override
	protected String buildQueryText() {
		final StringBuilder bquery = new StringBuilder();
		final QName qTYPE = QName.createQName(TYPE_CONRACT, this.serviceRegistry.getNamespaceService());
		bquery.append( "TYPE:"+ quoted(qTYPE.toString()));

		// начало
		if (filter.dateStart != null) { // "X to MAX"
			final String stMIN = ArgsHelper.dateToStr( filter.dateStart, "MIN");
			bquery.append( " AND @lecm\\-contract\\:startDate:[" + stMIN + " TO MAX]");
		}

		// окончание
		if (filter.dateEnd != null) { // "MIN to X"
			final String stMAX = ArgsHelper.dateToStr( filter.dateEnd, "MAX");
			bquery.append( " AND( (@lecm\\-contract\\:unlimited:true) OR (@lecm\\-contract\\:endDate:[ MIN TO " + stMAX + "]) )");
		}

		// Сумма договора (указан минимум)
		if (filter.contractSum != null && filter.contractSum.doubleValue() != 0) { // "X to *"
			bquery.append( " AND @lecm\\-contract\\:totalAmount:(" + filter.contractSum.toString() + " TO *)");
		}

		// Контракт актуален: если ещё не истёк срок 
		if ( Boolean.TRUE.equals(filter.contractActualOnly)) {
			bquery.append( " AND (@lecm\\-contract\\:unlimited:true OR @lecm\\-contract\\:endDate:[NOW TO MAX])"); // неограниченый или "истекает позже чем сейчас"
		}

		return bquery.toString();
	}

	
}
