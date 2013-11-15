package ru.it.lecm.reports.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.reports.api.model.ColumnDescriptor;


public class Utils {

    final static Logger logger = LoggerFactory.getLogger(Utils.class);

    final public static char QUOTE = '\"';
    private static final char CH_WALL = '\\'; // символ экранировки

    final public static int MILLIS_PER_DAY = 86400000;
    final public static long MILLIS_PER_HOUR = 1000 * 60 * 60;

    private Utils() {
    }

    /**
     * Check if string is empty or null.
     *
     * @param s String
     * @return true, if string is null or empty.
     */
    public static boolean isStringEmpty(final String s) {
        return (s == null) || (s.length() == 0);
    }

    public static String quoted(final String s) {
        return QUOTE + s + QUOTE;
    }

    public static String dequote(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }
        final int b = (s.charAt(0) == '"') ? 1 : 0;
        int e = s.length();
        if (s.charAt(e - 1) == QUOTE) {
            e--;
        }
        return s.substring(b, e);
    }

    /**
     * Экранировка символов [':', '-'] в указанной строке символом '\' для Lucene-строк
     *
     * @return String
     */
    public static String luceneEncode(String s) {
        return doCharsProtection(s, ":-");
    }

    /**
     * Экранировка указанных символов в строке символом '\'
     *
     * @param s     экранируемая строка
     * @param chars символы, которые подлежат экранировке
     * @return String
     */
    public static String doCharsProtection(String s, String chars) {
        if (isStringEmpty(s) || isStringEmpty(chars)) {
            return s;
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (CH_WALL == ch   /* сам символ "экрана" тоже надо экранировать */
                    || chars.indexOf(ch) >= 0
                    )// надо экранировку
                result.append(CH_WALL);
            result.append(ch); // сам символ
        }
        return result.toString();
    }

    /**
     * Выполнить расширение символьных пар "\X" в их кодовые эквиваленты.
     * <br/>Обратная к expandCharPairs.
     *
     * @param s String
     * @return String
     */
    public static String expandFromCharPairs(String s) {
        return (s == null) ? null : s.replaceAll("[\\\\][n]", "\n").replaceAll("[\\\\][r]", "\r").replaceAll("[\\\\][t]", "\t");
    }

    /**
     * Выполнить подстановку для символов, кодирующихся как '\X' в двухсимвольные последовательности.
     * <br/>Обратная к expandCharPairs.
     *
     * @param s String
     * @return String
     */
    public static String expandToCharPairs(String s) {
        return (s == null) ? null : s.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r").replaceAll("\t", "\\\\t");
    }

    /**
     * Get value, replacing empty one it by default value.
     *
     * @param obj String
     * @param defIfEmpty String
     * @return @param(obj) if it is not empty, otherwise @param(defIfEmpty).
     */
    public static String nvl(Object obj, String defIfEmpty) {
        return coalesce(obj, defIfEmpty);
    }

    /**
     * Вернуть первое непустое строковое представление элементов списка.
     *
     * @param values String
     * @return не-null строку, если в списке values нашёлся объект такой что
     *         .toString() != null и null если таких объектов нет.
     */
    public static String coalesce(Object... values) {
        if (values != null)
            for (final Object obj : values) {
                if (obj != null) {
                    final String val = obj.toString();
                    if (val != null)
                        return val; // FOUND NON-NULL value
                }
            }
        return null; // all vales are null
    }

    /**
     * Вернуть строку без пробелов в начале и конце (trim-строку).
     *
     * @param s        исходное значение (могут быть пробелы в начале/конце, может быть null)
     * @param sDefault значение по-умолчанию, используется если исходная строка пуста (null или из одних пробелов)
     * @return String
     */
    public static String nonblank(String s, String sDefault) {
        if (s != null) {
            s = s.trim();
            if (s.length() > 0) {
                // только непустые
                return s;
            }
        }
        return (sDefault != null) ? sDefault.trim() : null;
    }

    /**
     * @param values String[]
     * @return true, если среди values есть хотя бы одно не пустое значение
     */
    public static boolean hasNonEmptyValues(String[] values) {
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.isEmpty())
                    return true; // found
            }
        }
        // not found
        return false;
    }

    public static String trimmed(String s) {
        return (s == null) ? null : s.trim();
    }

    public static String dup(String s, int count) {
        final StringBuilder result = new StringBuilder();
        if (s != null && s.length() > 0)
            while (count > 0) {
                result.append(s);
            }
        return result.toString();
    }


    /**
     * Проверить что строка начинается с указанной строки и имеет ровно одно вхождение
     *
     * @param s String
     * @param what String
     * @return String
     */
    public static boolean hasStartOnce(final String s, final String what) {
        return (s != null) && (what != null)
                && (what.length() > 0)
                && (s.length() > what.length())
                && s.startsWith(what) // начиается с ...
                && (s.indexOf(what, 1) == -1); // больше вхождений нет ...
    }

    /**
     * Проверить что строка оканчивается указанной строкой и имеет ровно одно такое вхождение
     *
     * @param s String
     * @param what String
     * @return String
     */
    public static boolean hasEndOnce(final String s, final String what) {
        return (s != null) && (what != null)
                && (what.length() > 0)
                && (s.length() > what.length())
                && (s.indexOf(what) == s.length() - what.length()); // оканчивается указанной строкой и больше вхождений нет ...
    }

    /**
     * null-safe проверка объектов на равенство, при этом принимается null == null.
     *
     * @param a Object
     * @param b Object
     * @return true если объекты равны или оба одновременно null, иначе false
     */
    public static boolean isSafelyEquals(Object a, Object b) {
        return (a == b) || ((a != null) && (b != null) && a.equals(b));
    }

    /**
     * Журналирование данных.
     *
     * @param dest  целевой буфер для вывода
     * @param props список свойств для журналирования
     * @param info  сообщение, выводится в журнал если не null
     */
    public static StringBuilder dumpAlfData(final StringBuilder dest, final Map<?, ?> props, final String info) {
        final StringBuilder result = (dest != null) ? dest : new StringBuilder();
        if (info != null) {
            result.append(info);
        }
        result.append("\n");
        if (props != null) {
            result.append(String.format("\t[%s]\t %25s\t %s\n", 'n', "fldName", "value"));
            int i = 0;
            for (@SuppressWarnings("rawtypes") Map.Entry e : props.entrySet()) {
                i++;
                result.append(String.format("\t[%d]\t %25s\t '%s'\n", i, e.getKey(), nvl(e.getValue(), "NULL")));
            }
        }
        return result;
    }

    public static StringBuilder dumpAlfData(final Map<?, ?> props, final String info) {
        return dumpAlfData(new StringBuilder(), props, info);
    }


    /**
     * Make string enumeration of the items as list with delimiters.
     *
     * @param col Collection<?>
     * @param delimiter String
     * @param quoteOpen  открывающая кавычка.
     * @param quoteClose закрывающая кавычка.
     * @return String
     */
    public static String getAsString(final Collection<?> col, final String delimiter, String quoteOpen, String quoteClose) {
        if (col == null) {
            return null;
        }
        if (quoteOpen == null) quoteOpen = "";
        if (quoteClose == null) quoteClose = "";
        final StringBuilder result = new StringBuilder(5);
        final Iterator<?> itr = col.iterator();
        while (itr.hasNext()) {
            final Object item = itr.next();
            final String strItem = (item != null)
                    ? quoteOpen + item.toString() + quoteClose
                    : "NULL";

            result.append(strItem);
            if (delimiter != null && itr.hasNext()) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

    /**
     * Make string enumeration of the items as list with delimiters.
     *
     * @param col Collection
     * @param delimiter String
     * @param quote     ограничители-кавычки отдельных элементов.
     * @return String
     */
    public static String getAsString(final Collection<?> col, final String delimiter, final String quote) {
        return getAsString(col, delimiter, quote, quote);
    }

    /**
     * Вернуть список с разделителем без ограничителей-кавычек.
     *
     * @param col Collection
     * @param delimiter String
     * @return String
     */
    public static String getAsString(Collection<?> col, String delimiter) {
        return getAsString(col, delimiter, null);
    }

    /**
     * Вернуть список с разделителем запятая.
     *
     * @param col Collection
     * @return  String
     */
    public static String getAsString(Collection<?> col) {
        return getAsString(col, ", ");
    }

    public static String getAsString(Object[] args) {
        return (args == null) ? "NULL" : getAsString(Arrays.asList(args), ", ");
    }

    /**
     * Сформировать lucene-style проверку попадания поля даты в указанный интервал.
     * Формируется условие вида " @fld:[ x TO y]"
     * Если обе даты пустые - ничего не формируется
     *
     * @param fldName (!) экранированное имя поля, (!) без символа '@' в начале
     * @param from    дата начала
     * @param upto    дата конца
     * @return условие проверки вхождения даты в диапазон или NULL, если обе даты NULL
     */
    public static String emmitDateIntervalCheck(String fldName, Date from, Date upto) {
        final boolean needEmmition = (from != null || upto != null);
        if (!needEmmition) {
            return null;
        }

        // если даты не по-порядку - поменяем их местами
        if (from != null && upto != null) {
            if (from.after(upto)) {
                final Date temp = from;
                from = upto;
                upto = temp;
            }
        }

        // add " ... [X TO Y]"
        final String stMIN = ArgsHelper.dateToStr(from, "MIN");
        final String stMAX = ArgsHelper.dateToStr(upto, "MAX");
        return " ISNULL:\"" + fldName + "\" OR " + "@" + fldName + ":[" + stMIN + " TO " + stMAX + "]";
    }

    /**
     * Сформировать lucene-style проверку попадания поля числа в указанный интервал.
     * Формируется условие вида " @fld:[ x TO y]"
     * Если обе границы пустые - ничего не формируется
     *
     * @param fldName (!) экранированное имя поля, (!) без символа '@' в начале
     * @param from    числовая границы слева
     * @param upto    числовая границы справа
     * @return условие проверки вхождения числа в диапазон или NULL, если обе границы NULL
     */
    public static String emmitNumericIntervalCheck(String fldName, Number from, Number upto) {
        final boolean needEmmition = (from != null || upto != null);
        if (!needEmmition) {
            return null;
        }
        // если даты не по-порядку - поменяем их местами
        if (from != null && upto != null) {
            if (from.doubleValue() > upto.doubleValue()) {
                final Number temp = from;
                from = upto;
                upto = temp;
            }
        }

        // add " ... [X TO Y]"
        //  используем формат без разделителя, чтобы нормально выполнялся строковый поиск ...
        final String stMIN = (from != null) ? String.format("%12.0f", from.doubleValue()) : "MIN";
        final String stMAX = (upto != null) ? String.format("%12.0f", upto.doubleValue()) : "MAX";
        return " ISNULL:\"" + fldName + "\" OR " + "@" + fldName + ":[" + stMIN + " TO " + stMAX + "]";
    }


    /**
     * Сформировать условие для проверки значения на вхождение в список вида:
     * "( fld:value1 OR fld:value2 ...)"
     * (!) скобки включаются, операция между значениями "OR"
     *
     * @param fldName String
     * @param values String[]
     * @return  boolean
     */
    public static boolean emmitValuesInsideList(final StringBuilder result, String fldName, final String[] values) {
        if (!hasNonEmptyValues(values)) {
            return false;
        }

        // final StringBuilder result = new StringBuilder();
        final boolean isSpecialName = "TYPE ID".contains(fldName);
        //result.append("( ISNULL:\"" + fldName + "\") OR ( ");
        result.append("( ");
        boolean addOR = false;
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                final String quotedValue = Utils.quoted(value);
                if (addOR)
                    result.append(" OR ");
                if (!isSpecialName) // добавление '@' требуется ТОЛЬКО для обычных полей
                    result.append("@"); //
                result.append(fldName).append(":").append(quotedValue);
                addOR = true;
            }
        }
        result.append(") ");
        return true;
    }


    /**
     * Сгенерировать условие для единичного параметра (кавыки " добавляются здесь).
     * Если параметр не задан (null) - поднимается исключение с сообщением errMsg, если raiseIfNull=true.
     *
     * @param bquery      текст запроса для добавления
     * @param column ColumnDescriptor
     * @param prefix      текст перед добавляемым условием
     * @param errMsg      сообщение, выводимое при пустом параметре и raiseIfNull = true
     * @param raiseIfNull true, чтобы поднять исключение когда параметр не задан
     * @return true, если условия по параметру было добавлено
     *         и false, когда raiseIfNull = false или исключение иначе
     */
    public static boolean emmitParamCondition(final StringBuilder bquery
            , ColumnDescriptor column
            , final String prefix
            , final String errMsg
            , boolean raiseIfNull) {
        if (column == null || column.getParameterValue().isEmpty()) {
            if (raiseIfNull) {
                throw new RuntimeException(errMsg);
            }
            return false;
        }
        bquery.append(prefix).append(Utils.quoted(column.getParameterValue().getBound1().toString()));
        return true;
    }


    /**
     * Сгенерировать условие для единичного параметра (кавыки " добавляются здесь).
     * Если параметр не задан ничего не добавляется.
     *
     * @param bquery текст запроса для добавления
     * @param column ColumnDescriptor
     * @param prefix текст перед добавляемым условием
     * @return true, если условия по параметру было добавлено и false иначе
     */
    public static boolean emmitParamCondition(final StringBuilder bquery, ColumnDescriptor column, final String prefix) {
        return emmitParamCondition(bquery, column, prefix, null, false);
    }

    /**
     * Вычислить длительность в днях между парой дат
     *
     * @param startAt      время начала
     * @param endAt        время окончания
     * @param defaultValue значение по-умолчанию
     * @return разницу в днях (возможно нецелое значение) между двумя датами;
     *         значение по-умолчанию воз-ся если одна из дат или обе null.
     */
    final public static float calcDurationInDays(Date startAt, Date endAt, float defaultValue) {
        if (startAt == null || endAt == null)
            return defaultValue;
        final double duration_ms = (endAt.getTime() - startAt.getTime());
        return (float) (duration_ms / MILLIS_PER_DAY);
    }

    /**
     * По  названию класса вернуть известный класс или defaultClass.
     *
     * @param vClazz проверяемое название класса - полное имя типа или алиас
     * @return  Class
     */
    public static Class<?> getJavaClassByName(final String vClazz, final Class<?> defaultClass) {
        Class<?> byAlias = findKnownType(vClazz);
        if (byAlias != null) {
            return byAlias;
        }

        // не найдено синононима -> ищем по полному имени
        if (vClazz != null) {
            try {
                return Class.forName(vClazz);
            } catch (ClassNotFoundException ex) {
                // ignore class name fail by default
                logger.warn(String.format("Unknown java class '%s' -> used as '%s'", vClazz, defaultClass));
            }
        }

        return defaultClass;
    }

    // для возможности задавать типы алиасами
    private static Map<String, Class<?>> knownTypes = null;

    /**
     * По короткому названию класса вернуть известный класс или null, если такого не зарегистрировано.
     *
     * @param vClazzAlias проверяемый алиас класса
     * @return Class
     */
    private static Class<?> findKnownType(String vClazzAlias) {
        if (vClazzAlias == null) {
            return null;
        }

        if (knownTypes == null) {
            knownTypes = new HashMap<String, Class<?>>();

            // TODO: (RUSA) вынести всё это в бины

            knownTypes.put("integer", Integer.class);
            knownTypes.put("int", Integer.class);

            knownTypes.put("bool", Boolean.class);
            knownTypes.put("boolean", Boolean.class);
            // knownTypes.put("yesno", Boolean.class);
            // knownTypes.put("logical", Boolean.class);

            knownTypes.put("long", Long.class);
            knownTypes.put("longint", Long.class);

            knownTypes.put("id", String.class);
            knownTypes.put("string", String.class);

            knownTypes.put("date", Date.class);

            knownTypes.put("numeric", Number.class);
            knownTypes.put("number", Number.class);
            knownTypes.put("float", Float.class);
            knownTypes.put("double", Double.class);
        }

        final String skey = vClazzAlias.toLowerCase();
        return (knownTypes.containsKey(skey)) ? knownTypes.get(skey) : null;
    }

    /**
     * Пример:
     * final File backupName = findEmptyFile( fout, ".bak%s");
     *
     * @param checkFile проверяемый файл
     * @param fmtSuffix суффикс, который имеет один параметр (пустой или числовой номер),
     *                  для генерации уникального имени файла
     * @return имя несуществующего файла: если checkFile + суффикс с
     *         подстановкой пустой строки отсутствует, то воз-ся именно его имя,
     *         иначе "имя + суффиксN", с подставленым номером.
     */
    public static File findEmptyFile(final File checkFile, final String fmtSuffix) {
		/* поиск уникального имени файла */
        { // проба самого простого - добавить суффикс ...
            final File simpleFile = new File(checkFile.getAbsolutePath() + String.format(fmtSuffix, ""));
            if (!simpleFile.exists())
                return simpleFile;
        }

        // числовой индеск (с ограничением по кол-ву)...
        int MAX = 2048;
        for (int i = 1; i < MAX; i++) {
            final File newFile = new File(checkFile.getAbsolutePath() + String.format(fmtSuffix, i));
            if (!newFile.exists()) // found empty name
                return newFile;
        }
        throw new RuntimeException(String.format("Too may files like '%s%s'", checkFile, fmtSuffix));
    }

    /**
     * Вернуть длительность в часах
     *
     * @param duration_ms длительность в миллисекундах
     * @return float
     */
    public static float getDurationInHours(long duration_ms) {
        return ((float) duration_ms) / MILLIS_PER_HOUR;
    }

    /**
     * Вычислить разницу двух дат в часах. Если одна из дат NULL, воз-ся 0.
     *
     * @param start начало
     * @param end   конец
     * @return float
     */
    public static float getDurationInHours(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        return getDurationInHours(end.getTime() - start.getTime());
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(T source) {
        if (source == null) {
            return null;
        }

        final ByteArrayOutputStream stm = new ByteArrayOutputStream();

        try {
            final ObjectOutputStream w = new ObjectOutputStream(stm);
            w.writeObject(source);
            w.close();

            final ByteArrayInputStream result = new ByteArrayInputStream(stm.toByteArray());
            return (T) (new ObjectInputStream(result)).readObject();

        } catch (Exception ex) {
            throw new RuntimeException(String.format("Fail to clone() object: %s", ex.getMessage()), ex);
        }
    }

    /**
     * Выставить суточное время в указанные величины
     *
     * @param srcDate исходная дата, если null - будет использоваться today (!)
     * @param hh      выставляемые для даты часы (0..23)
     * @param mm      -//- минуты (0..59)
     * @param ss      -//- секунды (0..59)
     * @param msec    -//- миллисекунды (0..999)
     * @return дата с днём как в srcDate и с заказанным суточным временем
     */
    public static Date adjustDayTime(Date srcDate, int hh, int mm, int ss, int msec) {
        final Calendar result = Calendar.getInstance();
        result.setTime((srcDate != null) ? srcDate : new Date());

        result.set(Calendar.HOUR_OF_DAY, hh);
        result.set(Calendar.MINUTE, mm);
        result.set(Calendar.SECOND, ss);
        result.set(Calendar.MILLISECOND, msec);

        return result.getTime();
    }


    /**
     * Получить содержимое контента в виде байт
     *
     * @param reader ContentReader
     * @return byte
     * @throws IOException
     */
    public static byte[] ContentToBytes(final ContentReader reader)
            throws IOException {
        if (reader == null) {
            return null;
        }
        InputStream stmIn = null;
        try {
            stmIn = reader.getContentInputStream();
            return (stmIn != null) ? IOUtils.toByteArray(stmIn) : null;
        } finally {
            if (stmIn != null) {
                IOUtils.closeQuietly(stmIn);
            }
        }
    }

    /**
     * Сохранение данных в виде файла
     *
     * @param destFile File
     * @param srcData  сохраняемые данные
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveDataToFile(File destFile, final byte[] srcData) throws FileNotFoundException, IOException {
        // сохранение контента в файл ...
        final OutputStream out = new FileOutputStream(destFile);
        try {
            if (srcData != null) {
                IOUtils.write(srcData, out);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Сохранение потока в файл
     *
     * @param destFile File
     * @param srcStm   сохраняемые данные
     * @throws IOException
     */
    public static void saveDataToFile(File destFile, InputStream srcStm) throws IOException {
        // сохранение контента в файл ...
        final OutputStream out = new FileOutputStream(destFile);
        try {
            if (srcStm != null) {
                IOUtils.copy(srcStm, out);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Загрузить данные из файла как байтовый блок.
     * IO-исключения обёрнуты как rtm-exceptions.
     *
     * @param srcFile исходный файл для загрузки
     * @param errInfo сообщение при ошибках
     * @return byte[]
     */
    public static byte[] loadFileAsData(File srcFile) throws IOException {
        FileInputStream stmIn = null;
        try {
            stmIn = new FileInputStream(srcFile);
            return IOUtils.toByteArray(stmIn);
        } finally {
            if (stmIn != null) {
                IOUtils.closeQuietly(stmIn);
            }
        }
    }
}
