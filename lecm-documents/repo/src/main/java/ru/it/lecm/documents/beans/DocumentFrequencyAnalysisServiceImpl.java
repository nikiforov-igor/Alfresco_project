package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.TransactionNeededException;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 19.03.13
 * Time: 11:51
 */
public class DocumentFrequencyAnalysisServiceImpl extends BaseBean implements DocumentFrequencyAnalysisService {
	final protected Logger logger = LoggerFactory.getLogger(DocumentFrequencyAnalysisServiceImpl.class);
    private OrgstructureBean orgstructureService;
    private int maxLastDocumentsToSave;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setMaxLastDocumentsToSave(int maxLastDocumentsToSave) {
        this.maxLastDocumentsToSave = maxLastDocumentsToSave;
    }

    public void init() {
    }

    @Override
    public Long getFrequencyCount(NodeRef employee, String docType, String actionId) {
        NodeRef freqUnit = getFrequencyUnit(employee, docType, actionId);
        if (freqUnit != null) {
            return (Long) nodeService.getProperty(freqUnit, PROP_UNIT_COUNT);
        }
        return 0L;
    }

    @Override
    public Map<String, Long> getFrequenciesCountsByDocType(NodeRef employee, String docType) {
        List<AssociationRef> freqUnitsAssocs = nodeService.getSourceAssocs(employee, ASSOC_UNIT_EMPLOYEE);
        Map<String, Long> freqs = new HashMap<String, Long>();
        for (AssociationRef freqUnitsAssoc : freqUnitsAssocs) {
            NodeRef freqUnit = freqUnitsAssoc.getSourceRef();
            String type = (String) nodeService.getProperty(freqUnit, PROP_UNIT_DOC_TYPE);
            if (type.equals(docType)) {
                String action = (String) nodeService.getProperty(freqUnit, PROP_UNIT_ACTION_ID);
                Long count = (Long) nodeService.getProperty(freqUnit, PROP_UNIT_COUNT);
                freqs.put(action, count);
            }
        }
        return freqs;
    }

    public void updateFrequencyCount(NodeRef employee, String docType, String actionId) {
//        NodeRef freqUnit = getOrCreateFrequencyUnit(employee, docType, actionId);
		NodeRef freqUnit = getFrequencyUnit(employee, docType, actionId);
        if (freqUnit != null) {
            Long current = (Long) nodeService.getProperty(freqUnit, PROP_UNIT_COUNT);
            nodeService.setProperty(freqUnit, PROP_UNIT_COUNT, current + 1);
        }
    }

    @Override
    public NodeRef getFrequencyUnit(NodeRef employee, String docType, String actionId) {
        List<AssociationRef> freqUnitsAssocs = nodeService.getSourceAssocs(employee, ASSOC_UNIT_EMPLOYEE);
        for (AssociationRef freqUnitsAssoc : freqUnitsAssocs) {
            NodeRef freqUnit = freqUnitsAssoc.getSourceRef();
            String type = (String) nodeService.getProperty(freqUnit, PROP_UNIT_DOC_TYPE);
            if (type.equals(docType)) {
                String action = (String) nodeService.getProperty(freqUnit, PROP_UNIT_ACTION_ID);
                if (action.equals(actionId)) {
                    return freqUnit;
                }
            }
        }
        return null;
    }

    @Override
    public List<NodeRef> getFrequencyUnits(NodeRef employee, String docType) {
        List<NodeRef> units = new ArrayList<NodeRef>();
        List<AssociationRef> freqUnitsAssocs = nodeService.getSourceAssocs(employee, ASSOC_UNIT_EMPLOYEE);
        for (AssociationRef freqUnitsAssoc : freqUnitsAssocs) {
            NodeRef freqUnit = freqUnitsAssoc.getSourceRef();
            String type = (String) nodeService.getProperty(freqUnit, PROP_UNIT_DOC_TYPE);
            if (type.equals(docType)) {
                units.add(freqUnit);
            }
        }
        return units;
    }

    public NodeRef getWorkDirectory(final NodeRef employee) {
//		TODO: Метод разделён, создание вынесено в метод createDocTypeFolder
        final String employeeFolderName = orgstructureService.getEmployeeLogin(employee);
		final NodeRef rootRef = getServiceRootFolder();
		return nodeService.getChildByName(rootRef, ContentModel.ASSOC_CONTAINS, employeeFolderName);
    }

	@Override
	public NodeRef createDocTypeFolder(NodeRef employee) throws WriteTransactionNeededException{
//		Проверка на выполнение в транзакции
		try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create doc type folder for employee " + employee);
        }
		final String employeeFolderName = orgstructureService.getEmployeeLogin(employee);
		final NodeRef rootRef = getServiceRootFolder();
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, employeeFolderName);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		properties.put(ContentModel.PROP_NAME, employeeFolderName);
		return nodeService.createNode(rootRef, assocTypeQName, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
	}

	public NodeRef createWorkDirectory(NodeRef employee, String docType) throws WriteTransactionNeededException {
//		Проверка на выполнение в транзакции
		try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create work directory for employee " + employee);
        }
		final String typeFolderName = docType.replace(":", "_");
		final NodeRef SUB_ROOT = createDocTypeFolder(employee);
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, typeFolderName);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		properties.put(ContentModel.PROP_NAME, typeFolderName);
		return nodeService.createNode(SUB_ROOT, assocTypeQName, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
	}

	@Override
	public NodeRef createFrequencyUnit(final NodeRef employee, final String docType, final String actionId) throws WriteTransactionNeededException {
//		Проверка на выполнение в транзакции
		try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create work directory for employee " + employee);
        }
		QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
		final String name = GUID.generate();
		NodeRef root = getWorkDirectory(employee);
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
		properties.put(ContentModel.PROP_NAME, name);
		properties.put(PROP_UNIT_DOC_TYPE, docType);
		properties.put(PROP_UNIT_ACTION_ID, actionId);
		return nodeService.createNode(root, assocTypeQName, assocQName, TYPE_FREQUENCY_UNIT, properties).getChildRef();
	}

    private NodeRef getLastDocumentsContainer(NodeRef employee) {
        List<AssociationRef> lastDocsContainers = nodeService.getSourceAssocs(employee, ASSOC_LAST_DOC_TO_EMPLOYEE);
        if (lastDocsContainers != null && !lastDocsContainers.isEmpty()) {
            AssociationRef lastDocContainerAssoc = lastDocsContainers.get(0);
            return lastDocContainerAssoc.getSourceRef();
        }
        return null;
    }

    @Override
    public String getLastDocuments() {
        NodeRef employee = orgstructureService.getCurrentEmployee();
        NodeRef lastDocContainer = getLastDocumentsContainer(employee);
        if (lastDocContainer != null) {
            String lastDocs = (String) nodeService.getProperty(lastDocContainer, PROP_LAST_DOCUMENTS);
            if (lastDocs != null) {
                return lastDocs;
            }
        }
        return "";
    }

    @Override
    public boolean checkLastDocuments(NodeRef document) {
        NodeRef employee = orgstructureService.getCurrentEmployee();
        NodeRef lastDocContainer = getLastDocumentsContainer(employee);
        if (lastDocContainer != null) {
            String lastDocs = (String) nodeService.getProperty(lastDocContainer, PROP_LAST_DOCUMENTS);
            if (lastDocs != null && lastDocs.contains(document.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean saveToLastDocuments(NodeRef document) throws WriteTransactionNeededException {
        NodeRef employee = orgstructureService.getCurrentEmployee();
        //		Проверка на выполнение в транзакции
        try {
            lecmTransactionHelper.checkTransaction();
        } catch (TransactionNeededException ex) {
            throw new WriteTransactionNeededException("Can't create doc type folder for employee " + employee);
        }
        NodeRef lastDocContainer = getLastDocumentsContainer(employee);
        if (lastDocContainer != null) {
            String lastDocs = (String) nodeService.getProperty(lastDocContainer, PROP_LAST_DOCUMENTS);
            if (lastDocs != null) {
                lastDocs = lastDocs.replace(document.toString() + ";", ""); //убираем, если уже есть в списке
                lastDocs += document.toString() + ";"; //добавляем в конец
                if (StringUtils.countOccurrencesOf(lastDocs, ";") > maxLastDocumentsToSave) {
                    lastDocs = lastDocs.substring(lastDocs.indexOf(";") + 1); //убираем лишние
                }
                nodeService.setProperty(lastDocContainer, PROP_LAST_DOCUMENTS, lastDocs);
                return true;
            }
        } else {
            NodeRef workDirectory = getWorkDirectory(employee);
            if (workDirectory == null) {
                //имеем право вызвать, т.к. метод изначально должен вызываться в транзакции
                workDirectory = createDocTypeFolder(employee);
            }
            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate());
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
            properties.put(PROP_LAST_DOCUMENTS, document.toString() + ";");
            lastDocContainer = nodeService.createNode(workDirectory, ContentModel.ASSOC_CONTAINS, assocQName, TYPE_EMPLOYEE_LAST_DOCUMENTS, properties).getChildRef();
            nodeService.createAssociation(lastDocContainer, employee, ASSOC_LAST_DOC_TO_EMPLOYEE);
            return true;
        }
        return false;
    }

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
            return getFolder(DFA_ROOT_ID);
	}
}
