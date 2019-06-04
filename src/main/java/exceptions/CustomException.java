package exceptions;

public class CustomException extends Exception {

	private static final long serialVersionUID = 8398406001439837198L;

	public CustomException() {
		super();
	}

	public CustomException(String msg) {
		super(msg);
	}

	public CustomException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CustomException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CustomException(Throwable arg0) {
		super(arg0);
	}
}
