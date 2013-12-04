package ru.it.lecm.reports.calc;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Контейнерный именованный класс.
 * Группа может подсчитывать связанные характеристики - ключи в attrCounters.
 * Предполагается использовать например для счётчиков статусов, связанных со статусами. 
 * 
 * @author rabdullin
 */
public class DataGroupCounter {
	/**
	 * Тэг данной группы
	 */
	final String groupTag;

	/**
	 * Общая накопленная сумма
	 */
	private Integer total; 

	/**
	 * Счётчики связанные с характеристиками данной группы.
	 */
	final private Map<String, Integer> attrCounters = new LinkedHashMap<String, Integer>();

	public DataGroupCounter(String groupTag) {
		super();
		this.groupTag = groupTag;
	}

	/**
	 * Тэг данной группы
	 */
	public String getGroupTag() {
		return groupTag;
	}

	/**
	 * @return Общая накопленная сумма счётчиков всех атрибутов
	 */
	public Integer sumAll() {
		return total;
	}

	/**
	 * Счётчики связанные с характеристиками данной группы.
	 */
	public Map<String, Integer> getAttrCounters() {
		return attrCounters;
	}

	@Override
	public String toString() {
		return "[group '" + groupTag+ "' (" + attrCounters + ")]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupTag == null) ? 0 : groupTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DataGroupCounter other = (DataGroupCounter) obj;
		if (groupTag == null) {
			if (other.groupTag != null)
				return false;
		} else if (!groupTag.equals(other.groupTag))
			return false;
		return true;
	}

	/**
	 * Увеличить счётчик, связанный с указанным атрибутом, на указанное значение.
	 * Воз-ся увеличенный счётчик.
	 * @param attrName название атрибута
	 * @param delta увеличение
	 */
	public int incCounter(final String attrName, int delta) {
		// корректировка общей суммы
		this.total = delta + ((this.total == null) ? 0 : this.total);

		// корректировка текущей суммы для статуса ...
		Integer counter = null;
		if (this.attrCounters.containsKey(attrName))
			counter = this.attrCounters.get(attrName);
		final int result = delta + ( (counter == null) ? 0 : counter);
		this.attrCounters.put(attrName, result);

		return result;
	}

	/**
	 * Увеличить счётчик, связанный с указанным атрибутом, на единицу.
	 * Воз-ся увеличенный счётчик.
	 * @param attrName название атрибута
	 */
	public int incCounter(final String attrName) {
		return incCounter(attrName, 1);
	}

	/**
	 * Вычислить сумму счётчиков всех атрибутов, которые НЕ перечислены в указанном списке
	 * @param attrMarked контрольная группа имён атрибутов
	 * @return null, если ничего не найдено или суммарное кол-во для статусов вне attrMarked
	 */
	public Integer sumAllOthers(Collection<String> attrMarked) {
		int result = 0;
		for(Map.Entry<String, Integer> e: this.attrCounters.entrySet()) {
			if (!attrMarked.contains(e.getKey())) { // учесть если вне контрольной группы ...
				if (e.getValue() != null)
					result += e.getValue();
			}
		}
		return (result != 0) ? result : null;
	}

	/**
	 * Создать счётчики для указанных атрибутов
	 */
	public void regAttributes( String ... colNames) {
		if (colNames != null)
			for (String name: colNames) {
				if (!this.attrCounters.containsKey(name))
					this.attrCounters.put(name, null);
			}
	}

}
