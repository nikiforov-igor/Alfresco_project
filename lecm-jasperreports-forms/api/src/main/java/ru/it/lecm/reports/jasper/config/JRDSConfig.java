package ru.it.lecm.reports.jasper.config;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRField;


/**
 * Конфигурация для JasperReports DataSource.
 * В сущности два момента:
 *   1) jasper-описания колонок данных
 *   2) простые параметры (имя и значение)
 * @author rabdullin
 */
public interface JRDSConfig {

	/**
	 * @return описания полей набора, которые используются в jrxml-файле
	 */
	List<? extends JRField> getArgMetaFeilds();

	/**
	 * @return простые аргументы имя-значение (имя пользователя, пароль, url и пр)
	 */
	Map<String, Object> getArgs();
}
