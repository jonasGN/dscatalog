package jonas.gn.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.repositories.CategoryRepository;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> fetchAll() {
		List<Category> result = repository.findAll();
		Stream<CategoryDTO> categories = result.stream().map(c -> new CategoryDTO(c));

		return categories.collect(Collectors.toList());
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

}
