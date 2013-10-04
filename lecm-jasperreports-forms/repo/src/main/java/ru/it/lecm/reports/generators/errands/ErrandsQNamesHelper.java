package ru.it.lecm.reports.generators.errands;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Здесь собраны использованные для отчётов атрибуты Поручений. 
 * @author rabdullin
 */
public class ErrandsQNamesHelper {

	/** namespace "Поручения" */
	final public static String NSURI_ERRANDS = "lecm-errands"; 

	/** type "Поручение" */
	final public static String TYPE_ERRANDS = "lecm-errands:document"; 

	/** assoc Исполнитель (1) */
	final public static String ASSOC_EXECUTOR = "lecm-errands:executor-assoc"; // default value for QN_ASSOC_REF

	/** assoc Соисполнители */
	final public static String ASSOC_COEXECUTORS = "lecm-errands:coexecutors-assoc";

	/** Дата выдачи поручения: date */
	final public static String FLD_START_DATE = "lecm-errands:start-date";

	/** Фактическая дата начала работы с поручением */
	final public static String FLD_START_WORK_DATE ="lecm-errands:work-start-date";

	/** Дата завершения поручения: date */
	final public static String FLD_END_DATE = "lecm-errands:end-date";

	/** Дата исполнения */
	final public static String FLD_EXEC_DATE = "lecm-errands:execution-date";

	/** Требуемый срок исполнения поручения: date */
	final public static String FLD_LIMIT_DATE = "lecm-errands:limitation-date";

	/** Признак того, что поручение было когда-либо отклонено: boolean, default=false */
	final public static String FLD_WAS_REJECTED ="lecm-errands:was-rejected";

	/** "Важность": boolean, default = false */
	final public static String FLD_IS_IMPORTANT = "lecm-errands:is-important";

	/** Просрочено: boolean, default=false */
	final public static String FLD_IS_EXPIRED = "lecm-errands:is-expired";

	/** Заголовок */
	final public static String FLD_TITLE = "lecm-errands:title";

	/** Статус */
	final public static String FLD_DOCSTATUS = "lecm-statemachine:status";

	/** type "Поручение" */
	final public QName QN_TYPE_ERRANDS; 

	/** Ассоциация для Исполнителей Поручения */
	final public QName QN_ASSOC_EXECUTOR;

	/** Ассоциация для Соисполнителей */
	final public QName QN_ASSOC_COEXECUTORS;

	/** Дата выдачи поручения: date */
	final public QName QNFLD_START_DATE;

	/** Фактическая дата начала работы с поручением */
	final public QName QNFLD_START_WORK_DATE;

	/** время создания объекта */
	final public QName QNFLD_CREATED;

	// final public QName QNFLD_CREATED;

	/** Дата завершения поручения: date */
	final public QName QNFLD_END_DATE;

	/** Требуемый срок исполнения поручения: date */
	final public QName QNFLD_LIMIT_DATE;

	/** Признак того, что поручение было когда-либо отклонено: boolean, default=false */
	final public QName QNFLD_WAS_REJECTED;

	/** "Важность": boolean, default = false */
	final public QName QNFLD_IS_IMPORTANT;

	/** Просрочено: boolean, default=false */
	final public QName QNFLD_IS_EXPIRED;

	/** Статус */
	final public QName QNFLD_DOCSTATUS;

	/** Заголовок */
	final public QName QNFLD_TITLE;

	final public NamespaceService ns;

	public ErrandsQNamesHelper(NamespaceService ns) {

		this.ns = ns;

		this.QN_TYPE_ERRANDS = QName.createQName(TYPE_ERRANDS, this.ns);
		this.QN_ASSOC_EXECUTOR = QName.createQName(ASSOC_EXECUTOR, this.ns);
		this.QN_ASSOC_COEXECUTORS = QName.createQName(ASSOC_COEXECUTORS, this.ns);

		this.QNFLD_START_DATE = QName.createQName(FLD_START_DATE, this.ns);
		this.QNFLD_START_WORK_DATE = QName.createQName(FLD_START_WORK_DATE, this.ns);
		this.QNFLD_CREATED =  QName.createQName("cm:created", this.ns);

		this.QNFLD_END_DATE = QName.createQName(FLD_END_DATE, this.ns);
		this.QNFLD_LIMIT_DATE = QName.createQName(FLD_LIMIT_DATE, this.ns);

		this.QNFLD_WAS_REJECTED = QName.createQName(FLD_WAS_REJECTED, this.ns);
		this.QNFLD_IS_IMPORTANT = QName.createQName(FLD_IS_IMPORTANT, this.ns);
		this.QNFLD_IS_EXPIRED = QName.createQName(FLD_IS_EXPIRED, this.ns);

		this.QNFLD_TITLE = QName.createQName(FLD_TITLE, this.ns);
		this.QNFLD_DOCSTATUS = QName.createQName(FLD_DOCSTATUS, this.ns);
	}

	/**
	 * Проверить установлен ли boolean-флажок в свойствах
	 * @param props набор свойств
	 * @param flagId имя свойства с флажком
	 * @return
	 */
	final public boolean isFlaged(Map<QName, Serializable> props, QName flagId) {
		return (props != null)
				&& props.containsKey(flagId) 
				&& Boolean.TRUE.equals(props.get(flagId));
	}

//	final boolean isFlaged(Map<QName, Serializable> props, String flag) {
//		return isFlaged( props, this.getFldQname(flag));
//	}

	final public boolean isПоручениеВажное(Map<QName, Serializable> props) {
		return isFlaged( props, QNFLD_IS_IMPORTANT);
	}

	final public boolean isПоручениеБылоОтклоненоБоссом(Map<QName, Serializable> props) {
		return isFlaged( props, QNFLD_WAS_REJECTED);
	}

	final public boolean isПоручениеИсполненоВСрок( Map<QName, Serializable> props) {
		return !isFlaged( props, QNFLD_IS_EXPIRED);
	}

	/**
	 * Проверить закрыто ли поручение - считаем закрытыми такие, у которых дата закрытия определена (не null).
	 * @param props
	 * @return
	 */
	final public boolean isПоручениеЗакрыто(Map<QName, Serializable> props) {
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
	 * @param props набор свойств Поручения, из которого выбирается start/end
	 * @return длительность (в мсек) или ноль, если не достаточно данных
	 */
	public long getВремяИсполнения_мсек(Map<QName, Serializable> props) {
		if (props == null)
			return 0;
		Date start = (Date) props.get(QNFLD_START_WORK_DATE);
		if (start == null) // если нет "work-start" взять "время создания" ("cm:created"?) 
			start = (Date) props.get(QNFLD_START_DATE); 
		if (start == null)
			start = (Date) props.get(QNFLD_CREATED);
		final Date end = (Date) props.get(QNFLD_END_DATE);
		if (start == null || end == null) // нельзя определить
			return 0; 
		return (end.getTime() - start.getTime());  
	}
}
