package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
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

   /* private ServiceRegistry serviceRegistry;
    private SearchService searchService;
    private OrgstructureBean orgstructureService;
    private SubstitudeBean substituteService;
    private PersonService personService;
    private AuthenticationService authService;*/

    private NodeRef ROOT;
    private OrgstructureBean orgstructureService;

    /*public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSubstituteService(SubstitudeBean substituteService) {
        this.substituteService = substituteService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }*/

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void init() {
        ROOT = getFolder(DFA_ROOT_ID);
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
    public Map<String, Long> getFrequenciesCounts(NodeRef employee, String docType) {
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

    @Override
    public void updateFrequencyCount(NodeRef employee, String docType, String actionId, Long newCount) {
        NodeRef freqUnit = getOrCreateFrequencyUnit(employee, docType, actionId);
        if (freqUnit != null) {
            updateFrequencyCount(freqUnit, newCount);
        }
    }

    public void updateFrequencyCount(NodeRef employee, String docType, String actionId) {
        NodeRef freqUnit = getOrCreateFrequencyUnit(employee, docType, actionId);
        if (freqUnit != null) {
            Long current = (Long) nodeService.getProperty(freqUnit, PROP_UNIT_COUNT);
            updateFrequencyCount(freqUnit, current + 1);
        }
    }

    private void updateFrequencyCount(NodeRef fuRef, Long newCount) {
        nodeService.setProperty(fuRef, PROP_UNIT_COUNT, newCount);
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

    private synchronized NodeRef getWorkDirectory(NodeRef employee, String docType) {
        final String typeFolderName = docType.replace(":", "_");
        final String employeeFolderName = orgstructureService.getEmployeeLogin(employee);
        NodeRef docTypeFolderRef = nodeService.getChildByName(ROOT, ContentModel.ASSOC_CONTAINS, employeeFolderName);
        if (docTypeFolderRef == null) {
            AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
                            NodeRef directoryRef;
                            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, employeeFolderName);
                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                            properties.put(ContentModel.PROP_NAME, employeeFolderName);
                            directoryRef = nodeService.createNode(ROOT, assocTypeQName, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
                            return directoryRef;
                        }
                    });
                }
            };
            docTypeFolderRef = AuthenticationUtil.runAsSystem(raw);

        }
        final NodeRef SUB_ROOT = docTypeFolderRef;
        NodeRef workDirectory = nodeService.getChildByName(SUB_ROOT, ContentModel.ASSOC_CONTAINS, typeFolderName);
        if (workDirectory == null) {
            AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
                            NodeRef directoryRef;
                            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, typeFolderName);
                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                            properties.put(ContentModel.PROP_NAME, typeFolderName);
                            directoryRef = nodeService.createNode(SUB_ROOT, assocTypeQName, assocQName, ContentModel.TYPE_FOLDER, properties).getChildRef();
                            return directoryRef;
                        }
                    });
                }
            };
            return AuthenticationUtil.runAsSystem(raw);

        }
        return workDirectory;
    }

    private synchronized NodeRef getOrCreateFrequencyUnit(final NodeRef employee, final String docType, final String actionId) {
        NodeRef unitRef = getFrequencyUnit(employee, docType, actionId);
        if (unitRef == null) {
            AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
                            NodeRef directoryRef;
                            QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                            final String name = GUID.generate();
                            NodeRef root = getWorkDirectory(employee, docType);
                            QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
                            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                            properties.put(ContentModel.PROP_NAME, name);
                            properties.put(PROP_UNIT_DOC_TYPE, docType);
                            properties.put(PROP_UNIT_ACTION_ID, actionId);
                            directoryRef = nodeService.createNode(root, assocTypeQName, assocQName, TYPE_FREQUENCY_UNIT, properties).getChildRef();
                            return directoryRef;
                        }
                    });
                }
            };
            return AuthenticationUtil.runAsSystem(raw);
        }
        return unitRef;
    }
}
