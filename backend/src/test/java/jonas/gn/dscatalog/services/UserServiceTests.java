package jonas.gn.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jonas.gn.dscatalog.dto.UserDTO;
import jonas.gn.dscatalog.dto.UserInsertDTO;
import jonas.gn.dscatalog.entities.Role;
import jonas.gn.dscatalog.entities.User;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.repositories.RoleRepository;
import jonas.gn.dscatalog.repositories.UserRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private RoleRepository roleRepository;

	private long existingId = 1L;
	private long nonExistingId = 2L;
	private long dependentId = 3L;

	private User user = Factory.createUser();
	private UserDTO dto = Factory.createUserDTO();
	private Page<User> page = new PageImpl<>(List.of(user));

	private Role role = Factory.createRole();

	@BeforeEach
	public void setUp() throws Exception {
		when(repository.findAll(any(Pageable.class))).thenReturn(page);

		when(repository.findById(existingId)).thenReturn(Optional.of(user));
		when(repository.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		when(repository.save(any())).thenReturn(user);

		when(repository.getById(existingId)).thenReturn(user);
		when(repository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		doNothing().when(repository).deleteById(existingId);
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		when(roleRepository.getById(existingId)).thenReturn(role);
		when(roleRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);
	}

	@Test
	public void fetchAllShouldReturnUsersPaged() {
		final int page = 0;
		final int size = 1;
		final Pageable pageable = PageRequest.of(page, size);

		final Page<UserDTO> result = service.fetchAllPaged(pageable);
		assertNotNull(result);
		assertEquals(page, result.getNumber());
		assertEquals(size, result.getSize());
		verify(repository).findAll(pageable);
	}

	@Test
	public void fetchByIdShouldReturnUserWhenIdExists() {
		final UserDTO result = service.fetchById(existingId);

		assertNotNull(result);
		assertEquals(dto.getFirstName(), result.getFirstName());
		verify(repository).findById(existingId);
	}

	@Test
	public void fetchByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
		assertThrows(EntityNotFoundException.class, () -> {
			service.fetchById(nonExistingId);
		});
		verify(repository).findById(nonExistingId);
	}

	@Test
	public void addShouldPersist() {
		final UserInsertDTO userDto = Factory.createUserInsertDTO();

		final UserDTO result = service.add(userDto);

		assertNotNull(result);
		assertEquals(dto.getFirstName(), result.getFirstName());
	}

	@Test
	public void updateShouldUpdateWhenIdExists() {
		final UserDTO result = service.update(existingId, dto);

		assertNotNull(result);
		assertEquals(existingId, result.getId());
		verify(repository).getById(existingId);
		verify(repository).save(any());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dto);
		});
		verify(repository).getById(nonExistingId);
		verify(repository, never()).save(any());
	}

	@Test
	public void deleteShouldDeleteWhenIdExists() {
		assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		verify(repository).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		verify(repository).deleteById(nonExistingId);
	}

	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependet() {
		assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		verify(repository).deleteById(dependentId);
	}
}
