package jonas.gn.dscatalog.resources;

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

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.factory.Factory;
import jonas.gn.dscatalog.services.ProductService;
import jonas.gn.dscatalog.services.exceptions.DatabaseException;
import jonas.gn.dscatalog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	private static final String URI_PATH = "/products/";
	private static final String URI_PATH_WITH_ID = URI_PATH + "{id}";
	private static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;

	@Autowired
	private ObjectMapper objectMapper;

	private long existingId = 1L;
	private long nonExistingId = 2L;
	private long dependentId = 3L;

	private ProductDTO productDTO = Factory.createProductDTO();
	private PageImpl<ProductDTO> page = new PageImpl<>(List.of(productDTO));

	@BeforeEach
	public void setUp() throws Exception {
		Mockito.when(service.fetchAllPaged(Mockito.any())).thenReturn(page);

		Mockito.when(service.fetchById(existingId)).thenReturn(productDTO);
		Mockito.when(service.fetchById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		Mockito.when(service.add(Mockito.any())).thenReturn(productDTO);

		Mockito.when(service.update(Mockito.eq(existingId), Mockito.any())).thenReturn(productDTO);
		Mockito.when(service.update(Mockito.eq(nonExistingId), Mockito.any()))
				.thenThrow(ResourceNotFoundException.class);

		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void findAllPagedShouldReturnProductsPage() throws Exception {
		ResultActions request = mockMvc.perform(get(URI_PATH).accept(JSON_TYPE));
		request.andExpect(status().isOk());
	}

	@Test
	public void fetchByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions request = mockMvc.perform(get(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE));

		request.andExpect(status().isOk());
		checkProductJsonBody(request);
	}

	@Test
	public void fetchByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions request = mockMvc.perform(get(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE));
		request.andExpect(status().isNotFound());
	}

	@Test
	public void addShouldAddAndReturnNewProduct() throws Exception {
		final String body = objectMapper.writeValueAsString(productDTO);
		ResultActions request = mockMvc.perform(post(URI_PATH).accept(JSON_TYPE).contentType(JSON_TYPE).content(body));

		request.andExpect(status().isCreated());
		checkProductJsonBody(request);
	}

	@Test
	public void updateShouldUpdateAnReturnProductWhenIdExists() throws Exception {
		final String body = objectMapper.writeValueAsString(productDTO);
		ResultActions request = mockMvc
				.perform(put(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE).contentType(JSON_TYPE).content(body));

		request.andExpect(status().isOk());
		checkProductJsonBody(request);
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final String body = objectMapper.writeValueAsString(productDTO);
		ResultActions request = mockMvc
				.perform(put(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE).contentType(JSON_TYPE).content(body));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldDeleteProductWhenIdExists() throws Exception {
		ResultActions request = mockMvc.perform(delete(URI_PATH_WITH_ID, existingId).accept(JSON_TYPE));

		request.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions request = mockMvc.perform(delete(URI_PATH_WITH_ID, nonExistingId).accept(JSON_TYPE));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnNoContentWhenIdIsDependet() throws Exception {
		ResultActions request = mockMvc.perform(delete(URI_PATH_WITH_ID, dependentId).accept(JSON_TYPE));

		request.andExpect(status().isBadRequest());
	}

	private void checkProductJsonBody(ResultActions request) throws Exception {
		request.andExpect(jsonPath("$.id").exists());
		request.andExpect(jsonPath("$.name").exists());
		request.andExpect(jsonPath("$.description").exists());
		request.andExpect(jsonPath("$.price").exists());
		request.andExpect(jsonPath("$.imgUrl").exists());
		// request.andExpect(jsonPath("$.moment").exists());
		request.andExpect(jsonPath("$.categories").exists());
	}

}
