package jonas.gn.dscatalog.dto;

import jonas.gn.dscatalog.entities.User;
import jonas.gn.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

	public UserUpdateDTO() {
		super();
	}

	public UserUpdateDTO(User entity) {
		super(entity);
	}

}
