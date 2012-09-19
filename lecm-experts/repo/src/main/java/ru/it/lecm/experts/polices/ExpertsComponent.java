package ru.it.lecm.experts.polices;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.it.lecm.experts.beans.GetExpertsBean;

/**
 * @author dbashmakov
 *         Date: 10.09.12
 *         Time: 11:16
 */
public class ExpertsComponent implements ContentServicePolicies.OnContentUpdatePolicy, NodeServicePolicies.OnCreateNodePolicy, VersionServicePolicies.AfterCreateVersionPolicy {

	private static Log logger = LogFactory.getLog(ExpertsComponent.class);
	private final static QName EXPERTS_ASPECT = QName.createQName("http://www.it.ru/logicECM/experts/1.0", "hasExperts");
	private final static QName EXPERTS_ASPECT_PROPERTY = QName.createQName("http://www.it.ru/logicECM/experts/1.0", "experts");

	private final static String AFFECTED_EXTENSIONS = "DOC,DOCX,PPTX,XLSX,TXT,PDF";

	private final long DELAY = 10000;

	private PolicyComponent exp_policyComponent;
	private NodeService exp_nodeService;
	private PersonService exp_personService;
	private TransactionService exp_transService;

	private static ServiceRegistry serviceRegistry;
	// Queue of node update events

	//private NodeEventQueue exp_eventQueue;

	public final void init() {
		PropertyCheck.mandatory(this, "nodeService", exp_nodeService);
		PropertyCheck.mandatory(this, "policyComponent", exp_policyComponent);

		exp_policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentUpdatePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onContentUpdate"));
		exp_policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "onCreateNode"));
		exp_policyComponent.bindClassBehaviour(VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
				ContentModel.TYPE_CONTENT, new JavaBehaviour(this, "afterCreateVersion"));
	}

	private boolean checkFilename(String fileName) {
		if (fileName != null && fileName.lastIndexOf(".") > 0) {
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			return AFFECTED_EXTENSIONS.contains(extension.toUpperCase());
		}
		return false;
	}

	@Override
	public void onContentUpdate(NodeRef nodeRef, boolean newContent) {
		if (exp_nodeService.hasAspect(nodeRef, EXPERTS_ASPECT) && newContent) {
			final String fileName = (String) exp_nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			final NodeRef ref = nodeRef;
			if (checkFilename(fileName)) {
				Thread getExpertsThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(DELAY);
							RetryingTransactionHelper.RetryingTransactionCallback<Object> processEventCallback =
									new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
										public Object execute() throws Throwable {
											getAndSaveExperts(ref);
											return null;
										}
									};
							exp_transService.getRetryingTransactionHelper().doInTransaction(processEventCallback, false, true);
						} catch (Exception ex) {
							logger.error(ex);
						}
					}
				});
				getExpertsThread.start();
			}
		}
	}

	private synchronized void getAndSaveExperts(final NodeRef nodeRef) {
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			public Object doWork() throws Exception {
				//get experts
				GetExpertsBean expertsBean = new GetExpertsBean();
				expertsBean.setByUri(false);

				String expertsString = expertsBean.get(nodeRef.toString());
				try {
					JSONArray expertsArray = new JSONArray(expertsString);
					for (int i = 0; i < expertsArray.length(); i++) {
						try {
							JSONObject expert = (JSONObject) expertsArray.get(i);
							String login = (String) expert.get(GetExpertsBean.ATTR_LNAME);

							// find system user - throw exception if not exist
							NodeRef personRef = exp_personService.getPerson(login, false);
							exp_nodeService.createAssociation(nodeRef, personRef, EXPERTS_ASPECT_PROPERTY);
						} catch (NoSuchPersonException ex) {
							logger.info(ex);
						} catch (AssociationExistsException ex) {
							logger.info(ex);
						} catch (InvalidNodeRefException ex) {
							logger.info(ex);
						} catch (Exception ex) {
							logger.info(ex);
						}
					}
				} catch (JSONException e) {
					logger.error(e);
				}
				return null;
			}
		}, AuthenticationUtil.SYSTEM_USER_NAME);
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		String fileName = (String) exp_nodeService.getProperty(childAssocRef.getChildRef(), ContentModel.PROP_NAME);
		if (checkFilename(fileName)) {
			// добавляем аспект, если ещё не добавлен
			if (!exp_nodeService.hasAspect(childAssocRef.getChildRef(), EXPERTS_ASPECT)) {
				exp_nodeService.addAspect(childAssocRef.getChildRef(), EXPERTS_ASPECT, null);
			}
		}
	}

	public TransactionService getExp_transService() {
		return exp_transService;
	}

	public void setExp_transService(TransactionService exp_transService) {
		this.exp_transService = exp_transService;
	}

	public PersonService getExp_personService() {
		return exp_personService;
	}

	public void setExp_personService(PersonService exp_personService) {
		this.exp_personService = exp_personService;
	}

	public PolicyComponent getExp_policyComponent() {
		return exp_policyComponent;
	}

	public void setExp_policyComponent(PolicyComponent exp_policyComponent) {
		this.exp_policyComponent = exp_policyComponent;
	}

	public NodeService getExp_nodeService() {
		return exp_nodeService;
	}

	public void setExp_nodeService(NodeService exp_nodeService) {
		this.exp_nodeService = exp_nodeService;
	}

	@Override
	public void afterCreateVersion(NodeRef versionableNode, Version version) {
		onContentUpdate(versionableNode, true);
	}
}
