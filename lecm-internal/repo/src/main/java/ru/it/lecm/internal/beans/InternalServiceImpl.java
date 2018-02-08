/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.internal.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.internal.api.InternalService;

import java.util.EnumMap;

/**
 *
 * @author ikhalikov
 */
public class InternalServiceImpl extends BaseBean implements InternalService{

    private EnumMap<INTERNAL_STATUS, String> internalStatusesMap;

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public String getInternalStatusName(INTERNAL_STATUS code) {
        return internalStatusesMap != null ? internalStatusesMap.get(code) : null;
    }

    @Override
    protected void initServiceImpl() {
        internalStatusesMap = new EnumMap<INTERNAL_STATUS,String>(INTERNAL_STATUS.class){{
                put(INTERNAL_STATUS.DIRECTED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.internal.statemachine-status.directed", "Направлен"));
                put(INTERNAL_STATUS.CLOSED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.internal.statemachine-status.closed", "Закрыт"));
            }};
    }
}
