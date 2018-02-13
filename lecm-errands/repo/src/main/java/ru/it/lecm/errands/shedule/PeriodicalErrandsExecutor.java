package ru.it.lecm.errands.shedule;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.*;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.errands.shedule.processors.BaseCreationExceptionProcessor;
import ru.it.lecm.errands.shedule.processors.BaseCreationExceptionProcessor.ProcessorParamName;

import java.io.Serializable;
import java.util.*;

/**
 * User: mshafeev
 * Date: 24.07.13
 * Time: 15:48
 */
public class PeriodicalErrandsExecutor extends ActionExecuterAbstractBase {

    private final static Logger logger = LoggerFactory.getLogger(PeriodicalErrandsExecutor.class);

    private NodeService nodeService;
    private DocumentService documentService;
    private ErrandsService errandsService;
    private DocumentCopySettings copySettings;

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

    public List<BaseCreationExceptionProcessor> getExceptionProcessors() {
        return exceptionProcessors;
    }

    public void setExceptionProcessors(List<BaseCreationExceptionProcessor> exceptionProcessors) {
        this.exceptionProcessors = exceptionProcessors;
    }

    public void setErrandsService(ErrandsService errandsService) {
        this.errandsService = errandsService;
    }

    public void setCopySettings(DocumentCopySettings copySettings) {
        this.copySettings = copySettings;
    }

    public DocumentCopySettings getCopySettings() {
        return copySettings;
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
                switch (type) {
                    case DAILY:
                        createErrand = true;
                        break;
                    case WEEK_DAYS:
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
                        break;
                    case MONTH_DAYS:
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        for (int i = 0; i < data.length(); i++) {
                            int value = data.getInt(i);
                            if (value == dayOfMonth) {
                                createErrand = true;
                                break;
                            }
                        }
                        break;
                    case QUARTERLY:
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
                        break;
                    default:
                        logger.warn("Error parse periodical rule. Property 'type' has not supported value");
                        break;

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
        final String systemLogin = AuthenticationUtil.getFullyAuthenticatedUser();
        try {
            boolean createErrand = false;
            createErrand = doesRuleAllowCreation(periodicalErrandNodeRef);

            Map<ProcessorParamName, Object> processorParams = new HashMap<>();
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
            AuthenticationUtil.setFullyAuthenticatedUser(userLogin);
            final boolean finalCreateErrand = createErrand;
            AuthenticationUtil.runAs(() -> {
                if (finalCreateErrand) {
                    NodeRef newErrand = documentService.duplicateDocument(periodicalErrandNodeRef, copySettings);
                    logger.debug("Created errand [{}] based on periodical errand [{}]", newErrand, periodicalErrandNodeRef);
                    processorParams.put(ProcessorParamName.ERRAND, newErrand);

                    //set up limitation date
                    Map<QName, Serializable> additionalProps = new HashMap<>();
                    String dateRadio = (String) nodeService.getProperty(periodicalErrandNodeRef, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
                    Date date  = errandsService.calculatePeriodicalErrandControlDate(periodicalErrandNodeRef);
                    if (date != null) {
                        additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE, date);
                        additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, "DATE");
                    } else if ("LIMITLESS".equals(dateRadio)) {
                        additionalProps.put(ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO, "LIMITLESS");
                    }
                    additionalProps.put(ErrandsService.PROP_ERRANDS_IS_SHORT, true);
                    nodeService.addProperties(newErrand, additionalProps);

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
        } catch (Throwable e) {
            logger.error("PeriodicalErrandsExecutor failed to execute for periodical errand [" + periodicalErrandNodeRef + "]", e);
            throw e;
        } finally {
            AuthenticationUtil.setFullyAuthenticatedUser(systemLogin);
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }
}
