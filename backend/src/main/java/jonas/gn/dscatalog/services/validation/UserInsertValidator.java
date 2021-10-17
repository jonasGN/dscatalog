package jonas.gn.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import jonas.gn.dscatalog.dto.UserInsertDTO;
import jonas.gn.dscatalog.entities.User;
import jonas.gn.dscatalog.repositories.UserRepository;
import jonas.gn.dscatalog.resources.exceptions.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

	@Autowired
	private UserRepository repository;

	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

		final List<FieldMessage> errors = new ArrayList<>();

		final User user = repository.findByEmail(dto.getEmail());
		if (user != null) {
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
