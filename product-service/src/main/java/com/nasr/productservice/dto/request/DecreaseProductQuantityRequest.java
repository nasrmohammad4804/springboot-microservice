package com.nasr.productservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecreaseProductQuantityRequest implements Serializable {


    @NotNull
    @Min(1)
    private Long productId;

    @NotNull
    @Min(1)
    private Long productNumber;
}
