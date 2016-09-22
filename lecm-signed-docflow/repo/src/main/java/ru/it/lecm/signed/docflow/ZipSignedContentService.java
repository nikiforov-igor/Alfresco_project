package ru.it.lecm.signed.docflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author vlevin
 */
public class ZipSignedContentService extends BaseBean {

	private static final int BUFFER_SIZE = 2048;
	private FileFolderService fileFolderService;
	private SignedDocflow signedDocflowService;
	private DocumentAttachmentsService documentAttachmentsService;
	private DictionaryService dictionaryService;
	private final static Log logger = LogFactory.getLog(ZipSignedContentService.class);

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "fileFolderService", fileFolderService);
		PropertyCheck.mandatory(this, "signedDocflowService", signedDocflowService);
		PropertyCheck.mandatory(this, "documentAttachmentsService", documentAttachmentsService);
	}

	private Map<String, FileInfo[]> processContent(NodeRef nodeRef, boolean onlyOurSignatures, boolean createFolder) {
		List<NodeRef> signsRef = signedDocflowService.getSignaturesByContent(nodeRef);
		List<NodeRef> content;
		if (onlyOurSignatures) {
			content = new ArrayList<NodeRef>();
			for (NodeRef signatureRef : signsRef) {
				if (signedDocflowService.isOurSignature(signatureRef)) {
					content.add(signatureRef);
				}
			}
		} else {
			content = new ArrayList<NodeRef>(signsRef);
		}

		content.add(nodeRef);

		List<FileInfo> contentInfo = getFileInfo(content);
		Map<String, FileInfo[]> files = new HashMap<String, FileInfo[]>();
		String folderName = createFolder ? fileFolderService.getFileInfo(nodeRef).getName() : null;
		files.put(folderName, contentInfo.toArray(new FileInfo[contentInfo.size()]));
		return files;
	}

	private Map<String, FileInfo[]> processDocument(NodeRef nodeRef, boolean onlyOurSignatures) {
		List<NodeRef> attachments = getAttachments(nodeRef);
		Map<String, FileInfo[]> files = new HashMap<String, FileInfo[]>();
		for (NodeRef contentNodeRef : attachments) {
			files.putAll(processContent(contentNodeRef, onlyOurSignatures, true));
		}
		return files;

	}

	/**
	 * Архивирует контент (или все вложения документа) с привязанными к нему подписями в ZIP и пишет этот ZIP в выходной
	 * поток.
	 *
	 * @param outputStream поток, в который требуется записать архив
	 * @param nodeRef ссылка на документ, вложение документа или cm:content
	 * @param onlyOurSignatures архивировать только подписи нашей организации
	 * @return имя получившегося архива
	 * @throws IOException
	 */
	public String writeZipToStream(OutputStream outputStream, NodeRef nodeRef, boolean onlyOurSignatures) throws IOException {
		Map<String, FileInfo[]> fileInfo;
		String zipName;

		if (dictionaryService.isSubClass(nodeService.getType(nodeRef), DocumentService.TYPE_BASE_DOCUMENT)) {
			zipName = (String) nodeService.getProperty(nodeRef, DocumentService.PROP_PRESENT_STRING);
			fileInfo = processDocument(nodeRef, onlyOurSignatures);
		} else {
			zipName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			fileInfo = processContent(nodeRef, onlyOurSignatures, false);
		}

		ZipArchiveOutputStream zipOut = null;
		byte[] buf = new byte[BUFFER_SIZE];

		try {
			zipOut = new ZipArchiveOutputStream(outputStream);
			zipOut.setEncoding("UTF-8");
			zipOut.setUseLanguageEncodingFlag(true);
			zipOut.setFallbackToUTF8(true);
			for (Map.Entry<String, FileInfo[]> entry : fileInfo.entrySet()) {
				String folderName = entry.getKey();
				FileInfo[] fileInfos = entry.getValue();
				for (FileInfo file : fileInfos) {
					String filePath;

					if (folderName != null) {
						filePath = FilenameUtils.removeExtension(folderName) + "/" + file.getName();
					} else {
						filePath = file.getName();
					}

					ArchiveEntry zipEntry = new ZipArchiveEntry(filePath);
					zipOut.putArchiveEntry(zipEntry);

					int length;
					InputStream inputStream = getFileInputStream(file);
					while ((length = inputStream.read(buf)) > 0) {
						zipOut.write(buf, 0, length);
					}
					zipOut.closeArchiveEntry();
				}
			}

		} finally {
			try {
				if (zipOut != null) {
					zipOut.close();
				}
			} catch (Exception e) {
				logger.error("somethig goes bad", e);
			}
		}
		return zipName;
	}

	/**
	 * Распаковать указанный zip-архив в папку с сохранением структуры каталогов.
	 * @param file zip-архив.
	 * @param destDirectory целевой каталог
	 * @return список распакованных файлов.
	 */
	public List<File> unzipFile(File file, File destDirectory) {
		List<File> result = new ArrayList<File>();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration entries = zipFile.getEntries();
			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
				File entryDestination = new File(destDirectory, entry.getName());
				entryDestination.getParentFile().mkdirs();
				InputStream in = zipFile.getInputStream(entry);
				OutputStream out = new FileOutputStream(entryDestination);
				IOUtils.copy(in, out);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
				result.add(entryDestination);
			}
		} catch (IOException ex) {
			logger.error("Error opening archive", ex);
			throw new RuntimeException(ex);
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
					logger.error("Error closing archive", ex);
				}
			}
		}

		return result;
	}

	private InputStream getFileInputStream(FileInfo fileInfo) {
		ContentReader reader = fileFolderService.getReader(fileInfo.getNodeRef());
		InputStream is = reader.getContentInputStream();

		return is;
	}

	private List<FileInfo> getFileInfo(List<NodeRef> nodeRefList) {
		List<FileInfo> result = new ArrayList<FileInfo>();
		for (NodeRef nodeRef : nodeRefList) {
			result.add(fileFolderService.getFileInfo(nodeRef));
		}
		return result;
	}

	private List<NodeRef> getAttachments(NodeRef docNodeRef) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<NodeRef> categories = new ArrayList<NodeRef>();
		try{
			categories = documentAttachmentsService.getCategories(docNodeRef);
		}catch(WriteTransactionNeededException e){
			logger.error("error: ",e);
		}
		for (NodeRef nodeRef : categories) {
			String categoryName = documentAttachmentsService.getCategoryName(nodeRef);
			result.addAll(documentAttachmentsService.getAttachmentsByCategory(docNodeRef, categoryName));
		}
		return result;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
