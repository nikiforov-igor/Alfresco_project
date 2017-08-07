package ru.it.lecm.errands.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.alfresco.util.ParameterCheck;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.base.beans.RepositoryStructureHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.beans.ErrandsServiceImpl;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.statemachine.StatemachineModel;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: AIvkin
 * Date: 10.07.13
 * Time: 11:56
 */
public class ErrandsWebScriptBean extends BaseWebScript {

    final static protected Logger logger = LoggerFactory.getLogger(ErrandsWebScriptBean.class);

    public static final String EXECUTION_KEY = "Ожидает исполнения";
    public static final String ON_EXECUTION_KEY = "На исполнении";
    public static final String ON_REPORT_CHECK_KEY = "На проверке отчета";
    public static final String ON_COMPLETION_KEY = "На доработке";
    public static final int DEADLINE_DAY_COUNT = 5;
    private ErrandsService errandsService;

    private OrgstructureBean orgstructureService;
    private DocumentService documentService;
    private IWorkCalendar workCalendar;
    private NodeService nodeService;
    private DocumentConnectionService documentConnectionService;
	private LecmTransactionHelper lecmTransactionHelper;
    private ThreadPoolExecutor threadPoolExecutor;
    private TransactionService transactionService;
    private RepositoryStructureHelper repositoryStructureHelper;
    private CopyService copyService;
    private NamespaceService namespaceService;
    private DocumentAttachmentsService documentAttachmentsService;

    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
		this.lecmTransactionHelper = lecmTransactionHelper;
	}

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setRepositoryStructureHelper(RepositoryStructureHelper repositoryStructureHelper) {
        this.repositoryStructureHelper = repositoryStructureHelper;
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }
    public void setCopyService(CopyService copyService) {
        this.copyService = copyService;
    }

    public static enum IssuedByMeEnum {
        ISSUED_ERRANDS_ALL,
        ISSUED_ERRANDS_EXECUTION,
        ISSUED_ERRANDS_ON_EXECUTION,
        ISSUED_ERRANDS_ON_CHECK_REPORT,
        ISSUED_ERRANDS_ON_COMPLETION,
        ISSUED_ERRANDS_EXPIRED,
        ISSUED_ERRANDS_DEADLINE,
        ISSUED_ERRANDS_ALL_IMPORTANT,
        ISSUED_ERRANDS_EXECUTION_IMPORTANT,
        ISSUED_ERRANDS_ON_EXECUTION_IMPORTANT,
        ISSUED_ERRANDS_ON_CHECK_REPORT_IMPORTANT,
        ISSUED_ERRANDS_ON_COMPLETION_IMPORTANT,
        ISSUED_ERRANDS_EXPIRED_IMPORTANT,
        ISSUED_ERRANDS_DEADLINE_IMPORTANT
    }

	public void setErrandsService(ErrandsService errandsService) {
		this.errandsService = errandsService;
	}

	/**
	 * Получение узла с глобальными настройками поручений
	 * @return узел с глобальными настройками поручений
	 */
	public ScriptNode getSettingsNode() {
//		TODO: Метод getSettingsNode ранее был типа getOrCreate, поэтому здесь надо бы проверить ноду на
//		существование и создать при необходимости
//              нода создаётся при инициализации бина
		NodeRef settings = errandsService.getSettingsNode();
		return new ScriptNode(settings, serviceRegistry, getScope());
	}


        //TODO может быть создавать ноду с настройками при создании пользователя?
        //Пока будем создавать здесь - при входе в персональные настройки.

	/**
	 * Получение узла с настройка для поручений текущего пользователя
	 * @return узел с настройками поручений для текущего пользователя
	 */
	public ScriptNode getCurrentUserSettingsNode() {
		NodeRef settings = errandsService.getCurrentUserSettingsNode();
		if(settings == null) {
//			Вызывается без транзакции, поэтому обернём
			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {

				@Override
				public NodeRef execute() throws Throwable {
					return errandsService.createCurrentUserSettingsNode();
				}
			};
			settings = (NodeRef) lecmTransactionHelper.doInTransaction(cb, false);
		}
		return new ScriptNode(settings, serviceRegistry, getScope());
	}

	/**
	 * Получение списка сотрудников, доступных текущему пользователю для выбора
	 * исполнителя и соисполнителей
	 * @return список доступных исполнителей (сотрудников)
	 */
    public Scriptable getAvailableExecutors() {
        List<NodeRef> results = errandsService.getAvailableExecutors();
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

	/**
	 * Получение списка сотрудников, доступных данному пользователю для выбора
	 * исполнителя и соисполнителей
	 * @return список доступных исполнителей (сотрудников)
	 */
    public Scriptable getAvailableExecutors(ScriptNode employeeRef) {
        List<NodeRef> results = errandsService.getAvailableExecutors(employeeRef.getNodeRef());
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

	public Scriptable getAvailableExecutors(String employeeRef) {
        List<NodeRef> results = errandsService.getAvailableExecutors(new NodeRef(employeeRef));
        if (results != null) {
            return createScriptable(results);
        }
        return null;
    }

	/**
	 * Проверяет личные настройки "Без утверждения Инициатором"
	 * @return true - если в личных настройках выбрано "Без утверждения
	 * Инициатором"
	 */
	public boolean isDefaultWithoutInitiatorApproval() {
//		TODO: Метод isDefaultWithoutInitiatorApproval в итоге вызывает метод getCurrentUserSettingsNode,
//		который был ранее типа getOrCreate, теперь он разделён и надо проверить существование папки и
//		при необходимости создать её в короткой транзакции
//		if(errandsService.getCurrentUserSettingsNode() == null) {
//			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
//
//				@Override
//				public Void execute() throws Throwable {
//					errandsService.createCurrentUserSettingsNode();
//					return null;
//				}
//
//			};
//
////			TODO: Явно вызывается из вебскрипта без транзакции
//			lecmTransactionHelper.doInTransaction(cb, false);
//		}
		return  errandsService.isDefaultWithoutInitiatorApproval();
	}

	/**
	 * Получает инициатора по умолчанию из личных настроек
	 *
	 * @return ссылка на сотрудника
	 */
	public NodeRef getDefaultInitiator() {
//		TODO: Метод getDefaultSubject в итоге вызывает метод getCurrentUserSettingsNode,
//		который был ранее типа getOrCreate, теперь он разделён и надо проверить существование папки и
//		при необходимости создать её в короткой транзакции
//		if(errandsService.getCurrentUserSettingsNode() == null) {
//			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
//
//				@Override
//				public Void execute() throws Throwable {
//					errandsService.createCurrentUserSettingsNode();
//					return null;
//				}
//
//			};
//
////			TODO: Явно вызывается из вебскрипта без транзакции
//			lecmTransactionHelper.doInTransaction(cb, false);
//		}
		return  errandsService.getDefaultInitiator();
	}

	/**
	 * Получает тематику по умолчанию из личных настроек
	 *
	 * @return ссылка на элеменнт справочника "Тематика"
	 */
	public Scriptable getDefaultSubject() {
        final List<NodeRef> defaultSubject = errandsService.getDefaultSubject();
        return defaultSubject == null ? null : createScriptable(defaultSubject);
	}

    public Scriptable getMyDocumentErrands(ScriptNode document, String filter) {
        List<NodeRef> myErrands = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter,
                Arrays.asList(ErrandsService.ASSOC_ERRANDS_EXECUTOR, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS));
        return createScriptable(myErrands);
    }

    public Scriptable getDocumentErrandsIssuedByMe(ScriptNode document, String filter) {
        List<NodeRef> errandsIssuedByMe = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter,
                Arrays.asList(ErrandsService.ASSOC_ERRANDS_INITIATOR));
        return createScriptable(errandsIssuedByMe);
    }

    /**
     * Получить поручения на контроле
     * @param document ScriptNode документа
     * @param filter фильтр
     * @return массив нод поручений
     */
    public Scriptable getDocumentErrandsControlledMe(ScriptNode document, String filter) {
        List<NodeRef> errandsControlledMe = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter,
                Arrays.asList(ErrandsService.ASSOC_ERRANDS_CONTROLLER));
        return createScriptable(errandsControlledMe);
    }

    public Scriptable getDocumentErrandsOther(ScriptNode document, String filter) {
        List<NodeRef> errandsAll = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter, null);
        List<NodeRef> errandsList = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter,
                Arrays.asList(ErrandsService.ASSOC_ERRANDS_EXECUTOR, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS,
                        ErrandsService.ASSOC_ERRANDS_INITIATOR, ErrandsService.ASSOC_ERRANDS_CONTROLLER));

        errandsAll.removeAll(errandsList);

        return createScriptable(errandsAll);
    }

    /**
     * Получение всех поручений по документу
     * @param document ScriptNode документа
     * @param filter фильтр
     * @return массив нод поручений
     */
    public Scriptable getDocumentErrandsAll(ScriptNode document, String filter) {
        List<NodeRef> errandsAll = errandsService.getFilterDocumentErrands(document.getNodeRef(), filter, null);
        return createScriptable(errandsAll);
    }

    public Scriptable getActiveErrands(Scriptable paths,int skipCount, int maxItems) {
        List<NodeRef> activeErrands= errandsService.getActiveErrands(getElements(Context.getCurrentContext().getElements(paths)), skipCount, maxItems);
        return createScriptable(activeErrands);
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    private ArrayList<String> getElements(Object[] object){
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj != null && obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj != null && obj instanceof String){
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }

    public Scriptable getErrandsDocs(Scriptable paths,int skipCount, int maxItems) {
        return createScriptable(errandsService.getErrandsDocuments(getElements(Context.getCurrentContext().getElements(paths)), skipCount, maxItems));
    }

    /**
     * Получить список выданных текущим пользователем поручений по ключу (все, на исполнении, просроченные, с приближающимся сроком)
     * @return список поручений
     */
    public Scriptable getIssuedErrands(String filterType) {
        List<QName> types = new ArrayList<QName>(1);
        types.add(ErrandsService.TYPE_ERRANDS);

        Map<String, String> filters = DocumentStatusesFilterBean.getFilterForType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService));

        List<String> paths = Arrays.asList(documentService.getDocumentsFolderPath());

        List<String> statuses = new ArrayList<String>();

        String defFilter = DocumentStatusesFilterBean.getDefaultFilter(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), false);
        String sts = DocumentStatusesFilterBean.getFilterForType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService)).get(defFilter);
        for (String status : sts.split(",")) {
            if (status != null && !status.isEmpty()) {
                statuses.add(status.trim());
            }
        }

        List<SearchParameters.SortDefinition> sort = new ArrayList<SearchParameters.SortDefinition>();
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ErrandsService.PROP_ERRANDS_IS_IMPORTANT.toString(), false));
        sort.add(new SearchParameters.SortDefinition(SearchParameters.SortDefinition.SortType.FIELD, "@" + ErrandsService.PROP_ERRANDS_LIMITATION_DATE.toString(), false));

        NodeRef currentEmployee = orgstructureService.getCurrentEmployee();

        final String PROP_ITINITATOR =
                ErrandsService.PROP_ERRANDS_INITIATOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_AUTHOR =
                DocumentService.PROP_AUTHOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_ERRANDS_EXECUTORS =
                ErrandsService.PROP_ERRANDS_EXECUTORS_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXPIRED =
                ErrandsServiceImpl.PROP_ERRANDS_IS_EXPIRED.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_IMPORTANT =
                ErrandsServiceImpl.PROP_ERRANDS_IS_IMPORTANT.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        final String PROP_EXEC_DATE =
                ErrandsServiceImpl.PROP_ERRANDS_LIMITATION_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

        String currentEmployeeStr = ":\"" + currentEmployee.toString().replace(":", "\\:") + "\"";
        String issuedFilterQuery = "(@" + PROP_ITINITATOR + currentEmployeeStr +
                " OR @" + PROP_AUTHOR + currentEmployeeStr +
                " OR @" + PROP_ERRANDS_EXECUTORS + currentEmployeeStr + ")";

        if (filterType != null && !"".equals(filterType)) {
                switch (IssuedByMeEnum.valueOf(filterType.toUpperCase())) {
                    //просроченные
                    case ISSUED_ERRANDS_EXPIRED_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_EXPIRED: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXPIRED + ":true ";
                        break;
                    }
                    // на ожидании исполнения
                    case ISSUED_ERRANDS_EXECUTION_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_EXECUTION: {
                        statuses = getErrandsStatuses(filters, EXECUTION_KEY);
                        break;
                    }
                    // на исполнении
                    case ISSUED_ERRANDS_ON_EXECUTION_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_ON_EXECUTION: {
                        statuses = getErrandsStatuses(filters, ON_EXECUTION_KEY);
                        break;
                    }
                    //на проверке отчета (на контроле)
                    case ISSUED_ERRANDS_ON_CHECK_REPORT_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_ON_CHECK_REPORT: {
                        statuses = getErrandsStatuses(filters, ON_REPORT_CHECK_KEY);
                        break;
                    }
                    // на доработке
                    case ISSUED_ERRANDS_ON_COMPLETION_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_ON_COMPLETION: {
                        statuses = getErrandsStatuses(filters, ON_COMPLETION_KEY);
                        break;
                    }
                    //с приближающимся сроком
                    case ISSUED_ERRANDS_DEADLINE_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_DEADLINE: {
                        Date now = new Date();

                        Date deadlineDate = workCalendar.getNextWorkingDate(now, DEADLINE_DAY_COUNT, Calendar.DAY_OF_MONTH);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(deadlineDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        Date end = calendar.getTime();

                        final String MIN = DateFormatISO8601.format(now);
                        final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";

                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_EXEC_DATE + ":[\"" + MIN + " \" TO \"" + MAX + "\"]";

                        break;
                    }
                    // все
                    case ISSUED_ERRANDS_ALL_IMPORTANT: {
                        issuedFilterQuery += (issuedFilterQuery.length() > 0 ? " AND " : "") + " @" + PROP_IMPORTANT + ":true ";
                    }
                    case ISSUED_ERRANDS_ALL: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
        }

        List<NodeRef> refs = documentService.getDocumentsByFilter(types, paths, statuses, issuedFilterQuery, sort);
        return createScriptable(refs);
    }

    private List<String> getErrandsStatuses (Map<String, String> filters, String key) {
        List<String> statuses = new ArrayList<String>();
        String filtersStr = filters.get(key);
        String[] statusesArray = filtersStr.split(",");
        for (String st:statusesArray) {
            if (st != null && !st.isEmpty()) {
                statuses.add(st.trim());
            }
        }
        return statuses;
    }

    /**
     * Получить строку с параметрами для списка поручений. Метод используется в дашлете "Выданные мною поручения" для формирования адреса перехода по ссылкам
     * @return строка с параметрами (query=[query]&formId=[formId]&filterOver=[filterOver]#filter=[filter]
     */
    public String getIssuedFilter(String filterType) {
        StringBuilder builder = new StringBuilder();
        builder.append("query=");
        Map<String, String> filters = DocumentStatusesFilterBean.getFilterForType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService));
        String defFilter = DocumentStatusesFilterBean.getDefaultFilter(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), false);

        // список фильтров - по умолчанию, Все (не финальные)
        String statusesStr = filters.get(defFilter);
        String form = defFilter;

        switch (IssuedByMeEnum.valueOf(filterType.toUpperCase())) {
            // на исполнении - подменяем строку со статусами
            case ISSUED_ERRANDS_EXECUTION_IMPORTANT: {
            }
            case ISSUED_ERRANDS_EXECUTION: {
                String status = EXECUTION_KEY;
                statusesStr = filters.get(status);
                form = status;
                break;
            }
            default: {
                break;
            }
        }

        builder.append(statusesStr);
        builder.append("&formId=");
        builder.append(form);

        DocumentFilter docFilter = FiltersManager.getFilterById(filterType);
        if (docFilter != null) {
            builder.append("&filterOver=true");
            builder.append("#filter=")
                    .append(docFilter.getId())
                    .append("|")
                    .append(docFilter.getParamStr() != null ? docFilter.getParamStr() : "");
        }
        return builder.toString();
    }

    public Scriptable getAvailableEmployeesForChildErrand(String parent) {
        if (errandsService.getModeChoosingExecutors() == ErrandsService.ModeChoosingExecutors.ORGANIZATION) {
            return getAvailableExecutors();
        }
        if (NodeRef.isNodeRef(parent)) {
            NodeRef parentRef = new NodeRef(parent);
            QName type = nodeService.getType(parentRef);
			Scriptable result = null;
            if (type.equals(ErrandsService.TYPE_ERRANDS)) {
                Set<NodeRef> employees = new HashSet<NodeRef>();
                //соисполнители - подходят!
                List<AssociationRef> empRefs = nodeService.getTargetAssocs(parentRef, ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
                for (AssociationRef empRef : empRefs) {
                    employees.add(empRef.getTargetRef());
                }
                // подчиненые исполнителя - подходят!
                List<AssociationRef> bossRef = nodeService.getTargetAssocs(parentRef, ErrandsService.ASSOC_ERRANDS_EXECUTOR);
                if (bossRef != null && !bossRef.isEmpty()) {
                    List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(bossRef.get(0).getTargetRef());
                    employees.addAll(departmentEmployees);
                }
                result = createScriptable(new ArrayList<>(employees));
            } else {
				List<NodeRef> availableExecutors = errandsService.getAvailableExecutors();
				if (availableExecutors != null) {
					result = createScriptable(availableExecutors);
				}
			}
			return result;
        }
        return null;
    }

    public ScriptNode getAdditionalDocument(String errandNodeRef) {
        ParameterCheck.mandatory("errandNodeRef", errandNodeRef);
        NodeRef errandRef = new NodeRef(errandNodeRef);
        NodeRef additionalDocRef = errandsService.getAdditionalDocumentNode(errandRef);

        if (additionalDocRef != null) {
            return new ScriptNode(additionalDocRef, serviceRegistry, getScope());
        }

        return null;
    }

	/**
	 * Получение папки со ссылками
	 *
	 * @param documentRef nodeRef документа
	 * @return папка со ссылками
	 */
    public ScriptNode getLinkFolder(String documentRef){
//		TODO: Метод getLinksFolderRef,
//		был ранее типа getOrCreate, теперь он разделён и надо проверить существование папки и
//		при необходимости создать её в короткой транзакции
//              а может быть создавать её при создании документа?
        ParameterCheck.mandatory("documentRef", documentRef);
        final NodeRef document = new NodeRef(documentRef);
		NodeRef linksFolderRef = errandsService.getLinksFolderRef(document);
		if(linksFolderRef == null) {
			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> () {

				@Override
				public NodeRef execute() throws Throwable {
					return errandsService.createLinksFolderRef(document);
				}

			};

//			TODO: Явно вызывается из вебскрипта без транзакции
			linksFolderRef = (NodeRef) lecmTransactionHelper.doInTransaction(cb, false);
		}
        ParameterCheck.mandatory("documentRef", documentRef);
        return new ScriptNode(linksFolderRef, serviceRegistry, getScope());
    }

	/**
	 * Возвращает ссылки на внутренние и внешние объекты системы из формы
	 * поручения.
	 *
	 * @param documentRef nodeRef документа
	 * @return массив ссылок
	 */
    public Scriptable getLinks(String documentRef) {
//		TODO: Метод getLinks в итоге вызывает метод getLinksFolderRef,
//		который был ранее типа getOrCreate, теперь он разделён и надо проверить существование папки и
//		при необходимости создать её в короткой транзакции
//              Если здесь не создана папка для связей, то и создавать её не надо, просто связей нет.
        ParameterCheck.mandatory("documentRef", documentRef);
        final NodeRef document = new NodeRef(documentRef);
//		if(errandsService.getLinksFolderRef(document) == null) {
//			RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void> () {
//
//				@Override
//				public Void execute() throws Throwable {
//					errandsService.createLinksFolderRef(document);
//					return null;
//				}
//
//			};
//
////			TODO: Явно вызывается из вебскрипта без транзакции
//			lecmTransactionHelper.doInTransaction(cb, false);
//		}

        List<NodeRef> links = errandsService.getLinks(document);
        return createScriptable(links);
    }

	/**
	 * Возвращает ссылки на внутренние и внешние объекты системы из формы
	 * поручения.
	 *
	 * @param documentRef nodeRef документа
	 * @param association ассоциация, например: "lecm-errands:links-assoc",
	 * "lecm-errands:execution-links-assoc"
	 * @return массив ссылок
	 */
    public Scriptable getLinksByAssociation(String documentRef, String association) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        List<NodeRef> links = errandsService.getLinksByAssociation(document, association);
        return createScriptable(links);
    }

	/**
	 * Создание ссылки lecm-links:link
	 *
	 * @param nodeRef nodeRef документf
	 * @param name название ссылки
	 * @param url ссылка например: http://www.test
	 * @param isExecute true создание "lecm-errands:links-assoc" ассоциации
	 * false создание "lecm-errands:execution-links-assoc" оссоциации
	 * @return ссылка
	 */
    public NodeRef createLinks(String nodeRef, String name, String url, boolean isExecute){
//		TODO: Метод createLinks в итоге вызывает метод getLinksFolderRef,
//		который был ранее типа getOrCreate, теперь он разделён и надо проверить существование папки и
//		при необходимости создать её в короткой транзакции
		NodeRef document = new NodeRef(nodeRef);
		if(errandsService.getLinksFolderRef(document) == null) {
			try {
				errandsService.createLinksFolderRef(document);
			} catch (WriteTransactionNeededException ex) {
				throw new RuntimeException("Can't create links folder", ex);
			}
		}

        return errandsService.createLinks(document, name, url, isExecute);
    }

	/**
	 * Сохранение отчёта об исполнении
	 *
	 * @param documentRef nodeRef документа
	 * @param report текст отчёта об исполнении
	 */
	public void setExecutionReport(String documentRef, String report) {
		ParameterCheck.mandatory("documentRef", documentRef);
		ParameterCheck.mandatory("report", report);

		errandsService.setExecutionReport(new NodeRef(documentRef), report);
	}

	public Scriptable getChildErrands(String documentRef) {
		ParameterCheck.mandatory("documentRef", documentRef);
		NodeRef document = new NodeRef(documentRef);
		if (nodeService.exists(document)) {
			List<NodeRef> childErrands = errandsService.getChildErrands(document);
			return createScriptable(childErrands);
		}
		return null;
	}

    /**
     * Получение списка нод подчиненных резолюций
     * @param documentRef документ
     * @return список нод подчиненных резолюций
     */
    public Scriptable getChildResolutions(String documentRef) {
        ParameterCheck.mandatory("documentRef", documentRef);
        NodeRef document = new NodeRef(documentRef);
        if (nodeService.exists(document)) {
            List<NodeRef> childResolutions = errandsService.getChildResolutions(document);
            return createScriptable(childResolutions);
        }
        return null;
    }

    public ScriptNode getDashletSettings() {
        NodeRef settings = errandsService.getDashletSettingsNode();
        if(settings == null){
        	settings = errandsService.createDashletSettingsNode();
        }
        if(settings != null) {
        	return new ScriptNode(settings, serviceRegistry, getScope());
        }
        return null;
    }

    public void createErrands(final Map<String, Object> properties, ScriptNode parentErrandNode) {
        final NodeRef parentDoc = parentErrandNode.getNodeRef();
        final String user = AuthenticationUtil.getFullyAuthenticatedUser();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
                        @Override
                        public Void doWork() throws Exception {
                            return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                @Override
                                public Void execute() throws Throwable {
                                    Map<QName, Serializable> props = new HashMap<>();

                                    Object value = properties.get("lecmErrandWf_isImportant");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_IS_IMPORTANT, (Serializable) value);
                                    }

                                    value = properties.get("lecmErrandWf_title");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_TITLE, (Serializable) value);
                                    }

                                    value = properties.get("lecmErrandWf_content");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_CONTENT, (Serializable) value);
                                    }

                                    value = properties.get("lecmErrandWf_limitationDateRadio");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, (Serializable) value);
                                    }

                                    value = properties.get("lecmErrandWf_limitationDate");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE, (Serializable) value);
                                    }
                                    value = properties.get("lecmErrandWf_limitationDateDays");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS, (Serializable) value);
                                    }
                                    value = properties.get("lecmErrandWf_limitationDateType");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE, (Serializable) value);
                                    }
                                    value = properties.get("lecmErrandWf_periodically");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_IS_PERIODICALLY, (Serializable) value);
                                    }
                                    value = properties.get("lecmErrandWf_reportRequired");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_REPORT_REQUIRED, (Serializable) value);
                                    }
                                    value = properties.get("lecmErrandWf_reportRecipientType");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_REPORT_RECIPIENT_TYPE, (Serializable) value);
                                    }
                                    value = properties.get("lecmErrandWf_withoutInitiatorApproval");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_WITHOUT_INITIATOR_APPROVAL, (Serializable) value);
                                    }

                                    value = properties.get("lecmErrandWf_startDate");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_START_DATE, (Serializable) value);
                                    }

                                    value = properties.get("lecmErrandWf_isShort");
                                    if (value != null) {
                                        props.put(ErrandsService.PROP_ERRANDS_IS_SHORT, (Serializable) value);
                                    }

                                    String name = GUID.generate();

                                    NodeRef draft = repositoryStructureHelper.getDraftsRef(user);
                                    NodeRef errand = nodeService.createNode(
                                            draft,
                                            ContentModel.ASSOC_CONTAINS,
                                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)),
                                            ErrandsService.TYPE_ERRANDS,
                                            props).getChildRef();

                                    value = properties.get("lecmErrandWf_executorAssoc");
                                    if (value != null) {
                                        nodeService.createAssociation(errand, ((ScriptNode) value).getNodeRef(), ErrandsService.ASSOC_ERRANDS_EXECUTOR);
                                    }

                                    value = properties.get("lecmErrandWf_typeAssoc");
                                    if (value != null) {
                                        nodeService.createAssociation(errand, ((ScriptNode) value).getNodeRef(), ErrandsService.ASSOC_ERRANDS_TYPE);
                                    }

                                    value = properties.get("lecmErrandWf_coexecutorsAssoc");
                                    if (value != null) {
                                        value = getObjectsArray(value);
                                        Collection<ScriptNode> coexecutors = (Collection<ScriptNode>) value;
                                        for (ScriptNode coexecutor : coexecutors) {
                                            nodeService.createAssociation(errand, coexecutor.getNodeRef(), ErrandsService.ASSOC_ERRANDS_CO_EXECUTORS);
                                        }
                                    }

                                    value = properties.get("lecmErrandWf_controllerAssoc");
                                    if (value != null) {
                                        nodeService.createAssociation(errand, ((ScriptNode) value).getNodeRef(), ErrandsService.ASSOC_ERRANDS_CONTROLLER);
                                    }

                                    value = properties.get("lecmErrandWf_initiatorAssoc");
                                    if (value != null) {
                                        nodeService.createAssociation(errand, ((ScriptNode) value).getNodeRef(), ErrandsService.ASSOC_ERRANDS_INITIATOR);
                                    }

                                    value = properties.get("lecmErrandWf_subjectAssoc");
                                    if (value != null) {
                                        value = getObjectsArray(value);
                                        Collection<ScriptNode> subjects = (Collection<ScriptNode>) value;
                                        for (ScriptNode subject : subjects) {
                                            nodeService.createAssociation(errand, subject.getNodeRef(), DocumentService.ASSOC_SUBJECT);
                                        }
                                    }

                                    value = properties.get("lecmErrandWf_attachmentsAssoc");
                                    if (value != null) {
                                        value = getObjectsArray(value);
                                        NodeRef categoryRef = null;
                                        List<NodeRef> categories = documentAttachmentsService.getCategories(errand);
                                        if (categories != null) {
                                            for (NodeRef category : categories) {
                                                if (!documentAttachmentsService.isReadonlyCategory(category)) {
                                                    categoryRef = category;
                                                    break;
                                                }
                                            }
                                        }
                                        Collection<ScriptNode> attachments = (Collection<ScriptNode>) value;

                                        if (categoryRef != null) {
                                            for (ScriptNode attachment : attachments) {
                                                String attachName = nodeService.getProperty(attachment.getNodeRef(), ContentModel.PROP_NAME).toString();
                                                QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, attachName);
                                                copyService.copyAndRename(attachment.getNodeRef(), categoryRef, ContentModel.ASSOC_CONTAINS, assocQname, false);
                                            }
                                        }
                                    }
                                    nodeService.createAssociation(errand, parentDoc, ErrandsService.ASSOC_BASE_DOCUMENT);

                                    return null;
                                }
                            }, false, true);
                        }
                    }, user);
                } catch (Exception e) {
                    logger.error("Error while create errand", e);
                }
            }
        };
        threadPoolExecutor.execute(runnable);
    }

    public void createErrands(Scriptable json) {
        final String user = AuthenticationUtil.getFullyAuthenticatedUser();
        final Map<String, String> fields = (Map<String, String>) getValueConverter().convertValueForJava(json);

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
                        @Override
                        public Void doWork() throws Exception {
                            return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                                @Override
                                public Void execute() throws Throwable {
                                    Map<String, String> properties = new HashMap<>();
                                    Map<String, String> associations = new HashMap<>();

                                    for (Object entryObj : fields.entrySet()) {
                                        //Не знаю почему, но если для json.entrySet() сразу указать Map.Entry, то компилятор ругается
                                        Map.Entry entry = (Map.Entry) entryObj;

                                        String key = (String) entry.getKey();
                                        String field = key.replaceAll("prop_", "").replaceAll("assoc_", "").replaceAll("_", ":");
                                        String value = (String) entry.getValue();

                                        if (value != null && !value.isEmpty()) {
                                            if (key.startsWith("prop_")) {
                                                properties.put(field, value);
                                            } else if (key.startsWith("assoc_")) {
                                                associations.put(field, value);
                                            }
                                        }
                                    }

                                    documentService.createDocument(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService), properties, associations);
                                    return null;
                                }
                            }, false, true);
                        }
                    }, user);
                } catch (Exception e) {
                    logger.error("Error while create errand", e);
                }
            }
        };
        threadPoolExecutor.execute(runnable);
    }

    private Object getObjectsArray(Object value) {
        if (value instanceof NativeArray) {
            final NativeArray nativeArray = (NativeArray) value;
            final Collection<ScriptNode> arrayResult = new ArrayList<>();
            for (int i = 0; i < (int) nativeArray.getLength(); i++) {
                if (nativeArray.get(i, null) instanceof ScriptNode) {
                    arrayResult.add((ScriptNode) nativeArray.get(i, null));
                }
            }
            value = arrayResult;
        }
        return value;
    }

    public void changeStatusDocumentByExecution(ScriptNode doc) {
        NodeRef nodeRef = doc.getNodeRef();
        QName documentType = QName.createQName("lecm-incoming:document", namespaceService);
        QName transitionFromErrand = QName.createQName("http://www.it.ru/logicECM/incoming/1.0", "auto-transition-from-errand");
        AssociationRef assocErrandsExecutors = nodeService.getTargetAssocs(nodeRef, errandsService.ASSOC_ERRANDS_EXECUTOR).get(0);
        List<NodeRef> nodeRefList = documentConnectionService.getConnectedWithDocument(nodeRef, "onBasis", documentType);

        for (NodeRef ref : nodeRefList) {
            String status = String.valueOf(nodeService.getProperty(ref, StatemachineModel.PROP_STATUS));
            if (status.equals("Направлен на исполнение") || status.equals("На рассмотрении") || status.equals("Зарегистрирован")) {
                nodeService.setProperty(ref, transitionFromErrand, assocErrandsExecutors.getTargetRef());
            }
        }
    }

    public boolean isHideAdditionAttributes() {
        return errandsService.isHideAdditionAttributes();
    }

    public void sendCancelSignal(String errandRef, String reason, String senderRef) {
        NodeRef errand = new NodeRef(errandRef);
        NodeRef signalSender = new NodeRef(senderRef);
        if (nodeService.exists(errand)) {
            errandsService.sendCancelSignal(errand, reason, signalSender);
        }
    }

    public void resetCancelSignal(ScriptNode doc) {
        ParameterCheck.mandatory("nodeRef", doc.getNodeRef());
        errandsService.resetCancelSignal(doc.getNodeRef());
    }
}
