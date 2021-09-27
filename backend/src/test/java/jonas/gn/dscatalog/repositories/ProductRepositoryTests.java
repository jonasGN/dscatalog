package jonas.gn.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import jonas.gn.dscatalog.entities.Product;
import jonas.gn.dscatalog.factory.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	long existingId = 1L;
	long nonExistingId = 1000L;
	long lastProductId = 25L;

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);

		product = repository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(lastProductId + 1, product.getId());
	}

	@Test
	public void findByIdShouldReturnProductWhenIdExists() {
		Optional<Product> product = repository.findById(existingId);

		Assertions.assertTrue(product.isPresent());
	}

	@Test
	public void findByIdShouldBeEmptyWhenIdDoesNotExist() {
		Optional<Product> product = repository.findById(nonExistingId);

		Assertions.assertTrue(product.isEmpty());
	}

	@Test
	public void deleteShouldDeleteWhenIdExists() {
		repository.deleteById(existingId);
		Optional<Product> result = repository.findById(existingId);

		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}

}
