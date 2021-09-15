package jonas.gn.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String defaultMsg = "Could not find this resource";

	public EntityNotFoundException() {
		this(defaultMsg);
	}

	public EntityNotFoundException(String msg) {
		super(msg);
	}

}
