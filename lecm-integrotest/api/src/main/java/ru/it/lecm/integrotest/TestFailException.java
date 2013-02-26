package ru.it.lecm.integrotest;

public class TestFailException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private boolean canContinue = false;

	public TestFailException() {
		super();
	}

	public TestFailException(boolean flagContinue, String message, Throwable cause) {
		super(message, cause);
		this.canContinue = flagContinue;
	}

	public TestFailException( String message, Throwable cause) {
		this( false, message, cause);
	}
	public TestFailException(boolean flagContinue, String message) {
		this( flagContinue, message, null);
	}


	public TestFailException(String message) {
		this( false, message);
	}

	public TestFailException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return флаг разрешения продолжения выполнения после ошибки
	 */
	public boolean isCanContinue() {
		return canContinue;
	}

	
	/**
	 * @param canContinue флаг разрешения продолжения выполнения после ошибки
	 */
	public void setCanContinue(boolean canContinue) {
		this.canContinue = canContinue;
	}

}
