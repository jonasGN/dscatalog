package jonas.gn.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.repositories.CategoryRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	@InjectMocks
	private CategoryService service;

	@Mock
	private CategoryRepository repository;

	private long existingId = 1L;
	private long nonExistingId = 2L;
	private long dependentId = 3L;

	private Category category = Factory.createCategory();
	private CategoryDTO categoryDTO = Factory.createCategoryDTO();
	private PageImpl<Category> page = new PageImpl<>(List.of(category));

	@BeforeEach
	void setUp() throws Exception {
		when(repository.findAll(any(Pageable.class))).thenReturn(page);

		when(repository.findById(existingId)).thenReturn(Optional.of(category));
		when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		when(repository.save(ArgumentMatchers.any())).thenReturn(category);

		when(repository.getById(existingId)).thenReturn(category);
		when(repository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		doNothing().when(repository).deleteById(existingId);
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	@Test
	public void fetchAllPagedShouldReturnPage() {
		final Pageable pageable = PageRequest.of(0, 10);
		final Page<CategoryDTO> result = service.fetchAllPaged(pageable);

		assertNotNull(result);
		verify(repository).findAll(pageable);
	}

	@Test
	public void fetchByIdShouldReturnCategoryDtoWhenIdExists() {
		final CategoryDTO result = service.fetchById(existingId);

		assertNotNull(result);
		verify(repository).findById(existingId);
	}

	@Test
	public void fetchByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.fetchById(nonExistingId);
		});
		verify(repository).findById(nonExistingId);
	}

	@Test
	public void addShouldPersist() {
		final CategoryDTO result = service.add(categoryDTO);

		assertNotNull(result);
	}

	@Test
	public void updateShouldReturnCategoryDtoWhenIdExists() {
		final CategoryDTO result = service.update(existingId, categoryDTO);

		assertNotNull(result);
		assertEquals(existingId, result.getId());
		verify(repository).getById(existingId);
		verify(repository).save(category);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, categoryDTO);
		});
		verify(repository).getById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
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

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
		assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		verify(repository).deleteById(dependentId);
	}
}
