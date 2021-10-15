package jonas.gn.dscatalog.resources;

import java.net.URI;

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

import jonas.gn.dscatalog.dto.UserDTO;
import jonas.gn.dscatalog.dto.UserInsertDTO;
import jonas.gn.dscatalog.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

	@Autowired
	private UserService service;

	@GetMapping
	public ResponseEntity<Page<UserDTO>> fetchAll(Pageable pageable) {
		final Page<UserDTO> result = service.fetchAllPaged(pageable);

		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> fetchById(@PathVariable Long id) {
		final UserDTO result = service.fetchById(id);

		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<UserDTO> add(@RequestBody UserInsertDTO user) {
		final UserDTO result = service.add(user);

		final UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}");
		final URI uri = builder.buildAndExpand(result.getId()).toUri();

		return ResponseEntity.created(uri).body(result);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO user) {
		final UserDTO result = service.update(id, user);

		return ResponseEntity.ok(result);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);

		return ResponseEntity.noContent().build();
	}

}
