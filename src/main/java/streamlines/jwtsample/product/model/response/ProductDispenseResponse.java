package streamlines.jwtsample.product.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDispenseResponse {
    private String responseMessage;
    private String productName;
    private Double currentBalance;
}
