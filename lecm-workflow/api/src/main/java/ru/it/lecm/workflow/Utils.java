package ru.it.lecm.workflow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vlevin
 */
public class Utils implements ApplicationContextAware {

	private static DictionaryService dictionaryService;
	private static NodeService nodeService;
	private static OrgstructureBean orgstructureService;
	private static DocumentAttachmentsService documentAttachmentsService;
	private static VersionService versionService;
	private final static String BUSINESS_ROLE_CONTRACT_CURATOR_ID = "CONTRACT_CURATOR";
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static NodeRef getObjectFromBpmPackage(final NodeRef bpmPackage) {
		NodeRef result = getDocumentFromBpmPackage(bpmPackage);
		if (result == null) {
			result = getContentFromBpmPackage(bpmPackage);
		}
		return result;
	}

	private static NodeRef getSmthByTypeFromBpmPackage(NodeRef bpmPackage, QName type) {
		NodeRef documentRef = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage);
		if (children != null) {
			for (ChildAssociationRef assocRef : children) {
				NodeRef candidateRef = assocRef.getChildRef();
				if (dictionaryService.isSubClass(nodeService.getType(candidateRef), type)) {
					documentRef = candidateRef;
					break;
				}
			}
		} else {
			logger.error("List of bpm:package children is null");
		}
		return documentRef;
	}

	public static NodeRef getObjectFromPackageContents(final List<NodeRef> packageContents) {
		NodeRef result = getDocumentFromPackageContents(packageContents);
		if (result == null) {
			result = getContentFromPackageContents(packageContents);
		}
		return result;
	}

	public static NodeRef getContentFromPackageContents(List<NodeRef> packageContents) {
		return getSmthByTypeFromPackageContents(packageContents, ContentModel.TYPE_CONTENT);
	}

	private static NodeRef getSmthByTypeFromPackageContents(List<NodeRef> packageContents, QName type) {
		NodeRef documentRef = null;
		for (NodeRef node : packageContents) {
			if (dictionaryService.isSubClass(nodeService.getType(node), type)) {
				documentRef = node;
				break;
			}
		}
		return documentRef;
	}

	/**
	 * получение ссылки на документ через переменную регламента bpm_package
	 *
	 * @param bpmPackage
	 * @return
	 */
	public static NodeRef getDocumentFromBpmPackage(final NodeRef bpmPackage) {
		return getSmthByTypeFromBpmPackage(bpmPackage, DocumentService.TYPE_BASE_DOCUMENT);
	}

	public static NodeRef getDocumentFromPackageContents(List<NodeRef> packageContents) {
		return getSmthByTypeFromPackageContents(packageContents, DocumentService.TYPE_BASE_DOCUMENT);
	}

	/**
	 * получение ссылки на cm:content через переменную регламента bpm_package
	 *
	 * @param bpmPackage
	 * @return
	 */
	public static NodeRef getContentFromBpmPackage(NodeRef bpmPackage) {
		return getSmthByTypeFromBpmPackage(bpmPackage, ContentModel.TYPE_CONTENT);
	}

	public static boolean isDocument(final NodeRef node) {
		boolean result;
		if (node != null) {
			result = dictionaryService.isSubClass(nodeService.getType(node), DocumentService.TYPE_BASE_DOCUMENT);
		} else {
			result = false;
		}
		return result;
	}

	public static boolean isBpmPackageContainsDocument(final NodeRef bpmPackage) {
		return getDocumentFromBpmPackage(bpmPackage) != null;
	}

	private static List<NodeRef> getAllContentsFromBpmPackage(NodeRef bpmPackage) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage);
		if (children != null) {
			for (ChildAssociationRef assocRef : children) {
				NodeRef candidateRef = assocRef.getChildRef();
				if (dictionaryService.isSubClass(nodeService.getType(candidateRef), ContentModel.TYPE_CONTENT)) {
					result.add(candidateRef);
				}
			}
		} else {
			logger.error("List of bpm:package children is null");
		}
		return result;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		nodeService = applicationContext.getBean("nodeService", NodeService.class);
		dictionaryService = applicationContext.getBean("dictionaryService", DictionaryService.class);
		orgstructureService = applicationContext.getBean("serviceOrgstructure", OrgstructureBean.class);
		versionService = applicationContext.getBean("versionService", VersionService.class);
		documentAttachmentsService = applicationContext.getBean("documentAttachmentsService", DocumentAttachmentsService.class);
	}

	/**
	 * return boss login or self employee login if boss is null
	 *
	 * @param executorPersonName
	 * @return
	 */
	public static String getExecutorBoss(String executorPersonName) {
		NodeRef executorEmployee = orgstructureService.getEmployeeByPerson(executorPersonName);
		NodeRef boss = orgstructureService.findEmployeeBoss(executorEmployee);
		return boss != null ? orgstructureService.getEmployeeLogin(boss) : null;
	}

	public static List<NodeRef> getCurators() {
		List<NodeRef> curators = orgstructureService.getEmployeesByBusinessRole(BUSINESS_ROLE_CONTRACT_CURATOR_ID, true);
		return curators != null ? curators : new ArrayList<NodeRef>();
	}

	/**
	 * Получить из документа версию вложения типа "Договор"
	 *
	 * @param contractDocumentRef ссылка на документ "Договор"
	 * @return последняя версия вложенного файла из категории "Договор"
	 */
	private static String getContractDocumentVersion(final NodeRef contractDocumentRef, final String documentAttachmentCategoryName) {
		NodeRef contractCategory = null;
		List<NodeRef> contractCategories = new ArrayList<NodeRef>();
		try{
			contractCategories = documentAttachmentsService.getCategories(contractDocumentRef);
		}catch(WriteTransactionNeededException e){
			logger.error("error: ",e);
		}
		for (NodeRef categoryRef : contractCategories) {
			String categoryName = (String) nodeService.getProperty(categoryRef, ContentModel.PROP_NAME);
			if (documentAttachmentCategoryName.equals(categoryName)) {
				contractCategory = categoryRef;
				break;
			}
		}
		if (contractCategory == null) {
			logger.error("Document {} has no Contracts attachment category", contractDocumentRef);
			return "0.0";
		}
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(contractCategory, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		if (childAssocs.isEmpty()) {
			logger.error("Document {} has no Contracts attachment", contractDocumentRef);
			return "0.0";
		} else if (childAssocs.size() > 1) {
			logger.error("Document {} has {} Contracts attachments. I'll use first.", contractDocumentRef, childAssocs.size());
		}

		NodeRef contractAttachmentRef = childAssocs.get(0).getChildRef();
		Collection<Version> attachmentVersions = documentAttachmentsService.getAttachmentVersions(contractAttachmentRef);
		if (attachmentVersions != null && !attachmentVersions.isEmpty()) {
			Version[] versionsArray = attachmentVersions.toArray(new Version[]{});
			return versionsArray[0].getVersionLabel();
		} else {
			return "1.0";
		}
	}

	public static String getObjectVersion(final NodeRef bpmPackage, final String documentAttachmentCategoryName) {
		NodeRef documentRef = getDocumentFromBpmPackage(bpmPackage);
		if (documentRef != null) {
			return getContractDocumentVersion(documentRef, documentAttachmentCategoryName);
		} else {
			return getContentVersion(getAllContentsFromBpmPackage(bpmPackage));
		}
	}

	private static String getContentVersion(List<NodeRef> contents) {
		String result;
		if (contents.size() == 1) {
			Version currentVersion = versionService.getCurrentVersion(contents.get(0));
			if (currentVersion == null) {
				result = "0.0";
			} else {
				result = currentVersion.getVersionLabel();
			}
		} else if (contents.isEmpty()) {
			result = "0.0";
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
			result = dateFormat.format(new Date());
		}
		return result;
	}

	/**
	 * Получить версию листа результатов. Проверяется наличие листа
	 * результатов с указанной версией в указанной папке. Если лист с такой
	 * версией уже есть, то версия инкреминируется. Пример: если есть лист
	 * результатов с версией 1.5, то будет создан новый с версией 1.5.1. После
	 * этого будет создан лист с версией 1.5.2 и т. д.
	 *
	 * @param version начальная версия листа результатов.
	 * @param parentRef каталог с листами результатов
	 * @param resultListNameFormat Формат имени листа результатов, в который подставляется версия
	 * @return версия листа результатов, которую можно безбоязненно
	 * использовать для нового листа
	 */
	public static String getResultListVersion(final String version, final NodeRef parentRef, final String resultListNameFormat) {
		String result;
		String versionedResultListName = String.format(resultListNameFormat, version);
		NodeRef resultListNode = nodeService.getChildByName(parentRef, ContentModel.ASSOC_CONTAINS, versionedResultListName);
		if (resultListNode == null) {
			return version;
		} else {
			String[] splittedVersion = version.split("\\.");
			if (splittedVersion.length == 2) {
				// В версии две цифры (1.5)
				result = version + ".1";
			} else if (splittedVersion.length == 3) {
				// В версии три цифры (1.5.3)
				String minorVersionStr = splittedVersion[2];
				int minorVersionInt = Integer.parseInt(minorVersionStr);
				minorVersionInt++;
				splittedVersion[2] = String.valueOf(minorVersionInt);
				result = StringUtils.join(splittedVersion, ".");
			} else {
				// Мы не должны сюда попасть
				logger.error("Error in version string: {}", version);
				return null;
			}
			return getResultListVersion(result, parentRef, resultListNameFormat);
		}
	}
}
