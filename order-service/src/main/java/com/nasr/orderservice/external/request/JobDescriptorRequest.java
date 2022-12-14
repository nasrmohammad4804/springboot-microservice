package com.nasr.orderservice.external.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDescriptorRequest {

    private String name;
    private long orderId;
    private List<TriggerDescriptorRequest> triggers = new ArrayList<>();
    private Map<Long,Long> productInfo;
}
