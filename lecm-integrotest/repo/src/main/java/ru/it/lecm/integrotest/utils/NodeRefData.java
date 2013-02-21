package ru.it.lecm.integrotest.utils;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.integrotest.FinderBean;

/**
 * Контейнерный объект для хранения ссылки на данные
 * (со значением одного атрибута)
 */
public class NodeRefData {
	private String nodeType, propName, value;

	@Override
	public String toString() {
		return "NodeRefData{" + "nodeType=" + nodeType + ", propName=" + propName + ", value=" + value + '}';
	}

	public String getNodeType() {
		return nodeType;
	}

	public void clear() {
		nodeType = null;
		propName = null;
		value = null;
	}

	/**
	 * Присвоить одной строкой все три поля nodeType, propName, value
	 * (значения разделяются запятой или точкой с запятой) 
	 * @param args
	 */
	public void setRefStr(String args) {
		if (args == null || args.length() == 0) {
			clear();
			return;
		}
		final String[] values = args.split("[;,]");
		if (values.length > 0)
			setNodeType(values[0].trim());
		if (values.length > 1)
			setPropName(values[1].trim());
		if (values.length > 2)
			setValue(values[2].trim());
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean hasRefData() {
		return getValue() != null;
	}

	public String findNodeId(FinderBean service) {
		final NodeRef ref = findNodeRef(service);
		return (ref != null) ? ref.getId() : null; 
	}

	public NodeRef findNodeRef(FinderBean service) {
		if ( getNodeType() == null && getValue() == null)
			return null;
		final NodeRef ref = service.findNodeByProp( getNodeType(), getPropName(), getValue());
		return ref; 
	}
}