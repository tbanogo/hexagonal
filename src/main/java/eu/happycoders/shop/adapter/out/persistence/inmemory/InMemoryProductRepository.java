package eu.happycoders.shop.adapter.out.persistence.inmemory;

import eu.happycoders.shop.adapter.out.persistence.DemoProducts;
import eu.happycoders.shop.application.port.out.persistence.ProductRepository;
import eu.happycoders.shop.model.product.Product;
import eu.happycoders.shop.model.product.ProductId;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<ProductId, Product> products = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
        this.createDemoProducts();
    }

    private void createDemoProducts() {
        DemoProducts.DEMO_PRODUCTS.forEach(this::save);
    }

    @Override
    public void save(Product product) {
        this.products.put(product.id(), product);
    }

    @Override
    public Optional<Product> findById(ProductId productId) {
        return Optional.of(this.products.get(productId));
    }

    @Override
    public List<Product> findByNameOrDescription(String query) {
        String queryToLowerCase = query.toLowerCase(Locale.ROOT);

        return this.products.values().stream()
                .filter(product -> matchesQuery(product, queryToLowerCase))
                .toList();
    }

    private boolean matchesQuery(Product product, String query) {
        return product.name().toLowerCase(Locale.ROOT).contains(query)
        || product.description().toLowerCase(Locale.ROOT).contains(query);
    }
}
