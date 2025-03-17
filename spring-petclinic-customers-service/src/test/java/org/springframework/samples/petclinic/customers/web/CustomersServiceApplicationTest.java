package org.springframework.samples.petclinic.customers;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class CustomersServiceApplicationTest {

    @Test
    void main_shouldCallSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            CustomersServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(CustomersServiceApplication.class, new String[]{}));
        }
    }
}
