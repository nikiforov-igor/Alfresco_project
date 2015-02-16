package ru.it.lecm.reports.jasper.utils;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Для упрощения журналирования длительности операций.
 * Примерная схема использования:
 * ... org.slf4j.Logger logger;
 * ...
 * 		DurationLogger dl = new dr(logger);
 * ...
 * 		// [1] перед началом операции
 * 		dl.markStart();
 * 		... выполнение длительной операции ...
 *		// [2] вывод длительность от т [1] до этой точки [2]
 * 		dl.markDuration("время выполнения Операции A: начало {s,date}, конец: {e,date}, длительность мс: {t}, в сек: {t,s}, в мин: {t,min}, дата: {t,date}");
 * 		... выполнение продолжается ...
 *		// [3] отметка суммарного времени обоих операций от [1] до [3]
 *		// и перезапуск
 * 		dl.markDurationAndStart("время выполнения операций А и Б: начало {s,date}, конец: {e,date}, длительность мс: {t}, в сек: {t,s}, в мин: {t,min}, дата: {t,date}");
 *
 * 		// тут уже будет другой замер - после точки [3]
 * 		... другая Последовательность действий ...
 * 		// [4] замер времени от точки [3] до [4]
 * 		dl.markDuration("время выполнения Последовательности, мс: {t}, в сек: {t,s}");
 *
 * Т.е. явно вызывается функция начала, отметки времени и перезапуска.
 * При вызове отметок можно указать форматную строку, с необязательными
 * аргументами вида "{name,units}", где name обозначение сути переменной,
 * а units единица её измерения.
 * Для name можно использовать букву или слово:
 * 		буква   Слово
 * 		's' 	"start" начало замера
 * 		'e' 	"end" конец замера
 * 		't' 	"time" конец замера
 * для units указываются единицы измерения времени:
 * 		нет		по-умолчанию, в зависимости от name
 * 			если t то миллисекунды,
 * 			иначе дата с точностью до секунд
 * 		n или nano		наносекунды
 * 		ms				миллисекунды
 * 		s или sec		секунды
 * 		m или min 		минуты
 * 		h или hhmm		часы:минуты (для начала и конца - внутри дня, для длительности - она сама)
 * 		date			дата "dd/mm/yy" (без времени суток)
 * 		date_hhmmss		дата до секунд ("dd/mm/yyyy hh:mm:ss")
 *
 */
public class DurationLogger {

	public static final String TAG_TOO_SLOW = "(!) TOO SLOW";

	public static final String DURATION_MS = "duration, ms: {t}";

	/**
	 * Значение по-умолчанию (в мс) для длительностей, которые принимаются "большими"
	 */
	public static final long DEFAULT_WARN_DURATION_MS = 2000;

	private long ms_start, ms_end; // мс в нормальном времени
	private long nano_start;
    private long warn_duration_ms = DEFAULT_WARN_DURATION_MS; // 2 сек считаем по-умолчанию большим времени

//	private org.apache.log4j.Logger logger;
//	/**
//	 * Создать измеритель и сразу начать замер
//	 * @param log
//	 */
//	public DurationLogger(org.apache.log4j.Logger log) {
//		super();
//		this.logger = log;
//		markStart();
//	}

	/**
	 * Создать измеритель и сразу начать замер
	 */
	public DurationLogger() {
		this( DEFAULT_WARN_DURATION_MS);
	}

	/**
	 * Создать измеритель и сразу начать замер
	 * @param warn_ms граничная длительность (мс), больше которой время считается "большим"
	 */
	public DurationLogger(long warn_ms) {
		super();
		this.warn_duration_ms = warn_ms;
		markStart();
	}

	/**
	 * Перевести наносекунды в миллисекунды
	 * @param nanotime время в нс
	 * @return время в мс
	 */
	public static long toMillis( long nanotime) {
		return nanotime / 1000000;
	}

	/**
	 * Перевести миллисекунды в наносекунды
	 * @param millies время в мс
	 * @return время в нс
	 */
	public static long toNanos( long millies) {
		return millies * 1000000;
	}

	/**
	 * Перевести наносекунды в секунды
	 * @param nanotime время в нс
	 * @return время в сек
	 */
	public static long toSeconds( long nanotime) {
		return nanotime / 1000000000;
	}

	/**
	 * Выполнить отметку начала
	 * @return дата-отметка начала в мс
	 */
	public long markStart() {
		this.nano_start = System.nanoTime();
		return this.ms_start = System.currentTimeMillis();
	}

	// отметка окончания (без рестарта)
	/**
	 * Отметка окончания без рестарта и вычисление длительности последнего замера
	 * @return длительность в нс, замер продолжается
	 */
	public long calcDurationNanos() {
        long nano_end = System.nanoTime();
		this.ms_end = System.currentTimeMillis();
		return nano_end - nano_start;
	}

	/**
	 * Названия для Начала, Конца, Длительности
	 */
	public final static String[][] TAGS = {
		{ "s", "start"}, { "e", "end"}, { "t", "time"}
	};


	/**
	 * Названия для единиц измерения
	 * строки - см индексы и названия для "точности",
	 * в строках синонимы для оот-щей ед измерения
	 */
	public final static String[][] UNITS = {
		/*0*/ {""}
		/*1*/ , {"n", "nano"}
		/*2*/ , { "ms", "msec" }
		/*3*/ , { "s", "sec" }
		/*4*/ , { "m", "min" }
		/*5*/ , { "h", "hhmm" }
		/*6*/ , { "date" }
		/*7*/ , { "date_hhmmss" }
	};

	/**
	 * Выполнить вывод длительности с миллисекундах от последнего маркера начала.
	 * Пример: "call time is {t} msec"
	 * @param fmt форматная строка для вывода отметки времени.
	 * @return <p> отформатированная строка, новый замер времени НЕ начинается. </p>
	 *
	 * <p> Могут использоваться следующие макросы в форматной строке:</p>
	 * <p> необ аргументы в виде "{название:ед_врем}" для подстановки времени начала,
	 * конца и длительности. </p>
	 * <p>Значение (fmt=null) соот-ет "по-умолачнию" DURATION_MS </p>
	 * <p>"название" это обозначение сути переменной, </p>
	 * <p>"точность" единица её измерения.</p>
	 * <p> В качестве "названия" можно использовать букву или слово: </p>
	 * <p>		буква   слово-синоним 	Значение</p>
	 * <p>		's' 	"start" 		начало замера</p>
	 * <p>		'e' 	"end"			конец замера</p>
	 * <p>		't' 	"time"			длительность замера</p>
	 *
	 * <p>Для "ед_врем" указываются единицы измерения Выводимого времени: </p>
	 * <p>	Значение		Действие</p>
	 * <p>	------------------------------------------------------------------- </p>
	 * <p>	0	нет				(действует по-умолчанию) желаемые единицы измерения в зависимости от названия:</p>
	 * <p>						если t то миллисекунды, иначе дата с точностью до секунд </p>
	 * <p>	1	n или nano		наносекунды</p>
	 * <p>	2	ms				миллисекунды</p>
	 * <p>	3	s или sec		секунды</p>
	 * <p>	4	m или min 		минуты</p>
	 * <p>	5	h или hhmm		часы:минуты (для начала и конца - внутри дня, для длительности - она сама)</p>
	 * <p>	6	date			дата "dd/mm/yy" (без времени суток)</p>
	 * <p>	7	date_hhmmss		дата до секунд ("dd/mm/yyyy hh:mm:ss")</p>
	 * <p>	------------------------------------------------------------------- </p>
	 */
	public String fmtDuration(String fmt) {
		if (fmt == null)
			fmt = DURATION_MS;

		// отметка окончания (без рестарта)
		final long nano_duration = calcDurationNanos();

		String message = fmt;
		if (fmt.length() > 0) {
			// формирование значений для макро-подстановок ...
			// (!) здесь не используется nano_start и nano_end, т.к. они не
			// являются временем суток, а ms_start и ms_end являются.
			final Map<String, String> substData = getSubstitutionMap( new long[]
					{ toNanos( ms_start), toNanos(ms_end), nano_duration });

			// макро-подстановка ...
			for(Map.Entry<String, String>  entry: substData.entrySet()) {
				message = message.replaceAll( entry.getKey(), entry.getValue());
			}
		}

		// logger.debug( message); return toMillis(nano_duration);
		return message;
	}

	/**
	 * Журналировать длительность активного замера.
	 * Если длительность превышает значение warn_duration_ms сообщение
	 * выводится на уроне WARN иначе на уровне DEBUG.
	 * @param log целевой журнал
	 * @param fmt форматная строка (см. форматы)
	 * @param startNew true, если надо начать новый замер после этого
	 * @return длительность последней операции в мс
	 */
	public long logCtrlDuration(Logger log, String fmt, boolean startNew) {
		final long duration_ms = toMillis( calcDurationNanos());

		final boolean isWarn = (duration_ms > this.warn_duration_ms);
		if ( (duration_ms > 0) && (isWarn || log.isDebugEnabled()) ) {
			final String message = fmtDuration(fmt);
			if (isWarn)
				log.warn( MessageFormat.format( TAG_TOO_SLOW + " {0} ms > {1} ms \n", duration_ms, warn_duration_ms) + message);
			else
				log.debug(message);
		}

		if (startNew)
			this.markStart();

		return duration_ms;
	}

	/**
	 * Журналировать длительность активного замера и сразу начать новый.
	 * Если длительность превышает значение warn_duration_ms, то сообщение
	 * выводится на уроне WARN иначе на уровне DEBUG.
	 * @param log целевой журнал
	 * @param fmt форматная строка (см. формат)
	 * @return длительность последней операции в мс
	 */
	public long logCtrlDuration(Logger log, String fmt) {
		return logCtrlDuration(log, fmt, true);
	}


	final protected static int IND_DURATION = 2; // индекс длительности в массиве размеров

	/**
	 * Получить список аргументов для замены макросов (в разных написаниях)
	 * см их использование в javadoc
	 * @param nanos три времени в нс: [0] начало [1] конец [2] длительность, нс
	 * @return мап с ключом что заменять (например, "{s:}"
	 */
	protected Map<String, String> getSubstitutionMap(final long[] nanos) {
		final Map<String, String> substData = new HashMap<String, String>();

		/* subst:
		 * Первый индекс [i]: 0: начало	1: конец	3: длительность
		 * Второй индекс [j]:
		 * 	i,0: default
		 * 	i,1: нс
		 *  i,2: мс
		 *  i,3: сек
		 *  i,4: минуты
		 *  i,5: чч:мм
		 *  i,6: date 			"dd/mm/yyyy"
		 *  i,7: date_hhmmss	"dd/mm/yyyy hh:mm:ss"
		 *  Например
		 *  	subs[0,3] это представление для формата "{start,sec}"
		 *  	subs[1,6] для "{end,date}"
		 *  	subs[2,1] для "{time,nano}"
		 */
		final String[][] subst = new String[TAGS.length][];
		for(int i = 0; i < TAGS.length; i++) {
			subst[i] = new String[UNITS.length];
			subst[i][1] =  String.valueOf(nanos[i]);

			final long millis = toMillis(nanos[i]);
			subst[i][2] =  String.valueOf(millis);

			final Date dt = new Date( millis);

			// сек минуты и часы
			if (i == IND_DURATION) {
				// длительность непосредственно ...
				final long sec = toSeconds(nanos[i]);
				final long min = sec/60;
				subst[i][3] =  String.valueOf(sec/60);
				subst[i][4] =  String.format( "%02d", min);
				subst[i][5] =  String.format( "%02d:%02d", min/60, min%60);
			} else {
				// начало и конец ...
				subst[i][3] =  String.format( "%1$tS", dt);
				subst[i][4] =  String.format( "%1$tM", dt);
				subst[i][5] =  String.format( "%1$tH:%1$tM", dt);
			}

			subst[i][6] =  String.format( "%1$tm/%1$td/%1$ty", dt); // "dd/mm/yyyy"
			subst[i][7] =  String.format( "%1$tm/%1$td/%1$tY %1$tH:%1$tM:%1$tS", dt); // "dd/mm/yyyy hh:mm:ss"

			// default: берём в зависимости от значения Начало, Конец,
			// Длительность одно из уже вычисленных значений
			subst[i][0] = subst[i][ (i == 2) ? /*msec*/ 2 : /*date*/ 7];

			final String[] tags = TAGS[i]; // список синонимов для соот-щей переменной Начала или Конца, или Длительности
            for (final String tag : tags) {
                for (int ii = 0; ii < UNITS.length; ii++) {
                    final String value = subst[i][ii];
                    // для разных синонимов замена будет одна и та же ...
                    for (int jj = 0; jj < UNITS[ii].length; jj++) {
                        final String macro =
                                "[{]"
                                        + tag
                                        + (DurationLogger.UNITS[ii][jj].length() > 0 ? "," + DurationLogger.UNITS[ii][jj] : "")
                                        + "[}]";
                        substData.put(macro, value);
                    }
                }
            }
		}
		return substData;
	}


	/**
	 * Замерить время выполнения метода, вывести сообщение с замером времени,
	 * если время превысит указанный предел - вывести предупреждение.
	 * @param log журнал, в который выполнять логирование
	 * @param infoFmt формат выводимого сообщения (см возможные параметры в fmtDuration)
	 * @param normal_time_ms  время выполнения (в мс), превышение которого считается аномалией
	 * @param todo выполняемое действие
	 * @return время выполнения в мс
	 */
	static public long exec(Logger log, String infoFmt, long normal_time_ms
			, Runnable todo)
	{
		final DurationLogger d = new DurationLogger(normal_time_ms);
		todo.run();
        return d.logCtrlDuration(log, infoFmt);
	}

	/**
	 * Замерить время выполнения метода, вывести сообщение с замером времени,
	 * если время превышает предел  - вывести прудупреждение.
	 * @param log журнал, в который выполнять логирование
	 * @param infoFmt формат выводимого сообщения (см возможные параметры в fmtDuration)
	 * @param todo выполняемое действие
	 * @return время выполнения в мс
	 */
	static public long exec(Runnable todo, Logger log, String infoFmt) {
		return exec(log, infoFmt, DEFAULT_WARN_DURATION_MS, todo);
	}
}
