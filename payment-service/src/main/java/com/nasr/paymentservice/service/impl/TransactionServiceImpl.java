package com.nasr.paymentservice.service.impl;

import com.nasr.paymentservice.base.service.impl.BaseServiceImpl;
import com.nasr.paymentservice.domain.Transaction;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import com.nasr.paymentservice.exception.InvalidPaymentException;
import com.nasr.paymentservice.mapper.PaymentMapper;
import com.nasr.paymentservice.repository.TransactionRepository;
import com.nasr.paymentservice.service.PaymentService;
import com.nasr.paymentservice.service.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@Log4j2
public class TransactionServiceImpl extends BaseServiceImpl<Transaction, Long, TransactionRepository, PaymentResponse, PaymentRequest>
        implements TransactionService {

    private final PaymentService paymentService;

    public TransactionServiceImpl(TransactionRepository repository, PaymentMapper mapper, PaymentService paymentService) {
        super(repository, mapper);
        this.paymentService = paymentService;
    }

    @Override
    public Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    @Override
    @Transactional
    public Mono<PaymentResponse> saveOrUpdate(PaymentRequest paymentRequest) {
        // can pay order by third party service with cardNumber and cvv2 etc ...  we mocked this section
        //and if third party do payment and send response as ok with set paymentStatus as SUCCESS
        //after that we save transaction in db

        try {
            paymentService.doPayment(paymentRequest);
            log.info("payment successfully done !");
        } catch (Exception e) {
            log.error("payment was not successfully !");
            throw new InvalidPaymentException(e.getMessage());
        }


        Transaction transaction = mapper.convertViewToEntity(paymentRequest);

        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setPaymentDate(LocalDateTime.now());
        return repository.save(transaction)
                .map(mapper::convertEntityToDto)
                .doOnNext(tx -> log.info("payment was successfully and transaction id is : {} ", tx.getId()))
                .log();
    }

    @Override
    public Mono<PaymentResponse> getByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }
}
