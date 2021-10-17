package jonas.gn.dscatalog.resources;

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

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.services.CategoryService;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(CategoryResource.class)
public class CategoryResourceTests {

	private static final String URI_PATH = "/categories/";
	private static final String URI_PATH_WITH_ID = URI_PATH + "{id}";
	private static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON;

	@Autowired
	private MockMvc mock;

	@MockBean
	private CategoryService service;

	@Autowired
	private ObjectMapper objectMapper;

	private long existingId = 1L;
	private long nonExistingId = 2L;
	private long dependentId = 3L;

	private CategoryDTO categoryDTO = Factory.createCategoryDTO();
	private PageImpl<CategoryDTO> page = new PageImpl<>(List.of(categoryDTO));

	@BeforeEach
	public void setUp() throws Exception {
		when(service.fetchAllPaged(Mockito.any())).thenReturn(page);

		when(service.fetchById(existingId)).thenReturn(categoryDTO);
		when(service.fetchById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(service.add(Mockito.any())).thenReturn(categoryDTO);

		when(service.update(Mockito.eq(existingId), Mockito.any())).thenReturn(categoryDTO);
		when(service.update(Mockito.eq(nonExistingId), Mockito.any())).thenThrow(ResourceNotFoundException.class);

		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void findAllPagedShouldReturnCategorysPage() throws Exception {
		final ResultActions request = mock.perform(get(URI_PATH).accept(JSON_TYPE));

		request.andExpect(status().isOk());
	}

	@Test
	public void fetchByIdShouldReturnCategoryWhenIdExists() throws Exception {
		final ResultActions request = mock.perform(get(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE));

		request.andExpect(status().isOk());
		checkCategoryJsonBody(request);
	}

	@Test
	public void fetchByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final ResultActions request = mock.perform(get(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void addShouldAddAndReturnNewCategory() throws Exception {
		final String body = objectMapper.writeValueAsString(categoryDTO);
		final ResultActions request = mock
				.perform(post(URI_PATH).accept(JSON_TYPE).contentType(JSON_TYPE).content(body));

		request.andExpect(status().isCreated());
		checkCategoryJsonBody(request);
	}

	@Test
	public void updateShouldUpdateAnReturnCategoryWhenIdExists() throws Exception {
		final String body = objectMapper.writeValueAsString(categoryDTO);
		final ResultActions request = mock
				.perform(put(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE).contentType(JSON_TYPE).content(body));

		request.andExpect(status().isOk());
		checkCategoryJsonBody(request);
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final String body = objectMapper.writeValueAsString(categoryDTO);
		final ResultActions request = mock
				.perform(put(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE).contentType(JSON_TYPE).content(body));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldDeleteCategoryWhenIdExists() throws Exception {
		final ResultActions request = mock.perform(delete(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE));

		request.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final ResultActions request = mock.perform(delete(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnBadRequestWhenIdIsDependet() throws Exception {
		final ResultActions request = mock.perform(delete(URI_PATH_WITH_ID, dependentId).accept(JSON_TYPE));

		request.andExpect(status().isBadRequest());
	}

	private void checkCategoryJsonBody(ResultActions request) throws Exception {
		request.andExpect(jsonPath("$.id").exists());
		request.andExpect(jsonPath("$.name").exists());
	}

}
