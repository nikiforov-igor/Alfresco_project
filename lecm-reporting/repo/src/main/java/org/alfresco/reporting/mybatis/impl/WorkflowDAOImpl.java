package org.alfresco.reporting.mybatis.impl;

import java.util.HashMap;
import java.util.List;
import org.alfresco.reporting.mybatis.WorkflowDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

public class WorkflowDAOImpl implements WorkflowDAO {

   private SqlSession sessionTemplate = null;
   private static Log logger = LogFactory.getLog(WorkflowDAOImpl.class);


   public void setWorkflowTemplate(SqlSessionTemplate template) {
      this.sessionTemplate = template;
   }

   public List getDeletedTasks(String fromDate) {
      List results = null;
      if(null == fromDate) {
         logger.debug("getDeletedTasks - no date");
         results = this.sessionTemplate.selectList("get-all-completed-tasks");
      } else {
         logger.debug("getDeletedTasks - with date " + fromDate);
         results = this.sessionTemplate.selectList("get-completed-tasks-since", fromDate);
      }

      return results;
   }

   public List getCreatedTasks(String fromDate) {
      List results;
      if(null == fromDate) {
         logger.debug("getCreatedTasks - no date");
         results = this.sessionTemplate.selectList("get-all-created-tasks");
      } else {
         logger.debug("getCreatedTasks - with date " + fromDate);
         results = this.sessionTemplate.selectList("get-created-tasks-since", fromDate);
      }

      return results;
   }

   public HashMap getPropertiesForWorkflowTask(String taskId) {
      return (HashMap)this.sessionTemplate.selectOne("get-additional-task-properties", taskId);
   }

   public List getCompletedProcesses(String fromDate) {
      List results = null;
      if(null == fromDate) {
         logger.debug("getCompletedProcesses - no date");
         results = this.sessionTemplate.selectList("get-all-completed-processes");
      } else {
         logger.debug("getCompletedProcesses - with date " + fromDate);
         results = this.sessionTemplate.selectList("get-completed-processes-since", fromDate);
      }

      return results;
   }

   public List getCreatedProcesses(String fromDate) {
      List results;
      if(null == fromDate) {
         logger.debug("getCreatedProcesses - no date");
         results = this.sessionTemplate.selectList("get-all-created-processes");
      } else {
         logger.debug("getCreatedProcesses - with date " + fromDate);
         results = this.sessionTemplate.selectList("get-created-processes-since", fromDate);
      }

      return results;
   }

   public HashMap getPropertiesForWorkflowInstance(String processId) {
      return (HashMap)this.sessionTemplate.selectOne("get-additional-process-properties", processId);
   }

}
