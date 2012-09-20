package ru.it.lecm.forms.jasperforms;

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

import java.io.Serializable;
import java.util.*;

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
	не удалять - нужен для инициализации класса при вызове из iReport
	 */
	public FilesDataSourceProvider() {
		super();
	}

	public FilesDataSourceProvider(Map<String, String[]> templateParams, ServiceRegistry serviceRegistry) {
		super(templateParams, serviceRegistry);
	}

	@Override
	protected void initFields() {
		//init fields list
		addField("name", String.class);
		addField("node-dbid", Long.class);
		addField("store-identifier", String.class);
		addField("locale", Locale.class);
		addField("modified", Date.class);
		addField("node-uuid", String.class);
	}

	@Override
	public JRDataSource create(JasperReport report) throws JRException {
		JRField[] fields = getFields(report);
		final String[] columnNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			JRField field = fields[i];
			columnNames[i] = field.getName();
		}
		final List<Object[]> records = new ArrayList<Object[]>();
		final List<FileInfo> fileInfos = getFileInfos();
		for (FileInfo fileInfo : fileInfos) {
			Object[] record = new Object[columnNames.length];
			for (int i = 0; i < columnNames.length; i++) {
				String columnName = columnNames[i];
				for (Map.Entry<QName, Serializable> entry : fileInfo.getProperties().entrySet()) {
					if (entry.getKey().getLocalName().equals(columnName)) {
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
		String[] nodeRefsStr = requestParameters.get("nodeRef");
		final List<NodeRef> nodeRefs = new ArrayList<NodeRef>();
		for (String nodeRefStr : nodeRefsStr) {
			nodeRefs.addAll(NodeRef.getNodeRefs(nodeRefStr));
		}
		final FileFolderService fileFolderService = serviceRegistry.getFileFolderService();
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();

		if (nodeRefs.size() == 1) {
			NodeRef folderRef = nodeRefs.get(0);
			if (fileFolderService.getFileInfo(folderRef).isFolder()) {
				fileInfos.addAll(fileFolderService.list(folderRef));
			} else {
				fileInfos.add(fileFolderService.getFileInfo(folderRef));
			}
		} else {
			for (NodeRef nodeRef : nodeRefs) {
				fileInfos.add(fileFolderService.getFileInfo(nodeRef));
			}
		}

		return fileInfos;
	}
}
