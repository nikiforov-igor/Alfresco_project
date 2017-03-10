package ru.it.lecm.documents.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.io.Serializable;

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
            this.settingsNode = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, DOCUMENT_GLOBAL_SETTINGS_NODE_NAME);
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
        Serializable modeValue = null;
        NodeRef settings = getSettingsNode();
        if (nodeService.exists(settings)) {
            modeValue = nodeService.getProperty(settings, PROP_SETTINGS_LINKS_VIEW_MODE);
        }
        return (null != modeValue) ? (String) modeValue : DEFAULT_VIEW_MODE;
    }

    @Override
    public boolean isEnablePassiveNotifications() {
        NodeRef globalSettingsNode = getSettingsNode();
        if (nodeService.exists(globalSettingsNode)) {
            return Boolean.TRUE.equals(nodeService.getProperty(globalSettingsNode, PROP_ENABLE_PASSIVE_NOTIFICATIONS));
        } else {
            return false;
        }
    }

    @Override
    public int getSettingsNDays() {
        Serializable nDays = null;
        NodeRef globalSettingsNode = getSettingsNode();
        if (nodeService.exists(globalSettingsNode)) {
            nDays = nodeService.getProperty(globalSettingsNode, PROP_N_DAYS);
        }
        return (null != nDays) ? (Integer) nDays : DEFAULT_N_DAYS;
    }
}
