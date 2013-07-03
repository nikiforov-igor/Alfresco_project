package ru.it.lecm.reports.generators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.search.SearchParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.jasper.utils.Utils;
import ru.it.lecm.reports.model.ColumnDescriptorImpl;

/**
 * Запрос под Lucene Альфреско:
 *    1) сам текст 
 *    2) search-структура
 *    3-4) списки целевых атрибутов (и параметров) непосредственных и ссылочных
 * @author rabdullin
 *
 */
public class LucenePreparedQuery {

	/* тип объектов по-умолчанию (когда не указано явно) */
	public static final String DEFAULT_DOCUMENT_TYPE = "lecm-document:base";

	private static final Logger logger = LoggerFactory.getLogger(LucenePreparedQuery.class);

	// текст Lucene запроса с условиями от простых параметров
	private String luceneQueryText;

	// поисковый запрос
	private SearchParameters alfrescoSearch;

	// колонки простые - с именем свойств  
	final private List<ColumnDescriptor> argsByProps = new ArrayList<ColumnDescriptor>();

	// колонки со сложными условиями (доступ к данных через ассоциации)
	final private List<ColumnDescriptor> argsByLinks = new ArrayList<ColumnDescriptor>();

	/** колонки со сложными условиями (доступ к данным через ассоциации) */
	public List<ColumnDescriptor> argsByLinks() { return this.argsByLinks; }

	/** колонки с простыми условиями (доступ к данным непосредственно по именам свойств Альфреско) */
	public List<ColumnDescriptor> argsByProps() { return this.argsByProps; }

	/** текущий текст запроса */
	public String luceneQueryText() { return this.luceneQueryText; }

	/** поисковый запрос */
	public SearchParameters alfrescoSearch() { 
		return this.alfrescoSearch;
	}

	public void setAlfrescoSearch(SearchParameters search) {
		this.alfrescoSearch = search;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("LuceneQuery [");

		builder.append("\n\t\t argsByProps ");
		builder.append( Utils.getAsString(argsByProps));

		builder.append("\n\t\t, argsByLinks ");
		builder.append( Utils.getAsString(argsByLinks));

		builder.append("\n\t\t, alfrescoSearch=");
		builder.append(alfrescoSearch);
		
		builder.append("\n\t\t, luceneQueryText:");
		builder.append("\n\t>>>\n").append(luceneQueryText).append("\n\t<<<");

		builder.append("\n\t ]");
		return builder.toString();
	}

	/**
	 * Сгенерить текст Lucene-запроса с учётом имеющихся простых параметров
	 * и создать список сложных параметров.
	 * Выполняется также проверка заполнения обязательных параметров, с поднятием исключений.
	 * @param reportDescriptor
	 * @return
	 */
	public static LucenePreparedQuery prepareQuery(final ReportDescriptor reportDescriptor) {

		final LucenePreparedQuery result = new LucenePreparedQuery();

		final StringBuilder bquery = new StringBuilder();

		final StringBuilder blog = new StringBuilder(); // для журналирования
		int iblog = 0;

		boolean hasData = false; // true становится после внесения первого любого условия в bquery

		result.argsByLinks.clear();
		result.argsByProps.clear();

		/* создаём базовый запрос:  по ID или TYPE */
		makeMasterCondition(bquery, reportDescriptor);
		hasData = true;

		/* 
		 * проход по параметрам, которые являются простыми - и включение их в 
		 * выражение поиска, другие параметры надо будет проверять после загрузки
		 * в фильтре данных
		 */
		// TODO: проход по простым параметрам
		for(ColumnDescriptor colDesc: reportDescriptor.getDsDescriptor().getColumns()) {

			if (colDesc.getParameterValue() == null
					|| Utils.isStringEmpty( colDesc.getExpression())
				) // не параметр ...
				continue;

			if (!ColumnDescriptorImpl.isMapped2ImmediateProperty(colDesc)) { // составной параметр (ассоциация или функция) 
				if ( !Utils.isStringEmpty(colDesc.getExpression()) )
					result.argsByLinks.add(colDesc); // сложный параметр будем проверять позже - в фильтре данных
				continue;
			}

			/* здесь colDesc содержит простой параметр ... */
			result.argsByProps.add(colDesc);

			// параметр пустой?  обязательный ? ...
			if (colDesc.getParameterValue().isEmpty()) {
				if (colDesc.getParameterValue().isRequired()) // пустой и обязательный - это криминал ...
					throw new RuntimeException( String.format( 
							"Required parameter '%s' must be spesified '%s' (data column '%s')"
							, ParameterMapper.getArgRootName(colDesc)
							, colDesc.getColumnName()
							));
				continue; // Если нет условия для необязательного параметра - просто его пропускаем
			}

			// экранированное имя с именем поля для поиска в Lucene
			final String luceneFldName = Utils.luceneEncode(colDesc.getExpression());

			/*
			 *  граничные значения для поиска. По-идее здесь, после проверки 
			 *  isEmpty(), для LIST/VALUE нидняя граница не пустая, а для
			 *  RANGE - одна из границ точно не пустая
			*/
			final Object bound1 = colDesc.getParameterValue().getBound1()
					, bound2 = colDesc.getParameterValue().getBound2();

			final String cond; // сгенерированное условие

			/* генерим условие поиска - одно значение или интервал ... */
			switch(colDesc.getParameterValue().getType()) {

			/*
			1) экранировка символов в полном имени поля: ':', '-' 
			2) кавычки для значения
			3) (для LIST)  что-то для списка элементов (посмотреть синтаксис люцена)

			4) при подстановке дат надо их форматировать
				если надо чётко указать формат, его можно предусмотреть в 
				описателе колонки - для самой колонки и для параметра
			 */
			case VALUE:
			case LIST: // TODO: сгенерить запрос для списка (LIST) полное условие со всеми значениями
				// пример формируемой строки: bquery.append( " AND @cm\\:creator:\"" + login + "\"");
				final String value = Utils.quoted(bound1.toString());
				cond = " @"+ luceneFldName + ":" + value;
				break;

			case RANGE:
				/*
				проверить тип значения фактических значений параметра: 
						для дат вызывать emmitDate 
						для чисел (и строк) emmitNumeric
				 */
				final boolean isArgDate = (bound1 instanceof Date) || (bound2 instanceof Date);
				if (isArgDate) {
					cond = Utils.emmitDateIntervalCheck(luceneFldName, (Date) bound1, (Date) bound2);
				} else {
					cond = Utils.emmitNumericIntervalCheck(luceneFldName, (Number) bound1, (Number) bound2);
				}
				break;

			default: // непонятный тип - сообщение об ошибке и игнор ...
				cond = null;
				logger.error( String.format("Unsupported parameter type '%s' skipped", Utils.coalesce(colDesc.getParameterValue().getType(), "NULL") ));
				break;
			}

			if (cond != null) {
				bquery.append( (hasData ? " AND" : "")+ cond);
				hasData = true;

				iblog++;
				blog.append( String.format("\t[%d]\t%s\n", iblog, cond));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug( String.format("Quering nodes by Lucene conditions:\n%s\n" , blog.toString() ));
		}

		result.luceneQueryText = bquery.toString();
		return result;
	}

	/**
	 * Вставить основу запроса this.reportDescriptor: выборку по типу или по ID,
	 * в зависимости от isMultiRow().
	 * При отстутсвии параметров поднимается исключение.
	 * @param bquery
	 * @param reportDescriptor 
	 */
	private static void makeMasterCondition(final StringBuilder bquery, ReportDescriptor reportDescriptor) {
		if ( reportDescriptor != null && reportDescriptor.getDsDescriptor() != null) {
			// @NOTE: (reportDescriptor.getFlags().isMultiRow()) не достаточно для определения того что именно долждно проверяться TYPE или ID
			// так что выбираем оба значения

			// по типу ...
			final boolean hasType = Utils.emmitParamCondition(
						bquery
						, reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_TYPE)
						, "TYPE:"
						// , String.format( "Multi-row query parameter '%s' must be specified", DataSourceDescriptor.COLNAME_TYPE)
				);
			// по ID/NodeRef ...
			ColumnDescriptor colWithID = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_ID);
			if (colWithID == null)
				colWithID = reportDescriptor.getDsDescriptor().findColumnByParameter(DataSourceDescriptor.COLNAME_NODEREF);
			final boolean hasId = Utils.emmitParamCondition(
						bquery
						, colWithID
						, "ID:"
						// , String.format( "Single-row query parameter '%s'/'%s' must be specified", DataSourceDescriptor.COLNAME_ID, DataSourceDescriptor.COLNAME_NODEREF)
				);

			if (!(hasType || hasId))
				logger.warn( String.format("None of main parameteres specified: '%s' nor '%s'/'%s' "
						, DataSourceDescriptor.COLNAME_TYPE, DataSourceDescriptor.COLNAME_ID, DataSourceDescriptor.COLNAME_NODEREF) );
		} else { // если НД не задан - выборка по документам ...
			bquery.append( "TYPE:"+ Utils.quoted(DEFAULT_DOCUMENT_TYPE));
		}
	}

}
