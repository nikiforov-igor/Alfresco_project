package ru.it.lecm.errands.shedule.processors;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.wcalendar.absence.IAbsence;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: IGanin
 * Date: 17.11.2017
 * Time: 17:25
 *
 * Базовый процессор(обработчик) исключительных ситуаций по периодическому созданию поручений
 */
public abstract class BaseCreationExceptionProcessor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected NodeService nodeService;
    protected PersonService personService;
    protected OrgstructureBean orgstructureService;
    protected ErrandsService errandsService;
    protected LecmPermissionService lecmPermissionService;
    protected IAbsence absenceService;

    public void setAbsenceService(IAbsence absenceService) {
        this.absenceService = absenceService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setLecmPermissionService(LecmPermissionService lecmPermissionService) {
        this.lecmPermissionService = lecmPermissionService;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    /**
     * Вычисление запрета на процесс создания.
     * @param params
     */
    public boolean isAllowCreation(Map<ProcessorParamName, Object> params) {
        return true;
    }

    /**
     * Проверка необходимости запуска процессора
     * @param params параметры для работы процессора
     */
    public abstract boolean checkConditionsToProcess(final Map<ProcessorParamName, Object> params);

    /**
     * Запуск процессора для выполнения действий по обработке исключительной ситуации
     * @param params параметры для работы процессора
     */
    public abstract void processException(final Map<ProcessorParamName, Object> params);

    /**
     * Проверка сотрудника на активность(не уволен, не выключен, ...)
     */
    protected boolean isEmployeeActive(NodeRef employeeNodeRef) {
        final Boolean isActive = (Boolean) nodeService.getProperty(employeeNodeRef, BaseBean.IS_ACTIVE);
        final String login = (String) nodeService.getProperty(employeeNodeRef, OrgstructureBean.PROP_EMPLOYEE_PERSON_LOGIN);
        final NodeRef primaryStaff = orgstructureService.getEmployeePrimaryStaff(employeeNodeRef);
        NodeRef position = null;
        if (primaryStaff != null) {
            position = orgstructureService.getPositionByStaff(primaryStaff);
        }
        return nodeService.exists(employeeNodeRef) // сотрудник существует
                && (isActive != null && isActive) // сотрудник активный
                && (position != null) // у сотрудника указана основная должность
                && (login != null && !login.isEmpty()) // логин сотрудника не пустой
                && personService.isEnabled(login) // пользователь не выключен
                && !absenceService.isEmployeeAbsentToday(employeeNodeRef);//пользоавтель не отсутвует
    }

    protected NodeRef getAdminEmployee() {
        NodeRef adminEmployee = null;
        final String adminUserName = AuthenticationUtil.getAdminUserName();
        if (adminUserName != null) {
            final NodeRef adminPerson = personService.getPerson(adminUserName);
            if (adminPerson != null) {
                adminEmployee = orgstructureService.getEmployeeByPerson(adminPerson);
            }
        }
        return adminEmployee;
    }

    /**
     * Добавление в список получателей автора поручения
     */
    protected void addAuthorToRecipients(Set<NodeRef> recipients, NodeRef errandNodeRef) {
        List<NodeRef> authors = lecmPermissionService.getEmployeesByDynamicRole(errandNodeRef, EDSDocumentService.DYNAMIC_ROLE_CODE_INITIATOR);
        if (authors != null && authors.size() > 0) {
            recipients.addAll(authors);
        }
    }

    public enum ProcessorParamName {
        PERIODICAL_ERRAND,
        ERRAND
    }
}
