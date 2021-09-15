package jonas.gn.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	public List<Category> fetchAll() {
		return repository.findAll();
	}
}
