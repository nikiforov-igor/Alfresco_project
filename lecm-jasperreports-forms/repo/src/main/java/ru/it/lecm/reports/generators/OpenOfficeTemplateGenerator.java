package ru.it.lecm.reports.generators;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.sf.jooreports.openoffice.connection.OpenOfficeConnection;

import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.utils.Utils;

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyAttribute;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertyContainer;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
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
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.DateTime;

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

	/**
	 * Флажок для свойства документа, который толко и позволит сохранить
	 * динамиечски добавленное свойство в документе на диске из всей кучи
	 * атрибутов com.sun.star.beans.PropertyAttribute.* надо оставлять только
	 * 128
	 */
	final static public short DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE = PropertyAttribute.REMOVEABLE; // 128;

	private static final Logger logger = LoggerFactory
			.getLogger(OpenOfficeTemplateGenerator.class);

	/** активный описатель отчёта */
	// private ReportDescriptor reportDesc;

	public OpenOfficeTemplateGenerator() {
		super();
	}

//	public OpenOfficeTemplateGenerator(ReportDescriptor reportDesc) {
//		super();
//		this.reportDesc = reportDesc;
//	}
//
//	/** активный описатель отчёта */
//	public ReportDescriptor getReportDesc() {
//		return reportDesc;
//	}
//
//	/** активный описатель отчёта */
//	public void setReportDesc(ReportDescriptor reportDesc) {
//		this.reportDesc = reportDesc;
//	}

	/**
	 * Открыть указанный openOffice-файл
	 * 
	 * @param desktop
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static XComponent openDoc(XComponentLoader desktop, String srcUrl)
			throws IOException, IllegalArgumentException {
		/* (1) Open the document */
		// stage = String.format( "opening openOffice document '%s'", srcOdtUrl);
		final int searchFlags = 0;
		final XComponent xCompDoc = desktop.loadComponentFromURL(
						srcUrl,
						"_blank",
						searchFlags,
						new PropertyValue[] { newPropertyValue("Hidden", Boolean.TRUE) });
		return xCompDoc;
	}

	/**
	 * Сохранить указанный документ по своим текущим именем
	 * 
	 * @param xCompDoc
	 * @return
	 * @throws IOException
	 */
	public static com.sun.star.frame.XStorable saveDoc(final XComponent xCompDoc)
			throws IOException {
		return saveDocAs(xCompDoc, null);
	}

	/**
	 * Сохранить указанный документ по именем
	 * 
	 * @param xCompDoc
	 * @param destUrl
	 * @return
	 * @throws IOException
	 */
	public static com.sun.star.frame.XStorable saveDocAs(
			final XComponent xCompDoc, final String destUrl
			) throws IOException
	{
		// автоматически определим формат rtf по расширению ...
		final boolean isRtf = (destUrl != null) && destUrl.endsWith(".rtf");

		// сохранение ...
		com.sun.star.frame.XStorable resultStorable = null;
		if (xCompDoc != null) {
			resultStorable = UnoRuntime.queryInterface( com.sun.star.frame.XStorable.class, xCompDoc);

			final PropertyValue[] storeProps = new PropertyValue[2];
			storeProps[0] = newPropertyValue("Overwrite", Boolean.TRUE);
			// storeProps[1] = newPropertyValue( "FilterName", "Rich Text Format");
			storeProps[1] = newPropertyValue( "FilterName", (isRtf ? FILTERTAG_FOR_RTF : FILTERTAG_FOR_STAR_OFFICE_XML_WRITER) );

			if (destUrl != null)
				resultStorable.storeAsURL(destUrl, storeProps);
			else
				resultStorable.store();
		}
		return resultStorable;
	}

	public static void closeDoc(final XComponent xCompDoc)
			throws CloseVetoException {
		// Closing the converted document. Use XCloseable.close if the
		// interface is supported, otherwise use XComponent.dispose
		com.sun.star.util.XCloseable xCloseable = UnoRuntime.queryInterface(
				com.sun.star.util.XCloseable.class, xCompDoc);
		if (xCloseable != null) {
			xCloseable.close(false);
		} else {
			com.sun.star.lang.XComponent xComp = UnoRuntime.queryInterface(
					com.sun.star.lang.XComponent.class, xCompDoc);
			xComp.dispose();
		}
	}

	/**
	 * Добавить в указанный поток описания колонок
	 * 
	 * @param connection
	 *            соединение для openOffice
	 * @param desc
	 *            описатель шаблона отчёта
	 * @param srcOODocUrl
	 *            исходный файл с документом для openOffice (обычно с
	 *            расширением ".odt")
	 * @param destSaveAsUrl
	 *            целевой файл (*.odt) для сохранения под другим именем, или
	 *            null, если сохранить надо под прежним именем.
	 * @param author
	 *            если не null, то автор, которого надо прописать.
	 */
	public void odtAddColumnsAsDocCustomProps(OpenOfficeConnection connection,
			ReportDescriptor desc, String srcOODocUrl, String destSaveAsUrl,
			String author) 
	{
		final boolean needSaveAs = !Utils.isStringEmpty(destSaveAsUrl);
		logger.debug(String.format(
				"\n\t add DS columns into openOffice document '%s' %s"
				, srcOODocUrl
				, (needSaveAs ? String.format("\n\t as '%s'", destSaveAsUrl) : "")
		));

		PropertyCheck.mandatory(this, "connection", connection);
		PropertyCheck.mandatory(this, "templateUrl", srcOODocUrl);
		// PropertyCheck.mandatory(this, "saveUrl", destOdtUrl);

		PropertyCheck.mandatory(this, "reportDesc", desc);
		PropertyCheck.mandatory(this, "dataSource", desc.getDsDescriptor());
		PropertyCheck.mandatory(this, "columns", desc.getDsDescriptor().getColumns());

		// авто-соединение
		// if (!connection.isConnected()) connection.connect();

		String stage = "create desktop";
		try {
			final XComponentLoader xLoaderDesktop = connection.getDesktop();

			/* (1) Open the document */
			stage = String.format("opening openOffice document '%s'", srcOODocUrl);
			final XComponent xCompDoc = openDoc(xLoaderDesktop, srcOODocUrl);

			// final com.sun.star.beans.XPropertySet docProps = UnoRuntime.queryInterface( com.sun.star.beans.XPropertySet.class, xCompDoc);
			// dumpProperties( String.format( "\nProperties of document \"%s\":\n\t", srcOdtUrl), docProps);

			/* (2) добавление свойств */
			{
				// final XTextDocument document = UnoRuntime.queryInterface(com.sun.star.text.XTextDocument.class, xCompDoc);
				// final XDocumentPropertiesSupplier xDocInfoSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, document);

				stage = String.format( "get openOffice properties of document '%s'", srcOODocUrl);
				final XDocumentPropertiesSupplier xDocPropsSuppl = UnoRuntime.queryInterface( XDocumentPropertiesSupplier.class, xCompDoc);
				final XDocumentProperties xDocProps = xDocPropsSuppl.getDocumentProperties();
				final XPropertyContainer userPropsContainer = xDocProps.getUserDefinedProperties();

				if (author != null) {
					stage = String.format("set openOffice property Author='%s'\n\t of document '%s'", author, srcOODocUrl);
					xDocProps.setAuthor(author);
					logger.debug(stage);
				}

				/*
				 * stage = String.format( "set openOffice property createDate of document '%s'", srcOdtUrl);
				 * com.sun.star.util.DateTime dt = new DateTime(now); xDocProps.setCreationDate(dt);
				 */

				for (ColumnDescriptor col : desc.getDsDescriptor().getColumns()) {
					stage = String.format("(!) fail to directly add property '%s' with expression '%s'", col.getColumnName(), col.getExpression());
					userPropsContainer.addProperty(col.getColumnName(), DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, col.getExpression());
				} // for ColumnDescriptor

				// final XPropertySet userPropSet = UnoRuntime.queryInterface(XPropertySet.class, userPropsContainer);
				// dumpProperties( String.format( "\nCustom properties of document \"%s\":\n\t", srcOdtUrl), userPropSet);
			}

			/* (3) Сохранение */
			{
				final String docInfo = (needSaveAs) ? String.format( "\n\t as '%s'", destSaveAsUrl) : "";
				stage = String.format("saving openOffice document\n\t '%s' %s", srcOODocUrl, docInfo);
				final com.sun.star.frame.XStorable xStorable = saveDocAs( xCompDoc, destSaveAsUrl);
				if (xStorable != null)
					logger.debug(String.format("\nDocument '%s' saved %s \n", srcOODocUrl, docInfo));
			}

			/* (4) Закрыть Документ */
			stage = String.format("closing openOffice document '%s'", srcOODocUrl);
			closeDoc(xCompDoc);

			stage = null;
		} catch (Throwable ex) {
			final String msg = String.format("fail at stage\n\t %s\n\t error %s", stage, ex.getMessage());
			logger.error(msg, ex);
			if (ex instanceof DisposedException)
				throw (DisposedException) ex;
			throw new RuntimeException(msg, ex);
		}
	}

	/**
	 * Задать свойства для атрибутов документа
	 * 
	 * @param props
	 *            задаваемые значения (ключи - имена атрибутов)
	 * @param connection
	 * @param desc
	 * @param srcOODocUrl
	 * @param destSaveAsUrl
	 * @param author автор изменений
	 */
	public void odtSetColumnsAsDocCustomProps( Map<String, Object> props
			, OpenOfficeConnection connection
			, ReportDescriptor desc
			, String srcOODocUrl
			, String destSaveAsUrl
			, String author) 
	{
		final boolean needSaveAs = !Utils.isStringEmpty(destSaveAsUrl);
		logger.debug( String.format( "\n\t add DS columns into openOffice document '%s' %s"
				, srcOODocUrl
				, (needSaveAs ? String.format( "\n\t as '%s'", destSaveAsUrl) : "") ));

		PropertyCheck.mandatory(this, "connection", connection);
		PropertyCheck.mandatory(this, "templateUrl", srcOODocUrl);
		// PropertyCheck.mandatory(this, "saveUrl", destOdtUrl);

		PropertyCheck.mandatory(this, "reportDesc", desc);

		// авто-соединение
		// if (!connection.isConnected()) connection.connect();

		String stage = "create desktop";
		try {
			final XComponentLoader xLoaderDesktop = connection.getDesktop();

			/* (1) Open the document */
			stage = String.format( "opening openOffice document '%s'", srcOODocUrl);
			final XComponent xCompDoc = openDoc(xLoaderDesktop, srcOODocUrl) ;

			// final com.sun.star.beans.XPropertySet docProps = UnoRuntime.queryInterface( com.sun.star.beans.XPropertySet.class, xCompDoc);
			// dumpProperties( String.format( "\nProperties of document \"%s\":\n\t", srcOdtUrl), docProps);

			/* (2) обновление существующих свойств ... */
			{
				// final XTextDocument document = UnoRuntime.queryInterface( com.sun.star.text.XTextDocument.class, xCompDoc);
				// final XDocumentPropertiesSupplier xDocInfoSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, document);

				stage = String.format( "get openOffice properties of document '%s'", srcOODocUrl);

				final XDocumentProperties xDocProps;
				{
					final XDocumentPropertiesSupplier xDocPropsSuppl = UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xCompDoc);
					xDocProps = xDocPropsSuppl.getDocumentProperties();
					// final XPropertyContainer userPropsContainer = xDocProps.getUserDefinedProperties();
					// final XPropertySet userPropsSet = UnoRuntime.queryInterface( com.sun.star.beans.XPropertySet.class, userPropsContainer);
				}

				final XPropertySet docProperties;
				final XPropertyContainer docPropertyContainer;
				{
					final XDocumentInfoSupplier xDocInfoSuppl = UnoRuntime.queryInterface(XDocumentInfoSupplier.class, xCompDoc);
					final XDocumentInfo docInfo = xDocInfoSuppl.getDocumentInfo();
					docProperties = UnoRuntime.queryInterface(XPropertySet.class, docInfo);

					docPropertyContainer = UnoRuntime.queryInterface( XPropertyContainer.class, docInfo);
					// if (docProperties.getPropertySetInfo().hasPropertyByName(fldName) ...
					// final XPropertySetInfo docPropSetInfo = docProperties.getPropertySetInfo();
				}

				if (author != null) {
					stage = String.format( "set openOffice property Author='%s'\n\t of document '%s'", author, srcOODocUrl);
					xDocProps.setAuthor(author);
					logger.debug(stage);
				}

				// logger.info( dumpProperties("props before assign values", docProperties).toString());

				final StringBuilder sb = new StringBuilder("Update openOffice attributes list: ");
				boolean mustLog = false; // true, чтобы обязательно зажурналировать список присвоений из sb
				try {
					if (props != null) {
						int i = 0;
						for (Map.Entry<String, Object> item: props.entrySet()) {
							final String propName = item.getKey();
							final Object propValue = item.getValue();
							try {
								final boolean isPresent = docProperties.getPropertySetInfo().hasPropertyByName( propName);
								i++;
								sb.append(String.format("\n %s [%s]\t'%s' = '%s'", (isPresent ? "set" : "add"), i, propName, Utils.coalesce( propValue, "NULL") ));
								if (isPresent) {
									assignTypedProperty(docProperties, propName, propValue);
								} else {
									docPropertyContainer.addProperty( propName, DOC_PROP_GOLD_FLAG_FOR_PERSISTENCE, propValue);
								}
							} catch (Throwable t) {
								/* 
								 * это не страшно: например, сюда падаем с com.sun.star.lang.IllegalArgumentException 
								 * при присвоении типизированному свойству значения NULL (например, для Дат)
								 */
								mustLog = true;
								logger.warn( String.format("\n [%s]\t'%s' = '%s'", i, propName, propValue), t);
								sb.append( String.format( "\n\t (!) error %s", t.getMessage()));
							}

						}
					} else 
						sb.append("no row properties");

				} finally {
					if (logger.isDebugEnabled() || mustLog) {
						if (mustLog)
							logger.warn( sb.toString());
						else
							logger.debug(sb.toString());
					}
				}

				// final XPropertySet userPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, userPropsContainer);
				// dumpProperties( String.format( "\nCustom properties of document \"%s\":\n\t", srcOdtUrl), userPropSet);
			}

			/* (3) Сохранение */
			{
				final String docInfo = (needSaveAs) ? String.format("\n\t as '%s'", destSaveAsUrl) : "";
				stage = String.format( "saving openOffice document\n\t '%s' %s", srcOODocUrl, docInfo);
				final com.sun.star.frame.XStorable xStorable = saveDocAs( xCompDoc, destSaveAsUrl);
				if (xStorable != null)
					logger.debug( String.format( "\nDocument '%s' saved %s \n",  srcOODocUrl, docInfo));
			}

			/* (4) Закрыть Документ */
			stage = String.format( "closing openOffice document '%s'", srcOODocUrl);
			closeDoc(xCompDoc);

			stage = null;
		} catch(Throwable ex) {
			final String msg = String.format( "fail at stage\n\t %s\n\t error %s", stage, ex.getMessage());
			logger.error( msg, ex);
			if (ex instanceof DisposedException)
				throw (DisposedException) ex;
			throw new RuntimeException( msg, ex);
		}
	}

	/**
	 * Присвоение значения openOffice-атрибуту свойства с учётом его типа.
	 * @param docProperties
	 * @param propName
	 * @param propValue присваиваемое значение, конвертируется в целевой тип.
	 * @throws UnknownPropertyException
	 * @throws PropertyVetoException
	 * @throws IllegalArgumentException
	 * @throws WrappedTargetException
	 */
	public static void assignTypedProperty(final XPropertySet docProperties,
			final String propName, final Object propValue)
		throws UnknownPropertyException, PropertyVetoException,
			IllegalArgumentException, WrappedTargetException
	{
		// присвоение NULL всегда прокатит ...
		// docProperties.getPropertySetInfo().getPropertyByName("").Type
		if (propValue == null) {
			docProperties.setPropertyValue( propName,  null);
			return;
		}

		final String sPropValue = (propValue instanceof String) ? (String) propValue : null;

		// проверяем фактический тип аргумента ...
		final Property pi = docProperties.getPropertySetInfo().getPropertyByName(propName);
		com.sun.star.uno.Type t = pi.Type;
		if (sPropValue != null) {
			// при присвоении строки будем выполнять конвертирование в целевой тип ...
			if (t.equals(Type.STRING)) {
				docProperties.setPropertyValue( propName,  propValue);
				return;
			} else if (t.equals(Type.BOOLEAN)) {
				docProperties.setPropertyValue( propName,  Boolean.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.BYTE)) {
				docProperties.setPropertyValue( propName,  Byte.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.SHORT) || t.equals(Type.UNSIGNED_SHORT)) { // 2х байтный
				docProperties.setPropertyValue( propName,  Short.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.LONG) || t.equals(Type.UNSIGNED_LONG)) { // 4х байтный
				docProperties.setPropertyValue( propName,  Integer.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.HYPER) || t.equals(Type.UNSIGNED_HYPER) ) { // 8и байтный
				docProperties.setPropertyValue( propName,  Long.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.FLOAT)) {
				docProperties.setPropertyValue( propName,  Float.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.DOUBLE)) {
				docProperties.setPropertyValue( propName,  Double.valueOf( sPropValue.trim()));
				return;
			} else if (t.equals(Type.CHAR)) {
				final char ch = (sPropValue.length() == 0) ? '\00' : sPropValue.charAt(0);
				docProperties.setPropertyValue( propName, ch);
				return;
			}
		}

		// здесь propValue уже не строки ...
		if (propValue instanceof java.util.Date) {
			// дату надо преобразовать в star-office-date
			final com.sun.star.util.DateTime ooDate = newDateTime( (java.util.Date) propValue);
			docProperties.setPropertyValue( propName,  ooDate);
			return;
		}

		// по-умолчанию - простое присвоение ...
		docProperties.setPropertyValue( propName,  propValue);
	}

	/**
	 * Преобразование даты в office-DateTime
	 * @param d
	 * @return null, если d = null и преобразованную дату иначе (часовой пояс 
	 * новой даты будет соот-ть часовому поясу d).
	 */
	public static DateTime newDateTime(Date d) {
		if (d == null)
			return null;

		final Calendar c = Calendar.getInstance();
		c.setTime(d);

		final DateTime result = new DateTime();
		result.Year = (short) c.get(Calendar.YEAR);
		result.Month= (short) c.get(Calendar.MONTH);
		result.Day  = (short) c.get(Calendar.DAY_OF_MONTH);

		result.Hours  = (short) c.get(Calendar.HOUR_OF_DAY);
		result.Minutes= (short) c.get(Calendar.MINUTE);
		result.Seconds= (short) c.get(Calendar.SECOND);

		return result;
	}

	public static void addPropertySafely(XPropertySet destProperties,
			XPropertyContainer destPropsContainer, StringBuilder log,
			String fldName, int fldOptions, Object fldValue)
	{
		String stage = "prepare";
		try {
			stage = "addPropertyDef";
			destPropsContainer.addProperty(fldName, (short) fldOptions, fldValue);

			// stage = "setPropertyValue";
			// destProperties.setPropertyValue(fldName, fldValue);

			stage = "logging";
			log.append(String.format(
					"\n\t property added '%s' (options %s) with value <%s>",
					fldName, fldOptions, Utils.coalesce(fldValue, "NULL")));
		} catch (Throwable ex) {
			log.append(String.format(
					"\n\t (!) add property '%s' error at stage %s: %s",
					fldName, stage, ex.getMessage()));
		}
	}

	public static StringBuilder dumpProperties(String info, XPropertySet docProps) {
		final StringBuilder result = new StringBuilder();
		if (info != null)
			result.append(info);
		if (info != null) {
			final XPropertySetInfo pset = docProps.getPropertySetInfo();
			final Property[] properties = (pset != null) ? pset.getProperties() : null;
			if (properties == null) {
				result.append("\t properties are null");
			} else {
				result.append(String.format("\t properties count: %s", properties.length));
				for (int i = 0; i < properties.length; i++) {
					try {
						final Property p = properties[i];
						result.append( String.format("\n\t[%s] '%s'=", i+1, p.Name));
						result.append( String.format(
								"'%s'\n\t\t short flags: %s\n\t\t type: %s"
								, Utils.coalesce( docProps.getPropertyValue(p.Name), "NULL")
								, p.Attributes
								, Utils.coalesce( (p.Type != null) ? p.Type.getZClass() : null, "Type=NULL")
						));
					} catch (Throwable ex) {
						result.append(String.format("\n Error: %s\n", ex.getMessage()));
					}
				} // for
				result.append("\n");
			}
		} // if (info != null)
		return result;
	}

	public static PropertyValue newPropertyValue(String propName, Object propVal) {
		final PropertyValue result = new PropertyValue();
		result.Name = propName;
		result.Value = propVal;
		return result;
	}

}
