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
 *    3) имеется возможность иметь вычисляемые поля (кодируется в имени - обрамление символами '{}').  

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
	private Map<String, JRXField> metaFields; // ключ = имя колонки данных в jasper

	// список текущих Альфреско атрибутов активной строки данных набора
	// ключ = QName.toString() с короткими именами типов (т.е. вида "cm:folder" или "lecm-contract:document")
	protected Map<String, Serializable> curProps; // ключ = нативное Альфреско-имя
	protected NodeRef curNodeRef;
	protected Iterator<ResultSetRow> rsIter;
	protected ResultSetRow rsRow;

	/**
	 * список простых gname Альфреско-атрибутов, которые только упоминаются в 
	 * самом JR-отчёте (причём имена - с короткими префиксами)
	 * null означает, что ограничений нет.
	 */
	private Set<String> jrSimpleProps;


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

	/**
	 * Простые (не вычисляемые) свойства ищ jr-отчёта.
	 * null означает что видны могут быть люые свойства.
	 * @return
	 */
	public Set<String> getJRSimpleProps() {
		return jrSimpleProps;
	}

	public void setJrSimpleProps(Set<String> jrVisibleProps) {
		this.jrSimpleProps = jrVisibleProps;
	}

	/**
	 * Мета описание полей данных
	 * @return
	 */
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

	/**
	 * Фильтр данных. Если NULL, то не используется.
	 * @return
	 */
	public AssocDataFilter getFilter() {
		return filter;
	}

	public void setFilter(AssocDataFilter value) {
		this.filter = value;
	}

	/**
	 * @param propNameWithPrefix название свойства
	 * @return true, если свойствоо
	 */
	protected boolean isPropVisibleInReport(final String propNameWithPrefix) {
		return (jrSimpleProps == null) // если нет фильтра -> видно всё
				|| jrSimpleProps.contains(propNameWithPrefix); // или название имеется в списке того, что отрисовывается в Jasper
	}

	@Override
	public boolean next() throws JRException {
		while (rsIter != null && rsIter.hasNext()) {
			rsRow = rsIter.next();
			curNodeRef = rsRow.getNodeRef();
			if (loadAlfNodeProps(curNodeRef)) // загрузка данных по строке 
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
	private boolean loadAlfNodeProps(NodeRef id) {
		// дополнительно фильтруем по критериям, если они есть ...
		if (this.filter != null && !filter.isOk(id)) {
			logger.debug( String.format("Filtered out node %s", id));
			return false;
		}

		// далее формируем список полей, т.к. фильтр пройден положительно

		/*
		 * на случай, если в alfProps будет НЕ полной набор всех свойств 
		 * объекта (например, может не быть пустых значений) гарантируем 
		 * чтобы curProps содержал всё, что задано в фильтре
		 */
		this.curProps = ensureJRProps();

		final NodeService nodeSrv = serviceRegistry.getNodeService();
		final Map<QName, Serializable> realProps = nodeSrv.getProperties(id);
		logAlfData( realProps, String.format("Loaded properties of %s\n\t Filtering fldNames for jasper-report by list: %s", id, jrSimpleProps));
		if (realProps != null) { 
			for (Map.Entry<QName, Serializable> e: realProps.entrySet()) {
				// переводим название свойства в краткую форму
				final String key = e.getKey().toPrefixString(serviceRegistry.getNamespaceService());
				// если есть мета-описания - добавим всё, что там упоминается
				if (isPropVisibleInReport(key))
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

	/**
	 * Сформировать список обычных (не вычисляемых и не косвенных) свойств, 
	 * перечисленных в visibleProps. 
	 * Удобно для случая, когда загружаемые данные по объекту содержат НЕ полный 
	 * набор всех свойств объекта (например, может не быть пустых значений), 
	 * добавлением мы гарантируем, чтобы curProps содержал всё, что надо для jr.
	 */
	private HashMap<String, Serializable> ensureJRProps() {
		final HashMap<String, Serializable> result = new HashMap<String, Serializable>();
		final StringBuilder sb = new StringBuilder("Filtering alfresco properties by names: \n"); 
		if (this.jrSimpleProps != null){
			// все свойства включаем в набор с пустыми значениями
			int i = 0;
			for (String fldName: this.jrSimpleProps) {
				i++;
				if (!isCalcField(fldName)) { // обычное поле
					result.put( fldName, null);
					sb.append( String.format( "\t[%d]\t field '%s'\n", i, fldName));
				} else
					// Если есть "пути" в именах -> атрибут косвенный -> в result здесь не включаем
					sb.append( String.format( "\t[%d]\t referenced field '%s' detected -> using evaluator for it \n", i, fldName));
			}
		} else
			sb.append("\t all fields will be Sincluded");
		if (logger.isDebugEnabled()) 
			logger.debug(sb.toString());
		return result;
	}

}
