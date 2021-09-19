package jonas.gn.dscatalog.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

	@Autowired
	private ProductService service;

	@GetMapping
	public ResponseEntity<Page<ProductDTO>> fetchAllPaged(
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer linesPerPage,
			@RequestParam(defaultValue = "ASC") String direction, 
			@RequestParam(defaultValue = "id") String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		Page<ProductDTO> result = service.fetchAllPaged(pageRequest);

		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> fetchById(@PathVariable Long id) {
		ProductDTO entity = service.fetchById(id);

		return ResponseEntity.ok(entity);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		
		return ResponseEntity.noContent().build();
	}

}
