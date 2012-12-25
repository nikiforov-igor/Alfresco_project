package ru.it.lecm.statemachine;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationContext;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.workflow.WorkflowDeployment;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 26.10.12
 * Time: 11:51
 */
public class LecmWorkflowDeployer extends AbstractLifecycleBean {

	private WorkflowService workflowService;
	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private NodeService nodeService;
	private Repository repositoryHelper;
	private ContentService contentService;
	private AuthenticationContext authenticationContext;
	private TransactionService transactionService;


	private static final String ENGINE_ID = "activiti";
	private static final String MIMETYPE = "text/xml";
	private static final String WORKFLOW_FOLDER = "workflow";

	public void init() {
		redeploy();
	}

	public void redeploy() {
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "authenticationContext", authenticationContext);
		PropertyCheck.mandatory(this, "workflowService", workflowService);

		UserTransaction userTransaction = transactionService.getUserTransaction();

		try {
			userTransaction.begin();
			NodeRef companyHome = repositoryHelper.getCompanyHome();
			NodeRef workflowRef = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, WORKFLOW_FOLDER);
			if (workflowRef == null) {
				HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
				props.put(ContentModel.PROP_NAME, WORKFLOW_FOLDER);
				ChildAssociationRef childAssocRef = nodeService.createNode(
						companyHome,
						ContentModel.ASSOC_CONTAINS,
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(WORKFLOW_FOLDER)),
						ContentModel.TYPE_FOLDER,
						props);
				workflowRef = childAssocRef.getChildRef();
			}
			List<ChildAssociationRef> workflows = nodeService.getChildAssocs(workflowRef);
			for (ChildAssociationRef workflow : workflows) {
				ContentReader reader = contentService.getReader(workflow.getChildRef(), ContentModel.PROP_CONTENT);
				String fileName = (String) nodeService.getProperty(workflow.getChildRef(), ContentModel.PROP_NAME);
				InputStream is = reader.getContentInputStream();
				byte[] buf = new byte[1 << 8];
				int c = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((c = is.read(buf)) != -1) {
					baos.write(buf, 0, c);
				}
				byte[] bytes = baos.toByteArray();
				baos.close();
				if (workflowService.isDefinitionDeployed(ENGINE_ID, new ByteArrayInputStream(bytes), MIMETYPE)) {
					String key = getProcessKey(new ByteArrayInputStream(bytes));
					RepositoryService repositoryService = activitiProcessEngineConfiguration.getRepositoryService();
					ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
					if (definition != null) {
						InputStream latestResource = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
						String latestChecksum = getMD5Checksum(latestResource);
						String newChecksum = getMD5Checksum(new ByteArrayInputStream(bytes));
						if (!latestChecksum.equals(newChecksum)) {
							deploy(ENGINE_ID, MIMETYPE, new ByteArrayInputStream(bytes), fileName);
						}
					}
				} else {
					deploy(ENGINE_ID, MIMETYPE, new ByteArrayInputStream(bytes), fileName);
				}
			}
			userTransaction.commit();
		} catch (Exception e) {
			try {
				userTransaction.rollback();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			authenticationContext.clearCurrentSecurityContext();
		}
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setAuthenticationContext(AuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	private String getProcessKey(InputStream workflowDefinition) throws Exception {
		try {
			InputSource inputSource = new InputSource(workflowDefinition);
			DOMParser parser = new DOMParser();
			parser.parse(inputSource);
			Document document = parser.getDocument();
			NodeList elemnts = document.getElementsByTagName("process");
			if (elemnts.getLength() < 1) {
				throw new IllegalArgumentException("The input stream does not contain a process definition!");
			}
			NamedNodeMap attributes = elemnts.item(0).getAttributes();
			Node idAttrib = attributes.getNamedItem("id");
			if (idAttrib == null) {
				throw new IllegalAccessError("The process definition does not have an id!");
			}
			return idAttrib.getNodeValue();
		} finally {
			workflowDefinition.close();
		}
	}

	private String getMD5Checksum(InputStream is) throws IOException {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[8192];
			int read;
			while ((read = is.read(buffer)) > 0) {
				md.update(buffer, 0, read);
			}
			byte[] digest = md.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			result = bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			is.close();
		}
		return result;
	}

	@Override
	protected void onBootstrap(ApplicationEvent applicationEvent) {
		// run as System on bootstrap
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			public Object doWork() {
				init();
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}

	@Override
	protected void onShutdown(ApplicationEvent applicationEvent) {
	}

	private WorkflowDeployment deploy(String engineId, String mimetype, InputStream inputStream, String filename) throws IOException {
		return workflowService.deployDefinition(engineId, inputStream, mimetype, filename);
	}

}