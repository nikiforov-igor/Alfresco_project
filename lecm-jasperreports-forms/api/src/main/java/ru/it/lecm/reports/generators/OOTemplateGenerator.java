package ru.it.lecm.reports.generators;

import com.sun.star.beans.*;
import com.sun.star.document.XDocumentProperties;
import com.sun.star.document.XDocumentPropertiesSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.io.IOException;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.DateTime;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ColumnDescriptor;
import ru.it.lecm.reports.model.impl.JavaDataType;
import ru.it.lecm.reports.model.impl.ReportTemplate;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: dbashmakov
 * Date: 03.03.14
 * Time: 14:39
 */
public abstract class OOTemplateGenerator {

    protected static final Logger logger = LoggerFactory.getLogger(OOTemplateGenerator.class);

    final static protected String DD_MM_YYYY = "dd.MM.yyyy";

    /**
     * Флажок для свойства документа, который только и позволит сохранить
     * динамически добавленное свойство в документе на диске из всей кучи
     * атрибутов com.sun.star.beans.PropertyAttribute.* надо оставить
     * только 128
     */
    final static public short DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE = PropertyAttribute.REMOVEABLE; // 128;

    protected DataSource dataSource;
    protected OpenOfficeConnection connection;

    public OpenOfficeConnection getConnection() {
        return connection;
    }

    public void setConnection(OpenOfficeConnection connection) {
        this.connection = connection;
    }

    public abstract com.sun.star.frame.XStorable saveDocAs(final XComponent xCompDoc, final String destUrl) throws IOException;

    public abstract void odtSetColumnsAsDocCustomProps(JRDataSourceProvider jrDataProvider, Map<String, Object> requestParameters, ReportDescriptor desc,
                                                       String srcOODocUrl, String destSaveAsUrl, String author);

    public abstract void assignTableProperty(final XComponent xDoc, final XPropertySet docProps, final String propName, final List<Map> listObjects,
                                             final Map<String, Object> settingProps);

    /**
     * Открыть указанный openOffice-файл
     *
     * @throws IllegalArgumentException
     * @throws com.sun.star.io.IOException
     */
    public static XComponent openDoc(XComponentLoader desktop, String srcUrl) throws IOException, com.sun.star.lang.IllegalArgumentException {
        /* (1) Open the document */
        final int searchFlags = 0;
        return desktop.loadComponentFromURL(
                srcUrl,
                "_blank",
                searchFlags,
                new PropertyValue[]{newPropertyValue("Hidden", Boolean.TRUE)});
    }

    public static void closeDoc(final XComponent xCompDoc) throws CloseVetoException {
        // Closing the converted document. Use XCloseable.close if the
        // interface is supported, otherwise use XComponent.dispose
        com.sun.star.util.XCloseable xCloseable =
                (com.sun.star.util.XCloseable) UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class, xCompDoc);
        if (xCloseable != null) {
            xCloseable.close(false);
        } else {
            com.sun.star.lang.XComponent xComp =
                    (com.sun.star.lang.XComponent)UnoRuntime.queryInterface(com.sun.star.lang.XComponent.class, xCompDoc);
            xComp.dispose();
        }
    }


    /**
     * Добавить в указанный поток описания колонок
     *
     * @param connection    соединение для openOffice
     * @param desc          описатель шаблона отчёта
     * @param template      текущий шаблон, для которого происходит генерация
     * @param srcOODocUrl   исходный файл с документом для openOffice (обычно с
     *                      расширением ".odt")
     * @param destSaveAsUrl целевой файл (*.odt) для сохранения под другим именем, или
     *                      null, если сохранить надо под прежним именем.
     * @param author        если не null, то автор, которого надо прописать.
     */
    public void odtAddColumnsAsDocCustomProps(OpenOfficeConnection connection, JRDataSourceProvider dsProvider,
                                              ReportDescriptor desc, ReportTemplate template, String srcOODocUrl, String destSaveAsUrl,
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
            final XDocumentPropertiesSupplier xDocPropsSuppl = (XDocumentPropertiesSupplier)UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xCompDoc);
            final XDocumentProperties xDocProps = xDocPropsSuppl.getDocumentProperties();
            final XPropertyContainer userPropsContainer = xDocProps.getUserDefinedProperties();

            if (author != null) {
                xDocProps.setAuthor(author);
            }
            //основные колонки - не подотчеты!
            if (desc.isSQLDataSource()) {
                //дополнительно добавляем поле с SQL запросом
                userPropsContainer.addProperty(desc.getMnem() + "-query", DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, desc.getFlags().getText());
            }

            if (!desc.isSQLDataSource() || !desc.getFlags().isLoadColumnsFromSQLQuery()) {
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
                addPropsFromSQLToContainer(desc, dsProvider, userPropsContainer);
            }

            //обработаем подотчеты отдельно
            List<ReportDescriptor> subReportsList = desc.getSubreports();
            if (subReportsList != null) {
                for (ColumnDescriptor col : desc.getDsDescriptor().getColumns()) {
                    if (col.getExpression() != null) {
                        if (col.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {//если колонка - подотчет
                            String subReportCode = col.getColumnName();
                            for (ReportDescriptor subReportDescriptor : subReportsList) {
                                SubReportDescriptorImpl subReportWrapper = (SubReportDescriptorImpl) subReportDescriptor;
                                if (subReportWrapper.getParentTemplate() == null || subReportWrapper.getParentTemplate().getMnem().equals(template.getMnem())) {
                                    if (subReportDescriptor.getMnem().equals(subReportCode)) { // нашли нужный подотчет - добавляем его к параметрам
                                        userPropsContainer.addProperty(subReportCode, DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, col.getExpression());
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
                                            addPropsFromSQLToContainer(subReportDescriptor, dsProvider, userPropsContainer);
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

    private void addPropsFromSQLToContainer(ReportDescriptor desc, JRDataSourceProvider dsProvider, XPropertyContainer userPropsContainer)
            throws PropertyExistException, IllegalTypeException, com.sun.star.lang.IllegalArgumentException {
        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            if (dsProvider != null) {
                sqlConnection = ((SQLProvider) dsProvider).getConnection();
                String query = Utils.trimmed(desc.getFlags().getText());
                if (!Utils.isStringEmpty(query)) {
                    int indexWhere = query.toLowerCase().indexOf("where");
                    if (indexWhere > -1) {
                        query = query.substring(0, indexWhere); // обрезаем все после первого WHERE
                    }
                    statement = sqlConnection.prepareStatement(query);
                    statement.setMaxRows(1);
                    resultSet = statement.executeQuery();

                    int columnCount = resultSet.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = resultSet.getMetaData().getColumnName(i);

                        String value = SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + columnName + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL;
                        userPropsContainer.addProperty(desc.isSubReport() ? (desc.getMnem() + "." + columnName) : columnName, DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, value);
                    }
                }
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

    protected void getSQLPropsValue(ReportDescriptor desc, Connection sqlConnection, Map<String, Object> paramsToQuery, XPropertySet docProperties, Map<String, Object> propsToAssign) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            //1 Получаем Query - берем либо базовую, либо заполненную из параметров (если там есть такое свойство)
            String baseQuery = getQueryString(desc, paramsToQuery, docProperties);

            logger.debug("SQL query to execute: " + baseQuery);

            //2 Выполняем запрос
            statement = sqlConnection.prepareStatement(baseQuery);
            statement.setMaxRows(1); // для office у нас может быть одно значение!
            resultSet = statement.executeQuery();

            // по умолчанию - названия столбцов из запроса
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object value = resultSet.getObject(columnName);
                    propsToAssign.put(columnName, value);
                }
            }

            /* + из колонок берем подотчеты... */
            for (ColumnDescriptor colDesc : desc.getDsDescriptor().getColumns()) {
                if (colDesc.getExpression() != null && colDesc.getExpression().matches(SubreportBuilder.REGEXP_SUBREPORTLINK)) {
                    propsToAssign.put(colDesc.getColumnName(), new ArrayList<Map>());
                }
            }
            //обработаем подотчеты
            List<ReportDescriptor> subReportsList = desc.getSubreports();
            if (subReportsList != null && !subReportsList.isEmpty()) {
                for (ReportDescriptor subReport : subReportsList) {
                    propsToAssign.put(subReport.getMnem(), getSubList((SubReportDescriptorImpl) subReport, sqlConnection, propsToAssign, docProperties));
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

    protected List<Map> getSubList(SubReportDescriptorImpl subReport, Connection sqlConnection, Map<String, Object> propsToAssign, XPropertySet docProperties) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        List<Map> result = new ArrayList<>();
        try {
            //1 Получаем Query - берем либо базовую, либо заполненную из параметров (если там есть такое свойство)
            String baseQuery = getQueryString(subReport, propsToAssign, docProperties);

            logger.debug("SQL query to execute: " + baseQuery);

            //2 Выполняем запрос
            statement = sqlConnection.prepareStatement(baseQuery);
            resultSet = statement.executeQuery();

            //3 Получить актуальные значения.
            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> subObject = new HashMap<>();
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
        if (docProperties != null) {
            final boolean isQueryPresent = docProperties.getPropertySetInfo().hasPropertyByName(reportDesc.getMnem() + "-query");
            if (isQueryPresent) {
                final Object queryProp = docProperties.getPropertyValue(reportDesc.getMnem() + "-query");
                //параметризуем query
                baseQuery = queryProp.toString();
            }
        }
        return insertParamsToQuery(baseQuery, propsToAssign);
    }

    private String insertParamsToQuery(String baseQuery, Map<String, Object> requestParameters) {
        if (baseQuery != null && requestParameters != null) {
            for (String param : requestParameters.keySet()) {
                if (baseQuery.contains("$P{" + param + "}")) {
                    JavaDataType.SupportedTypes type;
                    Object paramValue = requestParameters.get(param);
                    if (!(paramValue instanceof List)) {
                        type = paramValue != null ?
                                JavaDataType.SupportedTypes.findType(paramValue.getClass().getName()) :
                                JavaDataType.SupportedTypes.NULL;
                    } else {
                        type = JavaDataType.SupportedTypes.LIST;
                    }

                    if (type != null) {
                        baseQuery = baseQuery.replaceAll("\\$P\\{" + param + "\\}", type.getSQLPreparedValue(requestParameters.get(param)));
                    }
                }
            }
            baseQuery = baseQuery.replaceAll("\\$P\\{.*\\}","NULL"); // заменяем все пустые параметры
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
            throws UnknownPropertyException, PropertyVetoException, com.sun.star.lang.IllegalArgumentException, WrappedTargetException {
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

    /**
     * Преобразование даты в office-DateTime
     *
     * @return null, если d = null и преобразованную дату иначе (часовой пояс
     * новой даты будет соот-ть часовому поясу d).
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
}
