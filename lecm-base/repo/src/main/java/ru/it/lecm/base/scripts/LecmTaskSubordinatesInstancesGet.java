package ru.it.lecm.base.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.web.scripts.workflow.AbstractWorkflowWebscript;
import org.alfresco.repo.web.scripts.workflow.WorkflowModelBuilder;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

/**
 * User: AIvkin
 * Date: 30.04.13
 * Time: 12:49
 */
public class LecmTaskSubordinatesInstancesGet extends AbstractWorkflowWebscript {
	public static final String PARAM_AUTHORITY = "authority";
	public static final String PARAM_STATE = "state";
	public static final String PARAM_PRIORITY = "priority";
	public static final String PARAM_DUE_BEFORE = "dueBefore";
	public static final String PARAM_DUE_AFTER = "dueAfter";
	public static final String PARAM_PROPERTIES = "properties";
	public static final String PARAM_POOLED_TASKS = "pooledTasks";

	public static final String EMPLOYEE_KEY = "employee";
	public static final String EMPLOYEE_NODEREF_KEY = "nodeRef";
	public static final String EMPLOYEE_NAME_KEY = "name";

	private OrgstructureBean orgstructureService;

	private WorkflowTaskDueAscComparator taskComparator = new WorkflowTaskDueAscComparator();

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	@Override
	protected Map<String, Object> buildModel(WorkflowModelBuilder modelBuilder, WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> filters = new HashMap<String, Object>(4);

		// authority is not included into filters list as it will be taken into account before filtering
		String authority = getAuthority(req);

		// state is also not included into filters list, for the same reason
		WorkflowTaskState state = getState(req);

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

		List<WorkflowTask> allTasks = new ArrayList<WorkflowTask>();
		List<String> subordinates = getSubordinates(authority);
		if (subordinates != null) {
			for (String login: subordinates) {
				allTasks.addAll(getTasks(login, state, pooledTasksOnly));
			}
		}

		// filter results
		ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (WorkflowTask task : allTasks) {
			if (matches(task, filters)) {
				Map<String, Object> props = modelBuilder.buildSimple(task, properties);

				String owner = (String) task.getProperties().get(ContentModel.PROP_OWNER);
				if (owner != null) {
					props.put(EMPLOYEE_KEY, getEmployeeProps(owner));
				}

				results.add(props);
			}
		}

		// create and return results, paginated if necessary
		return createResultModel(req, "taskInstances", results);
	}

	private List<String> getSubordinates(String bossLogin) {
		List<String> result = new ArrayList<String>();
		NodeRef bossEmployee = this.orgstructureService.getEmployeeByPerson(bossLogin);
		if (bossEmployee != null) {
			List<NodeRef> subordinates = this.orgstructureService.getBossSubordinate(bossEmployee);
			if (subordinates != null) {
				for (NodeRef ref : subordinates) {
					String employeeLogin = this.orgstructureService.getEmployeeLogin(ref);
					if (employeeLogin != null) {
						result.add(employeeLogin);
					}
				}
			}
		}
		return result;
	}

	private List<WorkflowTask> getTasks(String authority, WorkflowTaskState state, Boolean pooledTasksOnly) {
		List<WorkflowTask> allTasks = new ArrayList<WorkflowTask>();

		// default task state to IN_PROGRESS if not supplied
		if (state == null) {
			state = WorkflowTaskState.IN_PROGRESS;
		}

		if (authority != null) {
			List<WorkflowTask> tasks = workflowService.getAssignedTasks(authority, state);
			List<WorkflowTask> pooledTasks = workflowService.getPooledTasks(authority);
			if (pooledTasksOnly != null) {
				if (pooledTasksOnly.booleanValue()) {
					// only return pooled tasks the user can claim
					allTasks = new ArrayList<WorkflowTask>(pooledTasks.size());
					allTasks.addAll(pooledTasks);
				} else {
					// only return tasks assigned to the user
					allTasks = new ArrayList<WorkflowTask>(tasks.size());
					allTasks.addAll(tasks);
				}
			} else {
				// include both assigned and unassigned tasks
				allTasks = new ArrayList<WorkflowTask>(tasks.size() + pooledTasks.size());
				allTasks.addAll(tasks);
				allTasks.addAll(pooledTasks);
			}

			// sort tasks by due date
			Collections.sort(allTasks, taskComparator);
		}
		return allTasks;
	}

	private HashMap<String, String> getEmployeeProps(String owner) {
		if (owner != null) {
			NodeRef ownerEmployee = this.orgstructureService.getEmployeeByPerson(owner);
			if (ownerEmployee != null) {
				HashMap<String, String> employeeProps = new HashMap<String, String>();
				employeeProps.put(EMPLOYEE_NODEREF_KEY, ownerEmployee.toString());
				employeeProps.put(EMPLOYEE_NAME_KEY, (String) nodeService.getProperty(ownerEmployee, ContentModel.PROP_NAME));

				return employeeProps;
			}
		}
		return null;
	}

	/**
	 * Retrieves the list of property names to include in the response.
	 *
	 * @param req The WebScript request
	 * @return List of property names
	 */
	private List<String> getProperties(WebScriptRequest req) {
		String propertiesStr = req.getParameter(PARAM_PROPERTIES);
		if (propertiesStr != null) {
			return Arrays.asList(propertiesStr.split(","));
		}
		return null;
	}

	/**
	 * Retrieves the pooledTasks parameter.
	 *
	 * @param req The WebScript request
	 * @return null if not present, Boolean object otherwise
	 */
	private Boolean getPooledTasks(WebScriptRequest req) {
		Boolean result = null;
		String includePooledTasks = req.getParameter(PARAM_POOLED_TASKS);

		if (includePooledTasks != null) {
			result = Boolean.valueOf(includePooledTasks);
		}

		return result;
	}

	/**
	 * Gets the specified {@link WorkflowTaskState}, null if not requested
	 *
	 * @param req
	 * @return
	 */
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

	/**
	 * Returns the specified authority. If no authority is specified then returns the current Fully Authenticated user.
	 *
	 * @param req
	 * @return
	 */
	private String getAuthority(WebScriptRequest req) {
		String authority = req.getParameter(PARAM_AUTHORITY);
		if (authority == null || authority.length() == 0) {
			authority = null;
		}
		return authority;
	}

	/**
	 * Determine if the given task should be included in the response.
	 *
	 * @param task    The task to check
	 * @param filters The list of filters the task must match to be included
	 * @return true if the task matches and should therefore be returned
	 */
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

	/**
	 * Comparator to sort workflow tasks by due date in ascending order.
	 */
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
