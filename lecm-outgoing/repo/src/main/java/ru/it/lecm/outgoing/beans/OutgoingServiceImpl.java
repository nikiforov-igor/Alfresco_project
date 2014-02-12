package ru.it.lecm.outgoing.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.outgoing.api.OutgoingModel;
import ru.it.lecm.outgoing.api.OutgoingService;

/**
 *
 * @author vmalygin
 */
public class OutgoingServiceImpl extends BaseBean implements OutgoingService {

	private EDSGlobalSettingsService edsGlobalSettingsService;

    public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
        this.edsGlobalSettingsService = edsGlobalSettingsService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

	@Override
    public NodeRef getSettingsNode() {
        final NodeRef rootFolder = edsGlobalSettingsService.getServiceRootFolder();
		//String bla = OutgoingModel.OUTGOING_NAMESPACE;
		
        NodeRef settings = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, OutgoingModel.OUTGOING_SETTINGS_NODE_NAME);
        if (settings != null) {
            return settings;
        } else {
            AuthenticationUtil.RunAsWork<NodeRef> raw = new AuthenticationUtil.RunAsWork<NodeRef>() {
                @Override
                public NodeRef doWork() throws Exception {
                    return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                        @Override
                        public NodeRef execute() throws Throwable {
                            NodeRef settingsRef = nodeService.getChildByName(rootFolder, ContentModel.ASSOC_CONTAINS, OutgoingModel.OUTGOING_SETTINGS_NODE_NAME);
                            if (settingsRef == null) {
                                QName assocTypeQName = ContentModel.ASSOC_CONTAINS;
                                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, OutgoingModel.OUTGOING_SETTINGS_NODE_NAME);
                                QName nodeTypeQName = OutgoingModel.TYPE_SETTINGS;

                                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
                                properties.put(ContentModel.PROP_NAME, OutgoingModel.OUTGOING_SETTINGS_NODE_NAME);
                                ChildAssociationRef associationRef = nodeService.createNode(rootFolder, assocTypeQName, assocQName, nodeTypeQName, properties);
                                settingsRef = associationRef.getChildRef();
                            }
                            return settingsRef;
                        }
                    });
                }
            };
            return AuthenticationUtil.runAsSystem(raw);
        }
    }

	public boolean isRegistrationCenralized() {
        NodeRef settings = getSettingsNode();
        if (settings != null) {
            return (Boolean) nodeService.getProperty(settings, OutgoingModel.PROP_SETTINGS_CENTRALIZED_REGISTRATION);
        }
        return false;
    }
}
