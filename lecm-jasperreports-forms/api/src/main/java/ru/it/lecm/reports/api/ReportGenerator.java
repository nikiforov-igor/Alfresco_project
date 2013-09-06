package ru.it.lecm.reports.api;

import java.io.IOException;
import java.util.Map;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;

public interface ReportGenerator {

	/**
	 * Построить отчёт по его мнемоническому названию и параметрам
	 * @param result выходной ответ
	 * @param reportDesc описатель отчёта (null, если нет описателя - "hardcoded report")
	 * @param parameters параметры (обычно это request-параметры).
	 * подразумевается что названия параметров в этом списке совпадают с мнемоникой
	 * соот-щих колонок набора данных, который соот-ет шаблону reportName.
	 * Если это не так, тогда провайдер "сам" должен разбираться "что и куда"
	 * надо назначить.
	 * @param rptContent
	 * @throws IOException
	 */
	void produceReport( ReportFileData result
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

	/**
	 * Сгенерировать шаблон отчёта на основании макета шаблона
	 * @param maketData поток с данными макета шаблона
	 * @param desc описатель отчёта
	 * @return данные готового шаблона отчёта (получить из них поток достаточно
	 * просто, например:<br/>
	 * <code>
	 * 		byte[] data = g.generateReportTemplateByMaket(...); <br/> 
	 * 		ByteArrayOutputStream stm = new ByteArrayOutputStream( data);<br/>
	 *	</code>
	 */
	byte[] generateReportTemplateByMaket(byte[] maketData, ReportDescriptor desc);


}
