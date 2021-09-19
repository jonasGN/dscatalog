package jonas.gn.dscatalog.services.exceptions;

public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String defaultMsg = "Database integrity violation";

	public DatabaseException() {
		this(defaultMsg);
	}

	public DatabaseException(String msg) {
		super(msg);
	}

}
