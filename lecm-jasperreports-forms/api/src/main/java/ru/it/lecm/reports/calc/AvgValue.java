package ru.it.lecm.reports.calc;

/**
 * Структура для накопления и хранения средних значений некоторой величины
 */
public class AvgValue {

	private String tag; // название
	private int count; // кол-во
	private float avg; // текущее среднее

	public AvgValue(String tag) {
		super();
		this.tag = tag;
	}

	public AvgValue() {
	}

	@Override
	public String toString() {
		return String.format( "Avg(%s) [count %d, avg %s]", tag, count, avg);
	}

	/**
	 * Название данной переменной
	 */
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return количество
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return текущее среднее
	 */
	public float getAvg() {
		return avg;
	}
	public void clear() {
		count = 0;
		avg = 0;
	}

	/**
	 * Скорректировать среднее значение с учётом очердного "замера"
	 */
	public void adjust(float value) {
		if (++count == 1) { // первая порция данных
			avg = value;
		} else { // корректировка ср значения
			avg = (avg * (count - 1) + value)/count;
		}
	}
}

