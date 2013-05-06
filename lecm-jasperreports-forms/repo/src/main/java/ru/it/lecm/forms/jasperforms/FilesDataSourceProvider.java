package ru.it.lecm.forms.jasperforms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.ListOfArrayDataSource;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AZinovin
 * Date: 14.09.12
 * Time: 17:41
 *
 * Простой провайдер для стандартных файлов и папок
 * На входе принимает всего один параметр - <code>nodeRef</code>, который может представлять список папок/файлов, либо единственный файл/папку.
 * В случае единственной папки возвращает список содержимого папки, в остальных случаях - список по переданным идентификаторам
 */
public class FilesDataSourceProvider extends AbstractDataSourceProvider {

	/**
	 * id узлов для наполнения набора данных 
	 */
	final private List<String> nodeRefs = new ArrayList<String>();

	/**
	не удалять - нужен для инициализации класса при вызове из iReport
	 */
	public FilesDataSourceProvider() {
		super();
	}

	public FilesDataSourceProvider(ServiceRegistry serviceRegistry) {
		super(serviceRegistry);
	}

	/**
	 * @return id узлов для наполнения набора данных  (см также query)
	 */
	public List<String> getNodeRefs() {
		return nodeRefs;
	}

	/**
	 * @param value id узлов для наполнения набора данных (см также query)
	 */
	public void setNodeRefs(List<String> value) {
		this.nodeRefs.clear();
		if (value != null) 
			this.nodeRefs.addAll( value);
	}

	public void setNodeRefs(String[] value) {
		this.nodeRefs.clear();
		if (value != null && value.length > 0) 
			this.nodeRefs.addAll(Arrays.asList(value));
	}

	public void setNodeRef(String value) {
		this.nodeRefs.clear();
		if (value != null) 
			this.nodeRefs.add(value);
	}

	@Override
	protected void initFields() {
		// init fields list
		addField("name", String.class);
		addField("node-dbid", Long.class);
		addField("store-identifier", String.class);

		addField("locale", Locale.class);
		addField("modified", Date.class);
		addField("node-uuid", String.class);

		addField("status", String.class);
		addField("list-present-string", String.class);
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		// report.getParameters()
		final JRField[] fields = getFields(report);
		final String[] columnNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			final JRField field = fields[i];
			columnNames[i] = field.getName();
		}
		final List<Object[]> records = new ArrayList<Object[]>();
		final List<FileInfo> fileInfos = getFileInfos();
		for (FileInfo fileInfo : fileInfos) {
			final Object[] record = new Object[columnNames.length];
			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				if (columnName == null) continue;
				for (Map.Entry<QName, Serializable> entry : fileInfo.getProperties().entrySet()) {
					if (columnName.equals(entry.getKey().getLocalName())) {
						record[i] = entry.getValue();
						break;
					}
				}
			}
			records.add(record);
		}
		return new ListOfArrayDataSource(records, columnNames);
	}

	@Override
	public void dispose(JRDataSource dataSource) throws JRException {
		//не используется
	}

	private List<FileInfo> getFileInfos() {
		final List<NodeRef> nodeRefs = new ArrayList<NodeRef>();
		for (String nodeRefStr : this.nodeRefs) {
			nodeRefs.addAll(NodeRef.getNodeRefs(nodeRefStr));
		}
		final FileFolderService fileFolderService = serviceRegistry.getFileFolderService();
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();

		if (nodeRefs.size() == 1) {
			final NodeRef folderRef = nodeRefs.get(0);
			final FileInfo info = fileFolderService.getFileInfo(folderRef);
			if (info.isFolder()) {
				fileInfos.addAll(fileFolderService.list(folderRef));
			} else {
				fileInfos.add(info);
			}
		} else {
			for (NodeRef nodeRef : nodeRefs) {
				fileInfos.add(fileFolderService.getFileInfo(nodeRef));
			}
		}

		return fileInfos;
	}

	private String namespace = "http://www.it.ru/lecm/model/document/1.0";
	private String typename = null; // "document";

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String value) {
		this.namespace = value;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

//	private void execSearch(String query) {
//		final SearchService srv = serviceRegistry.getSearchService();
//		final NodeService nodesrv = serviceRegistry.getNodeService();
//
//		/* параметры Lucene поиска */
//		final SearchParameters sp = new SearchParameters();
//		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
//
//		// sp.setLanguage(SearchService.LANGUAGE_LUCENE); //  Lucene
//		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO); // FTS (!)
//
//		final StringBuilder sbQuery = new StringBuilder(query);
//
//		/* строго задаём тип */
//		if (getTypename() != null && getTypename().length() > 0) {
//			sbQuery.append(String.format("TYPE:\"{%s}%s\"", getNamespace(), getTypename()));
//		}
//
//		/* TODO: добавление условий поиска */
////		sbQuery.append(String.format(" AND @{%s}%s:\"%s\"", getNamespace(), "status", status));
////		sbQuery.append(String.format(" AND @{%s}%s:\"%s\"", getNamespace(), "flag", flag));
//
//		/* добавление параметров pg_limit/offset ... */
//		// TODO: SearchHelper.assignArgs(sp, this.args);
//
//		/* запрос */
//		final DurationLogger d = new DurationLogger();
//		sp.setQuery(sbQuery.toString()); // example: TYPE:"{http://www.it.ru/lecm/model/blanks/1.0}blank" AND @"{http://www.it.ru/lecm/model/blanks/1.0}status":New AND @"{http://www.it.ru/lecm/model/blanks/1.0}flag":false
//
////		final String msg = d.fmtDuration("{t} msec");
////		d.markStart();
////		log.info( "searchTime "+ msg);
//
//		ResultSet foundSet = null;
//		try {
//			// проходим по набору perms, эмулируя обращение к данным ...
//			foundSet = serviceRegistry.getSearchService().query(sp);
//			int i_total = 0, i_allow = 0, i_rawLength = -1;
//			if (foundSet != null) {
//				for(ResultSetRow row : foundSet) {
//					i_total++;
//					final NodeRef nr = row.getNodeRef();
//
//					final Map<QName, Serializable> props = nodesrv.getProperties(nr);
//
//					final Set<AccessPermission> perms = serviceRegistry.getPermissionService().getPermissions(nr);
//					final boolean hasAccess = (perms != null) && ((props != null) && props.size() > 0);
//
//					// if (hasAccess) i_allow++;
//				}
////				if (foundSet instanceof OrgStrucureAfterInvocationProvider.FilteringResultSet)
////					i_rawLength = ((OrgStrucureAfterInvocationProvider.FilteringResultSet)foundSet).rawLength();
//			}
//		} finally {
////			final String info = d.fmtDuration("{t} msec");
////			logger.info( "processTime "+ info);
//
//			if(foundSet != null)
//				foundSet.close();
//		}
//	}

}
