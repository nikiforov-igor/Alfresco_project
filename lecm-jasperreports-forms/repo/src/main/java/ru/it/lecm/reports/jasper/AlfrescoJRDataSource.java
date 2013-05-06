package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.jasper.config.JRDSConfigBaseImpl.JRXField;
import ru.it.lecm.reports.jasper.filter.AssocDataFilter;
import ru.it.lecm.reports.jasper.utils.Utils;


/**
 * AlfrescoJRDataSource: набор данных обеспечивает JR-интерфейс для полученных данных Alfresco.
 * Подразумеваемтся такая схема получения данных:
 *    [ 0) имена JR-полей совпадают с названиями model-атрибутов Альфреско ]
 *    1) основной поисковый запрос выдаёт только id отобранных объектов
 *    2) далее LocalJRDataSource "догружает" в методе next() значения 
 * атрибутов Альфреско, которые прописаны в config (или грузит все атрибуты,
 * т.к. ссылки на них всё равно будут обеспечены по именам) 

 * Значения метаописаний fields из config, могут использоваться в дальнейшем 
 * для вычитывания части атрибутов данных, вместо выборки целиком всех.
 * 
 * @author rabdullin
 *
 */
public class AlfrescoJRDataSource implements JRDataSource 
{
	private static final Logger logger = LoggerFactory.getLogger(AlfrescoJRDataSource.class);

	private ServiceRegistry serviceRegistry;
	private SubstitudeBean substitudeService;
	private AssocDataFilter filter; // может быть NULL
	private Map<String, JRXField> metaFields;

	// список текущих Альфреско атрибутов активной строки данных набора
	// ключ = QName.toString() с короткими именами типов (т.е. вида "cm:folder" или "lecm-contract:document")
	protected Map<String, Serializable> curProps;
	protected NodeRef curNodeRef;
	protected Iterator<ResultSetRow> rsIter;
	protected ResultSetRow rsRow;

	/**
	 * список gname Альфреско-атрибутов, которые только и нужны для отчёта
	 * (с короткими префиксами)
	 * null означает, что ограничений нет.
	 */
	private Set<String> visibleProps;


	/**
	 * Проверить является ли указанное поле вычисляемым (в понимании SubstitudeBean):
	 * если первый символ "{", то является.
	 * @param fldName
	 * @return
	 */
	public static boolean isCalcField(final String fldName) {
		return (fldName != null) && fldName.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL);
	}

	public AlfrescoJRDataSource(Iterator<ResultSetRow> iterator) {
		this.rsIter = iterator;
	}

	public void clear() {
		curProps = null;
		curNodeRef= null;
		rsRow = null;
		rsIter = null;
	}


	public Set<String> getVisibleProps() {
		return visibleProps;
	}

	public void setVisibleProps(Set<String> visibleProps) {
		this.visibleProps = visibleProps;
	}

	public Map<String, JRXField> getMetaFields() {
		return metaFields;
	}

	public void setMetaFields(Map<String, JRXField> metaFields) {
		this.metaFields = metaFields;
	}

	public ServiceRegistry getserviceRegistry() {
		return serviceRegistry;
	}

	public void setRegistryService(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public SubstitudeBean getSubstitudeService() {
		return substitudeService;
	}

	public void setSubstitudeService(SubstitudeBean substitudeService) {
		this.substitudeService = substitudeService;
	}

	public AssocDataFilter getFilter() {
		return filter;
	}

	public void setFilter(AssocDataFilter value) {
		this.filter = value;
	}


	protected boolean isPropVisible(final String propNameWithPrefix) {
		return (visibleProps == null) // видно всё
				|| visibleProps.contains(propNameWithPrefix); // или название имеется в списке того, что отрисовывается в Jasper
	}

	@Override
	public boolean next() throws JRException {
		while (rsIter != null && rsIter.hasNext()) {
			rsRow = rsIter.next();
			curNodeRef = rsRow.getNodeRef();
			if (loadAlfNodeProps(curNodeRef))
				return true; // FOUND ONE MORE
		} // while
		// NOT FOUND MORE - DONE
		curProps = null;
		return false;
	}

	@Override
	public Object getFieldValue(JRField jrf) throws JRException {
		if (jrf == null)
			return null;

		// получаем нативное название данных
		final JRXField fld = metaFields.get( jrf.getName());
		final String fldAlfName = (fld != null) ? fld.getValueLink() : jrf.getName();
		if (curProps != null) {
			if (curProps.containsKey(fldAlfName))
				return curProps.get(fldAlfName);
		}

		// (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
		if (isCalcField(fldAlfName)) {
			if (substitudeService != null) {
				final Object value = substitudeService.formatNodeTitle(curNodeRef, fldAlfName);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format( "\nData: {%s}\nFound as: '%s'", fldAlfName, value));
				}
				return value;
			}
			logger.warn("(!) substitudeService is NULL -> fld values cannot be loaded");
		}

		return (String.class.equals(jrf.getValueClass())) ? fldAlfName : null; // no value -> return current name if valueClass is String
	}

	/**
	 * Загржуает строку с указанным id и проверяет её на соот-вие фильтру	
	 * @param id
	 * @return true, если фильтра нет или строка удовлетворяет фильтру
	 */
	protected boolean loadAlfNodeProps(NodeRef id) {
		final NodeService nodeSrv = serviceRegistry.getNodeService();

		// дополнительно фильтруем по критериям, если они есть ...
		if (this.filter != null && !filter.isOk(id)) {
			logger.debug( String.format("Filtered out node %s", id));
			return false;
		}

		final Map<QName, Serializable> alfProps = nodeSrv.getProperties(id);

		// далее формируем список полей, т.к. фильтр пройден положительно

		/*
		 * на случай, если в alfProps будет НЕ полной набор всех свойств 
		 * объекта (например, может не быть пустых значений) гарантируем 
		 * чтобы curProps содержал всё, что задано в фильтре
		 */

		this.curProps = makeDSRowProps();

		if (alfProps != null) { 
			logAlfData( alfProps, String.format("Loaded properties of %s\n\t Filtering fldNames for jasper-report by list: %s", id, visibleProps));
			for (Map.Entry<QName, Serializable> e: alfProps.entrySet()) {
				final String key = e.getKey().toPrefixString(serviceRegistry.getNamespaceService());
				// если есть мета-описания - оставим только то, что там упоминается
				if (isPropVisible(key))
					curProps.put(key, e.getValue());
			}
		}

		return true;
	}

	protected void logAlfData(Map<QName, Serializable> props, String info) {
		if (logger.isDebugEnabled()) {
			final StringBuilder dump = Utils.dumpAlfData(props, info);
			logger.debug(dump.toString());
		}
	}

	/*
	 * на случай, если в alfProps будет НЕ полной набор всех свойств 
	 * объекта (например, может не быть пустых значений) гарантируем, 
	 * чтобы curProps содержал всё, что задано в фильтре visibleProps
	 */
	protected HashMap<String, Serializable> makeDSRowProps() {
		final HashMap<String, Serializable> result = new HashMap<String, Serializable>();
		final StringBuilder sb = new StringBuilder("Filtering alfresco properties by names: \n"); 
		if (this.visibleProps != null){
			// все свойства включаем в набор с пустыми значениями
			int i = 0;
			for (String fldName: this.visibleProps) {
				i++;
				if (!isCalcField(fldName)) { // обычное поле
					result.put( fldName, null);
					sb.append( String.format( "\t[%d]\t field '%s'\n", i, fldName));
				} else
					// Если есть "пути" в именах -> атрибут косвенный -> в result не включаем
					sb.append( String.format( "\t[%d]\t referenced field '%s' detected -> using evaluator for it \n", i, fldName));
			}
		}
		if (logger.isDebugEnabled()) 
			logger.debug(sb.toString());
		return result;
	}

}
