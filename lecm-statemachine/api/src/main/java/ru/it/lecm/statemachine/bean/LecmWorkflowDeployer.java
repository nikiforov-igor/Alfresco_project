package ru.it.lecm.statemachine.bean;

import org.alfresco.service.cmr.workflow.WorkflowDeployment;

import java.io.InputStream;
import java.io.IOException;

/**
 * User: pmelnikov
 * Date: 10.10.13
 * Time: 9:16
 */
public interface LecmWorkflowDeployer {

    public static final String WORKFLOW_FOLDER = "workflowStore";

    public void redeploy();

    public WorkflowDeployment deploy(String engineId, String mimetype, InputStream inputStream, String filename) throws IOException;
}
