package ru.it.lecm.reports.api;

import java.io.IOException;
import java.util.Map;

import org.springframework.extensions.webscripts.WebScriptResponse;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;

public interface ReportGenerator {

	/**
	 * Построить отчёт по его мнемоническому названию и параметрам
	 * @param webScriptResponse выходной ответ
	 * @param reportDesc описатель отчёта (null, если нет описателя - "hardcoded report")
	 * @param parameters параметры (обычно это request-параметры).
	 * подразумевается что названия параметров в этом списке совпадают с мнемоникой
	 * соот-щих колонок набора данных, который соот-ет шаблону reportName.
	 * Если это не так, тогда провайдер "сам" должен разбираться "что и куда"
	 * надо назначить.
	 * @param rptContent
	 * @throws IOException
	 */
	void produceReport( WebScriptResponse webScriptResponse
			, ReportDescriptor reportDesc
			, Map<String, String[]> parameters
			, ReportContentDAO rptContent
	) throws IOException;

	/**
	 * Вызывается менеджером при получении нового шаблона отчёта, чтобы провайдер 
	 * среагировал (например, успел построить .jasper для .jrxml)
	 * @param desc (!) зарегеный описатель
	 * @param templateData данные шаблона
	 * @param storage хранилище, определённое для отчёта (в котором сами данные 
	 * templateData уже будут сохранены к моменту вызова).
	 * <p/>
	 * Если хранилище допускает запись, то её можно выполнить примерно так:<br/>
	 * <code>
	 * 		<br/> String fileName = ... формирование названия файла ...;
	 * 		<br/> byte[] fileRawData = ... формирование данных для записи ...;
	 * 		<br/> IdRContent id = IdRContent.createId( desc, fileName);
	 * 		<br/> // непосредственное сохранение в хранилище
	 * 		<br/> storage.storeContent(id, new ByteArrayInputStream(fileRawData)); 
	 * </code>
	 */
	void onRegister(ReportDescriptor desc, byte[] templateData, ReportContentDAO storage);

}
