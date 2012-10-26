package ru.it.lecm.base.statemachine;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.alfresco.repo.workflow.WorkflowDeployer;
import org.alfresco.repo.workflow.activiti.AlfrescoProcessEngineConfiguration;
import org.alfresco.service.cmr.workflow.WorkflowAdminService;
import org.alfresco.service.cmr.workflow.WorkflowException;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Properties;

/**
 * User: PMelnikov
 * Date: 26.10.12
 * Time: 11:51
 */
public class lecmWorkflowDeployer extends WorkflowDeployer {

    private WorkflowService workflowService;
    private WorkflowAdminService workflowAdminService;
    private AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration;

    @Override
    public void init() {
        try {
            Iterator<Properties> iterator = getWorkflowDefinitions().iterator();
            while (iterator.hasNext()) {
                Properties workflowDefinition = iterator.next();
                // retrieve workflow specification
                String engineId = workflowDefinition.getProperty(ENGINE_ID);
                if (engineId == null || engineId.length() == 0) {
                    throw new WorkflowException("Workflow Engine Id must be provided");
                }

                String location = workflowDefinition.getProperty(LOCATION);
                if (location == null || location.length() == 0) {
                    throw new WorkflowException("Workflow definition location must be provided");
                }

                if (workflowAdminService.isEngineEnabled(engineId)) {
                    String mimetype = workflowDefinition.getProperty(MIMETYPE);
                    // retrieve input stream on workflow definition
                    ClassPathResource workflowResource = new ClassPathResource(location);
                    // deploy workflow definition
                    if (workflowService.isDefinitionDeployed(engineId, workflowResource.getInputStream(), mimetype)) {
                        String key = getProcessKey(workflowResource.getInputStream());
                        RepositoryService repositoryService = activitiProcessEngineConfiguration.getRepositoryService();
                        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
                        if (definition != null) {
                            InputStream latestResource = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
                            String latestChecksum = getMD5Checksum(latestResource);
                            String newChecksum = getMD5Checksum(workflowResource.getInputStream());
                            if (latestChecksum.equals(newChecksum)) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.init();
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
        super.setWorkflowService(workflowService);
    }

    public void setWorkflowAdminService(WorkflowAdminService workflowAdminService) {
        this.workflowAdminService = workflowAdminService;
        super.setWorkflowAdminService(workflowAdminService);
    }

    public void setActivitiProcessEngineConfiguration(AlfrescoProcessEngineConfiguration activitiProcessEngineConfiguration) {
        this.activitiProcessEngineConfiguration = activitiProcessEngineConfiguration;
    }

    private String getProcessKey(InputStream workflowDefinition) throws Exception {
        try {
            InputSource inputSource = new InputSource(workflowDefinition);
            DOMParser parser = new DOMParser();
            parser.parse(inputSource);
            Document document = parser.getDocument();
            NodeList elemnts = document.getElementsByTagName("process");
            if (elemnts.getLength() < 1) {
                throw new IllegalArgumentException("The input stream does not contain a process definition!");
            }
            NamedNodeMap attributes = elemnts.item(0).getAttributes();
            Node idAttrib = attributes.getNamedItem("id");
            if (idAttrib == null) {
                throw new IllegalAccessError("The process definition does not have an id!");
            }
            return idAttrib.getNodeValue();
        } finally {
            workflowDefinition.close();
        }
    }

    private String getMD5Checksum(InputStream is) throws IOException {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            result = bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return result;
    }


}