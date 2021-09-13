package jonas.gn.dscatalog.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jonas.gn.dscatalog.entities.Category;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

	@GetMapping
	public ResponseEntity<Category> fetchAll() {
		Category category = new Category(1L, "Drogas");
		return ResponseEntity.ok(category);
	}

}
