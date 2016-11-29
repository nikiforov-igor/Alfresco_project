package ru.it.lecm.base.scripts;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.security.authority.AuthorityDAO;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.web.scripts.workflow.TaskInstancesGet;
import org.alfresco.repo.web.scripts.workflow.WorkflowModelBuilder;
import org.alfresco.repo.workflow.*;
import org.alfresco.repo.workflow.activiti.ActivitiNodeConverter;
import org.alfresco.repo.workflow.activiti.ActivitiTypeConverter;
import org.alfresco.repo.workflow.activiti.ActivitiUtil;
import org.alfresco.repo.workflow.activiti.properties.ActivitiPropertyConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ModelUtil;
import org.alfresco.util.collections.Function;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by pmelnikov on 14.11.2016.
 */
public class LecmTaskInstancesGet extends TaskInstancesGet {

    private WorkflowTaskDueAscComparator taskComparator = new WorkflowTaskDueAscComparator();
    private ProcessEngine processEngine;

    // Set fields
    private TenantService tenantService;
    private MessageService messageService;
    private ServiceRegistry serviceRegistry;
    private AuthorityDAO authorityDAO;

    private String engineId;
    private boolean deployWorkflowsInTenant;

    private ActivitiTypeConverter typeConverter = null;

    private int limit = 200;

    @Override
    protected Map<String, Object> buildModel(WorkflowModelBuilder modelBuilder, WebScriptRequest req, Status status, Cache cache) {
        Map<String, String> params = req.getServiceMatch().getTemplateVars();
        Map<String, Object> filters = new HashMap<>(4);

        // authority is not included into filters list as it will be taken into account before filtering
        String authority = getAuthority(req);

        if (authority == null) {
            // ALF-11036 fix, if authority argument is omitted the tasks for the current user should be returned.
            authority = authenticationService.getCurrentUserName();
        }

        // state is also not included into filters list, for the same reason
        WorkflowTaskState state = getState(req);

        // look for a workflow instance id
        String workflowInstanceId = params.get(VAR_WORKFLOW_INSTANCE_ID);

        // determine if pooledTasks should be included, when appropriate i.e. when an authority is supplied
        Boolean pooledTasksOnly = getPooledTasks(req);

        // get list of properties to include in the response
        List<String> properties = getProperties(req);

        // get filter param values
        filters.put(PARAM_PRIORITY, req.getParameter(PARAM_PRIORITY));
        processDateFilter(req, PARAM_DUE_BEFORE, filters);
        processDateFilter(req, PARAM_DUE_AFTER, filters);

        String excludeParam = req.getParameter(PARAM_EXCLUDE);
        if (excludeParam != null && excludeParam.length() > 0) {
            filters.put(PARAM_EXCLUDE, new ExcludeFilter(excludeParam));
        }

        List<WorkflowTask> allTasks;

        if (workflowInstanceId != null) {
            // a workflow instance id was provided so query for tasks
            WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
            taskQuery.setActive(null);
            taskQuery.setProcessId(workflowInstanceId);
            taskQuery.setTaskState(state);
            taskQuery.setOrderBy(new WorkflowTaskQuery.OrderBy[]{WorkflowTaskQuery.OrderBy.TaskDue_Asc});

            if (authority != null) {
                taskQuery.setActorId(authority);
            }

            allTasks = workflowService.queryTasks(taskQuery, false);
        } else {
            // default task state to IN_PROGRESS if not supplied
            if (state == null) {
                state = WorkflowTaskState.IN_PROGRESS;
            }

            // no workflow instance id is present so get all tasks
            if (authority != null) {
                //ALF-6215
                if (state == WorkflowTaskState.COMPLETED) {
                    HistoricTaskInstanceQuery historicQuery = processEngine.getHistoryService().createHistoricTaskInstanceQuery().finished();
                    historicQuery.taskAssignee(authority);
                    historicQuery.taskVariableValueNotEquals("taskFormKey", "lecm-statemachine:startTask");
                    historicQuery.orderByHistoricTaskInstanceEndTime().desc();
                    List<HistoricTaskInstance> tasks = historicQuery.listPage(0, limit);
                    ActivitiTypeConverter typeConverter = getActivitiTypeConverter();
                    allTasks = typeConverter.filterByDomainAndConvert(tasks, new Function<HistoricTaskInstance, String>() {
                        public String apply(HistoricTaskInstance task) {
                            String defId = task.getProcessDefinitionId();
                            ProcessDefinition definition = (ProcessDefinition) ((RepositoryServiceImpl) processEngine.getRepositoryService()).getDeployedProcessDefinition(defId);
                            return definition.getKey();
                        }
                    });
                //ALF-6215
                } else {
                    List<WorkflowTask> tasks = workflowService.getAssignedTasks(authority, state, true);
                    List<WorkflowTask> pooledTasks = workflowService.getPooledTasks(authority, true);
                    if (pooledTasksOnly != null) {
                        if (pooledTasksOnly) {
                            // only return pooled tasks the user can claim
                            allTasks = new ArrayList<>(pooledTasks.size());
                            allTasks.addAll(pooledTasks);
                        } else {
                            // only return tasks assigned to the user
                            allTasks = new ArrayList<>(tasks.size());
                            allTasks.addAll(tasks);
                        }
                    } else {
                        // include both assigned and unassigned tasks
                        allTasks = new ArrayList<>(tasks.size() + pooledTasks.size());
                        allTasks.addAll(tasks);
                        allTasks.addAll(pooledTasks);
                    }

                    // sort tasks by due date
                    Collections.sort(allTasks, taskComparator);
                }
            } else {
                // authority was not provided -> return all active tasks in the system
                WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
                taskQuery.setTaskState(state);
                taskQuery.setActive(null);
                taskQuery.setOrderBy(new WorkflowTaskQuery.OrderBy[]{WorkflowTaskQuery.OrderBy.TaskDue_Asc});
                allTasks = workflowService.queryTasks(taskQuery, false);
            }
        }

        int maxItems = getIntParameter(req, PARAM_MAX_ITEMS, DEFAULT_MAX_ITEMS);
        int skipCount = getIntParameter(req, PARAM_SKIP_COUNT, DEFAULT_SKIP_COUNT);
        int totalCount = 0;
        ArrayList<Map<String, Object>> results = new ArrayList<>();

        // Filter results
        WorkflowTask task;
        for (WorkflowTask allTask : allTasks) {
            task = allTask;
            if (matches(task, filters)) {
                // Total-count needs to be based on matching tasks only, so we can't just use allTasks.size() for this
                totalCount++;
                if (totalCount > skipCount && (maxItems < 0 || maxItems > results.size())) {
                    // Only build the actual detail if it's in the range of items we need. This will
                    // drastically improve performance over paging after building the model
                    results.add(modelBuilder.buildSimple(task, properties));
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("taskInstances", results);

        if (maxItems != DEFAULT_MAX_ITEMS || skipCount != DEFAULT_SKIP_COUNT) {
            // maxItems or skipCount parameter was provided so we need to include paging into response
            model.put("paging", ModelUtil.buildPaging(totalCount, maxItems == DEFAULT_MAX_ITEMS ? totalCount : maxItems, skipCount));
        }

        // create and return results, paginated if necessary
        return model;
    }

    private ActivitiTypeConverter getActivitiTypeConverter() {
        if (this.typeConverter == null) {
            ActivitiNodeConverter nodeConverter = new ActivitiNodeConverter(serviceRegistry);
            DefaultWorkflowPropertyHandler defaultPropertyHandler = new DefaultWorkflowPropertyHandler();
            defaultPropertyHandler.setMessageService(messageService);
            defaultPropertyHandler.setNodeConverter(nodeConverter);

            WorkflowQNameConverter qNameConverter = new WorkflowQNameConverter(namespaceService);
            WorkflowPropertyHandlerRegistry handlerRegistry = new WorkflowPropertyHandlerRegistry(defaultPropertyHandler, qNameConverter);

            WorkflowAuthorityManager authorityManager = new WorkflowAuthorityManager(authorityDAO);
            QName defaultStartTaskType = WorkflowModel.TYPE_ACTIVTI_START_TASK;
            WorkflowObjectFactory factory = new WorkflowObjectFactory(qNameConverter, tenantService, messageService, dictionaryService, engineId, defaultStartTaskType);
            ActivitiUtil activitiUtil = new ActivitiUtil(processEngine, deployWorkflowsInTenant);
            ActivitiPropertyConverter propertyConverter = new ActivitiPropertyConverter(activitiUtil, factory, handlerRegistry, authorityManager, messageService, nodeConverter);
            this.typeConverter = new ActivitiTypeConverter(processEngine, factory, propertyConverter, deployWorkflowsInTenant);
        }
        return typeConverter;
    }

    private String getAuthority(WebScriptRequest req) {
        String authority = req.getParameter(PARAM_AUTHORITY);
        if (authority == null || authority.length() == 0) {
            authority = null;
        }
        return authority;
    }

    private WorkflowTaskState getState(WebScriptRequest req) {
        String stateName = req.getParameter(PARAM_STATE);
        if (stateName != null) {
            try {
                return WorkflowTaskState.valueOf(stateName.toUpperCase());
            } catch (IllegalArgumentException e) {
                String msg = "Unrecognised State parameter: " + stateName;
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, msg);
            }
        }

        return null;
    }

    private Boolean getPooledTasks(WebScriptRequest req) {
        Boolean result = null;
        String includePooledTasks = req.getParameter(PARAM_POOLED_TASKS);

        if (includePooledTasks != null) {
            result = Boolean.valueOf(includePooledTasks);
        }

        return result;
    }

    private List<String> getProperties(WebScriptRequest req) {
        String propertiesStr = req.getParameter(PARAM_PROPERTIES);
        if (propertiesStr != null) {
            return Arrays.asList(propertiesStr.split(","));
        }
        return null;
    }

    private boolean matches(WorkflowTask task, Map<String, Object> filters) {
        // by default we assume that workflow task should be included
        boolean result = true;

        for (String key : filters.keySet()) {
            Object filterValue = filters.get(key);

            // skip null filters (null value means that filter was not specified)
            if (filterValue != null) {
                if (key.equals(PARAM_EXCLUDE)) {
                    ExcludeFilter excludeFilter = (ExcludeFilter) filterValue;
                    String type = task.getDefinition().getMetadata().getName().toPrefixString(this.namespaceService);
                    if (excludeFilter.isMatch(type)) {
                        result = false;
                        break;
                    }
                } else if (key.equals(PARAM_DUE_BEFORE)) {
                    Date dueDate = (Date) task.getProperties().get(WorkflowModel.PROP_DUE_DATE);

                    if (!isDateMatchForFilter(dueDate, filterValue, true)) {
                        result = false;
                        break;
                    }
                } else if (key.equals(PARAM_DUE_AFTER)) {
                    Date dueDate = (Date) task.getProperties().get(WorkflowModel.PROP_DUE_DATE);

                    if (!isDateMatchForFilter(dueDate, filterValue, false)) {
                        result = false;
                        break;
                    }
                } else if (key.equals(PARAM_PRIORITY)) {
                    if (!filterValue.equals(task.getProperties().get(WorkflowModel.PROP_PRIORITY).toString())) {
                        result = false;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setDeployWorkflowsInTenant(boolean deployWorkflowsInTenant) {
        this.deployWorkflowsInTenant = deployWorkflowsInTenant;
    }

    public void setAuthorityDAO(AuthorityDAO authorityDAO) {
        this.authorityDAO = authorityDAO;
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public void setLimit(String limit) {
        try {
            this.limit = Integer.parseInt(limit);
        } catch (NumberFormatException e) {
        }
    }

    class WorkflowTaskDueAscComparator implements Comparator<WorkflowTask> {
        @Override
        public int compare(WorkflowTask o1, WorkflowTask o2) {
            Date date1 = (Date) o1.getProperties().get(WorkflowModel.PROP_DUE_DATE);
            Date date2 = (Date) o2.getProperties().get(WorkflowModel.PROP_DUE_DATE);

            long time1 = date1 == null ? Long.MAX_VALUE : date1.getTime();
            long time2 = date2 == null ? Long.MAX_VALUE : date2.getTime();

            long result = time1 - time2;

            return (result > 0) ? 1 : (result < 0 ? -1 : 0);
        }

    }

}
