package jonas.gn.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.repositories.CategoryRepository;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> fetchAllPaged(Pageable pageable) {
		Page<Category> result = repository.findAll(pageable);

		return result.map(c -> new CategoryDTO(c));
	}

	@Transactional(readOnly = true)
	public CategoryDTO fetchById(Long id) {
		Optional<Category> result = repository.findById(id);
		Category entity = result.orElseThrow(() -> new ResourceNotFoundException());

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO add(CategoryDTO category) {
		Category entity = CategoryDTO.toEntity(category);
		entity = repository.save(entity);

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO category) {
		try {
			Category entity = repository.getById(id);
			entity.setName(category.getName());
			entity = repository.save(entity);

			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID not found: " + id);
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

}
