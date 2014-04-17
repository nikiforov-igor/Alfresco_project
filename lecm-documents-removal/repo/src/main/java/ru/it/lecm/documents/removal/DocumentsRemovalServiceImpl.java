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
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 *
 * @author vmalygin
 */
public class DocumentsRemovalServiceImpl implements DocumentsRemovalService {

	private final static Logger logger = LoggerFactory.getLogger(DocumentsRemovalServiceImpl.class);
//	private final static String MEMBERS_TEMPLATE = "Пользователи были исключены из участников и лишены прав доступа к документу #mainobject";
//	private final static String WORKFLOWS_TEMPLATE = "Все бизнес-процессы, в которых участвовал документ #mainobject остановлены";
//	private final static String CONNECTIONS_TEMPLATE = "Связи документа #mainobject с другими документами разорваны";
//	private final static String OTHERS_TEMPLATE = "Ассоциации документа #mainobject с прочими объектами системы удалены";
	private final static String DOCUMENT_TEMPLATE = "Документ #mainobject полностью удален из системы";

	private DictionaryService dictionaryService;
	private NodeService nodeService;
	private BehaviourFilter behaviourFilter;
	private DocumentMembersService documentMembersService;
	private LecmPermissionService lecmPermissionService;
	private StateMachineServiceBean stateMachineService;
	private DocumentAttachmentsService documentAttachmentsService;
	private OrgstructureBean orgstructureService;
	private BusinessJournalService businessJournalService;

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

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	private void removeAssociations(final List<AssociationRef> assocs) {
		for (AssociationRef assoc : assocs) {
			NodeRef sourceRef = assoc.getSourceRef(),
					targetRef = assoc.getTargetRef();
			behaviourFilter.disableBehaviour(sourceRef);
			behaviourFilter.disableBehaviour(targetRef);

			nodeService.removeAssociation(sourceRef, targetRef, assoc.getTypeQName());
		}
	}

	/**
	 * Жестокое удаление объекта.
	 * Выключаем все полиси, вешаем темповый аспект и удаляем мимо корзины.
	 *
	 * @param node кого удаляем
	 */
	private void cruellyDeleteNode(NodeRef node) {
		behaviourFilter.disableBehaviour(node);
		nodeService.addAspect(node, ContentModel.ASPECT_TEMPORARY, null);
		nodeService.deleteNode(node);
	}

	/**
	 * Рекурсивно побежать по чайлдовым ассоциациям и жестоко удалить все встретившиеся объекты.
	 *
	 * @param dirNode папка, в которую мы будем спускаться.
	 */
	private void clearDir(NodeRef dirNode) {
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(dirNode);
		for (ChildAssociationRef childAssoc : childAssocs) {
			NodeRef parentRef = childAssoc.getParentRef(),
					childRef = childAssoc.getChildRef();
			if (!nodeService.getChildAssocs(childRef).isEmpty()) {
				clearDir(childRef);
			}
			behaviourFilter.disableBehaviour(parentRef);
			cruellyDeleteNode(childRef);
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
		if (!lecmPermissionService.isAdmin(user)) {
			String template = "User with login %s must be alfresco administrator to purge document {%s}%s";
			String msg = String.format(template, user, documentType, documentRef);
			throw new AlfrescoRuntimeException(msg);
		}

		behaviourFilter.disableBehaviour(documentRef);
		logger.debug("All policies for document {} are deactivated!", documentRef);

		//лишаем участников документа всех прав связанных с этим документом
		List<NodeRef> members = documentMembersService.getDocumentMembers(documentRef);
		List<String> users = new ArrayList<String>();
		for (NodeRef member : members) {
			List<AssociationRef> assocs = nodeService.getTargetAssocs(member, DocumentMembersService.ASSOC_MEMBER_EMPLOYEE);
			NodeRef employeeRef = assocs.get(0).getTargetRef();
			String login = orgstructureService.getEmployeeLogin(employeeRef);
			String shortName = (String) nodeService.getProperty(employeeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
			users.add(String.format("%s(%s)", shortName, login));
			LecmPermissionGroup permissionGroup = documentMembersService.getMemberPermissionGroup(documentRef);
			lecmPermissionService.revokeAccess(permissionGroup, documentRef, employeeRef);
			List<String> roles = lecmPermissionService.getEmployeeRoles(documentRef, employeeRef);
			for (String role : roles) {
				lecmPermissionService.revokeDynamicRole(role, documentRef, employeeRef.getId());
			}
			documentMembersService.deleteMember(documentRef, employeeRef);
		}
		logger.debug("Members {} are deleted and access is revoked for document {}", users, documentRef);

		//останавливаем все workflow в которых участвует этот документ
		List<WorkflowInstance> workflows = stateMachineService.getDocumentWorkflows(documentRef);
		Set<String> definitions = new HashSet<String>();
		List<String> activities = new ArrayList<String>();
		for (WorkflowInstance workflow : workflows) {
			definitions.add(workflow.getDefinition().getId());
			activities.add(String.format("%s(%s)", workflow.getId(), workflow.getDefinition().getId()));
		}
		stateMachineService.terminateWorkflowsByDefinitionId(documentRef, new ArrayList<String>(definitions), null, null);
		logger.debug("All workflows {} for document {} are stopped", activities, documentRef);

		try {
			//получаем все вложения отключаем их policy и удаляем
			List<NodeRef> categories = documentAttachmentsService.getCategories(documentRef);
			for (NodeRef categoryRef : categories) {
				String category = documentAttachmentsService.getCategoryName(categoryRef);
				List<NodeRef> attachments = documentAttachmentsService.getAttachmentsByCategory(documentRef, category);
				for (NodeRef attachRef : attachments) {
					cruellyDeleteNode(attachRef);
				}
			}
		} catch (Exception ex) {
			// что-то сломалось при попытке получить категории вложений. это не повод прекращать удаление документа
			String msg = "Error during deleting document %s";
			logger.warn(String.format(msg, documentRef), ex);
		}

		// зачищаем папку документа. удаляются все дочерние элементы, которые не были удалены до этого
		clearDir(documentRef);

		//удаляем все связи какие есть
		//разгребаем все ассоциации которые есть по категориям
		//уведомления
		//бизнес журнал
		//прочие ассоциации
		List<AssociationRef> allAssocs = new ArrayList<AssociationRef>();
		allAssocs.addAll(nodeService.getTargetAssocs(documentRef, RegexQNamePattern.MATCH_ALL));
		allAssocs.addAll(nodeService.getSourceAssocs(documentRef, RegexQNamePattern.MATCH_ALL));
		List<AssociationRef> connectionAssocs = new ArrayList<AssociationRef>(); //связи
		List<NodeRef> connections = new ArrayList<NodeRef>();
		List<AssociationRef> businessJournalAssocs = new ArrayList<AssociationRef>(); //ассоциации на бизнес-журнал
		List<AssociationRef> notificationAssocs = new ArrayList<AssociationRef>(); //ассоциации на уведомления
		List<AssociationRef> otherAssocs = new ArrayList<AssociationRef>(); //все остальное
		for (AssociationRef assoc : allAssocs) {
			QName assocType = assoc.getTypeQName();
			String namespaceURI = assocType.getNamespaceURI();
			if (DocumentConnectionService.DOCUMENT_CONNECTIONS_NAMESPACE_URI.equals(namespaceURI)) {
				//связи
				connectionAssocs.add(assoc);
				connections.add(assoc.getSourceRef());
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
		removeAssociations(connectionAssocs);
		//в будущем сработает policy DocumentConnectionPolicy.onDeleteAssociation а пока будем их удалять принудительно
		for (NodeRef connectionRef : connections) {
			cruellyDeleteNode(connectionRef);
		}
		logger.debug("Connections with other documents are removed");
		//удаляем прочие ассоциации
		removeAssociations(otherAssocs);
		logger.debug("Associations with other objects are removed");

		//удаляем сам документ
		BusinessJournalRecord record = businessJournalService.createBusinessJournalRecord(user, documentRef, EventCategory.DELETE, DOCUMENT_TEMPLATE);
		cruellyDeleteNode(documentRef);

		//сообщаем об удалении в бизнес журнал
		logger.debug("Document {} of type {} successfully purged", documentRef, documentType);
		businessJournalService.sendRecord(record);
	}
}
