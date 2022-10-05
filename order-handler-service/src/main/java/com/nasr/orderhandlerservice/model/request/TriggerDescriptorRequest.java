package com.nasr.orderhandlerservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.DateBuilder;
import org.quartz.Trigger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.springframework.util.StringUtils.isEmpty;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TriggerDescriptorRequest {

    @NotNull(message = "name of job is mandatory !")
    private String name;

    private String group;

    @Min(value = 1)
    private int hour;

    private String buildName(){
        return isEmpty(name) ? UUID.randomUUID().toString() : name;
    }

    public Trigger buildTrigger(){
        return newTrigger()
                .withIdentity(buildName(),group)
                .startAt(futureDate(hour, DateBuilder.IntervalUnit.HOUR))
                .build();

    }
}
