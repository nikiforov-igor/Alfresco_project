package ru.it.lecm.utils;

import java.util.Date;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

import ru.it.lecm.reports.utils.Utils;


/**
 * Утилитарный класс для построения lucene-поисковых запросов.
 * 
 * @author rabdullin
 *
 */
public class LuceneSearchBuilder {

	private StringBuilder bquery;
	private NamespaceService nameService;

	public LuceneSearchBuilder() {
		super();
	}

	public LuceneSearchBuilder(NamespaceService nameService) {
		this(nameService, null);
	}

	public LuceneSearchBuilder(NamespaceService nameService, StringBuilder bquery) {
		super();
		this.nameService = nameService;
		this.bquery = bquery;
	}

	public void clear() {
		this.bquery = null;
	}

	public boolean isEmpty() {
		return (this.bquery == null) || (this.bquery.length() == 0);
	}

	@Override
	public String toString() {
		return getQuery().toString();
	}

	public NamespaceService getNameService() {
		return nameService;
	}

	public void setNameService(NamespaceService nameService) {
		this.nameService = nameService;
	}

	public StringBuilder getQuery() {
		if (bquery == null)
			bquery = new StringBuilder(); 
		return bquery;
	}

	public void setQuery(StringBuilder bquery) {
		this.bquery = bquery;
	}

	/**
	 * Добавить условие для проверки по типу
	 * @param typeName требующийся тип, если NULL ничего не добавляется
	 * @param prefix префикс, добавляемый перед условием на тип  (например " AND")
	 * , может быть NULL
	 * @return true, если условие было добавлено
	 */
	public boolean emmitTypeCond(final String typeName, final String prefix) {
		if (typeName == null)
			return false;
		PropertyCheck.mandatory(this, "nameService", getNameService() );
		final QName qType = QName.createQName( typeName, this.getNameService());
		return emmitTypeCond(qType, prefix);
	}

	/**
	 * Добавить условие для проверки по типу
	 * @param qType требующийся тип, если NULL ничего не добавляется
	 * @param prefix префикс, добавляемый перед условием на тип  (например " AND")
	 * , может быть NULL
	 * @return true, если условие было добавлено
	 */
	public boolean emmitTypeCond(final QName qType, final String prefix) {
		if (qType == null)
			return false;
		if (prefix != null)
			getQuery().append( prefix);
		getQuery().append( " TYPE:"+ Utils.quoted(qType.toString()));
		return true;
	}

	public boolean emmitTypeCond(final QName qType) {
		return emmitTypeCond(qType, null);
	}


	/**
	 * Добавить провеку на соот-вие id узла
	 * @param nodeRef узел, если NULL ничего не добавляется
	 * @param prefix префикс, добавляемый перед условием на тип  (например " AND")
	 * , может быть NULL
	 * @return true, если условие было добавлено
	 */
	public boolean emmitIdCond(final NodeRef nodeRef, final String prefix) {
		if (nodeRef == null)
			return false;
		return emmitIdCond(nodeRef.toString(), prefix);
	}

	public boolean emmitIdCond(final NodeRef nodeRef) {
		return emmitIdCond(nodeRef, null);
	}

	/**
	 * Добавить провеку на соот-вие id узла
	 * @param sNodeRef ref строка узла, если NULL ничего не добавляется
	 * @param prefix префикс, добавляемый перед условием на тип  (например " AND")
	 * , может быть NULL
	 * @return true, если условие было добавлено
	 */
	public boolean emmitIdCond(final String sNodeRef, final String prefix) {
		if (sNodeRef == null)
			return false;
		if (prefix != null)
			getQuery().append( prefix);
		getQuery().append( " ID:"+ Utils.quoted(sNodeRef));
		return true;
	}

	/**
	 * Выполнить вставку условия для проверки равенства поля указанной константе.
	 * Экранированные кавычки для значения добавляются автоматически.
	 * @param prefix вставляется перед сгенерированным условием, если оно будет получено
	 * @param fld ссылка на поле (экранирование '-' и ':' не требуется)
	 * @param value значение или Null (генерации не будет в этом случае)
	 * @return true, если условие было добавлено
	 */
	public boolean emmitFieldCond( final String prefix, final String fld, Object value) {
		if (value == null)
			return false;
		if (prefix != null)
			getQuery().append( prefix);
		getQuery()
			.append(" @")
			.append(Utils.luceneEncode(fld))
			.append(":\"") // bquery.append( " AND @cm\\:creator:\"" + login + "\"");
			.append( value.toString()) // Utils.luceneEncode(value.toString())
			.append("\"");
		return true;
	}

	/**
	 * Выполнить вставку условия для проверки нахождения числа внутри интервала
	 * @param prefix вставляется перед сгенерированным условием, если оно будет получено
	 * @param fld ссылка на поле (экранирование '-' и ':' не требуется)
	 * @param lower граница снизу или Null
	 * @param upper граница сверху или Null
	 * @return true, если условие было добавлено (т.е. если задана хотя бы одна граница)
	 */
	public boolean emmitNumericIntervalCond( final String prefix, final String fld, Number lower, Number upper) {
		final String cond = Utils.emmitNumericIntervalCheck( Utils.luceneEncode(fld), lower, upper);
		if (cond == null)
			return false;
		if (prefix != null)
			getQuery().append( prefix);
		getQuery().append(" ").append(cond);
		return true;
	}


	/**
	 * Выполнить вставку условия для проверки нахождения даты внутри интервала
	 * @param prefix вставляется перед сгенерированным условием, если оно будет получено
	 * @param fld ссылка на поле (экранирование '-' и ':' не требуется)
	 * @param after дата после которой или Null
	 * @param before дата перед которой или Null
	 * @return true, если условие было добавлено (т.е. если задана хотя бы одна дата)
	 */
	public boolean emmitDateIntervalCond( final String prefix, final String fld, Date after, Date before) {
		final String cond = Utils.emmitDateIntervalCheck( Utils.luceneEncode(fld), after, before);
		if (cond == null)
			return false;
		if (prefix != null)
			getQuery().append( prefix);
		getQuery().append(" ").append(cond);
		return true;
	}

}

