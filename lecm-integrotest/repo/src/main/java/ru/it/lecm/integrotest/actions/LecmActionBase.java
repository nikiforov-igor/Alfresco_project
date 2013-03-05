package ru.it.lecm.integrotest.actions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.integrotest.RunAction;
import ru.it.lecm.integrotest.RunContext;

public abstract class LecmActionBase implements RunAction {

	final protected Logger logger = LoggerFactory.getLogger (LecmActionBase.class);

	private RunContext context;
	private String description;

	/**
	 * @return описание данного действия
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setContext(RunContext context) {
		this.context = context;
	}

	@Override
	public RunContext getContext() {
		return this.context;
	}

	@Override
	public abstract void run();

	public void run(RunContext runContext) {
		this.setContext(runContext);
		run();
	}

	/**
	 * @return объект для выполнения присвоений макроподобных значений
	 */
	public ByMacrosAssigner getArgsAssigner() {
		return new ByMacrosAssigner(
					  "result", context.results()
					, "config", context.configArgs()
					, "work", context.workArgs()); 
	}

	/**
	 * Класс используется для присвоения значений свойств внутренним maps/props
	 */
	@SuppressWarnings("rawtypes")
	public class ByMacrosAssigner {

		final Map<String, Map> macroData = new HashMap<String, Map>();

		/**
		 * @param args: пары значений: строка-ключ, объект типа Map (или Properties)
		 * т.е. парами - ключ, соот0щий контеёнер для хранения данных
		 */
		public ByMacrosAssigner(Object ... storage) {
			if (storage != null) {
				for (int i = 0; i < storage.length; i+=2) {
					if (! (storage[i+1] instanceof Map) )
						throw new RuntimeException( String.format( "Unsupported type '%s' - only Map enabled", storage[i+1].getClass().getName()));
					macroData.put( storage[i].toString().toLowerCase(), (Map) storage[i+1]);
				}
			}
		}

		/**
		 * Получить преобразованное значение, принимая что строка storeNameAndKey
		 * содержит название набора и ключ внутри этого набора
		 * @param storeNameAndKey в виде набор.ключ_в_наборе
		 * @return
		 */
		public Object getMacroValue(String storeNameAndKey) {
			if (storeNameAndKey == null)
				return null;
			final NameSplitter sp = new NameSplitter();
			if (!sp.split(storeNameAndKey))
				return null;
			final Map store = macroData.get(sp.keyName);
			return (store != null && store.containsKey(sp.tail))
					? store.get(sp.tail)
					: null;
		}

		/**
		 * Присвоение значения
		 * @param storeNameAndKey
		 * @param value
		 * @return карта, в которую присвоено значение
		 */
		public Map setMacroValue(String storeNameAndKey, Object value) {
			if (storeNameAndKey == null)
				return null;
			final NameSplitter sp = new NameSplitter();
			if (!sp.split(storeNameAndKey))
				return null;
			final Map store = macroData.get(sp.keyName);
			store.put(sp.tail, value);
			return store;
		}

		private class NameSplitter {
			private String keyName, tail;
			/**
			 * Выполнить разбор составного названия 
			 * @param longName составная строка вида "key.tail"
			 * (!) при отсутствии (пустых в том числе) key или tail поднимается исключение
			 * @return если выполнен разбор на два параметра и ключ существует 
			 * внутри списка macroData возвращается true, иначе false;
			 * если разбор не выполняется, поднимается исключение. 
			 */
			boolean split(String longName) {
				// final String parts[] = longName.split("[.]");
				// if (parts == null || parts.length < 2) throw new RuntimeException( String.format( "Illegaly formated string '%s' - must be 'abc.def...'", longName));
				final String msg = String.format( "Illegaly formated string '%s' must be 'abc.def': ", longName);
				final int i = longName.indexOf('.');
				if (i < 0) 
					throw new RuntimeException(msg + " '.' not found");

				this.keyName = longName.substring(0, i).trim().toLowerCase();
				if (keyName.length() == 0) 
					throw new RuntimeException(msg + " key value is empty");

				if (i == longName.length() -1)
					throw new RuntimeException(msg + " no value found");

				this.tail = longName.substring(i+1).trim();
				if (tail.length() == 0) 
					throw new RuntimeException(msg + " value is empty");

				if (!macroData.containsKey(this.keyName)) {
					logger.warn( String.format("store entry '%s' not found", this.keyName));
					return false;
				}

				return true; //OK
			}
		}
	}
}
