/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.base.beans;

import java.util.List;
import org.alfresco.repo.action.scheduled.AbstractScheduledAction;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.quartz.Scheduler;
import ru.it.lecm.base.beans.LecmTransactionHelper;

/**
 *
 * @author ikhalikov
 */
public abstract class BaseTransactionalSchedule extends AbstractScheduledAction {

	private Scheduler scheduler; 

	private LecmTransactionHelper lecmTransactionHelper;
	private boolean readOnly;

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}
	
	public abstract List<NodeRef> getNodesInTx();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		register(getScheduler());
	}
	
	@Override
	public List<NodeRef> getNodes() {
		return lecmTransactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<List>() {
			@Override
			public List execute() throws Throwable {
				return getNodesInTx();
			}
		}, readOnly);
	}
	
}
