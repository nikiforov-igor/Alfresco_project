package ru.it.lecm.reports.api;

import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.model.impl.ReportTemplate;

import java.io.IOException;
import java.util.Map;

public interface ReportGenerator {

    /**
     * Построить отчёт по его мнемоническому названию и параметрам
     *
     * @param reportsManager     менеджер отчетов
     * @param reportDesc         описатель отчёта (null, если нет описателя - "hardcoded report")
     * @param templateDescriptor шаблон отчета
     * @param parameters         параметры (обычно это request-параметры).
     *                           подразумевается что названия параметров в этом списке совпадают с мнемоникой
     *                           соот-щих колонок набора данных, который соот-ет шаблону reportName.
     */
    ReportFileData produceReport(ReportsManager reportsManager, ReportDescriptor reportDesc, ReportTemplate templateDescriptor, Map<String, Object> parameters) throws IOException;

    /**
     * Вызывается менеджером при получении нового шаблона отчёта, чтобы провайдер
     * среагировал (например, успел построить .jasper для .jrxml)
     *
     * @param desc         (!) зарегеный описатель
     * @param templateData данные шаблона
     * @param storage      хранилище, определённое для отчёта (в котором сами данные
     *                     templateData уже будут сохранены к моменту вызова).
     *                     <p/>
     *                     Если хранилище допускает запись, то её можно выполнить примерно так:<br/>
     *                     <code>
     *                     <br/> String fileName = ... формирование названия файла ...;
     *                     <br/> byte[] fileRawData = ... формирование данных для записи ...;
     *                     <br/> IdRContent id = IdRContent.createId( desc, fileName);
     *                     <br/> // непосредственное сохранение в хранилище
     *                     <br/> storage.storeContent(id, new ByteArrayInputStream(fileRawData));
     *                     </code>
     */
    void onRegister(ReportDescriptor desc, ReportTemplate template, byte[] templateData, ReportContentDAO storage) throws Exception;

    /**
     * Сгенерировать шаблон отчёта на основании макета шаблона
     *
     * @param maketData поток с данными макета шаблона
     * @param desc      описатель отчёта
     * @param template
     * @return данные готового шаблона отчёта (получить из них поток достаточно
     * просто, например:<br/>
     * <code>
     * byte[] data = g.generateReportTemplateByMaket(...); <br/>
     * ByteArrayOutputStream stm = new ByteArrayOutputStream( data);<br/>
     * </code>
     */
    byte[] generateReportTemplateByMaket(byte[] maketData, ReportDescriptor desc, ReportTemplate template);

    /**
     * Получить имя файла с шаблоном
     */
    String getTemplateFileName(ReportDescriptor desc, ReportTemplate template, String extension);
}
