package jonas.gn.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.repositories.ProductRepository;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

	@Autowired
	private ProductService service;

	@Autowired
	private ProductRepository repository;

	private long existingId = 1L;
	private long nonExistingId = 1000L;
	private long totalProducts = 25L;

	private ProductDTO productDTO = Factory.createProductDTO();

	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		service.delete(existingId);
		Assertions.assertEquals(totalProducts - 1L, repository.count());
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}

	@Test
	public void fetchAllPagedShouldReturnPageWhenPageZeroSizeTen() {
		final int page = 0;
		final int size = 10;
		Pageable pageable = PageRequest.of(page, size);

		Page<ProductDTO> result = service.fetchAllPaged(pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(page, result.getNumber());
		Assertions.assertEquals(size, result.getSize());
		Assertions.assertEquals(totalProducts, result.getTotalElements());
	}

	@Test
	public void fetchAllPagedShouldBeEmptyWhenPageDoesNotExist() {
		Pageable pageable = PageRequest.of(50, 10);
		Page<ProductDTO> result = service.fetchAllPaged(pageable);

		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void fetchAllPagedShouldReturnSortedPageWhenSortByName() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));

		Page<ProductDTO> result = service.fetchAllPaged(pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}

	@Test
	public void fetchByidShouldReturnProductDtoWhenIdExists() {
		ProductDTO result = service.fetchById(existingId);
		Assertions.assertNotNull(result);
	}

	@Test
	public void fetchByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.fetchById(nonExistingId);
		});
	}

	@Test
	public void addShouldPersist() {
		ProductDTO result = service.add(productDTO);

		Assertions.assertNotNull(result);
	}

	@Test
	public void updateShouldUpdateWhenIdExists() {
		ProductDTO result = service.update(existingId, productDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(productDTO.getName(), result.getName());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
	}

}
