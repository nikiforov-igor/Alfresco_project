package ru.it.lecm.statemachine.action.document;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.statemachine.LifecycleStateMachineHelper;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: PMelnikov Date: 05.10.12 Time: 11:30
 */
public class WaitForDocumentChangeListenerPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private PolicyComponent policyComponent;
	private NodeService nodeService;
	private ServiceRegistry serviceRegistry;
	private DocumentService documentService;
	private ThreadPoolExecutor threadPoolExecutor;
	private TransactionListener transactionListener;
	private TransactionService transactionService;
	private LifecycleStateMachineHelper stateMachineHelper;
	final static Logger logger = LoggerFactory.getLogger(WaitForDocumentChangeListenerPolicy.class);

	private static final String WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER = "wait_for_document_change_transaction_listener";

	public final void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "serviceRegistry", serviceRegistry);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				StatemachineModel.TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.FIRST_EVENT));

		transactionListener = new WaitForDocumentChangePolicyTransactionListener();
	}
	
	private Set<NodeRef> getPostTxnNodes(){
		@SuppressWarnings("unchecked")
		Set<NodeRef> pendingActions = (Set<NodeRef>)AlfrescoTransactionSupport.getResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER);
		if (pendingActions == null) {
			pendingActions = new LinkedHashSet<NodeRef>(11);
			AlfrescoTransactionSupport.bindResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER, pendingActions);
		}
		return pendingActions;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		AlfrescoTransactionSupport.bindListener(this.transactionListener);
		getPostTxnNodes().add(nodeRef);
		if (logger.isDebugEnabled()) {
			logger.debug("МАШИНА СОСТОЯНИЙ. Добавление документа " + nodeRef + " в список на обработку изменений");
		}
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setStateMachineHelper(LifecycleStateMachineHelper stateMachineHelper) {
		this.stateMachineHelper = stateMachineHelper;
	}

	private class WaitForDocumentChangePolicyTransactionListener implements TransactionListener {

		@Override
		public void flush() {
			logger.debug("МАШИНА СОСТОЯНИЙ. Вызывается метод flush");
//			List<NodeRef> docs = AlfrescoTransactionSupport.getResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER);
//			if(docs != null && !docs.isEmpty()) {
//				for(int i = 0; i < docs.size(); i++) {
//					NodeRef nodeRef = docs.get(i);
//					if (nodeRef != null && nodeService.exists(nodeRef)) {
//						Serializable state = nodeService.getProperty(nodeRef, QName.createQName("http://www.it.ru/logicECM/model/signing/aspects/1.0", "signingState"));
//						logger.debug("МАШИНА СОСТОЯНИЙ. state полученный в методе flush равен {}", state);
//					}
//				}
//			}
		}

		@Override
		public void beforeCommit(boolean readOnly) {
			logger.debug("МАШИНА СОСТОЯНИЙ. Вызывается метод beforeCommit");
//			List<NodeRef> docs = AlfrescoTransactionSupport.getResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER);
//			if(docs != null && !docs.isEmpty()) {
//				for(int i = 0; i < docs.size(); i++) {
//					NodeRef nodeRef = docs.get(i);
//					if (nodeRef != null && nodeService.exists(nodeRef)) {
//						Serializable state = nodeService.getProperty(nodeRef, QName.createQName("http://www.it.ru/logicECM/model/signing/aspects/1.0", "signingState"));
//						logger.debug("МАШИНА СОСТОЯНИЙ. state полученный в методе beforeCommit равен {}", state);
//					}
//				}
//			}
		}

		@Override
		public void beforeCompletion() {
//            if (!logger.isTraceEnabled()) {
//                return;
//            }
			logger.debug("МАШИНА СОСТОЯНИЙ. Вызывается метод beforeCompletion");
//			List<NodeRef> docs = AlfrescoTransactionSupport.getResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER);
//			if(docs != null && !docs.isEmpty()) {
//				for(int i = 0; i < docs.size(); i++) {
//					NodeRef nodeRef = docs.get(i);
//					if (nodeRef != null && nodeService.exists(nodeRef)) {
//						Serializable state = nodeService.getProperty(nodeRef, QName.createQName("http://www.it.ru/logicECM/model/signing/aspects/1.0", "signingState"));
//						logger.trace("МАШИНА СОСТОЯНИЙ. state полученный в методе beforeCompletion равен {}", state);
//					}
//				}
//			}
		}
		
		/**
		 * Безопасный (с точки зрения доступа к БД) метод для получения пользователя,
		 * изменнившего документ. Нужно для корректной обёртки в runAs
		 * @param documentNodeRef
		 * @return 
		 */
		private String getModifier(final NodeRef documentNodeRef) {
			return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<String>() {
				@Override
				public String execute() throws Throwable {
					String login = null;

					login = (String) nodeService.getProperty(documentNodeRef, ContentModel.PROP_MODIFIER);
					
					if (AuthenticationUtil.SYSTEM_USER_NAME.equals(login)) {
						logger.warn("Modifier is System. Using admin instead");
						login = AuthenticationUtil.getAdminUserName();
					}
					
					return login;
				}
			}, true);
		}

		@Override
		public void afterCommit() {
			if (logger.isDebugEnabled()) {
				logger.debug("МАШИНА СОСТОЯНИЙ. Вызывается метод afterCommit");
			}
				for(final NodeRef nodeRef : getPostTxnNodes()) {
//                    final NodeRef nodeRef = pendingDocs;
//					if (nodeService.exists(nodeRef)) {
//						Serializable state = nodeService.getProperty(nodeRef, QName.createQName("http://www.it.ru/logicECM/model/signing/aspects/1.0", "signingState"));
//						logger.debug("МАШИНА СОСТОЯНИЙ. state полученный в методе afterCommit равен {}", state);
						Runnable runnable = new Runnable() {
							public void run() {
								try {
//									String modifier = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIER);
//									if (logger.isDebugEnabled()) {
//										logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " пользователем " + modifier);
//									}
									final String modifier = getModifier(nodeRef);
									AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
										@Override
										public Void doWork() throws Exception {
											return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
												@Override
												public Void execute() throws Throwable {
													if (nodeService.exists(nodeRef)) {
														Serializable state = nodeService.getProperty(nodeRef, QName.createQName("http://www.it.ru/logicECM/model/signing/aspects/1.0", "signingState"));
														if (logger.isDebugEnabled()) {
															logger.debug("МАШИНА СОСТОЯНИЙ. state полученный в методе afterCommit равен {}", state);
														}
														if (logger.isDebugEnabled()) {
															logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " пользователем " + modifier);
														}
														//TODO transaction in loop!!!
														if (nodeService.hasAspect(nodeRef, StatemachineModel.ASPECT_WORKFLOW_DOCUMENT_TASK)) {
															final String taskId = (String) nodeService.getProperty(nodeRef, StatemachineModel.PROP_WORKFLOW_DOCUMENT_TASK_STATE_PROCESS);
															if (logger.isDebugEnabled()) {
																logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " таска: " + taskId);
															}
															List<StateMachineAction> actions = stateMachineHelper.getTaskActionsByName(taskId, StateMachineActionsImpl.getActionNameByClass(WaitForDocumentChangeAction.class));
															if (logger.isDebugEnabled()) {
																logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " actions: " + actions);
															}
															WaitForDocumentChangeAction.Expression result = null;
															for (StateMachineAction action : actions) {
																if (logger.isDebugEnabled()) {
																	logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " action: " + action);
																}
																WaitForDocumentChangeAction documentChangeAction = (WaitForDocumentChangeAction) action;
																List<WaitForDocumentChangeAction.Expression> expressions = documentChangeAction.getExpressions();
																if (logger.isDebugEnabled()) {
																	logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " expressions: " + expressions);
																}
																for (WaitForDocumentChangeAction.Expression expression : expressions) {
																	if (logger.isDebugEnabled()) {
																		logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " expression: " + expression);
																	}
																	if (documentService.execExpression(nodeRef, expression.getExpression())) {
																		if (logger.isDebugEnabled()) {
																			logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " execExpression: " + expression);
																		}
																		result = expression;
																		break;
																	}
																}
																if (result != null) {
																	break;
																}
															}

															if (result != null) {
																if (logger.isDebugEnabled()) {
																	logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " выражение: " + result.getExpression());
																}
																final String executionId = stateMachineHelper.getCurrentExecutionId(taskId);
																if (result.getScript() != null && !"".equals(result.getScript())) {
																	final String script = result.getScript();
																	if (logger.isDebugEnabled()) {
																		logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " скрипт: " + script);
																	}
		//   			                                           		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
		//                                                      		@Override
		//                                                      		public Object doWork() throws Exception {
																	stateMachineHelper.executeScript(script, executionId);
		//                                                      		return null;
		//                                                     			}
		//                                                  			});
																}
																if (result.getOutputValue() != null && !"".equals(result.getOutputValue())) {
																	//HashMap<String, Object> parameters = new HashMap<String, Object>();
																	//parameters.put(result.getOutputVariable(), result.getOutputValue());
																	final String messageName = result.getOutputVariable() + "_msg";
																	//TODO может сразу execution? Или переписать на message?
																	//stateMachineHelper.setExecutionParamentersByTaskId(taskId, parameters);
																	if (result.isStopSubWorkflows()) {
																		//TODO DONE nodeRef это и есть документ
																		stateMachineHelper.stopDocumentSubWorkflows(nodeRef, null);
																	}
																	//stateMachineHelper.nextTransition(taskId);
																	AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
																		@Override
																		public Void doWork() throws Exception {
																			if (logger.isDebugEnabled()) {
																				logger.debug("МАШИНА СОСТОЯНИЙ. Обработка изменений документа " + nodeRef + " executionId: " + executionId + " ,messageName: " + messageName);
																			}
																			stateMachineHelper.sendMessage(messageName, executionId);
																			return null;
																		}
																	});
																}
															}
															logger.debug("МАШИНА СОСТОЯНИЙ. Конец");
														}
													}
													return null;
												}
											}, false, true);
										}
									}, modifier);
								} catch (Exception e) {
									logger.error("Error while execution change document action", e);
								}
							}
						};
						threadPoolExecutor.execute(runnable);
//					}
				}
		}

		@Override
		public void afterRollback() {
			logger.debug("МАШИНА СОСТОЯНИЙ. Вызывается метод afterRollback");
////			NodeRef nodeRef = AlfrescoTransactionSupport.getResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER);
//			List<NodeRef> docs = AlfrescoTransactionSupport.getResource(WAIT_FOR_DOCUMENT_CHANGE_TRANSACTION_LISTENER);
//			if(docs != null && !docs.isEmpty()) {
//				for(int i = 0; i < docs.size(); i++) {
//					NodeRef nodeRef = docs.get(i);
//					if (nodeRef != null && nodeService.exists(nodeRef)) {
//						Serializable state = nodeService.getProperty(nodeRef, QName.createQName("http://www.it.ru/logicECM/model/signing/aspects/1.0", "signingState"));
//						logger.debug("МАШИНА СОСТОЯНИЙ. state полученный в методе afterRollback равен {}", state);
//					}
//				}
//			}
		}
	}

}
