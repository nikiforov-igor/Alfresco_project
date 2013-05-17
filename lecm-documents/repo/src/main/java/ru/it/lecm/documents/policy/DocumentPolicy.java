package ru.it.lecm.documents.policy;

import org.alfresco.model.ForumModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.constraints.PresentStringConstraint;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 20.03.13
 * Time: 15:02
 */
public class DocumentPolicy extends BaseBean
        implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy {

    final static protected Logger logger = LoggerFactory.getLogger(DocumentPolicy.class);
    final private QName[] IGNORED_PROPERTIES = {DocumentService.PROP_RATING, DocumentService.PROP_RATED_PERSONS_COUNT, StatemachineModel.PROP_STATUS};

    private PolicyComponent policyComponent;
    private BusinessJournalService businessJournalService;
    private DictionaryService dictionaryService;
    private SubstitudeBean substituteService;
    private AuthenticationService authenticationService;
    private OrgstructureBean orgstructureService;
    private StateMachineServiceBean stateMachineHelper;

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

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final NodeRef employeeRef = orgstructureService.getCurrentEmployee();
        if (employeeRef != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER, substituteService.getObjectDescription(employeeRef));
            nodeService.setProperty(nodeRef, DocumentService.PROP_DOCUMENT_MODIFIER_REF, employeeRef.toString());
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

        if (isChangeProperty(before, after, StatemachineModel.PROP_STATUS)) { //если изменили статус - фиксируем дату изменения и переформируем представление
            nodeService.setProperty(nodeRef,DocumentService.PROP_STATUS_CHANGED_DATE, new Date());
            if (stateMachineHelper.isDraft(nodeRef)) {
                String status = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_STATUS);
                List<String> objects = new ArrayList<String>(1);
                if (status != null) {
                    objects.add(status);
                }
                businessJournalService.log(nodeRef, EventCategory.ADD, "Создан новый документ \"#mainobject\" в статусе \"#object1\"", objects);
            }
        }
        updatePresentString(nodeRef);
    }

    private void updatePresentString(NodeRef nodeRef) {
        String presentString = "{cm:name}";

        QName type = nodeService.getType(nodeRef);
        ConstraintDefinition constraint = dictionaryService.getConstraint(QName.createQName(type.getNamespaceURI(), DocumentService.CONSTRAINT_PRESENT_STRING));
        if (constraint != null && constraint.getConstraint() != null && (constraint.getConstraint() instanceof PresentStringConstraint)) {
            PresentStringConstraint psConstraint = (PresentStringConstraint) constraint.getConstraint();
            if (psConstraint.getPresentString() != null) {
                presentString = psConstraint.getPresentString();
            }
        }

        String presentStringValue = substituteService.formatNodeTitle(nodeRef, presentString);
        if (presentStringValue != null) {
            nodeService.setProperty(nodeRef, DocumentService.PROP_PRESENT_STRING, presentStringValue);
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
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
