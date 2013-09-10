package ru.it.lecm.reports.generators.errands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.TypedJoinDS;
import ru.it.lecm.reports.jasper.config.JRDSConfigXML;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.xml.DSXMLProducer;

/**
 * Дерево Поручений по Документам
 * Фильтр отчета:
 * Выводимые показатели:
 * 	•	дерево поручений от документов (с отступами в зависимости от уровня Поручения)
 * @author rabdullin
 */
public class ErrandsDocTreeDSProvider
		extends GenericDSProviderBase
{

	private static final Logger logger = LoggerFactory.getLogger(ErrandsDocTreeDSProvider.class);

	/** Формат названия документов по-умолчанию */
	final static String DEFAULT_DISPLAY_FORMAT = "cm:name";

	/** для упрощения работы с QName-объектами Поручений */
	private LocalQNamesHelper _qnames;

	/** конфигурационные параметры */
	final private ErrandsDocTreeParams configDocTreeParams = new ErrandsDocTreeParams();

	final protected LocalQNamesHelper qnames() {
		if (this._qnames == null) {
			this._qnames = new LocalQNamesHelper( this.getServices().getServiceRegistry().getNamespaceService());
		}
		return this._qnames;
	}


	/**
	 * Параметры для построения иерархического списка Поручений.
	 * @author rabdullin
	 * SuppressWarnings("unused")
	 */
	private static class ErrandsDocTreeParams {

		final static String XML_TREE_FORMATS = "tree.formats"; // key: "levelShift" = String_For_Level_Shift
		final static String KEY_TREE_FORMATS_SHIFT = "levelShift";
		final static String KEY_TREE_DUP_MARKER = "cycleMarker"; // пометка повторов в дереве документов
		final static String XML_TREE_LEVEL_ASSOC = "tree.levelAssoc"; // key: "level.nn" = "qname-of-assoc-for-level-nn-to-get-next-level"

		/** строка в качестве отступа на каждый уровень*/
		private String levelShift = "\t";

		/** строка для пометки повторов */
		private String dupMarker = "(!)";

		/** конфигурация по связям в виде: ключ "level.n" = значение qname-ассоциации...*/
		final private Map<String, Object> levelsAssoc = new HashMap<String, Object>(4);

		/** строка-префикс, для сдвига на каждом уровне, обычно - табуляция  */
		public String getLevelShift() {
			return levelShift;
		}

		/** строка-префикс, для сдвига на каждом уровне, обычно - табуляция */
		public void setLevelShift(String levelShift) {
			this.levelShift = levelShift;
		}

		/** строка для пометки повторов */
		public String getDupMarker() {
			return dupMarker;
		}

		/** строка для пометки повторов */
		public void setDupMarker(String dupMarker) {
			this.dupMarker = dupMarker;
		}

		/**
		 * Сконфигурированные значения ассоциативной связи (на родителя) для уровня.
		 * Ключи в виде "level.nn", где nn индекс уроня связи (1 = первые дети, 2 = дети-детей и т.д.)
		 * Для получения связи соот-щей уровню n использовать метод getLeveledAssocQn().
		 * Здесь перечислены только уровни, на которых связи отличны.
		 * Сейчас тут один элемент (все связи одинаковы - по ассоциации "lecm-errands:additional-document-assoc").
		 * @return
		 */
		public Map<String, Object> getLevelsAssocMap() {
			return levelsAssoc;
		}

		/**
		 * Получить название ключа, хранящего параметр указанного уровня
		 * @param level
		 * @return
		 */
		public static String getLeveledKey(int level) {
			return String.format( "level.%d", level);
		}

		/**
		 * Получить ассцоаицию на детей указанного уровня.
		 * @param level уровень на который надо протянуть связь к детям (т.е.
		 * значение level=1 соот-ет получению ассоциации от Родителей верхнего
		 * уровня к своим непосредственным детям и т.д.)
		 * @return связь, или null если явно заданного значения нет в конфе.
		 */
		public String getLeveledAssocQn(int level) {
			final String key = getLeveledKey(level);
			return (this.levelsAssoc != null && this.levelsAssoc.containsKey(key))
						? (String) this.levelsAssoc.get(key)
						: null;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("ErrandsDocTreeParams [levelShift=");
			builder.append(levelShift);
			builder.append(", levelsAssoc [");
			builder.append(levelsAssoc);
			builder.append("]]");
			return builder.toString();
		}

		/**
		 * Прогрузка параметров из XML ...
		 * @param config
		 */
		public void scanTreeParams(JRDSConfigXML config) {
			if (config == null)
				return;

			{ /* Форматы */
				final Map<String, Object> formats = config.getMap(XML_TREE_FORMATS);
				if (formats != null) {
					if (formats.containsKey(KEY_TREE_FORMATS_SHIFT)) {
						this.levelShift = Utils.coalesce( formats.get(KEY_TREE_FORMATS_SHIFT), this.levelShift);
					}
					if (formats.containsKey(KEY_TREE_DUP_MARKER)) {
						this.dupMarker = Utils.coalesce( formats.get(KEY_TREE_DUP_MARKER), this.dupMarker);
					}
				}
			}

			{ /* Ассоциации для переходов с уровня на уровень ... */
				final Map<String, Object> levels = config.getMap(XML_TREE_LEVEL_ASSOC);
				if (levels != null) {
					this.levelsAssoc.putAll(levels);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug( String.format( "Config loaded for Errands Tree Report:\n%s", this));
			}
		}
	}

	@Override
	protected void setXMLDefaults(Map<String, Object> defaults) {
		super.setXMLDefaults(defaults);
		defaults.put( ErrandsDocTreeParams.XML_TREE_FORMATS, null);
		defaults.put( ErrandsDocTreeParams.XML_TREE_LEVEL_ASSOC, null);
	}


	private void loadConfig() {
		try {
			conf().setConfigName( DSXMLProducer.makeDsConfigFileName( this.getReportDescriptor().getMnem()) );
			conf().loadConfig();
			this.configDocTreeParams.scanTreeParams( conf());
		} catch (JRException e) {
			logger.error(e.getMessage(), e);
		}
	}


	@Override
	protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
		final ExecDocTreeJRDataSource result = new ExecDocTreeJRDataSource(iterator);
		return result;
	}


	@Override
	protected ResultSet execQuery() {
		loadConfig();
		return super.execQuery();
	}


	/**
	 * Названия колонок в наборе данных для отчёта.
	 */
	final private class DsErrandsDocTreeColumnNames {

		/** Колонка "Выбранные документы" */
		@SuppressWarnings("unused")
		final static String COL_PARAM_DOCUMENTS = "Col_Param_Documents"; // String со списком nodeRefs

		/** Колонка "Иерархический номер" */
		final static String Col_LEVELED_NUM = "Col_LeveledNum"; // String

		/** Колонка "Название" */
		final static String COL_DISPLAY_NAME = "Col_DisplayName"; // String
	}


	/** QName-ссылки на данные Альфреско **************************************/
	private class LocalQNamesHelper extends ErrandsQNamesHelper
	{
		LocalQNamesHelper(NamespaceService ns) {
			super(ns);
		}
	}

	/**
	 * Структура для хранения данных о иерархическом объекте:
	 */
	private static class ItemLeveledInfo {

		final private NodeRef node;

		/* текущий уровень данного узла, фиксируем, т.к. не собираемся его менять
		 * и было проще вычислять numberedStr
		 */
		final private int level;

		/** номер данного элемент (от одного) внутри родительского списка */
		final private int number;

		/** строка с номером, включающая родительские элементы */
		final private String leveledNumberStr;

		/** Название элемента */
		private String displayName;

		/**
		 * Список детей
		 */
		final private List<ItemLeveledInfo> children = new ArrayList<ItemLeveledInfo>();

		public ItemLeveledInfo() {
			// псевдо-узел
			this(null, /*level*/-1, /*number*/0, /*parentStr*/ "", /*displayName*/ "RootNode");
		}

		/**
		 * Создать описание узла указанного уровня
		 * @param node id узла
		 * @param level уровень данного узла
		 * @param numberedStrOfParent номерная строка родителя (или пусто/null для корневых)
		 * @param displayName название объекта
		 */
		public ItemLeveledInfo(NodeRef node, int level, int number, String numberedStrOfParent, String displayName) {
			super();
			this.node = node;
			this.level = level;
			this.number = number;
			final String fmt = (level > 0 && !Utils.isStringEmpty(numberedStrOfParent))
							? "%s.%s"
							: "%2$s"; // без точки в начале
			this.leveledNumberStr = String.format( fmt, Utils.coalesce( numberedStrOfParent, ""), number);
			this.displayName = displayName;
		}

		/** id узла */
		public NodeRef getNode() {
			return node;
		}

		/** Уровень узла */
		public int getLevel() {
			return level;
		}

		/** номер данного элемент (от одного) внутри родительского списка */
		public int getNumber() {
			return number;
		}

		/** Строка с иерархическим номером */
		public String getLeveledNumberStr() {
			return leveledNumberStr;
		}

		/** Название объекта */
		public String getDisplayName() {
			return displayName;
		}

		/** Название объекта */
		@SuppressWarnings("unused")
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		/** (всегда assigned) дети данного объекта */
		public List<ItemLeveledInfo> getChildren() {
			return children;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("ItemLeveledInfo [");
			builder.append("node={").append(getNode()).append("}");
			builder.append(", level ").append(getLevel());
			builder.append(", number ").append(getNumber());
			builder.append(", numberStr ").append(getLeveledNumberStr());
			builder.append(", displayName=").append(getDisplayName());
			builder.append("\n, children count ").append(getChildren() == null ? " null" : getChildren().size());
			builder.append("]");
			return builder.toString();
		}

		/**
		 * Зарегистрировать нового детёныша ...
		 * (!) уровень детёнышу присваивается автоматически
		 * @param childId id узла
		 * @param number номер в списке присваивается автоматически
		 * @param childName имя детёнышу
		 * @return созданный объект, (!) его номер в личном списке присваивается автоматически
		 */
		public ItemLeveledInfo addChild( NodeRef childId, String childName) {
			final int number = this.children.size() + 1; // нумерация детишек от одного
			final ItemLeveledInfo result = new ItemLeveledInfo( childId, this.level + 1, number, this.leveledNumberStr, childName);
			this.children.add(result);
			return result;
		}

	} // class ItemLeveledInfo


	/**
	 * Построитель линейного списка из иерархического
	 */
	private class DocTreeBuilder {

		/** ообозначение для неограниченной вложенности */
		final static int UNLIMITED_LEVELS = -1;

		final private NodeService nodeService;
		final private SubstitudeBean substService;
		final private NamespaceService ns;

		final private Map<NodeRef, ItemLeveledInfo> processed = new HashMap<NodeRef, ItemLeveledInfo>();
		final private List<ItemLeveledInfo> processedList = new ArrayList<ItemLeveledInfo>();
		final private ItemLeveledInfo virtRoot = new ItemLeveledInfo(); // псевдоузел, в который подкладываются отсальные ...

		public DocTreeBuilder() {
			this.nodeService = getServices().getServiceRegistry().getNodeService();
			this.ns = getServices().getServiceRegistry().getNamespaceService();
			this.substService = getServices().getSubstitudeService();
		}

		/**
		 * Зарегистрировать документ верхнего уровня и выполнить прогрузку его детей.
		 * Защита от зацикливания.
		 * @param docId
		 */
		public void regRootItem(NodeRef docId) {
			regLeveldItem(docId, virtRoot, null, UNLIMITED_LEVELS);
		}

		/**
		 * Собранные данные по объектам (Документам и Пручениям, для быстрого доступа по id)
		 */
		public Map<NodeRef, ItemLeveledInfo> getProcessed() {
			return processed;
		}

		/**
		 * Линейный список-представление
		 * (то же что и processed.values(), но "железо-бетонно" в нужном порядке)
		 */
		public List<ItemLeveledInfo> getProcessedList() {
			return processedList;
		}

		/**
		 * Зарегить документ указанного уровня и потом рекурсивно подгрузить его
		 * детей (с контролем от зацикливания).
		 * @param docId
		 * @param parentNode
		 * @param maxDeepLevel оставшиеся уровни, если 0, то дети не будут грузиться,
		 * если (<0), то глубина не ограничена.
		 * @param qnParentAssoc ассоциация, которая привела на уровень level (для корневых null)
		 */
		private void regLeveldItem( final NodeRef docId, ItemLeveledInfo parentNode, final QName qnParentAssoc, int maxDeepLevel) {

			PropertyCheck.mandatory(this, "docId", docId);
			PropertyCheck.mandatory(this, "parentNode", parentNode);

			// уровень создаваемого здесь объекта (0 = корневой)
			final int level = parentNode.getLevel() + 1;

			// флажок повтора узла
			final boolean isDup = this.getProcessed().containsKey(docId);

			/* Название нового элемента */
			String displayName = makeDocName(docId);

			// повторяющиеся элементы помечаем "звёздочкой"
			if (isDup) displayName = configDocTreeParams.getDupMarker() + displayName;

			/* Добавление нового элемента */
			final ItemLeveledInfo docItem = parentNode.addChild( docId, displayName);
			this.processed.put( docId, docItem);
			this.processedList.add( docItem);

			/* Детей добавим только если элемента ещё не было или он верхнего уровня */
			if (isDup && level > 0) { // повтор и глубина есть ...
				logger.warn( String.format( "Item {%s} at level %s found recursevly -> deeper children not scanned", docId, level));
				return;
			}

			/* подгружаем детей ((!) по обратной связи) */
			if (maxDeepLevel != 0) {
				final String cur_qnameStr = configDocTreeParams.getLeveledAssocQn(level+1);

				// если явно не задано ассоциации - используем от предыдущего уровня
				final QName qnCurAssoc = Utils.isStringEmpty(cur_qnameStr) ? qnParentAssoc : QName.createQName(cur_qnameStr, this.ns);
				if (qnCurAssoc == null)
					throw new RuntimeException( String.format( "Invalid configuration: no association found from level %d", level));

				final List<ChildAssociationRef> children = nodeService.getParentAssocs(docId, qnCurAssoc, RegexQNamePattern.MATCH_ALL);
				if (children != null) {
					for (ChildAssociationRef child: children) {
						regLeveldItem( child.getParentRef(), docItem, qnCurAssoc, maxDeepLevel-1);
					}
				} else { // пробуем другой тип ассоциаций ...
					final List<AssociationRef> children2 = nodeService.getSourceAssocs(docId, qnCurAssoc);
					if (children2 != null) {
						for (AssociationRef child: children2) {
							regLeveldItem( child.getSourceRef(), docItem, qnCurAssoc, maxDeepLevel-1);
						} // for
					} // if
				}
			}
		}

		private String makeDocName(NodeRef docId) {
			if (docId == null)
				return "<...>";
			String result = substService.getObjectDescription(docId);
			if (Utils.isStringEmpty(result)) { // формирование только имени
				result = substService.formatNodeTitle(docId, DEFAULT_DISPLAY_FORMAT);
			}
			return (Utils.isStringEmpty(result)) ? docId.toString() : result;
		}
	}

	/**
	 * Jasper-НД для вычисления статистики
	 */
	private class ExecDocTreeJRDataSource extends TypedJoinDS<ItemLeveledInfo> {

		public ExecDocTreeJRDataSource(Iterator<ResultSetRow> iterator) {
			super(iterator);
		}


		/**
		 * Прогрузить строку отчёта
		 */
		@Override
		protected Map<String, Serializable> getReportContextProps(ItemLeveledInfo item)
		{
			final Map<String, Serializable> result = new LinkedHashMap<String, Serializable>();
			/* Номер ... */
			result.put( DsErrandsDocTreeColumnNames.Col_LEVELED_NUM, item.getLeveledNumberStr());
			/* Название ... */
			final String prefix = Utils.dup( configDocTreeParams.getDupMarker(), item.getLevel());
			result.put( DsErrandsDocTreeColumnNames.COL_DISPLAY_NAME, prefix + item.getDisplayName() );
			return result;
		}

		@Override
		public int buildJoin() {
			// final DataSourceDescriptor ds = getReportDescriptor().getDsDescriptor();
			// final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();

			final DocTreeBuilder result = new DocTreeBuilder();

			// проход по данным ...
			if (context.getRsIter() != null) {
				/* проход по все загруженным Документам ... */
				while(context.getRsIter().hasNext()) {
					final ResultSetRow rs = context.getRsIter().next();
					final NodeRef docId= rs.getNodeRef(); // id Документа или Поручения - что User выбрали то и будет)
					if (context.getFilter() != null && !context.getFilter().isOk(docId)) {
						if (logger.isDebugEnabled())
							logger.debug( String.format("{%s} filtered out", docId));
						continue;
					}
					result.regRootItem( docId);
				} // while по НД

				this.setData( result.getProcessedList() );
			} // if

			if (this.getData() != null)
				this.setIterData(this.getData().iterator());

			final int foundCount = result.getProcessedList().size();
			logger.info( String.format( "found %s data items", foundCount));

			return foundCount;
		}

	}

}
