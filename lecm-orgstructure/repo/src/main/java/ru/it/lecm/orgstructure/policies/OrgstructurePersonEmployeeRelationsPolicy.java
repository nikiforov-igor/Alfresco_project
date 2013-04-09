package ru.it.lecm.orgstructure.policies;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * Полиси, которые регулируют отношения между lecm-orgstr:employee (сотрудником)
 * и cm:person (пользователем). При из связывании атрибутивный состав копируется
 * из person в employee. При создании person для него создается employee.
 *
 * @author vlevin
 */
public class OrgstructurePersonEmployeeRelationsPolicy extends SecurityJournalizedPolicyBase {

	private BehaviourFilter behaviourFilter;
	// атрибуты cm:person, при изменении которых мы будем проводить синхронизацию cm:person -> lecm-orgstr:employee
	private final static QName[] AFFECTED_PERSON_PROPERTIES = {ContentModel.PROP_FIRSTNAME, ContentModel.PROP_LASTNAME,
		ContentModel.PROP_EMAIL, ContentModel.PROP_TELEPHONE};

	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}

	@Override
	public final void init() {
		super.init();

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateEmployeeNodeLog",
				Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
				OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onDeleteEmployeeNode"));
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
		policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				ContentModel.TYPE_PERSON, ContentModel.ASSOC_AVATAR,
				new JavaBehaviour(this, "onDeletePersonAvatarAssociation"));


		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onCreatePersonNode"));
//		policyComponent.bindClassBehaviour(NodeServicePolicies.OnDeleteNodePolicy.QNAME,
//				ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onDeletePersonNode"));
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ContentModel.TYPE_PERSON, new JavaBehaviour(this, "onUpdatePersonProperties"));

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
		businessJournalService.log(node, EventCategory.ADD, "Сотрудник #initiator добавил нового сотрудника - #mainobject");
	}

	/**
	 * Удаление ноды сотрудника. Оповестить SG
	 */
	public void onDeleteEmployeeNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		final NodeRef employee = childAssocRef.getChildRef();
		final NodeRef person = orgstructureService.getPersonForEmployee(employee);
		notifyEmploeeDown(employee, person);
	}

	/**
	 * Атрибуты сотрудника переключены. Сотрудник помечен как неактивный
	 */
	public void onUpdateEmployeeProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean nowActive = (Boolean) after.get(BaseBean.IS_ACTIVE);
		final Boolean oldActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final boolean changed = !PolicyUtils.safeEquals(oldActive, nowActive);
		if (changed) { // произошло переключение активности -> отработать ...
			notifyEmploeeTie(nodeRef, nowActive);
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
			businessJournalService.log(nodeRef, EventCategory.EDIT, "Сотрудник #initiator внес изменения в сведения о сотруднике #mainobject");
		}

		if (changed && curActive != null && !curActive) {
			businessJournalService.log(nodeRef, EventCategory.DELETE, "Сотрудник #initiator удалил сведения о сотруднике #mainobject");
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
		final NodeRef employee = orgstructureService.getEmployeeByPerson(person);
		// если сотрудник не привязан, то все тщетно
		if (employee != null) {
			// получить ссылку на аватар
			final NodeRef personAvatar = nodeAssocRef.getTargetRef();
			// получить ссылку на фото
			final NodeRef employeePhoto = findNodeByAssociationRef(employee, ContentModel.ASSOC_AVATAR, ContentModel.TYPE_CONTENT, ASSOCIATION_TYPE.TARGET);

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
	 * Удален аватар пользователя. Удалить и фотографию сотрудника.
	 * NB! Полиси пока не влючена, так как из-за зацикливания
	 * onCreatePersonAvatarAssociation и onCreateEmployeePhotoAssociation
	 * принято решение синхронизировать фотографию итолько от сотрудника к
	 * пользователю.
	 */
	public void onDeletePersonAvatarAssociation(AssociationRef nodeAssocRef) {
		final NodeRef person = nodeAssocRef.getSourceRef();
		final NodeRef employee = orgstructureService.getEmployeeByPerson(person);
		// есть сотрудник не привязан, то все тщетно
		if (employee != null) {
			// получить фото
			final NodeRef employeePhoto = orgstructureService.getEmployeePhoto(employee);
			// если оно существует...
			if (employeePhoto != null) {
				// то удалить его
				nodeService.removeAssociation(employee, employeePhoto, OrgstructureBean.ASSOC_EMPLOYEE_PHOTO);
			}
		}
	}

	/**
	 * Создан новый пользователь. Создать сотрудника и ассоциировать его с
	 * пользователем.
	 */
	public void onCreatePersonNode(ChildAssociationRef childAssocRef) {
		final NodeRef personNode = childAssocRef.getChildRef();
		final ChildAssociationRef employeeNodeRef = nodeService.createNode(orgstructureService.getEmployeesDirectory(), ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				OrgstructureBean.TYPE_EMPLOYEE);

		final NodeRef employeeNode = employeeNodeRef.getChildRef();

		// после создания ассоциации будет вызвана полиси onCreateEmployeePersonAssociation, которая и скопирует атрибутивный состав
		nodeService.createAssociation(employeeNode, personNode, OrgstructureBean.ASSOC_EMPLOYEE_PERSON);
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
	public void onUpdatePersonProperties(NodeRef person, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		/*
		 * Нас интересует, не были ли изменены следующие атрибуты: cm:firstName, cm:lastName, cm:email, cm:telephone
		 * Атрибуты для сравнени лежат в AFFECTED_PERSON_PROPERTIES
		 * before будет пустым, если полиси была вызвана при создании нового пользователя.
		 * Для этого случая synchronizePersonAndEmployee уже была вызвана явным образом.
		 * Если не проводить такую проверку, то ломается cm:name
		 */

		if (!before.isEmpty() && synchronizablePropertiesChanged(before, after)) {
			// если что-то из этого было изменено, то синхронизируем атрибуты cm:person и lecm-orgstr:employee
			// для начала пролучаем сотрудника, привязанного к измененному пользователю
			NodeRef employee = orgstructureService.getEmployeeByPerson(person);
			// если таковой присутствует...
			if (employee != null) {
				// ...то запускаем синхронизацию
				synchronizePersonAndEmployee(person, employee);
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
//			personMiddleName = "Имярекович";
		}
//		else {
//			personLastName = "Имяреков";
//			personMiddleName = "Имярекович";
//		}

		Map<QName, Serializable> employeeProperties = nodeService.getProperties(employee);

		employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME, personFirstName);
		employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_LAST_NAME, personLastName);
		employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME, personMiddleName);
		employeeProperties.put(OrgstructureBean.PROP_EMPLOYEE_EMAIL, personEmail);

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
}
