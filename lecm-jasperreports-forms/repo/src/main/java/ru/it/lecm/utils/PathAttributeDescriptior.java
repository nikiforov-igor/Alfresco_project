package ru.it.lecm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.it.lecm.reports.api.AssocDataFilter.AssocDesc;

/**
 * Контейнер для представления ссылок вида "A/B/C/.../fld".
 * здесь "A", "B" ... это ссылочные атрибуты-ассоциации,
 * а вместо символа '/' могут применяться ссылки любого из 4х видов:
 *   1) child-ссылка (обычное "/")
 *   2) parent-ссылка ("..")
 *   3) target-ассоциация ("|/")
 *   4) source-ассоциация ("|..")
 * Для доп-го фильтрования объектов по типам при переходах по ссылкам, имеется 
 * необязательный элемент перед ассоциацией (ссылкой) - дискриминатор типа вида:
 *    "[тип]".
 * Например:
 *   "[lecm-orgstr:employee]lecm-contract:registrator-assoc/lecm-orgstr:employee-first-name"
 *
 * Если последний элемент не является "атомарным атрибутом", то используется "cm:name".
 *
 * @author Ruslan
 */
public class PathAttributeDescriptior {

	// public static String PATH_ATTR_SEPARATOR = "/";
//	protected static final Pattern REG_FORMAT = Pattern.compile(
//		//    source attribute                                destination attribute
//		//    [type            :]     id               [->    [type          :]     id               ]
//		"\\s*(([^:\\-\\s]*)\\s*:)?\\s*([^:\\-\\s]*)\\s*(->\\s*(([^:\\s]*)\\s*:)?\\s*([^:\\-\\s]*)\\s*)?");

	//	protected static final Pattern REG_FORMAT = Pattern.compile(
//			//    source attribute                                destination attribute
//			//    [type             ]           id              [->    [type          :]     id               ]
//			"\\s*((\\[[^:\\-\\s]*)\\s*\\])?\\s*([^:\\-\\s]*)\\s*(/|[|]/[.][.]|[|][.][.])?\\s*)+");

	// private Integer maxLength = null; // количество символов которые нужно вывести

	/**
	 * Последовательность атрибутов с данном пути.
	 * набор атрибутов. Все кроме последнего ссылочные, последний содержит 
	 * атомарный атрибут.
	 */
	private List<AssocDesc> track;

	public PathAttributeDescriptior(String reference) {
		// setOriginalRef(reference);
	}

	public PathAttributeDescriptior(AssocDesc assoc) {
		track = new ArrayList<AssocDesc>(1);
		track.add(assoc);
	}

//	public void setOriginalRef(String s) {
//		if (s == null)
//			return;
//
//		if (s.contains("#"))
//			s = ParseCell(s);
//
//		// список атрибутов
//		final String[] ids = s.split(PATH_ATTR_SEPARATOR);
//		track = new ArrayList<ObjectId>(ids.length);
//		for (String id : ids) {
//			track.add(getAttrId(id));
//		}
//
//	}
//
///*
//	public Integer getMaxLength() {
//		return maxLength;
//	}
//
//	public void setMaxLength(Integer newMaxLength) {
//		this.maxLength = newMaxLength;
//	}
// */
//
//	// id ::= [type:]code
//	private ObjectId getAttrId(String id) {
//		ObjectId result = null;
//
//		String code, type;
//		int posSep = id.indexOf(":");
//		code = id.substring(posSep + 1).trim();
//		type = posSep > 0 ? id.substring(0, posSep).trim() : null;
//
//		// используем псевдоатрибут, для получения типа связи для
//		// TypedCardLinkAttribute
//		if (code.equals("_CARDTYPE")) {
//			result = new ObjectId(ReferenceValue.class, "_CARDTYPE");
//		} else if (type != null) {
//			result = ObjectIdUtils.getObjectId(AttrUtils.getAttrClass(type),
//					code, false);
//		} else {
//			result = IdUtils.tryFindPredefinedObjectId(code);
//		}
//
//		return result;
//	}
//
//	public List<ObjectId> gettrack() {
//		return track;
//	}
//
//	public String getDelimiter() {
//		return delimiter;
//	}
//
//	public void setDelimiter(String newDelimiter) {
//		delimiter = newDelimiter;
//	}
//
//	// патерно разбора параметров
//	// легко добавить новые орпции...
//	final static String strptrn = "#[0-9]+|#delim='.*'";
//	final static Pattern pattern = Pattern.compile(strptrn);
//
//	/*
//	 * Распарсить строку str, убрать из неё опции, оставив только атрибут.
//	 * @param str строка и опции.
//	 * Пример: 
//	 *		str на входе: "link: jbr.ThemeOfQuery@NAME#40#delim='\\, '" 
//	 * 		вырезанные доп параметры: #40#delim='\\; '
//	 *		result = ""link: jbr.ThemeOfQuery@NAME"
//	 */ 
//	private String ParseCell(final String str) {
//		final int position = str.indexOf("#");
//		if (position < 0) return str;
//
//		final Matcher matcher = pattern.matcher(str);
//
//		while (matcher.find()) {
//			final String tmp = matcher.group();
//			if (tmp.contains("delim")) {
//				setDelimiter( tmp.replace("#delim='\\\\", "").replace("'", ""));
//			} else {
//				try {
//					setMaxLength( Integer.parseInt(tmp.replace("#", "")) );
//				} catch (Exception e) {
//					setMaxLength( null);
//				}
//			}
//		}
//		return str.substring(0, position);
//	}
}
