package ru.it.lecm.reports.generators;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.document.XDocumentProperties;
import com.sun.star.document.XDocumentPropertiesSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.io.IOException;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.*;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.uno.UnoRuntime;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author snovikov
 */
public class OpenOfficeCalcTemplateGenerator extends OOTemplateGenerator {

    private static final String FILTERTAG_FOR_STAR_OFFICE_XML_CALC = "StarOffice XML (Calc)";
    private static final String FILTERTAG_FOR_EXCEL = "MS Excel 97";

    /**
     * Сохранить указанный документ под именем
     *
     * @throws IOException
     */
    public com.sun.star.frame.XStorable saveDocAs(final XComponent xCompDoc, final String destUrl) throws IOException {
        // автоматически определим формат по расширению ...
        final boolean isExcel = (destUrl != null) && (destUrl.endsWith(".xls") || destUrl.endsWith(".xlsx"));
        // сохранение ...
        com.sun.star.frame.XStorable resultStorable = null;
        if (xCompDoc != null) {
            resultStorable = UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, xCompDoc);

            final PropertyValue[] storeProps = new PropertyValue[2];
            storeProps[0] = newPropertyValue("Overwrite", Boolean.TRUE);
            String filterName = isExcel ? FILTERTAG_FOR_EXCEL : FILTERTAG_FOR_STAR_OFFICE_XML_CALC;
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
     * @param props             задаваемые значения (ключи - имена атрибутов). Акутальные значения для обычных провайдеров, дефолтные - для SQL
     * @param requestParameters список параметров из запроса
     * @param author            автор изменений
     */
    public void odtSetColumnsAsDocCustomProps(Map<String, Object> props, Map<String, Object> requestParameters, ReportDescriptor desc,
                                              String srcOODocUrl, String destSaveAsUrl, String author) {

        final boolean needSaveAs = !Utils.isStringEmpty(destSaveAsUrl);

        PropertyCheck.mandatory(this, "connection", getConnection());
        PropertyCheck.mandatory(this, "databaseHelper", getDatabaseHelper());
        PropertyCheck.mandatory(this, "templateUrl", srcOODocUrl);
        PropertyCheck.mandatory(this, "reportDesc", desc);

        // авто-соединение
        String stage = "Create Desktop";
        try {
            final XComponentLoader xLoaderDesktop = getConnection().getDesktop();

			/* (1) Open the document */
            stage = String.format("opening openOffice calc document '%s'", srcOODocUrl);
            final XComponent xCompDoc = openDoc(xLoaderDesktop, srcOODocUrl);

            XSpreadsheet xSpreadsheet = OpenOfficeCalcTemplateGenerator.SpreadsheetManager.getSpreadsheet(xCompDoc, 0);

            /* (2) обновление существующих свойств ... */
            stage = String.format("Get openOffice calc properties of document '%s'", srcOODocUrl);

            final XDocumentPropertiesSupplier xDocPropsSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xCompDoc);
            final XDocumentProperties xDocProps = xDocPropsSuppl.getDocumentProperties();

            final XDocumentInfoSupplier xDocInfoSuppl = UnoRuntime.queryInterface(XDocumentInfoSupplier.class, xCompDoc);
            final XDocumentInfo docInfo = xDocInfoSuppl.getDocumentInfo();
            final XPropertySet docProperties = UnoRuntime.queryInterface(XPropertySet.class, docInfo);

            if (author != null) {
                stage = String.format("Set openOffice property Author='%s'\n\t of document '%s'", author, srcOODocUrl);
                xDocProps.setAuthor(author);
                logger.debug(stage);
            }

            final StringBuilder sb = new StringBuilder("Update openOffice calc attributes list: ");
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
                                    //поместим значение переменной в свойства документа
                                    assignTypedProperty(docProperties, propName, propValue);
                                    //поместим значение переменной в соответсвующую ячейку
                                    XCellRange xCellRange = xSpreadsheet.getCellRangeByName(propName);
                                    XCell xCell = xCellRange.getCellByPosition(0, 0);
                                    xCell.setFormula(propValue.toString());
                                } else { // значение список - отрабатываем его как таблицу
                                    assignTableProperty(xCompDoc, docProperties, propName, (List<Map>) propValue);
                                }
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
            stage = String.format("saving openOffice calc document\n\t '%s' %s", srcOODocUrl, docInfoStr);

            final com.sun.star.frame.XStorable xStorable = saveDocAs(xCompDoc, destSaveAsUrl);

            if (xStorable != null) {
                logger.debug(String.format("\nDocument '%s' saved %s \n", srcOODocUrl, docInfoStr));
            }

			/* (4) Закрыть Документ */
            stage = String.format("closing openOffice calc document '%s'", srcOODocUrl);
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

    public void assignTableProperty(final XComponent xDoc, final XPropertySet docProps, final String propName, final List<Map> listObjects) {
        try {
            XSpreadsheet xSpreadsheet = OpenOfficeCalcTemplateGenerator.SpreadsheetManager.getSpreadsheet(xDoc, 0);
            XCellRangeMovement xMovement = UnoRuntime.queryInterface(XCellRangeMovement.class, xSpreadsheet);

            XCellRange namedRowCellRange = xSpreadsheet.getCellRangeByName(propName);
            XCellRangeAddressable namedRowCellRangeAddressable = UnoRuntime.queryInterface(XCellRangeAddressable.class, namedRowCellRange);

            XPropertySet xDocProp = UnoRuntime.queryInterface(XPropertySet.class, xDoc);
            Object aRangesObj = xDocProp.getPropertyValue("NamedRanges");
            XNamedRanges xNamedRanges = UnoRuntime.queryInterface(XNamedRanges.class, aRangesObj);

            CellRangeAddress namedRowCellRangeAddress = namedRowCellRangeAddressable.getRangeAddress();

            int startCol = namedRowCellRangeAddress.StartColumn;
            int endCol = namedRowCellRangeAddress.EndColumn;
            int startRow = namedRowCellRangeAddress.StartRow;
            int endRow = namedRowCellRangeAddress.EndRow;
            int tableColCount = endCol - startCol + 1;
            int expi = 0;
            String[] columnsExpressions = new String[tableColCount];
            for (int ic = startCol; ic <= endCol; ic++) {
                XCell titledCell = xSpreadsheet.getCellByPosition(ic, startRow);
                columnsExpressions[expi] = titledCell.getFormula();
                expi++;
            }

            int rowIndex = 0;
            for (Object listObject : listObjects) {
                if (listObject != null) {
                    if (listObject instanceof Map) {
                        Map<String, Object> objMap = (Map<String, Object>) listObject;

                        //добавляем строку
                        if (rowIndex > 0) {
                            XCellRange rowCellRange = xSpreadsheet.getCellRangeByPosition(startCol, startRow + rowIndex, endCol, startRow + rowIndex);
                            XCellRangeAddressable rowRangeAddr = UnoRuntime.queryInterface(XCellRangeAddressable.class, rowCellRange);
                            xMovement.insertCells(rowRangeAddr.getRangeAddress(), CellInsertMode.DOWN);
                        }

                        //заполняем её данными из объекта
                        for (int j = 0; j < tableColCount; j++) {
                            XCell cell = xSpreadsheet.getCellByPosition(startCol + j, startRow + rowIndex);
                            String cellExpression = columnsExpressions[j];
                            if (cellExpression.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) &&
                                    cellExpression.endsWith(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL)) {
                                // в строке должно находиться выражение вида: {SR_CODE.SR_COLUMN_CODE}
                                // SR_CODE - код подотчета, SR_COLUMN_CODE - код колонки в подотчете
                                String subRangeCode = //название range по столбцу
                                        cellExpression.
                                                replace(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL, "").
                                                replace(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, "");
                                if (xNamedRanges.hasByName(subRangeCode)) {
                                    //увеличим range столбца
                                    XCellRange colCellRange = xSpreadsheet.getCellRangeByPosition(startCol + j, startRow, startCol + j, startRow + rowIndex);
                                    XPropertySet colCellRangeProperties = UnoRuntime.queryInterface(XPropertySet.class, colCellRange);
                                    CellAddress startColRangePos = new CellAddress();
                                    startColRangePos.Sheet = 0;
                                    startColRangePos.Column = j;
                                    startColRangePos.Row = startRow;
                                    xNamedRanges.removeByName(subRangeCode); //удалим range со старыми размерами
                                    xNamedRanges.addNewByName(subRangeCode, colCellRangeProperties.getPropertyValue("AbsoluteName").toString(), startColRangePos, 0);
                                }

                                String subColumnCode = subRangeCode.replace(propName + ".", "");
                                Object valueToWrite = objMap.get(subColumnCode);
                                if (valueToWrite instanceof Date) {
                                    DateFormat dFormat = new SimpleDateFormat(DD_MM_YYYY);
                                    valueToWrite = dFormat.format(valueToWrite);
                                }
                                cell.setFormula(valueToWrite != null ? String.valueOf(valueToWrite) : "");
                            } else {
                                int rowNum = startRow + 1 + rowIndex;
                                String resultExp = cellExpression.replaceAll("\\{[^{}]*\\}", String.valueOf(rowNum));
                                cell.setFormula(resultExp);
                            }
                        }
                    }
                }
                rowIndex++;
            }
        } catch (Throwable t) {
            logger.warn(String.format("\n \t'%s'", propName), t);
        }
    }

    protected static class SpreadsheetManager {
        protected static XSpreadsheet getSpreadsheet(XComponent xDoc, Integer ind) {
            try {
                XSpreadsheetDocument xSpreadsheetDocument = UnoRuntime.queryInterface(XSpreadsheetDocument.class, xDoc);
                XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets();
                List<String> sheetNames = Arrays.asList(xSpreadsheets.getElementNames());
                Object sheet = xSpreadsheets.getByName(sheetNames.get(ind));
                return UnoRuntime.queryInterface(XSpreadsheet.class, sheet);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }
}
