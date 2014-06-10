package ru.it.lecm.experts.polices;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.experts.beans.GetExpertsBean;

/**
 * @author dbashmakov
 *         Date: 10.09.12
 *         Time: 11:16
 */
public class ExpertsComponent implements ContentServicePolicies.OnContentUpdatePolicy, NodeServicePolicies.OnCreateNodePolicy, VersionServicePolicies.AfterCreateVersionPolicy {

	private static final transient Logger logger = LoggerFactory.getLogger(ExpertsComponent.class);
	private final static QName EXPERTS_ASPECT = QName.createQName("http://www.it.ru/logicECM/experts/1.0", "hasExperts");
	private final static QName EXPERTS_ASPECT_PROPERTY = QName.createQName("http://www.it.ru/logicECM/experts/1.0", "experts");

	private final Set<String> AFFECTED_EXTENSIONS
			= new HashSet<String>() {{
		add("DOC");
		add("DOCX");
		add("PPTX");
		add("XLSX");
		add("TXT");
		add("PDF");
	}};

	private final long DELAY = 1000;
	private final int ATTEMPTS = 30;

	private PolicyComponent exp_policyComponent;
	private NodeService exp_nodeService;
	private PersonService exp_personService;
	private TransactionService exp_transService;
	private ContentService exp_contentService;
	private AuthenticationService exp_authService;
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
			final NodeRef ref = nodeRef;
                        //onUpdate policy. должна быть транзакция
//			RetryingTransactionHelper.RetryingTransactionCallback<Object> processEventCallback =
//					new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
//						public Object execute() throws Throwable {
							searchExperts(ref, null);
//							return null;
//						}
//					};
//			exp_transService.getRetryingTransactionHelper().doInTransaction(processEventCallback, false);
		}
	}

	private void searchExperts(final NodeRef nodeRef, final NodeRef versionRef) {
		final String fileName = (String) exp_nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		if (checkFilename(fileName)) {
			//final String username = exp_authService.getCurrentUserName();
			for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
				String experts = getExperts(versionRef != null ? versionRef : nodeRef, fileName);
				if (experts != null) {
					createExpertsAssociations(experts, nodeRef);
					break;
				} else {
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			/*Thread getExpertsThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						*//*for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
							String experts = getExperts(versionRef != null ? versionRef : nodeRef, fileName);
							if (experts != null) {
								createExpertsAssociations(experts, nodeRef);
								break;
							} else {
								Thread.sleep(DELAY);
							}
						}*//*

						for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
							String experts = getExperts(versionRef != null ? versionRef : nodeRef, fileName);
							if (experts != null) {
								createExpertsAssociations(experts, nodeRef);
								break;
							} else {
								Thread.sleep(DELAY);
							}
						}

					} catch (Exception ex) {
						logger.error(ex);
					}
				}
			});
			getExpertsThread.start();*/
		}
	}

	private String getExperts(final NodeRef nodeRef, String fileName) {
		GetExpertsBean expertsBean = new GetExpertsBean();
		expertsBean.setByUri(false); // byContent search

		String result = null;
		InputStream originalInputStream = null;
		ByteArrayOutputStream outputStream = null;
		if (exp_nodeService.exists(nodeRef)) {
			try {
				result = expertsBean.get(nodeRef.toString());
				/*ContentReader reader = exp_contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
				originalInputStream = reader.getContentInputStream();
				outputStream = new ByteArrayOutputStream();

				final int BUF_SIZE = 1 << 8; //1KiB buffer
				byte[] buffer = new byte[BUF_SIZE];
				int bytesRead = -1;


				while ((bytesRead = originalInputStream.read(buffer)) > -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				byte[] binaryData = outputStream.toByteArray();
				if (binaryData.length > 0) {
					result = expertsBean.get(binaryData, fileName);
				}*/
			} /*catch (IOException e) {
				logger.error(e);
			}*/ catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			} finally {
				IOUtils.closeQuietly(originalInputStream);
				IOUtils.closeQuietly(outputStream);
			}
		}
		return result;
	}

	private void createExpertsAssociations(String expertsString, NodeRef ref) {
		try {
			JSONArray expertsArray = new JSONArray(expertsString);
			for (int i = 0; i < expertsArray.length(); i++) {
				try {
					JSONObject expert = (JSONObject) expertsArray.get(i);
					String login = (String) expert.get(GetExpertsBean.ATTR_LNAME);

					// find system user - will be thrown exception if not exist
					NodeRef personRef = exp_personService.getPerson(login, false);
					exp_nodeService.createAssociation(ref, personRef, EXPERTS_ASPECT_PROPERTY);
				} catch (NoSuchPersonException ex) {
					logger.error(ex.getMessage(), ex);
				} catch (AssociationExistsException ex) {
					logger.error(ex.getMessage(), ex);
				} catch (JSONException ex) {
					logger.error(ex.getMessage(), ex);
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
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

	public void setExp_transService(TransactionService exp_transService) {
		this.exp_transService = exp_transService;
	}


	public void setExp_personService(PersonService exp_personService) {
		this.exp_personService = exp_personService;
	}


	public void setExp_policyComponent(PolicyComponent exp_policyComponent) {
		this.exp_policyComponent = exp_policyComponent;
	}


	public void setExp_nodeService(NodeService exp_nodeService) {
		this.exp_nodeService = exp_nodeService;
	}

	@Override
	public void afterCreateVersion(NodeRef versionableNode, Version version) {
		if (exp_nodeService.hasAspect(versionableNode, EXPERTS_ASPECT)) {
			searchExperts(versionableNode, version.getFrozenStateNodeRef());
		}
	}

	public void setExp_contentService(ContentService exp_contentService) {
		this.exp_contentService = exp_contentService;
	}

	public void setExp_authService(AuthenticationService exp_authService) {
		this.exp_authService = exp_authService;
	}
}
