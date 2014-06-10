package ru.it.lecm.statemachine.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.bpmn.model.BaseElement;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.security.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.statemachine.StateMachineHelper;
//import ru.it.lecm.statemachine.TimerActionHelper;
import ru.it.lecm.statemachine.action.StateMachineAction;
import ru.it.lecm.statemachine.action.StatusChangeAction;
import ru.it.lecm.statemachine.bean.StateMachineActionsImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:30
 * <p/>
 * Слушатель машины состояний. Содержит в себе описание событий и действий для определенного статуса машины состояний
 */

public class StateMachineHandler {

	private final static Logger logger = LoggerFactory.getLogger(StateMachineHandler.class);

	private ServiceRegistry serviceRegistry;
	private LecmPermissionService lecmPermissionService;
	private BusinessJournalService businessJournalService;
	private RepositoryStructureHelper repositoryStructureHelper;
	private LecmPermissionService LecmPermissionService;
	private OrgstructureBean orgstructureBean;
//    private TimerActionHelper timerActionHelper;
    private DocumentService documentService;
    private PermissionService permissionService;
    private StateMachineHelper stateMachineHelper;

	private String processId = "";

	public StateMachineHandler() {
	}

    public StatemachineTaskListener configure(BaseElement lecmExtention, String processId) {
        Map<String, ArrayList<StateMachineAction>> events = new HashMap<String, ArrayList<StateMachineAction>>();
        this.processId = processId;
		events.put(ExecutionListener.EVENTNAME_START, new ArrayList<StateMachineAction>());
		events.put(ExecutionListener.EVENTNAME_TAKE, new ArrayList<StateMachineAction>());
		events.put(ExecutionListener.EVENTNAME_END, new ArrayList<StateMachineAction>());
		
//		events.get(ExecutionListener.EVENTNAME_START).add(getStateMachineAction(lecmExtention));
				
//		List<Element> eventsElement = lecmExtention.elements("event");
//		for (Element event : eventsElement) {
//			String eventName = event.attribute("on").toLowerCase();
//			ArrayList<StateMachineAction> stateMachineActions = events.get(eventName);
//			List<Element> actions = event.elements("action");
//			for (Element action : actions) {
//				StateMachineAction stateMachineAction = getStateMachineAction(action);
//				if (stateMachineAction != null) {
//					stateMachineActions.add(stateMachineAction);
//				}
//			}
//		}
        return new StatemachineTaskListener(events);
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
    }

	public void setLecmPermissionService(LecmPermissionService value) {
        this.lecmPermissionService = value;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
	}

//    public void setTimerActionHelper(TimerActionHelper timerActionHelper) {
//        this.timerActionHelper = timerActionHelper;
//    }

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    private StateMachineAction getStateMachineAction(final BaseElement actionElement) {
//		String actionName = actionElement.attribute("type");
		StateMachineAction action = null;
		try {
//			Class actionClass = Class.forName(StateMachineActionsImpl.getClassName(actionName));
//			action = (StateMachineAction) actionClass.newInstance();
			//action = (StateMachineAction) new StatusChangeAction();
		} catch (Exception e) {
//			logger.error("Cannot initialize action " + actionName, e);
		}

		if (action != null) {
    	//action.setServiceRegistry(serviceRegistry);
//		action.setLecmPermissionService(lecmPermissionService);
//		action.setBusinessJournalService(businessJournalService);
		//action.setRepositoryStructureHelper(repositoryStructureHelper);
		//action.setTimerActionHelper(timerActionHelper);
		//action.setOrgstructureBean(orgstructureBean);
		action.init(actionElement, processId);
    			
//		String actionName = actionElement.attribute("type");
//		StateMachineAction action = null;
//		try {
//			Class actionClass = Class.forName(StateMachineActionsImpl.getClassName(actionName));
//			action = (StateMachineAction) actionClass.newInstance();
//		} catch (Exception e) {
//			logger.error("Cannot initialize action " + actionName, e);
//		}
//
//		if (action != null) {
//			action.setServiceRegistry(serviceRegistry);
//			action.setLecmPermissionService(lecmPermissionService);
//			action.setBusinessJournalService(businessJournalService);
//			action.setRepositoryStructureHelper(repositoryStructureHelper);
//			action.setTimerActionHelper(timerActionHelper);
//          action.setOrgstructureBean(orgstructureBean);
//			final StateMachineAction currentAction = action;
//			try {
//				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
//					@Override
//					public Void doWork() throws Exception {
//						currentAction.init(actionElement, processId);
//						return null;
//					}
//				});
//			} catch (Exception e) {
//				logger.error("Error while init action", e);
//				throw new IllegalStateException(e);
//			}
		}
		return action;
	}

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
	}

    public void setStateMachineHelper(StateMachineHelper stateMachineHelper) {
        this.stateMachineHelper = stateMachineHelper;
    }

    public class StatemachineTaskListener implements ExecutionListener {

        private Map<String, ArrayList<StateMachineAction>> events = new HashMap<String, ArrayList<StateMachineAction>>();

        public StatemachineTaskListener(Map<String, ArrayList<StateMachineAction>> events) {
            this.events = events;
        }

        @Override
        public void notify(final DelegateExecution execution) throws Exception {
            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {
                @Override
                public Void doWork() throws Exception {
                    try {
                        String eventName = execution.getEventName();
                        List<StateMachineAction> actions = events.get(eventName);
                        for (StateMachineAction action : actions) {
                            action.execute(execution);
                        }

                        if (eventName.equals(ExecutionListener.EVENTNAME_START)) {
                            actions = events.get(ExecutionListener.EVENTNAME_TAKE);
                            for (StateMachineAction action : actions) {
                                action.execute(execution);
                            }
                        }
                    } catch (InvalidNodeRefException ex) {
						logger.error("Something goes wrong while changing status", ex);
						throw ex;
                    } catch (Exception e) {
                        logger.error("Error while action execution", e);
                    }
                    return null;
                }
            });
        }

        public Map<String, ArrayList<StateMachineAction>> getEvents() {
            return events;
        }

    }

}
