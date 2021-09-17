package jonas.gn.dscatalog.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

	@Autowired
	private CategoryService service;

	@GetMapping
	public ResponseEntity<List<CategoryDTO>> fetchAll() {
		List<CategoryDTO> result = service.fetchAll();
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> fetchById(@PathVariable Long id) {
		CategoryDTO result = service.fetchById(id);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<CategoryDTO> add(@RequestBody CategoryDTO category) {
		category = service.add(category);

		UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}");
		URI uri = uriBuilder.buildAndExpand(category.getId()).toUri();

		return ResponseEntity.created(uri).body(category);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO category) {
		CategoryDTO result = service.update(id, category);
		return ResponseEntity.ok(result);
	}
}
