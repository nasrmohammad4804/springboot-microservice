package com.nasr.orderhandlerservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nasr.orderhandlerservice.job.OrderPlacedHandlerJob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.newJob;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDescriptorRequest {

    @NotNull
    private String name;

    @JsonProperty(value = "triggers")
    private List<TriggerDescriptorRequest> triggerDescriptors = new ArrayList<>();

    @JsonIgnore
    private String group;

    @Min(value = 1)
    private int orderId;

    @NotNull
    private Map<Long,Long> productInfo;

    private Map<String,Object > getData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> data  =new HashMap<>();
        data.put("orderId",orderId);
        data.put("productInfo",mapper.writeValueAsString(productInfo));

        return data;
    }


    public JobDetail buildJobDetail() throws JsonProcessingException {
        JobDataMap jobDataMap = new JobDataMap(getData());

        return newJob(OrderPlacedHandlerJob.class)
                .withIdentity(name, group)
                .usingJobData(jobDataMap)
                .build();
    }
    public Set<Trigger> buildTriggers(){
        return this.triggerDescriptors.stream().map(TriggerDescriptorRequest::buildTrigger)
                .collect(Collectors.toSet());
    }
}
