package jonas.gn.dscatalog.factory;

import jonas.gn.dscatalog.dto.CategoryDTO;
import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.dto.RoleDTO;
import jonas.gn.dscatalog.dto.UserDTO;
import jonas.gn.dscatalog.dto.UserInsertDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.entities.Product;
import jonas.gn.dscatalog.entities.Role;
import jonas.gn.dscatalog.entities.User;

public class Factory {
	public static Product createProduct() {
		final Product product = new Product(1L, "Phone", "A good phone", 1000.0, "https://img.url");
		product.getCategories().add(createCategory());

		return product;
	}

	public static ProductDTO createProductDTO() {
		final Product product = createProduct();

		return new ProductDTO(product, product.getCategories());
	}

	public static Category createCategory() {
		return new Category(1L, "Eletronics");
	}

	public static CategoryDTO createCategoryDTO() {
		final Category category = createCategory();

		return new CategoryDTO(category);
	}

	public static User createUser() {
		final User user = new User(1L, "Maria", "Joaquina", "m.joaquina@email.com", "12345678");
		user.getRoles().add(createRole());

		return user;
	}

	public static UserDTO createUserDTO() {
		return new UserDTO(createUser());
	}

	public static UserInsertDTO createUserInsertDTO() {
		final User entity = createUser();
		final UserInsertDTO dto = new UserInsertDTO(entity);
		dto.setPassword(entity.getPassword());

		return dto;
	}

	public static Role createRole() {
		return new Role(1L, "ROLE_OPERATOR");
	}

	public static RoleDTO createRoleDTO() {
		return new RoleDTO(createRole());
	}
}
