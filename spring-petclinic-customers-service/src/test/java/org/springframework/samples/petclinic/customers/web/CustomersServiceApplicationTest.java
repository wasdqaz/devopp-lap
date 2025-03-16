package org.springframework.samples.petclinic.customers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CustomersServiceApplicationTest {

    @Test
    void main_shouldStartApplication() {
        assertDoesNotThrow(() -> CustomersServiceApplication.main(new String[]{}));
    }
}
