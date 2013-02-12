package ru.it.lecm.base.beans.getchildren;

import java.io.Serializable;

import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.node.getchildren.FilterType;
import org.alfresco.service.namespace.QName;

/**
 * @author dbashmakov
 *         Date: 11.02.13
 *         Time: 11:16
 */
public class FilterPropLECM implements FilterProp {

	public static enum FilterTypeLECM implements FilterType {
		NOT_EQUALS,
		EQUALS
	}

	private QName propName;
	private Serializable propVal;
	private FilterTypeLECM filterType;
	private Boolean defaultValue = Boolean.FALSE;

	public FilterPropLECM(QName propName, Serializable propVal, FilterTypeLECM filterType) {
		this(propName, propVal, filterType, Boolean.FALSE);
	}

	public FilterPropLECM(QName propName, Serializable propVal, FilterTypeLECM filterType, Boolean defaultValue) {
		this.propName = propName;
		this.propVal = propVal;
		this.filterType = filterType;
		this.defaultValue = defaultValue;
	}

	@Override
	public QName getPropName() {
		return propName;
	}

	@Override
	public Serializable getPropVal() {
		return propVal;
	}

	@Override
	public FilterType getFilterType() {
		return filterType;
	}

	public Boolean getDefaultValue() {
		return defaultValue;
	}
}
