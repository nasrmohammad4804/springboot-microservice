package com.nasr.orderservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class OrderControllerTest {

    @Autowired
    private WebTestClient webclient;

    @Test
    void itShouldPlaceOrder() {
        // given
        // when
        //then
    }

    @Test
    void itShouldCancelOrder() {
        // given
        // when
        //then
    }

    @Test
    void itShouldGetOrderPlaceProducts() {
        // given
        // when
        //then
    }

    @Test
    void itShouldCompleteOrderPlaceStatus() {
        // given
        // when
        //then
    }

    @Test
    void itShouldGetOrderPlaced() {
        // given
        // when
        //then
    }
}