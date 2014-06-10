package ru.it.lecm.integrotest;

import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.transaction.TransactionService;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureSGNotifierBean;
import ru.it.lecm.security.events.IOrgStructureNotifiers;
import ru.it.lecm.statemachine.StateMachineServiceBean;

/**
 * Контекст выполнения для Действия:
 *   1) конфигурационные аргументы
 *   2) активные рабочие аргументы (в начале совпадают с тем, что в 
 * конфигурационных было на момент старта Исполнителя)
 * 3) результирующие значения.
 *
 * (!) @NOTE в дальнейшем, для аргументов и результатов, возможно использование 
 * smart-map, которые могли бы контролировать добавляемые ключи, на предмет 
 * соот-вия некоторому предопределённому списку для контроля использования 
 * аргументов и результатов, чтобы "отсекать" неизвестные значения.
 */	
public interface RunContext {

	/**
	 * Получить конфигурационные настройки
	 *
	 * @return readonly список с конфигурацией типа
	 * Collections.UnmodifiableMap<String, Object>
	 */
	Map<String, Object> configArgs();

	/**
	 * Получить текущие (вычисляемые) настройки. Предполагается, что Actions 
	 * будут использовать для чтения-записи рабочих данных и аргументов. 
	 * см также getResults
	 *
	 * @return r/w карта с аргументами
	 */
	Map<String, Object> workArgs();

//	/**
//	 * @param args
//	 */
//	void setWorkArgs(Map<String, Object> args);

	/**
	 * Получить текущие результаты, которые наполняются последовательными действиями.
	 * Предполагается для выдачи своих результатов для Actions.
	 *
	 * @return r/w карта с результатами
	 */
	Map<String, Object> results();

	/**
	 * @return сервис узлов alfresco
	 */
	NodeService getNodeService();

	/**
	 * @return для явного управления транзакциями 
	 * (!) при фромировании объектов в базе надо иметь активной writable-транзакцию,
	 * а такие обычно надо создавать явно
	 */
	TransactionService getTransactionService();

	/**
	 * @return Служба прав доступа к узлам
	 */
	PermissionService getPermissionService();

	/**
	 * @return Публичные службы
	 */
	ServiceRegistry getPublicServices();

	/**
	 * @return сервис авторизации alfresco (выдача прав на папки)
	 */
	AuthorityService getAuthorityService();

	/**
	 * @return сервис аутентификации alfresco
	 */
	AuthenticationService getAuthenticationService();

	/**
	 * @return сервис оповещения для LECM-службы Seciruty Groups
	 */
	OrgstructureSGNotifierBean getOrgSGNotifier();

	/**
	 * @return сервис самой LECM-службы SG
	 */
	IOrgStructureNotifiers getSgNotifier();

	/**
	 * @return сервис орг-штатки LECM
	 */
	OrgstructureBean getOrgstructureService();

	/**
	 * @return help-сервис загрузки данных
	 */
	FinderBean getFinder();

	StateMachineServiceBean getStateMachineService();

	/**
	 * Получить родительский тест, в камках которого происходит выполнение.
	 * @return
	 */
	SingleTest getParent();

}