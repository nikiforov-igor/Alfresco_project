package ru.it.lecm.reports.generators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
import ru.it.lecm.reports.api.model.NamedValue;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.ReportFlags;
import ru.it.lecm.reports.beans.ReportProviderExt;
import ru.it.lecm.reports.beans.WKServiceKeeper;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

/**
 * Базовый класс для построителей отчётов в runtime.
 */
public abstract class ReportGeneratorBase
		implements ReportGenerator, ApplicationContextAware
{
	private static final transient Logger log = LoggerFactory.getLogger(ReportGeneratorBase.class);

	private WKServiceKeeper services; 
	private ReportsManager reportsMgr;
	private String reportsManagerBeanName;
	private ApplicationContext context;

	public void init() {
		log.info( String.format( "bean %s initialized", this.getClass().getName()));
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.context = ctx;
	}

	public WKServiceKeeper getServices() {
		return services;
	}

	public void setServices(WKServiceKeeper services) {
		this.services = services;
	}

	public String getReportsManagerBeanName() {
		return reportsManagerBeanName;
	}

	public void setReportsManagerBeanName(String beanName) {
		if ( Utils.isSafelyEquals(beanName, reportsManagerBeanName) )
			return;
		log.debug(String.format("ReportsManagerBeanName assigned: %s", beanName));
		this.reportsManagerBeanName = beanName;
		this.reportsMgr = null; // очистка
	}

	public ReportsManager getReportsManager() {
		if (this.reportsMgr == null && this.reportsManagerBeanName != null) {
			this.reportsMgr = (ReportsManager) this.context.getBean(this.reportsManagerBeanName);
		}
		return this.reportsMgr;
	}


	/**
	 * Предполагается, что входной поток это xml-макет для генератора ru.it.lecm.reports.generators.XMLMacroGenerator 
	 */
	@Override
	public byte[] generateReportTemplateByMaket(byte[] maketData, ReportDescriptor desc) {
		final XMLMacroGenerator xmlGenerator = new XMLMacroGenerator(desc);
		final ByteArrayOutputStream result = xmlGenerator.xmlGenerateByTemplate(
				new ByteArrayInputStream(maketData), "template for report " + desc.getMnem() );
		return (result != null) ? result.toByteArray() : null;
	}

	/**
	 * Создать объект указанного класса, потттом
	 * @param reportDesc
	 * @param dataSourceClass
	 * @param parameters
	 * @return созданный объект заказанного класса
	 * @throws IOException 
	 */
	protected JRDataSourceProvider createDsProvider( ReportDescriptor reportDesc
				, final String dataSourceClass
				, Map<String, String[]> parameters
	) throws IOException
	{
		final String failMsg = "Can not instantiate DataSourceProvider of class <" + dataSourceClass + ">";
		JRDataSourceProvider resultProvider = null;
		try {
			
			try {
				final Constructor<?> cons = Class.forName(dataSourceClass).getConstructor(ServiceRegistry.class); 
				resultProvider = (JRDataSourceProvider) cons.newInstance(getServices().getServiceRegistry());
			} catch (NoSuchMethodException e) {
				// если нет спец конструктора - пробуем обычный ...
				resultProvider = (JRDataSourceProvider) Class.forName(dataSourceClass).getConstructor().newInstance();
			}

			// "своих" особо облагородим ...
			if (resultProvider instanceof ReportProviderExt) {
				final ReportProviderExt adsp = (ReportProviderExt) resultProvider;

				adsp.setServices( this.getServices());
				adsp.setReportDescriptor( reportDesc);
				adsp.setReportManager( this.getReportsManager());
			}

			assignProviderProps( resultProvider, parameters, reportDesc);

		} catch (ClassNotFoundException e) {
			throw new IOException(failMsg + ". Class not found");
		} catch (NoSuchMethodException e) {
			throw new IOException(failMsg + ". Constructor not defined or has incorrect parameters");
		} catch (InvocationTargetException e) {
			throw new IOException(failMsg, e);
		} catch (InstantiationException e) {
			throw new IOException(failMsg, e);
		} catch (IllegalAccessException e) {
			throw new IOException(failMsg, e);
		}
		return resultProvider;
	}


	/**
	 * Присвоение свойств для Провайдера:
	 *    1) по совпадению названий параметров и свойств провайдера
	 *    2) по списку сконфигурированному списку алиасов для этого провайдера
	 * @param destProvider целевой Провайдер
	 * @param srcParameters список параметров
	 * @param srcReportDesc текущий описатель Отчёта, для получения из его флагов списка алиасов
	 * (в виде "property.xxx=paramName")
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void assignProviderProps(JRDataSourceProvider destProvider,
			Map<String, String[]> srcParameters, ReportDescriptor srcReportDesc
		) throws IllegalAccessException, InvocationTargetException
	{
		if (srcParameters != null && destProvider != null) {
			// присвоение сконфигурированных алиасов ...
			ArgsHelper.assignParameters( destProvider, getPropertiesAliases(srcReportDesc), srcParameters);

			// присвоение свойств с совпадающими именами с параметрами
			BeanUtils.populate(destProvider, srcParameters);
		}
	}

	/**
	 * Получить из флагов описателя отчёта список алиасов для именованных свойств.
	 * (Для случаев, когда название входного (web-)параметра отличается от 
	 * названия свойства провайдера, в которое это свойство должно попасть) 
	 *    ключ = название свойства провайдера,
	 *    значение = возможные синонимы в параметрах.
	 * см также ArgsHelper.assignParameters
	 */
	protected Map<String, String[]> getPropertiesAliases(ReportDescriptor reportDesc) {
		// выбираем из флагов дескриптора ...
		final Map<String, String[]> result = getPropertiesAliases( (reportDesc == null) ? null : reportDesc.getFlags());

		if (reportDesc != null && log.isDebugEnabled()) {
			log.debug(String.format( "Found parameters' aliases for provider %s:\n\t%s", reportDesc.getClass(), result));
		}

		return result;
	}

	/** префикс названия для конвертирующего свойства */
	final static String PFX_PROPERTY_ITEM = "property.".toLowerCase();

	/**
	 * Получить в списке reportFlags.flags() объекты относящиеся к свойствам.
	 * @param reportFlags
	 * @return
	 */
	protected Map<String, String[]> getPropertiesAliases(ReportFlags reportFlags) {
		// выбираем из флагов дескриптора ...
		if (reportFlags == null || reportFlags.flags() == null)
			return null;

		final Map<String, String[]> result = new HashMap<String, String[]>( reportFlags.flags().size());

		// сканируем параметры-флаги вида "property.XXX"
		for(NamedValue item: reportFlags.flags()) {
			if (item != null && item.getMnem() != null) {
				if (item.getMnem().toLowerCase().startsWith(PFX_PROPERTY_ITEM)) {
					// это описание конвертирования ...
					final String propName = item.getMnem().substring(PFX_PROPERTY_ITEM.length()); // часть строки после префикса это имя свойства (возможно вложенного)
					final String[] aliases = (Utils.isStringEmpty(item.getValue())) ? null : item.getValue().split("[,;]");
					if (aliases != null) {
						for(int i = 0; i < aliases.length; i++) {
							if (aliases[i] != null)
								aliases[i] = aliases[i].trim();
						}
					}
					result.put( propName, aliases);
				}
			}
		}

		return (result.isEmpty()) ? null : result;
	}

}
