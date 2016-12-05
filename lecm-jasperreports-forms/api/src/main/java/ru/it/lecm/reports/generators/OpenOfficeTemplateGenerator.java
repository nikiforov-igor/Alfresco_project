package ru.it.lecm.reports.generators;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertyContainer;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.document.XDocumentProperties;
import com.sun.star.document.XDocumentPropertiesSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.io.IOException;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.XComponent;
import com.sun.star.table.XCell;
import com.sun.star.text.XText;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTablesSupplier;
import com.sun.star.uno.UnoRuntime;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import org.alfresco.util.PropertyCheck;
import org.jsoup.Jsoup;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.JavaDataType;
import ru.it.lecm.reports.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Генератор шаблонов документов для OpenOffice-отчётов: формируется документ с
 * параметрами, которые соответствуют строке набора данных отчёта
 * (DSReportDesc.columns).
 *
 * @author rabdullin
 */
public class OpenOfficeTemplateGenerator extends OOTemplateGenerator {

    private static final String FILTERTAG_FOR_STAR_OFFICE_XML_WRITER = "StarOffice XML (Writer)";
    private static final String FILTERTAG_FOR_RTF = "Rich Text Format";
    private static final String FILTERTAG_FOR_DOC = "MS Word 97";

    /**
     * Сохранить указанный документ по именем
     *
     * @throws IOException
     */
    public com.sun.star.frame.XStorable saveDocAs(final XComponent xCompDoc, final String destUrl) throws IOException {
        // автоматически определим формат rtf по расширению ...
        final boolean isRtf = (destUrl != null) && destUrl.endsWith(".rtf");
        final boolean isDoc = (destUrl != null) && (destUrl.endsWith(".doc") || destUrl.endsWith(".docx"));
        // сохранение ...
        com.sun.star.frame.XStorable resultStorable = null;
        if (xCompDoc != null) {
            resultStorable = UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, xCompDoc);

            final PropertyValue[] storeProps = new PropertyValue[2];
            storeProps[0] = newPropertyValue("Overwrite", Boolean.TRUE);
            String filterName = (isRtf ? FILTERTAG_FOR_RTF : (isDoc ? FILTERTAG_FOR_DOC : FILTERTAG_FOR_STAR_OFFICE_XML_WRITER));
            storeProps[1] = newPropertyValue("FilterName", filterName);

            if (destUrl != null) {
                resultStorable.storeAsURL(destUrl, storeProps);
            } else {
                resultStorable.store();
            }
        }
        return resultStorable;
    }

    /**
     * Задать свойства для атрибутов документа
     *
     * @param requestParameters список параметров из запроса
     * @param author            автор изменений
     */
    public void odtSetColumnsAsDocCustomProps(JRDataSourceProvider jrDataProvider, Map<String, Object> requestParameters, ReportDescriptor desc,
                                              String srcOODocUrl, String destSaveAsUrl, String author) {

        final boolean needSaveAs = !Utils.isStringEmpty(destSaveAsUrl);

        PropertyCheck.mandatory(this, "connection", getConnection());
        PropertyCheck.mandatory(this, "templateUrl", srcOODocUrl);
        PropertyCheck.mandatory(this, "reportDesc", desc);

        // авто-соединение
        String stage = "Create Desktop";
        try {
            JRDataSource dataSource = jrDataProvider.create(null);
            final XComponentLoader xLoaderDesktop = getConnection().getDesktop();

			/* (1) Open the document */
            stage = String.format("opening openOffice document '%s'", srcOODocUrl);
            final XComponent xCompDoc = openDoc(xLoaderDesktop, srcOODocUrl);

            /* (2) обновление существующих свойств ... */
            stage = String.format("Get openOffice properties of document '%s'", srcOODocUrl);

            final XDocumentPropertiesSupplier xDocPropsSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xCompDoc);
            final XDocumentProperties xDocProps = xDocPropsSuppl.getDocumentProperties();
            final XPropertyContainer userProperties = xDocProps.getUserDefinedProperties();

            final XPropertySet docProperties = UnoRuntime.queryInterface(XPropertySet.class, userProperties);
            final XPropertyContainer docPropertyContainer = UnoRuntime.queryInterface(XPropertyContainer.class, docProperties);

            if (author != null) {
                stage = String.format("Set openOffice property Author='%s'\n\t of document '%s'", author, srcOODocUrl);
                xDocProps.setAuthor(author);
                logger.debug(stage);
            }

            final StringBuilder sb = new StringBuilder("Update openOffice attributes list: ");
            boolean mustLog = false; // true, чтобы обязательно зажурналировать список присвоений из sb
            try {
                // атрибуты одной строки НД, которые надо будет присвоить параметрам документа
                final Map<String, Object> props = new HashMap<>();

                // формируем значения
                if (!(jrDataProvider instanceof SQLProvider)) {
                    dataSource.next();
                    // по умолчанию - expressions
                    for (ColumnDescriptor colDesc : desc.getDsDescriptor().getColumns()) {
                        Object value = dataSource.getFieldValue(DataFieldColumn.createDataField(colDesc));
                        if (value == null && colDesc.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {
                            // пустой подотчет - вместо null подсовываем пустой список
                            value = new ArrayList();
                        }

                        JavaDataType.SupportedTypes type = JavaDataType.SupportedTypes.findType(colDesc.getClassName());
                        if (type != null && value != null) {
                            if (type.equals(JavaDataType.SupportedTypes.HTML)) {
                                value = Jsoup.parse(value.toString()).text();
                            }
                        }
                        props.put(colDesc.getColumnName(), value);
                    }
                } else {
                    getSQLPropsValue(desc, ((SQLProvider)jrDataProvider).getConnection(), requestParameters, docProperties, props);
                }

                //тут props точно заполнены для любого провайдера - передаем их в документ!
                int i = 0;
                for (Map.Entry<String, Object> item : props.entrySet()) {
                    final String propName = item.getKey();
                    final Object propValue = item.getValue();
                    try {
                        final boolean isPresent = docProperties.getPropertySetInfo().hasPropertyByName(propName);
                        i++;
                        sb.append(String.format("\n %s [%s]\t'%s' = '%s'", (isPresent ? "set" : "add"), i, propName, Utils.coalesce(propValue, "NULL")));
                        if (isPresent) {
                            if (!(propValue instanceof List)) {
                                assignTypedProperty(docProperties, propName, propValue);
                            } else { // значение список - отрабатываем его как таблицу
                                assignTableProperty(xCompDoc, docProperties, propName, (List<Map>) propValue, null);
                            }
                        } else {
                            docPropertyContainer.addProperty(propName, DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, propValue);
                        }
                    } catch (Throwable t) {
                        /*
                         * это не страшно: например, сюда падаем с com.sun.star.lang.IllegalArgumentException
                         * при присвоении типизированному свойству значения NULL (например, для Дат)
                         */
                        mustLog = true;
                        logger.warn(String.format("\n [%s]\t'%s' = '%s'", i, propName, propValue), t);
                        sb.append(String.format("\n\t (!) error %s", t.getMessage()));
                    }

                }
            } finally {
                if (logger.isDebugEnabled() || mustLog) {
                    if (mustLog)
                        logger.warn(sb.toString());
                    else
                        logger.debug(sb.toString());
                }
            }

            /* (3) Сохранение */
            final String docInfoStr = (needSaveAs) ? String.format("\n\t as '%s'", destSaveAsUrl) : "";
            stage = String.format("saving openOffice document\n\t '%s' %s", srcOODocUrl, docInfoStr);

            final com.sun.star.frame.XStorable xStorable = saveDocAs(xCompDoc, destSaveAsUrl);

            if (xStorable != null) {
                logger.debug(String.format("\nDocument '%s' saved %s \n", srcOODocUrl, docInfoStr));
            }

			/* (4) Закрыть Документ */
            stage = String.format("closing openOffice document '%s'", srcOODocUrl);
            closeDoc(xCompDoc);

            stage = null;
        } catch (Throwable ex) {
            final String msg = String.format("fail at stage\n\t %s\n\t error %s", stage, ex.getMessage());
            logger.error(msg, ex);
            if (ex instanceof DisposedException) {
                throw (DisposedException) ex;
            }
            throw new RuntimeException(msg, ex);
        }
    }

    @Override
    public void assignTableProperty(final XComponent xDoc, final XPropertySet docProps, final String propName, final List<Map> listObjects, final Map<String, Object> settingProps) {
        XTextTablesSupplier tablesSupplier = UnoRuntime.queryInterface(XTextTablesSupplier.class, xDoc);

        XTextTable xDocTable = TableManager.getTable(tablesSupplier, propName);
        if (xDocTable != null) {
            int firstRowIndex = xDocTable.getRows().getCount() > 1 ? 1 : 0;

            String[] columnsExpressions = new String[xDocTable.getColumns().getCount()];

            for (int i = 0; i < xDocTable.getColumns().getCount(); i++) {
                String cellName = TableManager.getColumnName(i, firstRowIndex + 1); // нумерация имен начинается с 1, а не 0

                XCell cell = xDocTable.getCellByName(cellName);
                XText text = UnoRuntime.queryInterface(XText.class, cell);

                String cellText = text.getString();
                columnsExpressions[i] = cellText;
            }

            Pattern p = Pattern.compile("[{]([^}]+)[}]");

            int rowIndex = firstRowIndex + 1;
            for (Object listObject : listObjects) {
                rowIndex++;
                if (listObject != null) {
                    if (listObject instanceof Map) {
                        Map<String, Object> objMap = (Map<String, Object>) listObject;

                        //добавляем строку
                        xDocTable.getRows().insertByIndex(xDocTable.getRows().getCount(), 1);

                        //заполняем её данными из объекта
                        for (int j = 0; j < xDocTable.getColumns().getCount(); j++) {
                            String cellName = TableManager.getColumnName(j, rowIndex);
                            XCell cell = xDocTable.getCellByName(cellName);
                            XText text = UnoRuntime.queryInterface(XText.class, cell);

                            // в строке должно находиться выражение вида: {SR_CODE.SR_COLUMN_CODE}
                            // SR_CODE - код подотчета, SR_COLUMN_CODE - код колонки в подотчете
                            String cellExpression = columnsExpressions[j];
                            Matcher m = p.matcher(cellExpression);

                            while (m.find()) {
                                String subExpression = m.group();
                                String subColumnCode =
                                        subExpression.
                                                replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").
                                                replace(propName + ".", "").
                                                replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");

                                Object valueToWrite = objMap.get(subColumnCode);
                                if (valueToWrite instanceof Date) {
                                    DateFormat dFormat = new SimpleDateFormat(DD_MM_YYYY);
                                    valueToWrite = dFormat.format(valueToWrite);
                                }
                                String replacedValue = valueToWrite != null ? String.valueOf(valueToWrite) : "";
                                if (!replacedValue.equals(subExpression)) {
                                    cellExpression = cellExpression.replace(subExpression, replacedValue);
                                }
                            }
                            text.setString(cellExpression);
                        }
                    }
                }
            }

            xDocTable.getRows().removeByIndex(firstRowIndex, 1); // удаляем строку, которую дублировали
        }
    }

    protected static class TableManager {
        protected final static char FIRST_COLUMN_NAME = 'A';

        protected static String getColumnName(int col_index, int rowIndex) {
            return String.valueOf((char) (TableManager.FIRST_COLUMN_NAME + col_index)) + rowIndex;
        }

        protected static XTextTable getTable(XTextTablesSupplier tablesSupplier, String tableName) {
            XNameAccess xNamedTables = tablesSupplier.getTextTables();
            Object table;
            try {
                table = xNamedTables.getByName(tableName);
                return UnoRuntime.queryInterface(XTextTable.class, table);
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
            return null;
        }
    }
}
