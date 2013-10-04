package ru.it.lecm.reports.beans;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl;
import ru.it.lecm.reports.utils.ArgsHelper;

/**
 * Вспомогательный класс для выполнения разименований ссылок вида:
 * <li>	1)	"{gname-атрибута}"
 * <li>	2)	"{gname-ассоциации/.../qname-атрибута}"
 * <li>	3)	константа
 * <br/> в типизированные значения (с выполнением преобразований при необходимости,
 * например, String->Float или Date -> String)
 * @author rabdullin
 */
public class LinksResolverBean implements LinksResolver {

	private WKServiceKeeper services;

	@Override
	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	@Override
	public boolean isSubstCalcExpr(final String expression) {
		return (expression != null) && expression.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL);
	}

	@Override
	public Object evaluateLinkExpr(NodeRef docId, String linkExpression) {
		return evaluateLinkExpr(docId, linkExpression, null, null);
	}

	@Override
	public Object evaluateLinkExpr(
			NodeRef docId
			, String linkExpression
			, String destClassName
			, Map<String, Object> curProps  // already loaded props, nullable
	)
	{
		if (linkExpression == null)
			return null;

		PropertyCheck.mandatory (this, "services", services);
		PropertyCheck.mandatory (this, "services.getSubstitudeService", services.getSubstitudeService());

		// final NodeService nodeService = services.getServiceRegistry().getNodeService();
		// final NamespaceService nameService = services.getServiceRegistry().getNamespaceService();
		final SubstitudeBean substService = services.getSubstitudeService();

		/*
		 * если название имеется среди готовых свойств (прогруженных или вычисленных заранее) ... 
		 */
		// (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
		// (!) если элемент начинается с "{{", то это спец. элемент, который будет обработан проксёй подстановок.
		Object value = null;
		if (curProps != null && curProps.containsKey(linkExpression)){
			value = curProps.get(linkExpression);
		} else if (isSubstCalcExpr(linkExpression)) { // ссылка или выражение ... 
			value = substService.formatNodeTitle(docId, linkExpression);
		} else { // считаем явно заданной константой ...
			//final QName qname = QName.createQName(sourceLink, ns);
			//value = (props != null) ? props.get(qname) : nodeService.getProperty( docId, qname);
			value = linkExpression;
		}

		if (value == null)
			return null;

		// типизация value согласно указанному классу ...
		if ( !ru.it.lecm.reports.utils.Utils.isStringEmpty(destClassName)) {
			// TODO: метод для восстановления реального типа данных ...
			final JavaDataTypeImpl.SupportedTypes type = JavaDataTypeImpl.SupportedTypes.findType(destClassName);
			String strValue = value.toString();
			switch (type) {
				case DATE: {
					value = (strValue.isEmpty()) ? null: ArgsHelper.tryMakeDate(strValue, null);
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
				default: { // case STRING:
					value = strValue;
					break;
				}
			} // switch
		}
		/*
		 * NOTE: вариант для случаев, когда NULL не желателен:
		if (value != null)
			return value;

		// NULL result in value -> return current name if valueClass is String
		return (fld != null && String.class.equals(fld.getValueClass())) ? fldAlfName : null;
		 */
		return value;
	}

}