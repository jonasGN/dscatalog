package jonas.gn.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.factory.Factory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {

	private static final String BASE_URI = "/products";
	private static final String BASE_URI_WITH_ID = BASE_URI + "/{id}";
	private static final MediaType JSON = MediaType.APPLICATION_JSON;

	@Autowired
	private MockMvc mock;

	@Autowired
	private ObjectMapper objectMapper;

	private long existingId = 1L;
	private long nonExistingId = 1000L;
	private long totalProducts = 25L;

	private ProductDTO productDTO = Factory.createProductDTO();

	@Test
	public void fetchAllPagedShouldReturnSortedPageWhenSortByName() throws Exception {
		String requestParams = "?page=0&size=10&sort=name,asc";
		ResultActions request = mock.perform(get(BASE_URI + requestParams).accept(JSON));

		request.andExpect(status().isOk());
		request.andExpect(jsonPath("$.totalElements").value(totalProducts));
		request.andExpect(jsonPath("$.number").value(0));
		request.andExpect(jsonPath("$.size").value(10));
		request.andExpect(jsonPath("$.sort.sorted").value(true));
		request.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		request.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		request.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

	@Test
	public void fetchByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions request = mock.perform(get(BASE_URI_WITH_ID, existingId).accept(JSON));

		request.andExpect(status().isOk());
		request.andExpect(jsonPath("$.id").value(existingId));
		request.andExpect(jsonPath("$.name").value("The Lord of the Rings"));
		request.andExpect(jsonPath("$.price").value(90.5));
	}

	@Test
	public void fetchByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions request = mock.perform(get(BASE_URI_WITH_ID, nonExistingId).accept(JSON));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void addShouldAddAndReturnProduct() throws Exception {
		final String body = objectMapper.writeValueAsString(productDTO);
		ResultActions request = mock.perform(post(BASE_URI).content(body).accept(JSON).contentType(JSON));

		request.andExpect(status().isCreated());
		request.andExpect(jsonPath("$.id").value(totalProducts + 1));
		request.andExpect(jsonPath("$.name").value(productDTO.getName()));
		request.andExpect(jsonPath("$.description").value(productDTO.getDescription()));
		request.andExpect(jsonPath("$.price").value(productDTO.getPrice()));
	}

	@Test
	public void updateShouldUpdateWhenIdExists() throws Exception {
		final String body = objectMapper.writeValueAsString(productDTO);
		ResultActions request = mock
				.perform(put(BASE_URI_WITH_ID, existingId).content(body).accept(JSON).contentType(JSON));

		request.andExpect(status().isOk());
		request.andExpect(jsonPath("$.id").value(existingId));
		request.andExpect(jsonPath("$.name").value(productDTO.getName()));
		request.andExpect(jsonPath("$.description").value(productDTO.getDescription()));
		request.andExpect(jsonPath("$.price").value(productDTO.getPrice()));
	}

	@Test
	public void updateShoudReturnNotFoundWhenIdDoesNotExist() throws Exception {
		final String body = objectMapper.writeValueAsString(productDTO);
		ResultActions request = mock
				.perform(put(BASE_URI_WITH_ID, nonExistingId).content(body).accept(JSON).contentType(JSON));

		request.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldDeleteWhenIdExists() throws Exception {
		ResultActions request = mock.perform(delete(BASE_URI_WITH_ID, existingId).accept(JSON));

		request.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions request = mock.perform(delete(BASE_URI_WITH_ID, nonExistingId).accept(JSON));

		request.andExpect(status().isNotFound());
	}

}
