package ru.it.lecm.reports.jasper.filter;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
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
		builder.append( (assocList == null) ? "NULL" : assocList.size() ).append(" ");
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
	public void addAssoc( QName type, QName assocType, NodeRef id, AssocKind kind) {
		assocList.add( new AssocDesc( type, assocType, id, kind) );
	}

	@Override
	public List<AssocDesc> getAssocList() {
		return this.assocList;
	}

	@Override
	public boolean isOk(NodeRef id) {
		if (this.assocList.isEmpty()) // в фильтре ничего не задачно -> любые данные подойдут
			return true;

		/* проверка ассоциаций */

		/*
		final Set<QName> assocChildTypes = new HashSet<QName>();
		for (AssocDataFilter.AssocDesc item: this.assocList) {
			if (item.kind == child) {
				assocChildTypes.add(item.type);
			}
		}
		 */

		// check parent - child associations
		final NodeService nodeSrv = serviceRegistry.getNodeService();
		final List<ChildAssociationRef> resultChild = nodeSrv.getChildAssocs(id);
		final List<ChildAssociationRef> resultParent = nodeSrv.getParentAssocs(id);

		for (AssocDataFilter.AssocDesc desc: this.assocList) { 

			if (desc.kind == null) continue;

			switch (desc.kind) {
			case child:
				if (!containAssoc(resultChild, desc, true))
					return false;
				break;
			case parent:
				if (!containAssoc(resultParent, desc, false))
					return false;
				break;
			case target:
				if (!containsAssoc( nodeSrv.getTargetAssocs(id, desc.assoctype), desc, true))
					return false;
				break;
			case source:
				if (!containsAssoc( nodeSrv.getSourceAssocs(id, desc.assoctype), desc, false))
					return false;
				break;
			default:
				throw new RuntimeException( String.format("Unsupported assosiation kind %s", desc.kind));
			} // switch

		}

		// all filtered -> OK
		return true; 
	}

	/**
	 * Проверить наличие в списке links дочерней ссылки указанной ссылки
	 * @param links	список дочерних ссылок
	 * @param qnAssocType тип ссылки
	 * @param desc значение ссылки
	 * @param isChild true, чтобы проверять детей, false родителей 
	 * @return true, если ссылка в списке есть, false иначе
	 */
	static boolean containAssoc(List<ChildAssociationRef> links,
			AssocDataFilter.AssocDesc desc, boolean isChild) {
		if (links != null && !links.isEmpty()) {
			for( ChildAssociationRef item: links) {
				if (desc.type == null || desc.type.equals(item.getTypeQName())) {
					if (   (isChild && item.getChildRef().equals(desc.id))
							|| (!isChild && item.getParentRef().equals(desc.id)) )
						return true; // FOUND
				}
			}
		}
		return false; // NOT FOUND
	}

	/**
	 * Проверить наличие в списке links дочерней ссылки указанной ссылки
	 * @param assocs список ассоциаций
	 * @param qnAssocType тип ссылки
	 * @param desc
	 * @param isTarget true для проверки целевых значений, false иначе
	 * @return true, если ссылка в списке есть, false иначе
	 * @return
	 */
	static boolean containsAssoc(List<AssociationRef> assocs, AssocDesc desc, boolean isTarget) {
		if (assocs != null && !assocs.isEmpty()) {
			for( AssociationRef item: assocs) {
				if (desc.assoctype == null || desc.assoctype.equals(item.getTypeQName()) ) {
					if (   (isTarget && item.getTargetRef().equals(desc.id))
							|| (!isTarget && item.getSourceRef().equals(desc.id)) ) {
						return true; // FOUND
					}
				}
			}
		}
		return false; // NOT FOUND
	}

}
