package ru.it.lecm.reports.api.model;

import java.util.Map;
import java.util.Set;


/**
 * <b>Описатель подотчёта</b>:
 *  	<li>название,</li>
 *  	<li>колонки данных,</li>
 *  	<li>исходная ассоциация в основном НД.</li>
 * <br/> Сейчас обеспечивается принцип <b>"локальности" </b> - т.е. всё что 
 * касается подотчёта описано только в нём тут, <b>без внешних ссылок </b>.
 * Мнемоника отчёта это фактически служебное название подотчёта.
 */
/*
 * TODO: продумать стоит ли разрешить делать ссылки в качестве подотчётов на 
 * другие реальные отчёты. В этом случае в провайдере построения отчётов 
 * надо будет предусмотреть достаточно хитрый механизм для формирования 
 * подотчётов - с запуском именно тех провайдеров, которые прописаны для 
 * подотчётов и продумать как передавать параметры для них (тут могут быть 
 * колонки из основного отчёта и также исходные аргументы основного).
 * 
 */
// TODO: сделать отнаследованным от обычного ReportDescriptor
public interface SubReportDescriptor extends ReportDescriptor
{

	// reportdesc
	/** название свойства бина для присвоения строкового номера */
	public static final String BEAN_PROPNAME_COL_ROWNUM = "colRownum";

	/**
	 * значение класса бина для случая использования простого форматирования
	 * (вместо формирования списка бинов)
	 */ 
	public static final String VAL_BEANCLASS_BYFORMAT = "byFormat";

	/**
	 * @return источник получения списка вложенных объектов для подотчёта.
	 * <br/> Обычно это исходная ассоциация в объкете главного набора данных,
	 * из которой надо будет получить список вложений.
	 */
	String getSourceListExpression();
	void setSourceListExpression(String expression);


    /**
     * @return тип вложенных объектов для подотчёта.
     * <br/> Это QName в короткой форме
     */
    Set<String> getSourceListType();
    void setSourceListType(Set<String> types);

	/** 
	 * Имя целевой колонки в основном НД. 
	 * <br/> Её типом будет либо List<ObjectOfBeanClass>, либо строка, когда 
	 * используется форматирование.
	 */
	String getDestColumnName();
	void setDestColumnName(String columnName);

	/**
	 * Класс бина или Map, который должен обеспечить хранение строки подотчёта.
	 * Если не указан, то предполагается "java.util.Map".
	 * @return
	 */
	String getBeanClassName();
	void setBeanClassName(String beanClassName);

	/**
	 * @return <li> true, если ассоциированный список должен быть отформатирован
	 * форматной строкой, 
	 * <li>и false, если представлен как список бинов класса beanClass.
	 * <br/>Фактически класс бина имеет более высокий приоритет. 
	 * <br/>Пустой класс или равный сигнатуре {@link #VAL_BEANCLASS_BYFORMAT} 
	 * (вне зависимости от регистра) означают исопльзование форматирования. 
	 */
	boolean isUsingFormat();

	/**
	 * @return "владелец": описатель основного отчёта, к которому относится данный подотчёт
	 */
	ReportDescriptor getOwnerReport();
	void setOwnerReport(ReportDescriptor ownerReport);

	/**
	 * Описатель форматирования элементов, когда их требуется представить в виде
	 * одной строки.
	 * <br/> Если null, то вложенные элементы представляются как List бинов/мапов.
	 */
	ItemsFormatDescriptor getItemsFormat();
	void setItemsFormat(ItemsFormatDescriptor itemsFmt);

	/**
	 * map xml конфигурация для получения свойств для суб-элементов.
	 * <li>   Ключи - названия колонок из dsDescriptor.columns (фактически 
	 * станут свойствами бина или ключами в карте свойств подобъекта), 
	 * </li>
	 * <li>   Значения = список атрибутов Альфреско (перечисляются через запятые 
	 * или точки с запятой) из ссылок на поля данных относительно суб-объекта.
	 * <br/> Пример значения:
	 * <br/>		{!-- Дата Задачи: Дата начала исполнения задачи или дата создания --}
	 * <br/>		{item key="colErrandDate"} {![CDATA[lecm-errands:start-date, cm:created]]} {/item}
	 * </li>
	 * <b>
	 * <br/> Колонки, которые описаны здесь, обязаны иметься в dsDescriptor, но не наоборот.
	 * <br/> Если колонка данных из dsDescriptor здесь не упоминается, то её 
	 * источником является expression самой колонки.
	 * </b>
	 * <br/> Отдельные ссылки задаются либо как названия вида "cm:name", либо как
	 * ассоциативные пути вида "{t1:my-accoc1/t2:my-assoc2/.../t3:myfield}".
	 * 
	 * <br/> Если надо включить все объекты по ассоциации единой строкой, то имя 
	 * надо пометить '*':
	 *    <item key="*colCoExecutorsList"> <![CDATA[ ...путь ]]> </item>
	 * а сама ассоциация имеет обычный вид (выборка списка будет выполнена по первой 
	 * ассоциации в пути)
	 * 
	 * NOTE: надо осторожно передавать тип бина, так как класс должен иметься в сборке.
	 */
	Map<String, String> getSubItemsSourceMap();
	void setSubItemsSourceMap(Map<String, String> map);


	/**
	 * Описатель для форматирования списка элементов в строку.
	 */
	public interface ItemsFormatDescriptor {

		/**
		 * Маркер перед именем свойства бина для случая когда требуется 
		 * формирование одной строки для всего списка вложенных элементов.
		 * <b><br/> Если маркета нет, то из списка будет браться только первый элемент.</b> 
		 */
		public static final String LIST_MARKER = "*";

		/**
		 * @return форматная строка для объединения значений колонок НД dsSourceDescriptor
		 */
		String getFormatString(); 
		void setFormatString(String formatString);

		/**
		 * @return значение, которое надо выводить если список вложенных 
		 * объектов пуст, по-умолчанию пустая строка.
		 */
		String getIfEmptyTag(); 
		void setIfEmptyTag(String tag);

		/**
		 * Разделитель элементов в списке, если используется форматирование 
		 * всех элементов в одну строку
		 */
		String getItemsDelimiter();
		void setItemsDelimiter(String delimiter);
	}


}