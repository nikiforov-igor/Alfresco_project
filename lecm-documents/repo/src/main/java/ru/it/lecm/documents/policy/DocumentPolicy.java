package ru.it.lecm.documents.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
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
import ru.it.lecm.documents.constraints.AuthorPropertyConstraint;
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

    final static protected Logger logger = LoggerFactory.getLogger(DocumentPolicy.class);

    private static final String DOCUMENT_SEARCH_CONTENT_TEMPLATE = "/alfresco/templates/webscripts/ru/it/lecm/documents/document-search-object-content.ftl";
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
    private NamespaceService namespaceService;
	private TemplateService templateService;

	private final Object lock = new Object();

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

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
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
    public void documentTransmit(NodeRef documentRef, Map<QName, Serializable> before, Map<QName, Serializable> after, QName authorPropertyQName) {
        NodeRef beforeAuthor = new NodeRef(before.get(authorPropertyQName).toString());
        NodeRef afterAuthor = new NodeRef(after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF).toString());
        Set<AccessPermission> permissionsDoc = permissionService.getAllSetPermissions(documentRef);
        Set<String> permissionsEmployee = authorityService.getAuthoritiesForUser(orgstructureService.getEmployeeLogin(beforeAuthor));

        if (authorPropertyQName.equals(DocumentService.PROP_DOCUMENT_CREATOR_REF)) {
            nodeService.setProperty(documentRef, DocumentService.PROP_DOCUMENT_CREATOR, substituteService.getObjectDescription(afterAuthor));
        }
        nodeService.setProperty(documentRef, authorPropertyQName, afterAuthor.toString());
        nodeService.setProperty(documentRef, DocumentService.PROP_DOCUMENT_EMPLOYEE_REF, "");

        for (AccessPermission permission : permissionsDoc) {
            if (permissionsEmployee.contains(permission.getAuthority()) && !PermissionService.ALL_AUTHORITIES.equals(permission.getAuthority())) {
                if (permission.getAuthority().indexOf(Types.SFX_BRME) != -1 || permission.getAuthority().indexOf(Types.SFX_SPEC) != -1 || permission.getAuthority().indexOf(Types.SFX_PRIV4USER) != -1) {
                    // удаляем динамическую роль
//                    lecmPermissionService.revokeDynamicRole(permission.getPermission(), documentRef, beforeAuthor.getId());
                    permissionService.clearPermission(documentRef, permission.getAuthority());
                    // назначаем динамическую роль другому сотруднику
                    lecmPermissionService.grantDynamicRole(permission.getPermission(), documentRef, afterAuthor.getId(), lecmPermissionService.findPermissionGroup(permission.getPermission()));
                }
            }
        }

        // добавляем в участники документа нового сотрудника
        documentMembersService.addMember(documentRef, afterAuthor, new HashMap<QName, Serializable>());
        // передаем задачи по документу
        stateMachineHelper.transferRightTask(documentRef, orgstructureService.getEmployeeLogin(beforeAuthor), orgstructureService.getEmployeeLogin(afterAuthor));


        // Проверяем выбран ли пункт лишать прав автора документа, если нет то добавляем бывшего автора в читатели документа и осталяем в участниках
        if (after.get(DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT).toString().equals("true")) {
            // удаляем из участников документа
            documentMembersService.deleteMember(documentRef, beforeAuthor);
        } else {
            lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), documentRef, beforeAuthor.getId());
        }

	    List<String> objects = new ArrayList<String>(1);
	    objects.add(afterAuthor.toString());
	    businessJournalService.log(documentRef, EventCategory.TRANSMIT_DOCUMENT, "#initiator передал документ \"#mainobject\" сотруднику #object1", objects);

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

    private QName getAuthorProperty(NodeRef nodeRef) {
        QName type = nodeService.getType(nodeRef);
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_AUTHOR_PROPERTY));
        return (constraint == null) ? null : QName.createQName(((AuthorPropertyConstraint)constraint.getConstraint()).getAuthorProperty(), namespaceService);
    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final NodeRef employeeRef = orgstructureService.getCurrentEmployee();
        if (employeeRef != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER, substituteService.getObjectDescription(employeeRef));
            nodeService.setProperty(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER_REF, employeeRef.toString());
        }
        QName authorPropertyQName = getAuthorProperty(nodeRef);
        if (authorPropertyQName == null) {
            authorPropertyQName = DocumentService.PROP_DOCUMENT_CREATOR_REF;
        }
        if (before.get(authorPropertyQName) != null && !before.get(authorPropertyQName).equals("") &&
		        after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF) != null && !after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF).equals("")) {
            if (!before.get(authorPropertyQName).equals(after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF))) {
                documentTransmit(nodeRef, before, after, authorPropertyQName);
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

	    NodeRef documentSearchObject = getDocumentSearchObject(nodeRef);
        updatePresentString(nodeRef);
	    if (documentSearchObject != null) {
		    updateDocumentSearchObject(nodeRef, documentSearchObject);
	    }

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

	    NodeRef documentSearchObject = getDocumentSearchObject(childAssocRef.getChildRef());
	    if (documentSearchObject != null) {
		    updateDocumentSearchObject(childAssocRef.getChildRef(), documentSearchObject);
	    }
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public NodeRef getDocumentSearchObject(final NodeRef documentRef) {
		final String fileName = FileNameValidator.getValidFileName((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
		NodeRef result = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, fileName);
		if (result == null) {
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							NodeRef result;
							synchronized (lock) {
								result = nodeService.getChildByName(documentRef, ContentModel.ASSOC_CONTAINS, fileName);
								if (result == null) {
									QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
									QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, fileName);
									QName nodeTypeQName = ContentModel.TYPE_CONTENT;

									Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
									properties.put(ContentModel.PROP_NAME, fileName);
									ChildAssociationRef associationRef = nodeService.createNode(documentRef, assocTypeQName, assocQName, nodeTypeQName, properties);
									result = associationRef.getChildRef();
								}
							}
							return result;
						}
					});
				}
			};
			return AuthenticationUtil.runAsSystem(raw);
		} else {
			return result;
		}
	}

	public void updateDocumentSearchObject(NodeRef documentRef, NodeRef objectRef) {
		if (objectRef != null) {
			String newFileName = FileNameValidator.getValidFileName((String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));
			nodeService.setProperty(objectRef, ContentModel.PROP_NAME, newFileName);

			ContentService contentService = serviceRegistry.getContentService();
			ContentWriter writer = contentService.getWriter(objectRef, ContentModel.PROP_CONTENT, true);
			if (writer != null) {
				writer.setEncoding("UTF-8");
				writer.setMimetype(MimetypeMap.MIMETYPE_HTML);

				Map<String, Object> model = new HashMap<String, Object>();
				model.put("properties", getDocumentSearchProperties(documentRef));

				SysAdminParams params = serviceRegistry.getSysAdminParams();
				String documentLink = params.getShareProtocol() + "://" + params.getShareHost() + ":" +
						params.getSharePort() +	DOCUMENT_LINK_URL + "?nodeRef=" + documentRef.toString();
				model.put("documentLink", documentLink);
				model.put("documentName", nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING));

				writer.putContent(templateService.processTemplate(DOCUMENT_SEARCH_CONTENT_TEMPLATE, model));
			}
		}
	}

	public Map<String, Serializable> getDocumentSearchProperties(NodeRef documentRef) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Map<QName, Serializable> doumentProperties = nodeService.getProperties(documentRef);
		if (doumentProperties != null) {
			NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();

			for (QName prop: doumentProperties.keySet()) {
				if (!prop.getNamespaceURI().equals(NamespaceService.SYSTEM_MODEL_1_0_URI) && !prop.getLocalName().endsWith("-ref")) {
					String propTitle = null;
					if (prop.getLocalName().endsWith("-text-content")) {
						String assocShortName = prop.toPrefixString(namespacePrefixResolver).replace("-text-content", "");
						AssociationDefinition assocDefinition = dictionaryService.getAssociation(QName.createQName(assocShortName, namespacePrefixResolver));
						if (assocDefinition != null) {
						 	propTitle = assocDefinition.getTitle();
						}
					} else {
						propTitle = dictionaryService.getProperty(prop).getTitle();
					}

					if (propTitle != null) {
						result.put(propTitle, doumentProperties.get(prop));
					}
				}
			}
		}

		return result;
	}
}
