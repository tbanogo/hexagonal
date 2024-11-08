package eu.happycoders.shop.adapter.in.rest.cart;

import eu.happycoders.shop.model.cart.Cart;
import eu.happycoders.shop.model.cart.CartLineItem;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

public final class CartsControllerAssertions {

    private CartsControllerAssertions() {}

    public static void assertThatResponseIsCart(Response response, Cart cart) {
        // Verify if request has been success
        assertThat(response.statusCode()).isEqualTo(OK.getStatusCode());

        JsonPath json = response.jsonPath();

        for(int i=0; i<cart.lineItems().size(); i++) {
            CartLineItem lineItem = cart.lineItems().get(0);

            String lineItemsPrefix = "lineItems[%d].".formatted(i);

            assertThat(json.getString(lineItemsPrefix + "productId"))
                    .isEqualTo(lineItem.product().id().value());
            assertThat(json.getString(lineItemsPrefix + "productName"))
                    .isEqualTo(lineItem.product().name());
            assertThat(json.getString(lineItemsPrefix + "price.currency"))
                    .isEqualTo(lineItem.product().price().currency().getCurrencyCode());
            assertThat(json.getDouble(lineItemsPrefix + "price.amount"))
                    .isEqualTo(lineItem.product().price().amount().doubleValue());
            assertThat(json.getInt(lineItemsPrefix + "quantity"))
                    .isEqualTo(lineItem.quantity());
        }

        assertThat(json.getInt("numberOfItems")).isEqualTo(cart.numberOfItems());

        if(cart.subTotal() != null) {
            assertThat(json.getString("subTotal.currency"))
                    .isEqualTo(cart.subTotal().currency().getCurrencyCode());
            assertThat(json.getDouble("subTotal.amount"))
                    .isEqualTo(cart.subTotal().amount().doubleValue());
        } else {
            assertThat(json.getString("subTotal")).isNull();
        }
    }
}
