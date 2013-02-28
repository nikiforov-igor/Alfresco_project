package ru.it.lecm.integrotest.actions;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;

import ru.it.lecm.integrotest.utils.NodeRefData;

/**
 * Продвинуть документ по его ЖЦ.
 */
public class DocMoveByWorkflowStep extends LecmActionBase {

	private NodeRef nodeRef;
	final private NodeRefData ref = new NodeRefData();
	private String nodeRefMacros;

	public NodeRef getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(NodeRef nodeRef) {
		this.nodeRef = nodeRef;
		ref.clear(); // drop
	}

	public void setNodeRefStr(String value) {
		setNodeRef( new NodeRef(value));
	}

	/**
	 * @param macros в виде "список_аргументов.аргумент"
	 * список аргументов - один из "result", "work", "config",
	 * аргумент - ключ для получения значения из этого списка.
	 * например <property name="nodeByMacros" value="result.createdId" />
	 */
	public void findNodeByMacros(String macros) {
		this.setNodeRef( (NodeRef) getArgsAssigner().getMacroValue(macros) );
	}

	/**
	 * Задать макрос, который надо будет использовать для вычисления nodeRef 
	 * @param macrosValue
	 */
	public void setNodeRefMacros(String macrosValue) {
		this.nodeRefMacros = macrosValue;
	}

	public NodeRefData getRef() {
		return ref;
	}

	@Override
	public void run() {
		// при необходимости находим узел ...
		if (this.nodeRef == null && ref.hasRefData()) {
			this.nodeRef = ref.findNodeRef(getContext().getFinder());
		}

		if (this.nodeRef == null && this.nodeRefMacros != null) {
			findNodeByMacros( this.nodeRefMacros);
		}

		doNext();
	}

	private void doNext() {
		final WorkflowService wfSrvc = getContext().getPublicServices().getWorkflowService();
		final List<WorkflowInstance> wfi = wfSrvc.getWorkflowsForContent(this.nodeRef, true); // активные процессы
		if (wfi != null && wfi.size() >=1 )  {
			final String wfid = wfi.get(0).getId();
			// final WorkflowTask wfTask = wfSrvc.getStartTask(wfid);
			// final String taskId = ...
			final String taskId = getContext().getStateMachineService().getCurrentTaskId(wfid);
			logger.warn( String.format("Document %s using active Workflow item %s", nodeRef, taskId));

			// смена статуса ...
			getContext().getStateMachineService().nextTransition( taskId);

			logger.info( String.format("Document '%s' has been moved to the next Workflow step", nodeRef));
		} else {
			logger.warn( String.format("Document '%s' has no active Workflow items -> nothing to move", nodeRef));
		}
	}

}