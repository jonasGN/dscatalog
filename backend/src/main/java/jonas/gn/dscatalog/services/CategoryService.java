package jonas.gn.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.repositories.CategoryRepository;

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

}