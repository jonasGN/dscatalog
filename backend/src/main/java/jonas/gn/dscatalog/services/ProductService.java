package jonas.gn.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.entities.Product;
import jonas.gn.dscatalog.repositories.ProductRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> fetchAllPaged(PageRequest pageRequest) {
		Page<Product> result = repository.findAll(pageRequest);

		return result.map(p -> new ProductDTO(p, p.getCategories()));
	}

	@Transactional(readOnly = true)
	public ProductDTO fetchById(Long id) {
		Optional<Product> result = repository.findById(id);
		Product entity = result.orElseThrow(() -> new ResourceNotFoundException());

		return new ProductDTO(entity, entity.getCategories());
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("ID not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException();
		}
	}

}
