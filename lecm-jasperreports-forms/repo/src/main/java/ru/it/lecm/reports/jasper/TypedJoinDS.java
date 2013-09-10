package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.alfresco.service.cmr.search.ResultSetRow;

/**
 * Типизированный НД, в котором строки набора являются контейнерами типа T.
 * Предполагается для реализации "JOIN" возможностей - когда из одной строки 
 * данных базового запроса будет получено ноль,одна или более строк в результате. 
 * @author rabdullin
 *
 */
public abstract class TypedJoinDS<T extends Object> extends AlfrescoJRDataSource{

	private List<T> data;
	private Iterator<T> iterData;

	public TypedJoinDS(Iterator<ResultSetRow> iterator) {
		super(iterator);
		// buildData(); <- не проходит, т.к. нет ещё свойств обвязки (services, metadata и пр)
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Iterator<T> getIterData() {
		return iterData;
	}

	public void setIterData(Iterator<T> iterData) {
		this.iterData = iterData;
	}

	@Override
	public boolean next() throws JRException {
		if (iterData == null) // если ещё не был вызван построитель - вызвать его ...
			buildJoin();
		while (iterData != null && iterData.hasNext()) {
			final T item = iterData.next();
			context.setCurNodeProps( getNodeProps(item)); 
			return true;
		} // while
		// NOT FOUND MORE - DONE
		context.setCurNodeProps(null);
		return false;
	}

	/**
	 * Подготовка карты данных на основании getReportContextProps - замена 
	 * ключей с названий колонок в отчёте на названия атрибутов
	 * @param item
	 * @return
	 */
	private Map<String, Object> getNodeProps(T item) {
		final Map<String, Object> result = new HashMap<String, Object>();

		final Map<String, Serializable> contextProps = getReportContextProps( item);
		if (contextProps != null) {
			for(Map.Entry<String, Serializable> entry: contextProps.entrySet()) {
				result.put(getAlfAttrNameByJRKey(entry.getKey()), entry.getValue());
			}
		}

		return result;
	}

	/**
	 * Выполнить построение результирующего join-списка:
	 * на основе this.context.getRsIter заполняются data и iterData. 
	 * @return количество добавленных значений data (ноль если ничего не найдено)
	 */
	public abstract int buildJoin();

	/**
	 * На основании контейнера item сформировать контйнер с данными для jr
	 * @param item
	 * @return готовый контейнер для context.curNodeProps, 
	 * (!) имена-ключи здесь это имена столбцов в отчёте (далее они будут преобразованы через getAlfAttrNameByJRKey) 
	 */
	protected abstract Map<String, Serializable> getReportContextProps(T item);

}
