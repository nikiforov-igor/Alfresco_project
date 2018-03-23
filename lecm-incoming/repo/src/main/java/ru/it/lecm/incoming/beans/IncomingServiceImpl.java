package ru.it.lecm.incoming.beans;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: AIvkin
 * Date: 17.01.14
 * Time: 11:59
 */
public class IncomingServiceImpl extends BaseBean {
	public static final String INCOMING_NAMESPACE_URI = "http://www.it.ru/logicECM/incoming/1.0";

	public static final QName TYPE_INCOMING = QName.createQName(INCOMING_NAMESPACE_URI, "document");

    public static final QName ASSOC_RECIPIENT = QName.createQName(INCOMING_NAMESPACE_URI, "recipient-assoc");
    public static final QName ASSOC_DELIVERY_METHOD = QName.createQName(INCOMING_NAMESPACE_URI, "delivery-method-assoc");
    public static final QName ASSOC_ADDRESSEE = QName.createQName(INCOMING_NAMESPACE_URI, "addressee-assoc");
    public static final QName ASSOC_SENDER = QName.createQName(INCOMING_NAMESPACE_URI, "sender-assoc");

    public static final QName PROP_IS_BY_CHANNEL = QName.createQName(INCOMING_NAMESPACE_URI, "is-by-channel");

    private SubstitudeBean substitudeBean;

	public void setSubstitudeBean(SubstitudeBean substitudeBean) {
		this.substitudeBean = substitudeBean;
	}

	private Comparator<NodeRef> comparatorByDescription = new Comparator<NodeRef>() {
		@Override
		public int compare(NodeRef o1, NodeRef o2) {
			String name1 = substitudeBean.getObjectDescription(o1);
			String name2 = substitudeBean.getObjectDescription(o2);
			return name1.compareTo(name2);
		}
	};

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public String getSortedRecipientValue(NodeRef document) {
		List<AssociationRef> recipients = nodeService.getTargetAssocs(document, ASSOC_RECIPIENT);
		if (!recipients.isEmpty()) {
			List<NodeRef> units = new ArrayList<>();
			List<NodeRef> employees = new ArrayList<>();
			for (AssociationRef recipientAssoc : recipients) {
				NodeRef recipient = recipientAssoc.getTargetRef();
				QName recipientType = this.nodeService.getType(recipient);
				if (OrgstructureBean.TYPE_EMPLOYEE.isMatch(recipientType)) {
					employees.add(recipient);
				} else {
					units.add(recipient);
				}
			}

			/*SORT*/
			units.sort(comparatorByDescription);
			employees.sort(comparatorByDescription);

			/*Merge*/
			List<NodeRef> resultedList = new ArrayList<>(units);
			resultedList.addAll(employees);

			return StringUtils.collectionToDelimitedString(resultedList, ",");
		}
		return "";
	}
}
