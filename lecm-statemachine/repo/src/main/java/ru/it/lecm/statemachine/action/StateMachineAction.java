package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
//import org.activiti.engine.impl.util.xml.Element;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
//import ru.it.lecm.statemachine.TimerActionHelper;
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
    
    private LifecycleStateMachineHelper stateMachineHelper;
    
    private static final transient Logger logger = LoggerFactory.getLogger(StateMachineAction.class);

	public ServiceRegistry getServiceRegistry() {
		ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
		if (config != null) 
		{
			// Fetch the registry that is injected in the activiti spring-configuration
			ServiceRegistry registry = (ServiceRegistry) config.getBeans().get(ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
			if (registry == null)
			{
				throw new RuntimeException(
						"Service-registry not present in ProcessEngineConfiguration beans, expected ServiceRegistry with key" + 
								ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
			}
			return registry;
		}
		throw new IllegalStateException("No ProcessEngineCOnfiguration found in active context");
	}
	
	public DocumentMembersService getDocumentMembersService() {
		WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
		DocumentMembersService documentMembersService = (DocumentMembersService) ctx.getBean("documentMembersService");
        return documentMembersService;
    }
	
//	public TimerActionHelper getTimerActionHelper() {
//    	WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
//    	TimerActionHelper timerActionHelper = (TimerActionHelper) ctx.getBean("timerActionHelper");
//        return timerActionHelper;
//    }

	public LecmPermissionService getLecmPermissionService() {
		WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
		LecmPermissionService lecmPermissionService = (LecmPermissionService) ctx.getBean("lecmPermissionServiceBean");
		return lecmPermissionService;
	}

	public BusinessJournalService getBusinessJournalService() {
		WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
		BusinessJournalService businessJournalService = (BusinessJournalService) ctx.getBean("businessJournalService");
		return businessJournalService;
	}

    public RepositoryStructureHelper getRepositoryStructureHelper() {
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        RepositoryStructureHelper repositoryStructureHelper = (RepositoryStructureHelper) ctx.getBean("repositoryStructureHelper");
		return repositoryStructureHelper;
    }

    public OrgstructureBean getOrgstructureBean() {
    	WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
    	OrgstructureBean orgstructureBean = (OrgstructureBean) ctx.getBean("serviceOrgstructure");
        return orgstructureBean;
    }

    public DocumentService getDocumentService() {
    	WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
    	DocumentService documentService = (DocumentService) ctx.getBean("documentService");
        return documentService;
    }

    public LifecycleStateMachineHelper getStateMachineHelper() {
    	WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
    	LifecycleStateMachineHelper stateMachineHelper = (LifecycleStateMachineHelper) ctx.getBean("lifecycleStateMachineHelper");
        return stateMachineHelper;
    }

    public void setStateMachineHelper(LifecycleStateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    abstract public void execute(DelegateExecution execution);

	abstract public void init(BaseElement actionElement, String processId);

	public String getActionName() {
		return StateMachineActionsImpl.getActionNameByClass(getClass());
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
		try {
			ChildAssociationRef childAssocRef = getServiceRegistry().getTransactionService().getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<ChildAssociationRef>() {
				@Override
				public ChildAssociationRef execute() throws Throwable {
					return nodeService.createNode(
							parent,
							ContentModel.ASSOC_CONTAINS,
							QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
							ContentModel.TYPE_FOLDER,
							props);
				}
			}, false, true);
			logger.debug("!!!!!!!!!!! Создал ноду для стстуса "+name);
			return childAssocRef.getChildRef();
		} catch(DuplicateChildNodeNameException e) {
			NodeRef subFolder = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, name);
			logger.debug("!!!!!!!!!!! Получил ноду для стстуса "+name, e);
			return subFolder;
		} catch(Exception e) {
			NodeRef subFolder = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, name);
			logger.debug("!!!!!!!!!!! Получил ноду для стстуса "+name, e);
			return subFolder;
		}
	}

}
