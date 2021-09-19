package jonas.gn.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.entities.Product;
import jonas.gn.dscatalog.repositories.CategoryRepository;
import jonas.gn.dscatalog.repositories.ProductRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;

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

	@Transactional
	public ProductDTO add(ProductDTO product) {
		Product entity = new Product();
		copyDtoToEntity(product, entity);
		entity = repository.save(entity);

		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO product) {
		try {
			Product entity = repository.getById(id);
			copyDtoToEntity(product, entity);
			entity = repository.save(entity);

			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException();
		}
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

	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		entity.setMoment(dto.getMoment());

		entity.getCategories().clear();
		for (CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getById(catDto.getId());
			entity.getCategories().add(category);
		}
	}
}
