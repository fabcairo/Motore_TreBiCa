
public class PathNonValidoException extends UrlNonValidoException {

	public PathNonValidoException() {
	}

	public PathNonValidoException(String message) {
		super(message);
	}

	public PathNonValidoException(Throwable cause) {
		super(cause);
	}

	public PathNonValidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public PathNonValidoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
