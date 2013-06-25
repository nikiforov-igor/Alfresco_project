package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRField;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.AssocDataFilter;
import ru.it.lecm.reports.api.JRXField;
import ru.it.lecm.reports.api.ReportDSContext;

public class ReportDSContextImpl implements ReportDSContext {

	private static final Logger logger = LoggerFactory.getLogger(ReportDSContextImpl.class);

	private ServiceRegistry serviceRegistry;
	final private ProxySubstitudeBean substitudeService = new ProxySubstitudeBean();

	private AssocDataFilter filter; // может быть NULL
	private Map<String, JRXField> metaFields; // ключ = имя колонки данных в jasper

	// список отобранных для Jasper атрибутов Альфреско для активной строки набора данных
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
	 * здесь перечислены имена - с короткими префиксами, доступными для Jasper;
	 * если список null - ограничений на имена не вносятся (и все поля объекта
	 * Фльреско могут использоваться в самом jasper-шаблоне.
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
	public AssocDataFilter getFilter() {
		return filter;
	}

	@Override
	public void setFilter(AssocDataFilter value) {
		this.filter = value;
	}

	@Override
	public Map<String, JRXField> getMetaFields() {
		return metaFields;
	}

	public void setMetaFields(Map<String, JRXField> metaFields) {
		this.metaFields = metaFields;
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

	@Override
	public Object getPropertyValueByJRField(JRField jrField) {
		if (jrField== null)
			return null;

		// получаем нативное название данных
		final JRXField fld = metaFields.get( jrField.getName());
		final String fldAlfName = (fld != null && fld.getValueLink() != null) ? fld.getValueLink() : jrField.getName();
		if (curProps != null) {
			if (curProps.containsKey(fldAlfName))
				return curProps.get(fldAlfName);
		}

		// (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
		// (!) если элемент начинается с "{{", то это спец. элемент, который будет обработан проксёй подстановок.
		if (isCalcField(fldAlfName)) {
			if (substitudeService != null) {
				final Object value = substitudeService.formatNodeTitle(curNodeRef, fldAlfName);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format( "\nData: {%s}\nFound as: '%s'", fldAlfName, value));
				}
				return value;
			}
			logger.warn("(!) substitudeService is NULL -> fld values cannot be loaded");
		}

		return (jrField != null && String.class.equals(jrField.getValueClass())) ? fldAlfName : null; // no value -> return current name if valueClass is String
	}

	/**
	 * ProxySubstitudeBean cейчас просто обходит расширеные выражения для 
	 * функции formatNodeTitle, в дальнейшем можно добавить макросы, встроенные 
	 * функции и пр. 
	 * Расширеный синтаксис маркируется строкой, начинающейся с пары "{{", вместо обычной "{"
	 * 
	 * @author rabdullin
	 * 
	 */
	private class ProxySubstitudeBean implements SubstitudeBean {

		/**
		 *  префикс расширенного синтаксиса
		 *  предполагается что строка вся феликом будет окружена: "{{ ... }}"
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
		protected String extendedFormatNodeTitle(NodeRef node, String fmt) {
			// TODO: here new features can be implemented
			final String begAuthorRef = XSYNTAX_MARKER + PREFIX_XFUNC + AUTHORREF; // "{{@AUTHOR.REF"
			if ( fmt != null && fmt.startsWith( begAuthorRef) ) {
				// замена node на узел Автора 
				final List<NodeRef> list = realBean.getObjectByPseudoProp(node, AUTHOR);
				node = (list != null && !list.isEmpty()) ? list.get(0) : null;

				// убираем из строки fmt одну пару скобок: {{@AUTHOR.REF/...}}  -> {...}
				// можно скорректировать и сделать более точно: fmt = fmt.substring(1, 1+ fmt.indexOf("}}")) + ...;
				int startPos = begAuthorRef.length();
				if (fmt.charAt(startPos) == '/') startPos++; // если после "@AUTHOR.REF" есть символ '/' его тоже убираем
				fmt = "{" + fmt.substring(startPos, fmt.length() - 1); // убираем в начале "{{@AUTHOR.REF/" и последюю скобку
				return realBean.formatNodeTitle(node, fmt);
			}
			return fmt;
		}

	}
}
