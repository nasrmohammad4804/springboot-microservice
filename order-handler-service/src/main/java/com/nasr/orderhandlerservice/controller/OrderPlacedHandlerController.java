package com.nasr.orderhandlerservice.controller;

import com.nasr.orderhandlerservice.model.request.JobDescriptorRequest;
import com.nasr.orderhandlerservice.service.OrderPlacedHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/orderPlaceHandler")
public class OrderPlacedHandlerController {

    @Autowired
    private OrderPlacedHandlerService orderPlacedHandlerService;

    /**
     *
     * @param jobDescriptor as body we can schedule on
     * @param group name of job
     * @return job
     * description
     * if customer order product, and we decrease product quantity accordingly customer ordered
     * but after ordering customer don't pay then  we give him 1 hour to continue payment order
     * delivered to shipment system otherwise we delete this order and revert which products to stock
     */

    @PostMapping("/groups/{group}/jobs")
    public ResponseEntity<JobDescriptorRequest> createJob(@RequestBody @Valid JobDescriptorRequest jobDescriptor, @PathVariable String group){
        jobDescriptor.setGroup(group);
        JobDescriptorRequest job = orderPlacedHandlerService.createJob(jobDescriptor);

        return ResponseEntity.ok(job);
    }
}
