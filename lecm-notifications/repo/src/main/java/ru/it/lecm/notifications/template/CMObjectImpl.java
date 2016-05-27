package ru.it.lecm.notifications.template;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.base.beans.SubstitudeBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vkuprin
 */
public class CMObjectImpl implements CMObject {

	protected final NodeRef nodeRef;
	protected final NodeService nodeService;
	protected final NamespaceService namespaceService;
	protected final ApplicationContext applicationContext;
	protected final ContentService contentService;
	private final static String LINK_URL = "view-metadata";
	
	CMObjectImpl(NodeRef ref, ApplicationContext applicationContext) {
		this.nodeRef = ref;
		this.applicationContext = applicationContext;
		this.nodeService = applicationContext.getBean("nodeService", NodeService.class);
		this.namespaceService = applicationContext.getBean("namespaceService", NamespaceService.class);
		this.contentService = applicationContext.getBean("contentService", ContentService.class);
	}

	@Override
	public Serializable attribute(String attributeName) {
		QName attributeQName = QName.createQName(attributeName, namespaceService);
		return nodeService.getProperty(nodeRef, attributeQName);
	}

	@Override
	public CMObject getAssoc(String assocName) {
		QName assocQName = QName.createQName(assocName, namespaceService);
		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(nodeRef, assocQName);
		if (!targetAssocs.isEmpty()){
			return new CMObjectImpl(targetAssocs.get(0).getTargetRef(), applicationContext) ;
		}
		return null;
	}
	
	@Override
	public List<CMObject> getAssocs(String assocName) {
		List<CMObject> result = null;
		QName assocQName = QName.createQName(assocName, namespaceService);
		List<AssociationRef> targetAssocs = nodeService.getTargetAssocs(nodeRef, assocQName);
		if (!targetAssocs.isEmpty()){
			result = new ArrayList<>();
			result.add(new CMObjectImpl(targetAssocs.get(0).getTargetRef(), applicationContext) );
		}
		return result;
	}

	@Override
	public NodeRef getNodeRef() {
		return nodeRef;
	}

	@Override
	public String getType() {
		return nodeService.getType(nodeRef).toPrefixString(namespaceService);
	}

	@Override
	public String getViewUrl() {
		ServiceRegistry serviceRegistry = applicationContext.getBean("ServiceRegistry", ServiceRegistry.class);
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String context = params.getShareContext();
        return "/" + context + "/page/"+ LINK_URL + "?nodeRef="+nodeRef.toString();
	}

	@Override
	public String getPresentString() {
		return nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
	}
	
	@Override
	public String getFormatted(String substitudeString) {
		SubstitudeBean substitudeBean = applicationContext.getBean("substitudeService", SubstitudeBean.class);
		return substitudeBean.formatNodeTitle(nodeRef, substitudeString);
	}
	
	@Override
	public String wrapAsLink(String description) {
		ServiceRegistry serviceRegistry = applicationContext.getBean("ServiceRegistry", ServiceRegistry.class);
        SysAdminParams params = serviceRegistry.getSysAdminParams();
		String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        String result = "<a href=\"" 
				+ serverUrl + this.getViewUrl() + "\">"
				+ description + "</a>";
		return result;
	}
	
	@Override
	public String wrapAsLink() {
		return this.wrapAsLink(this.getPresentString());
	}

	@Override
	public String getContentAsBase64() {
		ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		InputStream contentInputStream = reader.getContentInputStream();
		try {
			String base64String = Base64.encodeBase64String(IOUtils.toByteArray(contentInputStream));
			String mimeType = reader.getMimetype();
			return "data:" + mimeType + ";base64," + base64String;
		} catch (IOException e) {
			return "";
		}
	}
	
}
