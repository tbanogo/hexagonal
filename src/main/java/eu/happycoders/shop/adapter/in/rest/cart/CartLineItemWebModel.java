package eu.happycoders.shop.adapter.in.rest.cart;

import eu.happycoders.shop.model.cart.CartLineItem;
import eu.happycoders.shop.model.money.Money;
import eu.happycoders.shop.model.product.Product;

public record CartLineItemWebModel(
        String productId, String productName, Money price, int quantity) {

    public static CartLineItemWebModel fromDomainToModel(CartLineItem lineItems) {
        Product product = lineItems.product();
        return new CartLineItemWebModel(
                product.id().value(),
                product.name(),
                product.price(),
                lineItems.quantity()
        );
    }
}
