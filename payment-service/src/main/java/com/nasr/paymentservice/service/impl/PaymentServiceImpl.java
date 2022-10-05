package com.nasr.paymentservice.service.impl;

import com.nasr.paymentservice.base.service.impl.BaseServiceImpl;
import com.nasr.paymentservice.domain.Payment;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import com.nasr.paymentservice.mapper.PaymentMapper;
import com.nasr.paymentservice.repository.PaymentRepository;
import com.nasr.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl extends BaseServiceImpl<Payment, Long, PaymentRepository, PaymentResponse, PaymentRequest>
        implements PaymentService {

    public PaymentServiceImpl(PaymentRepository repository, PaymentMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public Class<Payment> getEntityClass() {
        return Payment.class;
    }

    @Override
    @Transactional
    public Mono<PaymentResponse> saveOrUpdate(PaymentRequest paymentRequest) {
        // can pay order by third party service with cardNumber and cvv2 etc ...  we mocked this section
        //and if third party do payment and send response as ok with set paymentStatus as SUCCESS
        //after that we save payment in db
        Payment payment = mapper.convertViewToEntity(paymentRequest);
        payment.setStatus(PaymentStatus.SUCCESS);
        return repository.save(payment)
                .map(mapper::convertEntityToDto)
                .log();
    }

    @Override
    public Mono<PaymentResponse> getByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }
}
