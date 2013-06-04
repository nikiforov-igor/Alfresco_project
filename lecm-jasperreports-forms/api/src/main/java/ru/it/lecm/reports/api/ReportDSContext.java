package ru.it.lecm.reports.api;

import java.io.Serializable;
import java.util.Map;

import net.sf.jasperreports.engine.JRField;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.base.beans.SubstitudeBean;

/**
 * Контекст выполнения для НД отчёта.
 * Используется во время генерации отчёта: последовательно прогружаются данные
 * по списку, полученному после поиска.
 */
public interface ReportDSContext {

	public void clear();

	/** "well-known" Службы */
	public ServiceRegistry getRegistryService();
	public SubstitudeBean getSubstitudeService();

	/** фильтр ассоциированных данных */
	public AssocDataFilter getFilter();
	public void setFilter(AssocDataFilter filter);

	/**
	 * Карта сопоставления ИМЁН полей из jrxml-шаблона и ОПИСАТЕЛЕЙ полей данных Альфреско.
	 * В общем, здесь описания свойств, доступных для Jasper по именам в jrxml-шаблоне.
	 */
	// TODO: назвать чтобы было понятно, что с чем сопоставляется, наподобии "jasperField2Descriptor"
	public Map<String, JRXField> getMetaFields();

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
	 * например, ключом может быть ссылка JRXField.getValueLink, где JRXField
	 * получен как getMetaFields().get( jrField.getName() ). 
	 */
	public Map<String, Serializable> getCurNodeProps();

	/**
	 * Получить данные для указанного поля отчёта
	 * @param jrField поле jasper-отчёта. Сейчас в этом поле используется только 
	 * название getName().
	 * @return если dsFiller не задан или ссылка есть внутри getCurNodeProps() по-умолчанию, воз-ся объект непосредственно из набора getCurNodeProps(): 
	 *    final JRXField XFld = getMetaFields(jrField.getName()); 
	 *    getCurNodeProps.get( (XFld.getValueLink() != null) ? XFld.getValueLink() : XFld.getName() );
	 * Если провайдер имеет свой dsFiller, то именно он должен будет грузить 
	 * значения непосредственно во время вызова по более сложным правилам провайдера.
	 */
	/* NOTE: например, для получения названия-ключа в curNodeProps может
	 * использоваться схема:
	 *    final JRXField XFld = getMetaFields(jrField.getName()); 
	 *    getCurNodeProps.get( (XFld.getValueLink() != null) ? XFld.getValueLink() : XFld.getName() );
	*/
	public Object getPropertyValueByJRField(JRField jrField);

	// NOTE: возможно потребуется закрепить НД ResultSet, тогда:
	//	/**
	//	 * @return текущая строка данных ответа
	//	 */
	//	public ResultSetRow getRsRow();

}
