package ru.it.lecm.delegation.utils;

import org.alfresco.service.cmr.search.SearchParameters;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchHelper {

	/**
	 * Параметры json-настроек запроса
	 */
	public static final String SEARCHARG_PG_OFFSET = "pg_offset"; // целое от нуля, смещение первой возвращаемой строки в общем списке найденных
	public static final String SEARCHARG_PG_LIMIT = "pg_limit"; // целое, кол-во воз-мых значений, начиная от pg_offset

	private SearchHelper() {}

	/**
	 * Задать настройки поиска, прочитав их в указанном json-объекте.
	 * @param destSearch целевые параметры поиска
	 * @param args аргументы для присвоения destSearch (см const SEARCHARG_XXX);
	 * (!) здесь могут содержаться как поисковые настройки так и другие значения.
	 * При args == null не выполняется никаких присвоений.
	 * @param removeUsedArgs true, чтобы убрать из args опознанные аргументы
	 * с поисковыми настройками; false, оставить.
	 * @return значение destSearch
	 * @throws JSONException 
	 */
	public static SearchParameters assignArgs(SearchParameters destSearch
				, JSONObject args
				, boolean removeUsedArgs
	) throws JSONException
	{
		if (args != null) {
			if (args.has(SEARCHARG_PG_LIMIT)) {
				destSearch.setMaxItems(args.optInt(SEARCHARG_PG_LIMIT,  destSearch.getMaxItems()));
				if (removeUsedArgs)
					args.remove(SEARCHARG_PG_LIMIT);
			}
			if (args.has(SEARCHARG_PG_OFFSET)) {
				destSearch.setSkipCount(args.optInt(SEARCHARG_PG_OFFSET, destSearch.getSkipCount()));
				if (removeUsedArgs)
					args.remove(SEARCHARG_PG_OFFSET);
			}
		}
		return destSearch;
	} 

	/**
	 * Задать настройки поиска, прочитав их в указанном json-объекте.
	 * @param destSearch целевые параметры поиска
	 * @param args аргументы для присвоения destSearch (см const SEARCHARG_XXX);
	 * (!) здесь могут содержаться как поисковые настройки так и другие значения.
	 * При args == null не выполняется никаких присвоений.
	 * @return значение destSearch
	 * @throws JSONException 
	 */
	public static SearchParameters assignArgs(SearchParameters destSearch
			, final JSONObject args
			) throws JSONException {
		return assignArgs(destSearch, args, false);
	}
}
