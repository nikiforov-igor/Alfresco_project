package ru.it.lecm.errands.reports;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.errands.ErrandsService;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Здесь собраны использованные для отчётов атрибуты Поручений.
 *
 * @author rabdullin
 */
public class ErrandsQNamesHelper {
    /**
     * Дата выдачи поручения: date
     */
    final static public QName QNFLD_START_DATE = ErrandsService.PROP_ERRANDS_START_DATE;

    /**
     * Фактическая дата начала работы с поручением
     */
    final static public QName QNFLD_START_WORK_DATE = ErrandsService.PROP_ERRANDS_START_WORK_DATE;

    /**
     * время создания объекта
     */
    final static public QName QNFLD_CREATED = ContentModel.PROP_CREATED;

    /**
     * Дата завершения поручения: date
     */
    final static public QName QNFLD_END_DATE = ErrandsService.PROP_ERRANDS_END_DATE;

    /**
     * Признак того, что поручение было когда-либо отклонено: boolean, default=false
     */
    final static public QName QNFLD_WAS_REJECTED = ErrandsService.PROP_ERRANDS_IS_REJECTED;

    /**
     * "Важность": boolean, default = false
     */
    final static public QName QNFLD_IS_IMPORTANT = ErrandsService.PROP_ERRANDS_IS_IMPORTANT;

    /**
     * Просрочено: boolean, default=false
     */
    final static public QName QNFLD_IS_EXPIRED = ErrandsService.PROP_ERRANDS_IS_EXPIRED;

    final public NamespaceService ns;

    public ErrandsQNamesHelper(NamespaceService ns) {
        this.ns = ns;
    }

    /**
     * Проверить установлен ли boolean-флажок в свойствах
     *
     * @param props  набор свойств
     * @param flagId имя свойства с флажком
     */
    final public boolean isFlaged(Map<QName, Serializable> props, QName flagId) {
        return (props != null)
                && props.containsKey(flagId)
                && Boolean.TRUE.equals(props.get(flagId));
    }

    final public boolean isErrandImportant(Map<QName, Serializable> props) {
        return isFlaged(props, QNFLD_IS_IMPORTANT);
    }

    final public boolean isErrandsRejected(Map<QName, Serializable> props) {
        return isFlaged(props, QNFLD_WAS_REJECTED);
    }

    final public boolean isErrandExpired(Map<QName, Serializable> props) {
        return !isFlaged(props, QNFLD_IS_EXPIRED);
    }

    /**
     * Проверить закрыто ли поручение - считаем закрытыми такие, у которых дата закрытия определена (не null).
     *
     */
    final public boolean isErrandClosed(Map<QName, Serializable> props) {
        return props.get(QNFLD_END_DATE) != null;
    }

    /**
     * Получить нормальное qname по его строкову варианту ...
     */
    final public QName makeQN(String qname) {
        return (qname != null && qname.trim().length() > 0) ? QName.createQName(qname, this.ns) : null;
    }

    /**
     * Вычислить длительность исполнения Поручения в миллисекундах:
     * от "work-start-date" до "end-date"
     *
     * @param props набор свойств Поручения, из которого выбирается start/end
     * @return длительность (в мсек) или ноль, если не достаточно данных
     */
    public long getErrandExecutionTime(Map<QName, Serializable> props) {
        if (props == null) {
            return 0;
        }
        Object startValue = props.get(QNFLD_START_WORK_DATE);
        Date start;
        if (startValue == null) {
            // если нет "work-start" взять "время создания" ("cm:created"?)
            startValue = props.get(QNFLD_START_DATE);

            if (startValue == null) {
                startValue = props.get(QNFLD_CREATED);
            }
        }

        start = (Date) startValue;
        Date end;
        Object endValue = props.get(QNFLD_END_DATE);
        if (endValue == null) {
            // нельзя определить
            return 0;
        }
        end = (Date) endValue;
        return (end.getTime() - start.getTime());
    }
}
