/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.web.scripts.content.StreamContent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.FileNameValidator;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.signed.docflow.api.SignedDocflow;

/**
 *
 * @author ikhalikov
 */
public class DownloadAsZipWebscript extends StreamContent {

	private static final int BUFFER_SIZE = 2048;
	private ServiceRegistry serviceRegistry;
	private FileFolderService fileFolderService;
	private SignedDocflow signedDocflowService;
	private DictionaryService dictionaryService;
	private NodeService nodeService;
	private DocumentAttachmentsService documentAttachmentsService;

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setSignedDocflowService(SignedDocflow signedDocflowService) {
		this.signedDocflowService = signedDocflowService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	private final static Log logger = LogFactory.getLog(DownloadAsZipWebscript.class);
	private static final String MIMETYPE_APPLICATION_ZIP = "application/zip";
	private static final String NODEREF_PARAM = "nodeRef";

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		this.fileFolderService = serviceRegistry.getFileFolderService();
		NodeRef nodeRef = new NodeRef(req.getParameter(NODEREF_PARAM));
		String zipName = "";

		if (dictionaryService.isSubClass(nodeService.getType(nodeRef), DocumentService.TYPE_BASE_DOCUMENT)) {
			zipName = (String) nodeService.getProperty(nodeRef, DocumentService.PROP_PRESENT_STRING);
			writeZip(res.getOutputStream(), processDocument(nodeRef));
		} else {
			writeZip(res.getOutputStream(), processContent(nodeRef));
			zipName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		}
		zipName = FileNameValidator.getValidFileName(zipName);
		zipName = URLEncoder.encode(zipName, "UTF8");
		res.setContentType(MIMETYPE_APPLICATION_ZIP);
		String headerValue = "attachment; filename=\"" + zipName + ".zip\"; charset=UTF-8";
		res.setHeader("Content-Disposition", headerValue);
	}

	private Map<String, FileInfo[]> processContent(NodeRef nodeRef) {
		List<NodeRef> signsRef = signedDocflowService.getSignaturesByContent(nodeRef);
		List<NodeRef> content = new ArrayList<NodeRef>(signsRef);
		content.add(nodeRef);

		List<FileInfo> contentInfo = getFileInfo(content);
		Map<String, FileInfo[]> files = new HashMap<String, FileInfo[]>();
		files.put(fileFolderService.getFileInfo(nodeRef).getName(), contentInfo.toArray(new FileInfo[contentInfo.size()]));
		return files;
	}

	private Map<String, FileInfo[]> processDocument(NodeRef nodeRef) {
		List<NodeRef> attachments = getAttachments(nodeRef);
		Map<String, FileInfo[]> files = new HashMap<String, FileInfo[]>();
		for (NodeRef contentNodeRef : attachments) {
			files.putAll(processContent(contentNodeRef));
		}
		return files;

	}

	private void writeZip(OutputStream outputStream, Map<String, FileInfo[]> fileInfo) throws IOException {
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
					ArchiveEntry zipEntry = new ZipArchiveEntry(FilenameUtils.removeExtension(folderName) + "/" + file.getName());
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
		List<NodeRef> categories = documentAttachmentsService.getCategories(docNodeRef);
		for (NodeRef nodeRef : categories) {
			String categoryName = documentAttachmentsService.getCategoryName(nodeRef);
			result.addAll(documentAttachmentsService.getAttachmentsByCategory(docNodeRef, categoryName));
		}
		return result;
	}
}
