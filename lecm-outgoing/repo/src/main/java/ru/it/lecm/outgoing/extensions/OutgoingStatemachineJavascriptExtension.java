package ru.it.lecm.outgoing.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vmalygin
 */
public class OutgoingStatemachineJavascriptExtension extends BaseWebScript {

	/**
	 * код бизнес роли "Исходящие. Отправляющий" Сотрудник, ответственный за отправку исходящих документов
	 */
	private final static String OUTGOING_SENDER = "OUTGOING_SENDER";
	private final static String DOCUMENT_FILE_REGISTER_NAMESPACE = "http://www.it.ru/logicECM/document/dictionaries/fileRegister/1.0";
	private final static QName ASSOC_DOCUMENT_FILE_REGISTER_UNIT = QName.createQName(DOCUMENT_FILE_REGISTER_NAMESPACE, "organization-unit-assoc");

	private final static Logger logger = LoggerFactory.getLogger(OutgoingStatemachineJavascriptExtension.class);

	private NodeService nodeService;
	private DocumentService documentService;
	private OrgstructureBean orgstructureService;

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentService(final DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setOrgstructureService(final OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	private String getOutgoingURL(final ScriptNode outgoingRef) {
		NodeRef outgoingDocumentRef = outgoingRef.getNodeRef();
		String presentString = (String) nodeService.getProperty(outgoingDocumentRef, DocumentService.PROP_PRESENT_STRING);
		return wrapperLink(outgoingDocumentRef.toString(), presentString, BaseBean.DOCUMENT_LINK_URL);
	}

	/**
	 * подговить уведомление Автору исходящего о доработке будет подготовлено уведомление с сообщением "Проект документа
	 * &lt;Вид документа&gt; № &lt;Номер документа&gt; направлен Вам на доработку"
	 *
	 * @param outgoingRef ссылка на исходящее из машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRework(final ScriptNode outgoingRef) {
		String outgoingURL = getOutgoingURL(outgoingRef);
		String description = String.format("Проект документа %s направлен Вам на доработку", outgoingURL);
		return prepareNotificationAboutRework(outgoingRef, description);
	}

	/**
	 * подговить уведомление Автору исходящего о доработке
	 *
	 * @param outgoingRef ссылка на исходящее из машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRework(final ScriptNode outgoingRef, final String description) {
		NodeRef documentAuthorRef = documentService.getDocumentAuthor(outgoingRef.getNodeRef());

		ArrayList<NodeRef> recipients = new ArrayList<NodeRef>();
		recipients.add(documentAuthorRef);

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(outgoingRef.getNodeRef());
		notification.setRecipientEmployeeRefs(recipients);
		return notification;
	}

	/**
	 * подготовить уведомление регистраторам о том, что надо зарегистрировать Исходящий будет подготовлено уведомление с
	 * сообщением "Документ &lt;Вид документа&gt; № &lt;Номер документа&gt; поступил Вам на регистрацию"
	 *
	 * @param outgoingRef ссылка на исходящее из машины состояний
	 * @param registrars js-массив регистраторов который мы получили из скрипта машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRegistration(final ScriptNode outgoingRef, final Scriptable registrars) {
		String outgoingURL = getOutgoingURL(outgoingRef);
		String description = String.format("Документ %s поступил Вам на регистрацию", outgoingURL);
		return prepareNotificationAboutRegistration(outgoingRef, registrars, description);
	}

	/**
	 * подготовить уведомление регистраторам о том, что надо зарегистрировать Исходящий
	 *
	 * @param outgoingRef ссылка на исходящее из машины состояний
	 * @param registrars js-массив регистраторов который мы получили из скрипта машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutRegistration(final ScriptNode outgoingRef, final Scriptable registrars, final String description) {
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
		notification.setObjectRef(outgoingRef.getNodeRef());
		notification.setRecipientEmployeeRefs(registrarRefs);
		return notification;
	}

	/**
	 * подготовить уведомление отправителям о том, что надо отправить Исходящий документ будет подготовлено уведомление
	 * с сообщением "На отправку поступил документ &lt;Вид документа&gt; № &lt;Номер документа&gt;"
	 *
	 * @param outgoingRef ссылка на исходящее из машины состояний
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutSending(final ScriptNode outgoingRef) {
		String outgoingURL = getOutgoingURL(outgoingRef);
		String description = String.format("На отправку поступил документ %s", outgoingURL);
		return prepareNotificationAboutSending(outgoingRef, description);
	}

	/**
	 * подготовить уведомление отправителям о том, что надо отправить Исходящий документ
	 *
	 * @param outgoingRef ссылка на исходящее из машины состояний
	 * @param description текст сообщения, которое необходимо отправить
	 * @return готовое к отправке уведомление
	 */
	public Notification prepareNotificationAboutSending(final ScriptNode outgoingRef, final String description) {
		//получаем список сотрудников включенных в статическую роль "Отправляющий" с учетом делегирования
		List<NodeRef> senders = orgstructureService.getEmployeesByBusinessRole(OUTGOING_SENDER, true);

		Notification notification = new Notification();
		notification.setAuthor(AuthenticationUtil.getSystemUserName());
		notification.setDescription(description);
		notification.setObjectRef(outgoingRef.getNodeRef());
		notification.setRecipientEmployeeRefs(senders);
		return notification;
	}

	/**
	 * проставить исходящему ассоциацию на подразделение для финализации, в зависимости от наличия/отсутствия
	 * номенклатуры дел
	 *
	 * @param outgoing исходящий документ
	 */
	public void configurePostSendingFinalization(final ScriptNode outgoing) {
		NodeRef outgoingRef = outgoing.getNodeRef();
		List<AssociationRef> assocs = nodeService.getTargetAssocs(outgoingRef, EDSDocumentService.ASSOC_FILE_REGISTER);
		if (assocs.isEmpty()) {
			NodeRef rootUnit = orgstructureService.getRootUnit();
			nodeService.setAssociations(outgoingRef, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, Arrays.asList(rootUnit));
		} else {
			if (assocs.size() > 1) {
				logger.warn("lecm-outgoing:document {} has multiple associations with file-register", outgoing);
			}
			NodeRef fileRegisterRef = assocs.get(0).getTargetRef();
			NodeRef fileRegisterDicUnit = nodeService.getPrimaryParent(fileRegisterRef).getParentRef();
			if (fileRegisterDicUnit != null) {
				List<AssociationRef> fileRegisterUnitAssocs = nodeService.getTargetAssocs(fileRegisterDicUnit, ASSOC_DOCUMENT_FILE_REGISTER_UNIT);
				if (fileRegisterUnitAssocs.size() > 0) {
					NodeRef fileRegisterUnit = fileRegisterUnitAssocs.get(0).getTargetRef();
					List<NodeRef> targetUnit = Arrays.asList(fileRegisterUnit);
					nodeService.setAssociations(outgoingRef, DocumentService.ASSOC_ORGANIZATION_UNIT_ASSOC, targetUnit);
				}
			}
		}
	}
}
