package eu.happycoders.shop.adapter.out.persistence;

import eu.happycoders.shop.application.port.out.persistence.ProductRepository;
import eu.happycoders.shop.model.product.Product;
import eu.happycoders.shop.model.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractProductRepositoryTest<T extends ProductRepository> {

    private T productRepository;

    @BeforeEach
    void initRepository() {
        productRepository = createProductRepository();
    }

    protected abstract T createProductRepository();

    @Test
    void givenPersistedProductId_whenFindById_thenReturnsATestProduct() {
        ProductId productId = DemoProducts.COMPUTER_MONITOR.id();

        Optional<Product> product = productRepository.findById(productId);

        assertThat(product).contains(DemoProducts.COMPUTER_MONITOR);
    }

    @Test
    void givenNotPersistedProductId_whenFindById_thenReturnsEmpty() {
        ProductId productId = new ProductId("00000");

        Optional<Product> product = productRepository.findById(productId);

        assertThat(product).isEmpty();
    }

    @Test
    void givenSearchQueryNotMatches_whenFindByNameOrDescription_thenReturnsEmpty() {
        String querySearch = "not matching any product";

        List<Product> products = productRepository.findByNameOrDescription(querySearch);

        assertThat(products).isEmpty();
    }

    @Test
    void givenSearchQueryMatchesOneProduct_whenFindByNameOrDescription_thenReturnsEmpty() {
        String querySearch = "lights";

        List<Product> products = productRepository.findByNameOrDescription(querySearch);

        assertThat(products).containsExactlyInAnyOrder(DemoProducts.LED_LIGHTS);
    }

    @Test
    void givenSearchQueryMatchesTwoProducts_whenFindByNameOrDescription_thenReturnsEmpty() {
        String querySearch = "monitor";

        List<Product> products = productRepository.findByNameOrDescription(querySearch);

        assertThat(products).containsExactlyInAnyOrder(
                DemoProducts.COMPUTER_MONITOR, DemoProducts.MONITOR_DESK_MOUNT);
    }
}
