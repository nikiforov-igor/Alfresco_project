package ru.it.lecm.integrotest.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.security.LecmPermissionService.LecmPermissionGroup;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;

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
			case SG_ME:
				result = SGKind.getSGMyRolePos( args.get("id"), args.get("userLogin"), args.get("displayInfo"));
				break;
			default: result = kind.getSGPos( args.get("id"));
		}
		return result;
	}

	/**
	 * Сформировать карту Название-Доступ (например, "БР-Доступ" или "Пользователь-Доступ")
	 * согласно списку, заданному в виде строки "Имя:Доступ; Имя:Доступ ...".
	 * @param value список через ';' из записей "бизнес-роль:доступ;..."
	 *  	где роль = название роли (мнемоника),
	 *  		доступ = (noaccess | readonly | full)
	 * 			если доступ опущен, принимается за пустой
	 * @return
	 */
	final static public Map<String, String> makeBRoleMapping(String value) {
		final Map<String, String> result = new HashMap<String, String>();

		final Map<String, String> rawMap = makeSplitMapping(value); 
		if (rawMap != null && !rawMap.isEmpty()) {
			for(Map.Entry<String, String> entry: rawMap.entrySet() ) {
				try {
					final String keyName = entry.getKey().trim();
					final String access = (entry.getValue() != null) ? entry.getValue().trim() : null;
					result.put( keyName, (access != null) ? access : LecmPermissionGroup.PGROLE_Reader);
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
		final String[] parts = value.split("[;,]");
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

	final static public StringBuilder makeAttrDump(NodeRef node, NodeService nodeService) {
		return makeAttrDump(node, nodeService, String.format("\nAttributes of node {%s}:\n", node));
	}

	/**
	 * Сформировать дамп всех атрибутов указанного узла
	 * @param node
	 * @param nodeService
	 * @return
	 */
	final static public StringBuilder makeAttrDump(NodeRef node, NodeService nodeService, String msg) {
		final StringBuilder dump = new StringBuilder();
		if (msg != null)
			dump.append(msg);
		if (node != null) {
			final Map<QName, Serializable> chkData = nodeService.getProperties(node);
			int i = 0;
			dump.append( "============================================================\n");
			dump.append( String.format( "\t[%s]\t %15s \t <%s>\n", "nn", "attribute", "value"));
			dump.append( "============================================================\n");
			for (Map.Entry<QName, Serializable> e: chkData.entrySet() ) {
				i++;
				dump.append( String.format( "\t[%d]\t %15s \t <%s>\n", i, e.getKey(), e.getValue()));
			}
			dump.append( "============================================================\n");
		}
		return dump;
	}


	/**
	 * Make string enumeration of the items as list with delimiters.  
	 * @param col
	 * @param delimiter
	 * @param quoteOpen открывающая кавычка.
	 * @param quoteClose закрывающая кавычка.
	 * @return
	 */
	public static String getAsString( final Collection<?> col, 
			final String delimiter, String quoteOpen, String quoteClose)
	{
		if (col == null)
			return null;
		if (quoteOpen == null) quoteOpen = "";
		if (quoteClose == null) quoteClose = "";
		final StringBuffer result = new StringBuffer(5);
		final Iterator<?> itr = col.iterator();
		// final String fmtStr = (isStringEmpty(quote)) ? "{1}" : "{0}{1}{2}";
		while (itr.hasNext()) {
			final Object item = itr.next();

			final String strItem = (item != null) 
					? quoteOpen + item.toString() + quoteClose
					: "NULL" ;

			result.append(strItem);
			if (delimiter != null && itr.hasNext()) {
				result.append(delimiter);
			}
		}
		return result.toString();
	}

	/**
	 * Make string enumeration of the items as list with delimiters.  
	 * @param col
	 * @param delimiter
	 * @param quote ограничители-кавычки отдельных элементов.
	 * @return
	 */
	public static String getAsString( final Collection<?> col, 
			final String delimiter, final String quote) {
		return getAsString( col, delimiter, quote, quote);
	}

	/**
	 * Вернуть список с разделителем без ограничителей-кавычек.
	 * @param coll
	 * @param delimiter
	 * @return
	 */
	public static String getAsString(Collection<?> col, String delimiter) {
		return getAsString( col, delimiter, null);
	}

	/**
	 * Вернуть список с разделителем запятая.
	 * @param coll
	 * @return
	 */
	public static String getAsString(Collection<?> col) {
		return getAsString( col, ", ");
	}

	public static String getAsString(Object[] args) {
		return (args == null) ? "NULL" : getAsString( Arrays.asList(args), ", ");
	}
	
}
