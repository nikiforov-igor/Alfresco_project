package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentConnectionServiceImpl;
import ru.it.lecm.documents.beans.DocumentMembersServiceImpl;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.constraints.PresentStringConstraint;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 20.03.13
 * Time: 15:02
 */
public class DocumentPolicy extends BaseBean
        implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy {

	private static final String GRAND_DYNAMIC_ROLE_CODE_INITIATOR = "BR_INITIATOR";

    final static protected Logger logger = LoggerFactory.getLogger(DocumentPolicy.class);
    final private QName[] IGNORED_PROPERTIES = {DocumentService.PROP_RATING, DocumentService.PROP_RATED_PERSONS_COUNT, StatemachineModel.PROP_STATUS};

    private PolicyComponent policyComponent;
    private BusinessJournalService businessJournalService;
    private DictionaryService dictionaryService;
    private SubstitudeBean substituteService;
    private AuthenticationService authenticationService;
    private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineHelper;
	private LecmPermissionService lecmPermissionService;
    private PermissionService permissionService;
    private AuthorityService authorityService;
    private DocumentConnectionServiceImpl documentConnectionService;
    private DocumentMembersServiceImpl documentMembersService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setSubstituteService(SubstitudeBean substituteService) {
        this.substituteService = substituteService;
    }


    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setStateMachineHelper(StateMachineServiceBean stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

	public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
		this.lecmPermissionService = lecmPermissionService;
	}


    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setDocumentConnectionService(DocumentConnectionServiceImpl documentConnectionService) {
        this.documentConnectionService = documentConnectionService;
    }

    public void setDocumentMembersService(DocumentMembersServiceImpl documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

	final public void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "authenticationService", authenticationService);
        PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);


        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                DocumentService.TYPE_BASE_DOCUMENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * Метод переназначает документ новому сотруднику и выделяем ему соответствующие права
     */
    public void documentTransmit(NodeRef documentRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        NodeRef beforeAuthor = new NodeRef(before.get(DocumentService.PROP_DOCUMENT_CREATOR_REF).toString());
        NodeRef afterAuthor = new NodeRef(after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF).toString());
        Set<AccessPermission> permissionsDoc = permissionService.getAllSetPermissions(documentRef);
        Set<String> permissionsEmployee = authorityService.getAuthoritiesForUser(orgstructureService.getEmployeeLogin(beforeAuthor));

        nodeService.setProperty(documentRef, DocumentService.PROP_DOCUMENT_CREATOR, substituteService.getObjectDescription(afterAuthor));
        nodeService.setProperty(documentRef, DocumentService.PROP_DOCUMENT_CREATOR_REF, afterAuthor.toString());
        nodeService.setProperty(documentRef, DocumentService.PROP_DOCUMENT_EMPLOYEE_REF, "");

        for (AccessPermission permission : permissionsDoc) {
            if (permissionsEmployee.contains(permission.getAuthority()) && !PermissionService.ALL_AUTHORITIES.equals(permission.getAuthority())) {
                if (permission.getAuthority().indexOf(Types.SFX_BRME) != -1) {
                    // удаляем динамическую роль
//                    lecmPermissionService.revokeDynamicRole(permission.getPermission(), documentRef, beforeAuthor.getId());
                    permissionService.clearPermission(documentRef, permission.getAuthority());
                    // назначаем динамическую роль другому сотруднику
                    lecmPermissionService.grantDynamicRole(permission.getPermission(), documentRef, afterAuthor.getId(), lecmPermissionService.findPermissionGroup(permission.getPermission()));
                } else {
                    // удаляем статическую роль
                    lecmPermissionService.revokeAccess(lecmPermissionService.findPermissionGroup(permission.getPermission()), documentRef, beforeAuthor.getId());
                    // назначаем статическую роль другому сотруднику
                    lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(permission.getPermission()), documentRef, afterAuthor.getId());
                }
            }
        }

        // добавляем в участники документа нового сотрудника
        documentMembersService.addMember(documentRef, afterAuthor, new HashMap<QName, Serializable>());
        // передаем задачи по документу
        stateMachineHelper.transferRightTask(orgstructureService.getEmployeeLogin(beforeAuthor), orgstructureService.getEmployeeLogin(afterAuthor));


        // Проверяем выбран ли пункт лишать прав автора документа, если нет то добавляем бывшего автора в читатели документа и осталяем в участниках
        if (after.get(DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT).toString().equals("true")) {
            // удаляем из участников документа
            documentMembersService.deleteMember(documentRef, beforeAuthor);
        } else {
            lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), documentRef, beforeAuthor.getId());
        }
        // Нужно ли передовать права на документы введенные на основании
        if (after.get(DocumentService.PROP_DOCUMENT_IS_TRANSMIT).toString().equals("true")) {
            // переопределяем права на документы к договору.
            // для этого получаем список документов из папочки Связи и берем только документы с "Системной" связью
            NodeRef rootLinks = documentConnectionService.getRootFolder(documentRef);
            List<ChildAssociationRef> links = nodeService.getChildAssocs(rootLinks);

//            List<NodeRef> additionalDocuments = documentConnectionService.getConnectionsWithDocument(documentRef);
            for (ChildAssociationRef link : links) {
                if (nodeService.getProperty(link.getChildRef(), DocumentConnectionService.PROP_IS_SYSTEM) != null) {
                    List<AssociationRef> addDoc = nodeService.getTargetAssocs(link.getChildRef(),DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
                    if (addDoc.size() > 0) {
                        // присваиваем значения property документу к договору, чтобы инициализировать policy уже для
                        // документа к договору при это устанавливаем значения как и основном документе
                        nodeService.setProperty(addDoc.get(0).getTargetRef(), DocumentService.PROP_DOCUMENT_EMPLOYEE_REF, afterAuthor.toString());
                        nodeService.setProperty(addDoc.get(0).getTargetRef(), DocumentService.PROP_DOCUMENT_IS_TRANSMIT, after.get(DocumentService.PROP_DOCUMENT_IS_TRANSMIT).toString());
                        nodeService.setProperty(addDoc.get(0).getTargetRef(), DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT, after.get(DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT).toString());
                    }
                }
            }
        }

    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final NodeRef employeeRef = orgstructureService.getCurrentEmployee();
        if (employeeRef != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER, substituteService.getObjectDescription(employeeRef));
            nodeService.setProperty(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER_REF, employeeRef.toString());
        }
        if (before.get(DocumentService.PROP_DOCUMENT_CREATOR_REF) != null && !before.get(DocumentService.PROP_DOCUMENT_CREATOR_REF).equals("") &&
		        after.get(DocumentService.PROP_DOCUMENT_CREATOR_REF) != null && !after.get(DocumentService.PROP_DOCUMENT_CREATOR_REF).equals("")) {
            if (!before.get(DocumentService.PROP_DOCUMENT_CREATOR_REF).equals(after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF))) {
                documentTransmit(nodeRef, before, after);
            }
        }
        if (!changeIgnoredProperties(before, after)) {
            if (before.size() == after.size()) { // только при изменении свойств (учитываем добавление/удаление комментариев, не учитываем создание документа + добавление рейтингов и прочего
                if (after.get(ForumModel.PROP_COMMENT_COUNT) != null) {
                    if ((Integer)after.get(ForumModel.PROP_COMMENT_COUNT) < (Integer)before.get(ForumModel.PROP_COMMENT_COUNT)){
                        businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator удалил(а) комментарий в документе \"#mainobject\"");
                    } else {
                        businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator оставил(а) комментарий в документе \"#mainobject\"");
                    }
                } else {
                    businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внес(ла) изменения в документ \"#mainobject\"");
                }
            } else {
                if (after.get(ForumModel.PROP_COMMENT_COUNT) != null) {
                    businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator оставил(а) комментарий в документе \"#mainobject\"");
                }
            }
        }

        updatePresentString(nodeRef);
        if (isChangeProperty(before, after, StatemachineModel.PROP_STATUS)) { //если изменили статус - фиксируем дату изменения и переформируем представление
            nodeService.setProperty(nodeRef,DocumentService.PROP_STATUS_CHANGED_DATE, new Date());
            if (stateMachineHelper.isDraft(nodeRef)) {
                String status = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATUS);
                List<String> objects = new ArrayList<String>(1);
                if (status != null) {
                    objects.add(status);
                }
                businessJournalService.log(nodeRef, EventCategory.ADD, "#initiator создал(а) новый документ \"#mainobject\" в статусе \"#object1\"", objects);
            }
        }
    }

    private void updatePresentString(final NodeRef nodeRef) {
        String presentString = "{cm:name}";

        QName type = nodeService.getType(nodeRef);
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_PRESENT_STRING));
        if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof PresentStringConstraint)) {
            PresentStringConstraint psConstraint = (PresentStringConstraint) constraint.getConstraint();
            if (psConstraint.getPresentString() != null) {
                presentString = psConstraint.getPresentString();
            }
        }

        final String finalPresentString = presentString;
        final AuthenticationUtil.RunAsWork<String> stringValue = new AuthenticationUtil.RunAsWork<String>() {
            @Override
            public String doWork() throws Exception {
                return substituteService.formatNodeTitle(nodeRef, finalPresentString);
            }
        };

        String presentStringValue = AuthenticationUtil.runAsSystem(stringValue);
        if (presentStringValue != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_PRESENT_STRING, presentStringValue);
            nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, FileNameValidator.getValidFileName(presentStringValue + " " + nodeRef.getId()));
        }
        String listPresentString = substituteService.getTemplateStringForObject(nodeRef, true);

        String listPresentStringValue = substituteService.formatNodeTitle(nodeRef, listPresentString);
        if (listPresentStringValue != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_LIST_PRESENT_STRING, listPresentStringValue);
        }

    }

    private boolean changeIgnoredProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
        for (QName ignored : IGNORED_PROPERTIES) {
            if (isChangeProperty(before, after, ignored)) return true;
        }
        return false;
    }

    private boolean isChangeProperty(Map<QName, Serializable> before, Map<QName, Serializable> after, QName prop) {
        Object prev = before.get(prop);
        Object cur = after.get(prop);
        return cur != null && !cur.equals(prev);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        updatePresentString(childAssocRef.getChildRef()); // при создании onUpdateproperties ещё не срабатывает - заполняем поле с представлением явно
        final NodeRef employeeRef = orgstructureService.getCurrentEmployee();
        nodeService.setProperty(childAssocRef.getChildRef(), DocumentService.PROP_DOCUMENT_CREATOR, substituteService.getObjectDescription(employeeRef));
        nodeService.setProperty(childAssocRef.getChildRef(), DocumentService.PROP_DOCUMENT_CREATOR_REF, employeeRef.toString());

	    // Добавление прав инициатора
	    NodeRef docRef = childAssocRef.getChildRef();
	    String authorLogin = authenticationService.getCurrentUserName();
	    NodeRef employee = orgstructureService.getEmployeeByPerson(authorLogin);
	    lecmPermissionService.grantDynamicRole(GRAND_DYNAMIC_ROLE_CODE_INITIATOR, docRef, employee.getId(), lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Initiator) );
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
