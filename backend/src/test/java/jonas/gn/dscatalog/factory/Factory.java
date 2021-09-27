package jonas.gn.dscatalog.factory;

import jonas.gn.dscatalog.dto.ProductDTO;
import jonas.gn.dscatalog.entities.Category;
import jonas.gn.dscatalog.entities.Product;

public class Factory {
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "A good phone", 1000.0, "https://img.url");
		product.getCategories().add(new Category(2L, "Eletronics"));

		return product;
	}

	public static ProductDTO createProductDTO() {
		Product product = createProduct();

		return new ProductDTO(product, product.getCategories());
	}
}
