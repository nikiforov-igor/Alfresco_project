package ru.it.lecm.reports.generators;

import net.sf.jasperreports.engine.JRDataSourceProvider;
import org.alfresco.service.ServiceRegistry;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.reports.api.ReportGenerator;
import ru.it.lecm.reports.api.ReportsManager;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.model.impl.NamedValue;
import ru.it.lecm.reports.model.impl.ReportFlags;
import ru.it.lecm.reports.model.impl.ReportTemplate;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс для построителей отчётов в runtime.
 */
public abstract class ReportGeneratorBase implements ReportGenerator, ApplicationContextAware {
    private static final transient Logger log = LoggerFactory.getLogger(ReportGeneratorBase.class);

    /**
     * префикс названия для конвертирующего свойства
     */
    final static String PFX_PROPERTY_ITEM = "property.".toLowerCase();

    private WKServiceKeeper services;
    private LinksResolver resolver;

    private LucenePreparedQueryHelper queryHelper;

    public void setQueryHelper(LucenePreparedQueryHelper queryHelper) {
        this.queryHelper = queryHelper;
    }

    public LucenePreparedQueryHelper getQueryHelper() {
        return queryHelper;
    }

    public WKServiceKeeper getServices() {
        return services;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
    }

    public LinksResolver getResolver() {
        return resolver;
    }

    public void setResolver(LinksResolver resolver) {
        this.resolver = resolver;
    }

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        ctx = appContext;
    }

    public ApplicationContext getApplicationContext() {
        return ctx;
    }
    /**
     * Предполагается, что входной поток это xml-макет для генератора ru.it.lecm.reports.generators.XMLMacroGenerator
     */
    public abstract  byte[] generateReportTemplateByMaket(byte[] maketData, ReportDescriptor desc, ReportTemplate template);

    @Override
    public String getTemplateFileName(ReportDescriptor desc, ReportTemplate template, String extension){
        if (desc == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(desc.getMnem()).append("_");

        if (template != null) {
            builder.append(template.getMnem());
        }
        builder.append(extension);
        return builder.toString();
    }

    /**
     * Создать объект указанного класса, потттом
     *
     *
     *
     * @return созданный объект заказанного класса
     */
    protected JRDataSourceProvider createDsProvider(ReportsManager reportsManager, ReportDescriptor reportDesc, final String dataSourceClass, Map<String, Object> parameters){
        JRDataSourceProvider resultProvider = null;
        try {
            try {
                // если у нас ID бина - достаем из контекста
                resultProvider = (JRDataSourceProvider) getApplicationContext().getBean(dataSourceClass);
            } catch (BeansException ex) {
                try {
                    final Constructor<?> cons = Class.forName(dataSourceClass).getConstructor(ServiceRegistry.class);
                    resultProvider = (JRDataSourceProvider) cons.newInstance(getServices().getServiceRegistry());
                } catch (NoSuchMethodException e) {
                    // если нет спец конструктора - пробуем обычный ...
                    resultProvider = (JRDataSourceProvider) Class.forName(dataSourceClass).getConstructor().newInstance();
                }
            }
            // "своих" особо облагородим ...
            if (resultProvider != null && resultProvider instanceof ReportProviderExt) {
                final ReportProviderExt adsp = (ReportProviderExt) resultProvider;
                adsp.setReportsManager(reportsManager);
                adsp.initializeFromGenerator(this);

                adsp.setReportDescriptor(reportDesc);
            }

            assignProviderProps(resultProvider, parameters, reportDesc);

        } catch (Exception e) {
            log.error("Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">" + ". Class not found", e);
        }
        return resultProvider;
    }


    /**
     * Присвоение свойств для Провайдера:
     * 1) по совпадению названий параметров и свойств провайдера
     * 2) по сконфигурированному списку алиасов для этого провайдера
     *
     *
     *
     * @param destProvider  целевой Провайдер
     * @param srcParameters список параметров
     * @param srcReportDesc текущий описатель Отчёта, для получения из его флагов списка алиасов
     *                      (в виде "property.xxx=paramName")
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void assignProviderProps(JRDataSourceProvider destProvider, Map<String, Object> srcParameters, ReportDescriptor srcReportDesc)
            throws IllegalAccessException, InvocationTargetException {
        if (srcParameters != null && destProvider != null) {
            // присвоение сконфигурированных алиасов ...
            ArgsHelper.assignParameters(destProvider, getPropertiesAliases(srcReportDesc), srcParameters);
            // присвоение свойств с совпадающими именами с параметрами
            BeanUtils.populate(destProvider, srcParameters);
        }
    }

    /**
     * Получить из флагов описателя отчёта список алиасов для именованных свойств.
     * (Для случаев, когда название входного (web-)параметра отличается от
     * названия свойства провайдера, в которое это свойство должно попасть)
     * ключ = название свойства провайдера,
     * значение = возможные синонимы в параметрах.
     * см также ArgsHelper.assignParameters
     */
    protected Map<String, String> getPropertiesAliases(ReportDescriptor reportDesc) {
        // выбираем из флагов дескриптора ...
        final Map<String, String> result = getPropertiesAliases((reportDesc == null) ? null : reportDesc.getFlags());

        if (reportDesc != null && log.isDebugEnabled()) {
            log.debug(String.format("Found parameters' aliases for provider %s:\n\t%s", reportDesc.getClass(), result));
        }

        return result;
    }

    /**
     * Получить в списке reportFlags.flags() объекты относящиеся к свойствам.
     *
     *
     */
    protected Map<String, String> getPropertiesAliases(ReportFlags reportFlags) {
        // выбираем из флагов дескриптора ...
        if (reportFlags == null || reportFlags.flags() == null) {
            return null;
        }

        final Map<String, String> result = new HashMap<>(reportFlags.flags().size());

        // сканируем параметры-флаги вида "property.XXX"
        for (NamedValue item : reportFlags.flags()) {
            if (item != null && item.getMnem() != null) {
                if (item.getMnem().toLowerCase().startsWith(PFX_PROPERTY_ITEM)) {
                    // это описание конвертирования ...
                    final String propName = item.getMnem().substring(PFX_PROPERTY_ITEM.length()); // часть строки после префикса это имя свойства (возможно вложенного)
                    final String aliases = (Utils.isStringEmpty(item.getValue())) ? null : item.getValue();
                    if (aliases != null) {
                        result.put(propName, aliases);
                    }
                }
            }
        }

        return (result.isEmpty()) ? null : result;
    }
}
