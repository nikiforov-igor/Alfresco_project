package ru.it.lecm.ord.extensions;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author snovikov
 */
public class ORDStatemachineJavascriptExtension extends BaseWebScript {

	private final static Logger logger = LoggerFactory.getLogger(ORDStatemachineJavascriptExtension.class);
	private NodeService nodeService;
	private DocumentService documentService;
	private OrgstructureBean orgstructureService;
	private EDSGlobalSettingsService edsGlobalSettingsService;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(final DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setOrgstructureService(final OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	private String getOrdURL(final ScriptNode ordRef) {
		NodeRef ordDocumentRef = ordRef.getNodeRef();
		String presentString = (String) nodeService.getProperty(ordDocumentRef, DocumentService.PROP_PRESENT_STRING);
		return wrapperLink(ordDocumentRef.toString(), presentString, BaseBean.DOCUMENT_LINK_URL);
	}

	/**
	 * подговить уведомление Автору ОРД о доработке будет подготовлено
	 * уведомление с сообщением "Проект документа &lt;Вид документа&gt; №
	 * &lt;Номер документа&gt; направлен Вам на доработку"
	 *
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRework(final ScriptNode ordRef) {
		String ordURL = getOrdURL(ordRef);
		String description = String.format("Проект документа %s направлен Вам на доработку", ordURL);
		return prepareNotificationAboutRework(ordRef, description);
	}

	/**
	 * подговить уведомление Автору ОРД о доработке
	 *
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRework(final ScriptNode ordRef, final String description) {
		NodeRef documentAuthorRef = documentService.getDocumentAuthor(ordRef.getNodeRef());

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(documentAuthorRef);

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(ordRef.getNodeRef());
		notification.setRecipientEmployeeRefs(recipients);
		return notification;
	}

	/**
	 * подготовить список регистраторов для последующей раздачи им прав
	 *
	 * @param businessRoleId идентификатор бизнес-роли, которая содержит
	 * потенциальных регистраторов
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @return объект Scriptable пригодный для работы из яваскрипта машины
	 * состояний
	 */
	public Scriptable getRegistrars(final String businessRoleId, final ScriptNode ordRef) {
		List<NodeRef> registrars;
		//получаем текущего пользователя
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		// централизованная ли регистрация
		Boolean registrationCenralized = edsGlobalSettingsService.isRegistrationCenralized();

		if (registrationCenralized) {
			registrars = orgstructureService.getEmployeesByBusinessRole(businessRoleId);
		} else {
			registrars = new ArrayList<NodeRef>();
			//получаем основную должностную позицию
			NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(currentEmployee);
			if (primaryStaff != null) {
				NodeRef unit = orgstructureService.getUnitByStaff(primaryStaff);
				registrars.addAll(edsGlobalSettingsService.getPotentialWorkers(businessRoleId, unit));
			}
		}
		return createScriptable(registrars);
	}

	/**
	 * подготовить уведомление регистраторам о том, что надо зарегистрировать
	 * ОРД документ будет подготовлено уведомление с сообщением "Документ
	 * &lt;Вид документа&gt; № &lt;Номер документа&gt; поступил Вам на
	 * регистрацию"
	 *
	 * @param ordRef ссылка на ОРД документ из машины состояний
	 * @param registrars js-массив регистраторов который мы получили из скрипта
	 * машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRegistration(final ScriptNode ordRef, final Scriptable registrars) {
		String ordURL = getOrdURL(ordRef);
		String description = String.format("Документ %s поступил Вам на регистрацию", ordURL);
		return prepareNotificationAboutRegistration(ordRef, registrars, description);
	}

	/**
	 * подготовить уведомление регистраторам о том, что надо зарегистрировать
	 * ОРД документ
	 *
	 * @param Ref ссылка на ОРД документ из машины состояний
	 * @param registrars js-массив регистраторов который мы получили из скрипта
	 * машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRegistration(final ScriptNode ordRef, final Scriptable registrars, final String description) {
		Object[] elements = Context.getCurrentContext().getElements(registrars);
		ArrayList<NodeRef> registrarRefs = new ArrayList<NodeRef>();
		for (Object element : elements) {
			if (element instanceof ScriptNode) {
				ScriptNode registrar = (ScriptNode) element;
				registrarRefs.add(registrar.getNodeRef());
			} else if (element instanceof NativeJavaObject) {
				NativeJavaObject object = (NativeJavaObject) element;
				ScriptNode registrar = (ScriptNode) object.unwrap();
				registrarRefs.add(registrar.getNodeRef());
			} else {
				logger.warn("{} {} is not a ScriptNode in registrars array!", element.getClass().getName(), element);
			}
		}

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(ordRef.getNodeRef());
		notification.setRecipientEmployeeRefs(registrarRefs);
		return notification;
	}

	/**
	 * установить в атрибутах документа ассоциацию на того сотрудника который
	 * выполнил регистрацию документа
	 *
	 * @param ord ссылка на ОРД документ из машины состояний
	 * @param employee пользователь который выполнил действие "зарегистрировать"
	 * и перевел документ в статус "зарегистрирован"
	 */
	public void setRegistrar(final ScriptNode ord, final ScriptNode employee) {
		NodeRef ordRef = ord.getNodeRef();
		NodeRef employeeRef = employee.getNodeRef();
		List<AssociationRef> assocs = nodeService.getTargetAssocs(ordRef, ORDModel.ASSOC_ORD_REGISTRAR);
		if (assocs.isEmpty()) {
			nodeService.createAssociation(ordRef, employeeRef, ORDModel.ASSOC_ORD_REGISTRAR);
		} else {
			String presentString = (String) nodeService.getProperty(ordRef, DocumentService.PROP_PRESENT_STRING);
			String shortname = (String) nodeService.getProperty(employeeRef, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);
			String template = "Association between document %s[%s] and registrar %s[%s] already exists!";
			logger.error(String.format(template, ordRef.toString(), presentString, employeeRef.toString(), shortname));
		}
	}
}
