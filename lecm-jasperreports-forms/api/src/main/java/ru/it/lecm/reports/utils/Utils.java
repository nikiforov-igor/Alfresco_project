package ru.it.lecm.reports.utils;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Utils {
    final public static char QUOTE = '\"';
    final public static char CH_WALL = '\\'; // символ экранировки

    final public static int MILLIS_PER_DAY = 86400000;
    final public static long MILLIS_PER_HOUR = 1000 * 60 * 60;

    final public static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

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

    public static <T> String getAsString(final T[] col, String delimiter) {
        return getAsString(col, delimiter, null, null);
    }

    public static <T> String getAsString(final T[] col, final String delimiter, String quoteOpen, String quoteClose) {
        if (col == null) {
            return null;
        }
        if (quoteOpen == null) {
            quoteOpen = "";
        }
        if (quoteClose == null) {
            quoteClose = "";
        }
        final StringBuilder result = new StringBuilder(10);
        for (T item : col) {
            final String strItem = (item != null)
                    ? quoteOpen + item.toString() + quoteClose
                    : "NULL";

            result.append(strItem);
            if (delimiter != null) {
                result.append(delimiter);
            }
        }
        if (delimiter != null && result.length() > delimiter.length()) {
            result.delete(result.length() - delimiter.length(), result.length());
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
     * Вычислить длительность в днях между парой дат
     *
     * @param startAt      время начала
     * @param endAt        время окончания
     * @param defaultValue значение по-умолчанию
     * @return разницу в днях (возможно нецелое значение) между двумя датами;
     *         значение по-умолчанию воз-ся если одна из дат или обе null.
     */
    public static float calcDurationInDays(Date startAt, Date endAt, float defaultValue) {
        if (startAt == null || endAt == null) {
            return defaultValue;
        }
        final double duration_ms = (endAt.getTime() - startAt.getTime());
        return (float) (duration_ms / MILLIS_PER_DAY);
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
     * Вернуть длительность в днях
     *
     * @param duration_ms длительность в миллисекундах
     * @return float
     */
    public static float getDurationInDays(long duration_ms) {
        return ((float) duration_ms) / MILLIS_PER_DAY;
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
    public static void saveDataToFile(File destFile, final byte[] srcData) throws IOException {
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

    public static String dateToString(Date valueToWrite) {
        if (valueToWrite != null) {
            return YYYY_MM_DD.format(valueToWrite);
        } else {
            return null;
        }
    }

    public static String replaceCR(String s) {
        final int length = s.length();
        final char[] chars = s.toCharArray();
        for (int i = 0; i < length; i++) {
            if (chars[i] == '\r' || chars[i] == '\n') {
                chars[i] = ' ';
            }
        }
        return new String(chars);
    }
}
