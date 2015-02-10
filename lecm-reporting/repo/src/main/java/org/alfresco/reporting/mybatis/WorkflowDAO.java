package org.alfresco.reporting.mybatis;

import java.util.HashMap;
import java.util.List;

public interface WorkflowDAO {

   List getCreatedTasks(String var1);

   List getDeletedTasks(String var1);

   HashMap getPropertiesForWorkflowTask(String var1);

   List getCreatedProcesses(String var1);

   List getCompletedProcesses(String var1);

   HashMap getPropertiesForWorkflowInstance(String var1);
}
