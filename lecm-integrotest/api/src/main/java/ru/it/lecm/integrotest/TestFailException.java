package ru.it.lecm.integrotest;

public class TestFailException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TestFailException() {
		super();
	}

	public TestFailException(String message, Throwable cause) {
		super(message, cause);
	}

	public TestFailException(String message) {
		super(message);
	}

	public TestFailException(Throwable cause) {
		super(cause);
	}

}
