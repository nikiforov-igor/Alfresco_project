package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

public class ReportDSContextImpl implements ReportDSContext {

	private static final Logger logger = LoggerFactory.getLogger(ReportDSContextImpl.class);

	private ServiceRegistry serviceRegistry;
	final private ProxySubstitudeBean substitudeService = new ProxySubstitudeBean();

	private DataFilter filter; // может быть NULL
	private Map<String, DataFieldColumn> metaFields; // ключ = имя колонки данных в НД

	// список отобранных для отчёта атрибутов Альфреско для активной строки набора данных
	// ключ = QName.toString() с короткими именами типов (т.е. вида "cm:folder" или "lecm-contract:document")
	private Map<String, Serializable> curProps; // ключ = нативное Альфреско-имя
	private NodeRef curNodeRef;
	private Iterator<ResultSetRow> rsIter;
	private ResultSetRow rsRow;

	public void clear() {
		curProps = null;
		curNodeRef= null;
		rsRow = null;
		rsIter = null;
	}

	/**
	 * список простых gname Альфреско-атрибутов, которые требуются для JR-отчёта, 
	 * здесь перечислены имена - с короткими префиксами, доступными для отчёта;
	 * если список null - ограничений на имена не вносятся (и все поля объекта
	 * Альфреско могут использоваться в самом шаблоне отчёта.
	 */
	private Set<String> jrSimpleProps;

	@Override
	public ServiceRegistry getRegistryService() {
		return serviceRegistry;
	}

	public void setRegistryService(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public SubstitudeBean getSubstitudeService() {
		return substitudeService;
	}

	public void setSubstitudeService(SubstitudeBean substitudeServiceBean) {
		this.substitudeService.setRealBean( substitudeServiceBean);
	}

	public Iterator<ResultSetRow> getRsIter() {
		return rsIter;
	}

	public void setRsIter(Iterator<ResultSetRow> rsIter) {
		this.rsIter = rsIter;
	}

	public ResultSetRow getRsRow() {
		return rsRow;
	}

	public void setRsRow(ResultSetRow rsRow) {
		this.rsRow = rsRow;
	}

	@Override
	public DataFilter getFilter() {
		return filter;
	}

	@Override
	public void setFilter(DataFilter value) {
		this.filter = value;
	}

	@Override
	public Map<String, DataFieldColumn> getMetaFields() {
		return metaFields;
	}

	public void setMetaFields(Map<String, DataFieldColumn> metaFields) {
		this.metaFields = metaFields;
	}

	public void setMetaFields(List<DataFieldColumn> list) {
		final Map<String, DataFieldColumn> result = new HashMap<String, DataFieldColumn>();
		if (list != null) {
			for(DataFieldColumn fld: list)
				result.put( fld.getName(), fld);
		}
		this.metaFields = result;
	}

	@Override
	public Map<String, Serializable> getCurNodeProps() {
		return curProps;
	}

	public void setCurNodeProps(Map<String, Serializable> value) {
		this.curProps = value;
	}

	@Override
	public NodeRef getCurNodeRef() {
		return curNodeRef;
	}

	public void setCurNodeRef(NodeRef curNodeRef) {
		this.curNodeRef = curNodeRef;
	}

	public Set<String> getJrSimpleProps() {
		return jrSimpleProps;
	}

	public void setJrSimpleProps(Set<String> jrSimpleProps) {
		this.jrSimpleProps = jrSimpleProps;
	}

	/**
	 * Проверить является ли указанное поле вычисляемым (в понимании SubstitudeBean):
	 * если первый символ "{", то является.
	 * @param fldName
	 * @return
	 */
	public static boolean isCalcField(final String fldName) {
		return (fldName != null) && fldName.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL);
	}

	/**
	 * Простой ссылкой считаем выражение вида "{abc}"
	 * @return true, если колонка содержит просто ссылку на поле
	 */
	public static boolean isDirectAlfrescoPropertyLink(final String expression) {
		return (expression != null) && (expression.length() > 0)
				&& Utils.hasStartOnce(expression, SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) // одна певая "{"
				&& Utils.hasEndOnce(expression, SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL) // одна последняя "}"
				&& expression.indexOf(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL) == -1 // нету "/"
				;
	}


	@Override
	public Object getPropertyValueByJRField(String reportColumnName) {
		if (reportColumnName == null) {
			return null;
		}

		// получаем нативное название данных
		final DataFieldColumn fld = metaFields.get(reportColumnName);
		final String fldAlfName = (fld != null && fld.getValueLink() != null) ? fld.getValueLink() : reportColumnName;

		/* если название имеется среди готовых свойств (прогруженных или вычисленных заранее) ... */
		if (curProps != null) {
			if (curProps.containsKey(fldAlfName)){
				return curProps.get(fldAlfName);
			}
		}

		// (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
		// (!) если элемент начинается с "{{", то это спец. элемент, который будет обработан проксёй подстановок.
		Object value = null;
		if (isCalcField(fldAlfName)) {
			value = substitudeService.formatNodeTitle(curNodeRef, fldAlfName);
		} else {
			value = fldAlfName;
		}

		if (value == null)
			return null;

		// типизация value согласно описанию ...
		if ((fld != null) && (fld.getValueClass() != null)) {
				// TODO: метод для восстановления реального типа данных ...
				final JavaDataTypeImpl.SupportedTypes type = JavaDataTypeImpl.SupportedTypes.findType(fld.getValueClassName());
				String strValue = value.toString();
				switch (type) {
					case DATE: {
						if (strValue.isEmpty()) {
							value = null;
						} else {
							value = ArgsHelper.tryMakeDate(strValue, null);
						}
						break;
					}
					case BOOL: {
						value = Boolean.valueOf(strValue);
						break;
					}
					case FLOAT: {
						value = (strValue.isEmpty()) ? null : Float.valueOf(strValue);
						break;
					}
					case INTEGER: {
						value = (strValue.isEmpty()) ? null : Integer.valueOf(strValue);
						break;
					}
					default: // case STRING:
					{
						value = strValue;
						break;
					}
				} // switch
		}
		if (value != null)
			return value;

		return (fld != null && String.class.equals(fld.getValueClass())) ? fldAlfName : null; // no value -> return current name if valueClass is String
	}

	/**
	 * ProxySubstitudeBean cейчас отрабатывет расширеные выражения для функции
	 * formatNodeTitle, в дальнейшем можно добавить макросы, встроенные 
	 * функции и пр. 
	 * Расширеный синтаксис выражений маркируется парами "{{" в начале строки и 
	 * закрывающей "}}", вместо обычных "{" и "}".
	 * 
	 * @author rabdullin
	 * 
	 */
	private class ProxySubstitudeBean implements SubstitudeBean {

		/**
		 *  префикс расширенного синтаксиса
		 *  предполагается что строка вся целиком будет окружена: "{{ ... }}"
		 */
		final public static String XSYNTAX_MARKER = "{{";

		/**
		 * префикс доп функции: сейчас используется пока только для @AUTHOR.REF
		 */
		final public static String PREFIX_XFUNC = "@";

		final public static String AUTHORREF = AUTHOR + ".REF";

		SubstitudeBean realBean; // имплементация нативного бина, который организует "хождение" по ссылкам


		public ProxySubstitudeBean() {
			super();
		}

		//		public SubstitudeBean getRealBean() {
		//			return realBean;
		//		}

		public void setRealBean(SubstitudeBean realBean) {
			this.realBean = realBean;
		}

		public String getObjectDescription(NodeRef object) {
			return (realBean == null) ? null : realBean.getObjectDescription(object);
		}

		public String getTemplateStringForObject(NodeRef object) {
			return (realBean == null) ? null : realBean.getTemplateStringForObject(object);
		}

		public String getTemplateStringForObject(NodeRef object, boolean forList) {
			return (realBean == null) ? null : realBean.getTemplateStringForObject(object, forList);
		}

		public List<NodeRef> getObjectsByTitle(NodeRef object,
				String formatTitle) {
			return (realBean == null) ? null : realBean.getObjectsByTitle(object, formatTitle);
		}

		public String formatNodeTitle(NodeRef node, String fmt, String dateFormat, Integer timeZoneOffset) {
			if (fmt == null)
				return null;
			if (isExtendedSyntax(fmt)) {
				return extendedFormatNodeTitle(node, fmt);
			}
			return (realBean == null) ? null : realBean.formatNodeTitle(node, fmt, dateFormat, timeZoneOffset);
		}

		@Override
		public String formatNodeTitle(NodeRef node, String formatString) {
			return formatNodeTitle(node, formatString, null, null);
		}

		protected boolean isExtendedSyntax(String fmt) {
			return (fmt != null) && fmt.startsWith(XSYNTAX_MARKER);
		}

		@Override
		public List<NodeRef> getObjectByPseudoProp(NodeRef object,
				String psedudoProp) {
			return (realBean == null) ? null : realBean.getObjectByPseudoProp(object, psedudoProp);
		}

		/**
		 * Функция расширенной обработки. Вызывается когда выражение начинается 
		 * с двойной фигурной скобки. Здесь сейчас отрабатывает дополнительно
		 * только @AUTHOR.REF, чтобы выполнить получение автора и применить к 
		 * нему отсавшуюся часть выражения.
		 * @param node
		 * @param fmt
		 * @return
		 */
		protected String extendedFormatNodeTitle(final NodeRef node, final String fmt) {
			// NOTE: here new features can be implemented
			final String begAuthorRef = XSYNTAX_MARKER + PREFIX_XFUNC + AUTHORREF; // "{{@AUTHOR.REF"
			if ( fmt != null && fmt.startsWith( begAuthorRef) ) {
				// замена node на узел Автора 
				final List<NodeRef> list = realBean.getObjectByPseudoProp(node, AUTHOR);
				final NodeRef authNode = (list != null && !list.isEmpty()) ? list.get(0) : null;

				// убираем из строки fmt одну пару скобок: {{@AUTHOR.REF/...}}  -> {...}
				// скорректировать и сделать более точно можно при условии скана ВСЕХ вхождений @XXX:
				// while (...) {fmt = fmt.substring(1, 1+ fmt.indexOf("}}")) + ...; ...}
				int startPos = begAuthorRef.length();
				if (fmt.charAt(startPos) == '/') startPos++; // если после "@AUTHOR.REF" есть символ '/' его тоже убираем
				// убираем в начале "{{@AUTHOR.REF/" и последюю скобку ...
				final String shortFmt = "{" + fmt.substring(startPos, fmt.length() - 1);
				return realBean.formatNodeTitle(authNode, shortFmt);
			}
			return fmt;
		}

	}
}
