package org.springframework.samples.petclinic.customers.web;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OwnerRequestTest {

    @Test
    void testOwnerRequest() {
        OwnerRequest request = new OwnerRequest("Tom", "Hanks", "789 Oak St", "Chicago", "3216549870");

        assertEquals("Tom", request.firstName());
        assertEquals("Hanks", request.lastName());
        assertEquals("789 Oak St", request.address());
        assertEquals("Chicago", request.city());
        assertEquals("3216549870", request.telephone());
    }

    @Test
    void testOwnerRequestEquality() {
        OwnerRequest req1 = new OwnerRequest("Tom", "Hanks", "789 Oak St", "Chicago", "3216549870");
        OwnerRequest req2 = new OwnerRequest("Tom", "Hanks", "789 Oak St", "Chicago", "3216549870");

        assertEquals(req1, req2); // Kiểm tra equals()
        assertEquals(req1.hashCode(), req2.hashCode()); // Kiểm tra hashCode()
    }

    @Test
    void testOwnerRequestToString() {
        OwnerRequest request = new OwnerRequest("Tom", "Hanks", "789 Oak St", "Chicago", "3216549870");
        String expected = "OwnerRequest[firstName=Tom, lastName=Hanks, address=789 Oak St, city=Chicago, telephone=3216549870]";

        assertEquals(expected, request.toString()); // Kiểm tra toString()
    }
}
