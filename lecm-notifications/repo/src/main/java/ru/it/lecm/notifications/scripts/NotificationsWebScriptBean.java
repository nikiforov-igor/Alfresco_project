package ru.it.lecm.notifications.scripts;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 11:57
 */
public class NotificationsWebScriptBean extends BaseScopableProcessorExtension {

	NotificationsServiceImpl service;

	/**
	 * Service registry
	 */
	protected ServiceRegistry services;

	public void setService(NotificationsServiceImpl service) {
		this.service = service;
	}

	/**
	 * Set the service registry
	 *
	 * @param services the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) {
		this.services = services;
	}

	public boolean testSendNotification(String objectRef, String[] notificationTypes, String[] employeeRefs,
	                                 String[] organizationUnitRefs, String[] workGroupRefs, String[] positionsRefs) {
		NodeRef objectNodeRef = new NodeRef(objectRef);

		Notification notf = new Notification();
		notf.setAutor("Тестовый WebScript");
		notf.setDescription("Тестовое описание");
		notf.setObjectRef(objectNodeRef);

		NodeService nodeService = services.getNodeService();

		if (notificationTypes != null) {
			List<NodeRef> typeList = new ArrayList<NodeRef>();
			for (String ref : notificationTypes) {
				NodeRef nodeRef = new NodeRef(ref);
				if (nodeService.exists(nodeRef)) {
					typeList.add(nodeRef);
				}
			}
			notf.setTypeRefs(typeList);
		}

		if (employeeRefs != null) {
			List<NodeRef> employeeList = new ArrayList<NodeRef>();
			for (String ref : employeeRefs) {
				NodeRef nodeRef = new NodeRef(ref);
				if (nodeService.exists(nodeRef)) {
					employeeList.add(nodeRef);
				}
			}
			notf.setRecipientEmployeeRefs(employeeList);
		}

		if (organizationUnitRefs != null) {
			List<NodeRef> organizationUnitList = new ArrayList<NodeRef>();
			for (String ref : organizationUnitRefs) {
				NodeRef nodeRef = new NodeRef(ref);
				if (nodeService.exists(nodeRef)) {
					organizationUnitList.add(nodeRef);
				}
			}
			notf.setRecipientOrganizationUnitRefs(organizationUnitList);
		}

		if (workGroupRefs != null) {
			List<NodeRef> workGroupList = new ArrayList<NodeRef>();
			for (String ref : workGroupRefs) {
				NodeRef nodeRef = new NodeRef(ref);
				if (nodeService.exists(nodeRef)) {
					workGroupList.add(nodeRef);
				}
			}
			notf.setRecipientWorkGroupRefs(workGroupList);
		}

		if (positionsRefs != null) {
			List<NodeRef> positionList = new ArrayList<NodeRef>();
			for (String ref : positionsRefs) {
				NodeRef nodeRef = new NodeRef(ref);
				if (nodeService.exists(nodeRef)) {
					positionList.add(nodeRef);
				}
			}
			notf.setRecipientPositionRefs(positionList);
		}

		return service.sendNotification(notf);
	}
}
