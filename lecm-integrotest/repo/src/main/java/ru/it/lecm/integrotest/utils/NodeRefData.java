package ru.it.lecm.integrotest.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;

import ru.it.lecm.integrotest.FinderBean;

/**
 * Контейнерный объект для хранения ссылки на данные
 * (со значением одного атрибута)
 */
public class NodeRefData {
	private String nodeType, propName, value;
	private NodeRefData parent;

	public NodeRefData() {
	}

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

	/**
	 * @return текущее инициализированное контейнерное значение parent (null не будет)
	 */
	public NodeRefData getParent() {
		if (parent == null) 
			parent = new NodeRefData();
		return parent;
	}

	/**
	 * @param parent контейнер-значение для задания родителя
	 */
	public void setParent(NodeRefData parent) {
		this.parent = parent;
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
		return (getNodeType() != null) || (getValue() != null);
	}

	/**
	 * Используя службу узлов, загрузить (найти) узел, описанный полями this,
	 * с учётом родителя
	 * @param service
	 * @return
	 */
	public String findNodeId(FinderBean service) {
		final NodeRef ref = findNodeRef(service);
		return (ref != null) ? ref.getId() : null; 
	}

	public NodeRef findNodeRef(FinderBean service) {
		final List<NodeRef> nodes = findNodeRefs( service);
		return (nodes == null || nodes.isEmpty()) ? null : nodes.get(0);
	}

	public List<NodeRef> findNodeRefs(FinderBean service) {
		if (!hasRefData())
			return null;

		// получение узлов с заданными параметрами ...
		final List<NodeRef> nodes = service.findNodesByProp( getNodeType(), getPropName(), getValue());
		if (nodes == null || nodes.isEmpty())
			return null;

		if (this.parent != null && this.parent.hasRefData()) {
			// получить родителей 
			final List<NodeRef> parents = this.parent.findNodeRefs( service);

			// отфильтровать по родителям
			filterByParents( nodes, parents, service);
		}

		return nodes; 
	}

	/**
	 * Проверить что есть общие элементы в двух списках 
	 * @param list1
	 * @param list2
	 * @return true, если есть общие элементы в списках list1 и List2,
	 * если оба списка пустые возвращается true  
	 */
	private boolean containsAny(List<NodeRef> list1, List<NodeRef> list2) {
		if (list1 == null || list1.isEmpty())
			return (list2 == null || list2.isEmpty());

		if (list2 == null || list2.isEmpty())
			return false;

		final Set<NodeRef> filterSet = new HashSet<NodeRef>( list1);
		for (NodeRef ref: list2) {
			if (filterSet.contains(ref))
				return true; // общее значение найдено
		}
		return false; // ни одного общего значения
	}


	/**
	 * Отфильтровать список nodes по родитиелям, перечисленным в parents
	 * @param nodes
	 * @param parents
	 * @param service 
	 */
	private void filterByParents(List<NodeRef> nodes, List<NodeRef> parents, FinderBean service) {
		if (parents != null && !parents.isEmpty()) {
			for (Iterator<NodeRef> i = nodes.iterator(); i.hasNext(); ) {
				final NodeRef ref = i.next();
				final List<NodeRef> refParents = service.getParents(ref);
				if (!containsAny( parents, refParents))
					i.remove(); // убрать узел, родители которого вне фильтруемого набора
			}
		} 
	}
}