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
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
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
public class OrgstructureUnitPolicy extends SecurityJournalizedPolicyBase implements NodeServicePolicies.OnCreateNodePolicy {

    public static final String RU_IT_LECM_PROPERTIES_ORGSTRUCTURE_EDITOR_ENABLED = "ru.it.lecm.properties.orgstructure.editor.enabled";
    private ISchedule scheduleService;
    private Repository repositoryHelper;
    private PermissionService permissionService;
    private LecmBasePropertiesService propertiesService;
    private DictionaryBean dictionaryBean;

    public void setDictionaryBean(DictionaryBean dictionaryBean) {
        this.dictionaryBean = dictionaryBean;
    }

    public void setScheduleService(ISchedule scheduleService) {
        this.scheduleService = scheduleService;
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

    @Override
    public void init() {
        super.init();

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateUnitLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateUnitLog", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateUnit"));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        try {
            Object editorEnabled = propertiesService.getProperty(RU_IT_LECM_PROPERTIES_ORGSTRUCTURE_EDITOR_ENABLED);
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (enabled) {
                //проверка на уникальность подразделения первого уровня
                final NodeRef unit = childAssocRef.getChildRef();

                NodeRef parent;
                if (orgstructureService.getRootUnit().equals(childAssocRef.getParentRef())) {
                    parent = orgstructureService.getParentUnit(unit, false);
                } else {
                    parent = orgstructureService.getParentUnit(unit);
                }

                if (parent == null) {
                    NodeRef root = orgstructureService.getRootUnit();
                    if (root != null && !root.equals(unit)) {
                        throw new AlfrescoRuntimeException("Нельзя создать два корневых подразделения!");
                    }
                } else {
                    // создаем контрагента - если подразделение 2-го уровня
                    if (parent.equals(orgstructureService.getRootUnit())) {
						//NOP
                    } else { // для остальных - прописываем ссылку, если есть аспект и организация
                        NodeRef organization = orgstructureService.getOrganization(parent);
                        if (organization != null) {
                            nodeService.addAspect(unit, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION, null);
                            nodeService.createAssociation(unit, organization, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION);
                        }
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

    @SuppressWarnings("unused")
    public void onUpdateUnitLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
        final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
        final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

        if (before.size() == after.size() && !changed) {
            String msg = String.format("#initiator внес(ла) изменения в сведения %s #mainobject",
                    isOrganization(nodeRef) ? "об организации" : "о подразделении");
            businessJournalService.log(nodeRef, EventCategory.EDIT, msg);
        }

        if (changed && !curActive) { // были изменения во флаге и подразделение помечено как неактивное
            String msg = String.format("#initiator удалил(а) сведения %s #mainobject",
                    isOrganization(nodeRef) ? "об организации" : "о подразделении");
            businessJournalService.log(nodeRef, EventCategory.DELETE, msg);
        }
    }

    @SuppressWarnings("unused")
    public void onUpdateUnit(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        try {
            Object editorEnabled = propertiesService.getProperty(RU_IT_LECM_PROPERTIES_ORGSTRUCTURE_EDITOR_ENABLED);
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

                    NodeRef parent = nodeService.getPrimaryParent(nodeRef).getParentRef();
                    //Удаляем аспект у контрагента при удалении подразделения
                    /* ALF-5321
                    if (nodeService.hasAspect(nodeRef, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)
                            && parent != null
                            && !nodeService.hasAspect(parent, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION)) {
                        NodeRef contractor = orgstructureService.getOrganization(nodeRef);
                        if (contractor != null && nodeService.hasAspect(contractor, OrgstructureAspectsModel.ASPECT_IS_ORGANIZATION)) {
                            nodeService.removeAspect(contractor, OrgstructureAspectsModel.ASPECT_IS_ORGANIZATION);
                        }
                    }
                    */
                    // оповещение securityService по Департаменту ...
                    if (parent != null) {
                        notifyDeleteOU(nodeRef, parent);
                    }
                } else {
                    // отслеживаем короткое название для SG-обозначений
                    final Object oldValue = before.get(PolicyUtils.PROP_ORGELEMENT_NAME);
                    final Object newValue = after.get(PolicyUtils.PROP_ORGELEMENT_NAME);
                    final boolean flagChanged = !PolicyUtils.safeEquals(newValue, oldValue);
                    if (flagChanged) {
                        logger.debug(String.format("updating details for OU '%s'\n\t from '%s'\n\t to '%s'", nodeRef, oldValue, newValue));
                        notifyNodeCreated(PolicyUtils.makeOrgUnitPos(nodeRef, nodeService));
                    }

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

    @SuppressWarnings("unused")
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
        String msg = String.format("#initiator создал(а) %s #mainobject в подразделении #object1",
                isOrganization(unit) ? "новую организацию" : "новое подразделение");
        businessJournalService.log(initiator, unit, EventCategory.ADD, msg, objects);
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
        } else if (root != null && root.equals(unit)) {
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
        if (parentStore == null || nodeService.getChildByName(parentStore, ContentModel.ASSOC_CONTAINS, name) != null) return;
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

    private boolean isOrganization(NodeRef unit) {
        NodeRef parent = orgstructureService.getParentUnit(unit);

        return nodeService.hasAspect(unit, OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION) &&
                parent != null && parent.equals(orgstructureService.getHolding());
    }
}
