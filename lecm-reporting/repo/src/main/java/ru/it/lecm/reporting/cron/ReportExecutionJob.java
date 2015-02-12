package ru.it.lecm.reporting.cron;

import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.InitializingBean;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReportExecutionJob implements Job, InitializingBean {

   protected JobLockService jobLockService;
   protected ActionService actionService;
   private int startDelayMinutes = 0;
   private static Log logger = LogFactory.getLog(ReportExecutionJob.class);
   private QName lock = QName.createQName("http://www.alfresco.org/model/reporting/1.0", "ReportExecutionJob");


   public ActionService getActionService() {
      return this.actionService;
   }

   public void setStartDelayMinutes(int startDelayMinutes) {
      this.startDelayMinutes = startDelayMinutes;
   }

   public void setActionService(ActionService actionService) {
      this.actionService = actionService;
   }

   private QName getLockKey() {
      logger.debug("Returning lock " + this.lock.toString());
      return this.lock;
   }

   private String getLock(QName lock, long time) {
      try {
         return this.jobLockService.getLock(lock, time);
      } catch (LockAcquisitionException var5) {
         return null;
      }
   }

   public void afterPropertiesSet() throws Exception {
      this.lock = QName.createQName("http://www.alfresco.org/model/reporting/1.0", "ReportExecutionJob");
   }

   public void execute(JobExecutionContext context) throws JobExecutionException {
      JobDataMap jobData = context.getJobDetail().getJobDataMap();
      this.jobLockService = (JobLockService)jobData.get("jobLockService");
      logger.debug("jobLockService hashcode=" + this.jobLockService.hashCode());
      String lockToken = this.getLock(this.getLockKey(), 60000L);
      if(lockToken != null) {
         final AtomicBoolean running = new AtomicBoolean(true);
         this.jobLockService.refreshLock(lockToken, this.lock, 30000L, new JobLockRefreshCallback() {
            public boolean isActive() {
               return running.get();
            }
            public void lockReleased() {
               running.set(false);
            }
         });

         try {
            logger.debug("Start executeImpl");
            this.executeImpl(running, context);
            logger.debug("End executeImpl");
         } catch (RuntimeException var9) {
            throw var9;
         } finally {
            running.set(false);
            this.jobLockService.releaseLock(lockToken, this.getLockKey());
            logger.debug("Released the lock");
         }

      }
   }

   public void executeImpl(AtomicBoolean running, JobExecutionContext context) throws JobExecutionException {
      JobDataMap jobData = context.getJobDetail().getJobDataMap();
      final String frequency = jobData.getString("frequency");
      if(logger.isDebugEnabled()) {
         logger.debug("executeImpl starting with frequency " + frequency);
      }

      boolean startupDelayMillis = false;
      int startupDelayMillis1 = Integer.parseInt(jobData.getString("startDelayMinutes")) * '\uea60';
      if(logger.isDebugEnabled()) {
         logger.debug("executeImpl starting with delay " + startupDelayMillis1);
      }

      RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
      if((long)startupDelayMillis1 < rb.getUptime()) {
         AuthenticationUtil.runAs(new RunAsWork() {
            public Void doWork() {
               Action action = ReportExecutionJob.this.actionService.createAction("report-root-executer");
               boolean canRun = false;
               if(frequency != null) {
                  if("hourly".equals(frequency)) {
                     action.setParameterValue("executionFrequency", frequency);
                     canRun = true;
                  }

                  if("daily".equals(frequency)) {
                     action.setParameterValue("executionFrequency", frequency);
                     canRun = true;
                  }

                  if("weekly".equals(frequency)) {
                     action.setParameterValue("executionFrequency", frequency);
                     canRun = true;
                  }

                  if("monthly".equals(frequency)) {
                     action.setParameterValue("executionFrequency", frequency);
                     canRun = true;
                  }

                  if(canRun) {
                     if(ReportExecutionJob.logger.isDebugEnabled()) {
                        ReportExecutionJob.logger.debug("executeImpl: start running action " + frequency);
                     }

                     ReportExecutionJob.this.actionService.executeAction(action, (NodeRef)null);
                  } else {
                     ReportExecutionJob.logger.error("executeImpl: Confguration mistake, cannot run. Frequency has no meaningful value: " + frequency);
                  }
               } else {
                  action.setParameterValue("executionFrequency", "hourly");
                  ReportExecutionJob.this.actionService.executeAction(action, (NodeRef)null);
                  action.setParameterValue("executionFrequency", "daily");
                  ReportExecutionJob.this.actionService.executeAction(action, (NodeRef)null);
                  action.setParameterValue("executionFrequency", "weekly");
                  ReportExecutionJob.this.actionService.executeAction(action, (NodeRef)null);
                  action.setParameterValue("executionFrequency", "monthly");
                  ReportExecutionJob.this.actionService.executeAction(action, (NodeRef)null);
               }

               return null;
            }
         }, AuthenticationUtil.getSystemUserName());
      } else {
         logger.info("Hey relax! Its too early to work... Let the repository get up first... Aborting this run.");
      }

   }

}
