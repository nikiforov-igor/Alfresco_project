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
	 * @param type тип на другом конце
	 * @param assocType тип связи-ассоциации или Null
	 * @param id связанный узел
	 * @param kind
	 */
	void addAssoc( QName type, QName assocType, NodeRef id, AssocKind kind);


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
		public final QName type, assoctype;
		public final NodeRef id;
		public final AssocKind kind; // true = child association descriptor, false = parent

//		public AssocDesc(QName type, NodeRef id, AssocKind kind) {
//			this(type, null, id, kind);
//		}

		public AssocDesc(QName type, QName assoctype, NodeRef id, AssocKind kind) {
			super();
			this.type = type;
			this.assoctype = assoctype;
			this.id = id;
			this.kind = kind;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AssocDesc [ ");
			builder.append( kind);
			builder.append(", type=").append(type);
			builder.append(", assoc=").append(assoctype);
			builder.append(", id=").append(id);
			builder.append("]");
			return builder.toString();
		}
	}

	/**
	 * Типы ассоциации
	 */
	public enum AssocKind {
		child, parent, source, target
	}
}
