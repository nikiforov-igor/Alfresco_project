package ru.it.lecm.statemachine.editor.script;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.workflow.WorkflowDeployment;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.statemachine.StateMachineServiceBean;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.statemachine.editor.SimpleDocumentDeployer;
import ru.it.lecm.statemachine.editor.StatemachineEditorModel;
import ru.it.lecm.statemachine.editor.export.XMLExporter;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 29.11.12
 * Time: 16:43
 */
public class BPMNDiagramScript extends AbstractWebScript {
	private static final transient Logger logger = LoggerFactory.getLogger(BPMNDiagramScript.class);

	private NodeService nodeService;
	private Repository repositoryHelper;
	private ContentService contentService;
	private FileFolderService fileFolderService;
	private RepositoryStructureHelper repositoryStructureHelper;
    private LecmBasePropertiesService propertiesService;
	private ProcessEngine activitiProcessEngine;
	private StateMachineServiceBean statemachineService;
	private SimpleDocumentDeployer simpleDocumentDeployer;
	private WorkflowService workflowService;

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

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;

	}

	public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
		this.repositoryStructureHelper = repositoryStructureHelper;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
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
		            logger.debug("Генерация процесса для машины состояний " + machineName);

					String fileName = machineName + ".bpmn20.xml";

					//Рвзворачиваем и создаем версию
					NodeRef statemachineVersions = checkVersions(statemachine);

					NodeRef file = checkWorkflowStore(fileName);
					ContentWriter writer = contentService.getWriter(file, ContentModel.PROP_CONTENT, true);
					writer.setMimetype("text/xml");
					ByteArrayInputStream bpmnIS = (ByteArrayInputStream) new BPMNGenerator(statemachineNodeRef, nodeService).generate();
					writer.putContent(bpmnIS);
					bpmnIS.close();

					Boolean isSimpleDocument = false;
					Object isSimpleDocumentObj = nodeService.getProperty(statemachine, StatemachineEditorModel.PROP_SIMPLE_DOCUMENT);
					if (isSimpleDocumentObj != null) {
						isSimpleDocument = (Boolean) isSimpleDocumentObj;
					}
					String lastVersion;
					if (!isSimpleDocument) {
						logger.debug("Деплой процесса для " + machineName);
						bpmnIS = (ByteArrayInputStream) new BPMNGenerator(statemachineNodeRef, nodeService).generate();
						WorkflowDeployment wd = workflowService.deployDefinition("activiti", bpmnIS, "text/xml", fileName);
						lastVersion = wd.getDefinition().getVersion();
						bpmnIS.close();
						//Сохраняем свойсвтва контейнера версий
						nodeService.setProperty(statemachineVersions, StatemachineEditorModel.PROP_LAST_VERSION, lastVersion);
					} else {
						simpleDocumentDeployer.appendType(statemachine);
						Object lastVersionProp = nodeService.getProperty(statemachineVersions, StatemachineEditorModel.PROP_SIMPLE_DOCUMENT_LAST_VERSION);
						long newVersion = 0;
						if (lastVersionProp != null) {
							try {
								newVersion = Long.valueOf(lastVersionProp.toString());
							} catch (NumberFormatException e) {}
						}
						newVersion++;
						lastVersion = Long.toString(newVersion);
						nodeService.setProperty(statemachineVersions, StatemachineEditorModel.PROP_SIMPLE_DOCUMENT_LAST_VERSION, lastVersion);
					}

					String versionFolderName = "version_" + (isSimpleDocument ? "NA_" + lastVersion : lastVersion);
		            NodeRef version = nodeService.getChildByName(statemachineVersions, ContentModel.ASSOC_CONTAINS, versionFolderName);
					if (version == null) {
						Map<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
			            props.put(ContentModel.PROP_NAME, versionFolderName);
			            props.put(StatemachineEditorModel.PROP_VERSION, lastVersion);
			            props.put(StatemachineEditorModel.PROP_VERSION_IS_SIMPLE_DOCUMENT, isSimpleDocument);
			            props.put(StatemachineEditorModel.PROP_PUBLISH_DATE, new Date());
			            if (req.getParameter("comment") != null) {
			                props.put(StatemachineEditorModel.PROP_PUBLISH_COMMENT, req.getParameter("comment"));
			            }
						ChildAssociationRef childAssocRef = nodeService.createNode(
		                    statemachineVersions,
		                    ContentModel.ASSOC_CONTAINS,
		                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(versionFolderName)),
		                    StatemachineEditorModel.TYPE_VERSION,
		                    props);
		            	version = childAssocRef.getChildRef();
					} else {
						Map<QName, Serializable> props = nodeService.getProperties(version);
						props.put(StatemachineEditorModel.PROP_VERSION, lastVersion);
						props.put(StatemachineEditorModel.PROP_VERSION_IS_SIMPLE_DOCUMENT, isSimpleDocument);
			            props.put(StatemachineEditorModel.PROP_PUBLISH_DATE, new Date());
			            if (req.getParameter("comment") != null) {
			                props.put(StatemachineEditorModel.PROP_PUBLISH_COMMENT, req.getParameter("comment"));
			            }
						nodeService.setProperties(version, props);
					}
		        	//добавляем бэкап в контейнер версии
					createVersionBackup(statemachineNodeRef,version);


		            try {
		            	NodeRef processFile = nodeService.getChildByName(version, ContentModel.ASSOC_CONTAINS, fileName);
			            if(processFile!=null) {
			            	fileFolderService.rename(processFile, "old_"+fileName);
			            }
		                fileFolderService.copy(file, version, fileName);
		            } catch (org.alfresco.service.cmr.model.FileNotFoundException e) {
		                logger.error("Не удалось скопировать файл", e);
		            } catch (org.alfresco.service.cmr.model.FileExistsException e) {
		            	logger.error("Не удалось скопировать файл", e);
		            }

		            statemachineService.resetStateMachene();
		            
		            logger.debug("Машина состояний развернута");
                }
            } catch (LecmBaseException e) {
                logger.error("Не удалось развернуть машину состояний");
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
		String processId = nodeService.getProperty(docRef, StatemachineModel.PROP_STATEMACHINE_ID).toString();
		List<String> history = statemachineService.getPreviousStatusesNames(docRef);
		ProcessInstance processInstance = activitiProcessEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processId.split("\\$")[1])
                .singleResult();

        String definitionId;
		if (processInstance == null) {
            HistoricProcessInstance historicProcessInstance = activitiProcessEngine.getHistoryService().createHistoricProcessInstanceQuery()
                    .processInstanceId(processId.split("\\$")[1])
                    .singleResult();
            if (historicProcessInstance == null) {
			    throw new RuntimeException("Entity not found for " + processId);
            } else {
                definitionId = historicProcessInstance.getProcessDefinitionId();
            }
		} else {
            definitionId = processInstance.getProcessDefinitionId();
        }

		BpmnModel model = activitiProcessEngine.getRepositoryService().getBpmnModel(definitionId);
		return new BPMNGraphGenerator().generateByModel(model, history);
	}

	/**
	 * Возвращает ноду версий МС, при необходомисоти создает
	 * @param stateMachine
	 * @return NodeRef stateMachineVersion
	 */
	private NodeRef checkVersions(NodeRef stateMachine) {
		NodeRef stateMachinesRoot = nodeService.getPrimaryParent(stateMachine).getParentRef();
		String stateMachineId = nodeService.getProperty(stateMachine, ContentModel.PROP_NAME).toString();
		//проверяем ноду версий
		NodeRef versionsFolder = nodeService.getChildByName(stateMachinesRoot, ContentModel.ASSOC_CONTAINS, StatemachineEditorModel.FOLDER_VERSIONS);
		if (versionsFolder == null) {
			try {
				versionsFolder = repositoryStructureHelper.createFolder(stateMachinesRoot, StatemachineEditorModel.FOLDER_VERSIONS);
			} catch (WriteTransactionNeededException e) {
				e.printStackTrace();
			}
		}
		NodeRef stateMachineVersionsNodeRef = nodeService.getChildByName(versionsFolder, ContentModel.ASSOC_CONTAINS, stateMachineId);
		if (stateMachineVersionsNodeRef == null) {
			Map<QName, Serializable> properties = new HashMap<>();
			properties.put(ContentModel.PROP_NAME, stateMachineId);
			properties.put(StatemachineEditorModel.PROP_LAST_VERSION, 0);
			ChildAssociationRef stateMachineVersionsChildAssoc = nodeService.createNode(versionsFolder, ContentModel.ASSOC_CONTAINS,
					QName.createQName(StatemachineEditorModel.STATEMACHINE_EDITOR_URI, stateMachineId), StatemachineEditorModel.TYPE_VERSIONS, properties);
			stateMachineVersionsNodeRef = stateMachineVersionsChildAssoc.getChildRef();
		}

		return stateMachineVersionsNodeRef;
	}

	private NodeRef checkWorkflowStore(String fileName) throws IOException {
		NodeRef companyHome = repositoryHelper.getCompanyHome();
		NodeRef workflowFolder = nodeService.getChildByName(companyHome, ContentModel.ASSOC_CONTAINS, "workflowStore");
		NodeRef file = nodeService.getChildByName(workflowFolder, ContentModel.ASSOC_CONTAINS, fileName);
		if (file == null) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
			props.put(ContentModel.PROP_NAME, fileName);
			ChildAssociationRef childAssocRef = nodeService.createNode(
					workflowFolder,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(fileName)),
					ContentModel.TYPE_CONTENT,
					props);
			file = childAssocRef.getChildRef();
		}
		return file;
	}

	private void createVersionBackup(String statemachineNodeRef, NodeRef version) throws IOException {
		//Добавляем в версию файл импорта
		ByteArrayOutputStream backupOut = new ByteArrayOutputStream();
		try {
			XMLExporter exporter = new XMLExporter(backupOut, nodeService);
			exporter.write(statemachineNodeRef);
		} catch (XMLStreamException e) {
			logger.error(e.getMessage(), e);
		}
		NodeRef backupFile = nodeService.getChildByName(version, ContentModel.ASSOC_CONTAINS, "backup.xml");
		if (backupFile == null) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>(1, 1.0f);
			props.put(ContentModel.PROP_NAME, "backup.xml");

			ChildAssociationRef childAssocRef = nodeService.createNode(
					version,
					ContentModel.ASSOC_CONTAINS,
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName("backup.xml")),
					ContentModel.TYPE_CONTENT,
					props);
			backupFile = childAssocRef.getChildRef();
		}
		ContentWriter writer = contentService.getWriter(backupFile, ContentModel.PROP_CONTENT, true);
		writer.setMimetype("text/xml");
		ByteArrayInputStream is = new ByteArrayInputStream(backupOut.toByteArray());
		writer.putContent(is);
		is.close();
	}

	public void setSimpleDocumentDeployer(SimpleDocumentDeployer simpleDocumentDeployer) {
		this.simpleDocumentDeployer = simpleDocumentDeployer;
	}

}
