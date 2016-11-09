package ru.it.lecm.modelEditor.beans;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import static ru.it.lecm.modelEditor.beans.FormsEditorBeanImpl.FORMS_EDITOR_MODELS_DEPLOY_UUID;

/**
 *
 * @author vmalygin
 */
public class ControlsEditorBeanImpl extends BaseBean {

	private final static Logger logger = LoggerFactory.getLogger(ControlsEditorBeanImpl.class);
	public static final String CONTROLS_EDITOR_ROOT_ID = "CONTROLS_EDITOR_ROOT_ID";

	public final static String CONTROLS_EDITOR_NAMESPACE = "http://www.it.ru/lecm/controls/editor/1.0";
	public final static String CONTROLS_EDITOR_PREFIX = "lecm-controls-editor";
	public final static QName TYPE_CONTROL = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "control");
	public final static QName TYPE_CONTROL_PARAM = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "control-param");
	public final static QName PROP_CONTROL_ID = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "control-id");
	public final static QName PROP_CONTROL_TEMPLATE = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "control-template");
	public final static QName PROP_CONTROL_DEFAULT = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "control-default");
	public final static QName PROP_PARAM_ID = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "param-id");
	public final static QName PROP_PARAM_VALUE = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "param-value");
	public final static QName PROP_PARAM_MANDATORY = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "param-mandatory");
	public final static QName PROP_PARAM_VISIBLE = QName.createQName(CONTROLS_EDITOR_NAMESPACE, "param-visible");

	protected VersionService versionService;
	protected CheckOutCheckInService cociService;
	protected ContentService contentService;
	protected DictionaryService dictionaryService;
	protected NamespaceService namespaceService;

	protected NodeRef getDeploymentFolder() {
		NodeRef folder;
		NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, FORMS_EDITOR_MODELS_DEPLOY_UUID);
		if (nodeService.exists(nodeRef)) {
			folder = nodeRef;
		} else {
			String msg = String.format("Node %s does not exist! Check bootstrap-forms-editor.xml", nodeRef);
			throw new AlfrescoRuntimeException(msg);
		}
		return folder;
	}

	protected String getTypeControlsFilename(String typename) {
		return typename.replace(":", "_") + "-controls.xml";
	}

	protected NodeRef geTypeControlsNode(String typename) {
		NodeRef parent = getDeploymentFolder();
		return nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, getTypeControlsFilename(typename));
	}

	protected String getTypeLocalName(String typename) {
		QName typeQName;
		String typeLocalName;
		try {
			typeQName = QName.createQName(typename, namespaceService);
			TypeDefinition typeDefinition = dictionaryService.getType(typeQName);
			typeLocalName = typeDefinition.getTitle(dictionaryService);
		} catch(RuntimeException ex) {
			logger.error(ex.getMessage());
			typeLocalName = "Фиктивное поле";
		}
		return typeLocalName;
	}

	protected void generateControlParam(final XMLStreamWriter xmlWriter, final NodeRef paramNode) throws XMLStreamException {
		xmlWriter.writeStartElement("param");
		Map<QName, Serializable> props = nodeService.getProperties(paramNode);
		String id = (String)props.get(PROP_PARAM_ID);
		String localName = (String)props.get(ContentModel.PROP_TITLE);
		String mandatory = Boolean.toString((Boolean)props.get(PROP_PARAM_MANDATORY));
		String visible = Boolean.toString((Boolean)props.get(PROP_PARAM_VISIBLE));
		String description = (String)props.get(ContentModel.PROP_DESCRIPTION);
		String value = (String)props.get(PROP_PARAM_VALUE);
		if (StringUtils.isNotBlank(id)) {
			xmlWriter.writeAttribute("id", id);
		}
		if (StringUtils.isNotBlank(localName)) {
			xmlWriter.writeAttribute("localName", localName);
		}
		if (StringUtils.isNotBlank(mandatory)) {
			xmlWriter.writeAttribute("mandatory", mandatory);
		}
		if (StringUtils.isNotBlank(visible)) {
			xmlWriter.writeAttribute("visible", visible);
		}
		if (StringUtils.isNotBlank(description)) {
			xmlWriter.writeAttribute("description", description);
		}
		xmlWriter.writeCharacters(value);
		xmlWriter.writeEndElement();
	}

	protected void generateControl(final XMLStreamWriter xmlWriter, final NodeRef controlNode) throws XMLStreamException {
		xmlWriter.writeStartElement("control");
		Map<QName, Serializable> props = nodeService.getProperties(controlNode);
		String id = (String)props.get(PROP_CONTROL_ID);
		String template = (String)props.get(PROP_CONTROL_TEMPLATE);
		String localName = (String)props.get(ContentModel.PROP_TITLE);
		String isDefault = Boolean.toString((Boolean)props.get(PROP_CONTROL_DEFAULT));

		if (StringUtils.isNotBlank(id)) {
			xmlWriter.writeAttribute("id", id);
		}
		if (StringUtils.isNotBlank(template)) {
			xmlWriter.writeAttribute("template", template);
		}
		if (StringUtils.isNotBlank(localName)) {
			xmlWriter.writeAttribute("localName", localName);
		}
		if (StringUtils.isNotBlank(isDefault)) {
			xmlWriter.writeAttribute("default", isDefault);
		}

		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(controlNode);
		for (ChildAssociationRef childAssoc : childAssocs) {
			generateControlParam(xmlWriter, childAssoc.getChildRef());
		}
		xmlWriter.writeEndElement();
	}

	protected String generateControlsXML(final String typename) throws XMLStreamException {
		StringWriter stringWriter = new StringWriter();
		XMLStreamWriter xmlWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(stringWriter);
		xmlWriter.writeStartElement("alfresco-config");
		xmlWriter.writeStartElement("config");
		xmlWriter.writeStartElement("field-types");
		xmlWriter.writeStartElement("field-type");
		xmlWriter.writeAttribute("id", typename);
		xmlWriter.writeAttribute("localName", getTypeLocalName(typename));

		NodeRef controlsRoot = getTypeRootFolder(typename);
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(controlsRoot);
		for (ChildAssociationRef childAssoc : childAssocs) {
			generateControl(xmlWriter, childAssoc.getChildRef());
		}
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.writeEndElement();
		xmlWriter.close();
		String result = stringWriter.toString();
		IOUtils.closeQuietly(stringWriter);
		return result;
	}

	protected void writeControlsXML(final NodeRef configNode, final String typename) {
		ContentWriter writer = contentService.getWriter(configNode, ContentModel.PROP_CONTENT, true);

		if (writer != null) {
			writer.setEncoding("UTF-8");
			writer.setMimetype(MimetypeMap.MIMETYPE_XML);
			String content;
			try {
				content = generateControlsXML(typename);
			} catch(XMLStreamException ex) {
				content = "";
				logger.warn("Can't generate controls.xml for {}. Caused by {}", typename, ex.getMessage());
			}
			writer.putContent(content);
		}
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	public void setCociService(CheckOutCheckInService cociService) {
		this.cociService = cociService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(CONTROLS_EDITOR_ROOT_ID);
	}

	public NodeRef getTypeRootFolder(final String typename) {
		String folderName = typename.replace(":", "_");
		NodeRef parent = getServiceRootFolder();
		return getFolder(parent, folderName);
	}

	public NodeRef createTypeRootFolder(final String typename) throws WriteTransactionNeededException {
		String folderName = typename.replace(":", "_");
		NodeRef parent = getServiceRootFolder();
		return createFolder(parent, folderName);
	}

	/*
        contract.ensureVersioningEnabled(true, false);
        var versionedContract = contract.checkout();
        versionedContract.properties.content.write(taskComment.properties.content);
        versionedContract.checkin();
	*/
	public boolean generateControls(final String typename) {

		NodeRef configNode = geTypeControlsNode(typename);
		String filename = getTypeControlsFilename(typename);
		if (configNode == null) {
			try {
				configNode = createNode(getDeploymentFolder(), ContentModel.TYPE_CONTENT, filename, null);
			} catch (WriteTransactionNeededException ex) {
				logger.warn("Can't create folder {}. Caused by: {}", filename, ex.getMessage());
				throw new AlfrescoRuntimeException("", ex);
			}
		}

		PropertyMap vProps = new PropertyMap();
		vProps.put(ContentModel.PROP_AUTO_VERSION, true);
		vProps.put(ContentModel.PROP_AUTO_VERSION_PROPS, false);
		versionService.ensureVersioningEnabled(configNode, vProps);
		NodeRef workingCopyConfigNode = cociService.checkout(configNode);
		writeControlsXML(workingCopyConfigNode, typename);
		Map<String, Serializable> ciProps = new HashMap<>();
		ciProps.put(Version.PROP_DESCRIPTION, "");
		ciProps.put(VersionModel.PROP_VERSION_TYPE, VersionType.MINOR);
		cociService.checkin(workingCopyConfigNode, ciProps);
		return true;
	}
	
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		lecmTransactionHelper.doInRWTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
			@Override
			public Void execute() throws Throwable {
				return AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
					@Override
					public Void doWork() throws Exception {
						getServiceRootFolder();
						return null;
					}
				});
			}
		});
	}
}
