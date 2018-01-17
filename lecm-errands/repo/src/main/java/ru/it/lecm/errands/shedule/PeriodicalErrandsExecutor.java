package ru.it.lecm.errands.shedule;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.shedule.exceptionProcessor.ProcessorParamName;
import ru.it.lecm.errands.shedule.periodicalErrandsCreation.BaseCreationExceptionProcessor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * User: mshafeev
 * Date: 24.07.13
 * Time: 15:48
 */
public class PeriodicalErrandsExecutor extends ActionExecuterAbstractBase {

    private final static Logger logger = LoggerFactory.getLogger(PeriodicalErrandsExecutor.class);

    private NodeService nodeService;
    private DocumentService documentService;
    private DocumentAttachmentsService documentAttachmentsService;
    private CopyService copyService;
    private NamespaceService namespaceService;

    private final static String WEEK_DAYS = "WEEKLY";
    private final static String MONTH_DAYS = "MONTHLY";
    private final static String DAILY = "DAILY";
    private final static String QUARTERLY = "QUARTERLY";

    // процессоры исключений создания поручения
    private List<BaseCreationExceptionProcessor> exceptionProcessors;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
        this.documentAttachmentsService = documentAttachmentsService;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    public void setCopyService(CopyService copyService) {
        this.copyService = copyService;
    }

    public List<BaseCreationExceptionProcessor> getExceptionProcessors() {
        return exceptionProcessors;
    }

    public void setExceptionProcessors(List<BaseCreationExceptionProcessor> exceptionProcessors) {
        this.exceptionProcessors = exceptionProcessors;
    }

    private boolean doesRuleAllowCreation(NodeRef periodicalErrandNodeRef) {
        boolean createErrand = false;
        String ruleContent = (String) nodeService.getProperty(periodicalErrandNodeRef, ErrandsService.PROP_ERRANDS_PERIODICAL_RULE);
        if (ruleContent != null) {
            try {
                JSONObject rule = new JSONObject(ruleContent);
                String type = rule.getString("type");
                JSONArray data = rule.has("data") ? rule.getJSONArray("data") : new JSONArray();
                Calendar calendar = GregorianCalendar.getInstance();
                if (type.equals(DAILY)) {
                    createErrand = true;
                } else if (type.equals(WEEK_DAYS)) {
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        dayOfWeek = 7;
                    } else {
                        dayOfWeek -= 1;
                    }
                    for (int i = 0; i < data.length(); i++) {
                        int value = data.getInt(i);
                        if (value == dayOfWeek) {
                            createErrand = true;
                            break;
                        }
                    }
                } else if (type.equals(MONTH_DAYS)) {
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    for (int i = 0; i < data.length(); i++) {
                        int value = data.getInt(i);
                        if (value == dayOfMonth) {
                            createErrand = true;
                            break;
                        }
                    }
                } else if (type.equals(QUARTERLY)) {
                    int currentMonthValue = calendar.get(Calendar.MONTH) + 1;
                    int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    Date startDate = (Date) nodeService.getProperty(periodicalErrandNodeRef, ErrandsService.PROP_ERRANDS_PERIOD_START);
                    calendar.setTime(startDate);
                    int startMonthValue = calendar.get(Calendar.MONTH) + 1;
                    if (currentMonthValue % 3 == startMonthValue % 3) {
                        for (int i = 0; i < data.length(); i++) {
                            int value = data.getInt(i);
                            if (value == currentDayOfMonth) {
                                createErrand = true;
                                break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                logger.warn("Error parse periodical rule", e);
            }
        }
        return createErrand;
    }

    @Override
    protected void executeImpl(Action action, final NodeRef periodicalErrandNodeRef) {
        logger.info("Start process periodical errand [{}]", periodicalErrandNodeRef);
        try {
            boolean createErrand = false;
            createErrand = doesRuleAllowCreation(periodicalErrandNodeRef);

            Map<ProcessorParamName, Object> processorParams = new HashMap<ProcessorParamName, Object>();
            processorParams.put(ProcessorParamName.PERIODICAL_ERRAND, periodicalErrandNodeRef);

            // Проверка не запрещает ли создание поручения хотя бы один из обработчиков исключений создания
            for (BaseCreationExceptionProcessor processor : exceptionProcessors) {
                if (processor.checkConditionsToProcess(processorParams)) {
                    final boolean allowCreation = processor.isAllowCreation(processorParams);
                    createErrand = createErrand && allowCreation;
                    if (!allowCreation) {
                        logger.debug("Exception processor {} permit creation errand based on periodical errand [{}].",
                                processor.getClass().getSimpleName(), periodicalErrandNodeRef);
                    }
                }
            }

            final String userLogin = nodeService.getProperty(periodicalErrandNodeRef, ContentModel.PROP_CREATOR).toString();
            final String systemLogin = AuthenticationUtil.getFullyAuthenticatedUser();
            AuthenticationUtil.setFullyAuthenticatedUser(userLogin);
            final boolean finalCreateErrand = createErrand;
            AuthenticationUtil.runAs(() -> {
                if (finalCreateErrand) {
                    NodeRef newErrand = documentService.duplicateDocument(periodicalErrandNodeRef);
                    logger.debug("Created errand [{}] based on periodical errand [{}]", newErrand, periodicalErrandNodeRef);
                    processorParams.put(ProcessorParamName.ERRAND, newErrand);

                    //set up limitation date
                    Map<QName, Serializable> additionalProps = new HashMap<>();
                    String dateRadio = (String) nodeService.getProperty(periodicalErrandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
                    if ("DATE".equals(dateRadio)) {
                        Date periodicalLimitDate = (Date) nodeService.getProperty(periodicalErrandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);
                        if (periodicalLimitDate != null) {
                            Date now = new Date();
                            Date createdDate = (Date) nodeService.getProperty(periodicalErrandNodeRef, ContentModel.PROP_CREATED);
                            long dateDif = TimeUnit.DAYS.convert(periodicalLimitDate.getTime() - createdDate.getTime(), TimeUnit.MILLISECONDS);
                            Date newLimitDate = DateUtils.addDays(now, (int) dateDif);
                            additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE, newLimitDate);
                            additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, "DATE");
                        }
                    } else if ("DAYS".equals(dateRadio)) {
                        Integer days = (Integer) nodeService.getProperty(periodicalErrandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);
                        if (days != null) {
                            Date newLimitDate = DateUtils.addDays(new Date(), days);
                            additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE, newLimitDate);
                            additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, "DATE");
                        }

                    } else if ("LIMITLESS".equals(dateRadio)) {
                        additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, "LIMITLESS");
                    }
                    additionalProps.put(ErrandsService.PROP_ERRANDS_IS_SHORT, true);
                    nodeService.addProperties(newErrand, additionalProps);

                    //copy Categories
                    DocumentCopySettings settings = DocumentCopySettingsBean.getSettingsForDocType(ErrandsService.TYPE_ERRANDS.toPrefixString(namespaceService));
                    List<String> copiedCategories = new ArrayList<>();
                    if (settings != null) {
                        copiedCategories = settings.getCategoriesToCopy();
                    }
                    List<NodeRef> categoriesRefs = documentAttachmentsService.getCategories(periodicalErrandNodeRef);
                    List<String> finalCopiedCategories = copiedCategories;
                    List<String> categoriesToCopy = categoriesRefs.stream()
                            .map(ref -> documentAttachmentsService.getCategoryName(ref))
                            .filter(cat -> !finalCopiedCategories.contains(cat))
                            .collect(Collectors.toList());
                    for (String category : categoriesToCopy) {
                        NodeRef originalCategoryRef = documentAttachmentsService.getCategory(category, periodicalErrandNodeRef);

                        NodeRef categoryFolder = documentAttachmentsService.getCategory(category, newErrand);
                        if (originalCategoryRef != null && categoryFolder != null) {
                            List<ChildAssociationRef> childs = nodeService.getChildAssocs(originalCategoryRef);
                            for (ChildAssociationRef child : childs) {
                                NodeRef childRef = child.getChildRef();
                                List<AssociationRef> parentDocAssocs = nodeService.getTargetAssocs(childRef, DocumentService.ASSOC_PARENT_DOCUMENT);
                                for (AssociationRef parentDocAssoc : parentDocAssocs) {
                                    //для всех вложений получаем ссылку на родительский документ
                                    NodeRef parentDoc = parentDocAssoc.getTargetRef();
                                    // временно удаляем ассоциацию
                                    nodeService.removeAssociation(childRef, parentDoc, DocumentService.ASSOC_PARENT_DOCUMENT);
                                    try {
                                        copyService.copyAndRename(childRef, categoryFolder, ContentModel.ASSOC_CONTAINS,
                                                QName.createQName(ContentModel.PROP_CONTENT.getNamespaceURI(), nodeService.getProperty(childRef, ContentModel.PROP_NAME).toString()),
                                                false);
                                    } finally {
                                        // возвращаем удаленную ассоциацию
                                        try {
                                            nodeService.createAssociation(childRef, parentDoc, DocumentService.ASSOC_PARENT_DOCUMENT);
                                        } catch (AssociationExistsException ignored) {
                                        }
                                    }
                                }
                            }
                        }
                    }

                    int childErrandsCount = 0;
                    List<AssociationRef> childErrands = nodeService.getSourceAssocs(periodicalErrandNodeRef, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
                    if (childErrands != null) {
                        childErrandsCount = childErrands.size();
                    }
                    nodeService.setProperty(newErrand, ErrandsService.PROP_ERRANDS_CHILD_INDEX, childErrandsCount + 1);
                    nodeService.createAssociation(newErrand, periodicalErrandNodeRef, ErrandsService.ASSOC_ADDITIONAL_ERRANDS_DOCUMENT);
                } else {
                    logger.debug("Skipped to process periodical errand [{}] cause exceptionProcessors permit creation.", periodicalErrandNodeRef);
                }
                // Запуск процессоров исключений создания поручения
                for (BaseCreationExceptionProcessor processor : exceptionProcessors) {
                    if (processor.checkConditionsToProcess(processorParams)) {
                        processor.processException(processorParams);
                    }
                }
                return null;
            },  userLogin);
            AuthenticationUtil.setFullyAuthenticatedUser(systemLogin);
        } catch (Throwable e) {
            logger.error("PeriodicalErrandsExecutor failed to execute for periodical errand [" + periodicalErrandNodeRef + "]", e);
            throw e;
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }
}
