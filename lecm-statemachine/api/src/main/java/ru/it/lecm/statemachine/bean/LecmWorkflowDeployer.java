package ru.it.lecm.statemachine.bean;

/**
 * User: pmelnikov
 * Date: 10.10.13
 * Time: 9:16
 */
public interface LecmWorkflowDeployer {

    public static final String WORKFLOW_FOLDER = "workflowStore";

    public void redeploy();

}
