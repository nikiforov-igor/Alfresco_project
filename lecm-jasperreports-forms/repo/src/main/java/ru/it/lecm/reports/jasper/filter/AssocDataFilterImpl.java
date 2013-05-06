package ru.it.lecm.reports.jasper.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.reports.jasper.utils.Utils;

public class AssocDataFilterImpl implements AssocDataFilter {

	final List<AssocDesc> assocList = new ArrayList<AssocDataFilter.AssocDesc>();
	private ServiceRegistry serviceRegistry;

	public AssocDataFilterImpl() {
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssocDataFilter [ assoc count: ");
		builder.append( (assocList == null) ? "NULL" : assocList.size() );
		builder.append( Utils.getAsString( assocList, "\n"));
		// builder.append(assocList != null ? assocList.subList(0, Math.min(assocList.size(), maxLen)) : null);
		builder.append("\n]");
		return builder.toString();
	}

	public AssocDataFilterImpl(ServiceRegistry srvRegistry) {
		this.serviceRegistry = srvRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void addChildAssoc(QName type, NodeRef id) {
		assocList.add( new AssocDesc( type, id, true) );
	}

	@Override
	public List<AssocDesc> getAssocList() {
		return this.assocList;
	}

	@Override
	public boolean isOk(NodeRef id) {
		if (this.assocList.isEmpty()) // в фильтре ничего не задачно -> данные подойдут
			return true;

		// проверка ассоциаций
		final Set<QName> assocChildTypes = new HashSet<QName>();
		for (AssocDataFilter.AssocDesc item: this.assocList) {
			if (item.isChild) {
				assocChildTypes.add(item.type);
			}
		}

		final NodeService nodeSrv = serviceRegistry.getNodeService();
		final List<ChildAssociationRef> resultChild = nodeSrv.getChildAssocs(id, assocChildTypes);
		final List<ChildAssociationRef> resultParent = nodeSrv.getParentAssocs(id);

		for (AssocDataFilter.AssocDesc desc: this.assocList) {
			if (desc.isChild) {
				if (!containAssoc(resultChild, desc))
					return false;
			} else {
				if (!containAssoc(resultParent, desc))
					return false;
			}
		}

		return true; // all filtered -> OK
	}

	/**
	 * Проверить наличие в списке links дочерней ссылки указанной ссылки
	 * @param links	список лочерних ссылок
	 * @param qnAssocType тип ссылки
	 * @param childId значение ссылки
	 * @return true, если ссылка в списке есть, false иначе
	 */
	private boolean containAssoc(List<ChildAssociationRef> links,
			AssocDataFilter.AssocDesc desc) {
		for( ChildAssociationRef item: links) {
			if (desc.type == null || item.getTypeQName().equals(desc.type)) {
				if (   (desc.isChild && item.getChildRef().equals(desc.id)) 
					&& (desc.isChild && item.getChildRef().equals(desc.id)) )
					return true; // FOUND
			}
		}
		return false; // NOT FOUND
	}
}
