package ru.it.lecm.contracts.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.LecmTransactionHelper;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.documents.beans.DocumentFilter;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.FiltersManager;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.*;

/**
 * User: mshafeev
 * Date: 12.03.13
 * Time: 10:31
 */
public class ContractsWebScriptBean extends BaseWebScript {
    private ContractsBeanImpl contractService;
    protected NodeService nodeService;
    private OrgstructureBean orgstructureService;
    private PreferenceService preferenceService;
    private AuthenticationService authService;
    private NamespaceService namespaceService;
    private DocumentService documentService;
    private TransactionService transactionService;
    private DocumentMembersService documentMembersService;
    private LecmTransactionHelper lecmTransactionHelper;

    private final String[] contractsDocsFinalStatuses = {"Аннулирован", "Отменен", "Исполнен"};

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

    public void setLecmTransactionHelper(LecmTransactionHelper lecmTransactionHelper) {
        this.lecmTransactionHelper = lecmTransactionHelper;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    private static enum WhoseEnum {
        MY,
        DEPARTMENT,
        MEMBER
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
    }

    public void setPreferenceService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    public void setContractService(ContractsBeanImpl contractService) {
        this.contractService = contractService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ScriptNode getDraftRoot() {
//		Стоит учесть, что веб-скрипт, где используется этот метод не транзакционный, поэтому
//		Если какая-либо транзакция залочит ноду - получение отвалится по таймауту.
        NodeRef ref = contractService.getDraftRoot();
        if (ref == null) {
//			TODO: В случае, если папки черновиков ещё нет - создадим в транзакции.
            RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
            ref = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {

                @Override
                public NodeRef execute() throws Throwable {
                    return contractService.createDraftRoot();
                }

            });
        }
        return new ScriptNode(ref, serviceRegistry, getScope());
    }

    public String getDraftPath() {
        return contractService.getDraftPath();
    }

    /**
     * Получить количество договоров
     *
     * @param path       список путей поиска
     * @param properties список значений для фильтрации
     * @return количество
     */
    @SuppressWarnings("unused")
    public Integer getAmountContracts(Scriptable path, Scriptable properties) {
        return contractService.getContracts(getElements(Context.getCurrentContext().getElements(path)), getElements(Context.getCurrentContext().getElements(properties))).size();
    }

    /**
     * Получить количество участников договорной деятельности
     *
     * @return общее число участников
     */
    @SuppressWarnings("unused")
    public Integer getAmountMembers() {
//		TODO: Метод getAllMembers дёрагал метод getOrCreateDocMemberUnit,
//		который был благополучно разделён. Поэтому сделаем проверку на существование
        String type = ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT.toPrefixString(namespaceService).replaceAll(":", "_");
        if (documentMembersService.getDocMembersUnit(type) == null) {
            try {
                documentMembersService.createDocMemberUnit(type);
            } catch (WriteTransactionNeededException ex) {
                throw new RuntimeException("Can't create DocMemberUnit");
            }
        }
        return contractService.getAllMembers().size();
    }

    /**
     * Получить список участников договорной деятельности
     *
     * @param sortColumnName сортируемый атрибут
     * @param sortAscending  сортировка
     * @return список участников
     */
    @SuppressWarnings("unused")
    public Scriptable getMembers(String sortColumnName, Boolean sortAscending) {
//		TODO: Метод getAllMembers дёрагал метод getOrCreateDocMemberUnit,
//		который был благополучно разделён. Поэтому сделаем проверку на существование
        final String type = ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT.toPrefixString(namespaceService).replaceAll(":", "_");
        if (documentMembersService.getDocMembersUnit(type) == null) {
//			Вызывается из веб-скрипта без транзакции, обернём
            RetryingTransactionHelper.RetryingTransactionCallback cb = new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
                @Override
                public Void execute() throws Throwable {
                    documentMembersService.createDocMemberUnit(type);
                    return null;
                }
            };
            lecmTransactionHelper.doInTransaction(cb, false);
        }
        return createScriptable(contractService.getAllMembers(sortColumnName, sortAscending));
    }

    private ArrayList<String> getElements(Object[] object) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Object obj : object) {
            if (obj instanceof NativeJavaObject) {
                NativeJavaObject element = (NativeJavaObject) obj;
                arrayList.add((String) element.unwrap());
            } else if (obj instanceof String) {
                arrayList.add(obj.toString());
            }
        }
        return arrayList;
    }

    /**
     * Получить список договоров по фильтрам (дата последней смены статуса и принадлежность)
     *
     * @param daysCount  дней, в пределах которых менялся статус документа
     * @param userFilter фильтр по принадлежности(@link ContractsWebScriptBean.WhoseEnum)
     */
    public NodeRef[] getContractsByFilters(String daysCount, String userFilter) {
        List<QName> types = new ArrayList<QName>(2);
        types.add(ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT);
        types.add(ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

        Date now = new Date();
        Date start = null;

        if (daysCount != null && !"".equals(daysCount)) {
            Integer days = Integer.parseInt(daysCount);
            if (days > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, (-1) * (days - 1));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                start = calendar.getTime();
            }
        }

        String filterQuery = "";

        List<NodeRef> initiatorsList = null;

        if (userFilter != null && !"".equals(userFilter)) {
            NodeRef employee = orgstructureService.getCurrentEmployee();
            if (employee != null) {
                switch (WhoseEnum.valueOf(userFilter.toUpperCase())) {
                    case MY: {
                        initiatorsList = new ArrayList<>();
                        initiatorsList.add(employee);
                        break;
                    }
                    case DEPARTMENT: {
                        List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(employee);
                        if (!departmentEmployees.isEmpty()) {
                            initiatorsList = departmentEmployees;
                        }
                        break;
                    }
                    case MEMBER: {
                        final String PROP_MEMBERS =
                                DocumentMembersService.PROP_DOC_MEMBERS.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                        filterQuery += " @" + PROP_MEMBERS + ":*" + employee.toString().replace(":", "\\:") + "*";
                        break;
                    }
                    default: {
                        initiatorsList = new ArrayList<>();
                        initiatorsList.add(orgstructureService.getCurrentEmployee());
                        break;
                    }
                }

                StringBuilder employeesQuery = new StringBuilder();
                // фильтр по сотрудниками-создателям
                if (initiatorsList != null) {
                    if (!initiatorsList.isEmpty()) {
                        Map<QName, List<NodeRef>> initList = new HashMap<QName, List<NodeRef>>();
                        initList.put(ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT, initiatorsList);
                        initList.put(ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, initiatorsList);

                        boolean addOR = false;
                        for (QName type : types) {
                            String authorProperty = documentService.getAuthorProperty(type);
                            authorProperty = authorProperty.replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
                            for (NodeRef employeeRef : initList.get(type)) {
                                employeesQuery.append(addOR ? " OR " : "").append("@").append(authorProperty).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"");
                                addOR = true;
                            }
                        }

                        if (employeesQuery.length() > 0) {
                            filterQuery += (filterQuery.length() > 0 ? " AND (" : "") + employeesQuery.toString() + (filterQuery.length() > 0 ? ")" : "");
                        }
                    }
                } else {
                    return new NodeRef[0];
                }
            }
        }

        // Фильтр по датам
        final String MIN = start != null ? DateFormatISO8601.format(start) : "MIN";
        final String MAX = DateFormatISO8601.format(now);

        String property = DocumentService.PROP_STATUS_CHANGED_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
        filterQuery += (filterQuery.length() > 0 ? " AND " : "") + "@" + property + ":[\"" + MIN + "\" TO \"" + MAX + "\"]";

        List<NodeRef> refs = documentService.getDocumentsByFilter(types,
                Arrays.asList(contractService.getDraftPath(), contractService.getDocumentsFolderPath()), null, filterQuery, null);

        return refs.toArray(new NodeRef[refs.size()]);
    }

    /**
     * Получить список документов для договора для выбранного договора
     *
     * @param document нода договора
     */
    public Scriptable getAllContractDocuments(ScriptNode document) {
        List<NodeRef> additionalDocuments = this.contractService.getAllContractDocuments(document.getNodeRef());
        return createScriptable(additionalDocuments);
    }

    /**
     * Получить список документов для договора по заданным параметрам
     *
     * @param paths         пути, согласно которым проихзводить поиск
     * @param typeFilter    список типов документов к договору (через запятую)
     * @param queryFilterId идентификатор фильтра (для добавления к итоговому запросу)
     * @param activeDocs    искать по активным или архивным документам
     */
    @SuppressWarnings("unused")
    public Scriptable getAdditionalDocsByType(Scriptable paths, String typeFilter, String queryFilterId, boolean activeDocs) {
        String[] types = typeFilter != null && typeFilter.length() > 0 ? typeFilter.split("\\s*,\\s") : new String[0];
        StringBuilder filter = new StringBuilder();
        for (String type : types) {
            if (filter.length() > 0) {
                filter.append(" OR ");
            }
            filter.append("@lecm\\-additional\\-document\\:additionalDocumentType\\-text\\-content:\"").append(type).append("\"");
        }

        if (filter.length() > 0) {
            filter.insert(0, " (").append(") ");
        }

        if (queryFilterId != null && !queryFilterId.isEmpty()) {
            String filterId = DocumentService.PREF_DOCUMENTS + ".";
            if (!activeDocs) {
                filterId += "archive.";
            }
            filterId += ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT.toPrefixString(namespaceService).replaceAll(":", "_") + "." + queryFilterId;
            String currentUser = authService.getCurrentUserName();
            Map<String, Serializable> preferences = preferenceService.getPreferences(currentUser, filterId);
            Serializable filterPref = preferences.get(filterId);
            String filterData = filterPref != null ? filterPref.toString() : null;
            String employeesFilter = "";
            DocumentFilter docFilter = FiltersManager.getFilterById(queryFilterId);
            if (docFilter != null && filterData != null && !filterData.isEmpty()) {
                employeesFilter = docFilter.getQuery((Object[]) filterData.split("/"));
            }
            if (employeesFilter.length() > 0) {
                filter.append(" AND (").append(employeesFilter).append(")");
            }
        }
        List<QName> docType = new ArrayList<QName>();
        docType.add(ContractsBeanImpl.TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

        List<String> statuses = new ArrayList<String>();
        for (String finalStatus : contractsDocsFinalStatuses) {
            statuses.add((activeDocs ? "!" : "") + finalStatus);
        }
        List<NodeRef> additionalDocuments = this.documentService.getDocumentsByFilter(docType, getElements(Context.getCurrentContext().getElements(paths)), statuses, filter.toString(), null);
        return createScriptable(additionalDocuments);
    }

    public ScriptNode getDashletSettings() {
        return new ScriptNode(contractService.getDashletSettings(), serviceRegistry, getScope());
    }
}
