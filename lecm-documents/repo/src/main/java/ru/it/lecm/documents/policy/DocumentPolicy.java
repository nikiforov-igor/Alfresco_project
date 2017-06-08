package ru.it.lecm.documents.policy;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.model.RenditionModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.M2Label;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.i18n.StaticMessageLookup;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.dictionary.*;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.*;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.documents.constraints.AuthorPropertyConstraint;
import ru.it.lecm.documents.constraints.PresentStringConstraint;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.InputStream;
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
    private static final String DOCUMENT_SEARCH_CONTENT_THUMBNAIL_PATH = "/alfresco/templates/webscripts/ru/it/lecm/documents/icons/";
    final private QName[] IGNORED_PROPERTIES = {DocumentService.PROP_RATING, DocumentService.PROP_RATED_PERSONS_COUNT, StatemachineModel.PROP_STATUS, StatemachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS};
	final private MessageLookup staticMessageLookup = new StaticMessageLookup();

    private PolicyComponent policyComponent;
    private BusinessJournalService businessJournalService;
    private DictionaryService dictionaryService;
    private SubstitudeBean substituteService;
    private AuthenticationService authenticationService;
    private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineService;
	private LecmPermissionService lecmPermissionService;
    private PermissionService permissionService;
    private AuthorityService authorityService;
    private DocumentConnectionServiceImpl documentConnectionService;
    private DocumentMembersServiceImpl documentMembersService;
    private NamespaceService namespaceService;
	private TemplateService templateService;
	private BehaviourFilter behaviourFilter;
	private RegNumbersService regNumbersService;
    private DocumentService documentService;
	private MessageService messageService;
	private LecmMessageService lecmMessageService;

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

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        this.stateMachineService = stateMachineService;
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

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setLecmMessageService(LecmMessageService lecmMessageService) {
		this.lecmMessageService = lecmMessageService;
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
    public void documentTransmit(NodeRef documentRef, Map<QName, Serializable> before, Map<QName, Serializable> after, QName authorPropertyQName) throws WriteTransactionNeededException {
    	logger.debug("ДОКУМЕНТ. documentTransmit");
        NodeRef beforeAuthor = new NodeRef(before.get(authorPropertyQName).toString());
        NodeRef afterAuthor = new NodeRef(after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF).toString());
        Set<AccessPermission> permissionsDoc = permissionService.getAllSetPermissions(documentRef);
        Set<String> permissionsEmployee = authorityService.getAuthoritiesForUser(orgstructureService.getEmployeeLogin(beforeAuthor));
        //TODO DONE замена нескольких setProperty на setProperties.
        Map<QName, Serializable> properties = nodeService.getProperties(documentRef);
        if (authorPropertyQName.equals(DocumentService.PROP_DOCUMENT_CREATOR_REF)) {
            properties.put(DocumentService.PROP_DOCUMENT_CREATOR, substituteService.getObjectDescription(afterAuthor));
        }
        properties.put(authorPropertyQName, afterAuthor.toString());
        properties.put(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF, "");
        nodeService.setProperties(documentRef, properties);
        //DONE
        // добавляем в участники документа нового сотрудника
        documentMembersService.addMember(documentRef, afterAuthor, new HashMap<QName, Serializable>());
        // передаем задачи по документу
        stateMachineService.transferRightTask(documentRef, orgstructureService.getEmployeeLogin(beforeAuthor), orgstructureService.getEmployeeLogin(afterAuthor));


        // Проверяем выбран ли пункт лишать прав автора документа, если нет то добавляем бывшего автора в читатели документа и осталяем в участниках
        if (after.get(DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT).toString().equals("true")) {
            // удаляем из участников документа
            documentMembersService.deleteMember(documentRef, beforeAuthor);
        } else {
            lecmPermissionService.grantAccess(lecmPermissionService.findPermissionGroup(LecmPermissionService.LecmPermissionGroup.PGROLE_Reader), documentRef, beforeAuthor);
        }

	    List<String> objects = new ArrayList<String>(1);
	    objects.add(afterAuthor.toString());
	    businessJournalService.log(documentRef, EventCategory.TRANSMIT_DOCUMENT, "#initiator передал документ \"#mainobject\" сотруднику #object1", objects);

        // Нужно ли передовать права на документы введенные на основании
        if (after.get(DocumentService.PROP_DOCUMENT_IS_TRANSMIT).toString().equals("true")) {
            // переопределяем права на документы к договору.
            // для этого получаем список документов из папочки Связи и берем только документы с "Системной" связью
            NodeRef rootLinks = documentConnectionService.getRootFolder(documentRef);
            List<ChildAssociationRef> links = new ArrayList<ChildAssociationRef>();
            if (null != rootLinks){ //TODO Рефакторинг AL-2733
                    links = nodeService.getChildAssocs(rootLinks);
            }

//            List<NodeRef> additionalDocuments = documentConnectionService.getConnectionsWithDocument(documentRef);
            for (ChildAssociationRef link : links) {
                if (nodeService.getProperty(link.getChildRef(), DocumentConnectionService.PROP_IS_SYSTEM) != null) {
                    List<AssociationRef> addDoc = nodeService.getTargetAssocs(link.getChildRef(),DocumentConnectionService.ASSOC_CONNECTED_DOCUMENT);
                    if (addDoc.size() > 0) {
                        // присваиваем значения property документу к договору, чтобы инициализировать policy уже для
                        // документа к договору при это устанавливаем значения как и основном документе
                        //TODO DONE замена нескольких setProperty на setProperties.
                        properties = nodeService.getProperties(addDoc.get(0).getTargetRef());
                        properties.put(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF, afterAuthor.toString());
                        properties.put(DocumentService.PROP_DOCUMENT_IS_TRANSMIT, after.get(DocumentService.PROP_DOCUMENT_IS_TRANSMIT).toString());
                        properties.put(DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT, after.get(DocumentService.PROP_DOCUMENT_DEPRIVE_RIGHT).toString());
                        nodeService.setProperties(addDoc.get(0).getTargetRef(), properties);
                        //DONE
                    }
                }
            }

	        for (AccessPermission permission : permissionsDoc) {
		        if (permissionsEmployee.contains(permission.getAuthority()) && !PermissionService.ALL_AUTHORITIES.equals(permission.getAuthority())) {
			        if (permission.getAuthority().contains(Types.SFX_BRME) || permission.getAuthority().contains(Types.SFX_SPEC) || permission.getAuthority().contains(Types.SFX_PRIV4USER)) {
				        // удаляем динамическую роль
//                    lecmPermissionService.revokeDynamicRole(permission.getPermission(), documentRef, beforeAuthor.getId());
				        permissionService.clearPermission(documentRef, permission.getAuthority());
				        // назначаем динамическую роль другому сотруднику
				        lecmPermissionService.grantDynamicRole(permission.getPermission(), documentRef, afterAuthor.getId(), lecmPermissionService.findPermissionGroup(permission.getPermission()));
        }
		        }
	        }
        }

    }

    private QName getAuthorProperty(NodeRef nodeRef) {
    	logger.debug("ДОКУМЕНТ. getAuthorProperty");
        QName type = nodeService.getType(nodeRef);
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_AUTHOR_PROPERTY));
        return (constraint == null) ? null : QName.createQName(((AuthorPropertyConstraint)constraint.getConstraint()).getAuthorProperty(), namespaceService);
    }

    //TODO сложная длинная логика. Нужно разобраться, попытаться упростить.
    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, Map<QName, Serializable> after) {
    	logger.debug("ДОКУМЕНТ. onUpdateProperties");
        //изменилась одна из дат регистрации (проекта или документа)
        if (isChangeProperty(before, after, DocumentService.PROP_REG_DATA_DOC_DATE)
                || isChangeProperty(before, after, DocumentService.PROP_REG_DATA_PROJECT_DATE)) {
            Date afterDocDate = (Date) after.get(DocumentService.PROP_REG_DATA_DOC_DATE);
            Date afterPrjDate = (Date) after.get(DocumentService.PROP_REG_DATA_PROJECT_DATE);
            if (afterDocDate != null) {
                setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_DATE, afterDocDate);
                after.put(DocumentService.PROP_DOCUMENT_DATE, afterDocDate);
            } else if (afterPrjDate != null) {
                setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_DATE, afterPrjDate);
                after.put(DocumentService.PROP_DOCUMENT_DATE, afterPrjDate);
            }
        }
        //изменился один из номеров регистрации (проекта или документа)
        if (isChangeProperty(before, after, DocumentService.PROP_REG_DATA_DOC_NUMBER)
                || isChangeProperty(before, after, DocumentService.PROP_REG_DATA_PROJECT_NUMBER)) {
            String afterDocNumber = (String) after.get(DocumentService.PROP_REG_DATA_DOC_NUMBER);
            String afterPrjNumber = (String) after.get(DocumentService.PROP_REG_DATA_PROJECT_NUMBER);
            if (afterDocNumber != null) {
                setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_REGNUM, afterDocNumber);
                after.put(DocumentService.PROP_DOCUMENT_REGNUM, afterDocNumber);
            } else if (afterPrjNumber != null) {
                setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_REGNUM, afterPrjNumber);
                after.put(DocumentService.PROP_DOCUMENT_REGNUM, afterPrjNumber);
            }
        }
		/*
		 изменился регистрационный номер документа
		 есть вероятность, что его поменяли руками
		 надо проверить, что этот номер никому больше не присвоем
		 */
		if (isChangeProperty(before, after, DocumentService.PROP_DOCUMENT_REGNUM)) {
			final String documentRegNumber = (String) after.get(DocumentService.PROP_REG_DATA_DOC_NUMBER),
					projectRegNumber = (String) after.get(DocumentService.PROP_REG_DATA_PROJECT_NUMBER),
					newRegNumber = (String) after.get(DocumentService.PROP_DOCUMENT_REGNUM);
			if (newRegNumber != null && !newRegNumber.equals("Не присвоено") && !newRegNumber.equals(documentRegNumber) && !newRegNumber.equals(projectRegNumber)) {
				QName documentType = nodeService.getType(nodeRef);
				Date regDate = (Date) nodeService.getProperty(nodeRef, DocumentService.PROP_REG_DATA_DOC_DATE);
				if (regDate == null) {
					regDate = new Date();
				}
				// изменился только актуальный регистрационный номер. кто-то поменял его руками.
				if (!regNumbersService.isNumberUnique(newRegNumber, documentType, regDate)) {
					// кто-то пытается записать уже существующий регистрационный номер. надо громко упасть
					throw new IllegalArgumentException(String.format("REGNUMBER_DUPLICATE_EXCEPTION Regnumber %s is already in use", newRegNumber));
				} else {
					// выясняем, откуда взялся регистрационный номер
					String oldRegNumber = (String) before.get(DocumentService.PROP_DOCUMENT_REGNUM);
					QName regNumberProp = null;
					if(oldRegNumber!=null) {
					if (oldRegNumber.equalsIgnoreCase(documentRegNumber)) {
						regNumberProp = DocumentService.PROP_REG_DATA_DOC_NUMBER;
					} else if (oldRegNumber.equalsIgnoreCase(projectRegNumber)) {
						regNumberProp = DocumentService.PROP_REG_DATA_PROJECT_NUMBER;
					} else {
						// что-то пошло не так
						throw new IllegalStateException(String.format("Error persisting regnumber %s", documentRegNumber));
					}
					} else {
						regNumberProp = DocumentService.PROP_REG_DATA_DOC_NUMBER;
					}
					final String newRegNumberTrimmedUpper = StringUtils.trim(newRegNumber).toUpperCase();
					setPropertyAsSystem(nodeRef, regNumberProp, newRegNumberTrimmedUpper);
					if (!newRegNumber.equals(newRegNumberTrimmedUpper)) {
						setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_REGNUM, newRegNumberTrimmedUpper);
					}
				}
			}
		}

		if(nodeService.exists(nodeRef)) {
        	NodeRef employeeRef = orgstructureService.getCurrentEmployee();
	        if (employeeRef != null) {
	            setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER, substituteService.getObjectDescription(employeeRef));
	            setPropertyAsSystem(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER_REF, employeeRef.toString());
	        }
	        QName authorPropertyQName = getAuthorProperty(nodeRef);
	        if (authorPropertyQName == null) {
	            authorPropertyQName = DocumentService.PROP_DOCUMENT_CREATOR_REF;
	        }
	        if (before.get(authorPropertyQName) != null && !before.get(authorPropertyQName).equals("") &&
			        after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF) != null && !after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF).equals("")) {
	            if (!before.get(authorPropertyQName).equals(after.get(DocumentService.PROP_DOCUMENT_EMPLOYEE_REF))) {
                        try {
                            //OnUpdatePropertiesPolicy : транзакция должна быть.
                            documentTransmit(nodeRef, before, after, authorPropertyQName);
                        } catch (WriteTransactionNeededException ex) {
                            throw new RuntimeException(ex);
                        }


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

		    NodeRef documentSearchObject = documentService.getDocumentSearchObject(nodeRef);

		    updatePresentString(nodeRef);

		    if (documentSearchObject == null) {
			    documentSearchObject = documentService.getDocumentSearchObject(nodeRef);
		    }
		    if (documentSearchObject != null) {
			    behaviourFilter.disableBehaviour(documentSearchObject, RenditionModel.ASPECT_RENDITIONED);
			    try{
				    updateDocumentSearchObject(nodeRef, documentSearchObject);
				    createDocumentSearchObjectThumbnail(documentSearchObject, nodeRef);
			    } finally {
					behaviourFilter.enableBehaviour(documentSearchObject, RenditionModel.ASPECT_RENDITIONED);
				}
		    }

	        if (isChangeProperty(before, after, StatemachineModel.PROP_STATUS)) { //если изменили статус - фиксируем дату изменения и переформируем представление
	            setPropertyAsSystem(nodeRef, DocumentService.PROP_STATUS_CHANGED_DATE, new Date());
	        }
		}
    }


    //TODO ???Оно надо???
    private void setPropertyAsSystem(final NodeRef nodeRef, final QName property, final Serializable value) {
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                nodeService.setProperty(nodeRef, property, value);
                return null;
            }
        });
    }


    //TODO сложная длинная логика. Нужно разобраться, попытаться упростить.
    private void updatePresentString(final NodeRef nodeRef) {
    	logger.debug("ДОКУМЕНТ. updatePresentString");
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
		presentStringValue  = presentStringValue.replaceAll("\r", " ").replaceAll("\n", " ");
        if (presentStringValue != null) {
            setPropertyAsSystem(nodeRef, DocumentService.PROP_PRESENT_STRING, presentStringValue);

            TypeDefinition typeDef = dictionaryService.getType(type);
			updateMLPresentString(typeDef, nodeRef, presentStringValue);

	        if (presentStringValue.endsWith(".")) {
		        presentStringValue = presentStringValue.substring(0, presentStringValue.length() - 1);
	        }
            setPropertyAsSystem(nodeRef, ContentModel.PROP_NAME, FileNameValidator.getValidFileName(presentStringValue + " " + nodeRef.getId()));

            setPropertyAsSystem(nodeRef, ContentModel.PROP_TITLE, presentStringValue);

            setPropertyAsSystem(nodeRef, DocumentService.PROP_EXT_PRESENT_STRING, typeDef.getTitle() + ": " + presentStringValue);
        }

		final AuthenticationUtil.RunAsWork<String> listStringValue = new AuthenticationUtil.RunAsWork<String>() {
            @Override
            public String doWork() throws Exception {
                return substituteService.getTemplateStringForObject(nodeRef, true);
            }
        };

        String listPresentString = AuthenticationUtil.runAsSystem(listStringValue);

        String listPresentStringValue = substituteService.formatNodeTitle(nodeRef, listPresentString);
        if (listPresentStringValue != null) {
            setPropertyAsSystem(nodeRef, DocumentService.PROP_LIST_PRESENT_STRING, listPresentStringValue);
        }

    }

	private void updateMLPresentString(final TypeDefinition typeDef, final NodeRef nodeRef, final String presentStringValue) {
		logger.debug("ДОКУМЕНТ. updateMLPresentString");
		if (lecmMessageService.isMlSupported()) {
			String typename = typeDef.getName().toPrefixString(namespaceService).replace(':', '_');
			String propname = DocumentService.PROP_ML_PRESENT_STRING.toPrefixString(namespaceService).replace(':', '_');
			String messageKey = String.format("%s.property.%s.value", typename, propname);
			List<Locale> locales = lecmMessageService.getMlLocales();
			List<Locale> fallback = lecmMessageService.getFallbackLocales();
			MLPropertyInterceptor.setMLAware(true);
			MLText mlText = (MLText)nodeService.getProperty(nodeRef, DocumentService.PROP_ML_PRESENT_STRING);
			MLText mlExtText = (MLText)nodeService.getProperty(nodeRef, DocumentService.PROP_ML_EXT_PRESENT_STRING);
			mlText = mlText != null ? mlText : new MLText();
			mlExtText = mlExtText != null ? mlExtText : new MLText();
			for (Locale locale : fallback) {
				String typeValue = M2Label.getLabel(locale, typeDef.getModel(), staticMessageLookup, "type", typeDef.getName(), "title");
				mlText.addValue(locale, presentStringValue);
				mlExtText.addValue(locale, typeValue + ": " + presentStringValue);
			}
			for (Locale locale : locales) {
				final String presentString = StringEscapeUtils.unescapeJava(messageService.getMessage(messageKey, locale));
				if (presentString != null) {
					String value = AuthenticationUtil.runAsSystem(new RunAsWork<String> () {
						@Override
						public String doWork() throws Exception {
							return substituteService.formatNodeTitle(nodeRef, presentString);
						}
					});
					if (value != null) {
						value = value.replaceAll("\r", " ").replaceAll("\n", " ");
						String typeValue = M2Label.getLabel(locale, typeDef.getModel(), staticMessageLookup, "type", typeDef.getName(), "title");
						mlText.addValue(locale, value);
						mlExtText.addValue(locale, typeValue + ": " + value);
					}
				}
			}
			setPropertyAsSystem(nodeRef, DocumentService.PROP_ML_PRESENT_STRING, mlText);
			setPropertyAsSystem(nodeRef, DocumentService.PROP_ML_EXT_PRESENT_STRING, mlExtText);
			MLPropertyInterceptor.setMLAware(false);
		}
	}

    private boolean changeIgnoredProperties(Map<QName, Serializable> before, Map<QName, Serializable> after) {
    	logger.debug("ДОКУМЕНТ. changeIgnoredProperties");
        for (QName ignored : IGNORED_PROPERTIES) {
            if (isChangeProperty(before, after, ignored)) return true;
        }
        return false;
    }

    private boolean isChangeProperty(Map<QName, Serializable> before, Map<QName, Serializable> after, QName prop) {
    	logger.debug("ДОКУМЕНТ. isChangeProperty");
        Object prev = before.get(prop);
        Object cur = after.get(prop);
        return cur != null && !cur.equals(prev);
    }

    @Override
    //TODO сложная длинная логика. Нужно разобраться, попытаться упростить.
    public void onCreateNode(ChildAssociationRef childAssocRef) {
    	logger.debug("ДОКУМЕНТ. onCreateNode");
	    NodeRef document = childAssocRef.getChildRef();

        final QName type = nodeService.getType(document);

	    if (!AuthenticationUtil.getFullyAuthenticatedUser().equals(AuthenticationUtil.SYSTEM_USER_NAME) && !stateMachineService.isStarter(type.toPrefixString(namespaceService))) {
		    throw new AlfrescoRuntimeException("User not starter for document type '" + type + "' for node " + document);
	    }

        updatePresentString(document); // при создании onUpdateproperties ещё не срабатывает - заполняем поле с представлением явно

        Map<QName, Serializable> properties = nodeService.getProperties(childAssocRef.getChildRef());

        // заполняем тип
        final TypeDefinition typeDef = dictionaryService.getType(type);
        if (typeDef != null) {
            typeDef.getTitle();
            properties.put(DocumentService.PROP_DOCUMENT_TYPE, typeDef.getTitle());
        }

        String creator = nodeService.getProperty(document, ContentModel.PROP_CREATOR).toString();
        final NodeRef author = orgstructureService.getEmployeeByPerson(creator);

        properties.put(DocumentService.PROP_DOCUMENT_CREATOR, substituteService.getObjectDescription(author));
        properties.put(DocumentService.PROP_DOCUMENT_CREATOR_REF, author.toString());
        if(properties.get(DocumentService.PROP_DOCUMENT_DATE)==null){
        properties.put(DocumentService.PROP_DOCUMENT_DATE, new Date());
        properties.put(DocumentService.PROP_DOCUMENT_REGNUM, DocumentService.DEFAULT_REG_NUM);
            nodeService.setProperties(childAssocRef.getChildRef(), properties); // нельзя вызывать после createAssociation
        //DONE
        nodeService.createAssociation(childAssocRef.getChildRef(), author, DocumentService.ASSOC_AUTHOR);
        } else {
            nodeService.setProperties(childAssocRef.getChildRef(), properties); // нельзя вызывать после createAssociation
        }

        // Приписывание документа к организации
        NodeRef contractor = orgstructureService.getEmployeeOrganization(author);
        if (contractor != null) {
            nodeService.addAspect(document, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION, null);
            nodeService.createAssociation(document, contractor, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
        }

        NodeRef attachmentsRef = nodeService.getChildByName(childAssocRef.getChildRef(), ContentModel.ASSOC_CONTAINS, DocumentAttachmentsService.DOCUMENT_ATTACHMENTS_ROOT_NAME);
        if (attachmentsRef == null) {
            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, DocumentAttachmentsService.DOCUMENT_ATTACHMENTS_ROOT_NAME);
            QName nodeTypeQName = ContentModel.TYPE_FOLDER;

            properties = new HashMap<QName, Serializable>(1);
            properties.put(ContentModel.PROP_NAME, DocumentAttachmentsService.DOCUMENT_ATTACHMENTS_ROOT_NAME);
            ChildAssociationRef associationRef = nodeService.createNode(childAssocRef.getChildRef(), assocTypeQName, assocQName, nodeTypeQName, properties);
            attachmentsRef = associationRef.getChildRef();
            //не индексируем свойства папки
            disableNodeIndex(attachmentsRef);
        }

        NodeRef documentSearchObject = documentService.getDocumentSearchObject(childAssocRef.getChildRef());
        if (documentSearchObject != null) {
            updateDocumentSearchObject(childAssocRef.getChildRef(), documentSearchObject);
        }
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

    public void updateDocumentSearchObject(final NodeRef documentRef, final NodeRef objectRef) {
    	logger.debug("ДОКУМЕНТ. updateDocumentSearchObject");
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                if (objectRef != null) {
                    String newFileName = FileNameValidator.getValidFileName((String) nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING));
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
                                params.getSharePort() +	documentService.getDocumentUrl(documentRef) + "?nodeRef=" + documentRef.toString();
                        model.put("documentLink", documentLink);
                        model.put("documentName", nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING));

                        writer.putContent(templateService.processTemplate(DOCUMENT_SEARCH_CONTENT_TEMPLATE, model));
                    }
                }
                return null;
            }
        });
    }

	public Map<String, Serializable> getDocumentSearchProperties(NodeRef documentRef) {
		logger.debug("ДОКУМЕНТ. getDocumentSearchProperties");
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
                        PropertyDefinition propDef = dictionaryService.getProperty(prop);
                        if (propDef != null) {
                            propTitle = propDef.getTitle();
                        }
					}

					if (propTitle != null) {
						result.put(propTitle, doumentProperties.get(prop));
					}
				}
			}
		}

		return result;
	}

	public NodeRef createDocumentSearchObjectThumbnail(final NodeRef objectRef, final NodeRef documentRef) {
		logger.debug("ДОКУМЕНТ. createDocumentSearchObjectThumbnail");
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
//					TODO: Метод явно вызывается только в onUpdateProperties, транзакция уже должна быть открыта
					String thumbnailName = "doclib";
					NodeRef thumbnail = serviceRegistry.getThumbnailService().getThumbnailByName(objectRef, ContentModel.PROP_CONTENT, thumbnailName);
					if (thumbnail == null) {
						QName assocTypeQName = RenditionModel.ASSOC_RENDITION;
						QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, thumbnailName);
						QName nodeTypeQName = ContentModel.TYPE_THUMBNAIL;

						Map<QName, Serializable> properties = new HashMap<QName, Serializable>(4);
						properties.put(ContentModel.PROP_NAME, thumbnailName);
						properties.put(ContentModel.PROP_THUMBNAIL_NAME, thumbnailName);
						properties.put(ContentModel.PROP_CONTENT_PROPERTY_NAME, ContentModel.PROP_CONTENT);
						ChildAssociationRef associationRef = nodeService.createNode(objectRef, assocTypeQName, assocQName, nodeTypeQName, properties);
						thumbnail = associationRef.getChildRef();

						NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
						String documentType = nodeService.getType(documentRef).toPrefixString(namespacePrefixResolver).replace(":", "_");

						InputStream stream = this.getClass().getResourceAsStream(DOCUMENT_SEARCH_CONTENT_THUMBNAIL_PATH + documentType + ".png");
						if (stream == null) {
							stream = this.getClass().getResourceAsStream(DOCUMENT_SEARCH_CONTENT_THUMBNAIL_PATH + "default_document.png");
						}
						if (stream != null){
							ContentService contentService = serviceRegistry.getContentService();
							ContentWriter writer = contentService.getWriter(thumbnail, ContentModel.PROP_CONTENT, true);
							if (writer != null) {
								writer.setMimetype(MimetypeMap.MIMETYPE_IMAGE_PNG);
								writer.putContent(stream);
							}
						}
					}
					return thumbnail;

//					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//						@Override
//						public NodeRef execute() throws Throwable {
//		                    String thumbnailName = "doclib";
//							NodeRef thumbnail = serviceRegistry.getThumbnailService().getThumbnailByName(objectRef, ContentModel.PROP_CONTENT, thumbnailName);
//                            if (thumbnail == null) {
//                                QName assocTypeQName = RenditionModel.ASSOC_RENDITION;
//                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, thumbnailName);
//                                QName nodeTypeQName = ContentModel.TYPE_THUMBNAIL;
//
//                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(4);
//                                properties.put(ContentModel.PROP_NAME, thumbnailName);
//                                properties.put(ContentModel.PROP_THUMBNAIL_NAME, thumbnailName);
//                                properties.put(ContentModel.PROP_CONTENT_PROPERTY_NAME, ContentModel.PROP_CONTENT);
//                                ChildAssociationRef associationRef = nodeService.createNode(objectRef, assocTypeQName, assocQName, nodeTypeQName, properties);
//                                thumbnail = associationRef.getChildRef();
//
//                                NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
//                                String documentType = nodeService.getType(documentRef).toPrefixString(namespacePrefixResolver).replace(":", "_");
//
//                                InputStream stream = this.getClass().getResourceAsStream(DOCUMENT_SEARCH_CONTENT_THUMBNAIL_PATH + documentType + ".png");
//                                if (stream == null) {
//                                    stream = this.getClass().getResourceAsStream(DOCUMENT_SEARCH_CONTENT_THUMBNAIL_PATH + "default_document.png");
//                                }
//                                if (stream != null){
//                                    ContentService contentService = serviceRegistry.getContentService();
//                                    ContentWriter writer = contentService.getWriter(thumbnail, ContentModel.PROP_CONTENT, true);
//                                    if (writer != null) {
//                                        writer.setMimetype(MimetypeMap.MIMETYPE_IMAGE_PNG);
//                                        writer.putContent(stream);
//                                    }
//                                }
//							}
//							return thumbnail;
//						}
//					});
				}
			};

        return AuthenticationUtil.runAsSystem(raw);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
