/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.meetings.scripts;

import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.meetings.beans.ProtocolService;

/**
 *
 * @author snovikov
 */
public class ProtocolWebScriptBean extends BaseWebScript {
	private NodeService nodeService;
    private ProtocolService protocolService;
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setProtocolService(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}
}
