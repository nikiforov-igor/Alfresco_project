package ru.it.lecm.regnumbers.template;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.counter.CounterFactory;
import ru.it.lecm.regnumbers.counter.CounterFactoryImpl;
import ru.it.lecm.regnumbers.counter.CounterType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * Инкапсулирует методы, связанные с документом, для которого генерируется
 * регистрационный номер.
 *
 * @author vlevin
 */
public class DocumentImpl implements Document {

	private NodeRef documentNode;
	private NodeService nodeService;
	private CounterFactory counterFactory;
	private NamespaceService namespaceService;
	private DictionaryService dictionaryService;
	private OrgstructureBean orgstuctureService;
	private DocumentMembersService documentMembersService;
	final private static Logger logger = LoggerFactory.getLogger(DocumentImpl.class);

	public DocumentImpl(NodeRef documentNode, ApplicationContext applicationContext) {
		this.documentNode = documentNode;
		this.nodeService = applicationContext.getBean("nodeService", NodeService.class);
		this.namespaceService = applicationContext.getBean("namespaceService", NamespaceService.class);
		this.dictionaryService = applicationContext.getBean("dictionaryService", DictionaryService.class);
		this.counterFactory = applicationContext.getBean("regNumbersCounterFactory", CounterFactoryImpl.class);
		this.orgstuctureService = applicationContext.getBean("serviceOrgstructure", OrgstructureBean.class);
		this.documentMembersService = applicationContext.getBean("documentMembersService", DocumentMembersService.class);
	}

	@Override
	public Object attribute(String attributeName) {
		return getNodeRefAttribute(documentNode, attributeName);
	}

	@Override
	public Object associatedAttribute(String assocName, String attributeName) {
		List<Object> resultAttributes = new ArrayList<Object>();
		QName assocQName = QName.createQName(assocName, namespaceService);
		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(documentNode, assocQName);
		for (AssociationRef targetAssoc : targetAssocs) {
			resultAttributes.add(getNodeRefAttribute(targetAssoc.getTargetRef(), attributeName));
		}
		if (resultAttributes.size() > 1) {
			StringBuilder stringBuilder = new StringBuilder();
			ListIterator itr = resultAttributes.listIterator();
			while (itr.hasNext()) {
				Object next = itr.next();
				if (next != null) {
					stringBuilder.append(next);
					if (itr.hasNext()) {
						stringBuilder.append(", ");
					}
				} else {
					if (stringBuilder.lastIndexOf(", ") == stringBuilder.length() - 2) {
						stringBuilder.deleteCharAt(stringBuilder.length() - 2);
					}
				}
			}
			return stringBuilder.toString();
		} else if (resultAttributes.size() == 1 && resultAttributes.get(0) != null) {
			return resultAttributes.get(0);
		} else {
			return "";
		}
	}

	@Override
	public Object associatedAttributePath(String attributePath) {
		Object result;
		NodeRef currentNode = documentNode;
		String pathElements[] = attributePath.split("/");

		for (int i = 0; i < pathElements.length - 1; i++) {
			QName assocQName = QName.createQName(pathElements[i], namespaceService);
			List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(currentNode, assocQName);
			if (targetAssocs == null || targetAssocs.isEmpty()) {
				currentNode = null;
				break;
			} else {
				currentNode = targetAssocs.get(0).getTargetRef();
			}
		}

		if (currentNode == null) {
			result = "";
		} else {
			result = getNodeRefAttribute(currentNode, pathElements[pathElements.length - 1]);
			if (result == null) {
				result = "";
			}
		}

		return result;
	}

	@Override
	public NodeRef getAssoc(String assocName) {
		QName assocQName = QName.createQName(assocName, namespaceService);
		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(documentNode, assocQName);
		if (!targetAssocs.isEmpty()){
			return targetAssocs.get(0).getTargetRef();
		}
		return null;
	}

	@Override
	public int getTypeCode() {
		logger.warn("getTypeCode() not supported yet.");
		return 9999;
	}

	@Override
	public String getTypeName() {
		TypeDefinition documentType = dictionaryService.getType(nodeService.getType(documentNode));
		return documentType.getTitle();
	}

	@Override
	public Date getCreationDate() {
		return (Date) nodeService.getProperty(documentNode, ContentModel.PROP_CREATED);
	}

	@Override
	public NodeRef member(String memberType) {
		logger.warn("member(String) not supported yet.");
		return null;
	}

	@Override
	public long getCounterPlain() {
		return counterFactory.getCounter(CounterType.PLAIN, documentNode, null).getValue();
	}

	@Override
	public long getCounterYear() {
		return counterFactory.getCounter(CounterType.YEAR, documentNode, null).getValue();
	}

	@Override
	public long getCounterPlainDoctype() {
		return counterFactory.getCounter(CounterType.DOCTYPE_PLAIN, documentNode, null).getValue();
	}

	@Override
	public long getCounterYearDoctype() {
		return counterFactory.getCounter(CounterType.DOCTYPE_YEAR, documentNode, null).getValue();
	}

	@Override
	public long counterPlainDoctype(String tag) {
		return counterFactory.getCounter(CounterType.DOCTYPE_PLAIN, documentNode, tag).getValue();
	}

	@Override
	public long counterYearDoctype(String tag) {
		return counterFactory.getCounter(CounterType.DOCTYPE_YEAR, documentNode, tag).getValue();
	}

	@Override
	public long getCounterSignedDocflow() {
		return counterFactory.getCounter(CounterType.SIGNED_DOCFLOW, documentNode, null).getValue();
	}

	private Object getNodeRefAttribute(NodeRef node, String attributeName) {
		QName attributeQName = QName.createQName(attributeName, namespaceService);
		return nodeService.getProperty(node, attributeQName);
	}

	@Override
	public NodeRef getCreator() {
		String creatorPerson = (String) nodeService.getProperty(documentNode, ContentModel.PROP_CREATOR);
		return orgstuctureService.getEmployeeByPerson(creatorPerson);
	}

	@Override
	public NodeRef getModifier() {
		String modifierPerson = (String) nodeService.getProperty(documentNode, ContentModel.PROP_MODIFIER);
		return orgstuctureService.getEmployeeByPerson(modifierPerson);
	}
}
