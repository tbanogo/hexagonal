package eu.happycoders.shop.adapter.in.rest.cart;

import eu.happycoders.shop.application.port.in.cart.AddToCartUseCase;
import eu.happycoders.shop.application.port.in.cart.ProductNotFoundException;
import eu.happycoders.shop.model.cart.Cart;
import eu.happycoders.shop.model.cart.NotEnoughItemsInStockException;
import eu.happycoders.shop.model.customer.CustomerId;
import eu.happycoders.shop.model.product.ProductId;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static eu.happycoders.shop.adapter.in.rest.common.ControllerCommons.clientErrorException;
import static eu.happycoders.shop.adapter.in.rest.common.CustomerIdParser.parseCustomerId;
import static eu.happycoders.shop.adapter.in.rest.common.ProductIdParser.parseProductId;

@Path("/carts")
@Produces(MediaType.APPLICATION_JSON)
public class AddToCartController {

    private AddToCartUseCase addToCartUseCase;

    public AddToCartController(AddToCartUseCase addToCartUseCase) {
        this.addToCartUseCase = addToCartUseCase;
    }

    @POST
    @Path("/{customerId}/line-items")
    public CartWebModel addLineItem(
            @PathParam("customerId") String customerIdString,
            @QueryParam("productId") String productIdString,
            @QueryParam("quantity") int quantity) {
        CustomerId customerId = parseCustomerId(customerIdString);
        ProductId productId = parseProductId(productIdString);

        try {
            Cart cart = addToCartUseCase.addToCart(customerId, productId, quantity);
            return CartWebModel.fromDomainToModel(cart);
        } catch (ProductNotFoundException e) {
            throw clientErrorException(
                    Response.Status.BAD_REQUEST,
                    "The request product does not exist");
        } catch (NotEnoughItemsInStockException e) {
            throw clientErrorException(
                    Response.Status.BAD_GATEWAY,
                    "Only %d items in stock".formatted(e.itemsInStock())
            );
        }
    }
}
