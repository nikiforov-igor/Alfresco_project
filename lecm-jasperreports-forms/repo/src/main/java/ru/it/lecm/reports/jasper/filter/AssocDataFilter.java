package ru.it.lecm.reports.jasper.filter;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Фильтр данных для ассоциаций
 */
public interface AssocDataFilter {

	/**
	 * Проверить, выполняются ли для указанного узла условия фильтра по ассоциациям
	 * @param id
	 * @return
	 */
	boolean isOk(NodeRef id); 

	/**
	 * Добавить условие для дочерней ассоциации 
	 * @param type тип связи-ассоциации
	 * @param id дочерний узел
	 */
	void addChildAssoc( QName type, NodeRef id);


	// TODO: void addParentAssoc( QName type, NodeRef id);

	/**
	 * Получить текущий набор фильтруемых связей (сформированный addChildAssoc)
	 * @return
	 */
	List<AssocDesc> getAssocList();

	/**
	 * Описатель связи
	 */
	public class AssocDesc {
		public final QName type;
		public final NodeRef id;
		public final boolean isChild; // true = child association descriptor, false = parent

		public AssocDesc(QName type, NodeRef id, boolean isChild) {
			super();
			this.type = type;
			this.id = id;
			this.isChild = isChild;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AssocDesc [ ");
			builder.append( isChild ? "child" : "parent");
			builder.append(", type=").append(type);
			builder.append(", id=").append(id);
			builder.append("]");
			return builder.toString();
		}
	}

}
