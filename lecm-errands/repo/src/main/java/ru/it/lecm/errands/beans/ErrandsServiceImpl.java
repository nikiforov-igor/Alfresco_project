package ru.it.lecm.errands.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<NodeRef> getErrandsDocuments(List<String> paths, int skipCount, int maxItems){
        List<QName> types =  new ArrayList<QName>();
        types.add(TYPE_ERRANDS);

        List<SortDefinition> sort = new ArrayList<SortDefinition>();
        List<NodeRef> sortingErrands = new ArrayList<NodeRef>();
        List<NodeRef> result = new ArrayList<NodeRef>();
        List<String> status = new ArrayList<String>();

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
        // сортируем по важности поручения и по сроку исполнения
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD,"@" + TYPE_ERRANDS_IS_IMPORTANT.toString(),false));
        sort.add(new SortDefinition(SortDefinition.SortType.FIELD,"@" + TYPE_ERRANDS_LIMITATION_DATE.toString(),false));

       status.add("Ожидает исполнения");
       status.add("В работе");
       status.add("На доработке");

        for (NodeRef nodeRef : documentService.getDocumentsByFilter(types, null, null, null, paths, status, null, sort)) {
            if (currentEmployee.equals(findNodeByAssociationRef(nodeRef, ASSOC_ERRANDS_EXECUTOR, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET))){
                sortingErrands.add(nodeRef);
            }
        }
        int endIndex = (skipCount + maxItems) < sortingErrands.size() ? (skipCount + maxItems) : sortingErrands.size();

        for (int i = skipCount; i < endIndex; i++) {
            result.add(sortingErrands.get(i));
        }
        return result;
    }
}
