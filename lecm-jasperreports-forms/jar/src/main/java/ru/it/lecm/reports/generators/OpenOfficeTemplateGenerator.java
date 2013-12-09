package ru.it.lecm.reports.generators;

import com.sun.star.beans.*;
import com.sun.star.container.XNameAccess;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.document.XDocumentProperties;
import com.sun.star.document.XDocumentPropertiesSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.io.IOException;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.table.XCell;
import com.sun.star.text.XText;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTablesSupplier;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.DateTime;
import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Генератор шаблонов документов для OpenOffice-отчётов: формируется документ с
 * параметрами, которые соответствуют строке набора данных отчёта
 * (DSReportDesc.columns).
 *
 * @author rabdullin
 */
public class OpenOfficeTemplateGenerator {

    private static final String FILTERTAG_FOR_STAR_OFFICE_XML_WRITER = "StarOffice XML (Writer)";

    private static final String FILTERTAG_FOR_RTF = "Rich Text Format";
    private static final String FILTERTAG_FOR_DOC = "MS Word 97";

    private static final String DD_MM_YYYY = "dd.MM.yyyy";

    /**
     * Флажок для свойства документа, который только и позволит сохранить
     * динамически добавленное свойство в документе на диске из всей кучи
     * атрибутов com.sun.star.beans.PropertyAttribute.* надо оставить
     * только 128
     */
    final static public short DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE = PropertyAttribute.REMOVEABLE; // 128;

    private static final Logger logger = LoggerFactory.getLogger(OpenOfficeTemplateGenerator.class);

    private final BasicDataSource dataSource;
    final private OpenOfficeConnection connection;

    /**
     * активный описатель отчёта
     */
    public OpenOfficeTemplateGenerator(OpenOfficeConnection connection, BasicDataSource dateSource) {
        super();
        this.dataSource = dateSource;
        this.connection = connection;
    }

    /**
     * Открыть указанный openOffice-файл
     *
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static XComponent openDoc(XComponentLoader desktop, String srcUrl) throws IOException, IllegalArgumentException {
        /* (1) Open the document */
        final int searchFlags = 0;
        return desktop.loadComponentFromURL(
                srcUrl,
                "_blank",
                searchFlags,
                new PropertyValue[]{newPropertyValue("Hidden", Boolean.TRUE)});
    }

    /**
     * Сохранить указанный документ по именем
     *
     * @throws IOException
     */
    public static com.sun.star.frame.XStorable saveDocAs(final XComponent xCompDoc, final String destUrl) throws IOException {
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

    public static void closeDoc(final XComponent xCompDoc) throws CloseVetoException {
        // Closing the converted document. Use XCloseable.close if the
        // interface is supported, otherwise use XComponent.dispose
        com.sun.star.util.XCloseable xCloseable =
                UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class, xCompDoc);
        if (xCloseable != null) {
            xCloseable.close(false);
        } else {
            com.sun.star.lang.XComponent xComp =
                    UnoRuntime.queryInterface(com.sun.star.lang.XComponent.class, xCompDoc);
            xComp.dispose();
        }
    }

    /**
     * Добавить в указанный поток описания колонок
     *
     * @param connection    соединение для openOffice
     * @param desc          описатель шаблона отчёта
     * @param srcOODocUrl   исходный файл с документом для openOffice (обычно с
     *                      расширением ".odt")
     * @param destSaveAsUrl целевой файл (*.odt) для сохранения под другим именем, или
     *                      null, если сохранить надо под прежним именем.
     * @param author        если не null, то автор, которого надо прописать.
     */
    public void odtAddColumnsAsDocCustomProps(OpenOfficeConnection connection,
                                              ReportDescriptor desc, String srcOODocUrl, String destSaveAsUrl,
                                              String author) {
        final boolean needSaveAs = !Utils.isStringEmpty(destSaveAsUrl);
        logger.debug(String.format(
                "\n\t add DS columns into openOffice document '%s' %s"
                , srcOODocUrl
                , (needSaveAs ? String.format("\n\t as '%s'", destSaveAsUrl) : "")
        ));

        PropertyCheck.mandatory(this, "connection", connection);
        PropertyCheck.mandatory(this, "templateUrl", srcOODocUrl);
        PropertyCheck.mandatory(this, "reportDesc", desc);
        PropertyCheck.mandatory(this, "dataSource", desc.getDsDescriptor());
        PropertyCheck.mandatory(this, "columns", desc.getDsDescriptor().getColumns());

        String stage = "Create Desktop";
        try {
            final XComponentLoader xLoaderDesktop = connection.getDesktop();

			/* (1) Open the document */
            stage = String.format("Opening openOffice document '%s'", srcOODocUrl);
            final XComponent xCompDoc = openDoc(xLoaderDesktop, srcOODocUrl);

			/* (2) добавление свойств */
            stage = String.format("Get openOffice properties of document '%s'", srcOODocUrl);
            final XDocumentPropertiesSupplier xDocPropsSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xCompDoc);
            final XDocumentProperties xDocProps = xDocPropsSuppl.getDocumentProperties();
            final XPropertyContainer userPropsContainer = xDocProps.getUserDefinedProperties();

            if (author != null) {
                xDocProps.setAuthor(author);
            }
            //основные колонки - не подотчеты!
            if (!desc.isSQLDataSource()) {
                for (ColumnDescriptor col : desc.getDsDescriptor().getColumns()) {
                    stage = String.format("Add property '%s' with expression '%s'", col.getColumnName(), col.getExpression());
                    if (col.getExpression() != null) {
                        if (!col.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {//если колонка - не подотчет
                            String value = SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + col.getColumnName() + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL;
                            userPropsContainer.addProperty(col.getColumnName(), DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, value);
                        }
                    }
                }
            } else {
                addPropsFromSQLToContainer(desc, userPropsContainer);
                //дополнительно добавляем поле с SQL запросом
                userPropsContainer.addProperty(desc.getMnem() + "-query",
                        DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, desc.getFlags().getText());
            }

            //обработаем подотчеты отдельно
            List<ReportDescriptor> subReportsList = desc.getSubreports();
            if (subReportsList != null) {
                for (ColumnDescriptor col : desc.getDsDescriptor().getColumns()) {
                    if (col.getExpression() != null) {
                        if (col.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {//если колонка - подотчет
                            String subReportCode = col.getColumnName();
                            for (ReportDescriptor subReportDescriptor : subReportsList) {
                                if (subReportDescriptor.getMnem().equals(subReportCode)) { // нашли нужный подотчет - добавляем его к параметрам
                                    userPropsContainer.addProperty(subReportCode,
                                            DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, col.getExpression());
                                    // вытаскиваем все его поля
                                    List<ColumnDescriptor> subReportColumns = subReportDescriptor.getDsDescriptor().getColumns();
                                    if (subReportColumns != null) { // вариант, когда подотчет не SQL
                                        for (ColumnDescriptor subReportColumn : subReportColumns) {
                                            String value = subReportCode + "." + subReportColumn.getColumnName();
                                            userPropsContainer.addProperty(value, DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE,
                                                    SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + value + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL);
                                        }
                                    }
                                    if (subReportDescriptor.isSQLDataSource()) {  // вариант, когда подотчет SQL
                                        addPropsFromSQLToContainer(subReportDescriptor, userPropsContainer);
                                        //дополнительно добавляем поле с SQL запросом
                                        userPropsContainer.addProperty(subReportDescriptor.getMnem() + "-query",
                                                DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, subReportDescriptor.getFlags().getText());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

			/* (3) Сохранение */
            final String docInfo = (needSaveAs) ? String.format("\n\t as '%s'", destSaveAsUrl) : "";
            stage = String.format("Saving openOffice document\n\t '%s' %s", srcOODocUrl, docInfo);
            final com.sun.star.frame.XStorable xStorable = saveDocAs(xCompDoc, destSaveAsUrl);
            if (xStorable != null) {
                logger.debug(String.format("\nDocument '%s' saved %s \n", srcOODocUrl, docInfo));
            }

			/* (4) Закрыть Документ */
            stage = String.format("Closing openOffice document '%s'", srcOODocUrl);
            closeDoc(xCompDoc);
        } catch (Throwable ex) {
            final String msg = String.format("fail at stage\n\t %s\n\t error %s", stage, ex.getMessage());
            logger.error(msg, ex);
            if (ex instanceof DisposedException) {
                throw (DisposedException) ex;
            }
            throw new RuntimeException(msg, ex);
        }
    }

    private void addPropsFromSQLToContainer(ReportDescriptor desc, XPropertyContainer userPropsContainer)
            throws PropertyExistException, IllegalTypeException, IllegalArgumentException {
        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            sqlConnection = dataSource.getConnection();
            String query = desc.getFlags().getText();
            statement = sqlConnection.prepareStatement(query);
            statement.setMaxRows(1);
            resultSet = statement.executeQuery();

            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);

                String value = SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + columnName + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL;
                userPropsContainer.addProperty(desc instanceof SubReportDescriptorImpl ? (desc.getMnem() + "." + columnName) : columnName, DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, value);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (sqlConnection != null) {
                try {
                    sqlConnection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * Задать свойства для атрибутов документа
     *
     * @param props             задаваемые значения (ключи - имена атрибутов). Акутальные значения для обычных провайдеров, дефолтные - для SQL
     * @param requestParameters список параметров из запроса
     * @param author            автор изменений
     */
    public void odtSetColumnsAsDocCustomProps(Map<String, Object> props, Map<String, Object> requestParameters, ReportDescriptor desc,
                                              String srcOODocUrl, String destSaveAsUrl, String author) {

        final boolean needSaveAs = !Utils.isStringEmpty(destSaveAsUrl);

        PropertyCheck.mandatory(this, "connection", connection);
        PropertyCheck.mandatory(this, "basicDataSource", dataSource);
        PropertyCheck.mandatory(this, "templateUrl", srcOODocUrl);
        PropertyCheck.mandatory(this, "reportDesc", desc);

        // авто-соединение
        String stage = "Create Desktop";
        try {
            final XComponentLoader xLoaderDesktop = connection.getDesktop();

			/* (1) Open the document */
            stage = String.format("opening openOffice document '%s'", srcOODocUrl);
            final XComponent xCompDoc = openDoc(xLoaderDesktop, srcOODocUrl);

            /* (2) обновление существующих свойств ... */
            stage = String.format("Get openOffice properties of document '%s'", srcOODocUrl);

            final XDocumentPropertiesSupplier xDocPropsSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xCompDoc);
            final XDocumentProperties xDocProps = xDocPropsSuppl.getDocumentProperties();

            final XDocumentInfoSupplier xDocInfoSuppl = UnoRuntime.queryInterface(XDocumentInfoSupplier.class, xCompDoc);
            final XDocumentInfo docInfo = xDocInfoSuppl.getDocumentInfo();
            final XPropertySet docProperties = UnoRuntime.queryInterface(XPropertySet.class, docInfo);
            final XPropertyContainer docPropertyContainer = UnoRuntime.queryInterface(XPropertyContainer.class, docInfo);

            if (author != null) {
                stage = String.format("Set openOffice property Author='%s'\n\t of document '%s'", author, srcOODocUrl);
                xDocProps.setAuthor(author);
                logger.debug(stage);
            }

            final StringBuilder sb = new StringBuilder("Update openOffice attributes list: ");
            boolean mustLog = false; // true, чтобы обязательно зажурналировать список присвоений из sb
            try {
                if (props != null) {
                    if (desc.isSQLDataSource()) { // для SQL провайдеров - получаем реальные значения вместо плейсхолдеров
                        getSQLPropsValue(desc, requestParameters, docProperties, props);
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
                                    assignTableProperty(xCompDoc, docProperties, propName, (List<Map>) propValue);
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
                } else {
                    sb.append("no row properties");
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

    private void getSQLPropsValue(ReportDescriptor desc, Map<String, Object> paramsToQuery, XPropertySet docProperties, Map<String, Object> propsToAssign) {
        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            sqlConnection = dataSource.getConnection();

            //1 Получаем Query - берем либо базовую, либо заполненную из параметров (если там есть такое свойство)
            String baseQuery = getQueryString(desc, paramsToQuery, docProperties);

            logger.debug("SQL query to execute: " +  baseQuery);

            //2 Выполняем запрос
            statement = sqlConnection.prepareStatement(baseQuery);
            statement.setMaxRows(1); // для office у нас может быть одно значение!
            resultSet = statement.executeQuery();

            //3 Получить актуальные значения. пропускаем подотчеты
            while (resultSet.next()) {
                for (String propName : propsToAssign.keySet()) { // в props список всех нужных полей со значениями по умолчанию
                    if (!(propsToAssign.get(propName) instanceof List)) {
                        Object value = resultSet.getObject(propName);
                        propsToAssign.put(propName, value); //подменяем на реальные значения!
                    }
                }
            }

            //обработаем подотчеты отдельно
            List<ReportDescriptor> subReportsList = desc.getSubreports();
            if (subReportsList != null && !subReportsList.isEmpty()) {
                for (ReportDescriptor subReport : subReportsList) {
                    propsToAssign.put(subReport.getMnem(), getSubList((SubReportDescriptorImpl) subReport, propsToAssign, docProperties));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (sqlConnection != null) {
                try {
                    sqlConnection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private List<Map> getSubList(SubReportDescriptorImpl subReport, Map<String, Object> propsToAssign, XPropertySet docProperties) {
        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        List<Map> result = new ArrayList<Map>();
        try {
            sqlConnection = dataSource.getConnection();

            //1 Получаем Query - берем либо базовую, либо заполненную из параметров (если там есть такое свойство)
            String baseQuery = getQueryString(subReport, propsToAssign, docProperties);

            logger.debug("SQL query to execute: " + baseQuery);

            //2 Выполняем запрос
            statement = sqlConnection.prepareStatement(baseQuery);
            resultSet = statement.executeQuery();

            //3 Получить актуальные значения.
            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> subObject = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object value = resultSet.getObject(columnName);
                    subObject.put(columnName, value);
                }
                result.add(subObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (sqlConnection != null) {
                try {
                    sqlConnection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    private String getQueryString(ReportDescriptor reportDesc, Map<String, Object> propsToAssign, XPropertySet docProperties) throws UnknownPropertyException, WrappedTargetException {
        String baseQuery = reportDesc.getFlags().getText();

        final boolean isQueryPresent = docProperties.getPropertySetInfo().hasPropertyByName(reportDesc.getMnem() + "-query");
        if (isQueryPresent) {
            final Object queryProp = docProperties.getPropertyValue(reportDesc.getMnem() + "-query");
            //параметризуем query
            baseQuery = insertParamsToQuery(queryProp.toString(), propsToAssign);
        }
        return baseQuery;
    }

    private String insertParamsToQuery(String baseQuery, Map<String, Object> requestParameters) {
        if (baseQuery != null && requestParameters != null) {
            for (String param : requestParameters.keySet()) {
                //TODO возможно придется добавить обработку по типам
                if (baseQuery.contains("$P{" + param + "}")) {
                    if (requestParameters.get(param) instanceof List) {
                        String resultedValue = "";
                        for (Object value : (List) requestParameters.get(param)) {
                            resultedValue += resultedValue +
                                    (resultedValue.length() > 0 ? "," : "") +
                                    (value instanceof String ?
                                            ("\'" + value + "\'") :
                                            String.valueOf(value));
                        }
                        if (resultedValue.length() > 0) {
                            baseQuery = baseQuery.replaceAll("\\$P\\{" + param + "\\}", resultedValue);
                        }
                    } else {
                        baseQuery = baseQuery.replaceAll("\\$P\\{" + param + "\\}",
                                (requestParameters.get(param) instanceof String ?
                                        ("\'" + requestParameters.get(param) + "\'") :
                                        String.valueOf(requestParameters.get(param))));
                    }
                }
            }
        }
        return baseQuery;
    }

    /**
     * Присвоение значения openOffice-атрибуту свойства с учётом его типа.
     *
     * @param propValue присваиваемое значение, конвертируется в целевой тип.
     * @throws UnknownPropertyException
     * @throws PropertyVetoException
     * @throws IllegalArgumentException
     * @throws WrappedTargetException
     */
    public static void assignTypedProperty(final XPropertySet docProperties, final String propName, final Object propValue)
            throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException {
        if (propValue == null) {
            docProperties.setPropertyValue(propName, "");
            return;
        }

        final String sPropValue = String.valueOf(propValue);

        // проверяем фактический тип аргумента ...
        final Property pi = docProperties.getPropertySetInfo().getPropertyByName(propName);
        com.sun.star.uno.Type t = pi.Type;
        // при присвоении строки будем выполнять конвертирование в целевой тип ...
        if (t.equals(Type.STRING)) {
            if (propValue instanceof java.util.Date) {
                // дату преобразуем в строку согласно формату
                DateFormat dFormat = new SimpleDateFormat(DD_MM_YYYY);
                String dateStr = dFormat.format(propValue);
                docProperties.setPropertyValue(propName, dateStr);
                return;
            }
            docProperties.setPropertyValue(propName, sPropValue);
            return;
        } else if (t.equals(Type.BOOLEAN)) {
            docProperties.setPropertyValue(propName, Boolean.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.BYTE)) {
            docProperties.setPropertyValue(propName, Byte.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.SHORT) || t.equals(Type.UNSIGNED_SHORT)) { // 2х байтный
            docProperties.setPropertyValue(propName, Short.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.LONG) || t.equals(Type.UNSIGNED_LONG)) { // 4х байтный
            docProperties.setPropertyValue(propName, Integer.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.HYPER) || t.equals(Type.UNSIGNED_HYPER)) { // 8и байтный
            docProperties.setPropertyValue(propName, Long.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.FLOAT)) {
            docProperties.setPropertyValue(propName, Float.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.DOUBLE)) {
            docProperties.setPropertyValue(propName, Double.valueOf(sPropValue.trim()));
            return;
        } else if (t.equals(Type.CHAR)) {
            final char ch = (sPropValue.length() == 0) ? '\00' : sPropValue.charAt(0);
            docProperties.setPropertyValue(propName, ch);
            return;
        }

        // здесь propValue уже не строки ...
        if (propValue instanceof java.util.Date) {
            // дату надо преобразовать в star-office-date
            final com.sun.star.util.DateTime ooDate = newDateTime((java.util.Date) propValue);
            docProperties.setPropertyValue(propName, ooDate);
            return;
        }

        // по-умолчанию - простое присвоение ...
        docProperties.setPropertyValue(propName, propValue);
    }

    public static void assignTableProperty(final XComponent xDoc, final XPropertySet docProps, final String propName, final List<Map> listObjects) {
        XTextTablesSupplier tablesSupplier = UnoRuntime.queryInterface(XTextTablesSupplier.class, xDoc);

        XTextTable xDocTable = TableManager.getTable(tablesSupplier, propName);

        int firstRowIndex = xDocTable.getRows().getCount() > 1 ? 1 : 0;

        String[] columnsExpressions = new String[xDocTable.getColumns().getCount()];

        for (int i = 0; i < xDocTable.getColumns().getCount(); i++) {
            String cellName = TableManager.getColumnName(i, firstRowIndex + 1); // нумерация имен начинается с 1, а не 0

            XCell cell = xDocTable.getCellByName(cellName);
            XText text = UnoRuntime.queryInterface(XText.class, cell);

            String cellText = text.getString();
            columnsExpressions[i] = cellText;
        }

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
                        String cellExpression = columnsExpressions[j];
                        if (cellExpression.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) &&
                                cellExpression.endsWith(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL)) {
                            // в строке должно находиться выражение вида: {SR_CODE.SR_COLUMN_CODE}
                            // SR_CODE - код подотчета, SR_COLUMN_CODE - код колонки в подотчете
                            String subColumnCode =
                                    cellExpression.
                                            replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").
                                            replace(propName + ".", "").
                                            replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");

                            Object valueToWrite = objMap.get(subColumnCode);
                            if (valueToWrite instanceof Date) {
                                DateFormat dFormat = new SimpleDateFormat(DD_MM_YYYY);
                                valueToWrite = dFormat.format(valueToWrite);
                            }
                            text.setString(valueToWrite != null ? String.valueOf(valueToWrite) : "");
                        } else {
                            text.setString(columnsExpressions[j]);
                        }
                    }
                }
            }
        }

        xDocTable.getRows().removeByIndex(firstRowIndex, 1); // удаляем строку, которую дублировали
    }

    /**
     * Преобразование даты в office-DateTime
     *
     * @return null, если d = null и преобразованную дату иначе (часовой пояс
     *         новой даты будет соот-ть часовому поясу d).
     */
    public static DateTime newDateTime(Date d) {
        if (d == null) {
            return null;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(d);

        final DateTime result = new DateTime();
        result.Year = (short) c.get(Calendar.YEAR);
        result.Month = (short) c.get(Calendar.MONTH);
        result.Day = (short) c.get(Calendar.DAY_OF_MONTH);

        result.Hours = (short) c.get(Calendar.HOUR_OF_DAY);
        result.Minutes = (short) c.get(Calendar.MINUTE);
        result.Seconds = (short) c.get(Calendar.SECOND);

        return result;
    }

    public static PropertyValue newPropertyValue(String propName, Object propVal) {
        final PropertyValue result = new PropertyValue();
        result.Name = propName;
        result.Value = propVal;
        return result;
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
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }
}
