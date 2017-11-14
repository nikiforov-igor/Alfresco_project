package ru.it.lecm.eds.scripts;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.eds.api.EDSGlobalSettingsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author dbayandin
 */
public class EDSGlobalSettingsWebScriptBean extends BaseWebScript {

	private EDSGlobalSettingsService edsGlobalSettingsService;
	private OrgstructureBean orgstructureService;

	public void setEdsGlobalSettingsService(EDSGlobalSettingsService edsGlobalSettingsService) {
		this.edsGlobalSettingsService = edsGlobalSettingsService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public Scriptable getPotentialWorkers(String businessRoleId, String organizationElementStrRef) {
		return getPotentialWorkers(businessRoleId, organizationElementStrRef, false);
	}

	public Scriptable getPotentialWorkers(ScriptNode businessRoleRef, ScriptNode organizationElementRef) {
		return getPotentialWorkers(businessRoleRef, organizationElementRef, false);
	}

	public Scriptable getPotentialWorkers(String businessRoleId, ScriptNode organizationElementRef) {
		return getPotentialWorkers(businessRoleId, organizationElementRef, false);
	}

	public Scriptable getPotentialWorkers(String businessRoleId, String organizationElementStrRef, boolean checkEmployeeRole) {
		if (organizationElementStrRef == null || !NodeRef.isNodeRef(organizationElementStrRef)
				|| businessRoleId == null || businessRoleId.isEmpty()) {
			return null;
		}
		NodeRef organizationElementRef = new NodeRef(organizationElementStrRef);
		Collection<NodeRef> result = edsGlobalSettingsService.getPotentialWorkers(businessRoleId, organizationElementRef, checkEmployeeRole);
		return createScriptable(new ArrayList<NodeRef>(result));
	}

	public Scriptable getPotentialWorkers(ScriptNode businessRoleRef, ScriptNode organizationElementRef, boolean checkEmployeeRole) {
		Collection<NodeRef> potentialWorkers = edsGlobalSettingsService.getPotentialWorkers(businessRoleRef.getNodeRef(), organizationElementRef.getNodeRef(), checkEmployeeRole);
		return createScriptable(new ArrayList<NodeRef>(potentialWorkers));
	}

	public Scriptable getPotentialWorkers(String businessRoleId, ScriptNode organizationElementRef, boolean checkEmployeeRole) {
		Collection<NodeRef> potentialWorkers = edsGlobalSettingsService.getPotentialWorkers(businessRoleId, organizationElementRef.getNodeRef(), checkEmployeeRole);
		return createScriptable(new ArrayList<NodeRef>(potentialWorkers));
	}

	public Scriptable getRegistrars(final String businessRoleId, final ScriptNode ordRef) {
		//получаем текущего пользователя
		NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
		// централизованная ли регистрация
//		TODO: Метод getRegistras в итоге дёргает метод getSettingsNode, который ранее был getOrCreate.
//		Теперь метод разделён и необходимо провести проверку на существование ноды и создать её при необходимости
//              нода настроек создаётся при инициализации бина. проверять не будем.
//		if(edsGlobalSettingsService.getSettingsNode() == null) {
//			try {
////				Используется только в скриптах машины состояний, должно быть уже в транзакции
//				edsGlobalSettingsService.createSettingsNode();
//			} catch (WriteTransactionNeededException ex) {
//				throw new RuntimeException("Can't create settings node", ex);
//			}
//		}
		List<NodeRef> registrars = edsGlobalSettingsService.getRegistras(currentEmployee, businessRoleId);
		return createScriptable(registrars);
	}

	public void savePotentialWorkers(String businessRoleId, JSONObject employeesJsonMap) throws JSONException {
		if (businessRoleId == null || employeesJsonMap == null) {
			return;
		}
		Iterator orgElementIterator = employeesJsonMap.keys();
		while (orgElementIterator.hasNext()) {
			String orgElementStrRef = (String) orgElementIterator.next();
			if (orgElementStrRef == null || !NodeRef.isNodeRef(orgElementStrRef)) {
				continue;
			}

			NodeRef orgElementRef = new NodeRef(orgElementStrRef);
			List<NodeRef> employeesRefs = new ArrayList<NodeRef>();
			JSONObject employees = employeesJsonMap.getJSONObject(orgElementStrRef);
			if (employees != null) {
				Iterator employeeIterator = employees.keys();
				while (employeeIterator.hasNext()) {
					String employeeStrRef = (String) employeeIterator.next();
					if (employeeStrRef == null || !NodeRef.isNodeRef(employeeStrRef)) {
						continue;
					}

					NodeRef employeeRef = new NodeRef(employeeStrRef);
					employeesRefs.add(employeeRef);
				}
			}
			edsGlobalSettingsService.savePotentialWorkers(businessRoleId, orgElementRef, employeesRefs);
		}
	}

	/**
	 * Получение дежурного регистратора
	 * @return scriptNode дежурного регистратора
     */
	public ScriptNode getDutyRegistrar() {
		return new ScriptNode(edsGlobalSettingsService.getDutyRegistrar(), serviceRegistry, getScope());
	}

	public ScriptNode getSettingsNode() {
//		TODO:  метод getSettingsNode ранее был getOrCreate.
//		Теперь метод разделён и необходимо провести проверку на существование ноды и создать её при необходимости
//              нода настроек создаётся при инициализации бина. проверять не будем.            
//		NodeRef settings = edsGlobalSettingsService.getSettingsNode();
//		if(settings == null) {
////			Вызывается без транзакции, обернём
//			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
//
//				@Override
//				public NodeRef execute() throws Throwable {
//					return edsGlobalSettingsService.createSettingsNode();
//				}
//			};
//			settings = (NodeRef) lecmTransactionHelper.doInTransaction(cb, false);
//		}
		
		NodeRef settings = edsGlobalSettingsService.getSettingsNode();
		if(settings == null) {
			try{
				settings = edsGlobalSettingsService.createSettingsNode();
			}catch(Exception e) {
				
			}
		}
		if (settings != null) {
			return new ScriptNode(settings, serviceRegistry, getScope());
		}
		return null;
	}

	public boolean isRegistrationCenralized() {
		return edsGlobalSettingsService.isRegistrationCenralized();
	}

	public boolean isAllowSigningOnPaper() {
		return edsGlobalSettingsService.isAllowSigningOnPaper();
	}

    /**
     * Проверка, скрывать ли свойства для получателей
     */
    @Deprecated
    public Boolean isHidePropsForRecipients() {
//		TODO: Метод isHideProperties в итоге дёргает метод getSettingsNode, который ранее был getOrCreate.
//		Теперь метод разделён и необходимо провести проверку на существование ноды и создать её при необходимости
//              нода настроек создаётся при инициализации бина. проверять не будем.
//		if(edsGlobalSettingsService.getSettingsNode() == null) {
//			try {
////			Используется только в скриптах машины состояний, должно быть уже в транзакции
//				edsGlobalSettingsService.createSettingsNode();
//			} catch (WriteTransactionNeededException ex) {
//				throw new RuntimeException("Can't create settings node", ex);
//			}
//		}
		return edsGlobalSettingsService.isHideProperties();
	}

    /**
     * Получение узла АРМ для Дашлета «Моя работа»
     */
    public ScriptNode getArmDashletNode() {
//		TODO: Метод getArmDashletNode в итоге дёргает метод getSettingsNode, который ранее был getOrCreate.
//		Теперь метод разделён и необходимо провести проверку на существование ноды и создать её при необходимости
//              нода настроек создаётся при инициализации бина. проверять не будем.
//		if(edsGlobalSettingsService.getSettingsNode() == null) {
////			Вызывается в RO транзакции, обернём
//			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//
//				@Override
//				public Void execute() throws Throwable {
//					edsGlobalSettingsService.createSettingsNode();
//					return null;
//				}
//			};
//			lecmTransactionHelper.doInTransaction(cb, false);
//		}

        NodeRef armDN =  edsGlobalSettingsService.getArmDashletNode();
        if (armDN != null) {
            return new ScriptNode(armDN, serviceRegistry, getScope());
        }
        return null;
    }

    /**
     * Получение АРМ для Дашлета «Моя работа»
     */
    public ScriptNode getArm() {
//		TODO: Метод getArm в итоге дёргает метод getSettingsNode, который ранее был getOrCreate.
//		Теперь метод разделён и необходимо провести проверку на существование ноды и создать её при необходимости
//              нода настроек создаётся при инициализации бина. проверять не будем.        
//		if(edsGlobalSettingsService.getSettingsNode() == null) {
////			Вызывается в RO транзакции, обернём
//			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
//
//				@Override
//				public Void execute() throws Throwable {
//					edsGlobalSettingsService.createSettingsNode();
//					return null;
//				}
//			};
//			lecmTransactionHelper.doInTransaction(cb, false);
//		}

        NodeRef arm =  edsGlobalSettingsService.getArm();
        if (arm != null) {
            return new ScriptNode(arm, serviceRegistry, getScope());
        }
        return null;
    }

	@Deprecated
	public String getLinksViewMode() {
		return edsGlobalSettingsService.getLinksViewMode();
	}

	/**
	 * Получение настроек сроков уведомлений
	 * @return узел с настройками сроков уведомлений
	 */
	public ScriptNode getTermsOfNotificationSettingsNode() {
		NodeRef settings = edsGlobalSettingsService.getTermsOfNotificationSettingsNode();
		if(settings != null) {
			return new ScriptNode(settings, serviceRegistry, getScope());
		}
		return null;
	}

	/**
	 * Получение количества рабочих дней за которое должно высылаться уведомление
	 * @return количество рабочих дней за которое должно высылаться уведомление
	 */
	public int getSettingsNDays() {
		return edsGlobalSettingsService.getSettingsNDays();
	}

	/**
	 * Получение количества рабочих дней для краткосрочного исполнения за которое должно высылаться уведомление
	 * @return количество рабочих дней для краткосрочного исполнения за которое должно высылаться уведомление
	 */
	public int getSettingsShortNDays() {
		return edsGlobalSettingsService.getSettingsShortNDays();
	}

	/**
	 * Получение предельного количества календарных дней краткосрочного исполнения
	 * @return предельное количество календарных дней краткосрочного исполнения
	 */
	public int getSettingsShortLimitDays() {
		return edsGlobalSettingsService.getSettingsShortLimitDays();
	}
}
