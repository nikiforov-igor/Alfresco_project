package ru.it.lecm.reporting.mybatis.impl;


public class WorkflowTaskAddition {

   private String proc_def_id_;
   private String task_def_key_;
   private String delete_reason_;
   private Long duration;


   public String getProc_def_id_() {
      return this.proc_def_id_;
   }

   public void setProc_def_id_(String proc_def_id_) {
      this.proc_def_id_ = proc_def_id_;
   }

   public String getTask_def_key_() {
      return this.task_def_key_;
   }

   public void setTask_def_key_(String task_def_key_) {
      this.task_def_key_ = task_def_key_;
   }

   public String getDelete_reason_() {
      return this.delete_reason_;
   }

   public void setDelete_reason_(String delete_reason_) {
      this.delete_reason_ = delete_reason_;
   }

   public Long getDuration() {
      return this.duration;
   }

   public void setDuration(Long duration) {
      this.duration = duration;
   }
}
