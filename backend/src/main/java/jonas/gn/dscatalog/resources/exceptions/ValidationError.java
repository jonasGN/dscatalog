package jonas.gn.dscatalog.resources.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ValidationError extends StandardError {

	private static final long serialVersionUID = 1L;

	private List<FieldMessage> errors = new ArrayList<>();

	public ValidationError() {
		super();
	}

	public ValidationError(HttpStatus status, Exception error, HttpServletRequest request) {
		super(status, error, request);
	}

	public List<FieldMessage> getErrors() {
		return Collections.unmodifiableList(this.errors);
	}

	public void addError(String field, String message) {
		errors.add(new FieldMessage(field, message));
	}

	@Override
	public ResponseEntity<StandardError> toJsonResponse() {
		return ResponseEntity.status(HttpStatus.valueOf(super.getStatus())).body(this);
	}

}
