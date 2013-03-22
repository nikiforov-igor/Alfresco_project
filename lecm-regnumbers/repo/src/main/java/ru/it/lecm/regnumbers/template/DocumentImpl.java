package ru.it.lecm.regnumbers.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.regnumbers.counter.CounterFactory;
import ru.it.lecm.regnumbers.counter.CounterType;

/**
 *
 * @author vlevin
 */
public class DocumentImpl implements Document {

	private NodeRef documentNode;
	private NodeService nodeService;
	private CounterFactory counterFactory;
	private NamespaceService namespaceService;
	private ApplicationContext applicationContext;
	private DocumentMembersService documentMembersService;
	
	final private static Logger logger = LoggerFactory.getLogger(DocumentImpl.class);

	public DocumentImpl(NodeRef documentNode, ApplicationContext applicationContext) {
		this.documentNode = documentNode;
		this.applicationContext = applicationContext;
		this.nodeService = applicationContext.getBean("nodeService", NodeService.class);
		this.namespaceService = applicationContext.getBean("namespaceService", NamespaceService.class);
		this.counterFactory = applicationContext.getBean("regNumbersCounterFactory", CounterFactory.class);
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
				stringBuilder.append(itr.next());
				if (itr.hasNext()) {
					stringBuilder.append(", ");
				}
			}
			return stringBuilder.toString();
		} else if (resultAttributes.size() == 1) {
			return resultAttributes.get(0);
		} else {
			return "";
		}
	}

	@Override
	public int getTypeCode() {
		logger.warn("getTypeCode() not supported yet.");
		return 9999;
	}

	@Override
	public String getTypeName() {
		logger.warn("getTypeName() not supported yet.");
		return "documentTypeNamePlaseholder";
	}

	@Override
	public Date getCreationDate() {
		logger.warn("getCreationDate() not supported yet.");
		return new Date();
	}

	@Override
	public NodeRef member(String memberType) {
		logger.warn("member(String) not supported yet.");
		return null;
	}

	@Override
	public long getCounterPlain() {
		return counterFactory.getCounter(CounterType.PLAIN, documentNode).getValue();
	}

	@Override
	public long getCounterYear() {
		return counterFactory.getCounter(CounterType.YEAR, documentNode).getValue();
	}

	@Override
	public long getCounterPlainDoctype() {
		return counterFactory.getCounter(CounterType.DOCTYPE_PLAIN, documentNode).getValue();
	}

	@Override
	public long getCounterYearDoctype() {
		return counterFactory.getCounter(CounterType.DOCTYPE_YEAR, documentNode).getValue();
	}

	private Object getNodeRefAttribute(NodeRef node, String attributeName) {
		QName attributeQName = QName.createQName(attributeName, namespaceService);
		return nodeService.getProperty(node, attributeQName);
	}
}
