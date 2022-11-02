package com.nasr.paymentservice.service.impl;

import com.nasr.paymentservice.domain.Transaction;
import com.nasr.paymentservice.domain.enumeration.PaymentMode;
import com.nasr.paymentservice.domain.enumeration.PaymentStatus;
import com.nasr.paymentservice.dto.request.AccountInfo;
import com.nasr.paymentservice.dto.request.PaymentRequest;
import com.nasr.paymentservice.dto.response.PaymentResponse;
import com.nasr.paymentservice.exception.InvalidPaymentException;
import com.nasr.paymentservice.exception.PaymentNotFoundException;
import com.nasr.paymentservice.external.response.OrderResponse;
import com.nasr.paymentservice.mapper.PaymentMapper;
import com.nasr.paymentservice.repository.TransactionRepository;
import com.nasr.paymentservice.service.PaymentService;
import com.nasr.paymentservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.nasr.paymentservice.domain.enumeration.PaymentMode.CASH;
import static com.nasr.paymentservice.domain.enumeration.PaymentStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.PREDICATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper mapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient.Builder webclient;

    @Mock
    private TransactionRepository repository;

    @Captor
    private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    private TransactionService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TransactionServiceImpl(
                repository, mapper, paymentService, webclient
        );
    }

    @Test
    @DisplayName("this unit test for get transaction detail by order id")
    void itShouldGetByOrderId() {

        // given
        Long orderId = 1L;

        // when
        Transaction mockTransaction = getMockTransaction();
        given(repository.findByOrderId(orderId)).willReturn(Mono.just(mockTransaction));
        given(mapper.convertEntityToDto(any(Transaction.class))).willReturn(getMockPaymentResponse());
        Mono<PaymentResponse> paymentResponse = underTest.getByOrderId(orderId);

        //then
        StepVerifier.create(paymentResponse)
                .assertNext(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(6L);
                    assertThat(response.getStatus()).isEqualTo(mockTransaction.getStatus());
                })
                .verifyComplete();

        verify(mapper, times(1)).convertEntityToDto(transactionArgumentCaptor.capture());
        assertThat(mockTransaction).isEqualToComparingFieldByField(transactionArgumentCaptor.getValue());
    }

    private PaymentResponse getMockPaymentResponse() {
        Transaction mockTransaction = getMockTransaction();

        return PaymentResponse.builder()
                .id(6L)
                .mode(mockTransaction.getMode())
                .status(mockTransaction.getStatus())
                .build();
    }

    private Transaction getMockTransaction() {
        return new Transaction(CASH, SUCCESS, LocalDateTime.now(), 1L);
    }

    @Test
    @DisplayName("this unit test for doPayment after creating after customer placed order")
    void itShouldDoPayment() throws Exception {

        // given
        PaymentRequest request = new PaymentRequest(
                CASH, new AccountInfo(5041652397612904L, "734", "01/03"),
                578_000D, 1L
        );
        // when
        Transaction mockTransaction = getMockTransaction();

        doNothing().when(paymentService).doPayment(any(PaymentRequest.class));
        given(mapper.convertViewToEntity(request)).willReturn(mockTransaction);

        given(
                webclient.build().put().uri(any(Function.class))
                        .retrieve()
                        .onStatus(any(Predicate.class), any(Function.class))
                        .bodyToMono(OrderResponse.class)

        ).willReturn((getMockOrderResponse()));

        Transaction transaction = (Transaction) mockTransaction.clone();
        transaction.setId(6L);

        given(repository.save(any(Transaction.class))).willReturn(Mono.just(transaction));

        PaymentResponse mockPaymentResponse = getMockPaymentResponse();
        given(mapper.convertEntityToDto(any(Transaction.class))).willReturn(mockPaymentResponse);
        Mono<PaymentResponse> paymentResponse = underTest.doPayment(request);

        //then
        StepVerifier.create(paymentResponse)
                .assertNext(response -> {
                    assertThat(response).isEqualToComparingFieldByField(mockPaymentResponse);
                })
                .verifyComplete();

        verify(repository, times(1)).save(any(Transaction.class));
        verify(mapper, times(1)).convertEntityToDto(transactionArgumentCaptor.capture());

        Transaction value = transactionArgumentCaptor.getValue();
        assertThat(value).isEqualToComparingFieldByField(transaction);
    }


    @Test
    @DisplayName(
            """
                this unit test expect to take error when payment failed with reason such
                as (expiration card - inventory not sufficient - not valid input - otp password not valid and ... )
            """
    )
    void itShouldThrowExceptionOnDoPaymentWhenPaymentFailed() throws Exception {

        // given
        PaymentRequest request = new PaymentRequest(
                CASH, new AccountInfo(5041652397612904L, "734", "01/03"),
                578_000D, 1L
        );

        // when
        //then
        doThrow(new IllegalStateException("your inventory not sufficient !"))
                .when(paymentService).doPayment(any(PaymentRequest.class));

        assertThrows(InvalidPaymentException.class, () -> underTest.doPayment(request).subscribe());


        verify(paymentService, only()).doPayment(any());
        verifyNoInteractions(mapper);
        verifyNoInteractions(repository);
        verifyNoInteractions(webclient);

    }

    private Mono<OrderResponse> getMockOrderResponse() {
        return Mono.just(
                new OrderResponse(1L, LocalDateTime.now(), 578_000D, "COMPLETED")
        );
    }
}