package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Провайдер для построения отчёта "Опись изменений к договору"
 * @author rabdullin
 */
public class DSProviderContractsDeltaById
	extends DSProviderSearchQueryReportBase
{
	private static final Logger logger = LoggerFactory.getLogger(DSProviderSearchQueryReportBase.class);

	@Override
	protected AlfrescoJRDataSource newJRDataSource(
			Iterator<ResultSetRow> iterator) {
		final LinkedDocumentsDS result = new LinkedDocumentsDS(iterator);
		result.context.setSubstitudeService(substitudeService);
		result.context.setRegistryService(serviceRegistry);
		result.context.setJrSimpleProps(jrSimpleProps);
		result.context.setMetaFields(conf().getMetaFields());
		result.buildJoin();
		return result;
	}

	private class LinkedDocumentInfo {

		public final NodeRef nodeRef;

		private LinkedDocumentInfo(NodeRef nodeRef) {
			super();
			this.nodeRef = nodeRef;
		}
	}

	private class LinkedDocumentsDS extends TypedJoinDS<LinkedDocumentInfo> {

		public LinkedDocumentsDS(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}

		@Override
		public int buildJoin() {
			final ArrayList<LinkedDocumentInfo> result = new ArrayList<LinkedDocumentInfo>();

			if (context.getRsIter() != null) {

//				final NodeService nodeSrv = serviceRegistry.getNodeService();
//				final NamespaceService ns = serviceRegistry.getNamespaceService();
//				final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

				while(context.getRsIter().hasNext()) { // тут только одна запись будет по-идее
					final int predCounter = result.size();
					final ResultSetRow rs = context.getRsIter().next();
					final NodeRef docId = rs.getNodeRef(); // id основного документа

					// TODO: здесь должен быть поиск id присоединённых документов, которые и надо будет добавлять в result ...

					// TODO: сейчас временно используем основной документ только ...
					result.add( new LinkedDocumentInfo(docId));

					if (logger.isInfoEnabled())
						logger.info( String.format("found %s joined documents for main node %s", result.size() - predCounter, docId));
				} // while
			}

			setData(result);
			setIterData( result.iterator());

			return result.size();
		}

		@Override
		protected Map<String, Serializable> getReportContextProps(
				LinkedDocumentInfo item) {
			final Map<String, Serializable> result = new HashMap<String, Serializable>();

			// TODO: fill the data from linked documents ...
			final Map<QName, Serializable> props = context.getRegistryService().getNodeService().getProperties(item.nodeRef);
			if (props != null) {
				final NamespaceService ns = context.getRegistryService().getNamespaceService();
				for( Map.Entry<QName, Serializable> entry: props.entrySet()) {
					/*
					 * не заморачиваемся по-поводу того, что в result надо иметь 
					 * ключи с именами колонок из отчёта, т.к. далее всё равно 
					 * будет выполняться преобразование этих имён колонок в 
					 * ссылки на данные - так что сразу задаём ключи ссылками
					*/
					result.put( entry.getKey().toPrefixString(ns), entry.getValue());
				}
			}
			/*
	<fields.jasper>
		<!-- Данные по "Виду договора" (с типом "lecm-contract-dic:contract-type") -->
		<field jrFldName="col_DocKind" queryFldName="{lecm-contract:typeContract-assoc/cm:name}" displayName="Вид договора" />

		<!-- Регистрационный номер договора -->
		<field queryFldName="lecm-contract:regNumSystem"
				jrFldName="col_Regnum"
				displayName="Регистрационный номер договора" javaValueClass="String"/>

		<!-- Регистрационный номер контрагента -->
		<field queryFldName="lecm-contract:regNumContractor"
				jrFldName="col_RegnumContractor"
				displayName="Регистрационный номер контрагента" javaValueClass="String"/>

		<!-- Дата регистрации договора -->
		<field queryFldName="lecm-contract:dateRegContracts"
				jrFldName="col_Register"
				displayName="Дата регистрации договора" javaValueClass="date"/>

		<!-- Полное наименование котрагента "lecm-contract:partner-assoc": target "lecm-contractor:contractor-type"
			и пр атрибуты котрагента
		  -->
		<field jrFldName="col_Contragent.name"  queryFldName="{lecm-contract:partner-assoc/lecm-contractor:shortname}" displayName="Контрагент.Короткое"/>
		<field jrFldName="col_Contragent.fullname"  queryFldName="{lecm-contract:partner-assoc/lecm-contractor:fullname}" displayName="Контрагент.Полное наименование"/>
		<field jrFldName="col_Contragent.phone" queryFldName="{lecm-contract:partner-assoc/lecm-contractor:phone}" displayName="Контрагент.Тел" />

		<field jrFldName="col_Contragent.YurAddress" queryFldName="{lecm-contract:partner-assoc/lecm-contractor:legal-address}" displayName="Контрагент.Юридический адрес" />
		<field jrFldName="col_Contragent.PhisAddress" queryFldName="{lecm-contract:partner-assoc/lecm-contractor:physical-address}" displayName="Контрагент.Фактический адрес" />

		<!-- Дата заключения договора -->
		<field jrFldName="col_ConclusionDate" queryFldName="lecm-contract:dateConclusionContracts" displayName="Дата заключения"
			javaValueClass="date"/>

		<!-- Статус -->
		<field jrFldName="col_Status" queryFldName="lecm-statemachine:status" displayName="Статус" />
		<!-- Дата изменения статуса -->
		<field jrFldName="col_StatusDate" queryFldName="lecm-document:status-changed-date" displayName="Дата смены статус"
				javaValueClass="java.util.Date"/>

		<!-- атрибуты для таблицы вложенных файлов -->

		<!-- <Тип документа>
			(изменения к договору, спецификация, протокол согласования/разногласий)

			Dictionary name="Типы связи">:
					<property name="lecm-dic:attributeForShow">cm:name</property>
					<property name="lecm-dic:type">lecm-connect-types:connection-type</property>
		  -->
		<field jrFldName="col_Item.Doctype" queryFldName="{{@list/lecm-connect-types:connection-type}}" displayName="Вложение.ТипДокумента" />

		<!-- Данные по "Тематика договора" (с типом "lecm-contract-dic:contract-subjects") -->
		<field jrFldName="col_Theme.code" queryFldName="{lecm-contract:subjectContract-assoc/lecm-contract-dic:contract-subjects-code}" displayName="Тематика.Код" />
		<field jrFldName="col_Theme.tname" queryFldName="{lecm-contract:subjectContract-assoc/cm:name}" displayName="Тематика.Название" />
		<field jrFldName="col_Theme.desc" queryFldName="{lecm-contract:subjectContract-assoc/lecm-contract-dic:contract-subjects-description}" displayName="Тематика.Описание" />

		<!-- Статус вложенного -->
		<field jrFldName="col_Item.Status" queryFldName="{{@list/lecm-statemachine:status}}" displayName="Вложение.Статус" />
		<!-- Дата изменения статуса вложенного -->
		<field jrFldName="col_Item.StatusDate" queryFldName="{{@list/lecm-document:status-changed-date}}" displayName="Вложение.Дата смены статус"
				javaValueClass="java.util.Date"/>
		<!-- Номер вложенного -->
		<field  jrFldName="col_Item.Regnum"
				queryFldName="{{@list/lecm-document:regnum}}"
				displayName="Регистрационный номер вложенного документа"/>

		<!--
			<field queryFldName="fmydata" dislayName="" javaValueClass="my.check.type"/>
		  -->
	</fields.jasper>
			 * */
			return result;
		}
	}
}
