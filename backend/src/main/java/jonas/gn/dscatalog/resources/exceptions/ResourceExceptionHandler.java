package jonas.gn.dscatalog.resources.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		String errorMessage = "Resource not found";
		StandardError error = new StandardError(HttpStatus.NOT_FOUND, errorMessage, e, request);

		return error.toHttpResponse();
	}

	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> databaseError(DatabaseException e, HttpServletRequest request) {
		String errorMessage = "Bad request";
		StandardError error = new StandardError(HttpStatus.BAD_REQUEST, errorMessage, e, request);

		return error.toHttpResponse();
	}

}