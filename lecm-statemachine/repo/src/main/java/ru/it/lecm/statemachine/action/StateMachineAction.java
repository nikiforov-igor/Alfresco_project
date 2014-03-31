package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.TimerActionHelper;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:29
 */
abstract public class StateMachineAction {

    public static final String TAG_EXPRESSIONS = "expressions";
    public static final String TAG_EXPRESSION = "expression";

    public static final String PROP_EXPRESSION = "expression";
    public static final String PROP_OUTPUT_VARIABLE = "outputVariable";
    public static final String PROP_OUTPUT_VALUE = "outputValue";

	private ServiceRegistry serviceRegistry;
	private LecmPermissionService lecmPermissionService;
	private BusinessJournalService businessJournalService;
	private RepositoryStructureHelper repositoryStructureHelper;
    private TimerActionHelper timerActionHelper;
    private OrgstructureBean orgstructureBean;
    private DocumentService documentService;
    private PermissionService permissionService;

    public TimerActionHelper getTimerActionHelper() {
        return timerActionHelper;
    }

    public void setTimerActionHelper(TimerActionHelper timerActionHelper) {
        this.timerActionHelper = timerActionHelper;
    }

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public LecmPermissionService getLecmPermissionService() {
		return lecmPermissionService;
	}

	public void setLecmPermissionService(LecmPermissionService value) {
		this.lecmPermissionService = value;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public BusinessJournalService getBusinessJournalService() {
		return businessJournalService;
	}

    public RepositoryStructureHelper getRepositoryStructureHelper() {
        return repositoryStructureHelper;
    }

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public OrgstructureBean getOrgstructureBean() {
        return orgstructureBean;
    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    abstract public void execute(DelegateExecution execution);

	abstract public void init(Element actionElement, String processId);

	public String getActionName() {
		return StateMachineActionsImpl.getActionNameByClass(getClass());
	}

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    protected NodeRef createFolder(NodeRef parent, String name) {
		return createFolder(parent, name, null);
	}

	protected NodeRef createFolder(final NodeRef parent, final String name, String uuid) {
		final NodeService nodeService = getServiceRegistry().getNodeService();
		final HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
		props.put(ContentModel.PROP_NAME, name);
		if (uuid != null) {
			props.put(ContentModel.PROP_NODE_UUID, uuid);
		}
		ChildAssociationRef childAssocRef = serviceRegistry.getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
			@Override
			public ChildAssociationRef execute() throws Throwable {
				ChildAssociationRef childAssocRef = nodeService.createNode(
						parent,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
						ContentModel.TYPE_FOLDER,
						props);
				return childAssocRef;
			}
		}, false, true);
		return childAssocRef.getChildRef();
	}

}
