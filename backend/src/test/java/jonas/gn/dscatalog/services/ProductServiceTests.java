package jonas.gn.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.entities.Product;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.repositories.CategoryRepository;
import jonas.gn.dscatalog.repositories.ProductRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	@Mock
	private CategoryRepository categoryRepository;

	private long existingId = 1L;
	private long nonExistingId = 2L;
	private long dependentId = 3L;

	private Product product = Factory.createProduct();
	private ProductDTO productDTO = Factory.createProductDTO();
	private PageImpl<Product> page = new PageImpl<>(List.of(product));

	private Category category = Factory.createCategory();

	@BeforeEach
	void setUp() throws Exception {
		Mockito.when(repository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		Mockito.when(repository.getById(existingId)).thenReturn(product);
		Mockito.when(repository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(categoryRepository.getById(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	@Test
	public void fetchAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.fetchAllPaged(pageable);

		Assertions.assertNotNull(result);
		Mockito.verify(repository).findAll(pageable);
	}

	@Test
	public void fetchByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.fetchById(existingId);

		Assertions.assertNotNull(result);
		Mockito.verify(repository).findById(existingId);
	}

	@Test
	public void fetchByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.fetchById(nonExistingId);
		});
		Mockito.verify(repository).findById(nonExistingId);
	}
	
	@Test
	public void addShouldPersist() {
		ProductDTO result =  service.add(productDTO);
		
		Assertions.assertNotNull(result);
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.update(existingId, productDTO);

		Assertions.assertNotNull(result);
		Mockito.verify(repository).getById(existingId);
		Mockito.verify(repository).save(product);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
		Mockito.verify(repository).getById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		Mockito.verify(repository).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		Mockito.verify(repository).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(repository).deleteById(dependentId);
	}
}
