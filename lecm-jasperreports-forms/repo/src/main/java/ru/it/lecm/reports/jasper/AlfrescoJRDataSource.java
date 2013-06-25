package ru.it.lecm.reports.jasper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.DataFieldColumn;
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

	final protected ReportDSContextImpl context = new ReportDSContextImpl();

	public AlfrescoJRDataSource(Iterator<ResultSetRow> iterator) {
		this.context.setRsIter( iterator);
	}

	public void clear() {
		context.clear();
	}

	public ReportDSContextImpl getContext() {
		return context;
	}

	/**
	 * @param propNameWithPrefix название свойства
	 * @return true, если свойство простое (т.е. получается непосредственно у объекта)
	 */
	protected boolean isPropVisibleInReport(final String propNameWithPrefix) {
		return (context.getJrSimpleProps() == null) // если нет фильтра -> видно всё
				|| context.getJrSimpleProps().contains(propNameWithPrefix); // или название имеется в списке того, что отрисовывается в Jasper
	}

	@Override
	public boolean next() throws JRException {
		while (context.getRsIter() != null && context.getRsIter().hasNext()) {
			context.setRsRow( context.getRsIter().next());
			context.setCurNodeRef( context.getRsRow().getNodeRef() );
			if (loadAlfNodeProps(context.getCurNodeRef())) // загрузка данных по строке 
				return true; // FOUND ONE MORE
		} // while
		// NOT FOUND MORE - DONE
		context.setCurNodeProps( null);
		return false;
	}

	@Override
	public Object getFieldValue(JRField jrf) throws JRException {
		return context.getPropertyValueByJRField(jrf.getName());
	}

	/**
	 * Загржуает строку с указанным id и проверяет её на соот-вие фильтру	
	 * @param id
	 * @return true, если фильтра нет или строка удовлетворяет фильтру
	 */
	protected boolean loadAlfNodeProps(NodeRef id) {
		// дополнительно фильтруем по критериям, если они есть ...
		if (this.context.getFilter() != null && !context.getFilter().isOk(id)) {
			logger.debug( String.format("Filtered out node %s", id));
			return false;
		}

		// далее формируем список полей, т.к. фильтр пройден положительно

		/*
		 * на случай, если в alfProps будет НЕ полной набор всех свойств 
		 * объекта (например, может не быть пустых значений) гарантируем 
		 * чтобы curProps содержал всё, что задано в фильтре
		 */
		this.context.setCurNodeProps( ensureJRProps());

		final NodeService nodeSrv = context.getRegistryService().getNodeService();
		final Map<QName, Serializable> realProps = nodeSrv.getProperties(id);
		logAlfData( realProps, String.format("Loaded properties of %s\n\t Filtering fldNames for jasper-report by list: %s", id, context.getJrSimpleProps()));
		if (realProps != null) { 
			for (Map.Entry<QName, Serializable> e: realProps.entrySet()) {
				// переводим название свойства в краткую форму
				final String key = e.getKey().toPrefixString(context.getRegistryService().getNamespaceService());
				// если есть мета-описания - добавим всё, что там упоминается
				if (isPropVisibleInReport(key))
					context.getCurNodeProps().put(key, e.getValue());
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
	 * Получить название поля (в списке атрибутов объекта после вызова getReportContextProps), 
	 * соот-щее jasper-названию колонки  
	 * @param jrFldName название колонки для Jasper (оно упрощено относительно "полного" названия в curProps)
	 * @return
	 */
	protected String getAlfAttrNameByJRKey(String jrFldName) {
		final DataFieldColumn fld = (context != null) && context.getMetaFields().containsKey(jrFldName) 
						? context.getMetaFields().get(jrFldName) 
						: null;
		return (fld != null) && (fld.getValueLink() != null)
					? fld.getValueLink() 
					: jrFldName;
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
		if (this.context.getJrSimpleProps() != null){
			// все свойства включаем в набор с пустыми значениями
			int i = 0;
			for (String fldName: this.context.getJrSimpleProps()) {
				i++;
				if (!context.isCalcField(fldName)) { // обычное поле
					result.put( fldName, null);
					sb.append( String.format( "\t[%d]\t field '%s'\n", i, fldName));
				} else
					// Если есть "пути" в именах -> атрибут косвенный -> в result здесь не включаем
					sb.append( String.format( "\t[%d]\t referenced field '%s' detected -> using evaluator for it \n", i, fldName));
			}
		} else
			sb.append("\t all fields will be included");
		if (logger.isDebugEnabled()) 
			logger.debug(sb.toString());
		return result;
	}

}
