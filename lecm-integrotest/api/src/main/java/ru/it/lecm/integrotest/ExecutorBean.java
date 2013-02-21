package ru.it.lecm.integrotest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;



/**
 * Интерфейс для интегрального тестирования.
 * Проводимые тесты собираются бинами от интерфейсов IExecAction, IStepAction. 
 *
 * @author rabdullin
 */
public interface ExecutorBean {


	/**
	 * @return список шагов для тестирования
	 */
	List<SingleTest> getSteps();

	/**
	 * @param list  
	 */
	void setSteps( List<SingleTest> list); 

	/**
	 * выполнить указанный шаг тестирования
	 * @param i
	 * @return код завершения
	 */
	StepResult runStep(int i);


	/**
	 * Поочерёдно выполнить все шаги тестирования по списку steps.
	 * @return  строки с кодами завершения отдельных шагов - один элемент на каждый шаг.
	 */
	List<StepResult> runAll();

	public enum EResult {
		OK, ERROR
	}

	public static class StepResult {

		final private EResult code;
		final private Throwable rtmError;
		final private String info;

		public StepResult(EResult code, Throwable rtmError, String info) {
			super();
			this.code = code;
			this.rtmError = rtmError;
			this.info = info;
		}

		public StepResult(EResult code, Throwable rtmError) {
			this(code, rtmError, (rtmError == null ? "" : rtmError.getMessage() ));
		}

		public StepResult(EResult code) {
			this( code, null);
		}

		public EResult getCode() {
			return this.code;
		}

		public String getInfo() {
			return this.info;
		}
		/**
		 * Ошибка для случая code==ERROR или null при code==OK
		 * @return
		 */
		public Throwable getRtmError() {
			return this.rtmError;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			result = prime * result
					+ ((rtmError == null) ? 0 : rtmError.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final StepResult other = (StepResult) obj;
			if (code != other.code)
				return false;
			if (rtmError == null) {
				if (other.rtmError != null)
					return false;
			} else if (!rtmError.equals(other.rtmError))
				return false;
			return true;
		}

		@Override
		public String toString() {
			final StringWriter result = new StringWriter();
			final PrintWriter wr = new PrintWriter( result);
			wr.print( ""+ code);
			if (rtmError != null) {
				wr.print( "\n");
				wr.println( info);
				rtmError.printStackTrace( wr);
			}
			wr.flush();
			return result.toString();
		}
	} 
}
