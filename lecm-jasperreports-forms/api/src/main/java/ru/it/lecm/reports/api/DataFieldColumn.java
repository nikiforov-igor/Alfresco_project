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
	 */
	public String getValueLink() {
		return valueLink;
	}

	public void setValueLink(String valueLink) {
		this.valueLink = valueLink;
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

	@Override
	public String toString() {
		return String.format( "%s(link: %s, '%s' %s)", super.getName(), valueLink
				, super.getDescription(), (attributes == null ? "" : attributes.toString()));
	}

	public static DataFieldColumn createDataField(ColumnDescriptor colDesc) {
		if (colDesc == null) {
            return null;
        }

		final DataFieldColumn result = new DataFieldColumn();
		result.setName( colDesc.getColumnName());
		result.setDescription( colDesc.getDefault());

		/* значение ... */
		if (colDesc.getParameterValue() != null) {
			// если параметризован ...
			if (colDesc.getParameterValue().getBound1() != null) {
                result.setValueLink( String.format( "%s", colDesc.getParameterValue().getBound1()) );
            } else {
                result.setValueLink( colDesc.getExpression());
            }
		} else { // не параметр ...
			result.setValueLink( colDesc.getExpression());
		}

		/* тип ... */
		try {
			if (colDesc.getDataType() != null && colDesc.getDataType().getClassName() != null) {
				result.setValueClass( Class.forName(colDesc.getDataType().getClassName()) );
			} else {
				result.setValueClass( Class.forName(colDesc.getClassName()) );
			}
		} catch (ClassNotFoundException ex) {
			final String msg = String.format( "Column '%s' has invalid value class type: '%s' "
					, colDesc.getColumnName(), colDesc.getClassName());
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

