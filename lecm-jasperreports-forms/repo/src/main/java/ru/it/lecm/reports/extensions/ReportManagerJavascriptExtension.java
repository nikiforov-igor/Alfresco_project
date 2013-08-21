package ru.it.lecm.reports.extensions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.reports.api.ReportInfo;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDefaultsDesc;
import ru.it.lecm.reports.api.model.ReportDescriptor;

public class ReportManagerJavascriptExtension
		extends BaseWebScript 
		// implements ScriptApiReportManager
{
	public final static String REPORTS_EDITOR_URI = "http://www.it.ru/logicECM/reports/editor/1.0";
	public final static QName PROP_REPORT_DESCRIPTOR_IS_DEPLOYED = QName.createQName(REPORTS_EDITOR_URI, "reportIsDeployed");

	private static final transient Logger logger = LoggerFactory.getLogger(ReportManagerJavascriptExtension.class);

	private ReportsManager reportsManager;

	public ReportsManager getReportsManager() {
		return reportsManager;
	}

	public void setReportsManager(ReportsManager reportsManager) {
		this.reportsManager = reportsManager;
	}

	// @Override
	public boolean deployReport(final String reportDescNode) {
		PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

		logger.info( String.format( "deploying report '%s' ...", reportDescNode));
		boolean result = false;
		if (NodeRef.isNodeRef(reportDescNode)) {
			final NodeRef rdId = new NodeRef(reportDescNode);
			getReportsManager().registerReportDescriptor(rdId);
			result = true;
			serviceRegistry.getNodeService().setProperty(rdId, PROP_REPORT_DESCRIPTOR_IS_DEPLOYED, result);
		}
		logger.warn( String.format( "report '%s' %sdeployed", reportDescNode, (result ? "" : "NOT ")));
		return result;
	}

	// @Override
	public boolean undeployReport(final String reportCode) {
		PropertyCheck.mandatory(this, "reportsManager", getReportsManager());

		logger.info( String.format( "Undeploying report '%s' ...", reportCode));
		getReportsManager().unregisterReportDescriptor(reportCode);
		NodeRef report = getReportsManager().getReportDAO().getReportDescriptorNodeByCode(reportCode);
		if (report != null) {
			serviceRegistry.getNodeService().setProperty(report, PROP_REPORT_DESCRIPTOR_IS_DEPLOYED, false);
		}
		logger.warn( String.format( "report '%s' undeployed", reportCode));
		return true;
	}

	public List<ReportInfo> getRegisteredReports(String docTypes, boolean forCollection) {
		logger.debug( String.format( "getRegisteredReports(docTypes=[%s], forCollection=%s) ...", docTypes, forCollection));

		final List<ReportInfo> reports = new ArrayList<ReportInfo>();

		final String[] types = (docTypes != null && docTypes.length() > 0)
									? docTypes.split(",") // задан тип(типы) - значит фильтруем по ним
									: null;

		final List<ReportDescriptor> found = getReportsManager().getRegisteredReports(types, forCollection);
		if (found != null && !found.isEmpty()) {
			for (ReportDescriptor rd : found) {
				final ReportInfo ri = new ReportInfo(rd.getReportType(), rd.getMnem(), (rd.getFlags() != null) ? rd.getFlags().getPreferedNodeType() : null);
				ri.setReportName(rd.get(null, rd.getMnem()));
				reports.add(ri);
			}
		}

		logger.debug( String.format( "getRegisteredReports(docTypes=[%s], forCollection=%s) return count %s", docTypes, forCollection, reports.size()));
		return reports;
	}

	public ScriptNode generateReportTemplate(final String reportRef) {
		logger.debug( String.format( "generateReportTemplate(reportRef={%s}) ...", reportRef));

		final NodeRef report = new NodeRef(reportRef);
		final ReportDescriptor desc = getReportsManager().getReportDAO().getReportDescriptor(report);
		if (desc == null)
			return null;

		final byte[] content = getReportsManager().produceDefaultTemplate(desc);
		final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());

		final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		
		{ // формирование названия
			final ReportDefaultsDesc def = getReportsManager().getReportDefaultsDesc(desc.getReportType()); // умолчания для типа
			final String ext = (def != null ? def.getFileExtension() : null);
			String reportTemplateName = desc.getMnem() + (Utils.isStringEmpty(ext) ? ".txt" : ext); // ".jrxml", etc ...

			properties.put(ContentModel.PROP_NAME, reportTemplateName);

			final NodeRef templateFile = serviceRegistry.getNodeService().getChildByName(report, ContentModel.ASSOC_CONTAINS, reportTemplateName);
			if (templateFile != null) {
				serviceRegistry.getNodeService().deleteNode(templateFile); // удаляем старый файл
			}
		}

		final ChildAssociationRef child =
				serviceRegistry.getNodeService().createNode(report, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, properties);
		InputStream is = null;
		try {
			ContentService contentService = serviceRegistry.getContentService();
			is = new ByteArrayInputStream(content);
			ContentWriter writer = contentService.getWriter(child.getChildRef(), ContentModel.PROP_CONTENT, true);
			writer.setEncoding("UTF-8");
			writer.setMimetype("text/xml");
			writer.putContent(is);
		} finally {
			IOUtils.closeQuietly(is);
		}

		logger.debug( String.format( "generateReportTemplate(reportRef={%s}) returns {%s}", reportRef, child.getChildRef()));

		return new ScriptNode(child.getChildRef(), serviceRegistry, getScope());
	}

}
