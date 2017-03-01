package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

/**
 *
 * @author apalm
 */

public class DocumentGlobalSettingsServiceImpl extends BaseBean implements DocumentGlobalSettingsService {

    private final static Logger logger = LoggerFactory.getLogger(DocumentGlobalSettingsServiceImpl.class);
    private NodeRef settingsNode;
    
    @Override
    public NodeRef getServiceRootFolder() {
        return getFolder(DOCUMENT_GLOBAL_SETTINGS_FOLDER_ID);
    }

	@Override
	protected void initServiceImpl() {
		if (null == getSettingsNode()) {
            lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
                @Override
                public NodeRef execute() throws Throwable {
                    return createSettingsNode();
                }
            });
        }
	}

    @Override
    public NodeRef getSettingsNode() {
        if (null == this.settingsNode) {
            this.settingsNode = this.settingsNode = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, DOCUMENT_GLOBAL_SETTINGS_NODE_NAME);
        }
        return this.settingsNode;
    }

    private NodeRef createSettingsNode() throws WriteTransactionNeededException {
        return createNode(getServiceRootFolder(), TYPE_SETTINGS, DOCUMENT_GLOBAL_SETTINGS_NODE_NAME, null);
    }
    
    @Override
    public Boolean isHideProperties() {
        NodeRef settings = getSettingsNode();
        if (nodeService.exists(settings)) {
            return Boolean.TRUE.equals(nodeService.getProperty(settings, PROP_SETTINGS_HIDE_PROPS));
        }
        return false;
    }
    
    @Override
    public String getLinksViewMode() {
        String mode = null;
        NodeRef settings = getSettingsNode();
        if (nodeService.exists(settings)) {
            mode = nodeService.getProperty(settings, PROP_SETTINGS_LINKS_VIEW_MODE).toString();
        }
        return mode != null ? mode : "VIEW_ALL";
    }

    @Override
    public boolean isEnablePassiveNotifications() {
        NodeRef globalSettingsNode = getSettingsNode();
        if (globalSettingsNode != null) {
            return (Boolean) nodeService.getProperty(globalSettingsNode, PROP_ENABLE_PASSIVE_NOTIFICATIONS);
        } else {
            return false;
        }
    }

    @Override
    public int getSettingsNDays() {
        NodeRef globalSettingsNode = getSettingsNode();
        if (globalSettingsNode != null) {
            return (Integer) nodeService.getProperty(globalSettingsNode, PROP_N_DAYS);
        } else {
            return DEFAULT_N_DAYS;
        }
    }
}
