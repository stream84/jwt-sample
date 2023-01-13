package streamlines.jwtsample.product;

import streamlines.jwtsample.product.model.request.ProductRequest;
import streamlines.jwtsample.product.model.response.ProductDispenseResponse;

public interface ProductService {

    ProductDispenseResponse dispense(ProductRequest productRequest);

}
