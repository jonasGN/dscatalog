package jonas.gn.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

	@Autowired
	private ProductService service;

	@GetMapping
	public ResponseEntity<Page<ProductDTO>> fetchAllPaged(Pageable pageable) {
		Page<ProductDTO> result = service.fetchAllPaged(pageable);

		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> fetchById(@PathVariable Long id) {
		ProductDTO result = service.fetchById(id);

		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<ProductDTO> add(@Valid @RequestBody ProductDTO product) {
		ProductDTO result = service.add(product);

		UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}");
		URI uri = uriBuilder.buildAndExpand(product.getId()).toUri();

		return ResponseEntity.created(uri).body(result);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO product) {
		ProductDTO result = service.update(id, product);

		return ResponseEntity.ok(result);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);

		return ResponseEntity.noContent().build();
	}

}
