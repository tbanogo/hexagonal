package eu.happycoders.shop.adapter.in.rest.common;

import eu.happycoders.shop.model.product.ProductId;
import jakarta.ws.rs.core.Response;

import static eu.happycoders.shop.adapter.in.rest.common.ControllerCommons.clientErrorException;

public final class ProductIdParser {

    private ProductIdParser() {}

    public static ProductId parseProductId(String string) {
        if (string == null) {
            throw clientErrorException(Response.Status.BAD_REQUEST, "Missing 'productId'");
        }

        try {
            return new ProductId(string);
        } catch (IllegalArgumentException e) {
            throw clientErrorException(Response.Status.BAD_REQUEST, "Invalid 'productId'");
        }
    }
}
