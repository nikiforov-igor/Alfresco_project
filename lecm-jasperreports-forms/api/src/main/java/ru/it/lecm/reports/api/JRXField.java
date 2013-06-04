package ru.it.lecm.reports.api;

import net.sf.jasperreports.engine.design.JRDesignField;

/**
 * JRXField это jasper-поле + дополнительные поля.
 * JRXField может содержать:
 *    - описание простого поля (значение которого имеется непосредствено в текущем объекте авктивной строки отчёта),
 *    - "длинные" ссылки, обрабатываемые службой SubstitudeBean (вида "Axxx/Bxxx/.../Fldxxx"), 
 *    - custom-поля, заполняемые непосредственно набором данных конкретного провайдера. 
 * @author Ruslan
 */
// TODO: сделать опции (представление NULL и пр) либо поотдельности, либо Map-списком (хуже, т.к. опции станут неявными) 
public class JRXField extends JRDesignField {

	private static final long serialVersionUID = 1L;

	private String valueLink;

	public JRXField() {
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

	@Override
	public String toString() {
		return String.format("%s(link: %s, '%s')", super.getName(), valueLink, super.getDescription());
	}
}

