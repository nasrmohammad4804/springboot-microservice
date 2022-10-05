package com.nasr.orderhandlerservice.service;

import com.nasr.orderhandlerservice.model.request.JobDescriptorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderPlacedHandlerService {

    private final Scheduler scheduler;

    public JobDescriptorRequest createJob(JobDescriptorRequest descriptor)  {
        JobDetail jobDetail = descriptor.buildJobDetail();
        Set<Trigger> triggers = descriptor.buildTriggers();

        try {
        scheduler.scheduleJob(jobDetail,triggers,false);
            log.info(" job with key : {} successfully saved !",jobDetail.getKey());
        }catch (Exception e){
            log.error("could not save job with key : {} ",jobDetail.getKey());
            throw new IllegalStateException(e.getMessage());
        }
        return descriptor;
    }
}
