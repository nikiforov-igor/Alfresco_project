/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.internal.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.internal.api.InternalService;

/**
 *
 * @author ikhalikov
 */
public class InternalServiceImpl extends BaseBean implements InternalService{

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }
}
