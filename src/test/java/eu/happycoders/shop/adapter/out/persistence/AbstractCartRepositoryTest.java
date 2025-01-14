package eu.happycoders.shop.adapter.out.persistence;

import eu.happycoders.shop.application.port.out.persistence.CartRepository;
import eu.happycoders.shop.application.port.out.persistence.ProductRepository;
import eu.happycoders.shop.model.cart.Cart;
import eu.happycoders.shop.model.cart.CartLineItem;
import eu.happycoders.shop.model.cart.NotEnoughItemsInStockException;
import eu.happycoders.shop.model.customer.CustomerId;
import eu.happycoders.shop.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static eu.happycoders.shop.model.money.TestMoneyFactory.euros;
import static eu.happycoders.shop.model.product.TestProductFactory.createTestProduct;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractCartRepositoryTest
        <T extends CartRepository, U extends ProductRepository>{

    private static final Product TEST_PRODUCT_1 = createTestProduct(euros(19, 99));
    private static final Product TEST_PRODUCT_2 = createTestProduct(euros(25, 99));

    private static final AtomicInteger CUSTOMER_ID_SEQUENCE_GENERATOR = new AtomicInteger();

    private T cartRepository;

    protected abstract T createCartRepository();
    protected abstract U createProductRepository();

    @BeforeEach
    void initRepository() {
        this.cartRepository = createCartRepository();
        this.persitTestProducts();
    }

    private void persitTestProducts() {
        U productRepository = createProductRepository();
        productRepository.save(TEST_PRODUCT_1);
        productRepository.save(TEST_PRODUCT_2);
    }

    @Test
    void givenCustomerIdForWhichNoCartIsPersisted_whenFindByCustomerId_thenReturnsAnEmptyOptional() {
        CustomerId customerId = createUniqueCustomerId();

        Optional<Cart> cart = cartRepository.findByCustomerId(customerId);

        assertThat(cart).isEmpty();
    }

    @Test
    void givenCustomerIdForWhichCartIsPersisted_whenFindByCustomerId_thenReturnsOptionalCart()
            throws NotEnoughItemsInStockException {
        CustomerId customerId = createUniqueCustomerId();

        Cart persistedCart = new Cart(customerId);
        persistedCart.addProduct(TEST_PRODUCT_1, 2);
        cartRepository.save(persistedCart);

        Optional<Cart> cart = cartRepository.findByCustomerId(customerId);

        assertThat(cart).isNotEmpty();
        assertThat(cart.get().id()).isEqualTo(customerId);
        assertThat(cart.get().lineItems()).hasSize(1);
        assertThat(cart.get().lineItems().get(0)).isEqualTo(TEST_PRODUCT_1);
        assertThat(cart.get().lineItems().get(0).quantity()).isEqualTo(2);
    }

    @Test
    void givenExistingCartWithProduct_andGivenNewCartWithProductForSameCustomer_whenSaveCart_thenOverwriteExistingCart()
            throws NotEnoughItemsInStockException {
        CustomerId customerId = createUniqueCustomerId();

        Cart existingCart = new Cart(customerId);
        existingCart.addProduct(TEST_PRODUCT_1, 1);
        cartRepository.save(existingCart);

        Cart newCart = new Cart(customerId);
        newCart.addProduct(TEST_PRODUCT_2, 2);
        cartRepository.save(newCart);

        Optional<Cart> cart = cartRepository.findByCustomerId(customerId);

        assertThat(cart).isNotEmpty();
        assertThat(cart.get().id()).isEqualTo(customerId);
        assertThat(cart.get().lineItems()).hasSize(1);
        assertThat(cart.get().lineItems().get(0).product()).isEqualTo(TEST_PRODUCT_2);
        assertThat(cart.get().lineItems().get(0).quantity()).isEqualTo(2);
    }

    @Test
    void givenExistingCartWithProductAndAddProductToTheExistingCart_whenSaveCart_thenUpdatesTheExistingCart()
            throws NotEnoughItemsInStockException {
        CustomerId customerId = createUniqueCustomerId();

        Cart existingCart = new Cart(customerId);
        existingCart.addProduct(TEST_PRODUCT_1, 2);
        cartRepository.save(existingCart);

        existingCart = cartRepository.findByCustomerId(customerId).orElseThrow();
        existingCart.addProduct(TEST_PRODUCT_2, 3);
        cartRepository.save(existingCart);

        Optional<Cart> cart = cartRepository.findByCustomerId(customerId);

        assertThat(cart).isNotEmpty();
        assertThat(cart.get().id()).isEqualTo(customerId);
        assertThat(cart.get().lineItems())
                .map(CartLineItem::product)
                .containsExactlyInAnyOrder(TEST_PRODUCT_1, TEST_PRODUCT_2);
    }

    @Test
    void givenExistingCart_whenDeleteByCustomerId_thenDeletesTheCart() {
        CustomerId customerId = createUniqueCustomerId();

        Cart existingCart = new Cart(customerId);
        cartRepository.save(existingCart);

        cartRepository.deleteById(customerId);

        assertThat(cartRepository.findByCustomerId(customerId)).isEmpty();
    }

    @Test
    void givenNotExistingCart_whenDeletebyCustomerId_thenDoesNothing() {
        CustomerId customerId = createUniqueCustomerId();
        assertThat(cartRepository.findByCustomerId(customerId)).isEmpty();

        cartRepository.deleteById(customerId);

        assertThat(cartRepository.findByCustomerId(customerId)).isEmpty();
    }

    private static CustomerId createUniqueCustomerId() {
        return new CustomerId(CUSTOMER_ID_SEQUENCE_GENERATOR.incrementAndGet());
    }
}
