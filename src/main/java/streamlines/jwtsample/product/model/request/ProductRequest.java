package streamlines.jwtsample.product.model.request;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotNull
    @Positive
    private Integer id;
}
