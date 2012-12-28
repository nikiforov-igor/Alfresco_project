package ru.it.lecm.base.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: AIvkin
 * Date: 27.12.12
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseBean {
	protected QName IS_ACTIVE = QName.createQName("http://www.it.ru/lecm/dictionary/1.0", "active");

	protected NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * проверяет что объект имеет подходящий тип
	 */
	public boolean isProperType(NodeRef ref, Set<QName> types) {
		if (ref != null) {
			QName type = nodeService.getType(ref);
			return types.contains(type);
		} else {
			return false;
		}
	}

	/**
	 * Проверка элемента на архивность
	 * @param ref Ссылка на элемент
	 * @return true - если элемент архивный, иначе false
	 */
	public boolean isArchive(NodeRef ref){
		boolean isArchive = ref.getStoreRef().getProtocol().equals("archive");
		Boolean isActive = (Boolean) nodeService.getProperty(ref, IS_ACTIVE);
		return isArchive || (isActive != null && !isActive);
	}
}
