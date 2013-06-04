package ru.it.lecm.reports.api;

import net.sf.jasperreports.engine.JRField;

/**
 * Интерфейс для получения данных для нужных полей.
 * @author rabdullin
 */
public interface ReportDSFiller {

	/**
	 * Получить (догрузить) список атрибутов Альфреско для активной строки набора данных
	 * (см context.getCurNodeProps(), context.getCurNodeRef()).
	 * Map-таблица context.getCurNodeProps() в качестве ключа использует значения 
	 * согласованные с методом получения данных getPropertyValueByJRField.
	 * Стандартно принимается, что ключом будет либо название атрибута узла 
	 * (когда атрибут принадлежит непосредственно узлу), либо (когда атрибут это 
	 * ссылка) значение из JRXField.getValueLink, где JRXField получено как  
	 * ReportDSFiller.getMetaFields().get( имя поля в отчёте).
	 */
	public void prepareCurNodeProps(ReportDSContext context);

	/**
	 * Получить данные для указанного поля отчёта. Метод вызывается после
	 * поиска значения соот-го jrField внутри context.getCurNodeProps(), если
	 * ключа там не оказалось.
	 * @param jrField поле jasper-отчёта, здесь сейчас имеет роль только название 
	 * getName(), остальное не используется.
	 * @return как-правило, здесь выполняется загрузка значения, соответствующего
	 * полю отчёта jrField, по правилам данного провайдера.
	 */
	/* NOTE: сейчас для получения значения соот-го jrField из curNodeProps
	 * используется схема:
	 *    // формирование ключа данных ...
	 *    final JRXField XFld = getMetaFields( jrField.getName());
	 *    final String dataKey = (XFld.getValueLink() != null) ? XFld.getValueLink() : XFld.getName();
	 *    // получение данных по ключу ...
	 *    Object resultObj = null; 
	 *    if (getCurNodeProps.contains(dataKey))
	 *       resultObj = getCurNodeProps.get( dataKey);
	 *    else if (<reportDSFiller> != null)
	 *       resultObj = <reportDSFiller>.getPropertyValueByJRField(jrField, context);
	 *    return resultObj;
	*/
	public Object getPropertyValueByJRField(JRField jrField, ReportDSContext context);

}
