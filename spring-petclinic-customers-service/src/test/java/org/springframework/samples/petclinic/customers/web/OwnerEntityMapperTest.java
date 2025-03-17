package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OwnerEntityMapperTest {

    @Test
    void testMapShouldUpdateOwnerFields() {
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Street", "New York", "123456789");

        OwnerEntityMapper mapper = new OwnerEntityMapper();
        Owner result = mapper.map(owner, request);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("123 Street", result.getAddress());
        assertEquals("New York", result.getCity());
        assertEquals("123456789", result.getTelephone());
    }
}
