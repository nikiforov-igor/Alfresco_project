package ru.it.lecm.resolutions.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.resolutions.api.ResolutionsService;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:41
 */
public class ResolutionsServiceImpl extends BaseBean implements ResolutionsService {
    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
