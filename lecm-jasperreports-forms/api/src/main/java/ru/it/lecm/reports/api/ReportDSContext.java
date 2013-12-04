package ru.it.lecm.reports.api;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Map;

/**
 * Контекст выполнения для НД отчёта.
 * Используется во время генерации отчёта: последовательно прогружаются данные
 * по списку, полученному после поиска.
 */
public interface ReportDSContext {

	public void clear();

	/** "well-known" Службы */
	public ServiceRegistry getRegistryService();

	/** фильтр ассоциированных данных */
	public DataFilter getFilter();
	public void setFilter(DataFilter filter);

	/**
	 * Карта сопоставления ИМЁН полей из шаблона отчёта к ОПИСАТЕЛЯМ полей данных Альфреско.
	 * ключ = название колонки отчёта (короткое название) или ссылка на поле Альфреско в виде "xx:yy".
	 */
	public Map<String, DataFieldColumn> getMetaFields();

	/**
	 * Id текущего узла
	 */
	public NodeRef getCurNodeRef();

	/**
	 * Список атрибутов Альфреско, загруженных для активной строки набора данных.
	 * Ключ здесь согласован с параметром jrField метода получения данных 
	 * getPropertyValueByJRField и набором getMetaFields по следующей схеме:
	 *    1) если поле непосредственно принадлжит активному узлу, ключом будет
	 * alfresco-название атрибута с типом, заданным в короткой форме (т.е. вида 
	 * "cm:folder" или "lecm-contract:document")
	 *    2) если поле это ссылка или что-то иное, тогда ключом будет такое  
	 * значение, которое "понятно" для метода ReportDSFiller.getPropertyValueByJRField:
	 * например, ключом может быть ссылка DataFieldColumn.getValueLink,
	 * где DataFieldColumn получен как getMetaFields().get( jrField.getName() ). 
	 */
	public Map<String, Object> getCurNodeProps();

	/**
	 * Получить данные для указанного поля отчёта
	 * @param reportColumnName колонка отчёта
	 * @return если dsFiller не задан или ссылка есть внутри getCurNodeProps(),
	 * тогда по-умолчанию, воз-ся объект непосредственно из набора getCurNodeProps(): 
	 *    final DataFieldColumn XFld = getMetaFields(reportColumnName); 
	 *    getCurNodeProps.get( (XFld.getValueLink() != null) ? XFld.getValueLink() : XFld.getName() );
	 * Если провайдер имеет свой dsFiller, то именно он должен будет грузить 
	 * значения непосредственно во время вызова по более сложным правилам провайдера.
	 */
	public Object getPropertyValueByJRField(String reportColumnName);

}
