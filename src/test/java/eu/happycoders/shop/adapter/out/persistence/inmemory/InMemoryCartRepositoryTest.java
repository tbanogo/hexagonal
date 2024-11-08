package eu.happycoders.shop.adapter.out.persistence.inmemory;

import eu.happycoders.shop.adapter.out.persistence.AbstractCartRepositoryTest;
import eu.happycoders.shop.application.port.out.persistence.CartRepository;
import eu.happycoders.shop.application.port.out.persistence.ProductRepository;

public class InMemoryCartRepositoryTest extends AbstractCartRepositoryTest<CartRepository, ProductRepository> {

    @Override
    protected CartRepository createCartRepository() {
        return new InMemoryCartRepository();
    }

    @Override
    protected ProductRepository createProductRepository() {
        return new InMemoryProductRepository();
    }
}
