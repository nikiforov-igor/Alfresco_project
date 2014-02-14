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

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
