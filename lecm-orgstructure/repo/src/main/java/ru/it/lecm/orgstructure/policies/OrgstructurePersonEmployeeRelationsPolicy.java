package ru.it.lecm.orgstructure.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.schedule.ISchedule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEvent;

/**
 * Полиси, которые регулируют отношения между lecm-orgstr:employee (сотрудником)
 * и cm:person (пользователем). При из связывании атрибутивный состав копируется
 * из person в employee. При создании person для него создается employee.
 *
 * @author vlevin
 */
public class OrgstructurePersonEmployeeRelationsPolicy extends SecurityJournalizedPolicyBase {

    private ISchedule scheduleService;
    private BehaviourFilter behaviourFilter;
    private LecmBasePropertiesService propertiesService;

    // атрибуты cm:person, при изменении которых мы будем проводить синхронизацию cm:person -> lecm-orgstr:employee
    private final static QName[] AFFECTED_PERSON_PROPERTIES = {ContentModel.PROP_FIRSTNAME, ContentModel.PROP_LASTNAME,
            ContentModel.PROP_EMAIL, ContentModel.PROP_TELEPHONE};

    public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
        this.behaviourFilter = behaviourFilter;
    }

    public void setScheduleService(ISchedule scheduleService) {
        this.scheduleService = scheduleService;
    }

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public final void init() {
        super.init();

//        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeNode"));
//        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeNodeLog",
//                Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "beforeDeleteEmployeeNode"));
//        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateEmployeeProperties"));
//        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateEmployeePropertiesLog",
//                Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//
//        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
//                new JavaBehaviour(this, "onCreateEmployeePersonAssociation"));
//        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
//                new JavaBehaviour(this, "onDeleteEmployeePersonAssociation"));
//
//        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO,
//                new JavaBehaviour(this, "onCreateEmployeePhotoAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
//        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
//                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO,
//                new JavaBehaviour(this, "onDeleteEmployeePhotoAssociation"));
//
//        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
//                ContentModel.TYPE_PERSON, ContentModel.ASSOC_AVATAR,
//                new JavaBehaviour(this, "onCreatePersonAvatarAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

//		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
//				ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onDeletePersonNode"));
//        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
//                ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onUpdatePersonProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }

	@Override
	protected void onBootstrap(ApplicationEvent event) {
		
		// TODO: Не уверен, что стоит переносить сюда, но так оно хотя бы стартует
		// Скорее всего, правильнее будет установить таки порядок бутстрапа
		
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeNodeLog",
                Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "beforeDeleteEmployeeNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateEmployeeProperties"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateEmployeePropertiesLog",
                Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
                new JavaBehaviour(this, "onCreateEmployeePersonAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PERSON,
                new JavaBehaviour(this, "onDeleteEmployeePersonAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO,
                new JavaBehaviour(this, "onCreateEmployeePhotoAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO,
                new JavaBehaviour(this, "onDeleteEmployeePhotoAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                ContentModel.TYPE_PERSON, ContentModel.ASSOC_AVATAR,
                new JavaBehaviour(this, "onCreatePersonAvatarAssociation", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

//		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
//				ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onDeletePersonNode"));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onUpdatePersonProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

    /**
     * Вызывается при создании нового сотрудника. Создать персональные данные и
     * связать их с сотрудником.
     */
    public void onCreateEmployeeNode(ChildAssociationRef childAssocRef) {
        final NodeRef employeeNode = childAssocRef.getChildRef();
        // Получаем папку где сохраняются персональныен данные
        final NodeRef personalDirectoryRef = orgstructureService.getPersonalDataDirectory();
        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        // Создаем пустые персональные данные
        ChildAssociationRef personalDataRef = nodeService.createNode(personalDirectoryRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                OrgstructureBean.TYPE_PERSONAL_DATA, properties);
        // Создаем ассоциацию сотруднику на персональные данные
        nodeService.createAssociation(employeeNode, personalDataRef.getChildRef(), OrgstructureBean.ASSOC_EMPLOYEE_PERSON_DATA);

        // сообщить 1) создание Сотрудника 2) связывание Сотрудника с Person/User.
        notifyEmploeeTie(employeeNode);

    }

    /**
     * Сделать запись в бизнес-журнал о создании сотрудника.
     */
    public void onCreateEmployeeNodeLog(ChildAssociationRef childAssocRef) {
        final NodeRef node = childAssocRef.getChildRef();
        businessJournalService.log(node, EventCategory.ADD, "#initiator добавил(а) нового сотрудника - #mainobject");
    }

    /**
     * Удаление ноды сотрудника. Оповестить SG
     */
    public void beforeDeleteEmployeeNode(NodeRef nodeRef) {
        final NodeRef person = orgstructureService.getPersonForEmployee(nodeRef);
        //notifyEmploeeDown(nodeRef, person);
    }

    /**
     * Атрибуты сотрудника переключены. Сотрудник помечен как неактивный
     */
    public void onUpdateEmployeeProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        try {
            boolean enabled;
            if (!AuthenticationUtil.isRunAsUserTheSystemUser()) {
                Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.employee.editor.enabled");
                if (editorEnabled == null) {
                    enabled = true;
                } else {
                    enabled = Boolean.valueOf((String) editorEnabled);
                }
            } else {
                enabled = true;
            }

            if (enabled) {
                final Boolean nowActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
                final Boolean oldActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
                final boolean changed = !PolicyUtils.safeEquals(oldActive, nowActive);
                if (changed) { // произошло переключение активности -> отработать ...
                    notifyEmploeeTie(nodeRef, nowActive);
					if (!nowActive) { //если сотрудник стал неактивен, то лишишь секретарей прав
						notifyChiefDown(nodeRef);
						notifySecretaryDown(nodeRef);
					}
                }

                //если сотрудник удаляется
                if (changed && !nowActive) {
                    List<NodeRef> employeeLinks = orgstructureService.getEmployeeLinks(nodeRef, true);
                    for (NodeRef employeeLink : employeeLinks) {
                        nodeService.addAspect(employeeLink, ContentModel.ASPECT_TEMPORARY, null);
                        nodeService.deleteNode(employeeLink);
                    }
                    NodeRef schedule = scheduleService.getScheduleByOrgSubject(nodeRef, true);
                    if (schedule != null) {
                        nodeService.addAspect(schedule, ContentModel.ASPECT_TEMPORARY, null);
                        nodeService.deleteNode(schedule);
                    }

                    //Отвязвываем пользователя Alfresco от сотрудника
                    List<AssociationRef> personAssocs = nodeService.getTargetAssocs(nodeRef, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
                    if (personAssocs.size() == 1) {
                        NodeRef person = personAssocs.get(0).getTargetRef();
                        nodeService.removeAssociation(nodeRef, person, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
                    }

                }

/*
                Код для импорта сотрудников
                List<AssociationRef> personAssocs = nodeService.getTargetAssocs(nodeRef, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
                if (personAssocs.size() == 0) {
                    Object personLogin = after.get(OrgstructureBean.PROP_EMPLOYEE_PERSON_LOGIN);
                    if (personLogin != null && !"".equals(personLogin)) {
                        String login = (String) personLogin;
                        NodeRef person = null;
                        try {
                            person = serviceRegistry.getPersonService().getPerson(login, false);
                        } catch (Exception e) {
                            logger.info("User with login " + login + " doesn't exists");
                        }
                        behaviourFilter.disableBehaviour(nodeRef);
                        try {
                            if (person == null) {
                                //Создаем пользователя с отключенными политиками
                                HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
                                properties.put(ContentModel.PROP_EMAIL, after.get(OrgstructureBean.PROP_EMPLOYEE_EMAIL));
                                properties.put(ContentModel.PROP_USERNAME, login);
                                properties.put(ContentModel.PROP_LASTNAME, after.get(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME));
                                properties.put(ContentModel.PROP_FIRSTNAME, after.get(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME));
                                person = serviceRegistry.getPersonService().createPerson(properties);
                            }
                        } catch (InvalidNodeRefException e) {
                            logger.error("Error while person object for employee created", e);
                        } catch (AssociationExistsException e) {
                            logger.error("Error while person object for employee created", e);
                        } finally {
                            behaviourFilter.enableBehaviour(nodeRef);
                        }
                        nodeService.createAssociation(nodeRef, person, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
                        //notifyEmploeeTie(nodeRef);
                    }
                }
*/
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read orgstructure properties");
        }
    }

    /**
     * Запись в бизнес-журнал об изменении в сотруднике.
     */
    public void onUpdateEmployeePropertiesLog(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
        final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
        final boolean changed = !PolicyUtils.safeEquals(prevActive, curActive);

        if (before.size() == after.size() && !changed) {
            businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внес(ла) изменения в сведения о сотруднике #mainobject");
        }

        if (changed && curActive != null && !curActive) {
            businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) сведения о сотруднике #mainobject");
        }
    }

    /**
     * Создана ассоциация между сотрудником и пользователем.
     */
    public void onCreateEmployeePersonAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        final NodeRef person = nodeAssocRef.getTargetRef();
        // оповещение SG о том, что создана ассоциация
        notifyEmploeeTie(employee);

        //Изменяем логин пользователя в настройках сотрудника
        nodeService.setProperty(employee, OrgstructureBean.PROP_EMPLOYEE_PERSON_LOGIN, nodeService.getProperty(person, ContentModel.PROP_USERNAME));

        // получить ссылку на фотографию сотрудника
        final NodeRef employeePhoto = orgstructureService.getEmployeePhoto(employee);
        if (employeePhoto != null) {
            // грохнуть фотографию
            nodeService.removeAssociation(employee, employeePhoto, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
        }
        // получить ссылку на автар пользовтеля
        NodeRef personAvatar = findNodeByAssociationRef(person, ContentModel.ASSOC_AVATAR, ContentModel.TYPE_CONTENT, ASSOCIATION_TYPE.TARGET);
        if (personAvatar != null) {
            // назначить его фотографией сотрудника
            nodeService.createAssociation(employee, personAvatar, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
        }

        synchronizePersonAndEmployee(person, employee);
    }

    /**
     * Разорвана связь между сотрудником и пользователем
     */
    public void onDeleteEmployeePersonAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        final NodeRef person = nodeAssocRef.getTargetRef();
		//если сотрудник стал неактивен, то лишишь секретарей прав
		notifyChiefDown(employee);
		notifySecretaryDown(employee);

        //Убираем логин пользователя
        nodeService.setProperty(employee, OrgstructureBean.PROP_EMPLOYEE_PERSON_LOGIN, "");
        // оповестить SG о том, что сотрудника оторвали от пользователя
        notifyEmploeeDown(employee, person);
    }

    /**
     * Сотруднику задана новая фотография. Привязать эту фотографию к
     * пользователю в качестве аватара.
     */
    public void onCreateEmployeePhotoAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        final NodeRef person = orgstructureService.getPersonForEmployee(employee);
        // есть пользователь не привязан, то все тщетно
        if (person != null) {
            // получить ссылку на фотографию
            final NodeRef employeePhoto = nodeAssocRef.getTargetRef();
            // получить ссылку на аватар
            final NodeRef personAvatar = findNodeByAssociationRef(person, ContentModel.ASSOC_AVATAR, ContentModel.TYPE_CONTENT, ASSOCIATION_TYPE.TARGET);
            // аватар существует и не равен фото пользователя

            behaviourFilter.disableBehaviour(person);
            try {
                if (personAvatar != null && !personAvatar.equals(employeePhoto)) {
                    // грохнуть аватар
                    nodeService.removeAssociation(person, personAvatar, ContentModel.ASSOC_AVATAR);
                    // установить пользователю фотографию сотрудника в качестве аватара
                    nodeService.createAssociation(person, employeePhoto, ContentModel.ASSOC_AVATAR);
                } else if (personAvatar == null) {
                    // установить пользователю фотографию сотрудника в качестве аватара
                    nodeService.createAssociation(person, employeePhoto, ContentModel.ASSOC_AVATAR);
                }
            } finally {
                behaviourFilter.enableBehaviour(person);
            }
        }
    }

    /**
     * Фотография пользователя удалена. Удалить ее и из аватара пользователя.
     */
    public void onDeleteEmployeePhotoAssociation(AssociationRef nodeAssocRef) {
        final NodeRef employee = nodeAssocRef.getSourceRef();
        final NodeRef person = orgstructureService.getPersonForEmployee(employee);
        // есть пользователь не привязан, то все тщетно
        if (person != null) {
            // получить аватар пользователя
            final NodeRef personAvatar = findNodeByAssociationRef(person, ContentModel.ASSOC_AVATAR, ContentModel.TYPE_CONTENT, ASSOCIATION_TYPE.TARGET);
            // если он существует...
            if (personAvatar != null) {
                // то удалить его
                nodeService.removeAssociation(person, personAvatar, ContentModel.ASSOC_AVATAR);
            }
        }
    }

    /**
     * Вызывается, когда к пользователю привязан новый аватар. Установить этот
     * аватар пользователю в качестве фотографии.
     * NB! Полиси не включена, так как в текущем видем зацикливается с
     * onCreateEmployeePhotoAssociation
     */
    public void onCreatePersonAvatarAssociation(AssociationRef nodeAssocRef) {
        final NodeRef person = nodeAssocRef.getSourceRef();
        final NodeRef employee = orgstructureService.getEmployeeByPerson(person, false);
        // если сотрудник не привязан, то все тщетно
        if (employee != null) {
            // получить ссылку на аватар
            final NodeRef personAvatar = nodeAssocRef.getTargetRef();
            // получить ссылку на фото
            final NodeRef employeePhoto = findNodeByAssociationRef(employee, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO, ContentModel.TYPE_CONTENT, ASSOCIATION_TYPE.TARGET);

            behaviourFilter.disableBehaviour(employee);
            try {
                // фото существует и не равно аватару
                if (employeePhoto != null && !employeePhoto.equals(personAvatar)) {
                    // грохнуть фото
                    nodeService.removeAssociation(employee, employeePhoto, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
                    // сотруднику аватар в качестве фото
                    nodeService.createAssociation(employee, personAvatar, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
                } else if (employeePhoto == null) {
                    // сотруднику аватар в качестве фото
                    nodeService.createAssociation(employee, personAvatar, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
                }
            } finally {
                behaviourFilter.enableBehaviour(employee);
            }
        }
    }

    /**
     * Удаление сотрудника. Возможно когда-нибудь понадобится.
     */
    public void onDeletePersonNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
    }

    /**
     * Изменение атрибутивного состава cm:person. Необходимо обновить атрибуты
     * соответствующего сотрудника.
     */
    public void onUpdatePersonProperties(final NodeRef person, Map<QName, Serializable> before, Map<QName, Serializable> after) {

        // проверка на тот случай, что полиси вызвана во время старта системы, когда происходит обнуление системной проперти
        if (PolicyUtils.safeEquals(before.get(ContentModel.PROP_SIZE_CURRENT), after.get(ContentModel.PROP_SIZE_CURRENT))) {
            // мы не будем создавать сотрудника для неактивных или неправильно заполненных пользователей
            if (personNeedsEmployee(person)) {
                try {
                    Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.orgstructure.employee.editor.enabled");
                    boolean enabled;
                    if (editorEnabled == null) {
                        enabled = true;
                    } else {
                        enabled = Boolean.valueOf((String) editorEnabled);
                    }
                    if (enabled) {
                        final ChildAssociationRef employeeNodeRef = nodeService.createNode(orgstructureService.getEmployeesDirectory(), ContentModel.ASSOC_CONTAINS,
                                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                                OrgstructureBean.TYPE_EMPLOYEE);

                        final NodeRef employeeNode = employeeNodeRef.getChildRef();

                        // после создания ассоциации будет вызвана полиси onCreateEmployeePersonAssociation, которая и скопирует атрибутивный состав
                        nodeService.createAssociation(employeeNode, person, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
                    }
                } catch (LecmBaseException e) {
                    logger.error("Cannot read orgstructure properties");
                }
                /*
				 * Нас интересует, не были ли изменены следующие атрибуты: cm:firstName, cm:lastName, cm:email, cm:telephone
				 * Атрибуты для сравнени лежат в AFFECTED_PERSON_PROPERTIES
				 * before будет пустым, если полиси была вызвана при создании нового пользователя.
				 * Для этого случая synchronizePersonAndEmployee уже была вызвана явным образом.
				 * Если не проводить такую проверку, то ломается cm:name
				 */
            } else if (!before.isEmpty() && synchronizablePropertiesChanged(before, after)) {
                // если что-то из этого было изменено, то синхронизируем атрибуты cm:person и lecm-orgstr:employee
                // для начала пролучаем сотрудника, привязанного к измененному пользователю
                final NodeRef employee = orgstructureService.getEmployeeByPerson(person, false);
                // если таковой присутствует...
                if (employee != null) {
                    // ...то запускаем синхронизацию
                    final String user = AuthenticationUtil.getFullyAuthenticatedUser();
                    AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
                    try{
                        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
                        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
                            @Override
                            public Void doWork() throws Exception {
                                return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>(){
                                    @Override
                                    public Void execute() throws Throwable {
                                        synchronizePersonAndEmployee(person, employee);
                                        return null;
                                    }
                                }, false, true);
                            }
                        }, AuthenticationUtil.getAdminUserName());
                    } finally{
                        AuthenticationUtil.setFullyAuthenticatedUser(user);
                    }
                }
            }
        }

    }

    private void synchronizePersonAndEmployee(NodeRef person, NodeRef employee) {
        String personLastName = "", personMiddleName = "";
        String[] personLastAndMiddleNameArr = null;

        Map<QName, Serializable> personProperties = nodeService.getProperties(person);
        String personFirstName = (String) personProperties.get(ContentModel.PROP_FIRSTNAME);
        String personLastAndMiddleName = (String) personProperties.get(ContentModel.PROP_LASTNAME);
        String personEmail = (String) personProperties.get(ContentModel.PROP_EMAIL);
        String personTelephone = (String) personProperties.get(ContentModel.PROP_TELEPHONE);

        // Фамилия и отчество будут жить в поле cm:lastName через пробел.
        // Так как поле необязательно для заполнения, то надо внимательно следить за входными данными.
        // Поля фамилии и отчетсва у сотрудника нельзя оставлять пустыми - из-за этого падает OrgstructureGenerateNamesPolicy.
        if (personLastAndMiddleName != null) {
            personLastAndMiddleNameArr = personLastAndMiddleName.split(" ");
        }
        if (personLastAndMiddleNameArr != null && personLastAndMiddleNameArr.length == 2) {
            personLastName = personLastAndMiddleNameArr[0];
            personMiddleName = personLastAndMiddleNameArr[1];
        } else if (personLastAndMiddleNameArr != null && personLastAndMiddleNameArr.length == 1 && personLastAndMiddleNameArr[0].length() > 0) {
            personLastName = personLastAndMiddleNameArr[0];
        }

        Map<QName, Serializable> employeeProperties = nodeService.getProperties(employee);

        if (personLastName.isEmpty()) {
            personLastName = personFirstName;
        }
        employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME, personFirstName);
        employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME, personLastName);
        if (!personMiddleName.isEmpty()) {
            employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME, personMiddleName);
        }
        employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_EMAIL, StringUtils.trim(personEmail));

        String employeeSex = (String) employeeProperties.get(OrgstructureBean.PROP_EMPLOYEE_SEX);
        if (!"MALE".equals(employeeSex) || !"FEMALE".equals(employeeSex)) {
            employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_SEX, "MALE");
        }

        if (!Boolean.FALSE.equals(employeeProperties.get(BaseBean.IS_ACTIVE))) {
            employeeProperties.put(BaseBean.IS_ACTIVE, true);
        }

        if (personTelephone != null && personTelephone.length() > 0) {
            employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_PHONE, personTelephone);
        }
        nodeService.setProperties(employee, employeeProperties);
    }

    private boolean synchronizablePropertiesChanged(Map<QName, Serializable> before, Map<QName, Serializable> after) {
        boolean changed = false;
        for (QName prop : AFFECTED_PERSON_PROPERTIES) {
            Serializable beforeProp = before.get(prop);
            Serializable afterProp = after.get(prop);
            if (!PolicyUtils.safeEquals(beforeProp, afterProp)) {
                changed = true;
                break;
            }
        }
        return changed;
    }

    private boolean personNeedsEmployee(NodeRef personNode) {
        NodeRef employeeNode = orgstructureService.getEmployeeByPerson(personNode, false);
        Set<QName> aspects = nodeService.getAspects(personNode);
        Map<QName, Serializable> personProperties = nodeService.getProperties(personNode);
        String firstName = (String) personProperties.get(ContentModel.PROP_FIRSTNAME);
        String email = (String) personProperties.get(ContentModel.PROP_EMAIL);

        return employeeNode == null && !aspects.contains(ContentModel.ASPECT_PERSON_DISABLED)
                && !aspects.contains(QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "incomplete"))
                && (firstName != null && !firstName.isEmpty())
                && (email != null && !email.isEmpty());

    }

}
