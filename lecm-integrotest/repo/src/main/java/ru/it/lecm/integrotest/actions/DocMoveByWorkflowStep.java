package ru.it.lecm.integrotest.actions;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;

import ru.it.lecm.integrotest.utils.NodeRefData;
import ru.it.lecm.statemachine.StateMachineServiceBean;

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
	public NodeRef findNodeByMacros(String macros) {
		return (NodeRef) getArgsAssigner().getMacroValue(macros);
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
		NodeRef docRef = this.nodeRef;
		// при необходимости находим узел ...
		if (docRef == null && ref.hasRefData()) {
			docRef = ref.findNodeRef(getContext().getFinder());
		}

		if (docRef == null && this.nodeRefMacros != null) {
			docRef = findNodeByMacros( this.nodeRefMacros);
		}

		doNext( docRef, getContext().getStateMachineService(), getContext().getPublicServices().getWorkflowService());
	}

	private void doNext(final NodeRef doc, final StateMachineServiceBean smSrvc, final WorkflowService wfSrvc) {
		final List<WorkflowInstance> wfi = wfSrvc.getWorkflowsForContent(doc, true); // активные процессы
		if (wfi != null && wfi.size() >=1 )  {
			logger.info( String.format("Found %d WorkflowInstanses for Document {%s}", wfi.size(), doc));

			// for( WorkflowInstance item: wfi) 
			{
				// final WorkflowTask wfTask = wfSrvc.getStartTask(wfid);
				// final String taskId = ...
				// final String wfid = item.getId(); 
				final String wfid = wfi.get(0).getId();
				final String taskId = smSrvc.getCurrentTaskId(wfid);
				logger.warn( String.format("Document %s using active Workflow item %s", doc, taskId));

				// смена статуса ...
				final String res = smSrvc.nextTransition( taskId);

				logger.info( String.format("Document '%s' has been moved to the next Workflow step as '%s'", doc, res));
			}
		} else {
			logger.warn( String.format("Document '%s' has no active Workflow items -> nothing to move", doc));
		}
	}

}