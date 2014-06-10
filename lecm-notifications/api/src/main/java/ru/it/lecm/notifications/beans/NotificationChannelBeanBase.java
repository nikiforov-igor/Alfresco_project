package ru.it.lecm.notifications.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: AIvkin Date: 16.01.13 Time: 17:35
 * <p/>
 * Интерфейс для сервисов каналов уведомлений
 */
public abstract class NotificationChannelBeanBase extends BaseBean {

    private static final Logger logger = LoggerFactory.getLogger(NotificationChannelBeanBase.class);

    protected OrgstructureBean orgstructureService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    /**
     * Отправка атомарного уведомления
     *
     * @param notification Атомарное уведомление
     * @return true - если отправка успешна, false - если при отправки возникли
     * ошибки
     */
    public abstract boolean sendNotification(NotificationUnit notification);

    /**
     * Метод, возвращает путь к папке в папке уведомлений
     * согласно заданным параметрам.
     *
     * @param employeeName - имя сотрудника
     * @param date - текущая дата. может быть null, тогда не используется
     * @return 
     */
    protected List<String> getDirectoryPath(String employeeName, Date date) {
        List<String> directoryPaths = new ArrayList<String>();
        if (employeeName != null) {
            directoryPaths.add(employeeName);
        }
        if (date != null) {
            directoryPaths.addAll(getDateFolderPath(date));
        }
        return directoryPaths;
    }
    
    /**
     * Метод, возвращающий ссылку на директорию пользователя в директории
     * "Уведомления/*root*" согласно заданным параметрам Если такой директории
     * нет, то она НЕ создаётся
     *
     * @param rootRef - корень
     * @return ссылка на директорию
     */
    protected NodeRef getCurrentEmployeeFolder(NodeRef rootRef) {
        NodeRef currentEmployeeNodeRef = orgstructureService.getCurrentEmployee();
        if (currentEmployeeNodeRef != null) {
            String employeeName = (String) nodeService.getProperty(currentEmployeeNodeRef, ContentModel.PROP_NAME);
            if (employeeName != null) {
                return nodeService.getChildByName(rootRef, ContentModel.ASSOC_CONTAINS, employeeName);
            }
        }
        return null;
    }

}
