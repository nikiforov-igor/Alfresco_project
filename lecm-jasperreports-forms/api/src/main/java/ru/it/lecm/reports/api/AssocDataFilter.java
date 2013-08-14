package ru.it.lecm.reports.api;

import java.util.Arrays;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Фильтр данных для ассоциаций
 */
public interface AssocDataFilter extends DataFilter {


	/**
	 * Типы ассоциации
	 */
	public enum AssocKind {
		child			// "на дочку"
		, parent		// "на родителя"
		, source		// "связь на исходный объект"
		, target		// "связь на целевой объект"
	}

	/**
	 * Добавить условие для ассоциации 
	 * @param type тип на другом конце связи или Null 
	 * @param assocType тип связи-ассоциации
	 * @param idAssoc связанный узел
	 * @param kind вид самой связи
	 */
	void addAssoc( AssocDesc desc);


	/**
	 * Получить текущий набор фильтруемых связей (сформированный addAssoc)
	 * @return
	 */
	List<AssocDesc> getAssocList();

	/**
	 * Описатель связи.
	 * Содержит:
	 *    1) вид связи (child/parent или target/source),
	 *    2) альфреско-тип связи,
	 *    3) альфреско-тип объектов на "другом" конце связи (опционально),
	 *    4) ограничивающий список допустимых объектов на другом конце, при этом,
	 * null-список принимается за отсутствие ограничения.
	 */
	public class AssocDesc {
		public final QName type, assoctype;
		public final List<NodeRef> ids;
		public final AssocKind kind; // true = child association descriptor, false = parent

		public AssocDesc(AssocKind kind, QName assocType, NodeRef id, QName type) {
			this(kind, assocType, type, id == null ? null : Arrays.asList(id));
		}

		public AssocDesc(AssocKind kind, QName assoctype, NodeRef toId) {
			this(kind, assoctype, toId, (QName) null);
		}

		/**
	 	* Добавить список из допустимых вариантов объектов на втором конце
		 */
		public AssocDesc(AssocKind kind, QName assocType, QName type,
				List<NodeRef> idList) {
			super();
			this.kind = kind;
			this.assoctype = assocType;
			this.type = type;
			this.ids = idList;
		}

		/**
		 * Проверить входит ли указанный узел в допустимые для данного описания.
		 * @param id
		 * @return true, если id присутсвует в перечисленных/допустимых узлах,
		 * т.е. содержится в this.ids или когда this.ids == null.
		 */
		public boolean contains(NodeRef id) {
			return (this.ids == null) || this.ids.contains(id);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AssocDesc[ ");
			builder.append( kind);
			builder.append(", type=").append(type);
			builder.append(", assoc=").append(assoctype);
			builder.append(", ids ").append( ids == null ? "NULL" : Arrays.toString(ids.toArray()));
			builder.append(" ]");
			return builder.toString();
		}
	}

}
