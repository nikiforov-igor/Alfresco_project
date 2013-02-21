package ru.it.lecm.integrotest.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.events.INodeACLBuilder.StdPermission;

public class Utils {

	final static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static String coalesce(Object ... values) {
		if (values != null)
			for(Object s: values) {
				if (s == null) continue;
				final String val = s.toString();
				if (val != null)
					return val; // FOUND non-null
			}
		return null;
	}

	/**
	 * Создать map с заданными аргументами.
	 * @param argstr строка с параметрами вида "a=b;c=d..." (разделитель запятая или точка с запятой)
	 * @return выбранные параметры, с удалёнными незначащими пробелами (в начале и в конце)
	 */
	final static public Map<String, String> makeArgsMap(String argstr){
		if (argstr == null)
			return null;
		final String[] pairs = argstr.split("[;,]");
		final Map<String, String> resultArgs = new HashMap<String, String>();
		for(String s: pairs) {
			if (s == null) continue;
			final String[] nameAndValue = s.split("=");
			if (nameAndValue == null || nameAndValue.length == 0) continue;
			final String value = (nameAndValue.length >= 2) ? nameAndValue[1].trim() : null;
			resultArgs.put(nameAndValue[0].trim(), value);
		}
		return resultArgs;
	}

	/**
	 * Создать SGPosition с заданными аргументами.
	 * @param argstr строка с параметрами вида "a=b;c=d..."
	 * @return
	 */
	final static public SGPosition makeSGPosition(String argstr){
		if (argstr == null)
			return null;
		final Map<String, String> args = makeArgsMap(argstr);
		return makeSGPosition(args);
	}

	/**
	 * Создать значение SGPosition согласно параметрам:
	 * 		"sgKind", "id", {"roleCode", "userId"}
	 * @param args
	 * @return
	 */
	// TODO: сделать возможным указывать не id, а коды или имена объектов
	final static public SGPosition makeSGPosition(Map<String, String> args){
		if (args == null)
			return null;
		final SGKind kind = SGKind.valueOf(args.get("sgKind").toUpperCase());
		final SGPosition result;
		switch(kind) {
			case SG_DP:
				result = SGKind.getSGDeputyPosition( args.get("id"), args.get("userId"));
				break;
			case SG_BRME:
				result = SGKind.getSGMyRolePos( args.get("id"), args.get("roleCode"));
				break;
			default: result = kind.getSGPos( args.get("id"));
		}
		return result;
	}

	/**
	 * Сформировать карту "БР-Доступ" согласно списку, заданному в виде строки.
	 * @param value список через ';' из записей "бизнес-роль:доступ;..."
	 *  	где роль = название роли (мнемоника),
	 *  		доступ = (noaccess | readonly | full)
	 * 			если доступ опущен, принимается за пустой
	 * @return
	 */
	final static public Map<String, StdPermission> makeBRoleMapping(String value) {
		final Map<String, StdPermission> result = new HashMap<String, StdPermission>();

		final Map<String, String> rawMap = makeSplitMapping(value); 
		if (rawMap != null && !rawMap.isEmpty()) {
			for(Map.Entry<String, String> entry: rawMap.entrySet() ) {
				try {
					final String brole = entry.getKey();
					final StdPermission access = (entry.getValue() != null) ? StdPermission.findPermission(entry.getValue().trim()) : null;
					result.put( brole, (access != null) ? access : StdPermission.noaccess);
				} catch(Throwable t) {
					logger.error( String.format("Check invalid map point '%s',\n\t expected to be 'BRole:access'\n\t\t, where access is (noaccess | readonly | full),\n\t\t BRole = mnemonic of business role"
							, entry.getKey() + ":"+ entry.getValue()), t);
				}
			}
		}

		return result;
	}

	/**
	 * Сформировать карту согласно списку, заданному в виде строки.
	 * @param value список через ';' из записей "ключ:значение;..."
	 * @return
	 */
	final static public Map<String, String> makeSplitMapping(String value) {
		final Map<String, String> result = new HashMap<String, String>();
		final String[] parts = value.split(";");
		if (parts != null) {
			for(String s: parts) {
				final String[] keyAndValue = s.split(":");
				if (keyAndValue.length == 0) continue;
				final String val = (keyAndValue.length >= 2) ? keyAndValue[1].trim() : null;
				result.put( keyAndValue[0], val);
			}
		}
		return result;
	}
}
