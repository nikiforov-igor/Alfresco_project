package ru.it.lecm.reports.api;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.design.JRDesignField;
import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.NamedValue;

/**
 * DataFieldColumn это jasper-поле + дополнительные поля.
 * Может содержать:
 *    - описание простого поля (значение которого имеется непосредствено в текущем объекте авктивной строки отчёта),
 *    - "длинные" ссылки, обрабатываемые службой SubstitudeBean (вида "Axxx/Bxxx/.../Fldxxx"), 
 *    - custom-атрибуты (attributes), заполняемые непосредственно набором данных конкретного провайдера. 
 * @author Ruslan
 */
// TODO: сделать опции (представление NULL и пр) либо поотдельности, либо Map-списком (хуже, т.к. опции станут неявными) 
public class DataFieldColumn extends JRDesignField {

	private static final long serialVersionUID = 1L;

	private String valueLink;
	private Map<String, String> attributes; // дополнительные флаги или атрибуты

	private boolean enUseAttributesTrim = true; // применять trim для значений атрибутов

	public DataFieldColumn() {
		super();
	}

	/**
	 * Строка для получения значения через ассоциации или просто qname-название поля
	 * @return
	 */
	public String getValueLink() {
		return valueLink;
	}

	public void setValueLink(String valueLink) {
		this.valueLink = valueLink;
	}

	/**
	 * @return если применяется trim при вставке значений в список attributes.
	 */
	public boolean isEnUseAttributesTrim() {
		return enUseAttributesTrim;
	}

	/**
	 * @param enableTrim true, чтобы применять trim при вставке значений в список flags.
	 */
	public void setEnUseAttributesTrim(boolean enableTrim) {
		this.enUseAttributesTrim = enableTrim;
	}

	/**
	 * @return дополнительные флаги или атрибуты в мета-описании поля
	 * (!) сохраняется порядок внесения флагов 
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @return true, если имеются описания доп-ныз флагов, атрибутов
	 */
	public boolean hasXAttributes() {
		return (attributes != null) && (attributes.size()>0);
	}

	/**
	 * Зарегить (или обновить значение) очередной флаг
	 * @param name название
	 * @param value значение
	 */
	public void addFlag(final String name, final String value) {
		if (this.attributes == null)
			this.attributes = new LinkedHashMap<String, String>();
		this.attributes.put(name, ( (!enUseAttributesTrim || value == null) ? value : value.trim()) );
	}

	/**
	 * Получить значение флага
	 * @param name название флага
	 * @param defaultValue значение если флага нет или его значение NULL
	 * @return найденное значение флага (если оно не NULL) или defaultValue, иначе.
	 */
	public String getFlag(final String name, final String defaultValue) {
		final String found = (attributes != null && attributes.containsKey(name)) ? attributes.get(name) : null;
		return (found != null) ? found : defaultValue;
	}

	@Override
	public String toString() {
		return String.format( "%s(link: %s, '%s' %s)", super.getName(), valueLink
				, super.getDescription(), (attributes == null ? "" : attributes.toString())
		);
	}

	public static DataFieldColumn createDataField(ColumnDescriptor colDesc) {
		if (colDesc == null)
			return null;
		final DataFieldColumn result = new DataFieldColumn();
		result.setName( colDesc.getColumnName());
		result.setDescription( colDesc.get(null, null));
		result.setValueLink(colDesc.getExpression());
		try {
			result.setValueClass( Class.forName(colDesc.className()) );
		} catch (ClassNotFoundException ex) {
			final String msg = String.format( "Column '%s' has invalid value class type: '%s' "
					, colDesc.getColumnName(), colDesc.className());
			// logger.error(msg, ex);
			throw new UnsupportedOperationException( msg, ex);
		}

		if (colDesc.flags() != null) {
			for(NamedValue nv: colDesc.flags()) {
				result.addFlag(nv.getMnem(), nv.getValue());
			}
		}
		return result;
	}
}

