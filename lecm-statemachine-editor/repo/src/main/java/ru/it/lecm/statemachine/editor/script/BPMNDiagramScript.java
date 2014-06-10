package ru.it.lecm.statemachine.editor.script;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.statemachine.bean.LecmWorkflowDeployer;
import ru.it.lecm.statemachine.editor.StatemachineEditorModel;
import ru.it.lecm.statemachine.editor.export.XMLExporter;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.runtime.ProcessInstance;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 * User: PMelnikov
 * Date: 29.11.12
 * Time: 16:43
 */
public class BPMNDiagramScript extends AbstractWebScript {
	private static final transient Logger logger = LoggerFactory.getLogger(BPMNDiagramScript.class);

	private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;
	private NodeService nodeService;
	private LecmWorkflowDeployer lecmWorkflowDeployer;
	private Repository repositoryHelper;
	private ContentService contentService;
	private FileFolderService fileFolderService;
    private LecmBasePropertiesService propertiesService;
	private ProcessEngine activitiProcessEngine;
	private StateMachineServiceBean statemachineService;

	public void setStatemachineService(StateMachineServiceBean statemachineService) {
		this.statemachineService = statemachineService;
	}



	public void setActivitiProcessEngine(ProcessEngine activitiProcessEngine) {
		this.activitiProcessEngine = activitiProcessEngine;
	}

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
		this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
	}

	public void setLecmWorkflowDeployer(LecmWorkflowDeployer lecmWorkflowDeployer) {
		this.lecmWorkflowDeployer = lecmWorkflowDeployer;
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    @Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String statemachineNodeRef = req.getParameter("statemachineNodeRef");
		String docNodeRef = req.getParameter("docNodeRef");
		String type = req.getParameter("type");
		if (statemachineNodeRef != null && "deploy".equals(type)) {
            try {
                Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.statemachine.editor.enabled");
                boolean enabled;
                if (editorEnabled == null) {
                    enabled = true;
                } else {
                    enabled = Boolean.valueOf((String) editorEnabled);
                }
                if (enabled) {
					NodeRef statemachine = new NodeRef(statemachineNodeRef);
					statemachine = nodeService.getPrimaryParent(statemachine).getParentRef();
		            String machineName = nodeService.getProperty(statemachine, ContentModel.PROP_NAME).toString();
		            logger.debug("Start generate diagram and deploy it for statemachine " + machineName);

		            //Создаем результирующую диаграмму
		            String fileName = machineName + ".bpmn20.xml";
					NodeRef companyHome = repositoryHelper.getCompanyHome();
					NodeRef workflowFolder = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, LecmWorkflowDeployer.WORKFLOW_FOLDER);
					NodeRef file = nodeService.getChildByName(workflowFolder, ContentModel.ASSOC_CONTAINS, fileName);
					if (file == null) {
						HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
						props.put(ContentModel.PROP_NAME, fileName);
						ChildAssociationRef childAssocRef = nodeService.createNode(
								workflowFolder,
								ContentModel.ASSOC_CONTAINS,
								QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(fileName)),
								ContentModel.TYPE_CONTENT,
								props);
						file = childAssocRef.getChildRef();
					}
					ContentWriter writer = contentService.getWriter(file, ContentModel.PROP_CONTENT, true);
					writer.setMimetype("text/xml");
					ByteArrayInputStream is = (ByteArrayInputStream) new BPMNGenerator(statemachineNodeRef, nodeService).generate();
					writer.putContent(is);
					is.close();

		            logger.debug("Diagram is generated. Create version for deployment.");

		            //Создаем версию
		            NodeRef statemachines = nodeService.getPrimaryParent(statemachine).getParentRef();
		            NodeRef versions = nodeService.getChildByName(statemachines, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.FOLDER_VERSIONS);
		            NodeRef statemachineVersions = nodeService.getChildByName(versions, ContentModel.ASSOC_CONTAINS, machineName);
		            Long lastVersion = (Long) nodeService.getProperty(statemachineVersions, StatemachineEditorModel.PROP_LAST_VERSION);
		            lastVersion++;

		            HashMap<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
		            props.put(ContentModel.PROP_NAME, "version_" + lastVersion);
		            props.put(StatemachineEditorModel.PROP_VERSION, lastVersion);
		            props.put(StatemachineEditorModel.PROP_PUBLISH_DATE, new Date());
		            if (req.getParameter("comment") != null) {
		                props.put(StatemachineEditorModel.PROP_PUBLISH_COMMENT, req.getParameter("comment"));
		            }

		            ChildAssociationRef childAssocRef = nodeService.createNode(
		                    statemachineVersions,
		                    ContentModel.ASSOC_CONTAINS,
		                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("version_" + lastVersion)),
		                    StatemachineEditorModel.TYPE_VERSION,
		                    props);
		            NodeRef version = childAssocRef.getChildRef();

		            //Добавляем в версию файл импорта
		            ByteArrayOutputStream backupOut = new ByteArrayOutputStream();
		            try {
		                XMLExporter exporter = new XMLExporter(backupOut, nodeService);
		                exporter.write(statemachineNodeRef);
		            } catch (XMLStreamException e) {
			            logger.error(e.getMessage(), e);
		            }

		            props = new HashMap<QName, Serializable>(1, 1.0f);
		            props.put(ContentModel.PROP_NAME, "backup.xml");

		            childAssocRef = nodeService.createNode(
		                    version,
		                    ContentModel.ASSOC_CONTAINS,
		                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("backup.xml")),
		                    ContentModel.TYPE_CONTENT,
		                    props);
		            NodeRef backupFile = childAssocRef.getChildRef();
		            writer = contentService.getWriter(backupFile, ContentModel.PROP_CONTENT, true);
		            writer.setMimetype("text/xml");
		            is = new ByteArrayInputStream(backupOut.toByteArray());
		            writer.putContent(is);
		            is.close();

		            try {
		                fileFolderService.copy(file, version, fileName);
		            } catch (org.alfresco.service.cmr.model.FileNotFoundException e) {
		                logger.error("Cannot copy file", e);
		            }

		            //Сохраняем свойсвтва контейнера версий
		            nodeService.setProperty(statemachineVersions, StatemachineEditorModel.PROP_LAST_VERSION, lastVersion);

		            //Публикуем машину состояний
		            logger.debug("Deploy process");
		            lecmWorkflowDeployer.redeploy();
		            logger.debug("Process is deployed");
                }
            } catch (LecmBaseException e) {
                logger.error("Error while deployed statemachine");
            }
		} else if (statemachineNodeRef != null && "diagram".equals(type)) {
			res.setContentType("image/png");
			// Create an XML stream writer
			OutputStream output = res.getOutputStream();
			InputStream bpmn = new BPMNGenerator(statemachineNodeRef, nodeService).generate();
			InputStream is = new BPMNGraphGenerator().generate(bpmn);
            byte[] buf = new byte[8 * 1024];
			int c;
            int len = 0;
			while ((c = is.read(buf)) != -1) {
				output.write(buf, 0, c);
                len += c;
			}
            res.setHeader("Content-length", "" + len);
			output.flush();
			output.close();
			is.close();
		} else if (docNodeRef != null && "current".equals(type)) {
			res.setContentType("image/png");
			// Create an XML stream writer
			OutputStream output = res.getOutputStream();
			InputStream is = currentStatusDiagram(new NodeRef(docNodeRef));
            byte[] buf = new byte[8 * 1024];
			int c;
            int len = 0;
			while ((c = is.read(buf)) != -1) {
				output.write(buf, 0, c);
                len += c;
			}
            res.setHeader("Content-length", "" + len);
			output.flush();
			output.close();
			is.close();
		}
	}

	public InputStream currentStatusDiagram(NodeRef docRef) {

		String currentStatus = nodeService.getProperty(docRef, StatemachineModel.PROP_STATUS).toString();
		String processId = nodeService.getProperty(docRef, StatemachineModel.PROP_STATEMACHINE_ID).toString();
		List<String> history = statemachineService.getPreviousStatusesNames(docRef);
		ProcessInstance processInstance = activitiProcessEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processId.split("\\$")[1])
                .singleResult();

		if (processInstance == null)
		{
			throw new RuntimeException("Entity not found for " + processId);
		}

		BpmnModel model = activitiProcessEngine.getRepositoryService().getBpmnModel(processInstance.getProcessDefinitionId());
		return new BPMNGraphGenerator().generateByModel(model, currentStatus, history);
	}
}
