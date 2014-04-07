package ru.it.lecm.ord;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.ord.api.ORDReportsService;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFileData;
import ru.it.lecm.reports.model.impl.ReportTemplate;

/**
 *
 * @author dbayandin
 */
public class ORDReportsServiceImpl extends BaseBean implements ORDReportsService{

	private final static Logger logger = LoggerFactory.getLogger(ORDReportsServiceImpl.class);
	private ReportsManager reportsManager;
	private DocumentAttachmentsService documentAttachmentsService;
	
	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }
	
    public void setReportsManager(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }
	
	@Override
	public NodeRef generateDocumentReport(final String reportCode, final String templateCode, final String documentRef) {
		ReportFileData result;
        try {
			Map<String,String> args = new HashMap<String,String>();
			args.put("ID", documentRef);
            result = reportsManager.generateReport(reportCode, templateCode, args);
        } catch (IOException ex) {
            final String msg = String.format("Exception at buildReportAndSave(reportCode='%s', documentRef={%s})", reportCode, documentRef);
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
		
		final NodeRef documentNodeRef = new NodeRef(documentRef);
		String reportNodeName = generateReportFileName(reportCode, templateCode, documentNodeRef);
		final NodeRef resultRef = saveAsAttachment(result, documentNodeRef, reportNodeName);
		
        return resultRef;
	}
	
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	private String generateReportFileName(final String reportCode, final String templateCode, NodeRef documentRef) {
		ReportDescriptor reportDesc = reportsManager.getRegisteredReportDescriptor(reportCode);
		ReportTemplate reportTemplate = reportsManager.getTemplateByCode(reportDesc, templateCode);
		
		String documentNumber = (documentRef != null) ? 
			(String) nodeService.getProperty(documentRef, DocumentService.PROP_REG_DATA_PROJECT_NUMBER) : "";
		
		String reportName = String.format(
			"%s-%s-%s", 
			reportTemplate.getDefault(),
			documentNumber,
			new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
		
		String fileName = FileNameValidator.getValidFileName(reportName);
		return fileName;
	}

	private NodeRef saveAsAttachment(ReportFileData result, NodeRef documentNodeRef, String reportNodeName) {
		NodeRef categoryRef = documentAttachmentsService.getCategory(
			ORDModel.ATTACHMENT_CATEGORIES_MAP.get(ORDModel.ATTACHMENT_CATEGORIES.DOCUMENT), 
			documentNodeRef);
		
		// сохранение внутри categoryRef ...
        final NodeRef resultRef = reportsManager.storeAsContent(result, categoryRef);
		String defaultReportName = (String) nodeService.getProperty(resultRef, ContentModel.PROP_NAME);
		String defaultReportExtension = defaultReportName.replaceFirst(".*\\.(?=[A-z0-9]+$)", "");
		String reportNodeFullName = String.format("%s.%s",reportNodeName,defaultReportExtension);
		
		NodeRef reportRef = null;
		List<AssociationRef> assocs = nodeService.getTargetAssocs(categoryRef, DocumentAttachmentsService.ASSOC_CATEGORY_ATTACHMENTS);
		for (AssociationRef assoc : assocs) {
			NodeRef attachmentRef = assoc.getTargetRef();
			String attachmentName = (String) nodeService.getProperty(attachmentRef, ContentModel.PROP_NAME);
			String[] attachmentNameParts = attachmentName.split("\\.(?=[A-z0-9]+$)");
			if (attachmentName.equals(reportNodeFullName)) {
				reportRef = attachmentRef;
			}
		}
		
		if (reportRef == null) {
			reportRef = resultRef;
						
			//saving new created report under necessary name
			nodeService.setProperty(reportRef, ContentModel.PROP_NAME, reportNodeFullName);
		} else {
			//moving content of created report to existing file
			Serializable newContent = nodeService.getProperty(resultRef, ContentModel.PROP_CONTENT);
			if (newContent != null) 
				nodeService.setProperty(reportRef, ContentModel.PROP_CONTENT, newContent);
			
			//deleting of new created file of report
			nodeService.addAspect(resultRef, ContentModel.ASPECT_TEMPORARY, null);
			nodeService.deleteNode(resultRef);
		}
        
		//adding necessary aspects
		nodeService.addAspect(reportRef, ContentModel.ASPECT_VERSIONABLE, null);
		nodeService.addAspect(reportRef, ContentModel.ASPECT_TEMPORARY, null);
		
		return reportRef;
	}
	
}
