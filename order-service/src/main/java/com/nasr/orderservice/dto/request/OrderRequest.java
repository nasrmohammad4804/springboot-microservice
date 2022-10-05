package com.nasr.orderservice.dto.request;

import com.nasr.orderservice.domain.enumeration.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private List<OrderPlaceRequest> orderPlaceRequestDtoList;

    @NotNull(message = "total amount is mandatory . when income from client")
    private Double totalPrice;
}
