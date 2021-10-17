package jonas.gn.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import jonas.gn.dscatalog.dto.UserUpdateDTO;
import jonas.gn.dscatalog.entities.User;
import jonas.gn.dscatalog.repositories.UserRepository;
import jonas.gn.dscatalog.resources.exceptions.FieldMessage;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired
	private UserRepository repository;

	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		@SuppressWarnings("unchecked")
		final var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		final long userId = Long.parseLong(uriVars.get("id"));

		final List<FieldMessage> errors = new ArrayList<>();

		final User user = repository.findByEmail(dto.getEmail());
		if (user != null && userId != user.getId()) {
			errors.add(new FieldMessage("email", "This email already exists"));
		}

		for (FieldMessage e : errors) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getField())
					.addConstraintViolation();
		}

		return errors.isEmpty();
	}
}
