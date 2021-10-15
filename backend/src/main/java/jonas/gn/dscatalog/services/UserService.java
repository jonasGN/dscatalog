package jonas.gn.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.RoleDTO;
import jonas.gn.dscatalog.dto.UserDTO;
import jonas.gn.dscatalog.dto.UserInsertDTO;
import jonas.gn.dscatalog.entities.Role;
import jonas.gn.dscatalog.entities.User;
import jonas.gn.dscatalog.repositories.RoleRepository;
import jonas.gn.dscatalog.repositories.UserRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public Page<UserDTO> fetchAllPaged(Pageable pageable) {
		final Page<User> result = repository.findAll(pageable);

		return result.map(u -> new UserDTO(u));
	}

	@Transactional(readOnly = true)
	public UserDTO fetchById(Long id) {
		final Optional<User> result = repository.findById(id);
		final User entity = result.orElseThrow(() -> new ResourceNotFoundException());

		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO add(UserInsertDTO user) {
		User entity = new User();
		copyDtoToEntity(user, entity);
		entity.setPassword(passwordEncoder.encode(user.getPassword()));
		entity = repository.save(entity);

		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO user) {
		try {
			User entity = repository.getById(id);
			copyDtoToEntity(user, entity);
			entity = repository.save(entity);

			return new UserDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException();
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException();
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException();
		}
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setEmail(dto.getEmail());
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());

		entity.getRoles().clear();
		for (RoleDTO roleDto : dto.getRoles()) {
			final Role role = roleRepository.getById(roleDto.getId());
			entity.getRoles().add(role);
		}
	}

}
