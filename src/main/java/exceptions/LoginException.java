package exceptions;

public class LoginException extends CustomException {

	private static final long serialVersionUID = -3403404608038592099L;

	public LoginException() {
	}

	public LoginException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public LoginException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public LoginException(String msg) {
		super(msg);
	}

	public LoginException(Throwable arg0) {
		super(arg0);
	}

}
