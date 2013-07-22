package ru.it.lecm.errands.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:43
 */
public class ErrandsServiceImpl extends BaseBean implements ErrandsService {
	private static enum ModeChoosingExecutors {
		ORGANIZATION,
		UNIT
	}

	private DocumentService documentService;
	private OrgstructureBean orgstructureService;
    private NamespaceService namespaceService;

	private final Object lock = new Object();

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ERRANDS_ROOT_ID);
	}

	public NodeRef getDraftRoot() {
		return  documentService.getDraftRootByType(TYPE_ERRANDS);
	}

	public NodeRef getSettingsNode() {
		final NodeRef rootFolder = this.getServiceRootFolder();

		NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
		if (settings != null) {
			return settings;
		} else {
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							NodeRef settingsRef;
							synchronized (lock) {
								settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, ERRANDS_SETTINGS_NODE_NAME);
								if (settingsRef == null) {
									QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
									QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ERRANDS_SETTINGS_NODE_NAME);
									QName nodeTypeQName = TYPE_ERRANDS_SETTINGS;

									Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
									properties.put(ContentModel.PROP_NAME, ERRANDS_SETTINGS_NODE_NAME);
									ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
									settingsRef = associationRef.getChildRef();
								}
							}
							return settingsRef;
						}
					});
				}
			};
			return AuthenticationUtil.runAsSystem(raw);
		}
	}

	public ModeChoosingExecutors getModeChoosingExecutors() {
		NodeRef settings = getSettingsNode();
		if (settings != null) {
		 	String modeChoosingExecutors = (String) nodeService.getProperty(settings, SETTINGS_PROP_MODE_CHOOSING_EXECUTORS);
			if (modeChoosingExecutors.equals(SETTINGS_PROP_MODE_CHOOSING_EXECUTORS_ORGANIZATION)) {
				return ModeChoosingExecutors.ORGANIZATION;
			}
		}
		return ModeChoosingExecutors.UNIT;
	}

	public NodeRef getCurrentUserSettingsNode() {
		final NodeRef rootFolder = this.getServiceRootFolder();
		final String settingsObjectName = authService.getCurrentUserName() + "_" + ERRANDS_SETTINGS_NODE_NAME;

		NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
		if (settings != null) {
			return settings;
		} else {
			AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							NodeRef settingsRef;
							synchronized (lock) {
								settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, settingsObjectName);
								if (settingsRef == null) {
									QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
									QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, settingsObjectName);
									QName nodeTypeQName = TYPE_ERRANDS_USER_SETTINGS;

									Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
									properties.put(ContentModel.PROP_NAME, settingsObjectName);
									ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
									settingsRef = associationRef.getChildRef();
								}
							}
							return settingsRef;
						}
					});
				}
			};
			return AuthenticationUtil.runAsSystem(raw);
		}
	}

	public boolean isDefaultWithoutInitiatorApproval() {
		NodeRef settings = getCurrentUserSettingsNode();
		if (settings != null) {
			return (Boolean) nodeService.getProperty(settings, USER_SETTINGS_PROP_WITHOUT_INITIATOR_APPROVAL);
		}
		return false;
	}

	public NodeRef getDefaultInitiator() {
		if (orgstructureService.isCurrentEmployeeHasBusinessRole(BUSINESS_ROLE_ERRANDS_CHOOSING_INITIATOR)) {
			NodeRef settings = getCurrentUserSettingsNode();
			if (settings != null) {
				List<AssociationRef> defaultInitiatorAssocs = nodeService.getTargetAssocs(settings, USER_SETTINGS_ASSOC_DEFAULT_INITIATOR);
				if (defaultInitiatorAssocs.size() > 0) {
					return defaultInitiatorAssocs.get(0).getTargetRef();
				}
			}
		}
		return null;
	}

	public NodeRef getDefaultSubject() {
		NodeRef settings = getCurrentUserSettingsNode();
		if (settings != null) {
			List<AssociationRef> defaultSubjectAssocs = nodeService.getTargetAssocs(settings, USER_SETTINGS_ASSOC_DEFAULT_SUBJECT);
			if (defaultSubjectAssocs.size() > 0) {
				return defaultSubjectAssocs.get(0).getTargetRef();
			}
		}
		return null;
	}

	public List<NodeRef> getAvailableInitiators() {
		List<NodeRef> initiatorRoleEmployees = orgstructureService.getEmployeesByBusinessRole(BUSINESS_ROLE_ERRANDS_INITIATOR_ID);
		if (getModeChoosingExecutors() == ModeChoosingExecutors.ORGANIZATION) {
			return initiatorRoleEmployees;
		} else {
			NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
			List<NodeRef> subordinates = orgstructureService.getBossSubordinate(currentEmployee, true);

			List<NodeRef> result = new ArrayList<NodeRef>();
			if (initiatorRoleEmployees.contains(currentEmployee)) {
				result.add(currentEmployee);
			}

			for (NodeRef subordinate: subordinates) {
				if (initiatorRoleEmployees.contains(subordinate)) {
					result.add(subordinate);
				}
			}
			return result;
		}
	}

    @Override
    public void requestDueDateChange() {
    }

    public List<NodeRef> getErrandsDocumentFilter(List<QName> types, List<String> paths, String filterKey){
        String filter = "";
        List<NodeRef> result = new ArrayList<NodeRef>();
        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        if (filterKey != null) {
            switch(FilterEnum.valueOf(filterKey.toUpperCase())) {
                case IMPORTANT : {
                    // получаем важные поручения
                    filter += " AND (" + "@lecm\\-errands\\:is\\-important:\"" + "true" + "\""+")";
                    filter += "";
                    for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, null, null, null, paths, null, null, null, filter)) {
                        if (currentEmployee.equals(findNodeByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))){
                            result.add(nodeRef);
                        }
                    }
                    break;
                }
                case OVERDUE: {
                      // просроченные
                    Date now = new Date();
                    filter += " AND NOT (" + "@lecm\\-errands\\:is\\-important:\"" + "true" + "\""+")";
                    filter += "";
                    QName dateType = QName.createQName("lecm-errands:limitation-date", namespaceService);
                    for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, dateType , null, now, paths, null, null, null, filter)) {
                        if (currentEmployee.equals(findNodeByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))){
                            result.add(nodeRef);
                        }
                    }
                    break;
                }
                case APPROACHING_DEADLINE: {
                    // с приближающимся сроком
                    Date now = new Date();
                    Date end = null;
                    String daysCount = "2";
                    if (daysCount != null &&  !"".equals(daysCount)) {
                        Integer days = Integer.parseInt(daysCount);

                        if (days > 0) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(now);
                            calendar.add(Calendar.DAY_OF_MONTH, days);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            end = calendar.getTime();
                        }
                    }
                    filter += " AND NOT (" + "@lecm\\-errands\\:is\\-important:\"" + "true" + "\""+")";
                    filter += "";
                    QName dateType = QName.createQName("lecm-errands:limitation-date", namespaceService);
                    for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, dateType, now, end, paths, null, null, null, filter)) {
                        if (currentEmployee.equals(findNodeByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))){
                            result.add(nodeRef);
                        }
                    }
                    break;
                }
                case OTHER: {
                    // Остальные
                    Date now = new Date();
                    Date start = null;
                    String daysCount = "2";
                    if (daysCount != null &&  !"".equals(daysCount)) {
                        Integer days = Integer.parseInt(daysCount);

                        if (days > 0) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(now);
                            calendar.add(Calendar.DAY_OF_MONTH, days);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            start = calendar.getTime();
                        }
                    }
                    filter += " AND NOT (" + "@lecm\\-errands\\:is\\-important:\"" + "true" + "\""+")";
                    filter += "";
                    QName dateType = QName.createQName("lecm-errands:limitation-date", namespaceService);
                    for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, dateType, start, null, paths, null, null, null, filter)) {
                        if (currentEmployee.equals(findNodeByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))){
                            result.add(nodeRef);
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
//        documentService.getDocumentsByFilter();
        return result;
    }
}
