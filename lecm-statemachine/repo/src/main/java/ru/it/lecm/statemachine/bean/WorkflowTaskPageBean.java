package ru.it.lecm.statemachine.bean;

import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.statemachine.WorkflowTaskBean;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 12.03.13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowTaskPageBean implements WorkflowTaskBean {

    private WorkflowTask workflowTask;
    private WorkflowTaskState workflowTaskState = WorkflowTaskState.WORKFLOWTASKSTATE_NA;

    public WorkflowTaskPageBean(WorkflowTask workflowTask) {
        if (workflowTask == null) {
            throw new IllegalArgumentException("WorkflowTask is null!");
        }

        this.workflowTask = workflowTask;
    }

    @Override
    public String getId() {
        return workflowTask.getId();
    }

    @Override
    public String getName() {
        return workflowTask.getName();
    }

    @Override
    public String getTitle() {
        return workflowTask.getTitle();
    }

    @Override
    public String getDescription() {
        return workflowTask.getDescription();
    }

    @Override
    public Date getStartDate() {
        return getProperties().containsKey(WorkflowModel.PROP_START_DATE) ? (Date) getProperties().get(WorkflowModel.PROP_START_DATE) : null;
    }

    @Override
    public Date getDueDate() {
        return getProperties().containsKey(WorkflowModel.PROP_DUE_DATE) ? (Date) getProperties().get(WorkflowModel.PROP_DUE_DATE) : null;
    }

    @Override
    public String getWorkflowTaskState() {
        return workflowTaskState.toString();
    }

    @Override
    public String getWorkflowTaskStateMessage() {
        return I18NUtil.getMessage(workflowTaskState.toString(), I18NUtil.getLocale());
    }

    @Override
    public Map<QName, Serializable> getProperties() {
        return workflowTask.getProperties();
    }
}