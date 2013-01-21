
package ru.it.lecm.utils.alfresco;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.alfresco.service.namespace.QName;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Вспомогательный класс для связывания pojo-данных и properties Alfresco.
 *  
 * @author rabdullin
 */
public class DataMapper<TModel extends Serializable> {

	final static protected Logger log = LoggerFactory.getLogger (DataMapper.class);

	public static class MapException extends Exception {

		private static final long serialVersionUID = 1L;

		public MapException() {
			super();
		}

		public MapException(String message, Throwable cause) {
			super(message, cause);
		}

		public MapException(String message) {
			super(message);
		}

		public MapException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * Соответствия атрибутов Alfresco -> pojo 
	 */
	private Map<String, String> mapPropsIntoPojo;

	/**
	 * флаг для пропуска свойств, которые не указаны в mapPropsIntoPojo
	 */
	private boolean enSkipUnmapedProps = true;

	public DataMapper() {
	}

	/**
	 * Словарь соот-вий свойств Alfresco и свойств бинов
	 * @return
	 */
	public Map<String, String> getMapPropsIntoPojo() {
		return mapPropsIntoPojo;
	}

	/**
	 * Задать словарь соот-вий свойств Alfresco и свойств бинов
	 * @param mapPropsIntoPojo
	 */
	public void setMapPropsIntoPojo(Map<String, String> mapPropsIntoPojo) {
		this.mapPropsIntoPojo = mapPropsIntoPojo;
	}

	/**
	 * Флаг для пропуска свойств, которые не указаны в mapPropsIntoPojo
	 * @return true (по-умолчанию), если свойства, которые есть в реальных данных
	 * Alfresco, но нет в mapPropsIntoPojo надо пропускать, false если надо
	 * поднимать исключения в таких случаях. 
	 */
	public boolean isEnSkipUnmapedProps() {
		return enSkipUnmapedProps;
	}

	/**
	 * Флаг для пропуска свойств, которые не указаны в mapPropsIntoPojo
	 * @param enSkipUnmapedProps true, если свойства, которые есть в реальных данных
	 * Alfresco, но нет в mapPropsIntoPojo надо пропускать, false если надо
	 * поднимать исключения в таких случаях.
	 */
	public void setEnSkipUnmapedProps(boolean enSkipUnmapedProps) {
		this.enSkipUnmapedProps = enSkipUnmapedProps;
	}

	/**
	 * Из указанных properties получить словарь преобразования, возможно 
	 * использование коротких синонимов типов - префиксов
	 * @param props список вида {имя атрибута alfresco}={название pojo-поля}
	 */
	public void setAlfrescoKeyMap(Properties props) {
		this.mapPropsIntoPojo = new HashMap<String, String>();
		if (props == null)
			return;
		for(Map.Entry<Object, Object> key: props.entrySet()) 
			this.mapPropsIntoPojo.put(key.getKey().toString(), key.getValue().toString());
	}

	/**
	 * Присвоить данные согласно текущему мапингу
	 * @param destBean целевой pojo-объект
	 * @param source источник со свойствами Alfresco
	 * @return воз-ет свой аргумент destBean
	 * @throws MapException поднимается если в source имеется свойство, которого 
	 * нет в mapPropsIntoPojo и устнаовлен enSkipUnmapedProps==false.
	 */
	public <T extends TModel> T assignPojo( T destBean, Map<QName, Serializable> source)
			throws MapException 
	{
		if (source == null || destBean == null)
			return destBean;
		for (Map.Entry<QName, Serializable> entry: source.entrySet() ) {
			final String propName = this.mapPropsIntoPojo.containsKey(entry.getKey())
						? this.mapPropsIntoPojo.get(entry.getKey())
						: null;
			if (propName == null) { // не задано присвоение ...
				final String msg = String.format("Alfresco property <%s> is not mapped", entry.getKey());
				if (enSkipUnmapedProps) {
					log.warn(msg + "-> skipped");
					continue;
				}
				log.error( msg);
				throw new MapException(msg);
			}
			// выполняем присвоение
			try {
				PropertyUtils.setProperty(destBean, propName, entry.getValue());
			} catch (Throwable ex) {
				final String msg = String.format("Fail to assign Alfresco property <%s> to bean field <%s>", entry.getKey(), propName);
				log.error( msg, ex);
				throw new MapException( msg, ex);
			}
		} // for
		return destBean;
	}


	// @SuppressWarnings("unused")
	@SuppressWarnings("rawtypes")
	static private final Map<Class<?>, DataMapper> mappers = new HashMap<Class<?>, DataMapper>();

	/**
	 * Задать мапер для указанного модельного класса из ресурcа
	 * @param className
	 * @param res содержит properties-файл 
	 * 		{имя атрибута alfresco}={название pojo-поля}
	 * @throws IOException
	 */
	public static void regData(String className, Resource res) {
		// TODO: загрузить props из ресурса: pojo_prop = alfresco prop
		if (res == null) return;

		try {
			final Class<?> cls = Class.forName(className);

			final Properties props = new Properties();
			final InputStream stm = res.getInputStream();
			try {
				props.load( stm);
				final DataMapper map = new DataMapper();
				map.setAlfrescoKeyMap(props);
				mappers.put(cls, map);
			} finally {
				org.apache.commons.io.IOUtils.closeQuietly(stm);
			}
		} catch(Throwable t) {
			final String msg = String.format( "Fail to assigne mapper for class %s", className);
			log.error(msg, t);
			throw new RuntimeException( msg, t);
		}
	}

	/*
	public void afterPropertiesSet() throws Exception
	{
		synchronized (isInitialized) {
			if(isInitialized) {
				return;
			}

			try {
				//Initializer init = new Initializer();
				final Resource[] xmls = new PathMatchingResourcePatternResolver(getClass().getClassLoader()).getResources(xmlFile);
				for (int i = 0; i < xmls.length; i++) {
					logger.info("Processing " + xmls[i].getDescription());
					final InputStream stm = xmls[i].getInputStream();
					try {
						new Initializer().parse(stm);
					} finally {
						IOUtils.closeQuietly(stm);
					}
				}
				//new Initializer().parse(new ClassPathResource(path).getInputStream());
			} catch (Exception e) {
				logger.error("Error reading query factory configuration", e);
				throw new RuntimeException("Error reading query factory configuration", e);
			}

			isInitialized = true;
		}
	}
 * */

	public static <TModel extends Serializable> DataMapper<TModel> getMapper(Class<TModel> modelClass
			) throws MapException 
	{
		@SuppressWarnings("unchecked")
		final DataMapper<TModel> result = (mappers.containsKey(modelClass)) ? mappers.get(modelClass) : null;
		if (result == null)
			throw new MapException( String.format( "No mapper for class %s", modelClass));
		return result;
	}
}

