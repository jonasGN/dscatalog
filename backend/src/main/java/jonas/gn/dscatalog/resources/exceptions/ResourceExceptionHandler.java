package jonas.gn.dscatalog.resources.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		final StandardError error = new StandardError(HttpStatus.NOT_FOUND, e, request);

		return error.toJsonResponse();
	}

	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> databaseViolation(DatabaseException e, HttpServletRequest request) {
		final StandardError error = new StandardError(HttpStatus.BAD_REQUEST, e, request);

		return error.toJsonResponse();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
		final ValidationError error = new ValidationError(HttpStatus.UNPROCESSABLE_ENTITY, e, request);

		for (FieldError f : e.getFieldErrors()) {
			error.addError(f.getField(), f.getDefaultMessage());
		}

		return error.toJsonResponse();
	}

}