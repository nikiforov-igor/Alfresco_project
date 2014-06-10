package ru.it.lecm.integrotest;


/**
 * Действие с контекстом.
 * @author Ruslan
 */
public interface RunAction extends Runnable {

	/**
	 * Задать контекст выполнения - конфигурационные, текущие и результирующие
	 * параметры.
	 *
	 * @param context
	 */
	void setContext(RunContext context);

	/**
	 * Получить контекст выполнения - конфигурационные, текущие и результирующие
	 * параметры.
	 *
	 * @param context
	 */
	RunContext getContext();

//    void prepare(RunContext context);
//    void run(RunContext context);
//    void check(RunContext context);
}