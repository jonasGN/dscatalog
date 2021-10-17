package jonas.gn.dscatalog.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String defaultMsg = "Could not find this resource";

	public ResourceNotFoundException() {
		super(defaultMsg);
	}

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(Long resourceId) {
		super(defaultMsg + ", by the given ID: " + resourceId);
	}

}
