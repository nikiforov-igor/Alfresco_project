package ru.it.lecm.documents.removal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 *
 * @author vmalygin
 */
public class DocumentsRemovalServiceImpl implements DocumentsRemovalService {

	private final static Logger logger = LoggerFactory.getLogger(DocumentsRemovalServiceImpl.class);
	private final static String MEMBERS_TEMPLATE = "Пользователи были исключены из участников и лишены прав доступа к документу #mainobject";
	private final static String WORKFLOWS_TEMPLATE = "Все бизнес-процессы, в которых участвовал документ #mainobject остановлены";
	private final static String CONNECTIONS_TEMPLATE = "Связи документа #mainobject с другими документами разорваны";
	private final static String OTHERS_TEMPLATE = "Ассоциации документа #mainobject с прочими объектами системы удалены";
	private final static String DOCUMENT_TEMPLATE = "Документ #mainobject полностью удален из системы";

	private DictionaryService dictionaryService;
	private NodeService nodeService;
	private BehaviourFilter behaviourFilter;
	private DocumentMembersService documentMembersService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineService;
	private BusinessJournalService businessJournalService;
	private DocumentAttachmentsService documentAttachmentsService;


	public void setDictionaryService(final DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setDocumentMembersService(DocumentMembersService documentMembersService) {
		this.documentMembersService = documentMembersService;
	}

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}

	public void setStateMachineService(StateMachineServiceBean stateMachineService) {
		this.stateMachineService = stateMachineService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	private void removeAssociations(final List<AssociationRef> assocs) {
		for (AssociationRef assoc : assocs) {
			nodeService.removeAssociation(assoc.getSourceRef(), assoc.getTargetRef(), assoc.getTypeQName());
		}
	}

	@Override
	public void purge(final NodeRef documentRef) {
		//проверяем что это действительно документ
		QName documentType = nodeService.getType(documentRef);
		boolean isDocument = dictionaryService.isSubClass(documentType, DocumentService.TYPE_BASE_DOCUMENT);
		if (!isDocument) {
			String template = "Node %s of type %s is not subtype of %s. This service can't remove it, please use standard removal mechanism";
			String msg = String.format(template, documentRef, documentType, DocumentService.TYPE_BASE_DOCUMENT);
			throw new AlfrescoRuntimeException(msg);
		}
		//проверяем что мы есть админ, причем совсем админ ???
		String user = AuthenticationUtil.getFullyAuthenticatedUser();

		behaviourFilter.disableBehaviour(documentRef);
		logger.debug("All policies for document {} are deactivated!", documentRef);

		//лишаем участников документа всех прав связанных с этим документом
		List<NodeRef> members = documentMembersService.getDocumentMembers(documentRef);
		for (NodeRef member : members) {
			List<AssociationRef> assocs = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);
			NodeRef employeeRef = assocs.get(0).getTargetRef();
			LecmPermissionGroup permissionGroup = documentMembersService.getMemberPermissionGroup(documentRef);
			lecmPermissionService.revokeAccess(permissionGroup, documentRef, employeeRef);
			List<String> roles = lecmPermissionService.getEmployeeRoles(documentRef, employeeRef);
			for (String role : roles) {
				lecmPermissionService.revokeDynamicRole(role, documentRef, employeeRef.getId());
			}
			documentMembersService.deleteMember(documentRef, employeeRef);
		}
		//logger.debug("Пользователи были исключены из участников и лишены прав на этот документ");
//			businessJournalService.log(user, documentRef, EventCategory.DELETE, MEMBERS_TEMPLATE, null);

		//останавливаем все workflow в которых участвует этот документ
		List<WorkflowInstance> workflows = stateMachineService.getDocumentWorkflows(documentRef);
		Set<String> definitions = new HashSet<String>();
		for (WorkflowInstance workflow : workflows) {
			definitions.add(workflow.getDefinition().getId());
		}
		stateMachineService.terminateWorkflowsByDefinitionId(documentRef, new ArrayList<String>(definitions), null, null);
//			logger.debug("Все бизнес процессы в которых участвовал документ остановлены");
//			businessJournalService.log(user, documentRef, EventCategory.DELETE, WORKFLOWS_TEMPLATE, null);

		//получаем все вложения отключаем их policy и удаляем
		List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
		for (NodeRef categoryRef : categories) {
			String category = documentAttachmentsService.getCategoryName(categoryRef);
			List<NodeRef> attachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, category);
			for (NodeRef attachRef : attachments) {
				behaviourFilter.disableBehaviour(attachRef);
				documentAttachmentsService.deleteAttachment(attachRef);
			}
		}

		//удаляем все связи какие есть
		//разгребаем все ассоциации которые есть по категориям
		//уведомления
		//бизнес журнал
		//прочие ассоциации
		List<AssociationRef> allAssocs = new ArrayList<AssociationRef>();
		List<NodeRef> connections = new ArrayList<NodeRef>(); //связи
		List<AssociationRef> businessJournalAssocs = new ArrayList<AssociationRef>(); //ассоциации на бизнес-журнал
		List<AssociationRef> notificationAssocs = new ArrayList<AssociationRef>(); //ассоциации на уведомления
		List<AssociationRef> otherAssocs = new ArrayList<AssociationRef>(); //все остальное
		allAssocs.addAll(nodeService.getTargetAssocs(documentRef, RegexQNamePattern.MATCH_ALL));
		allAssocs.addAll(nodeService.getSourceAssocs(documentRef, RegexQNamePattern.MATCH_ALL));
		for (AssociationRef assoc : allAssocs) {
			QName assocType = assoc.getTypeQName();
			String namespaceURI = assocType.getNamespaceURI();
			if (DocumentConnectionService.DOCUMENT_CONNECTIONS_NAMESPACE_URI.equals(namespaceURI)) {
				//связи
				QName sourceType = nodeService.getType(assoc.getSourceRef());
				QName targetType = nodeService.getType(assoc.getTargetRef());
				if (DocumentConnectionService.TYPE_CONNECTION.isMatch(sourceType)) {
					connections.add(assoc.getSourceRef());
				} else if (DocumentConnectionService.TYPE_CONNECTION.isMatch(targetType)) {
					connections.add(assoc.getTargetRef());
				}
				//сам объект связи тоже надо найти и удалить
			} else if (BusinessJournalService.BJ_NAMESPACE_URI.equals(namespaceURI)) {
				//бизнес журнал
				businessJournalAssocs.add(assoc);
			} else if (NotificationsService.NOTIFICATIONS_NAMESPACE_URI.equals(namespaceURI)) {
				//уведомления
				notificationAssocs.add(assoc);
			} else {
				//прочее
				otherAssocs.add(assoc);
			}
		}
		//удаляем связи
		for (NodeRef connectionRef : connections) {
			behaviourFilter.disableBehaviour(connectionRef);
			nodeService.addAspect(connectionRef, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(connectionRef);
		}
//			logger.debug("Связи с другими документами удалены");
//			businessJournalService.log(user, documentRef, EventCategory.DELETE, CONNECTIONS_TEMPLATE, null);
		//удаляем прочие ассоциации
		removeAssociations(otherAssocs);
//			logger.debug("Ассоциации с прочими объектами системы удалены");
//			businessJournalService.log(user, documentRef, EventCategory.DELETE, OTHERS_TEMPLATE, null);

		//удаляем сам документ
		nodeService.addAspect(documentRef, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(documentRef);

		//сообщаем об удалении в бизнес журнал
//			businessJournalService.log(user, documentRef, EventCategory.DELETE, DOCUMENT_TEMPLATE, null);
	}
}
