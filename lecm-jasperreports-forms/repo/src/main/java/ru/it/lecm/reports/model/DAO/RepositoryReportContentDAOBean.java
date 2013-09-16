package ru.it.lecm.reports.model.DAO;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.model.impl.ReportTypeImpl;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.NodeUtils;

/**
 * Служба хранения файлов, шаблонов и др контента, связанного с разворачиваемыми отчётами.
 * Атом хранения - файл или шаблон связанный с каким-либо отчётом.
 * Базовый объект типа "cm:content" имеет "cm:name" как ключ хранения.
 * Иерахия хранения:
 *   1. папка службы (как принято для lecm служб)
 *      2. папка "Типы отчётов"
 *         3. папка конкретного "Типа отчёта" (reportType)
 *            4. папка "Отчёт" (reportMnemo)
 *               5. [Файл/Контент] Название + данные 
 *                  здесь название файла должно быть уникально для своего отчёта
 *
 * @author rabdullin
 *
 */
public class RepositoryReportContentDAOBean 
		extends BaseBean
		implements ReportContentDAO
{
	/**
	 * для создания папки службы в репозитории, см также BaseBean.getFolder();
	 */
	final public static String REPORT_SERVICE_FOLDER_ROOT_ID = "REPORT_SERVICE_FOLDER_ID";
	final public static String REPORT_SERVICE_FOLDER_ROOT_NAME = "Сервис построения отчётов";

	/**
	 * Вложенная в ROOT службы папка с типом отчёта
	 */
	final public static String RS_FOLDER_REPORT_TYPES_ID = "REPORT_TYPES_FOLDER_ID";
	final public static String RS_FOLDER_REPORT_TYPES_NAME= "Типы отчётов";

	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryReportContentDAOBean.class);

	/** флаг запрета записи: true = запрещено, false = разрешено */
	private boolean readonly = false;

	public void init() {

		final NodeRef
				refRoot = getServiceRootFolder()
				, refTypes = getFolder(RS_FOLDER_REPORT_TYPES_ID)
				, refJasper = getFolder(ReportType.RTYPE_MNEMO_JASPER)
				, refOOffice = getFolder(ReportType.RTYPE_MNEMO_OOFFICE);

		logger.info( String.format( "Report Content Storage Service initialized as:\n\t%s %s\n\t%s %s\n\t%s %s\n\t%s %s\n\t%s %s"
				, "Readonly ", isReadonly()
				, "Root", refRoot
				, "\tTypes", refTypes
				, "\t\tTypes.Jasper", refJasper
				, "\t\tTypes.OOffice", refOOffice
		));
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append( String.format( "RepositoryReportContentDAOBean [readonly %s, root {%s} '%s']"
				, isReadonly()
				, getServiceRootFolder()
				, serviceFolders.get(REPORT_SERVICE_FOLDER_ROOT_ID)
		));
		return builder.toString();
	}


	@Override
	public boolean isReadonly() {
		return this.readonly;
	}

	@Override
	public void setReadonly(boolean value) {
		this.readonly = value;
	}

	/**
	 * Корневая папка lecm-службы (первый уровень)
	 */
	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(REPORT_SERVICE_FOLDER_ROOT_ID);
	}

	/**
	 * Гарантировать папку для типов отчётов (второй уровень)
	 * @return
	 */
	NodeRef getReportTypesFolder() {
		return getFolder(RS_FOLDER_REPORT_TYPES_ID);
	}

	/**
	 * Найти узел (3-го уровня) для указанного типа отчёта, если узла нет - вернёт NULL
	 * @param rtype тип отчёта
	 * @return узел для отчётов указанного типа или null, если не задана мнемоника для отчёта или ещё узла нет
	 */
	private NodeRef findRTypeNode(ReportType rtype) {
		if (rtype == null || Utils.isStringEmpty(rtype.getMnem()) )
			return null;
		return getFolder( getReportTypesFolder(), rtype.getMnem());
	}

	/**
	 * Создать узел (3-го уровня) для указанного типа отчёта
	 * @param rtype тип отчёта
	 * @return узел для отчётов указанного типа или null отчёта
	 */
	private NodeRef createRTypeNode(ReportType rtype) {
		if (rtype == null || Utils.isStringEmpty(rtype.getMnem()) )
			return null;
		return createFolder( getReportTypesFolder(), rtype.getMnem());
	}

	/**
	 * @param reportMnem
	 * @return Найти узел для указанного отчёта (4-го уровеня) по мнемонике и типу или вернуть NULL, если его нет
	 */
	private NodeRef findReportNode(ReportType rtype, String reportMnem) {
		if ( Utils.isStringEmpty(reportMnem))
			return null;
		final NodeRef nodeType = findRTypeNode(rtype);
		if (nodeType == null)
			return null;
		return getFolder( nodeType, reportMnem);
	}

	/**
	 * Создать узел (4-го уровня) для указанного отчёта.
	 * Родительские узлы создаются автоматом.
	 * Именно, в этом узле будут храниться файлы.
	 * @param reportMnem
	 * @return созданный узел
	 */
	private NodeRef createReportNode(ReportType rtype, String reportMnem) {
		if ( Utils.isStringEmpty(reportMnem))
			return null;
		// Узел типов отчёта (ур 3) ...
		NodeRef nodeType = findRTypeNode(rtype);
		if (nodeType == null)
			nodeType = createRTypeNode(rtype);

		// Узел самого отчёта (ур 4) ...
		NodeRef nodeReport = getFolder( nodeType, reportMnem);
		if (nodeReport == null)
			nodeReport = createFolder( nodeType, reportMnem);
		return nodeReport;
	}

	/**
	 * Вернуть узел для указанного отчёта и типа. Если нет - создать.
	 * @param rtype
	 * @param reportMnem
	 */
	private NodeRef ensureReportNode(ReportType rtype, String reportMnem) {
		NodeRef nodeReport = findReportNode( rtype, reportMnem);
		if (nodeReport == null)
			nodeReport = createReportNode( rtype, reportMnem);
		return nodeReport;
	}

	/**
	 * @param reportMnem
	 * @return Найти указанный отчёт по мнемонике и типу или вернуть NULL, если его нет
	 */
	private NodeRef findFileNode(ReportType rtype, String reportMnem, String fileName) {
		if ( Utils.isStringEmpty(fileName))
			return null;
		final NodeRef report = findReportNode( rtype, reportMnem);
		if (report == null)
			return null;
		if ( "*".equals(fileName))
			return report; // целиком узел для самого отчёта
		return getFolder(report, fileName);
	}

	private NodeRef findFileNode(IdRContent id) {
		return (id == null) 
					? null 
					: findFileNode( id.getReportType(), id.getReportMnemo(), id.getFileName());
	}

	@Override
	public boolean exists(IdRContent id) {
		return findFileNode(id) != null;
	}


	@Override
	public String getRoot() {
		return getServiceRootFolder().getId();
	}


	private void checkWriteable(IdRContent id, String operTag) {
		if (isReadonly())
			throw new RuntimeException( String.format("Cannot %s by id={%s} due to Readonly-mode", operTag, id));
	}

	@Override
	public int scanContent(final ContentEnumerator enumerator) {

		final NodeRef rootTypes = this.getFolder(RS_FOLDER_REPORT_TYPES_ID);

		// узлы и названия с соот-щих уровней
		final int levels = 3;
		final NodeRef[] refs = new NodeRef[levels];
		final String[] names = new String[levels];

		// проходим по всем типа, отчётам и файлам ...
		/*
		 * Иерахия хранения:
		 *   1. папка службы (как принято для lecm служб)
		 *      2. папка "Типы отчётов" 
		 *         [lev==1] 3. папка конкретного "Типа отчёта" (reportType)
		 *            [lev==2] 4. папка "Отчёт" (reportMnemo)
		 *               [lev==3] 5. [Файл/Контент] Название + данные 
		 *                  здесь название файла должно быть уникально для своего отчёта
		 */
		final int result = NodeUtils.scanHierachicalChilren( rootTypes
				, getNodeService()
				, levels
				, new NodeUtils.NodeEnumerator() {
					@Override
					public void lookAt(NodeRef node, List<NodeRef> parents) {
						if (enumerator != null) {
							// подгрузка названий ...
							for(int i = 0; i < refs.length; i++) {
								if (refs[i] == null || !refs[i].equals(parents.get(i))) {
									refs[i] = parents.get(i);
									names[i] = Utils.coalesce( getNodeService().getProperty(refs[i], ContentModel.PROP_NAME), "");
								}
							}
							final String nameNode = Utils.coalesce( getNodeService().getProperty( node, ContentModel.PROP_NAME), "");
							if (logger.isDebugEnabled()) {
								logger.debug( String.format( 
										"Scanning reports at:\n{%s} %s\n\t{%s} %s\n\t\t{%s} %s\n\t\t\t{%s} %s"
										, refs[0], names[0], refs[1], names[1], refs[2], names[2]
										, node, nameNode 
								));
							}

							final IdRContent id = new IdRContent( new ReportTypeImpl(names[1]), names[2], nameNode);
							enumerator.lookAtItem(id);
						}
					}
				}
		);

		return result;
	}

	@Override
	public void delete(IdRContent id) {
		if (id == null)
			return;
		checkWriteable( id, "delete");
		final NodeRef nodeFile = findFileNode(id);
		if (nodeFile != null) {
			nodeService.deleteNode(nodeFile);
			logger.info( String.format( "File node '%s'\n\t deleted by ref {%s}", id, nodeFile));
		}
	}

	@Override
	public ContentReader loadContent(IdRContent id) {
		ParameterCheck.mandatory("serviceRegistry", serviceRegistry);

		final NodeRef nodeFile = findFileNode(id);
		if (nodeFile == null)
			return null; // NOT FOUND

		// выдираем контент из узла типа "cm:content" ...
		final ContentReader reader = AuthenticationUtil.runAsSystem( new AuthenticationUtil.RunAsWork<ContentReader>() {
			@Override
			public ContentReader doWork() throws Exception {
				final ContentService contentService = serviceRegistry.getContentService();
				final ContentReader creader = contentService.getReader( nodeFile, ContentModel.PROP_CONTENT);
				return creader;
			}
		});

		return reader;
	}

	@Override
	public void storeContent(IdRContent id, InputStream stm) {
		if (id == null)
			return;

		checkWriteable( id, "store");

		ParameterCheck.mandatory("serviceRegistry", serviceRegistry);

		final NodeRef nodeReport = ensureReportNode(id.getReportType(), id.getReportMnemo());
		if (nodeReport == null)
			throw new RuntimeException( String.format( "Fail to create report node by: %s", id));

		// Убрать прежний контент ...
		/*
		{
			final NodeRef nodeFile = nodeService.getChildByName(nodeReport, ContentModel.ASSOC_CONTAINS, id.getFileName());
			if (nodeFile != null) {
				nodeService.deleteNode(nodeFile); // удаляем старый файл
				logger.debug( String.format( "File node '%s' deleted by ref {%s}", id, nodeFile));
			}
		}
		 */

		// Сохранение контента типа "cm:content" ...
		//
		final String localName = id.getFileName(); // UUID.randomUUID().toString()
		final QName assocQName = QName.createQName( NamespaceService.CONTENT_MODEL_PREFIX, localName, super.serviceRegistry.getNamespaceService());

		final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, id.getFileName());

		// запишем в прежний контент если узел был или создадим новый ...
		NodeRef nodeFile = nodeService.getChildByName(nodeReport, ContentModel.ASSOC_CONTAINS, id.getFileName());
		if (nodeFile == null) { // создание нового
			final ChildAssociationRef child =
					nodeService.createNode( nodeReport, ContentModel.ASSOC_CONTAINS, assocQName, ContentModel.TYPE_CONTENT, properties);
			nodeFile = child.getChildRef();
			logger.debug( String.format( "File node '%s'\n\t created by ref {%s}", id, nodeFile));
		}

		final ContentService contentService = serviceRegistry.getContentService();
		final ContentWriter writer = contentService.getWriter( nodeFile, ContentModel.PROP_CONTENT, true);
		try {
			// WritableByteChannel outer = writer.getWritableChannel();
			// writer.setEncoding("UTF-8"); writer.setMimetype("text/xml");
			writer.putContent(stm);
			logger.debug( String.format( "File node '%s'\n\t content saved %s bytes at ref {%s}", id, writer.getSize(), nodeFile));
		} finally {
			// IOUtils.closeQuietly(...);
		}
	}

}
