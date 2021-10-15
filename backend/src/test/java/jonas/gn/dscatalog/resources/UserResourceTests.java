package jonas.gn.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import jonas.gn.dscatalog.dto.UserDTO;
import jonas.gn.dscatalog.dto.UserInsertDTO;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.services.UserService;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(UserResource.class)
public class UserResourceTests {

	private static final String URI_PATH = "/users/";
	private static final String URI_PATH_WITH_ID = URI_PATH + "{id}";
	private static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON;

	@Autowired
	private MockMvc mock;

	@MockBean
	private UserService service;

	@Autowired
	private ObjectMapper mapper;

	private long existingId = 1L;
	private long nonExistingId = 2L;
	private long dependentId = 3L;

	private UserDTO dto = Factory.createUserDTO();
	private PageImpl<UserDTO> page = new PageImpl<UserDTO>(List.of(dto));

	@BeforeEach
	public void setUp() throws Exception {
		when(service.fetchAllPaged(any())).thenReturn(page);

		when(service.fetchById(existingId)).thenReturn(dto);
		when(service.fetchById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(service.add(any())).thenReturn(dto);

		when(service.update(Mockito.eq(existingId), any())).thenReturn(dto);
		when(service.update(Mockito.eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void fetchAllShouldReturnUsersPaged() throws Exception {
		final ResultActions request = mock.perform(get(URI_PATH).accept(JSON_TYPE));

		request.andExpect(status().isOk());
		request.andExpect(jsonPath("$.content").exists());
	}

	@Test
	public void fetchByIdShouldReturnUserWhenIdExists() throws Exception {
		final ResultActions request = mock.perform(get(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE));

		request.andExpect(status().isOk());
		request.andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void fetchByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final ResultActions request = mock.perform(get(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void addShouldAddAndReturnUser() throws Exception {
		final UserInsertDTO insertDto = Factory.createUserInsertDTO();
		final String body = mapper.writeValueAsString(insertDto);
		final ResultActions request = mock
				.perform(post(URI_PATH).content(body).contentType(JSON_TYPE).accept(JSON_TYPE));

		request.andExpect(status().isCreated());
		request.andExpect(jsonPath("$.id").exists());
		request.andExpect(jsonPath("$.firstName").value(insertDto.getFirstName()));
	}

	@Test
	public void updateShouldUpdateWhenIdExists() throws Exception {
		final String body = mapper.writeValueAsString(dto);
		final ResultActions request = mock
				.perform(put(URI_PATH_WITH_ID, existingId).content(body).contentType(JSON_TYPE).accept(JSON_TYPE));

		request.andExpect(status().isOk());
		request.andExpect(jsonPath("$.id").value(dto.getId()));
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final String body = mapper.writeValueAsString(dto);
		final ResultActions request = mock
				.perform(put(URI_PATH_WITH_ID, nonExistingId).content(body).contentType(JSON_TYPE).accept(JSON_TYPE));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldDeleteWhenIdExists() throws Exception {
		final ResultActions request = mock.perform(delete(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE));

		request.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final ResultActions request = mock.perform(delete(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnBadRequestWhenIdIsDependent() throws Exception {
		final ResultActions request = mock.perform(delete(URI_PATH_WITH_ID, dependentId).accept(JSON_TYPE));

		request.andExpect(status().isBadRequest());
	}
}
