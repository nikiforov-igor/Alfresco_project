package ru.it.lecm.orgstructure.policies;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.FileNameValidator;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.schedule.ISchedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dbashmakov
 *         Date: 28.01.13
 *         Time: 10:37
 */
public class OrgstructureUnitPolicy
		extends SecurityJournalizedPolicyBase
		implements NodeServicePolicies.OnCreateNodePolicy
					, NodeServicePolicies.OnDeleteNodePolicy
{

	final static String CHKNAME_AUTH_SERVICE = "authService";

    private ISchedule scheduleService;
    private Repository repositoryHelper;
    private PermissionService permissionService;
    private LecmBasePropertiesService propertiesService;

    @Override
	public void init() {
		PropertyCheck.mandatory(this, CHKNAME_AUTH_SERVICE, authService);

		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateUnitLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateUnitLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateUnit"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onDeleteNode"));
	}

//	public AuthenticationService getAuthService() {
//		return authService;
//	}

	public void setScheduleService(ISchedule scheduleService) {
		this.scheduleService = scheduleService;
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (enabled) {
                final NodeRef unit = childAssocRef.getChildRef();
                NodeRef parent = orgstructureService.getParentUnit(unit);
                if (parent == null) {
                    NodeRef root = orgstructureService.getRootUnit();
                    if (root != null && !root.equals(unit)) {
                        throw new  AlfrescoRuntimeException("Нельзя создать два корневых подразделения!");
                    }
                }
                // оповещение securityService по Департаменту ...
                notifyChangedOU(unit, parent);
                AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
                    @Override
                    public Object doWork() throws Exception {
                        createOrganizationUnitStore(unit);
                        return null;
                    }
                });
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read orgstructure properties");
        }
    }

	public void onUpdateUnitLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

		if (before.size() == after.size() && !changed) {
			businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внес(ла) изменения в сведения о подразделении #mainobject");
		}

		if (changed && !curActive) { // бьыли изменения во флаге и подразделение помечено как неактивное
			businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) сведения о подразделении #mainobject");
		}

		// отслеживаем котороткое название для SG-обозначений
		{
			final Object oldValue = before.get(PolicyUtils.PROP_ORGELEMENT_NAME);
			final Object newValue = after.get(PolicyUtils.PROP_ORGELEMENT_NAME);
			final boolean flagChanged = ! PolicyUtils.safeEquals( newValue, oldValue);
			if (flagChanged) {
				logger.debug( String.format( "updating details for OU '%s'\n\t from '%s'\n\t to '%s'", nodeRef, oldValue, newValue));
				notifyNodeCreated( PolicyUtils.makeOrgUnitPos(nodeRef, nodeService));
				/*
				notifyNodeCreated( PolicyUtils.makeOrgUnitSVPos(nodeRef, nodeService));
				(!) для OUSV явный вызов не потребуется, т.к. notifyOU автоматом создаёт sv-часть
				*/
			}
		}
	}

	public void onUpdateUnit(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
             try {
                 Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.editor.enabled");
                 boolean enabled;
                 if (editorEnabled == null) {
                     enabled = true;
                 } else {
                     enabled = Boolean.valueOf((String) editorEnabled);
                 }

                 if (enabled) {
                     final Boolean nowActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
                     final Boolean oldActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
                     final boolean changed = !PolicyUtils.safeEquals(oldActive, nowActive);

                     //если подразделение удаляется
                     if (changed && !nowActive) {
                         NodeRef schedule = scheduleService.getScheduleByOrgSubject(nodeRef, true);
                         if (schedule != null && nodeService.exists(schedule)) {
                             nodeService.addAspect(schedule, ContentModel.ASPECT_TEMPORARY, null);
                             nodeService.deleteNode(schedule);
                         }
                         //Обновляем папку подразделения
                         List<AssociationRef> shared = nodeService.getTargetAssocs(nodeRef, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
                         if (shared.size() > 0) {
                             final NodeRef folder = shared.get(0).getTargetRef();
                             final String name = nodeService.getProperty(folder, ContentModel.PROP_NAME).toString();
//                           TODO: DONE Вероятней всего, что полиси на изменение вызовется только в read-write транзакции, поэтому транзакцию убираю
                             nodeService.setProperty(folder, ContentModel.PROP_NAME, name + " (Удалено)");
                         }
                     } else {
                         //TODO DONE замена нескольких setProperty на setProperties.
                         //Обновляем папку подразделения
                         List<AssociationRef> shared = nodeService.getTargetAssocs(nodeRef, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
                         Map<QName, Serializable> properties;
                         if (shared.isEmpty()) {
                             if (before.size() > 0) {
                                 createOrganizationUnitStore(nodeRef);
                                 shared = nodeService.getTargetAssocs(nodeRef, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
                             }
                         }
                         if (shared.size() > 0) {
                             NodeRef folder = shared.get(0).getTargetRef();
                             properties = nodeService.getProperties(folder);
                             properties.put(ContentModel.PROP_NAME, FileNameValidator.getValidFileName(after.get(OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME).toString()));
                             properties.put(ContentModel.PROP_TITLE, after.get(OrgstructureBean.PROP_UNIT_CODE));
                             properties.put(ContentModel.PROP_DESCRIPTION, after.get(OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME));
                             nodeService.setProperties(folder, properties);
                         }
                     }
                 }
             } catch (LecmBaseException e) {
                 throw new IllegalStateException("Cannot read orgstructure properties");
             }
         }

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (enabled) {
                final NodeRef nodeOU = childAssocRef.getChildRef();
                final NodeRef parentOU = childAssocRef.getParentRef();
                notifyDeleteOU( nodeOU, parentOU);
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read orgstructure properties");
        }
	}

	public void onCreateUnitLog(ChildAssociationRef childAssocRef) {
		NodeRef unit = childAssocRef.getChildRef();
		NodeRef parent = orgstructureService.getParentUnit(unit);

		final List<String> objects = new ArrayList<String>(1);
		if (parent != null) {
			objects.add(parent.toString());
		} else { // корневое подразделение - берем Организацию
			objects.add(orgstructureService.getOrganization().toString());
		}

		final String initiator = authService.getCurrentUserName();
		businessJournalService.log(initiator, unit, EventCategory.ADD, "#initiator создал(а) новое подразделение #mainobject в подразделении #object1", objects);
	}

    private void createOrganizationUnitStore(NodeRef unit) {
        NodeRef parent = orgstructureService.getParentUnit(unit);
        NodeRef root = orgstructureService.getRootUnit();
        List<AssociationRef> sharedFolder = null;
        if (parent != null) {
            sharedFolder = nodeService.getTargetAssocs(parent, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);

            if (sharedFolder.isEmpty()) {
                createOrganizationUnitStore(parent);
                sharedFolder = nodeService.getTargetAssocs(parent, OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
            }
        }

        //Получаем хранилище
        NodeRef parentStore = null;
        if (sharedFolder != null && sharedFolder.size() > 0 && !root.equals(unit)) {
            parentStore = sharedFolder.get(0).getTargetRef();
        } else if (root.equals(unit)) {
            NodeRef companyHome = repositoryHelper.getCompanyHome();
            parentStore = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, OrgstructureBean.DOCUMENT_ROOT_NAME);
        }

        //Создаем основную папку подразделения
        String name = FileNameValidator.getValidFileName(nodeService.getProperty(unit, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME).toString());
        String title = nodeService.getProperty(unit, OrgstructureBean.PROP_UNIT_CODE).toString();
        String description = nodeService.getProperty(unit, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME).toString();

        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_NAME, name);
        props.put(ContentModel.PROP_TITLE, title);
        props.put(ContentModel.PROP_DESCRIPTION, description);
        props.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);

        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
        if (nodeService.getChildByName(parentStore, ContentModel.ASSOC_CONTAINS, name) != null) return;
        final ChildAssociationRef ref = nodeService.createNode(parentStore, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, props);

        //Создаем папку с общими документами
        props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_NAME, OrgstructureBean.ORGANIZATION_UNIT_SHARED_FOLDER_NAME);
        props.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);
        assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, OrgstructureBean.ORGANIZATION_UNIT_SHARED_FOLDER_NAME);
        final ChildAssociationRef shared = nodeService.createNode(ref.getChildRef(), ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, props);
        String sharedAuthority = PolicyUtils.makeOrgUnitPos(unit, nodeService).getAlfrescoSuffix();
        permissionService.setInheritParentPermissions(shared.getChildRef(), false);
        permissionService.setPermission(shared.getChildRef(), "GROUP_" + sharedAuthority, "LECM_BASIC_PG_Reader", true);

        //Добавляем правило к папке с общими документами
        final Rule rule = new Rule();
        rule.setTitle("Logic ECM Пассивные рассылки");
        rule.setDescription("Logic ECM Рассылка уведомлений");
        rule.applyToChildren(true);
        rule.setExecuteAsynchronously(false);
        rule.setRuleDisabled(false);
        rule.setRuleType(RuleType.INBOUND);
        Action ruleAction = serviceRegistry.getActionService().createAction("sharedFolderNotificationAction");
        rule.setAction(ruleAction);


        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                serviceRegistry.getRuleService().saveRule(shared.getChildRef(), rule);
                return null;
            }
        });

        //Создаем папку с документами подразделения
        props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_NAME, OrgstructureBean.ORGANIZATION_UNIT_PRIVATE_FOLDER_NAME);
        props.put(ContentModel.PROP_OWNER, AuthenticationUtil.SYSTEM_USER_NAME);
        assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, OrgstructureBean.ORGANIZATION_UNIT_PRIVATE_FOLDER_NAME);
        final ChildAssociationRef privateFolder = nodeService.createNode(ref.getChildRef(), ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_FOLDER, props);
        String privateAuthority = PolicyUtils.makeOrgUnitPrivatePos(unit, nodeService).getAlfrescoSuffix();
        permissionService.setInheritParentPermissions(privateFolder.getChildRef(), false);
        permissionService.setPermission(privateFolder.getChildRef(), "GROUP_" + privateAuthority, "LECM_BASIC_PG_Reader", true);

        //Создаем ассоциацию подразделения с папкой
        nodeService.createAssociation(unit, ref.getChildRef(), OrgstructureBean.ASSOC_ORGANIZATION_UNIT_FOLDER);
    }

    public void setRepositoryHelper(Repository repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }
}
